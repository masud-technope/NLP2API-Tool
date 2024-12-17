
/*****
 *
 * @author MasudRahman
 * Performance improvement calculator for Google code results.
 *
 */

package ca.usask.cs.srlab.nlp2api.se.data.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.utility.ItemSorter;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import ca.usask.cs.srlab.nlp2api.config.StaticData;

public class GoogleEvaluator {

    String goldFolder;
    String resultFolder;
    HashMap<Integer, String> queryMap;
    int RESULT_SIZE = 10;
    boolean isBaseline = false;
    int NUM_QUERY_SIZE = 310;

    public GoogleEvaluator(int TOPK, boolean isBaseline) {
        this.goldFolder = getGoldFolder();
        this.resultFolder = getResultFolder(isBaseline);
        this.queryMap = QueryLoader.loadQueries();
        this.isBaseline = isBaseline;
        this.RESULT_SIZE = TOPK;
    }

    protected String getGoldFolder() {
        return StaticData.EXP_HOME + "/search-engine-resx/google/goldset";
    }

    protected String getResultFolder(boolean isBaseline) {
        return StaticData.EXP_HOME + "/search-engine-resx/google/"
                + (isBaseline ? "nl-result" : "proposed-result");
    }

    protected double getAvgPrecisionK(ArrayList<Integer> resultLinks,
                                      ArrayList<Integer> goldLinks, int K) {
        double linePrec = 0;
        K = Math.min(resultLinks.size(), K);
        double found = 0.0;
        for (int index = 0; index < K; index++) {
            int resultEntry = resultLinks.get(index);
            if (checkEntryFound(goldLinks, resultEntry)) {
                found++;
                linePrec += (found / (index + 1));
            }
        }
        if (found == 0)
            return 0;

        return linePrec / found;
    }

    protected double getPrecision(ArrayList<Integer> resultLinks,
                                  ArrayList<Integer> goldLinks, int K) {
        K = Math.min(resultLinks.size(), K);
        double found = 0.0;
        for (int index = 0; index < K; index++) {
            int resultEntry = resultLinks.get(index);
            if (checkEntryFound(goldLinks, resultEntry)) {
                found++;
            }
        }
        if (found == 0)
            return 0;

        return found / K;
    }

    protected double getRecall(ArrayList<Integer> resultLinks,
                               ArrayList<Integer> goldLinks, int K) {
        K = Math.min(resultLinks.size(), K);
        double found = 0.0;
        for (int index = 0; index < K; index++) {
            int resultEntry = resultLinks.get(index);
            if (checkEntryFound(goldLinks, resultEntry)) {
                found++;
            }
        }
        if (found == 0)
            return 0;
        return found / goldLinks.size();
    }

    protected int checkResultFound(ArrayList<Integer> resultLinks,
                                   ArrayList<Integer> goldLinks, int K) {
        for (int resultLink : resultLinks) {
            if (goldLinks.contains(resultLink)) {
                return 1;
            }
        }
        return 0;
    }

    protected boolean checkEntryFound(ArrayList<Integer> goldFiles,
                                      int resultEntry) {
        return goldFiles.contains(resultEntry);
    }

    protected double getRRank(ArrayList<Integer> resultLinks,
                              ArrayList<Integer> goldLinks, int K) {
        K = Math.min(resultLinks.size(), K);
        double rrank = 0;
        for (int i = 0; i < K; i++) {
            int resultEntry = resultLinks.get(i);
            if (checkEntryFound(goldLinks, resultEntry)) {
                // make it double
                rrank = 1.0 / (i + 1);
                break;
            }
        }
        return rrank;
    }

    protected ArrayList<Integer> getNLResults(String nlResultFile) {
        ArrayList<String> results = ContentLoader
                .getAllLinesOptList(nlResultFile);
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 1; i <= results.size(); i++) {
            temp.add(i);
            if (temp.size() == RESULT_SIZE)
                break;
        }
        return temp;
    }

    protected ArrayList<Integer> getGoldLinks(String goldFile) {
        ArrayList<String> lines = ContentLoader.getAllLinesOptList(goldFile);
        ArrayList<Integer> tempList = new ArrayList<>();
        for (String line : lines) {
            int fileID = Integer.parseInt(line.split("\\s+")[0].trim());
            tempList.add(fileID);
        }
        return tempList;
    }

    protected double getDOI(int position, int totalSize) {
        return 1 - (double) position / totalSize;
    }

    protected HashMap<Integer, Double> loadGoldUtilityMap(int caseID) {
        HashMap<Integer, Double> goldUtilMap = new HashMap<>();
        String goldFile = this.goldFolder + "/" + caseID + ".txt";
        ArrayList<String> lines = ContentLoader.getAllLinesOptList(goldFile);
        int rank = 0;
        for (String line : lines) {
            rank++;
            int index = Integer.parseInt(line.trim());
            double utility = getDOI(rank, lines.size());
            goldUtilMap.put(index, utility);
        }
        return goldUtilMap;
    }

    protected double calculateNDCG(int K, ArrayList<Integer> results,
                                   ArrayList<Integer> goldLinks, HashMap<Integer, Double> utilMap) {
        double tempDCG = 0;
        for (int i = 0; i < K; i++) {
            int fileID = results.get(i);
            double rel_i = goldLinks.contains(fileID) ? utilMap.get(fileID) : 0;
            double denominator = Math.log((i + 1) + 1) / Math.log(2);
            if (denominator != 0) {
                tempDCG += rel_i / denominator;
            }
        }
        // now calculate NDCG
        double tempIDCG = 0;
        List<Map.Entry<Integer, Double>> sorted = ItemSorter
                .sortHashMapIntDouble(utilMap);
        int KLim = Math.min(sorted.size(), K);
        for (int i = 0; i < KLim; i++) {
            int fileID = sorted.get(i).getKey();
            double rel_i = utilMap.get(fileID);
            double denominator = Math.log((i + 1) + 1) / Math.log(2);
            if (denominator != 0) {
                tempIDCG += rel_i / denominator;
            }
        }

        // now calculate IDCG
        return tempDCG / tempIDCG;

    }


    public void determinePerformance(int TOPK) {

        double sumPreck = 0;
        double sumPrec = 0;
        double sumRRank = 0;
        double sumTopK = 0;
        double sumRecall = 0;
        double sumNDCG = 0;

        int querySize = NUM_QUERY_SIZE;

        for (int caseID = 1; caseID <= querySize; caseID++) {
            String resultFile = this.resultFolder + "/" + caseID + ".txt";
            String goldFile = this.goldFolder + "/" + caseID + ".txt";
            ArrayList<Integer> goldLinks = getGoldLinks(goldFile);
            ArrayList<Integer> candidateLinks = new ArrayList<>();
            HashMap<Integer, Double> goldUtilMap = loadGoldUtilityMap(caseID);

            if (this.isBaseline) {
                candidateLinks = getNLResults(resultFile);
            } else {
                candidateLinks = ContentLoader.getAllLinesInt(resultFile);
            }

            double precisionK = getAvgPrecisionK(candidateLinks, goldLinks, TOPK);
            double precision = getPrecision(candidateLinks, goldLinks, TOPK);
            double reciprocalRank = getRRank(candidateLinks, goldLinks, TOPK);
            double topK = checkResultFound(candidateLinks, goldLinks, TOPK);
            double recall = getRecall(candidateLinks, goldLinks, TOPK);
            double ndcg = calculateNDCG(TOPK, candidateLinks, goldLinks, goldUtilMap);

            sumPreck += precisionK;
            sumPrec += precision;
            sumRRank += reciprocalRank;
            sumTopK += topK;
            sumRecall += recall;
            sumNDCG += ndcg;
        }

        System.out.println("Hit@" + RESULT_SIZE + ": " + sumTopK / querySize);
        System.out.println("MAP@" + RESULT_SIZE + ": " + sumPreck / querySize);
        System.out.println("MRR@" + RESULT_SIZE + ": " + sumRRank / querySize);
        System.out.println("NDCG" + RESULT_SIZE + ": " + sumNDCG / querySize);

    }
}

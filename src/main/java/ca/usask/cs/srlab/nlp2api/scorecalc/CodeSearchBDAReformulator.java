
/******
 * @author MasudRahman
 * The actual class that reformulates a given NL query into relevant API classes.
 *
 */

package ca.usask.cs.srlab.nlp2api.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.ItemSorter;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;
import ca.usask.cs.srlab.nlp2api.prf.CandidateManager;
import ca.usask.cs.srlab.nlp2api.w2vec.W2VecCollector;

public class CodeSearchBDAReformulator {

    int caseNo;
    String initialQuery;
    int TOPK;
    String scoreKey;
    HashMap<String, ArrayList<Double>> vectorMap;

    public CodeSearchBDAReformulator(int caseNo, String initialQuery, int TOPK,
                                     String scoreKey) {
        this.caseNo = caseNo;
        this.initialQuery = initialQuery;
        this.TOPK = TOPK;
        this.scoreKey = scoreKey;
        this.vectorMap = getVectorMap(caseNo);
    }

    protected String getQueryKeywords(String searchQuery) {
        return new TextNormalizer(searchQuery).normalizeText();
    }

    protected HashMap<String, ArrayList<Double>> getVectorMap(int caseNo) {
        W2VecCollector w2vecCollector = new W2VecCollector(caseNo);
        return w2vecCollector.getWordVectors(true);
    }

    protected String getTopKItems(ArrayList<String> apiNames, int K) {
        String temp = new String();
        for (int i = 0; i < K; i++) {
            if (i < apiNames.size()) {
                temp += apiNames.get(i) + "\t";
            }
        }
        return temp.trim();
    }

    protected ArrayList<String> extractTopKAPINames(
            HashMap<String, Double> scoreMap) {
        List<Map.Entry<String, Double>> sorted = ItemSorter
                .sortHashMapDouble(scoreMap);
        ArrayList<String> suggestion = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sorted) {
            suggestion.add(entry.getKey());
            if (suggestion.size() == TOPK)
                break;
        }
        return suggestion;
    }

    protected ArrayList<String> combinedExtractTopKAPI(
            HashMap<String, Double> bordaScoreMap, HashMap<String, Double> proximityScoreMap) {
        HashMap<String, Double> combined = new HashMap<>();

        switch (scoreKey) {
            case "borda":
                for (String key : bordaScoreMap.keySet()) {
                    combined.put(key, bordaScoreMap.get(key));
                }
                break;
            case "w2vec":
                for (String key : proximityScoreMap.keySet()) {
                    if (combined.containsKey(key)) {
                        double updated = combined.get(key) + proximityScoreMap.get(key);
                        combined.put(key, updated);
                    } else {
                        combined.put(key, proximityScoreMap.get(key));
                    }
                }
                break;
            case "both":
                for (String key : bordaScoreMap.keySet()) {
                    double bordaScore = bordaScoreMap.get(key) * StaticData.Borda_Weight;
                    combined.put(key, bordaScore);
                }
                for (String key : proximityScoreMap.keySet()) {
                    if (combined.containsKey(key)) {
                        double bordaScore = combined.get(key);
                        double proximityScore = proximityScoreMap.get(key)
                                * StaticData.w2vec_Weight;
                        combined.put(key, bordaScore + proximityScore);
                    } else {
                        double score = proximityScoreMap.get(key) * StaticData.w2vec_Weight;
                        combined.put(key, score);
                    }
                }
                break;
            default:
                break;
        }

        return extractTopKAPINames(combined);
    }

    protected void saveAPICandidates(int caseID, HashSet<String> candidates) {
        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(MiscUtility
                .str2List(getQueryKeywords(this.initialQuery)));
        wordList.addAll(candidates);
        String outputFile = StaticData.EXP_HOME + "/candidate/" + caseID
                + ".txt";
        ContentWriter.writeContent(outputFile, wordList);
    }

    protected HashMap<String, Double> normalizedScores(
            HashMap<String, Double> scoreMap) {
        double maxScore = 0;
        for (String key : scoreMap.keySet()) {
            double myScore = scoreMap.get(key);
            if (myScore > maxScore) {
                maxScore = myScore;
            }
        }
        for (String key : scoreMap.keySet()) {
            double myScore = scoreMap.get(key);
            myScore = myScore / maxScore;
            scoreMap.put(key, myScore);
        }
        return scoreMap;
    }

    public String provideRelevantAPIs() {
        CandidateManager candidateManager = new CandidateManager(caseNo,
                initialQuery, StaticData.PRF_SIZE);
        HashMap<String, ArrayList<String>> candidateMap = candidateManager
                .collectQRCandidates();

        BordaScoreCalc borda = new BordaScoreCalc(candidateMap);
        HashMap<String, Double> bscoreMap = borda.calculateBordaScore();

        bscoreMap = normalizedScores(bscoreMap);

        if (scoreKey.equals("borda")) {
            return MiscUtility.list2Str(extractTopKAPINames(bscoreMap));
        }

        String normalized = getQueryKeywords(initialQuery);
        APIKeywordProximityCalc akpCalc = new APIKeywordProximityCalc(
                MiscUtility.str2List(normalized), new ArrayList<String>(
                bscoreMap.keySet()), this.vectorMap);


        HashMap<String, Double> akpScoreMap = akpCalc
                .calculateKeywordAPIProximity();

        if (scoreKey.equals("w2vec")) {
            return MiscUtility.list2Str(extractTopKAPINames(akpScoreMap));
        }

        return MiscUtility.list2Str(combinedExtractTopKAPI(
                bscoreMap, akpScoreMap));
    }
}


/**********
 *
 * @author MasudRahman
 * Code search performance calculator for a given set of queries.
 *
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.lucenecheck.LuceneSearcher;

public class RACKCodeSearcher {

    HashMap<Integer, String> queryMap;
    String indexFolder;
    int TOPK;
    int singleKey;
    String singleQuery;

    public RACKCodeSearcher(HashMap<Integer, String> queryMap, int TOPK) {
        this.queryMap = queryMap;
        this.indexFolder = getIndexFolder();
        this.TOPK = TOPK;
    }

    private String getIndexFolder() {
        return StaticData.EXP_HOME + "/" + StaticData.CODE_EXAMPLE_INDEX;
    }

    public RACKCodeSearcher(int key, String query, int TOPK) {
        this.indexFolder = getIndexFolder();
        this.singleKey = key;
        this.singleQuery = query;
        this.TOPK = TOPK;
    }

    protected void performSingleRACKCodeSearch() {
        LuceneSearcher searcher = new LuceneSearcher(this.singleKey,
                this.singleQuery, indexFolder);
        int goldRank = searcher.getFirstGoldRank(singleKey, TOPK);
        System.out.println(goldRank);
    }

    protected double performRACKCodeSearch() {
        double topKAcc = 0;
        int caseFound = 0;
        for (int key : this.queryMap.keySet()) {
            String query = queryMap.get(key);
            LuceneSearcher searcher = new LuceneSearcher(key, query,
                    indexFolder);
            int goldRank = searcher.getFirstGoldRank(key, TOPK);
            if (goldRank >= 0 && goldRank < TOPK) {
                caseFound++;
            }
        }
        topKAcc = (double) caseFound / queryMap.size();
        return topKAcc;
    }

    protected double performRACKCodeSearchForMRR() {
        double rr = 0;
        for (int key : this.queryMap.keySet()) {
            String query = queryMap.get(key);
            LuceneSearcher searcher = new LuceneSearcher(key, query,
                    indexFolder);
            int goldRank = searcher.getFirstGoldRank(key, TOPK);
            if (goldRank >= 0 && goldRank < TOPK) {

                rr += (1.0 / (goldRank + 1));
            }
        }
        return rr / queryMap.size();
    }


    public HashMap<Integer, Integer> getFirstGoldRankMap() {
        HashMap<Integer, Integer> rankMap = new HashMap<>();
        for (int key : this.queryMap.keySet()) {
            String query = queryMap.get(key);
            LuceneSearcher searcher = new LuceneSearcher(key, query,
                    indexFolder);
            int goldRank = searcher.getFirstGoldRank(key);
            if (!rankMap.containsKey(key)) {
                rankMap.put(key, goldRank);
            }
        }
        return rankMap;
    }
}

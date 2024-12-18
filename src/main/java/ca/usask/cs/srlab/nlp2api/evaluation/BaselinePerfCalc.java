
/****
 *
 * @author MasudRahman
 * Baseline performance calculator
 *
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;

public class BaselinePerfCalc {

    HashMap<Integer, String> queryMap;
    int TOPK = 10;

    public BaselinePerfCalc(int TOPK) {
        this.queryMap = normalizeBaselineQueries(QueryLoader.loadQueries());
        this.TOPK = TOPK;
    }

    private HashMap<Integer, String> normalizeBaselineQueries(HashMap<Integer, String> queryMap) {
        HashMap<Integer, String> cleanMap = new HashMap<>();
        for (int key : queryMap.keySet()) {
            String originalQuery = queryMap.get(key);
            String cleaned = new TextNormalizer(originalQuery).normalizeTextBaseline();
            cleanMap.put(key, cleaned);
        }
        return cleanMap;
    }

    protected void calculateBaseQueryPerformance() {
        RACKCodeSearcher searcher = new RACKCodeSearcher(queryMap, TOPK);
        System.out.println("Hit@" + TOPK + ":"
                + searcher.performRACKCodeSearch());
        System.out.println("MRR@" + TOPK + ":"
                + searcher.performRACKCodeSearchForMRR());
    }
}

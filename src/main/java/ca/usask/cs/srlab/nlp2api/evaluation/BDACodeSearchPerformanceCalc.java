
/*******
 *
 * @author MasudRahman
 * Performance calculator for the proposed search queries.
 *
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import ca.usask.cs.srlab.nlp2api.utility.SuggestedAPILoader;

public class BDACodeSearchPerformanceCalc {

    String resultFile;
    int TOPK;
    HashMap<Integer, String> resultMap;
    boolean apiProvided = false;

    public BDACodeSearchPerformanceCalc(String resultKey, int TOPK) {
        this.resultFile = resultKey;
        this.TOPK = TOPK;
    }

    public BDACodeSearchPerformanceCalc(HashMap<Integer, String> resultMap,
                                        int TOPK, boolean apiProvided) {
        this.resultMap = resultMap;
        this.TOPK = TOPK;
        this.apiProvided = apiProvided;
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

    public void evaluateQueries() {
        HashMap<Integer, String> queryMap = normalizeBaselineQueries(QueryLoader.loadQueries());
        HashMap<Integer, String> apiMap = null;
        if (apiProvided) {
            apiMap = resultMap;
        } else {
            apiMap = SuggestedAPILoader.loadSuggestedAPIs(resultFile);
        }

        HashMap<Integer, String> sQueryMap = new HashMap<>();
        for (int key : queryMap.keySet()) {
            String reformulated = queryMap.get(key) + "\t" + apiMap.get(key);
            sQueryMap.put(key, reformulated);
        }
        RACKCodeSearcher rackCodeSearcher = new RACKCodeSearcher(sQueryMap, TOPK);
        System.out.println("Hit@" + TOPK + ": " + rackCodeSearcher.performRACKCodeSearch() + "\nMRR@" + TOPK + ": "
                + rackCodeSearcher.performRACKCodeSearchForMRR());
    }
}

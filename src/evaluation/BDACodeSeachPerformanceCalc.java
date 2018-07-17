package evaluation;

import java.util.HashMap;
import utility.QueryLoader;
import utility.SuggestedAPILoader;
import config.StaticData;

public class BDACodeSeachPerformanceCalc {

	String resultFile;
	int TOPK;
	HashMap<Integer, String> resultMap;
	boolean apiProvided = false;

	public BDACodeSeachPerformanceCalc(String resultKey, int TOPK) {
		this.resultFile = resultKey;
		this.TOPK = TOPK;
	}

	public BDACodeSeachPerformanceCalc(HashMap<Integer, String> resultMap,
			int TOPK, boolean apiProvided) {
		this.resultMap = resultMap;
		this.TOPK = TOPK;
		this.apiProvided = apiProvided;
	}

	public void evaluateQueries() {
		HashMap<Integer, String> queryMap = QueryLoader.loadQueries();
		HashMap<Integer, String> apiMap = null;
		if (apiProvided) {
			apiMap = resultMap;
		} else {
			apiMap = SuggestedAPILoader.loadSuggestedAPIs(resultFile);
		}

		HashMap<Integer, String> squeryMap = new HashMap<>();
		for (int key : queryMap.keySet()) {
			String reformulated = queryMap.get(key) + "\t" + apiMap.get(key);
			squeryMap.put(key, reformulated);
		}
		// now do the search
		RACKCodeSearcher rsearch = new RACKCodeSearcher(squeryMap, TOPK);
		/*
		 * System.out.println("Top-" + TOPK + "-Acc:" +
		 * rsearch.performRACKCodeSearch()); System.out.println("MRR@" + TOPK +
		 * ": " + rsearch.performRACKCodeSearchForMRR());
		 */
		System.out.println("Hit@"+TOPK+": "+ rsearch.performRACKCodeSearch() + "\nMRR@"+TOPK+": "
				+ rsearch.performRACKCodeSearchForMRR());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String resultKey = "result-175-top-10-max-prox-both-combine-add-code";
		int TOPK = 10;
		new BDACodeSeachPerformanceCalc(resultKey, TOPK).evaluateQueries();
	}
}

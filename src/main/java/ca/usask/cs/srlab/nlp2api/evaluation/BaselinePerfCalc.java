
/****
 * 
 * @author MasudRahman
 * Baseline performance calculator
 * 
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;

public class BaselinePerfCalc {

	HashMap<Integer, String> queryMap;
	int TOPK = 10;

	public BaselinePerfCalc(int TOPK) {
		this.queryMap = QueryLoader.loadQueries();
		this.TOPK = TOPK;
	}

	protected void calculateBaseQueryPerformance() {
		RACKCodeSearcher searcher = new RACKCodeSearcher(queryMap, TOPK);
		System.out.println("Hit@" + TOPK + ":"
				+ searcher.performRACKCodeSearch());
		System.out.println("MRR@" + TOPK + ":"
				+ searcher.performRACKCodeSearchForMRR());
	}

	public static void main(String[] args) {
		int TOPK = 10;
		new BaselinePerfCalc(TOPK).calculateBaseQueryPerformance();
	}
}

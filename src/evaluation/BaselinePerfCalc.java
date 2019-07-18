
/****
 * 
 * @author MasudRahman
 * Baseline performance calculator
 * 
 */

package evaluation;

import java.util.HashMap;
import utility.QueryLoader;

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
		// TODO Auto-generated method stub
		int TOPK = 10;
		new BaselinePerfCalc(TOPK).calculateBaseQueryPerformance();
	}
}

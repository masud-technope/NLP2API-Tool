
/*******
 * 
 * @author MasudRahman
 * Baseline query rank calculator
 * 
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;
import ca.usask.cs.srlab.nlp2api.lucenecheck.LuceneSearcher;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;

public class BaselineRankCollector {

	HashMap<Integer, String> queryMap;

	public BaselineRankCollector() {
		// default constructor
		this.queryMap = QueryLoader.loadQueries();
	}

	public HashMap<Integer, Integer> getBaselineRanks() {
		HashMap<Integer, Integer> temp = new HashMap<>();
		for (int caseNo : this.queryMap.keySet()) {
			String myQuery = this.queryMap.get(caseNo);
			String normQuery = new TextNormalizer(myQuery).normalizeText();
			LuceneSearcher lsearcher = new LuceneSearcher(caseNo, normQuery);
			int baserank = lsearcher.getFirstGoldRank(caseNo);
			temp.put(caseNo, baserank);
		}
		return temp;
	}

	public static void main(String[] args) {
		HashMap<Integer,Integer> baseRankMap=new BaselineRankCollector().getBaselineRanks();
		int topk=0;
		for(int key: baseRankMap.keySet()){
			int rank=baseRankMap.get(key);
			if(rank>=0 && rank<10){
				topk++;
			}
		}
		System.out.println("Top-10 acc:"+ (double)topk/baseRankMap.size());
	}
}

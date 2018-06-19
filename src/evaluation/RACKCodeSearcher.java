package evaluation;

import java.util.HashMap;
import config.StaticData;
import lucenecheck.LuceneSearcher;

public class RACKCodeSearcher {

	HashMap<Integer, String> queryMap;
	String indexFolder;
	int TOPK;
	int singleKey;
	String singleQuery;

	public RACKCodeSearcher(HashMap<Integer, String> queryMap, int TOPK) {
		this.queryMap = queryMap;
		this.indexFolder = StaticData.EXP_HOME + "/"+StaticData.CODE_EXAMPLE_INDEX;
		this.TOPK = TOPK;
	}

	public RACKCodeSearcher(int key, String query, int TOPK) {
		this.indexFolder = StaticData.EXP_HOME + "/"+StaticData.CODE_EXAMPLE_INDEX;
		this.singleKey = key;
		this.singleQuery = query;
		this.TOPK = TOPK;
	}

	protected void performSingleRACKCodeSearch() {
		LuceneSearcher searcher = new LuceneSearcher(this.singleKey,
				this.singleQuery, indexFolder);
		int goldRank = searcher.getFirstGoldRank(singleKey, TOPK);
		if (goldRank >= 0 && goldRank < TOPK) {
			// System.out.println("Case:" + singleKey + "\t" + goldRank);
		} else {
			// System.err.println("Case:" + singleKey + "\t" + goldRank);
		}
	}

	protected double performRACKCodeSearch() {
		// perform RACK code search
		double topKAcc = 0;
		int caseFound = 0;
		for (int key : this.queryMap.keySet()) {
			String query = queryMap.get(key);
			LuceneSearcher searcher = new LuceneSearcher(key, query,
					indexFolder);
			int goldRank = searcher.getFirstGoldRank(key, TOPK);
			if (goldRank >= 0 && goldRank < TOPK) {
				// System.out.println("Case:" + key + "\t" + goldRank);
				caseFound++;
			} else {
				// System.err.println("Case:" + key + "\t" + goldRank);
			}
		}
		topKAcc = (double) caseFound / queryMap.size();
		// System.out.println("Code found:" + topKAcc);
		return topKAcc;
	}

	protected double performRACKCodeSearchForMRR() {
		// perform RACK code search
		double rr = 0;
		for (int key : this.queryMap.keySet()) {
			String query = queryMap.get(key);
			LuceneSearcher searcher = new LuceneSearcher(key, query,
					indexFolder);
			int goldRank = searcher.getFirstGoldRank(key, TOPK);
			if (goldRank >= 0 && goldRank < TOPK) {
				// System.out.println("Case:" + key + "\t" + goldRank);
				rr += (1.0 / (goldRank + 1));
			} else {
				// System.err.println("Case:" + key + "\t" + goldRank);
			}
		}
		return rr / queryMap.size();
	}


	public HashMap<Integer, Integer> getFirstGoldRankMap() {
		// collecting first gold ranks for the queries
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int key = 114;
		String query = "write text image View image list position android data SUM cos Adapter sin";
		int TOPK = 1;
		new RACKCodeSearcher(key, query, TOPK).performSingleRACKCodeSearch();
	}
}

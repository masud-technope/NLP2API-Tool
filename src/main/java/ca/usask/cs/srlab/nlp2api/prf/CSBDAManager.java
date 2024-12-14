/*****
 * 
 * 
 * @author MasudRahman
 * The main class for this tool. It collects the relevant API classes and does the evaluation.
 */


package ca.usask.cs.srlab.nlp2api.prf;

import java.util.ArrayList;
import java.util.HashMap;
import ca.usask.cs.srlab.nlp2api.scorecalc.CodeSearchBDAReformulator;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import ca.usask.cs.srlab.nlp2api.evaluation.BDACodeSeachPerformanceCalc;
import ca.usask.cs.srlab.nlp2api.evaluation.QueryRankComparer;
import ca.usask.cs.srlab.nlp2api.evaluation.ResultAnalyzer;


public class CSBDAManager {

	String resultKey;
	String outputFile;
	String queryFile;
	int TOPK;
	String scoreKey;
	static MaxentTagger tagger = null;

	public CSBDAManager(int TOPK, String resultKey) {
		this.TOPK = TOPK;
		this.outputFile = StaticData.EXP_HOME + "/result/" + resultKey + ".txt";
	}

	public CSBDAManager(int TOPK, String resultKey, String scoreKey) {
		this.TOPK = TOPK;
		this.outputFile = StaticData.EXP_HOME + "/result/" + resultKey + ".txt";
		this.scoreKey = scoreKey;
	}

	public CSBDAManager(int TOPK, String queryFile, String outputFile,
			String scoreKey) {
		this.TOPK = TOPK;
		this.queryFile = StaticData.EXP_HOME + "/" + queryFile;
		this.outputFile = StaticData.EXP_HOME + "/result/" + outputFile;
		this.scoreKey = scoreKey;
	}

	protected String refineQuery(String myquery) {
		String tagged = tagger.tagString(myquery);
		String[] parts = tagged.split("\\s+");
		String refined = new String();
		for (String pair : parts) {
			String[] mypair = pair.split("_");
			if (mypair.length == 2) {
				String tag = mypair[1].trim();
				if (tag.startsWith("NN") || tag.startsWith("VB")) {
					refined += mypair[0].trim() + "\t";
				}
			}
		}
		return refined.trim();
	}

	public void executeCSBDA() {
		HashMap<Integer, String> queryMap = QueryLoader
				.loadQueriesBasic(this.queryFile);
		ArrayList<String> outputItems = new ArrayList<>();
		for (int key = 1; key <= StaticData.TOTAL_CASE_COUNT; key++) {
			String myQuery = queryMap.get(key);
			// myQuery = refineQuery(myQuery);
			CodeSearchBDAReformulator ref = new CodeSearchBDAReformulator(key,
					myQuery, TOPK, scoreKey);
			String suggested = ref.provideRelevantAPIs();

			outputItems.add(myQuery);
			outputItems.add(suggested);
			if (!suggested.trim().isEmpty()) {
				System.out.println("Done:" + key);
			}
		}
		ContentWriter.writeContent(this.outputFile, outputItems);
	}

	protected static void evaluateMeNow(String resultKey,
			boolean strictMatching, int TOPK) {
		int K = TOPK;
		// boolean strictMatching = false;
		ResultAnalyzer analyzer = new ResultAnalyzer(resultKey, K,
				strictMatching, false);
		analyzer.analyzeResults();

		// rank comparison
		new QueryRankComparer(resultKey, true).compareFirstGoldRanksNew();

		// code search performance
		new BDACodeSeachPerformanceCalc(resultKey, TOPK).evaluateQueries();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		String resultKey = "result-310-TI-CS-16-PRF-35-K-10-April06";
		// String resultKey="result-310-PR-TI-only-both";
		// String resultKey="rack-310";
		int TOPK = 10;
		new CSBDAManager(TOPK, resultKey, "borda").executeCSBDA();
		long end = System.currentTimeMillis();
		System.out.println("Time required:" + (end - start) / 1000 + "s");
		evaluateMeNow(resultKey, true, TOPK);
	}
}

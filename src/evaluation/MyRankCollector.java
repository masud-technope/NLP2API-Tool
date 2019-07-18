
/******
 * 
 * @author MasudRahman
 * Rank collector for the proposed search queries.
 * 
 */

package evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lucenecheck.LuceneSearcher;
import config.StaticData;
import text.normalizer.TextNormalizer;
import utility.ContentLoader;
import utility.MiscUtility;
import utility.QueryLoader;
import utility.SuggestedAPILoader;

public class MyRankCollector {

	String candidateKey;
	String candidateFolder;
	String resultkey;
	String resultFile;
	boolean combineWithInitial = true;
	boolean expandAPIName = false;
	boolean apiProvided = false;
	HashMap<Integer, String> resultMap;
	int caseNo;
	String singleQuery;

	public MyRankCollector(int caseNo, String originalQuery, String apiClasses) {
		this.caseNo = caseNo;
		this.singleQuery = new TextNormalizer(originalQuery).normalizeText()
				+ "\t" + new TextNormalizer(apiClasses).normalizeCodeNKeepCC();
		// this.singleQuery=originalQuery;
	}

	public MyRankCollector(String resultKey, boolean combineWithInitial) {
		this.resultFile = StaticData.EXP_HOME + "/result/" + resultKey + ".txt";
		this.combineWithInitial = combineWithInitial;
	}

	public MyRankCollector(String resultKey, boolean combineWithInitial,
			boolean expandAPIName) {
		this.resultFile = StaticData.EXP_HOME + "/result/" + resultKey + ".txt";
		this.combineWithInitial = combineWithInitial;
		this.expandAPIName = expandAPIName;
	}

	public MyRankCollector(HashMap<Integer, String> resultMap,
			boolean combineWithInitial, boolean expandAPIName,
			boolean apiProvided) {
		this.resultMap = resultMap;
		this.combineWithInitial = combineWithInitial;
		this.expandAPIName = expandAPIName;
		this.apiProvided = apiProvided;
	}

	protected HashMap<Integer, String> getReformulationByKey() {
		HashMap<Integer, String> queryMap = QueryLoader.loadQueries();
		HashMap<Integer, String> suggestedAPIs = null;
		if (apiProvided) {
			suggestedAPIs = resultMap;
		} else {
			suggestedAPIs = SuggestedAPILoader.loadSuggestedAPIs(resultFile);
		}

		HashMap<Integer, String> reformulated = new HashMap<>();
		for (int key : queryMap.keySet()) {
			String orgQuery = queryMap.get(key);
			String normOrgQuery = new TextNormalizer(orgQuery)
					.normalizeTextLight();
			String suggested = new String();
			if (suggestedAPIs.containsKey(key)) {
				suggested = suggestedAPIs.get(key);
				if (expandAPIName && !suggested.trim().isEmpty()) {
					suggested = new TextNormalizer(suggested)
							.normalizeCodeNKeepCC();
				} else {
					// do not expand
				}
			}
			String combined = normOrgQuery + "\t" + suggested;
			reformulated.put(key, combined);
		}
		return reformulated;
	}

	protected HashMap<Integer, String> getQRCandidateByKey() {
		HashMap<Integer, String> candidateQRMap = new HashMap<>();
		File[] files = new File(this.candidateFolder).listFiles();
		for (File f : files) {
			int caseNo = Integer.parseInt(f.getName().split("\\.")[0]);
			ArrayList<String> lines = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			for (String line : lines) {
				String[] parts = line.split(":");
				if (parts[0].trim().equals(candidateKey)) {
					String candidateQR = parts[1].trim();
					candidateQRMap.put(caseNo, candidateQR);
					break;
				}
			}
		}
		return candidateQRMap;
	}

	public HashMap<Integer, Integer> getCandidateRanks() {
		HashMap<Integer, Integer> crankMap = new HashMap<>();
		// HashMap<Integer, String> candidateQRMap = getQRCandidateByKey();
		HashMap<Integer, String> candidateQRMap = getReformulationByKey();
		for (int caseNo : candidateQRMap.keySet()) {
			LuceneSearcher lsearcher = new LuceneSearcher(caseNo,
					candidateQRMap.get(caseNo));
			int crank = lsearcher.getFirstGoldRank(caseNo);
			crankMap.put(caseNo, crank);
		}
		return crankMap;
	}

	public int getSingleCandidateRank() {
		LuceneSearcher lsearcher = new LuceneSearcher(this.caseNo,
				this.singleQuery);
		return lsearcher.getFirstGoldRank(caseNo);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseNo = 116;
		String orgQuery = "How to draw text content to Image?";
		String apiClasses = "Image Graphics BufferedImage String DrawRoom Color AnimateImages Draw MyImage Buffimage";
		int rank = new MyRankCollector(caseNo, orgQuery, apiClasses)
				.getSingleCandidateRank();
		System.out.println(rank);
	}
}

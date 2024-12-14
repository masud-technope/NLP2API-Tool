/*****
 * 
 * 
 * @author MasudRahman
 * It collects candidate API classes from relevant Stack Overflow threads for each given query 
 */


package ca.usask.cs.srlab.nlp2api.prf;

import java.util.ArrayList;
import java.util.HashMap;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import ca.usask.cs.srlab.nlp2api.config.StaticData;


public class CandidateManager {

	String initialQuery;
	int TOPK;
	int caseNo;

	public CandidateManager(int caseNo, String initialQuery, int TOPK) {
		this.initialQuery = initialQuery;
		this.TOPK = TOPK;
		this.caseNo = caseNo;
	}

	public HashMap<String, ArrayList<String>> collectQRCandidates() {
		// collect query reformulation candidates
		HashMap<String, ArrayList<String>> prfColl = new PRFProvider(
				this.initialQuery, this.TOPK).getPRFExtendedV2();
		HashMap<String, String> candidateMap = new HashMap<>();
		HashMap<String, ArrayList<String>> candidateMapExt = new HashMap<>();
		for (String prfDocKey : prfColl.keySet()) {
			ArrayList<String> prfDocuments = prfColl.get(prfDocKey);
			PRQueryProvider prqProvider = new PRQueryProvider(prfDocuments,
					prfDocKey);

			ArrayList<String> candidateQR_PR = new ArrayList<>();
			ArrayList<String> candidateQR_TI = new ArrayList<>();

			String indexFolder = new String();

			switch (prfDocKey) {
			case "qc":
				candidateQR_PR = prqProvider.getPRFQueryTerms();
				// if (StaticData.COMBINE_PR_TFIDF) {
				indexFolder = StaticData.EXP_HOME + "/dataset/"
						+ StaticData.SO_QUESTION_CODE_INDEX;
				candidateQR_TI = prqProvider.getPRFQueryTermsTFIDF(indexFolder);
				// }
				break;
			case "ac":
				candidateQR_PR = prqProvider.getPRFQueryTerms();
				// if (StaticData.COMBINE_PR_TFIDF) {
				indexFolder = StaticData.EXP_HOME + "/dataset/"
						+ StaticData.SO_ANSWER_CODE_INDEX;
				candidateQR_TI = prqProvider.getPRFQueryTermsTFIDF(indexFolder);
				// }
				break;
			case "qac":
				candidateQR_PR = prqProvider.getPRFQueryTerms();
				if (StaticData.COMBINE_PR_TFIDF) {
					indexFolder = StaticData.EXP_HOME + "/dataset/"
							+ StaticData.SO_QA_INDEX;
					candidateQR_TI = prqProvider
							.getPRFQueryTermsTFIDF(indexFolder);
				}
				break;
			}

			// refine the candidates
			// candidateQR_PR = JDKFilter.filterNonJDKClasses(candidateQR_PR);
			// candidateQR_TI = JDKFilter.filterNonJDKClasses(candidateQR_TI);

			// now add the candidates
			if (StaticData.PR_ONLY) {
				candidateMap.put(prfDocKey + "_PR",
						MiscUtility.list2Str(candidateQR_PR));
				// System.out.println(MiscUtility.list2Str(candidateQR));
				candidateMapExt.put(prfDocKey + "_PR", candidateQR_PR);
			} else if (StaticData.COMBINE_PR_TFIDF) {
				candidateMap.put(prfDocKey + "_PR",
						MiscUtility.list2Str(candidateQR_PR));
				// System.out.println(MiscUtility.list2Str(candidateQR));
				candidateMapExt.put(prfDocKey + "_PR", candidateQR_PR);

				candidateMap.put(prfDocKey + "_TI",
						MiscUtility.list2Str(candidateQR_TI));
				candidateMapExt.put(prfDocKey + "_TI", candidateQR_TI);
			} else {
				candidateMap.put(prfDocKey + "_TI",
						MiscUtility.list2Str(candidateQR_TI));
				candidateMapExt.put(prfDocKey + "_TI", candidateQR_TI);
			}
		}
		// this.saveReformulationCandidates(this.caseNo, candidateMap);
		return candidateMapExt;
	}

	protected void saveReformulationCandidates(int key,
			HashMap<String, String> candidateMap) {
		String outFile = StaticData.EXP_HOME + "/candidate/" + key + ".txt";
		ArrayList<String> candidates = new ArrayList<>();
		for (String ckey : candidateMap.keySet()) {
			String candidate = ckey + ":" + candidateMap.get(ckey);
			candidates.add(candidate);
		}
		ContentWriter.writeContent(outFile, candidates);
		System.out.println("Done: " + caseNo);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// int caseNo = 2;
		// String query = "How do I compress or zip a directory recursively?";

		HashMap<Integer, String> queryMap = QueryLoader.loadQueries();
		for (int caseNo : queryMap.keySet()) {
			String query = queryMap.get(caseNo);
			String normQuery = new TextNormalizer(query).normalizeTextLight();
			new CandidateManager(caseNo, normQuery, 10).collectQRCandidates();
		}
	}
}

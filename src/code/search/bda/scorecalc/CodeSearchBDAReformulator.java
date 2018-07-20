package code.search.bda.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import config.StaticData;
import text.normalizer.TextNormalizer;
import utility.ContentWriter;
import utility.ItemSorter;
import utility.MiscUtility;
import code.search.bda.CandidateManager;

public class CodeSearchBDAReformulator {

	int caseNo;
	String initialQuery;
	int TOPK;
	String scoreKey;

	public CodeSearchBDAReformulator(int caseNo, String initialQuery, int TOPK,
			String scoreKey) {
		this.caseNo = caseNo;
		this.initialQuery = initialQuery;//  initialQuery.toLowerCase();
		this.TOPK = TOPK;
		this.scoreKey = scoreKey;
	}

	protected String getQueryKeywords(String searchQuery) {
		return new TextNormalizer(searchQuery).normalizeText();
	}

	protected String getTopKItems(ArrayList<String> apiNames, int K) {
		String temp = new String();
		for (int i = 0; i < K; i++) {
			if (i < apiNames.size()) {
				temp += apiNames.get(i) + "\t";
			}
		}
		return temp.trim();
	}

	protected ArrayList<String> extractTopKAPINames(
			HashMap<String, Double> scoreMap) {
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(scoreMap);
		ArrayList<String> suggestion = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			suggestion.add(entry.getKey());
			if (suggestion.size() == TOPK)
				break;
		}
		return suggestion;
	}

	protected ArrayList<String> combinedExtractTopKAPI(
			HashMap<String, Double> bscoreMap, HashMap<String, Double> pscoreMap) {
		HashMap<String, Double> combined = new HashMap<>();

		switch (scoreKey) {
		case "borda":
			for (String key : bscoreMap.keySet()) {
				combined.put(key, bscoreMap.get(key));
			}
			break;
		case "w2vec":
			for (String key : pscoreMap.keySet()) {
				if (combined.containsKey(key)) {
					double updated = combined.get(key) + pscoreMap.get(key);
					combined.put(key, updated);
				} else {
					combined.put(key, pscoreMap.get(key));
				}
			}
			break;
		case "both":
			for (String key : bscoreMap.keySet()) {
				double bscore = bscoreMap.get(key) * StaticData.Borda_Weight;
				combined.put(key, bscore);
			}
			for (String key : pscoreMap.keySet()) {
				if (combined.containsKey(key)) {
					double bscore = combined.get(key);
					double pscore = pscoreMap.get(key)
							* StaticData.w2vec_Weight;
					combined.put(key, bscore + pscore);
				} else {
					double score = pscoreMap.get(key) * StaticData.w2vec_Weight;
					combined.put(key, score);
				}
			}
			break;
		default:
			break;
		}

		return extractTopKAPINames(combined);
	}

	protected void saveAPICandidates(int caseID, HashSet<String> candidates) {
		// save the API candidates
		ArrayList<String> wordList = new ArrayList<>();
		wordList.addAll(MiscUtility
				.str2List(getQueryKeywords(this.initialQuery)));
		wordList.addAll(candidates);
		String outputFile = StaticData.EXP_HOME + "/candidate/" + caseID
				+ ".txt";
		ContentWriter.writeContent(outputFile, wordList);
	}

	protected HashMap<String, Double> normalizedScores(
			HashMap<String, Double> scoreMap) {
		double maxScore = 0;
		for (String key : scoreMap.keySet()) {
			double myScore = scoreMap.get(key);
			if (myScore > maxScore) {
				maxScore = myScore;
			}
		}
		for (String key : scoreMap.keySet()) {
			double myScore = scoreMap.get(key);
			myScore = myScore / maxScore;
			scoreMap.put(key, myScore);
		}
		return scoreMap;
	}

	public String provideRelevantAPIs() {
		// String keywords = getQueryKeywords();
		CandidateManager candidateManager = new CandidateManager(caseNo,
				initialQuery, StaticData.PRF_SIZE);
		HashMap<String, ArrayList<String>> candidateMap = candidateManager
				.collectQRCandidates();
		
		BordaScoreCalc borda = new BordaScoreCalc(candidateMap);
		HashMap<String, Double> bscoreMap = borda.calculateBordaScore();

		// normalize the scores
		bscoreMap = normalizedScores(bscoreMap);
		
		if (scoreKey.equals("borda")) {
			//always save the candidates
			saveAPICandidates(caseNo, new HashSet<String>(bscoreMap.keySet()));
			return MiscUtility.list2Str(extractTopKAPINames(bscoreMap));
		}

		String normalized = getQueryKeywords(initialQuery);
		APIKeywordProximityCalc akpCalc = new APIKeywordProximityCalc(
				MiscUtility.str2List(normalized), new ArrayList<String>(
						bscoreMap.keySet()));

		if (scoreKey.equals("w2vec-pp")) {
			akpCalc.saveAPICandidates(caseNo);
			return new String();
		}

		HashMap<String, Double> akpScoreMap = akpCalc
				.calculateQAProximityLocal();
		// .calculateKeywordAPIProximity();

		if (scoreKey.equals("w2vec")) {
			return MiscUtility.list2Str(extractTopKAPINames(akpScoreMap));
		}

		String combinedQuery = MiscUtility.list2Str(combinedExtractTopKAPI(
				bscoreMap, akpScoreMap));

		return combinedQuery;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseNo = 31;
		String searchQuery = "How do I execute Http Get request?";
		searchQuery = new TextNormalizer(searchQuery).normalizeTextLight();
		int TOPK = 10;
		String suggested = new CodeSearchBDAReformulator(caseNo, searchQuery,
				TOPK, "borda").provideRelevantAPIs();
		System.out.println(suggested);
	}
}

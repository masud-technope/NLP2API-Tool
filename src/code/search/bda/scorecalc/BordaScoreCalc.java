package code.search.bda.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import config.StaticData;
import stopwords.StopWordManager;
import text.normalizer.TextNormalizer;
import utility.ContentWriter;

public class BordaScoreCalc {

	HashMap<String, ArrayList<String>> candidateMap;
	HashMap<String, Double> bordaScoreMap;
	StopWordManager stopManager;

	public BordaScoreCalc(HashMap<String, ArrayList<String>> candidateMap) {
		this.candidateMap = candidateMap;
		this.bordaScoreMap = new HashMap<>();
		this.stopManager = new StopWordManager(true);
	}

	protected void add2BordaScore(HashSet<String> keys,
			ArrayList<String> candidates) {
		int index = 0;
		for (String candidate : candidates) {
			if (keys.contains(candidate)) {
				double score = 1 - (double) index / candidates.size();
				if (bordaScoreMap.containsKey(candidate)) {
					double updated = bordaScoreMap.get(candidate) + score;
					bordaScoreMap.put(candidate, updated);
				} else {
					bordaScoreMap.put(candidate, score);
				}
			}
			index++;
		}
	}

	protected void add2BordaScoreBySimpleCount(HashSet<String> keys,
			ArrayList<String> candidates) {
		for (String candidate : candidates) {
			if (keys.contains(candidate)) {
				if (bordaScoreMap.containsKey(candidate)) {
					double updated = bordaScoreMap.get(candidate) + 1.0;
					bordaScoreMap.put(candidate, updated);
				} else {
					bordaScoreMap.put(candidate, 1.0);
				}
			}
		}
	}

	protected HashSet<String> filterOutNonClasses(HashSet<String> codeElems) {
		String regex = "([A-Z][a-z0-9]+){1,}";
		HashSet<String> classes = new HashSet<>();
		for (String elem : codeElems) {
			if (elem.matches(regex)) {
				classes.add(elem);
			}
		}
		return classes;
	}

	protected HashSet<String> prepareMasterCandidates() {
		HashSet<String> masterList = new HashSet<>();
		String regex = "([A-Z][a-z0-9]+){1,}";
		for (String prfKey : candidateMap.keySet()) {
			ArrayList<String> candidates = candidateMap.get(prfKey);
			for (String candidate : candidates) {
				masterList.add(candidate);
				/*if (candidate.matches(regex)) {
					if (stopManager.isAStopWord(candidate)) {
						// discard this
					} else {
						masterList.add(candidate);
					}
				}*/
			}
		}
		return masterList;
	}

	public HashMap<String, Double> calculateBordaScore() {

		HashSet<String> masterSet = prepareMasterCandidates();

		for (String prfKey : candidateMap.keySet()) {
			ArrayList<String> candidates = candidateMap.get(prfKey);
			add2BordaScore(masterSet, candidates);
			//add2BordaScoreBySimpleCount(masterSet, candidates); //not good
			
			//System.out.println(prfKey+": "+candidates);
		}
		return this.bordaScoreMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

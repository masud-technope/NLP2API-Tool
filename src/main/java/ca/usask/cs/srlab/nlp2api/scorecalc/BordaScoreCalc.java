
/******
 * @author MasudRahman
 * It calculates the Borda score for each candidate API from multiple ranked lists.
 */

package ca.usask.cs.srlab.nlp2api.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.stopwords.StopWordManager;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;

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
			}
		}
		return masterList;
	}

	public HashMap<String, Double> calculateBordaScore() {

		HashSet<String> masterSet = prepareMasterCandidates();

		for (String prfKey : candidateMap.keySet()) {
			ArrayList<String> candidates = candidateMap.get(prfKey);
			add2BordaScore(masterSet, candidates);
		}
		return this.bordaScoreMap;
	}
}

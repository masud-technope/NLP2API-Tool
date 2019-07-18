
/**********
 * @author MasudRahman
 * This class determines the semantic proximity between the query keyword and a candidate API class.
 */

package code.search.bda.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import config.StaticData;
import utility.ContentWriter;
import w2vec.python.W2VecCollector;
import w2vec.python.WordEmbeddingCollector;
import data.analytics.WordProximityDetector;
import edu.stanford.nlp.util.ArrayUtils;

public class APIKeywordProximityCalc {

	ArrayList<String> candidateAPIKeys;
	ArrayList<String> queryTerms;
	HashMap<String, Double> proximityScoreMap;
	HashMap<String, ArrayList<Double>> vectorMap;
	boolean vectorProvided = false;
	public static HashMap<String, ArrayList<Double>> masterEmbeddingMap = new HashMap<>();

	public APIKeywordProximityCalc(ArrayList<String> queryKeywords,
			ArrayList<String> candidateAPIkeys) {
		this.queryTerms = queryKeywords;
		this.candidateAPIKeys = candidateAPIkeys;
		this.proximityScoreMap = new HashMap<>();
	}

	public APIKeywordProximityCalc(ArrayList<String> queryKeywords,
			ArrayList<String> candidateAPIkeys,
			HashMap<String, ArrayList<Double>> vectorMap) {
		this.queryTerms = queryKeywords;
		this.candidateAPIKeys = candidateAPIkeys;
		this.proximityScoreMap = new HashMap<>();
		this.vectorMap = vectorMap;
		this.vectorProvided = true;
	}


	protected void saveAPICandidates(int caseID) {
		// save the API candidates
		ArrayList<String> wordList = new ArrayList<>();
		wordList.addAll(this.queryTerms);
		wordList.addAll(this.candidateAPIKeys);
		String outputFile = StaticData.EXP_HOME + "/candidate/" + caseID
				+ ".txt";
		ContentWriter.writeContent(outputFile, wordList);
	}

	public HashMap<String, Double> calculateQAProximityLocal() {
		ArrayList<String> wordList = new ArrayList<>();
		wordList.addAll(this.queryTerms);
		wordList.addAll(this.candidateAPIKeys);

		// get the master embeddings
		if (masterEmbeddingMap.isEmpty()) {
			if (!vectorProvided) {
				masterEmbeddingMap = new WordEmbeddingCollector()
						.getMasterEmbeddingList();
			} else {
				masterEmbeddingMap = vectorMap;
			}
		}

		for (String apiKey : this.candidateAPIKeys) {
			ArrayList<Double> proxies = new ArrayList<>();
			for (String qterm : this.queryTerms) {
				double proximity = new WordProximityDetector(apiKey, qterm,
						masterEmbeddingMap).determineProximity();
				proxies.add(proximity);
			}
			DescriptiveStatistics desStat = new DescriptiveStatistics(
					ArrayUtils.asPrimitiveDoubleArray(proxies));
			double maxProximity = desStat.getMax();
			this.proximityScoreMap.put(apiKey, maxProximity);
		}
		return this.proximityScoreMap;
	}

	public HashMap<String, Double> calculateKeywordAPIProximity() {

		ArrayList<String> wordList = new ArrayList<>();
		wordList.addAll(this.queryTerms);
		wordList.addAll(this.candidateAPIKeys);
		W2VecCollector w2w = new W2VecCollector(wordList);
		HashMap<String, ArrayList<Double>> vectorMap = null;
		if (vectorProvided) {
			vectorMap = this.vectorMap;
		} else {
			vectorMap = w2w.getWordVectors();
		}
		for (String apiKey : this.candidateAPIKeys) {
			ArrayList<Double> proxies = new ArrayList<>();
			for (String qterm : this.queryTerms) {
				double proximity = new WordProximityDetector(apiKey, qterm,
						vectorMap).determineProximity();
				proxies.add(proximity);
			}
			DescriptiveStatistics desStat = new DescriptiveStatistics(
					ArrayUtils.asPrimitiveDoubleArray(proxies));
			double maxProximity = desStat.getMax();
			this.proximityScoreMap.put(apiKey, maxProximity);
		}

		return this.proximityScoreMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

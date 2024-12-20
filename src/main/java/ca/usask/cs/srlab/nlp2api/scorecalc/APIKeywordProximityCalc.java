
/**********
 * @author MasudRahman
 * This class determines the semantic proximity between the query keyword and a candidate API class.
 */

package ca.usask.cs.srlab.nlp2api.scorecalc;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.data.analytics.WordProximityDetector;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.w2vec.W2VecCollector;
import edu.stanford.nlp.util.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.HashMap;

public class APIKeywordProximityCalc {

    ArrayList<String> candidateAPIKeys;
    ArrayList<String> queryTerms;
    HashMap<String, Double> proximityScoreMap;
    HashMap<String, ArrayList<Double>> vectorMap=new HashMap<>();
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


    @Deprecated
    protected void saveAPICandidates(int caseID) {
        // save the API candidates
        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(this.queryTerms);
        wordList.addAll(this.candidateAPIKeys);
        String outputFile = StaticData.EXP_HOME + "/candidate/" + caseID
                + ".txt";
        ContentWriter.writeContent(outputFile, wordList);
    }

    @Deprecated
    public HashMap<String, Double> calculateQAProximityLocal() {
        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(this.queryTerms);
        wordList.addAll(this.candidateAPIKeys);

        if (masterEmbeddingMap.isEmpty()) {
            masterEmbeddingMap = vectorMap;
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
        for (String apiKey : this.candidateAPIKeys) {
            ArrayList<Double> proxies = new ArrayList<>();
            for (String qTerm : this.queryTerms) {
                double proximity = new WordProximityDetector(apiKey, qTerm,
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

}

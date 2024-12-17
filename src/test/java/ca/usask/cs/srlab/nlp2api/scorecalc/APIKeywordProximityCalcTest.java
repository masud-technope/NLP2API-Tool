package ca.usask.cs.srlab.nlp2api.scorecalc;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.w2vec.W2VecCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class APIKeywordProximityCalcTest {

    ArrayList<String> queryTerms;
    ArrayList<String> candidateAPIs;
    HashMap<String, ArrayList<Double>> vectorMap;
    int caseID = 2;

    @BeforeEach
    public void setup() {
        W2VecCollector collector = new W2VecCollector(caseID);
        vectorMap = collector.getWordVectors(true);
        queryTerms = new ArrayList<>();
        queryTerms.add("compress");
        queryTerms.add("zip");
        queryTerms.add("directory");
        queryTerms.add("recursively");
        candidateAPIs = new ArrayList<>();
        candidateAPIs.add("Zip");
        candidateAPIs.add("DeflaterOutputStream");
        candidateAPIs.add("ZipEntry");
        candidateAPIs.add("ZipOutputStream");
    }

    @Test
    public void testCalculateKeywordAPIProximity() {
        APIKeywordProximityCalc akpCalc = new APIKeywordProximityCalc(queryTerms, candidateAPIs, vectorMap);
        akpCalc.vectorProvided=true;
        HashMap<String, Double> maxProximityMap = akpCalc.calculateKeywordAPIProximity();
        System.out.println(maxProximityMap);
    }
}

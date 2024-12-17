package ca.usask.cs.srlab.nlp2api.analytics;

import ca.usask.cs.srlab.nlp2api.data.analytics.WordProximityDetector;
import ca.usask.cs.srlab.nlp2api.w2vec.W2VecCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class WordProximityDetectorTest {

    int caseNo;
    String firstWord;
    String secondWord;
    HashMap<String, ArrayList<Double>> wordVectorMap;

    @BeforeEach
    public void setup() {
        this.caseNo=2;
        W2VecCollector w2VecCollector = new W2VecCollector(caseNo);
        this.wordVectorMap = w2VecCollector.getWordVectors(true);
        this.firstWord="zip";
        this.secondWord="directory";
    }

    @Test
    public void testDetermineProximity(){
        WordProximityDetector wpDetector=new WordProximityDetector(firstWord, secondWord, wordVectorMap);
        System.out.println(wpDetector.determineProximity());
    }

}

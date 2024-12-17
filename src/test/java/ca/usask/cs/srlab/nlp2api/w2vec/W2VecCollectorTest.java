package ca.usask.cs.srlab.nlp2api.w2vec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;

public class W2VecCollectorTest {

    @BeforeEach
    public void setup(){

    }

    @Test
    public void testGetWordVectors(){
        W2VecCollector collector=new W2VecCollector(2);
        HashMap<String, ArrayList<Double>> vectorMap = collector.getWordVectors(true);
        System.out.println(vectorMap.keySet());
    }
}

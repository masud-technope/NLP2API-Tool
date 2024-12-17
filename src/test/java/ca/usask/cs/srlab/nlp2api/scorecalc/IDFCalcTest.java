package ca.usask.cs.srlab.nlp2api.scorecalc;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class IDFCalcTest {

    ArrayList<String> keys;
    String indexFolder;
    @BeforeEach
    public void setup(){
        keys= new ArrayList<>();
        keys.add("string");
        keys.add("list");
        keys.add("exception");
        keys.add("transport");
        this.indexFolder = StaticData.EXP_HOME
                + "/dataset/"+StaticData.SO_QA_INDEX;
    }

    @Test
    public void testCalculateIDFOnly(){
        System.out.println(new IDFCalc(indexFolder, keys).calculateIDFOnly());
    }
}

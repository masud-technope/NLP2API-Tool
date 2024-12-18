package ca.usask.cs.srlab.nlp2api.evaluation;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NLP2APICodeSearchPerfCalcTest {

    int topK;
    String resultFile;
    @BeforeEach
    public void setup(){
        this.topK=10;
        this.resultFile= StaticData.EXP_HOME+"/NLP2API-Results.txt";
    }

    @Test
    public void testNLP2APICodeSearchPerf(){
        BDACodeSearchPerformanceCalc bdaPerfCalc=new BDACodeSearchPerformanceCalc(resultFile, topK);
        bdaPerfCalc.evaluateQueries();
    }

}

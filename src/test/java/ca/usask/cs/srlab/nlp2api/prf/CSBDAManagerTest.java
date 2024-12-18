package ca.usask.cs.srlab.nlp2api.prf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CSBDAManagerTest {

    int topK;
    @BeforeEach
    public void setup(){
        this.topK=10;
    }

    @Test
    public void testExecuteCSBDAManagerBorda(){
        String scoreKey="borda";
        String outputKey="result-310-TI-CS-16-PRF-35-K-10-borda";
        CSBDAManager manager=new CSBDAManager(topK, outputKey, scoreKey);
        CSBDAManager.evaluateMeNow(outputKey, true, topK);
    }
}

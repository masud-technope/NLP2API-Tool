package ca.usask.cs.srlab.nlp2api.evaluation;

import org.junit.jupiter.api.Test;

public class BaselinePerfCalcTest {

    @Test
    public void testCalculateBaseQueryPerformance(){
        BaselinePerfCalc calc=new BaselinePerfCalc(10);
        calc.calculateBaseQueryPerformance();
    }
}

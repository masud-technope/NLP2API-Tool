package ca.usask.cs.srlab.nlp2api.evaluation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class BaselinePerfCalcTest {

    @Test
    public void testCalculateBaseQueryPerformance(){
        BaselinePerfCalc calc=new BaselinePerfCalc(10);
        calc.calculateBaseQueryPerformance();
    }

    @Test
    public void testGetBaselineRanks(){
        BaselineRankCollector brCollector=new BaselineRankCollector();
        HashMap<Integer,Integer> baseRankMap=brCollector.getBaselineRanks();
        int top10=brCollector.getTop10Ranks(baseRankMap);
        System.out.println("Top-10 acc:"+ (double)top10/baseRankMap.size());
    }
}

package ca.usask.cs.srlab.nlp2api.se.data.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CodeSearchEngineTest {

    int totalResults=10;
    boolean isBaseline = true;
    @BeforeEach
    public void setup(){
        this.isBaseline=false;
    }

    @Test
    public void testGoogleSearch(){
        GoogleEvaluator googleEvaluator=new GoogleEvaluator(totalResults, isBaseline);
        googleEvaluator.determinePerformance(totalResults);
    }

    @Test
    public void testGitHubSearch(){
        new GitHubEvaluator(totalResults, isBaseline).determinePerformance(totalResults);
    }

    @Test
    public void testStackOverflowSearch(){
        new SOEvaluator(totalResults, isBaseline).determinePerformance(totalResults);
    }

}

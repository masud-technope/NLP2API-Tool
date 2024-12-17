package ca.usask.cs.srlab.nlp2api.scorecalc;

import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class CodeSearchBDAReformulatorTest {

    int caseNo;
    String searchQuery;
    HashMap<Integer, String> queryMap;
    int TOPK = 10;

    @BeforeEach
    public void setup() {
        this.caseNo = 20;
        this.queryMap = QueryLoader.loadQueries();
        this.searchQuery = this.queryMap.get(this.caseNo);
    }

    @Test
    public void testProvideRelevantAPIBorda() {
        String normalizedQuery = new TextNormalizer(searchQuery).normalizeTextLight();
        String suggested = new CodeSearchBDAReformulator(caseNo, normalizedQuery,
                TOPK, "borda").provideRelevantAPIs();
        System.out.println(suggested);
    }


    @Test
    public void testProvideRelevantAPIProximity(){
        String normalizedQuery = new TextNormalizer(searchQuery).normalizeTextLight();
        String suggested = new CodeSearchBDAReformulator(caseNo, normalizedQuery, TOPK, "w2vec").provideRelevantAPIs();
        System.out.println(suggested);
    }

    @Test
    public void testProvideRelevantAPICombined(){
        String normalizedQuery = new TextNormalizer(searchQuery).normalizeTextLight();
        String suggested = new CodeSearchBDAReformulator(caseNo, normalizedQuery, TOPK, "both").provideRelevantAPIs();
        System.out.println(suggested);
    }
}

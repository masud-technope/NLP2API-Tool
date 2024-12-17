package ca.usask.cs.srlab.nlp2api.prf;

import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class CandidateManagerTest {

    HashMap<Integer, String> queryMap;
    @BeforeEach
    public void setup(){
        this.queryMap= QueryLoader.loadQueries();
    }

    @Test
    public void testCollectQRCandidates(){
        int caseNo=2;
        String query = queryMap.get(caseNo);
        String normQuery = new TextNormalizer(query).normalizeTextLight();
        System.out.println(new CandidateManager(caseNo, normQuery, 10).collectQRCandidates());
    }
}

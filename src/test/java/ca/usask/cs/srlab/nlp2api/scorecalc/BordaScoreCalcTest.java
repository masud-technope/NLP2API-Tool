package ca.usask.cs.srlab.nlp2api.scorecalc;

import ca.usask.cs.srlab.nlp2api.prf.CandidateManager;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class BordaScoreCalcTest {


    HashMap<Integer, String> queryMap;
    HashMap<String, ArrayList<String>> candidateMap;

    @BeforeEach
    public void setup() {
        int caseNo = 3;
        this.queryMap = QueryLoader.loadQueries();
        String query = queryMap.get(caseNo);
        String normQuery = new TextNormalizer(query).normalizeTextLight();
        this.candidateMap = new CandidateManager(caseNo, normQuery, 10).collectQRCandidates();
    }

    @Test
    public void testCalculateBordaScore() {
        BordaScoreCalc bsCalc = new BordaScoreCalc(this.candidateMap);
        System.out.println(bsCalc.calculateBordaScore());
    }
}

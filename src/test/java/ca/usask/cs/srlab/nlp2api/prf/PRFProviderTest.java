package ca.usask.cs.srlab.nlp2api.prf;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class PRFProviderTest {

    String indexFolder;
    HashMap<Integer, String> queryMap;

    @BeforeEach
    public void setup() {
        this.queryMap = QueryLoader.loadQueries();
        this.indexFolder = StaticData.EXP_HOME + "/dataset/" + StaticData.SO_QA_INDEX;
    }

    @Test
    public void testGetPRF() {
        String query = this.queryMap.get(2);
        String normQuery = new TextNormalizer(query).normalizeText();
        System.out.println(new PRFProvider(normQuery, indexFolder, 10).getPRF());
    }

    @Test
    public void testGetPRFExtended(){
        String query = this.queryMap.get(2);
        String normQuery = new TextNormalizer(query).normalizeText();
        HashMap<String, ArrayList<String>> resultMap=new PRFProvider(normQuery, indexFolder, 10).getPRFExtendedV2();
        for(String key: resultMap.keySet()){
            System.out.println(key+" "+resultMap.get(key).size());
        }
    }
}

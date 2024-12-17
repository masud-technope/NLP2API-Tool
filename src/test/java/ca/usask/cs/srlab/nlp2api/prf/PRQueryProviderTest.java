package ca.usask.cs.srlab.nlp2api.prf;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class PRQueryProviderTest {
    String indexFolder;
    HashMap<Integer, String> queryMap;
    HashMap<String, ArrayList<String>> resultMap;
    PRFProvider prfProvider;
    String docKey;

    @BeforeEach
    public void setup() {
        this.queryMap = QueryLoader.loadQueries();
        this.indexFolder = StaticData.EXP_HOME + "/dataset/" + StaticData.SO_QA_INDEX;
        String query = this.queryMap.get(2);
        String normQuery = new TextNormalizer(query).normalizeText();
        this.resultMap = new PRFProvider(normQuery, indexFolder, 10).getPRFExtendedV2();
        this.docKey = "qac";
    }

    @Test
    public void testGetPRFQueryTerms() {
        if (this.resultMap.containsKey(this.docKey)) {
            ArrayList<String> prfDocuments = this.resultMap.get(this.docKey);
            PRQueryProvider prQueryProvider = new PRQueryProvider(prfDocuments, this.docKey);
            System.out.println(prQueryProvider.getPRFQueryTerms());
        }
    }

    @Test
    public void testGetPRFQueryTermsTFIDF(){
        if (this.resultMap.containsKey(this.docKey)) {
            ArrayList<String> prfDocuments = this.resultMap.get(this.docKey);
            PRQueryProvider prQueryProvider = new PRQueryProvider(prfDocuments, this.docKey);
            System.out.println(prQueryProvider.getPRFQueryTermsTFIDF(this.indexFolder));
        }
    }


    @Test
    public void testGetPRFQueryTermsRocchio(){
        if (this.resultMap.containsKey(this.docKey)) {
            ArrayList<String> prfDocuments = this.resultMap.get(this.docKey);
            PRQueryProvider prQueryProvider = new PRQueryProvider(prfDocuments, this.docKey);
            System.out.println(prQueryProvider.getPRFQueryTermsRocchio());
        }
    }
}
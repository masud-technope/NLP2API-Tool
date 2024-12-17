package ca.usask.cs.srlab.nlp2api.lucenecheck;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LuceneSearcherTest {

    String query;
    int caseNo;
    String codeIndexFolder;
    String qaIndexFolder;

    @BeforeEach
    public void setup() {
        this.caseNo = 2;
        this.query = "How do I compress or zip a directory recursively?";
        this.codeIndexFolder = StaticData.EXP_HOME+"\\"+StaticData.CODE_EXAMPLE_INDEX;
        this.qaIndexFolder=StaticData.EXP_HOME+"\\dataset\\qa-corpus-ext-index";
    }

    @Test
    public void testPerformVSMSearchWithCodebase(){
        LuceneSearcher searcher=new LuceneSearcher(this.caseNo, this.query, this.codeIndexFolder);
        ArrayList<String> results = searcher.performVSMSearch(10);
        System.out.println(results);
    }

    @Test
    public void testGetFirstGoldRank(){
        LuceneSearcher searcher=new LuceneSearcher(this.caseNo, this.query, this.codeIndexFolder);
        System.out.println(searcher.getFirstGoldRank(this.caseNo));
    }


    @Test
    public void testPerformVSMSearchWithQA(){
        LuceneSearcher searcher=new LuceneSearcher(this.caseNo, this.query, this.qaIndexFolder);
        ArrayList<String> results=searcher.performVSMSearch(10);
        System.out.println(results);
    }
}

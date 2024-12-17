/*****
 *
 *
 * @author MasudRahman
 * It collects candidate API classes from relevant Stack Overflow threads for each given query 
 */


package ca.usask.cs.srlab.nlp2api.prf;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;

import java.util.ArrayList;
import java.util.HashMap;


public class CandidateManager {

    String initialQuery;
    int TOPK;
    int caseNo;

    public CandidateManager(int caseNo, String initialQuery, int TOPK) {
        this.initialQuery = initialQuery;
        this.TOPK = TOPK;
        this.caseNo = caseNo;
    }

    public HashMap<String, ArrayList<String>> collectQRCandidates() {
        HashMap<String, ArrayList<String>> prfColl = new PRFProvider(
                this.initialQuery, this.TOPK).getPRFExtendedV2();
        HashMap<String, String> candidateMap = new HashMap<>();
        HashMap<String, ArrayList<String>> candidateMapExt = new HashMap<>();
        for (String prfDocKey : prfColl.keySet()) {
            ArrayList<String> prfDocuments = prfColl.get(prfDocKey);
            PRQueryProvider prqProvider = new PRQueryProvider(prfDocuments,
                    prfDocKey);

            ArrayList<String> candidateQR_PR = new ArrayList<>();
            ArrayList<String> candidateQR_TI = new ArrayList<>();

            String indexFolder = new String();

            switch (prfDocKey) {
                case "qc":
                    candidateQR_PR = prqProvider.getPRFQueryTerms();
                    indexFolder = StaticData.EXP_HOME + "/dataset/"
                            + StaticData.SO_QUESTION_CODE_INDEX;
                    candidateQR_TI = prqProvider.getPRFQueryTermsTFIDF(indexFolder);
                    break;
                case "ac":
                    candidateQR_PR = prqProvider.getPRFQueryTerms();
                    indexFolder = StaticData.EXP_HOME + "/dataset/"
                            + StaticData.SO_ANSWER_CODE_INDEX;
                    candidateQR_TI = prqProvider.getPRFQueryTermsTFIDF(indexFolder);
                    break;
                case "qac":
                    candidateQR_PR = prqProvider.getPRFQueryTerms();
                    if (StaticData.COMBINE_PR_TFIDF) {
                        indexFolder = StaticData.EXP_HOME + "/dataset/"
                                + StaticData.SO_QA_INDEX;
                        candidateQR_TI = prqProvider
                                .getPRFQueryTermsTFIDF(indexFolder);
                    }
                    break;
            }


            if (StaticData.PR_ONLY) {
                candidateMap.put(prfDocKey + "_PR",
                        MiscUtility.list2Str(candidateQR_PR));
                candidateMapExt.put(prfDocKey + "_PR", candidateQR_PR);
            } else if (StaticData.COMBINE_PR_TFIDF) {
                candidateMap.put(prfDocKey + "_PR",
                        MiscUtility.list2Str(candidateQR_PR));
                candidateMapExt.put(prfDocKey + "_PR", candidateQR_PR);

                candidateMap.put(prfDocKey + "_TI",
                        MiscUtility.list2Str(candidateQR_TI));
                candidateMapExt.put(prfDocKey + "_TI", candidateQR_TI);
            } else {
                candidateMap.put(prfDocKey + "_TI",
                        MiscUtility.list2Str(candidateQR_TI));
                candidateMapExt.put(prfDocKey + "_TI", candidateQR_TI);
            }
        }
        return candidateMapExt;
    }

    protected void saveReformulationCandidates(int key,
                                               HashMap<String, String> candidateMap) {
        String outFile = StaticData.EXP_HOME + "/candidate/" + key + ".txt";
        ArrayList<String> candidates = new ArrayList<>();
        for (String cKey : candidateMap.keySet()) {
            String candidate = cKey + ":" + candidateMap.get(cKey);
            candidates.add(candidate);
        }
        ContentWriter.writeContent(outFile, candidates);
        System.out.println("Done: " + caseNo);
    }

}

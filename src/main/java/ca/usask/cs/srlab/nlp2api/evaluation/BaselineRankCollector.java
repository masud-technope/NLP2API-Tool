
/*******
 *
 * @author MasudRahman
 * Baseline query rank calculator
 *
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.lucenecheck.LuceneSearcher;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.QueryLoader;

public class BaselineRankCollector {

    HashMap<Integer, String> queryMap;

    public BaselineRankCollector() {
        this.queryMap = QueryLoader.loadQueries();
    }

    public HashMap<Integer, Integer> getBaselineRanks() {
        HashMap<Integer, Integer> temp = new HashMap<>();
        for (int caseNo : this.queryMap.keySet()) {
            String myQuery = this.queryMap.get(caseNo);
            String normQuery = new TextNormalizer(myQuery).normalizeText();
            LuceneSearcher luceneSearcher = new LuceneSearcher(caseNo, normQuery);
            int baseRank = luceneSearcher.getFirstGoldRank(caseNo);
            temp.put(caseNo, baseRank);
        }
        return temp;
    }

    public int getTop10Ranks(HashMap<Integer, Integer> baseRankMap) {
        int topk = 0;
        for (int key : baseRankMap.keySet()) {
            int rank = baseRankMap.get(key);
            if (rank >= 0 && rank < 10) {
                topk++;
            }
        }
        return topk;
    }
}

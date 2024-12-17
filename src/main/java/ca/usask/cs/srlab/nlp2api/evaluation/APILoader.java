
/****
 *
 * @author MasudRahman
 * Loads the ground truth API classes for the queries.
 */

package ca.usask.cs.srlab.nlp2api.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;

public class APILoader {

    String apiFileURL;

    public APILoader(String apiFileURL) {
        this.apiFileURL = apiFileURL;
    }

    public APILoader() {
        this.apiFileURL = StaticData.EXP_HOME + "/" + StaticData.ORACLE_FILE;
    }

    public HashMap<Integer, ArrayList<String>> getAPIMap() {
        ArrayList<String> lines = ContentLoader
                .getAllLinesOptList(this.apiFileURL);
        HashMap<Integer, ArrayList<String>> tempMap = new HashMap<>();
        int caseNo = 0;
        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;
            if (caseNo % 2 == 1) {
                int key = caseNo / 2 + 1;
                String[] words = line.trim().split("\\s+");
                ArrayList<String> apis = new ArrayList<>(Arrays.asList(words));
                if (line.trim().isEmpty()) {
                    tempMap.put(key, new ArrayList<String>());
                } else {
                    tempMap.put(key, apis);
                }

            }
            caseNo++;
        }
        return tempMap;
    }

    public HashMap<Integer, ArrayList<String>> getAPIMap(boolean sampled,
                                                         ArrayList<Integer> caseKeys) {
        ArrayList<String> lines = ContentLoader
                .getAllLinesOptList(this.apiFileURL);
        HashMap<Integer, ArrayList<String>> tempMap = new HashMap<>();
        int caseNo = 0;
        int caseKeyIndex = 0;
        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;
            if (caseNo % 2 == 1) {
                int key = caseKeys.get(caseKeyIndex++);
                String[] words = line.trim().split("\\s+");
                ArrayList<String> apis = new ArrayList<>(Arrays.asList(words));
                if (line.trim().isEmpty()) {
                    tempMap.put(key, new ArrayList<String>());
                } else {
                    tempMap.put(key, apis);
                }
            }
            caseNo++;
        }
        return tempMap;
    }

    public HashMap<Integer, ArrayList<String>> getGoldAPIMap() {
        return getAPIMap();
    }
}


/************
 *
 * @author MasudRahman
 * Word2vec collector using PYTHON
 *
 */

package ca.usask.cs.srlab.nlp2api.w2vec;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class W2VecCollector {

    ArrayList<String> words;

    int caseID;

    public W2VecCollector(ArrayList<String> words) {
        this.words = words;
    }

    public W2VecCollector(int caseID) {
        this.caseID = caseID;
        this.words = collectCandidateWordAPI();
    }

    protected ArrayList<String> collectCandidateWordAPI() {
        String candidateFile = StaticData.CANDIDATE_FOLDER + "/" + caseID + ".txt";
        return ContentLoader.getAllLinesList(candidateFile);
    }

    protected String getEmbeddingsFile() {
        return StaticData.EMBEDDING_FOLDER + "/" + caseID + ".json";
    }


    public HashMap<String, ArrayList<Double>> getWordVectors(boolean preComputed) {
        HashMap<String, ArrayList<Double>> vectorMap = new HashMap<>();
        String embeddingFile = getEmbeddingsFile();
        try {
            JSONParser parser = new JSONParser();
            JSONObject embeddings = (JSONObject) parser.parse(new FileReader(embeddingFile));
            for (String key : this.words) {
                if (embeddings.containsKey(key)) {
                    JSONArray emb = (JSONArray) embeddings.get(key);
                    vectorMap.put(key, MiscUtility.getArray2List(emb));
                }
            }
        } catch (ParseException | IOException exc) {
            exc.printStackTrace();
        }
        return vectorMap;
    }

    public HashMap<String, ArrayList<Double>> getWordVectors(boolean preComputed, ArrayList<String> words) {
        HashMap<String, ArrayList<Double>> vectorMap = new HashMap<>();
        String embeddingFile = getEmbeddingsFile();
        try {
            JSONParser parser = new JSONParser();
            JSONObject embeddings = (JSONObject) parser.parse(new FileReader(embeddingFile));
            for (String key : this.words) {
                if (embeddings.containsKey(key)) {
                    JSONArray emb = (JSONArray) embeddings.get(key);
                    if (words.contains(key)) {
                        vectorMap.put(key, MiscUtility.getArray2List(emb));
                    }
                }
            }
        } catch (ParseException | IOException exc) {
            exc.printStackTrace();
        }
        return vectorMap;
    }


}


/************
 *
 * @author MasudRahman
 * Word2vec collector using PYTHON
 *
 */

package ca.usask.cs.srlab.nlp2api.w2vec;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class W2VecCollector {

    ArrayList<String> words;
    String pyModuleFile;
    String modelKey;

    int caseID;

    public W2VecCollector(ArrayList<String> words) {
        this.words = words;
        this.pyModuleFile = "./fastText/FastTextW2VecLoader.py";
        this.storeCandidateWords();
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

    protected boolean storeCandidateWords() {
        String wordFile = "./fastText/word2vec-data/words.txt";
        return ContentWriter.writeContent(wordFile, this.words);
    }

    protected int executePythonScript() {
        int exitCode = 1;
        try {
            String[] cmd = new String[2];
            cmd[0] = StaticData.PYTHON_HOME + "/" + "python";
            cmd[1] = this.pyModuleFile;
            String cmdLineStr = cmd[0] + " " + cmd[1];
            CommandLine cmdLine = CommandLine.parse(cmdLineStr);
            DefaultExecutor executor = new DefaultExecutor();
            exitCode = executor.execute(cmdLine);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return exitCode;
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


    public HashMap<String, ArrayList<Double>> getWordVectors() {
        HashMap<String, ArrayList<Double>> temp = new HashMap<>();
        int exitCode = this.executePythonScript();
        if (exitCode == 0) {
            String w2vecFile = "./fastText/word2vec-data/w2vec.txt";
            ArrayList<String> lines = ContentLoader
                    .getAllLinesOptList(w2vecFile);
            for (String line : lines) {
                String[] parts = line.trim().split("\\s+");
                String key = parts[0].trim();
                ArrayList<Double> tempDim = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    double score = Double.parseDouble(parts[i].trim());
                    tempDim.add(score);
                }
                temp.put(key, tempDim);
            }
            //System.out.println("Loaded vec:" + temp.size());
        }
        return temp;
    }

    public static void main(String[] args) {
        // System.out.println(pyModule);
        ArrayList<String> words = new ArrayList<>();
        words.add("java");
        words.add("html");
        words.add("parser");
        new W2VecCollector(words).getWordVectors();
    }
}

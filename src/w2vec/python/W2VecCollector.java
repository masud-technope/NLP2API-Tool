
/************
 * 
 * @author MasudRahman
 * Word2vec collector using PYTHON
 * 
 */

package w2vec.python;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;

public class W2VecCollector {

	ArrayList<String> words;
	String pyModuleFile;
	String modelKey;

	public W2VecCollector(ArrayList<String> words) {
		this.words = words;
		this.pyModuleFile = "./fastText/FastTextW2VecLoader.py";
		this.storeCandidateWords();
	}
	
	protected boolean storeCandidateWords() {
		String wordFile =  "./fastText/word2vec-data/words.txt";
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

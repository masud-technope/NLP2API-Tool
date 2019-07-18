
/****
 * 
 * @author MasudRahman
 * Utility cleaning class
 * 
 */

package w2vec;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;

public class W2VecCleaner {

	String keyFile;
	String nnResultFile;
	String outFolder;

	public W2VecCleaner(String keyFile, String resultFile, String outFolder) {
		this.keyFile = config.StaticData.EXP_HOME + "/" + keyFile;
		this.nnResultFile = StaticData.EXP_HOME + "/nnresult/" + resultFile;
		this.outFolder = StaticData.EXP_HOME + "/nnresult-all/" + outFolder;
	}

	protected void cleanW2Vec() {
		ArrayList<String> keys = ContentLoader.getAllLinesOptList(this.keyFile);
		ArrayList<String> results = ContentLoader
				.getAllLinesOptList(this.nnResultFile);
		HashMap<String, ArrayList<String>> w2vec = new HashMap<>();
		boolean blockStarted = false;
		int keyIndex = -1;
		String key = null;
		for (String result : results) {
			if (result.trim().isEmpty())
				continue;
			if (result.startsWith("Query word?")) {
				blockStarted = true;
				String firstWord = result.split("\\?")[1].trim();
				keyIndex++;
				key = keys.get(keyIndex);
				ArrayList<String> temp = new ArrayList<>();
				temp.add(firstWord);
				w2vec.put(key, temp);

			} else if (blockStarted && key != null) {
				ArrayList<String> temp = w2vec.get(key);
				temp.add(result);
				w2vec.put(key, temp);
			} else {
				// do nothing
				blockStarted = false;
			}
		}
		// System.out.println(w2vec);
		saveW2Vec(keys, w2vec);
	}

	protected void saveW2Vec(ArrayList<String> keys,
			HashMap<String, ArrayList<String>> w2vecMap) {
		// save the word2vec
		for (int index = 0; index < keys.size(); index++) {
			String key = keys.get(index);
			if (w2vecMap.containsKey(key)) {
				ArrayList<String> w2vec = w2vecMap.get(key);
				String myOutFile = this.outFolder + "/" + index + ".txt";
				ContentWriter.writeContent(myOutFile, w2vec);
			}
		}
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String keyFile = "keys.txt";
		String resultFile = "java-ans-body-code-nn.txt";
		String outFolder="java-ans-body-code";
		
		new W2VecCleaner(keyFile, resultFile, outFolder).cleanW2Vec();
	}
}

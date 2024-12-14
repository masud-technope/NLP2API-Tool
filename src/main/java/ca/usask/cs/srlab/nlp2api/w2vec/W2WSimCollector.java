/******
 * 
 * @author MasudRahman
 * Word2vec similarity between two words
 * 
 */

package ca.usask.cs.srlab.nlp2api.w2vec;

import java.util.ArrayList;
import java.util.HashMap;


public class W2WSimCollector {

	ArrayList<String> words;

	public W2WSimCollector(ArrayList<String> words) {
		this.words = words;
	}

	public HashMap<String, ArrayList<Double>> getWordVectors() {
		W2WSimRunner runner = new W2WSimRunner(words);
		ArrayList<String> lines = runner.collectWordVector();
		HashMap<String, ArrayList<Double>> temp = new HashMap<>();
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
		return temp;
	}
}

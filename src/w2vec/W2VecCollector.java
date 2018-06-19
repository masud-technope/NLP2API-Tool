package w2vec;

import java.util.ArrayList;
import java.util.HashMap;

public class W2VecCollector {

	ArrayList<String> words;

	public W2VecCollector(ArrayList<String> words) {
		this.words = words;
	}

	public HashMap<String, ArrayList<String>> collectW2Vec() {
		// collect w2vec for the candidates
		W2VecRunner w2vecRunner = new W2VecRunner(words);
		ArrayList<String> lines = w2vecRunner.collectW2VecInfo();
		return cleanW2Vec(words, lines);
	}

	protected HashMap<String, ArrayList<String>> cleanW2Vec(
			ArrayList<String> keys, ArrayList<String> lines) {
		HashMap<String, ArrayList<String>> w2vec = new HashMap<>();
		boolean blockStarted = false;
		int keyIndex = -1;
		String key = null;
		for (String result : lines) {
			if (result.trim().isEmpty())
				continue;
			if (result.startsWith("Query word?")) {
				blockStarted = true;
				String[] parts = result.split("\\?");
				if (parts.length < 2)
					continue;
				String firstWord = parts[1].trim();
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
		return w2vec;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

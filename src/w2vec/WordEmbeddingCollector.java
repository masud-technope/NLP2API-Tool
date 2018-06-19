package w2vec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import utility.ContentLoader;
import config.StaticData;

public class WordEmbeddingCollector {

	String candidateDir;
	ArrayList<String> candidates;

	public WordEmbeddingCollector() {
		this.candidateDir = StaticData.EXP_HOME + "/candidate";
		this.candidates = new ArrayList<>();
	}

	public WordEmbeddingCollector(ArrayList<String> candidates) {
		this.candidates = candidates;
	}

	protected HashSet<String> developTokens() {
		File[] files = new File(this.candidateDir).listFiles();
		HashSet<String> tokens = new HashSet<>();
		for (File f : files) {
			tokens.addAll(ContentLoader.getAllTokensSC(f.getAbsolutePath()));
		}
		return tokens;
	}

	public HashMap<String, ArrayList<Double>> getMasterEmbeddingList() {
		if (this.candidates.isEmpty()) {
			this.candidates = new ArrayList<String>(developTokens());
		}
		W2WSimCollector w2w = new W2WSimCollector(new ArrayList<String>(
				this.candidates));
		return w2w.getWordVectors();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WordEmbeddingCollector wecoll = new WordEmbeddingCollector();
		System.out.println(wecoll.getMasterEmbeddingList());
	}
}

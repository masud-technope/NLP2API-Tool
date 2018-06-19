package code.search.bda.scorecalc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import config.StaticData;

public class IDFCalc {

	String indexFolder;
	ArrayList<String> mykeys;
	public static final String FIELD_CONTENTS = "contents";

	public IDFCalc(String indexFolder, ArrayList<String> keys) {
		this.indexFolder = indexFolder;
		this.mykeys = keys;
	}

	protected double getIDF(int N, int DF) {
		// getting the IDF
		if (DF == 0)
			return 0;
		return Math.log(1 + (double) N / DF);
	}

	public HashMap<String, Double> calculateIDFOnly() {
		IndexReader reader = null;
		HashMap<String, Double> inverseDFMap = new HashMap<>();
		try {
			reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder).toPath()));
			// String targetTerm = "breakpoint";

			// now go for the IDF
			int N = reader.numDocs();
			double maxIDF = 0;
			for (String term : mykeys) {
				Term t = new Term(FIELD_CONTENTS, term);
				int docFreq = reader.docFreq(t);
				double idf = getIDF(N, docFreq);
				if (!inverseDFMap.containsKey(term)) {
					inverseDFMap.put(term, idf);
					if (idf > maxIDF) {
						maxIDF = idf;
					}
				}
			}
			// now normalize the IDF scores
			for (String term : this.mykeys) {
				double idf = inverseDFMap.get(term);
				idf = idf / maxIDF;
				inverseDFMap.put(term, idf);
			}

		} catch (Exception exc) {
			// handle the exception
		}
		return inverseDFMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> keys = new ArrayList<>();
		keys.add("string");
		keys.add("list");
		keys.add("exception");
		keys.add("transport");
		String indexFolder = StaticData.EXP_HOME
				+ "/dataset/qeck-index";
		System.out.println(new IDFCalc(indexFolder, keys).calculateIDFOnly());
	}
}


/*****
 * @author MasudRahman
 * IDF calculator from a corpus index.
 * 
 */

package ca.usask.cs.srlab.nlp2api.scorecalc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

public class IDFCalc {

	String indexFolder;
	ArrayList<String> myKeys;
	public static final String FIELD_CONTENTS = "contents";

	public IDFCalc(String indexFolder, ArrayList<String> keys) {
		this.indexFolder = indexFolder;
		this.myKeys = keys;
	}

	protected double getIDF(int N, int DF) {
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

			int N = reader.numDocs();
			double maxIDF = 0;
			for (String term : myKeys) {
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
			for (String term : this.myKeys) {
				double idf = inverseDFMap.get(term);
				idf = idf / maxIDF;
				inverseDFMap.put(term, idf);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return inverseDFMap;
	}
}


/*******
 * 
 * @author MasudRahman
 * Semantic proximity calculator given the word embedding.
 * 
 */

package data.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import similarity.CosineMeasure;
import w2vec.python.W2VecCollector;
import edu.stanford.nlp.util.ArrayUtils;

public class WordProximityDetector {

	String firstWord;
	String secondWord;
	HashMap<String, ArrayList<Double>> proximityMap;

	public WordProximityDetector(String firstWord, String secondWord,
			HashMap<String, ArrayList<Double>> proximityMap) {
		this.firstWord = firstWord;
		this.secondWord = secondWord;
		this.proximityMap = proximityMap;
	}

	public WordProximityDetector(String firstWord, String secondWord) {
		this.firstWord = firstWord;
		this.secondWord = secondWord;
		this.proximityMap = developProximity();
	}

	public WordProximityDetector() {
		// default constructor
	}

	protected HashMap<String, ArrayList<Double>> developProximity() {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(this.firstWord);
		temp.add(this.secondWord);
		//W2WSimCollector w2wcoll = new W2WSimCollector(temp);
		//return w2wcoll.getWordVectors();
		W2VecCollector w2vcoll=new W2VecCollector(temp);
		return w2vcoll.getWordVectors();
	}

	public double determineProximity(ArrayList<Double> firstVec,
			ArrayList<Double> secondVec) {
		double[] list1 = ArrayUtils.asPrimitiveDoubleArray(firstVec);
		double[] list2 = ArrayUtils.asPrimitiveDoubleArray(secondVec);
		return CosineMeasure.getCosineSimilarity(list1, list2);
	}

	public double determineProximity() {
		double proximity = 0;
		if (this.proximityMap.containsKey(firstWord)
				&& this.proximityMap.containsKey(secondWord)) {
			ArrayList<Double> listD1 = this.proximityMap.get(firstWord);
			ArrayList<Double> listD2 = this.proximityMap.get(secondWord);
			double[] list1 = ArrayUtils.asPrimitiveDoubleArray(listD1);
			double[] list2 = ArrayUtils.asPrimitiveDoubleArray(listD2);
			proximity = CosineMeasure.getCosineSimilarity(list1, list2);
		}
		return proximity;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String firstWord = "HttpRequest";
		String secondWord = "HttpServletRequest";
		System.out.println(new WordProximityDetector(firstWord, secondWord)
				.determineProximity());
	}
}

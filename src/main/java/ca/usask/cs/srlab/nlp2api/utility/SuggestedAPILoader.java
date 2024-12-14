
/*****
 * 
 * @author MasudRahman
 * Utility class for loading the suggested API classes by NLP2API
 * 
 */

package ca.usask.cs.srlab.nlp2api.utility;

import java.util.ArrayList;
import java.util.HashMap;

public class SuggestedAPILoader {
	public static HashMap<Integer, ArrayList<String>> loadSuggestedAPIList(
			String resultFile) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(resultFile);
		HashMap<Integer, ArrayList<String>> temp = new HashMap<>();
		int index = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 1) {
				String query = lines.get(i);
				ArrayList<String> apis = new ArrayList<String>(
						MiscUtility.str2List(query));
				index = i / 2 + 1;
				temp.put(index, apis);
			}
		}
		return temp;
	}

	public static HashMap<Integer, String> loadSuggestedAPIs(String resultFile) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(resultFile);
		HashMap<Integer, String> temp = new HashMap<>();
		int index = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 1) {
				String query = lines.get(i);
				index = i / 2 + 1;
				temp.put(index, query);
			}
		}
		return temp;
	}
}

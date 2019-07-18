
/*****
 * 
 * @author MasudRahman
 * Utility class for loading the search queries.
 * 
 */

package utility;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class QueryLoader {

	public static HashMap<Integer, String> loadQueries() {
		String oracleFile = StaticData.EXP_HOME + "/"+StaticData.ORACLE_FILE;
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(oracleFile);
		HashMap<Integer, String> temp = new HashMap<>();
		int index = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				String query = lines.get(i);
				index = i / 2 + 1;
				temp.put(index, query);
			}
		}
		return temp;
	}
	
	public static HashMap<Integer, String> loadQueriesBasic(String oracleFile) {
		//String oracleFile = StaticData.EXP_HOME + "/oracle-310.txt";
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(oracleFile);
		HashMap<Integer, String> temp = new HashMap<>();
		int index = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (i % 2 == 0) {
				String query = lines.get(i);
				index = i / 2 + 1;
				temp.put(index, query);
			}
		}
		return temp;
	}

	public static HashMap<Integer, String> loadQueriesReformulated(
			String queryFile) {

		ArrayList<String> lines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> temp = new HashMap<>();
		int index = 0;
		for (int i = 0; i < lines.size(); i += 2) {
			if (i % 2 == 0) {
				String query = lines.get(i);
				index = i / 2 + 1;
				String apiNames = lines.get(i + 1);
				temp.put(index, query + " " + apiNames);
			}
		}
		return temp;
	}

	public static HashMap<Integer, String> loadQueries(String queryFile) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> temp = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.trim().split("\\s+");
			int caseID = Integer.parseInt(parts[0]);
			String myQuery = new String();
			for (int i = 1; i < parts.length; i++) {
				myQuery += parts[i] + " ";
			}
			temp.put(caseID, myQuery);
		}
		return temp;
	}

	public static void main(String[] args) {
		HashMap<Integer, String> queryMap = loadQueries();
		for (int key = 1; key <= 310; key++) {
			System.out.println(queryMap.get(key));
		}
	}
}

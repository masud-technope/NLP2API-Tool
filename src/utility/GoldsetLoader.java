package utility;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class GoldsetLoader {
	public static HashMap<Integer, ArrayList<String>> loadGoldset() {
		String oracleFile = StaticData.EXP_HOME + "/"+StaticData.ORACLE_FILE;
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(oracleFile);
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
	
	public static HashMap<Integer, ArrayList<String>> loadAPIset(String apiResultFile) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(apiResultFile);
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
	
}

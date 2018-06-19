package evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import utility.SelectedTasks;
import config.StaticData;

public class ResultAnalyzer {

	String oracleFile;
	String resultFile;
	HashMap<Integer, ArrayList<String>> results;
	HashMap<Integer, ArrayList<String>> golddata;
	int K;
	boolean strictMatching = false;
	ArrayList<Integer> selectedTasks;
	boolean sampled = false;

	public ResultAnalyzer(String resultKey, int K, boolean strictMatching,
			boolean sampled) {
		// variable initialization
		this.strictMatching = strictMatching;
		this.oracleFile = StaticData.EXP_HOME + "/"+StaticData.ORACLE_FILE;
		this.resultFile =  resultKey;
		this.results = new HashMap<>();
		this.golddata = new HashMap<>();
		this.golddata = new APILoader(oracleFile).getGoldAPIMap();
		if (sampled) {
			this.sampled = true;
			this.selectedTasks = SelectedTasks.loadSelectedTasks();
			this.results = new APILoader(this.resultFile).getAPIMap(true,
					selectedTasks);
		} else {
			this.results = new APILoader(this.resultFile).getAPIMap();
		}
		this.K = K;
	}

	public ResultAnalyzer(HashMap<Integer, ArrayList<String>> resultMap, int K,
			boolean strictMatching) {
		this.strictMatching = strictMatching;
		this.oracleFile = StaticData.EXP_HOME + "/"+StaticData.ORACLE_FILE;
		this.results = resultMap;
		this.golddata = new HashMap<>();
		this.golddata = new APILoader().getGoldAPIMap();
		this.K = K;
	}

	protected String[] decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		return ftokens;
	}

	protected void collectGoldAPIs() {
		// collect gold APIs
		try {
			Scanner scanner = new Scanner(new File(this.oracleFile));
			int key = 0;
			while (scanner.hasNext()) {
				scanner.nextLine();
				String goldAPI = scanner.nextLine().trim();
				String[] apis = goldAPI.split("\\s+");
				ArrayList<String> apilist = new ArrayList<>(Arrays.asList(apis));
				this.golddata.put(++key, apilist);
			}
			scanner.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void collectExpResult() {
		// collecting experimental results
		try {
			Scanner scanner = new Scanner(new File(this.resultFile));
			int key = 0;
			int qwordcount = 0;
			while (scanner.hasNext()) {
				String qwords = scanner.nextLine();
				qwordcount += qwords.split("\\s+").length;
				String resultAPI = scanner.nextLine().trim();
				if (resultAPI.isEmpty()) {
					this.results.put(++key, new ArrayList<String>());
					continue;
				}
				String[] apis = resultAPI.split("\\s+");
				ArrayList<String> apilist = new ArrayList<>(Arrays.asList(apis));
				this.results.put(++key, apilist);
			}
			scanner.close();

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected int isApiFound_K(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		outer: for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			for (String gapi : gapis) {
				if (gapi.endsWith(api) || api.endsWith(gapi)) {
					// System.out.println("Gold:"+gapi+"\t"+"Result:"+api);
					found = 1;
					break outer;
				}
			}
		}
		return found;
	}

	protected int isApiFound_K_Flexible(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// check if correct API is found
		K = rapis.size() < K ? rapis.size() : K;
		int found = 0;
		for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			boolean foundBool = isAPIFound_Flexible(api, gapis);
			if (foundBool) {
				found = 1;
				// System.out.println(api);
				// System.out.println(gapis);
				break;
			}
		}
		return found;
	}

	protected boolean isApiFound(String api, ArrayList<String> gapis) {
		// check if the API can be found
		for (String gapi : gapis) {
			if (gapi.endsWith(api) || api.endsWith(gapi)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isAPIFound_Flexible(String api, ArrayList<String> gapis) {
		ArrayList<String> rtokens = new ArrayList<String>(
				Arrays.asList(decomposeCamelCase(api)));
		for (String gapi : gapis) {
			ArrayList<String> gtokens = new ArrayList<String>(
					Arrays.asList(decomposeCamelCase(gapi)));
			if (gtokens.containsAll(rtokens) || rtokens.containsAll(gtokens)) {
				return true;
			}
		}
		return false;
	}

	protected double getRRank(ArrayList<String> rapis, ArrayList<String> gapis,
			int K) {
		K = rapis.size() < K ? rapis.size() : K;
		double rrank = 0;
		for (int i = 0; i < K; i++) {
			String api = rapis.get(i);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					rrank = 1.0 / (i + 1);
					break;
				}
			} else {
				if (isAPIFound_Flexible(api, gapis)) {
					rrank = 1.0 / (i + 1);
					break;
				}
			}
		}
		return rrank;
	}

	protected double getPrecisionK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// getting precision at K
		if (rapis.size() > 0)
			K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
				}
			} else {
				if (isAPIFound_Flexible(api, gapis)) {
					found++;
				}
			}
		}
		return found / K;
	}

	protected double getAvgPrecisionK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		double linePrec = 0;
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
					linePrec += (found / (index + 1));
				}
			} else {
				if (isAPIFound_Flexible(api, gapis)) {
					found++;
					linePrec += (found / (index + 1));
				}
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}

	protected double getRecallK(ArrayList<String> rapis,
			ArrayList<String> gapis, int K) {
		// getting recall at K
		K = rapis.size() < K ? rapis.size() : K;
		double found = 0;
		for (int index = 0; index < K; index++) {
			String api = rapis.get(index);
			if (strictMatching) {
				if (isApiFound(api, gapis)) {
					found++;
				}
			} else {
				if (isAPIFound_Flexible(api, gapis)) {
					found++;
				}
			}
		}
		return found / gapis.size();
	}

	public void analyzeResults() {
		// analyze two results and compare performance
		try {
			int correct_sum = 0;
			double rrank_sum = 0;
			double precision_sum = 0;
			double preck_sum = 0;
			double recall_sum = 0;
			double fmeasure_sum = 0;

			for (int key : this.golddata.keySet()) {
				try {

					if (sampled)
						if (!selectedTasks.contains(key))
							continue;

					ArrayList<String> rapis = this.results.get(key);
					ArrayList<String> gapis = this.golddata.get(key);

					if (rapis.isEmpty()) {
						// System.err.println(key);
					}

					if (strictMatching)
						correct_sum = correct_sum
								+ isApiFound_K(rapis, gapis, K);
					else
						correct_sum = correct_sum
								+ isApiFound_K_Flexible(rapis, gapis, K);

					rrank_sum = rrank_sum + getRRank(rapis, gapis, K);
					double prec = 0;
					prec = getPrecisionK(rapis, gapis, K);
					precision_sum = precision_sum + prec;
					double preck = 0;
					preck = getAvgPrecisionK(rapis, gapis, K);
					preck_sum = preck_sum + preck;
					double recall = 0;
					recall = getRecallK(rapis, gapis, K);
					recall_sum = recall_sum + recall;

					if (preck + recall > 0) {
						fmeasure_sum = fmeasure_sum
								+ ((2 * preck * recall) / (preck + recall));
						// System.out.print(key+",");
					}

				} catch (Exception exc) {
				}
			}

			//System.out.println();
			// System.out.println("K=" + K);
			if (!sampled) {
				/*
				 * System.out.println(K + "=" + (double) correct_sum /
				 * this.golddata.size() + ",\t"); System.out.println(rrank_sum /
				 * this.golddata.size() + ",\t"); System.out.println(preck_sum /
				 * this.golddata.size() + ",\t"); System.out.println(recall_sum
				 * / this.golddata.size() + ",\t"); System.out.println();
				 */
				System.out.println((double) correct_sum/this.golddata.size()+",\t"+rrank_sum /
						 this.golddata.size()+",\t"+preck_sum /this.golddata.size()+",\t"+recall_sum
						 /this.golddata.size()+",\t"+fmeasure_sum/this.golddata.size()+",");
				//System.out.println("Dataset:"+this.golddata.size());

			} else {
				/*
				 * System.out.println(K + "=" + (double) correct_sum /
				 * selectedTasks.size() + ",\t"); System.out.println(rrank_sum /
				 * selectedTasks.size() + ",\t"); System.out.println(preck_sum /
				 * selectedTasks.size() + ",\t"); System.out.println(recall_sum
				 * / selectedTasks.size() + ",\t"); System.out.println();
				 */
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// String resultKey="result-all-noun-verb-June18-10-no-weights-new";
		// String
		// resultKey="result-all-noun-verb-June18-10-with-weights-abc-325-575-10-v3";
		String resultKey = "result-350-Mar30-borda";
		// String resultKey="result-175-top-10-max-prox-cc-1";
		// for (int K = 1; K <= 10; K++) {
		int K = 10;
		boolean strictMatching = true;
		ResultAnalyzer analyzer = new ResultAnalyzer(resultKey, K,
				strictMatching, false);
		analyzer.analyzeResults();
		// }
	}
}

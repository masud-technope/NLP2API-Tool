
/******
 * @author MasudRahman
 * The runner class for the tool
 * 
 */

package ca.usask.cs.srlab.nlp2api.runner;

import java.util.ArrayList;
import java.util.HashMap;
import ca.usask.cs.srlab.nlp2api.se.data.manager.GitHubEvaluator;
import ca.usask.cs.srlab.nlp2api.se.data.manager.GoogleEvaluator;
import ca.usask.cs.srlab.nlp2api.se.data.manager.SOEvaluator;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;
import ca.usask.cs.srlab.nlp2api.prf.CSBDAManager;
import ca.usask.cs.srlab.nlp2api.scorecalc.CodeSearchBDAReformulator;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.evaluation.APILoader;
import ca.usask.cs.srlab.nlp2api.evaluation.BDACodeSearchPerformanceCalc;
import ca.usask.cs.srlab.nlp2api.evaluation.QueryRankComparer;
import ca.usask.cs.srlab.nlp2api.evaluation.ResultAnalyzer;

public class NLP2APIRunner {

	protected static String extractSingleQuery(int startIndex, String[] args) {
		String temp = new String();
		for (int index = startIndex; index < args.length; index++) {
			temp += args[index] + "\t";
		}
		return temp;
	}

	protected static HashMap<Integer, String> curtailQueries(int length,
			HashMap<Integer, ArrayList<String>> resultMap) {
		HashMap<Integer, String> temp = new HashMap<>();
		for (int key : resultMap.keySet()) {
			ArrayList<String> apis = resultMap.get(key);
			ArrayList<String> myList = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				if (i < apis.size()) {
					myList.add(apis.get(i));
				}
			}
			temp.put(key, MiscUtility.list2Str(myList));
		}
		return temp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StaticData.EXP_HOME = System.getProperty("user.dir");
		if (args.length > 0) {
			// String currentFolder = StaticData.EXP_HOME;
			// variables to store info
			String query = new String();
			String queryFile = "./NL-Query+GroundTruth.txt";
			String outputFile = "./NLP2API-queries.txt";
			String task = new String();
			int topk = 10;
			String sengine="google";
			
			for (int i = 0; i < args.length; i += 2) {
				String key = args[i];
				switch (key) {
				case "-query":
					query = extractSingleQuery(i + 1, args);
					break;
				case "-queryFile":
					queryFile = args[i + 1];
					break;
				case "-K":
					topk = Integer.parseInt(args[i + 1].trim());
					break;
				case "-outputFile":
					outputFile = args[i + 1];
					break;
				case "-resultFile":
					outputFile = args[i + 1];
					break;
				case "-task":
					task = args[i + 1];
					break;
				case "-se":
					sengine=args[i+1];
					break;
				}
			}

			if (task.equals("reformulate")) {
				if (!query.isEmpty()) {
					if (topk > 0) {
						String normalizedQuery = new TextNormalizer(query)
								.normalizeTextLight();
						//collect candidates
						CodeSearchBDAReformulator csReformulator = new CodeSearchBDAReformulator(
								0, normalizedQuery, topk, "w2vec-pp");
						csReformulator.provideRelevantAPIs();
						//get the APIs
						csReformulator = new CodeSearchBDAReformulator(
								0, normalizedQuery, topk, "both");
						System.out.println(csReformulator.provideRelevantAPIs());
					}
				} else if (!queryFile.isEmpty()) {
					if (topk > 0) {
						long start = System.currentTimeMillis();
						System.out.println("Starting query reformulation. This may take a few minutes!");
						//collect candidates
						CSBDAManager csManager = new CSBDAManager(topk,
								queryFile, outputFile, "w2vec-pp");
						csManager.executeCSBDA();
						//get the APIs
						csManager = new CSBDAManager(topk,
								queryFile, outputFile, "both");
						csManager.executeCSBDA();
						
						long end = System.currentTimeMillis();
						System.out.println("Time needed: " + (end - start)
								/ 1000 + " s");
					}
				} else {
					System.out.println("Please enter your query or the query file.");
				}
			} else if (task.equals("evaluate-as")) {
				if (!outputFile.isEmpty()) {
					if (topk > 0) {
						System.out.println("API Suggestion Performance:");
						ResultAnalyzer analyzer = new ResultAnalyzer(
								outputFile, topk, true, false);
						analyzer.analyzeResults();
					}
				}
			} else if (task.equals("evaluate-qe")) {
				if (!outputFile.isEmpty()) {
					if (topk > 0) {
						System.out.println("Query Improvement Statistics:");
						// String resultFile = outputFile;
						HashMap<Integer, ArrayList<String>> resultMap = new APILoader(
								outputFile).getAPIMap();
						HashMap<Integer, String> curtailed = curtailQueries(
								topk, resultMap);
						new QueryRankComparer(curtailed, true)
								.compareFirstGoldRanksNew();
					}
				}
			} else if (task.equals("evaluate-cs")) {
				if (!outputFile.isEmpty()) {
					if (topk > 0) {
						System.out
								.println("Code Segment Retrieval Performance:");
						new BDACodeSearchPerformanceCalc(outputFile, topk)
								.evaluateQueries();
					}
				} else {
					System.out
							.println("Please enter the reformulated query file");
				}
			} else if(task.equals("evaluate-se")){
				if(topk>0){
					if(!sengine.isEmpty()){
						System.out.println("Results for "+sengine);
						switch (sengine) {
						case "google":
							System.out.println("Performance of NL queries");
							new GoogleEvaluator(topk, true).determinePerformance(topk);
							System.out.println("Performance using proposed queries");
							new GoogleEvaluator(topk, false).determinePerformance(topk);
							break;
						case "stackoverflow":
							System.out.println("Performance of NL queries");
							new SOEvaluator(topk, true).determinePerformance(topk);
							System.out.println("Performance using proposed queries");
							new SOEvaluator(topk, false).determinePerformance(topk);
							break;
						case "github":
							System.out.println("Performance of NL queries");
							new GitHubEvaluator(topk, true).determinePerformance(topk);
							System.out.println("Performance using proposed queries");
							new GitHubEvaluator(topk, false).determinePerformance(topk);
							break;
						default:
							break;
						}
					}
				}
			}
		} else {
			System.out.println("Please enter the required parameters!");
		}
	}
}

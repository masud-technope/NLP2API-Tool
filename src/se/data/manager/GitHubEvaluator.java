package se.data.manager;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.QueryLoader;
import config.StaticData;

public class GitHubEvaluator {

	String goldFolder;
	String resultFolder;
	HashMap<Integer, String> queryMap;
	int RESULT_SIZE = 10;
	boolean isBaseline = false;
	int NUM_QUERY_SIZE = 310;

	public GitHubEvaluator(int TOPK, boolean isBaseline) {
		this.goldFolder = StaticData.EXP_HOME
				+ "/search-engine-resx/github/goldset";
		this.resultFolder = StaticData.EXP_HOME + "/search-engine-resx/github/"
				+ (isBaseline ? "nl-result" : "proposed-result");
		this.queryMap = QueryLoader.loadQueries();
		this.isBaseline = isBaseline;
		this.RESULT_SIZE = TOPK;
	}

	protected double getAvgPrecisionK(ArrayList<Integer> resultLinks,
			ArrayList<Integer> goldLinks, int K) {
		double linePrec = 0;
		K = resultLinks.size() < K ? resultLinks.size() : K;
		double found = 0.0;
		for (int index = 0; index < K; index++) {
			int resultEntry = resultLinks.get(index);
			if (checkEntryFound(goldLinks, resultEntry)) {
				found++;
				linePrec += (found / (index + 1));
			}
		}
		if (found == 0)
			return 0;

		return linePrec / found;
	}

	protected double getPrecision(ArrayList<Integer> resultLinks,
			ArrayList<Integer> goldLinks, int K) {
		K = resultLinks.size() < K ? resultLinks.size() : K;
		double found = 0.0;
		for (int index = 0; index < K; index++) {
			int resultEntry = resultLinks.get(index);
			if (checkEntryFound(goldLinks, resultEntry)) {
				found++;
			}
		}
		if (found == 0)
			return 0;

		return found / K;
	}

	protected double getRecall(ArrayList<Integer> resultLinks,
			ArrayList<Integer> goldLinks, int K) {
		K = resultLinks.size() < K ? resultLinks.size() : K;
		double found = 0.0;
		for (int index = 0; index < K; index++) {
			int resultEntry = resultLinks.get(index);
			if (checkEntryFound(goldLinks, resultEntry)) {
				found++;
			}
		}
		if (found == 0)
			return 0;
		return found / goldLinks.size();
	}

	protected int checkResultFound(ArrayList<Integer> resultLinks,
			ArrayList<Integer> goldLinks, int K) {
		for (int resultLink : resultLinks) {
			if (goldLinks.contains(resultLink)) {
				return 1;
			}
		}
		return 0;
	}

	protected boolean checkEntryFound(ArrayList<Integer> goldFiles,
			int resultEntry) {
		return goldFiles.contains(resultEntry);
	}

	protected double getRRank(ArrayList<Integer> resultLinks,
			ArrayList<Integer> goldLinks, int K) {
		K = resultLinks.size() < K ? resultLinks.size() : K;
		double rrank = 0;
		for (int i = 0; i < K; i++) {
			int resultEntry = resultLinks.get(i);
			if (checkEntryFound(goldLinks, resultEntry)) {
				// make it double
				rrank = 1.0 / (i + 1);
				break;
			}
		}
		return rrank;
	}

	protected ArrayList<Integer> getNLResults(String nlResultFile) {
		ArrayList<String> results = ContentLoader
				.getAllLinesOptList(nlResultFile);
		ArrayList<Integer> temp = new ArrayList<>();
		for (int i = 1; i <= results.size(); i++) {
			temp.add(i);
			if (temp.size() == RESULT_SIZE)
				break;
		}
		return temp;
	}

	public void determinePerformance(int TOPK) {

		double sumPreck = 0;
		double sumPrec = 0;
		double sumRRank = 0;
		double sumTopK = 0;
		double sumRecall = 0;

		int qsize = NUM_QUERY_SIZE;

		for (int caseID = 1; caseID <= qsize; caseID++) {
			String resultFile = this.resultFolder + "/" + caseID + ".txt";
			String goldFile = this.goldFolder + "/" + caseID + ".txt";
			ArrayList<Integer> goldLinks = ContentLoader
					.getAllLinesInt(goldFile);
			ArrayList<Integer> candidateLinks = new ArrayList<>();

			if (this.isBaseline) {
				candidateLinks = getNLResults(resultFile);
			} else {
				candidateLinks = ContentLoader.getAllLinesInt(resultFile);
			}

			double preck = getAvgPrecisionK(candidateLinks, goldLinks, TOPK);
			double prec = getPrecision(candidateLinks, goldLinks, TOPK);
			double rrank = getRRank(candidateLinks, goldLinks, TOPK);
			double topk = checkResultFound(candidateLinks, goldLinks, TOPK);
			double recall = getRecall(candidateLinks, goldLinks, TOPK);

			sumPreck += preck;
			sumPrec += prec;
			sumRRank += rrank;
			sumTopK += topk;
			sumRecall += recall;
		}

		System.out.println("Hit@" + RESULT_SIZE + ": " + sumTopK / qsize);
		System.out.println("MAP@" + RESULT_SIZE + ": " + sumPreck / qsize);
		System.out.println("MRR@" + RESULT_SIZE + ": " + sumRRank / qsize);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int TOPK = 10;
		boolean isBaseline = true;
		new GitHubEvaluator(TOPK, isBaseline).determinePerformance(TOPK);

	}
}

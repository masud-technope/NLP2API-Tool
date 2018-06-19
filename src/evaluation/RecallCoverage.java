package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import utility.GoldsetLoader;
import config.StaticData;

public class RecallCoverage {

	String candidateFolder;
	HashMap<Integer, ArrayList<String>> goldMap;

	public RecallCoverage() {
		this.candidateFolder = StaticData.EXP_HOME + "/candidate";
		this.goldMap = GoldsetLoader.loadGoldset();
	}

	protected void determineRecallCoverage() {
		double sumMatched = 0;
		int found=0;
		for (int i = 1; i <= StaticData.TOTAL_CASE_COUNT; i++) {
			String candidateFile = this.candidateFolder + "/" + i + ".txt";
			ArrayList<String> goldAPISet = this.goldMap.get(i);
			// determine overlap
			ArrayList<String> qterms = ContentLoader
					.getAllTokensSC(candidateFile);
			int orgGoldsize = goldAPISet.size();
			goldAPISet.retainAll(qterms);
			System.out.println(goldAPISet);
			double matched = (double) goldAPISet.size() / orgGoldsize;
			sumMatched += matched;
			if(matched>0){
				found++;
			}
		}
		System.out.println("Recall:" + sumMatched / 175);
		System.out.println("Found:"+ (double)found/175);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new RecallCoverage().determineRecallCoverage();
	}
}

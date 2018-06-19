package evaluation;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.ranking.RankingAlgorithm;

import utility.MiscUtility;

public class QueryRankComparer {

	public HashMap<Integer, Integer> baseRanks;
	public HashMap<Integer, Integer> toolRanks;
	String candidateKey;

	public ArrayList<Integer> improvedRanks;
	public ArrayList<Integer> worsenedRanks;
	public ArrayList<Integer> persistedRanks;
	public ArrayList<Integer> base4ImprovedRanks;
	public ArrayList<Integer> base4WorsenedRanks;

	double sumRankImproved = 0;
	double sumRankWorsened = 0;

	public QueryRankComparer(String resultKey, boolean combineWithInitquery) {
		this.baseRanks = new BaselineRankCollector().getBaselineRanks();
		this.toolRanks = new MyRankCollector(resultKey, combineWithInitquery,
				true).getCandidateRanks();
		this.improvedRanks = new ArrayList<>();
		this.worsenedRanks = new ArrayList<>();
		this.persistedRanks = new ArrayList<>();
		this.base4ImprovedRanks = new ArrayList<>();
		this.base4WorsenedRanks = new ArrayList<>();
	}

	public QueryRankComparer(HashMap<Integer, String> resultMap,
			boolean combineWithInitquery) {
		this.baseRanks = new BaselineRankCollector().getBaselineRanks();
		this.toolRanks = new MyRankCollector(resultMap, combineWithInitquery,
				true, true).getCandidateRanks();
		this.improvedRanks = new ArrayList<>();
		this.worsenedRanks = new ArrayList<>();
		this.persistedRanks = new ArrayList<>();
		this.base4ImprovedRanks = new ArrayList<>();
		this.base4WorsenedRanks = new ArrayList<>();
	}

	public void compareFirstGoldRanksNew() {
		// now compare these two ranks
		int improved = 0;
		int worsened = 0;
		int preserved = 0;

		ArrayList<Integer> improvedList = new ArrayList<>();

		double sumImpRR = 0;
		double sumWorRR = 0;
		double sumMyRR = 0;
		double sumBaseRR = 0;

		// ArrayList<Integer> improvedRanks = new ArrayList<>();
		// ArrayList<Integer> worsenedRanks = new ArrayList<>();

		for (int key : baseRanks.keySet()) {
			int baseRank = baseRanks.get(key) + 1;
			int rackRank = toolRanks.get(key) + 1;

			if (rackRank > 0) {
				if (baseRank > 0) {
					if (rackRank < baseRank) {
						improved++;
						improvedRanks.add(rackRank);
						base4ImprovedRanks.add(baseRank);
						sumRankImproved += (rackRank - baseRank);
					} else if (rackRank == baseRank) {
						preserved++;
						persistedRanks.add(rackRank);
					} else if (rackRank > baseRank) {
						worsened++;
						worsenedRanks.add(rackRank);
						base4WorsenedRanks.add(baseRank);
						sumRankWorsened += (rackRank - baseRank);
					}
				} else {
					if (rackRank > 0) {
						improved++;
						// strictRankSum += strictRank;
						improvedRanks.add(rackRank);
						sumRankImproved += (0 - rackRank);
					}
				}
			} else {
				if (baseRank == rackRank) {
					preserved++;
					persistedRanks.add(rackRank);
				} else {
					worsened++;
					base4WorsenedRanks.add(baseRank);
					// temporarily added
					worsenedRanks.add(rackRank);
					sumRankWorsened += (baseRank - 0); // this extent is
														// worsened
				}
			}
		}

		// System.out.println((double) improved / baseRanks.size() + ",\t"
		// + (double) worsened / baseRanks.size());

		System.out.println((double) improved / baseRanks.size() + ",\t"
				+ (double) worsened / baseRanks.size() + ",\t"
				+ (double) preserved / baseRanks.size() + ",\t" + "I/W:"
				+ (double) improved / worsened);

		double MRD = 1.0 / (sumMyRR / baseRanks.size()) - 1.0
				/ (sumBaseRR / baseRanks.size());
		double impMRD = 1.0 / (sumImpRR / improved) - 1.0
				/ (sumBaseRR / baseRanks.size());
		double worMRD = 1.0 / (sumWorRR / improved) - 1.0
				/ (sumBaseRR / baseRanks.size());

		System.out.println((double) sumRankImproved / improved + "\t"
				+ (double) sumRankWorsened / worsened);

		// System.out.println("MRD" + MRD);
		// System.out.println(impMRD + "\t" + worMRD + "\t" + MRD);
		// System.out.println(
		// (double)MiscUtility.getSum(improvedRanks)/improved
		// +"\t"+(double)MiscUtility.getSum(worsenedRanks)/worsened);
		// System.out.println(improvedList);
		// System.out.println(MiscUtility.getMedian(improvedRanks)+"\t"+MiscUtility.getMedian(worsenedRanks));
	}

	public static void main(String[] args) {
		String candidateKey = "qt";
		String resultKey = "qeck-Jan24-full";
		// new QueryRankComparer(candidateKey).compareFirstGoldRanksNew();
		new QueryRankComparer(resultKey, true).compareFirstGoldRanksNew();
	}
}

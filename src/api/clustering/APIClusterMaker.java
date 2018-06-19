package api.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import similarity.CosineMeasure;
import edu.stanford.nlp.util.ArrayUtils;
import evaluation.APILoader;

public class APIClusterMaker {

	HashMap<String, ArrayList<Double>> apiVectors;
	HashMap<String, ArrayList<Double>> queryVectors;
	int clusterSize = 1;
	String querySentence;
	ArrayList<String> queryTerms;

	public APIClusterMaker(int querySize,
			HashMap<String, ArrayList<Double>> apiVectors) {
		this.apiVectors = apiVectors;
		this.clusterSize = querySize;
	}

	public APIClusterMaker(ArrayList<String> qkeys,
			HashMap<String, ArrayList<Double>> apiVectors) {
		this.apiVectors = apiVectors;
		this.clusterSize = qkeys.size();
		this.queryTerms = qkeys;
	}

	public APIClusterMaker(HashMap<String, ArrayList<Double>> queryVectors,
			HashMap<String, ArrayList<Double>> apiVectors) {
		this.apiVectors = apiVectors;
		this.queryVectors = queryVectors;
		this.clusterSize = queryVectors.size();
	}

	public APIClusterMaker(String query, ArrayList<Double> queryVector,
			HashMap<String, ArrayList<Double>> apiVectors) {
		this.querySentence = query;
		this.apiVectors = apiVectors;
		this.clusterSize = query.split("\\s+").length;
		// adding the query into API vector
		this.apiVectors.put(this.querySentence, queryVector);
	}

	protected List<APILocationWrapper> prepareAPILocationGraph() {
		List<APILocationWrapper> clusterInput = new ArrayList<APILocationWrapper>(
				this.apiVectors.size());
		for (String key : this.apiVectors.keySet()) {
			ArrayList<Double> coordinate = this.apiVectors.get(key);
			APILocationWrapper alWrapper = new APILocationWrapper(key,
					coordinate);
			clusterInput.add(alWrapper);
		}
		return clusterInput;
	}

	protected double getQCProximity(double[] points) {
		double totalSim = 0;
		for (String key : this.queryVectors.keySet()) {
			double[] qpoints = ArrayUtils
					.asPrimitiveDoubleArray(this.queryVectors.get(key));
			double similarity = CosineMeasure.getCosineSimilarity(points,
					qpoints);
			totalSim += similarity;
		}
		return totalSim;
	}

	protected double getProximity2Center(double[] cpoints, double[] points) {
		return CosineMeasure.getCosineSimilarity(cpoints, points);
	}

	protected boolean containQueryKey(
			CentroidCluster<APILocationWrapper> centroid,
			ArrayList<String> qkeys) {
		List<APILocationWrapper> mypoints = centroid.getPoints();
		for (APILocationWrapper alwrapper : mypoints) {
			if (qkeys.contains(alwrapper.getAPIName())) {
				return true;
			}
		}
		return false;
	}

	public HashMap<String, Double> getClusteringScoresV2() {
		List<APILocationWrapper> clusterInput = prepareAPILocationGraph();
		KMeansPlusPlusClusterer<APILocationWrapper> clusterer = new KMeansPlusPlusClusterer<APILocationWrapper>(
				this.clusterSize, 10000);
		List<CentroidCluster<APILocationWrapper>> clusterResults = clusterer
				.cluster(clusterInput);
		ArrayList<Integer> selectedCls = new ArrayList<>();
		for (int i = 0; i < clusterResults.size(); i++) {
			CentroidCluster<APILocationWrapper> centroid = clusterResults
					.get(i);
			if (containQueryKey(centroid, this.queryTerms)) {
				if (!selectedCls.contains(i)) {
					selectedCls.add(i);
				}
			}
		}
		HashMap<String, Double> clusterScoreMap = new HashMap<>();
		for (int selected : selectedCls) {
			double[] cpoints = clusterResults.get(selected).getCenter()
					.getPoint();
			for (APILocationWrapper alWrapper : clusterResults.get(selected)
					.getPoints()) {
				double[] mypoints = alWrapper.getPoint();
				String key = alWrapper.getAPIName();
				double proximity = getProximity2Center(cpoints, mypoints);
				if (!clusterScoreMap.containsKey(key)) {
					clusterScoreMap.put(key, proximity);
				}
			}
		}
		return clusterScoreMap;

	}

	public HashMap<String, Double> getClusteringScores() {
		List<APILocationWrapper> clusterInput = prepareAPILocationGraph();
		// initialize a new clustering algorithm.
		// we use KMeans++ with 10 clusters and 10000 iterations maximum.
		// we did not specify a distance measure; the default (euclidean
		// distance) is used.
		KMeansPlusPlusClusterer<APILocationWrapper> clusterer = new KMeansPlusPlusClusterer<APILocationWrapper>(
				1, 10000);
		List<CentroidCluster<APILocationWrapper>> clusterResults = clusterer
				.cluster(clusterInput);
		int selected = -1;
		double maxProximity = 0;
		for (int i = 0; i < clusterResults.size(); i++) {
			double[] centerPoint = clusterResults.get(i).getCenter().getPoint();
			double proximity = getQCProximity(centerPoint);
			if (proximity > maxProximity) {
				maxProximity = proximity;
				selected = i;
			}
		}
		// now only consider the selected cluster
		HashMap<String, Double> clusterScoreMap = new HashMap<>();
		double[] cpoints = clusterResults.get(selected).getCenter().getPoint();
		for (APILocationWrapper alWrapper : clusterResults.get(selected)
				.getPoints()) {
			double[] mypoints = alWrapper.getPoint();
			String key = alWrapper.getAPIName();
			double proximity = getProximity2Center(cpoints, mypoints);
			if (!clusterScoreMap.containsKey(key)) {
				clusterScoreMap.put(key, proximity);
			}
		}
		return clusterScoreMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

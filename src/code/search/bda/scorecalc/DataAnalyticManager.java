package code.search.bda.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import data.analytics.WordProximityDetector;
import w2vec.W2WSimCollector;

public class DataAnalyticManager {

	DirectedGraph<String, DefaultEdge> graph;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
	HashSet<String> vertices;

	public DataAnalyticManager(DirectedGraph<String, DefaultEdge> graph) {
		this.graph = graph;
		this.vertices = new HashSet<>(this.graph.vertexSet());
		this.wgraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
	}

	protected HashMap<String, ArrayList<Double>> getSemanticVectors() {
		ArrayList<String> wordList = new ArrayList<>(this.vertices);
		W2WSimCollector w2w = new W2WSimCollector(wordList);
		HashMap<String, ArrayList<Double>> vectorMap = w2w.getWordVectors();
		return vectorMap;
	}

	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> getWeightedGraph() {
		// get the weighted graph
		for (String vertex : this.vertices) {
			if (!this.wgraph.containsVertex(vertex)) {
				this.wgraph.addVertex(vertex);
			}
		}
		// semantic vector map
		HashMap<String, ArrayList<Double>> vectorMap = getSemanticVectors();

		// now add the edges
		HashSet<DefaultEdge> edges = new HashSet<>(this.graph.edgeSet());
		for (DefaultEdge edge : edges) {
			String source = this.graph.getEdgeSource(edge);
			String target = this.graph.getEdgeTarget(edge);
			WordProximityDetector wpd = new WordProximityDetector(source,
					target, vectorMap);
			double edgeWeight = wpd.determineProximity();

			// adding nodes, edge, and edge weight
			if (!this.wgraph.containsVertex(source)) {
				this.wgraph.addVertex(source);
			}
			if (!this.wgraph.containsVertex(target)) {
				this.wgraph.addVertex(target);
			}

			// avoid the loops
			if (source.equals(target))
				continue;

			if (!this.wgraph.containsEdge(source, target)) {
				this.wgraph.addEdge(source, target);
				DefaultWeightedEdge wedge = this.wgraph.getEdge(source, target);
				this.wgraph.setEdgeWeight(wedge, edgeWeight);
			}
			if (!this.wgraph.containsEdge(target, source)) {
				this.wgraph.addEdge(target, source);
				DefaultWeightedEdge wedge = this.wgraph.getEdge(target, source);
				this.wgraph.setEdgeWeight(wedge, edgeWeight);
			}
		}

		return this.wgraph;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

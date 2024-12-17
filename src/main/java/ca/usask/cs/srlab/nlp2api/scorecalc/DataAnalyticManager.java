
/******
 * 
 * @author MasudRahman
 * Enriches a API class graph with weighted edges where the weight comes from the word embedddings.
 * 
 */

package ca.usask.cs.srlab.nlp2api.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import ca.usask.cs.srlab.nlp2api.data.analytics.WordProximityDetector;
import ca.usask.cs.srlab.nlp2api.w2vec.W2WSimCollector;

public class DataAnalyticManager {

	DirectedGraph<String, DefaultEdge> graph;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wGraph;
	HashSet<String> vertices;

	public DataAnalyticManager(DirectedGraph<String, DefaultEdge> graph) {
		this.graph = graph;
		this.vertices = new HashSet<>(this.graph.vertexSet());
		this.wGraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
	}

	protected HashMap<String, ArrayList<Double>> getSemanticVectors() {
		ArrayList<String> wordList = new ArrayList<>(this.vertices);
		W2WSimCollector w2w = new W2WSimCollector(wordList);
		HashMap<String, ArrayList<Double>> vectorMap = w2w.getWordVectors();
		return vectorMap;
	}

	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> getWeightedGraph() {
		for (String vertex : this.vertices) {
			if (!this.wGraph.containsVertex(vertex)) {
				this.wGraph.addVertex(vertex);
			}
		}

		HashMap<String, ArrayList<Double>> vectorMap = getSemanticVectors();

		HashSet<DefaultEdge> edges = new HashSet<>(this.graph.edgeSet());
		for (DefaultEdge edge : edges) {
			String source = this.graph.getEdgeSource(edge);
			String target = this.graph.getEdgeTarget(edge);
			WordProximityDetector wpd = new WordProximityDetector(source,
					target, vectorMap);
			double edgeWeight = wpd.determineProximity();

			if (!this.wGraph.containsVertex(source)) {
				this.wGraph.addVertex(source);
			}
			if (!this.wGraph.containsVertex(target)) {
				this.wGraph.addVertex(target);
			}
			
			if (source.equals(target))
				continue;

			if (!this.wGraph.containsEdge(source, target)) {
				this.wGraph.addEdge(source, target);
				DefaultWeightedEdge wedge = this.wGraph.getEdge(source, target);
				this.wGraph.setEdgeWeight(wedge, edgeWeight);
			}
			if (!this.wGraph.containsEdge(target, source)) {
				this.wGraph.addEdge(target, source);
				DefaultWeightedEdge wedge = this.wGraph.getEdge(target, source);
				this.wGraph.setEdgeWeight(wedge, edgeWeight);
			}
		}

		return this.wGraph;
	}
}

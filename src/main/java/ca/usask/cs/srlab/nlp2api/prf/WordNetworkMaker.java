
/**********
 * @author MasudRahman
 * This class creates the word network among the API classes from SO code segments based on co-occurrences.
 */

package ca.usask.cs.srlab.nlp2api.prf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class WordNetworkMaker {

	ArrayList<String> sentences;
	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wGraph;
	public DirectedGraph<String, DefaultEdge> graph;
	HashMap<String, QueryToken> tokenDB;
	final int WINDOW_SIZE = 2;

	public WordNetworkMaker(ArrayList<String> sentences) {
		this.sentences = sentences;
		this.wGraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.tokenDB = new HashMap<>();
	}

	public WordNetworkMaker(ArrayList<String> sentences,
			HashMap<String, ArrayList<String>> allTermMap) {
		this.sentences = sentences;
		this.wGraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.tokenDB = new HashMap<>();
	}

	public DirectedGraph<String, DefaultEdge> createWordNetwork() {
		for (String sentence : this.sentences) {
			String[] tokens = sentence.split("\\p{Punct}+|\\d+|\\s+");
			for (int index = 0; index < tokens.length; index++) {
				String previousToken = new String();
				String nextToken = new String();
				String currentToken = tokens[index];
				if (index > 0)
					previousToken = tokens[index - 1];

				if (index < tokens.length - 1)
					nextToken = tokens[index + 1];

				if (!graph.containsVertex(currentToken)) {
					graph.addVertex(currentToken);
				}
				if (!graph.containsVertex(previousToken)
						&& !previousToken.isEmpty()) {
					graph.addVertex(previousToken);
				}
				if (!graph.containsVertex(nextToken) && !nextToken.isEmpty()) {
					graph.addVertex(nextToken);
				}

				// adding edges to the graph
				if (!previousToken.isEmpty())
					if (!currentToken.equals(previousToken)) {
						if (!graph.containsEdge(currentToken, previousToken)) {
							graph.addEdge(currentToken, previousToken);
						}
					}

				if (!nextToken.isEmpty())
					if (!currentToken.equals(nextToken)) {
						if (!graph.containsEdge(currentToken, nextToken)) {
							graph.addEdge(currentToken, nextToken);
						}
					}
			}
		}
		return graph;
	}

	public HashMap<String, QueryToken> getTokenDictionary(boolean weighted) {
		HashSet<String> nodes = new HashSet<>();
		if (weighted)
			nodes.addAll(wGraph.vertexSet());
		else
			nodes.addAll(graph.vertexSet());
		for (String vertex : nodes) {
			QueryToken qToken = new QueryToken();
			qToken.token = vertex;
			this.tokenDB.put(vertex, qToken);
		}
		return this.tokenDB;
	}

	protected ArrayList<String> collectTopTokens(
			HashMap<String, QueryToken> sortedTokenDB) {
		ArrayList<String> topTokens = new ArrayList<>();
		int count = 0;
		for (String key : sortedTokenDB.keySet()) {
			topTokens.add(key);
			count++;
			if (count == 5)
				break;
		}
		return topTokens;
	}

	protected ArrayList<String> getImportantTokens(
			HashMap<String, QueryToken> sortedTokenDB, String queryTitle) {
		ArrayList<String> topTokens = new ArrayList<>();
		int count = 0;
		int intitle = 0;
		for (String key : sortedTokenDB.keySet()) {
			if (queryTitle.contains(key)) {
				topTokens.add(key);
				count++;
				intitle++;
			}
			if (count == 5)
				break;
		}
		int lateradded = 0;
		if (intitle < 5) {
			for (String token : sortedTokenDB.keySet()) {
				if (!queryTitle.contains(token)) {
					topTokens.add(token);
					lateradded++;
					if (lateradded + intitle == 5)
						break;
				}
			}
		}
		return topTokens;
	}

	public void showEdges(HashMap<String, QueryToken> tokenDB) {
		if (graph != null) {
			Set<DefaultEdge> edges = graph.edgeSet();
			ArrayList<DefaultEdge> edgeList = new ArrayList<>(edges);
			for (DefaultEdge edge : edgeList) {
				System.out.println(graph.getEdgeSource(edge) + "---"
						+ graph.getEdgeTarget(edge));
			}
		}
	}
}

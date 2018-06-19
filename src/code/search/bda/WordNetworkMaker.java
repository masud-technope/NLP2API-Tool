package code.search.bda;

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
	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
	public DirectedGraph<String, DefaultEdge> graph;
	HashMap<String, QueryToken> tokendb;
	final int WINDOW_SIZE = 2;

	public WordNetworkMaker(ArrayList<String> sentences) {
		// initializing both graphs
		this.sentences = sentences;
		this.wgraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.tokendb = new HashMap<>();
	}

	public WordNetworkMaker(ArrayList<String> sentences,
			HashMap<String, ArrayList<String>> alltermMap) {
		// initializing both graphs
		this.sentences = sentences;
		this.wgraph = new SimpleDirectedWeightedGraph<>(
				DefaultWeightedEdge.class);
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.tokendb = new HashMap<>();
		// adjacency initialization
		// temporary,just for now.
		// this.adjacent = new AdjacencyScoreProvider(sentences, alltermMap);
	}

	public DirectedGraph<String, DefaultEdge> createWordNetwork() {
		// developing the word network
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

				// now add the graph nodes
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

				// System.out.println(currentToken);

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
		// returning the created graph
		return graph;
	}

	public HashMap<String, QueryToken> getTokenDictionary(boolean weighted) {
		// populating token dictionary
		HashSet<String> nodes = new HashSet<>();
		if (weighted)
			nodes.addAll(wgraph.vertexSet());
		else
			nodes.addAll(graph.vertexSet());
		for (String vertex : nodes) {
			QueryToken qtoken = new QueryToken();
			qtoken.token = vertex;
			this.tokendb.put(vertex, qtoken);
		}
		return this.tokendb;
	}

	protected ArrayList<String> collectTopTokens(
			HashMap<String, QueryToken> sortedtokendb) {
		// collecting top tokens
		ArrayList<String> toptokens = new ArrayList<>();
		int count = 0;
		for (String key : sortedtokendb.keySet()) {
			toptokens.add(key);
			count++;
			if (count == 5)
				break;
		}
		return toptokens;
	}

	protected ArrayList<String> getImportantTokens(
			HashMap<String, QueryToken> sortedtokendb, String bugtitle) {
		ArrayList<String> toptokens = new ArrayList<>();
		int count = 0;
		int intitle = 0;
		for (String key : sortedtokendb.keySet()) {
			if (bugtitle.contains(key)) {
				toptokens.add(key);
				count++;
				intitle++;
			}
			if (count == 5)
				break;
		}
		int lateradded = 0;
		if (intitle < 5) {
			for (String token : sortedtokendb.keySet()) {
				if (!bugtitle.contains(token)) {
					toptokens.add(token);
					lateradded++;
					if (lateradded + intitle == 5)
						break;
				}
			}
		}
		return toptokens;
	}

	public void showEdges(HashMap<String, QueryToken> tokendb) {
		// showing the network edges
		if (graph != null) {
			Set<DefaultEdge> edges = graph.edgeSet();
			ArrayList<DefaultEdge> edgeList = new ArrayList<>(edges);
			for (DefaultEdge edge : edgeList) {
				System.out.println(graph.getEdgeSource(edge) + "---"
						+ graph.getEdgeTarget(edge));
			}
		}
	}

	public static void main(String[] args) {
		// main method
	}
}

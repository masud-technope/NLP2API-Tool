
/*******
 * @author MasudRahman
 * This class provides the actual queries from the PRF documents collected from SO.
 * 
 */


package ca.usask.cs.srlab.nlp2api.prf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ca.usask.cs.srlab.nlp2api.scorecalc.DataAnalyticManager;
import ca.usask.cs.srlab.nlp2api.scorecalc.IDFCalc;
import ca.usask.cs.srlab.tfidf.core.TFIDFCalculator;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import core.PageRankProvider;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ItemSorter;

public class PRQueryProvider {

	ArrayList<String> prfDocuments;
	String docTypeKey;

	public PRQueryProvider(ArrayList<String> prfDocuments, String docKey) {
		this.prfDocuments = prfDocuments;
		this.docTypeKey = docKey;
	}

	protected ArrayList<String> normalizeDocuments() {
		ArrayList<String> normDocs = new ArrayList<>();
		for (String content : this.prfDocuments) {
			ArrayList<String> normalized = new ArrayList<>();
			switch (this.docTypeKey) {
			case "t":
				String temp = new TextNormalizer(content).normalizeText();
				normDocs.add(temp);
				break;
			case "qt":
			case "at":
				normalized = cleanText(content);
				break;
			case "qc":
			case "ac":
			case "qac":
				normalized = cleanCode(content);
				break;
			}
			if (!normalized.isEmpty()) {
				normDocs.addAll(normalized);
			}
		}
		return normDocs;
	}

	protected ArrayList<String> cleanText(String content) {
		ArrayList<String> temp = new ArrayList<>();
		Document doc = Jsoup.parse(content);
		Elements elems = doc.select("p,li,div");
		for (Element elem : elems) {
			String normalized = new TextNormalizer(elem.text()).normalizeText();
			temp.add(normalized);
		}
		return temp;
	}

	protected ArrayList<String> cleanCode(String content) {
		ArrayList<String> temp = new ArrayList<>();
		String normalized = new TextNormalizer(content).normalizeSimpleCode();
		temp.add(normalized);
		return temp;
	}

	protected DirectedGraph<String, DefaultEdge> developTokenGraph(
			ArrayList<String> lines) {
		return new WordNetworkMaker(lines).createWordNetwork();
	}

	protected HashMap<String, Double> getTokenDB(
			DirectedGraph<String, DefaultEdge> graph) {
		HashMap<String, Double> tokenDB = new HashMap<>();
		HashSet<String> nodes = new HashSet<String>(graph.vertexSet());
		for (String node : nodes) {
			tokenDB.put(node, 0.0);
		}
		return tokenDB;
	}

	protected HashMap<String, Double> getTokenDBWeighted(
			SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wGraph) {
		HashMap<String, Double> tokenDB = new HashMap<>();
		HashSet<String> nodes = new HashSet<String>(wGraph.vertexSet());
		for (String node : nodes) {
			tokenDB.put(node, 0.0);
		}
		return tokenDB;
	}

	public ArrayList<String> getPRFQueryTerms() {
		ArrayList<String> normalizedPRF = normalizeDocuments();
		DirectedGraph<String, DefaultEdge> graph = developTokenGraph(normalizedPRF);
		HashMap<String, Double> tokendb = getTokenDB(graph);
		PageRankProvider prProvider = new PageRankProvider(graph, tokendb);
		tokendb = prProvider.calculatePageRank();
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(tokendb);
		ArrayList<String> expansions = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			expansions.add(entry.getKey());
			if (expansions.size() == StaticData.PRF_QR_SIZE) {
				break;
			}
		}
		return expansions;
	}

	@Deprecated
	public ArrayList<String> getPRFQueryTermsWeighted() {
		ArrayList<String> normalizedPRF = normalizeDocuments();
		DirectedGraph<String, DefaultEdge> graph = developTokenGraph(normalizedPRF);
		SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph = new DataAnalyticManager(
				graph).getWeightedGraph();
		HashMap<String, Double> tokendb = getTokenDBWeighted(wgraph);
		PageRankProvider prProvider = new PageRankProvider(wgraph, tokendb);
		tokendb = prProvider.calculatePageRankWeighted();
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(tokendb);
		ArrayList<String> expansions = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			expansions.add(entry.getKey());
			if (expansions.size() == StaticData.PRF_QR_SIZE) {
				break;
			}
		}
		return expansions;
	}

	protected ArrayList<String> makeItLowerCase(ArrayList<String> items) {
		ArrayList<String> temp = new ArrayList<>();
		for (String token : items) {
			if (!token.trim().isEmpty()) {
				temp.add(token.toLowerCase());
			}
		}
		return temp;
	}

	public ArrayList<String> getPRFQueryTermsTFIDF(String indexFolder) {
		ArrayList<String> normalizedPRF = normalizeDocuments();
		HashMap<Integer, String> docMap = new HashMap<>();
		for (int i = 0; i < normalizedPRF.size(); i++) {
			docMap.put(i, normalizedPRF.get(i));
		}
		TFIDFCalculator calc = new TFIDFCalculator(docMap);
		calc.runCalc();
		HashMap<String, Integer> corpusTF = calc.getCollectionTF();

		ArrayList<String> keys = makeItLowerCase(new ArrayList<String>(
				corpusTF.keySet()));

		IDFCalc iCalc = new IDFCalc(indexFolder, keys);
		HashMap<String, Double> idfMap = iCalc.calculateIDFOnly();
		HashMap<String, Double> tfidfMap = new HashMap<>();
		for (String key : corpusTF.keySet()) {
			String lcKey = key.toLowerCase();
			int tf = corpusTF.get(key);
			if (idfMap.containsKey(lcKey)) {
				// log transformation
				double logTF = 1 + Math.log(tf);
				double score = logTF * idfMap.get(lcKey);
				tfidfMap.put(key, score);
			}
		}
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(tfidfMap);
		ArrayList<String> expansions = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			expansions.add(entry.getKey());
			if (expansions.size() == StaticData.PRF_QR_SIZE) {
				break;
			}
		}
		return expansions;
	}

	public ArrayList<String> getPRFQueryTermsRocchio() {
		ArrayList<String> normalizedPRF = normalizeDocuments();
		HashMap<String, Double> rocchioMap = new HashMap<>();
		HashMap<Integer, String> docMap = new HashMap<>();
		for (int i = 0; i < normalizedPRF.size(); i++) {
			docMap.put(i, normalizedPRF.get(i));
		}
		TFIDFCalculator calc = new TFIDFCalculator(docMap);

		calc.runCalc();
		HashMap<Integer, HashMap<String, Double>> docTFIDF = calc
				.calculateTFIDF();
		for (int docID : docTFIDF.keySet()) {
			HashMap<String, Double> tfidf = docTFIDF.get(docID);
			for (String token : tfidf.keySet()) {
				double score = tfidf.get(token);

				if (rocchioMap.containsKey(token)) {
					double updated = rocchioMap.get(token) + score;
					rocchioMap.put(token, updated);
				} else {
					rocchioMap.put(token, score);
				}
			}
		}
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(rocchioMap);
		ArrayList<String> expansions = new ArrayList<>();
		for (Map.Entry<String, Double> entry : sorted) {
			expansions.add(entry.getKey());
			if (expansions.size() == StaticData.PRF_QR_SIZE) {
				break;
			}
		}
		return expansions;
	}
}

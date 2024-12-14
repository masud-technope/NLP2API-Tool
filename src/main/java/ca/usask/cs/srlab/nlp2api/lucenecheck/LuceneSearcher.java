
/******
 * 
 * 
 * @author MasudRahman
 * The code search engine, that returns relevant code from the corpus against a query.
 * 
 */

package ca.usask.cs.srlab.nlp2api.lucenecheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.config.StaticData;

public class LuceneSearcher {

	int bugID;
	String repository;
	String indexFolder;
	String field = "contents";
	String queries = null;
	int repeat = 0;
	boolean raw = true;
	String queryString = null;
	int hitsPerPage = 10;
	String searchQuery;
	int MAX_RESULTS = 10000;
	ArrayList<String> results;
	ArrayList<String> goldset;
	int caseNo;

	IndexReader reader = null;
	IndexSearcher searcher = null;
	Analyzer analyzer = null;

	public double precision = 0;
	public double recall = 0;
	public double precatk = 0;

	public double maxScore = 0;
	public double minScore = 100000;

	@Deprecated
	public LuceneSearcher(String searchQuery, String repository) {
		// initialization
		// this.bugID = bugID;
		this.repository = repository;
		this.indexFolder = StaticData.EXP_HOME + "/lucene/index/" + repository;
		this.searchQuery = searchQuery;
		this.results = new ArrayList<>();
		// this.goldset = new ArrayList<>();
	}

	public LuceneSearcher(int caseNo, String searchQuery, String indexFolder) {
		this.searchQuery = searchQuery;
		this.indexFolder = indexFolder;
		this.results = new ArrayList<>();
		this.caseNo = caseNo;
	}

	public LuceneSearcher(int caseNo, String searchQuery) {
		this.searchQuery = searchQuery;
		this.indexFolder = StaticData.EXP_HOME + "/"
				+ StaticData.CODE_EXAMPLE_INDEX;
		this.results = new ArrayList<>();
		this.caseNo = caseNo;
	}

	public ArrayList<String> performVSMSearch(int TOPK) {
		// performing Lucene search
		try {
			if (reader == null)
				reader = DirectoryReader.open(FSDirectory.open(new File(
						indexFolder).toPath()));
			if (searcher == null)
				searcher = new IndexSearcher(reader);
			if (analyzer == null)
				analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);

			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, TOPK);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < TOPK; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					// int docID=item.doc;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					// System.out.println("Doc ID:"+docID);
					// Terms terms=reader.getTermVector(docID, "contents");
					// System.out.println(doc.get("contents"));

					String APIName = new File(fileURL).getName().split("\\.")[0];
					this.results.add(APIName);
				}
				// showing gold set
				// showGoldSet();
				// getGoldSet();
			}
		} catch (Exception e) {
			// handle the exception
		}
		return this.results;
	}

	public ArrayList<String> performVSMSearch() {
		// performing Lucene search
		try {
			if (reader == null)
				reader = DirectoryReader.open(FSDirectory.open(new File(
						indexFolder).toPath()));
			if (searcher == null)
				searcher = new IndexSearcher(reader);
			if (analyzer == null)
				analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);

			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, MAX_RESULTS);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < hits.length; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					// int docID=item.doc;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					// System.out.println("Doc ID:"+docID);
					// Terms terms=reader.getTermVector(docID, "contents");
					// System.out.println(doc.get("contents"));

					String APIName = new File(fileURL).getName().split("\\.")[0];
					this.results.add(APIName);
				}
				// showing gold set
				// showGoldSet();
				// getGoldSet();
			}
		} catch (Exception e) {
			// handle the exception
		}
		return this.results;
	}

	public HashMap<String, Double> performVSMSearchWithScore(int TOPK) {
		HashMap<String, Double> resultMap = new HashMap<>();
		try {
			if (reader == null)
				reader = DirectoryReader.open(FSDirectory.open(new File(
						indexFolder).toPath()));
			if (searcher == null)
				searcher = new IndexSearcher(reader);
			if (analyzer == null)
				analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);

			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, TOPK);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < hits.length; i++) {
					ScoreDoc item = hits[i];
					Document doc = searcher.doc(item.doc);
					double score = item.score;
					String fileURL = doc.get("path");
					fileURL = fileURL.replace('\\', '/');
					String resultFileName = new File(fileURL).getName().split(
							"\\.")[0].trim();
					resultMap.put(resultFileName, score);

					// determine max-min scores
					if (score > maxScore) {
						maxScore = score;
					}
					if (score < minScore) {
						minScore = score;
					}
				}
			}
		} catch (Exception e) {
			// handle the exception
		}
		return resultMap;
	}

	public int getFirstGoldRank(int key, int TOPK) {
		// getting the first gold rank
		ArrayList<String> results = performVSMSearch(TOPK);
		int rank = -1;
		for (String fileURL : results) {
			// rank++;
			int fileID = Integer.parseInt(fileURL.trim());
			if (fileID == key) {
				// rank++;
				rank = results.indexOf(fileURL);
				return rank;
			}
		}
		return rank;
	}

	public int getFirstGoldRank(int key) {
		// getting the first gold rank
		ArrayList<String> results = performVSMSearch();
		int rank = -1;
		for (String fileURL : results) {
			// rank++;
			int fileID = Integer.parseInt(fileURL.trim());
			if (fileID == key) {
				// rank++;
				rank = results.indexOf(fileURL);
				return rank;
			}
		}
		return rank;
	}

	public static void main(String[] args) {
		// int bugID = 41186;
		String repository = "chcomment";
		String searchQuery = "How do I move a file in JDK 7";
		String indexFolder = StaticData.EXP_HOME
				+ "/dataset/qeck-corpus-ext-index";
		LuceneSearcher searcher = new LuceneSearcher(0, searchQuery,
				indexFolder);
		int TOPK = 10;
		ArrayList<String> results = searcher.performVSMSearch(TOPK);
		System.out.println(results);
		/*
		 * for(String postID: results){
		 * System.out.println(ContentLoader.loadFileContent
		 * (StaticData.EXP_HOME+"/dataset/question/"+postID+".txt")); }
		 */
	}
}

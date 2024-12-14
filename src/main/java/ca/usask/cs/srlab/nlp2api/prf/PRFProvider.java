/*******
 * 
 * @author MasudRahman
 * Pseudo-relevance provider for NLP2API
 */


package ca.usask.cs.srlab.nlp2api.prf;

import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.lucenecheck.LuceneSearcher;

public class PRFProvider {

	String initialQuery;
	String indexFolder;
	int TOPK = 10;

	public PRFProvider(String initialQuery, String indexFolder, int TOPK) {
		this.initialQuery = initialQuery;
		this.indexFolder = indexFolder;
		this.TOPK = TOPK;
	}

	public PRFProvider(String initialQuery, int TOPK) {
		this.initialQuery = new TextNormalizer(initialQuery).normalizeText();
		this.TOPK = TOPK;
	}

	protected ArrayList<String> getPRF() {
		LuceneSearcher lsearcher = new LuceneSearcher(0, initialQuery,
				indexFolder);
		ArrayList<String> results = lsearcher.performVSMSearch(TOPK);
		return results;
	}

	protected ArrayList<String> getPRF(String indexFolder) {
		LuceneSearcher lsearcher = new LuceneSearcher(0, initialQuery,
				indexFolder);
		ArrayList<String> results = lsearcher.performVSMSearch(TOPK);
		return results;
	}

	protected HashMap<String, ArrayList<String>> getPRFExtendedV2() {
		ArrayList<String> prfIDs = new ArrayList<>();
		HashMap<String, ArrayList<String>> prfMap = new HashMap<>();

		String overallIndex = StaticData.EXP_HOME + "/dataset/"
				+ StaticData.SO_QA_INDEX;
		prfIDs = getPRF(overallIndex);

		ArrayList<String> questionCodes = getQuestionCodes(prfIDs);
		prfMap.put("qc", questionCodes);

		ArrayList<String> answerCodes = getAnswerCodes(prfIDs);
		prfMap.put("ac", answerCodes);

		// adding codes from Q and A
		if (StaticData.COMBINE_QA_CODES) {
			ArrayList<String> allCodes = new ArrayList<>();
			allCodes.addAll(questionCodes);
			allCodes.addAll(answerCodes);
			prfMap.put("qac", allCodes);
		}

		// added for RQ3
		// questionCodes.addAll(answerCodes);
		// prfMap.put("qac", questionCodes);

		return prfMap;
	}

	@Deprecated
	protected HashMap<String, ArrayList<String>> getPRFExtendedV1() {
		ArrayList<String> prfIDs = getPRF();
		HashMap<String, ArrayList<String>> prfMap = new HashMap<>();
		ArrayList<String> titles = getTitles(prfIDs);
		prfMap.put("t", titles);
		ArrayList<String> questionTexts = getQuestionTexts(prfIDs);
		prfMap.put("qt", questionTexts);
		ArrayList<String> questionCodes = getQuestionCodes(prfIDs);
		prfMap.put("qc", questionCodes);
		ArrayList<String> answerTexts = getAnswerTexts(prfIDs);
		prfMap.put("at", answerTexts);
		ArrayList<String> answerCodes = getAnswerCodes(prfIDs);
		prfMap.put("ac", answerCodes);
		return prfMap;
	}

	@Deprecated
	protected ArrayList<String> getTitles(ArrayList<String> results) {
		ArrayList<String> temp = new ArrayList<>();
		for (String fileName : results) {
			String titleFile = StaticData.EXP_HOME + "/dataset/question-title/"
					+ fileName + ".txt";
			String title = ContentLoader.loadFileContent(titleFile);
			temp.add(title);
		}
		return temp;
	}

	@Deprecated
	protected ArrayList<String> getQuestionTexts(ArrayList<String> results) {
		ArrayList<String> temp = new ArrayList<>();
		for (String fileName : results) {
			String questionFile = StaticData.EXP_HOME + "/dataset/question/"
					+ fileName + ".txt";
			String content = ContentLoader.loadFileContent(questionFile);
			Document doc = Jsoup.parse(content);
			Elements codes = doc.select("pre,code");
			codes.remove();
			temp.add(doc.html());
		}
		return temp;
	}

	protected ArrayList<String> getQuestionCodes(ArrayList<String> results) {
		ArrayList<String> temp = new ArrayList<>();
		for (String fileName : results) {
			String questionFile = StaticData.EXP_HOME + "/dataset/"
					+ StaticData.SO_QUESTION_DIR + "/" + fileName + ".txt";
			String content = ContentLoader.loadFileContent(questionFile);
			Document doc = Jsoup.parse(content);
			Elements codes = doc.select("code");
			temp.add(codes.html());
		}
		return temp;
	}

	@Deprecated
	protected ArrayList<String> getAnswerTexts(ArrayList<String> results) {
		ArrayList<String> temp = new ArrayList<>();
		for (String fileName : results) {
			String questionFile = StaticData.EXP_HOME + "/dataset/answer/"
					+ fileName + ".txt";
			String content = ContentLoader.loadFileContent(questionFile);
			Document doc = Jsoup.parse(content);
			Elements codes = doc.select("pre,code");
			codes.remove();
			temp.add(doc.html());
		}
		return temp;
	}

	protected ArrayList<String> getAnswerCodes(ArrayList<String> results) {
		ArrayList<String> temp = new ArrayList<>();
		for (String fileName : results) {
			String questionFile = StaticData.EXP_HOME + "/dataset/"
					+ StaticData.SO_ANSWER_DIR + "/" + fileName + ".txt";
			String content = ContentLoader.loadFileContent(questionFile);
			Document doc = Jsoup.parse(content);
			Elements codes = doc.select("code");
			temp.add(codes.html());
		}
		return temp;
	}

	@Deprecated
	protected static void showPRFTitles(ArrayList<String> results) {
		// showing the PRF titles
		for (String fileName : results) {
			String titleFile = StaticData.EXP_HOME + "/dataset/question-title/"
					+ fileName + ".txt";
			System.out.println(ContentLoader.loadFileContent(titleFile));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query = "How do I create port scanner program?";
		String normQuery = new TextNormalizer(query).normalizeText();
		String indexFolder = StaticData.EXP_HOME
				+ "/dataset/question-title-index";
		new PRFProvider(normQuery, indexFolder, 10).getPRF();
	}
}

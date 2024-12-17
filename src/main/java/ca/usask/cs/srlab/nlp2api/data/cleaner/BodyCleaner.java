
/***********
 * SO corpus utility class
 */

package ca.usask.cs.srlab.nlp2api.data.cleaner;

import java.io.File;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;

public class BodyCleaner {

	String orgFolder;
	String normFolderText;
	String normFolderCode;

	public BodyCleaner() {
		this.orgFolder = StaticData.EXP_HOME + "/dataset/answer";
		this.normFolderCode = StaticData.EXP_HOME + "/dataset/answer-norm-code";
		this.normFolderText = StaticData.EXP_HOME + "/dataset/answer-norm-text";
	}

	protected void saveBodyText(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	protected void saveBodyCode(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	protected String extractCode(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		return new TextNormalizer(codeText).normalizeSimpleCodeDiscardSmall();
	}

	protected String extractText(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		doc.select("code,pre").remove();
		return doc.text();
	}

	protected void cleanTheBody() {
		File[] files = new File(this.orgFolder).listFiles();
        assert files != null;
        for (File f : files) {
			ArrayList<String> lines = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			String postHTML = MiscUtility.list2Str(lines);
			String normText = extractText(postHTML);
			String outTextFile = this.normFolderText + "/" + f.getName();
			saveBodyText(outTextFile, normText);
			String normCode = extractCode(postHTML);
			String outCodeFile = this.normFolderCode + "/" + f.getName();
			saveBodyCode(outCodeFile, normCode);
		}
	}
}

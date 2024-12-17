
/***********
 * SO corpus utility class
 */

package ca.usask.cs.srlab.nlp2api.data.cleaner;

import java.io.File;
import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.text.normalizer.TextNormalizer;
import ca.usask.cs.srlab.nlp2api.utility.ContentLoader;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;

public class TitleCleaner {

	String titleFolder;
	String titleFolderNorm;

	public TitleCleaner() {
		this.titleFolder = StaticData.EXP_HOME + "/dataset/question";
		this.titleFolderNorm = StaticData.EXP_HOME + "/dataset/question-norm";
	}

	protected void cleanTheTitle() {
		File[] files = new File(this.titleFolder).listFiles();
		for (File f : files) {
			String content = ContentLoader.loadFileContent(f.getAbsolutePath());
			String normalized = new TextNormalizer(content).normalizeText();
			String outFile = this.titleFolderNorm + "/" + f.getName();
			ContentWriter.writeContent(outFile, normalized);
		}
	}
}

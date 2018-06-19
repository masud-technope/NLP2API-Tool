package se.data.manager;

import java.io.File;
import java.util.ArrayList;
import utility.ContentLoader;
import utility.ContentWriter;

public class NLSetMaker {

	String se;
	String nlFolder;

	public NLSetMaker(String se, String nlFolder) {
		this.se = se;
		this.nlFolder = nlFolder;
	}

	protected void restructureNLResults() {
		File[] files = new File(nlFolder).listFiles();

		for (File f : files) {
			ArrayList<String> lines = ContentLoader.getAllLinesOptList(f
					.getAbsolutePath());
			int index = 0;
			ArrayList<String> tempLines = new ArrayList<>();
			for (String line : lines) {
				index++;
				tempLines.add(index + "\t" + line);
			}
			String outFile=f.getAbsolutePath();
			ContentWriter.writeContent(outFile, tempLines);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String nlFolder="C:/My MSc/ThesisWorks/BigData_Code_Search/CodeSearchBDA/Replication-package/search-engine-resx/github/nl-result";
		new NLSetMaker("so", nlFolder).restructureNLResults();
	}
}

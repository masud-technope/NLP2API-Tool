
/***********
 * SO corpus utility class
 */

package ca.usask.cs.srlab.nlp2api.data.cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.usask.cs.srlab.nlp2api.config.StaticData;
import ca.usask.cs.srlab.nlp2api.utility.ContentWriter;
import ca.usask.cs.srlab.nlp2api.utility.MiscUtility;

public class CodeCleaner {

	String orgFile;
	String cleanedFile;

	public CodeCleaner() {
		this.orgFile = StaticData.EXP_HOME + "/question-answer/"
				+ "java-question-answer-code.txt";
		this.cleanedFile = StaticData.EXP_HOME+ "/question-answer/"
				+ "java-question-answer-code-cleaned-class.txt";
	}

	protected void saveBodyText(ArrayList<String> items) {
		ContentWriter.appendContent(this.cleanedFile, items);
	}

	protected String extractCamelCaseOnly(String line) {
		String ccRegex = "([A-Z][a-z0-9]+)+";
		ArrayList<String> ccItems = new ArrayList<>();
		Pattern p = Pattern.compile(ccRegex);
		Matcher m = p.matcher(line);
		while (m.find()) {
			String ccItem = line.substring(m.start(), m.end());
			ccItems.add(ccItem);
		}
		return MiscUtility.list2Str(ccItems);
	}

	protected void cleanTheBody() {
		ArrayList<String> itemTextList = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(new File(orgFile));
			while (scanner.hasNext()) { 
				String line = scanner.nextLine().trim();

				line = extractCamelCaseOnly(line);

				if (!line.isEmpty()) {
					itemTextList.add(line);
				}

				if (itemTextList.size() == 10000) {
					saveBodyText(itemTextList);
					itemTextList.clear();
				}
			}
			scanner.close();
			if (!itemTextList.isEmpty()) {
				saveBodyText(itemTextList);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

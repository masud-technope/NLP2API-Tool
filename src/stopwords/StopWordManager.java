
/*****
 * 
 * @author MasudRahman
 * Stop word filtration class
 * 
 */

package stopwords;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import utility.ContentLoader;
import utility.MiscUtility;

public class StopWordManager {

	public ArrayList<String> stopList;
	String stopDir = "./data/stop-words-english-total.txt";
	String javaKeywordFile = "./data/java-keywords.txt";
	String javaLangKeywordFile = "./data/java-lang-keywords.txt";

	public StopWordManager() {
		// initialize the Hash set
		this.stopList = new ArrayList<>();
		this.loadStopWords();
	}

	public StopWordManager(boolean code) {
		this.stopList = new ArrayList<>();
		this.loadStopNkeywords();
	}

	protected void loadStopWords() {
		// loading stop words
		try {
			Scanner scanner = new Scanner(new File(this.stopDir));
			while (scanner.hasNext()) {
				String word = scanner.nextLine().trim();
				this.stopList.add(word);
			}
			scanner.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void loadStopNkeywords() {
		try {
			Scanner scanner = new Scanner(new File(this.stopDir));
			while (scanner.hasNext()) {
				String word = scanner.nextLine().trim();
				this.stopList.add(word);
			}
			scanner.close();

			// now add the programming keywords
			ArrayList<String> keywords = ContentLoader
					.getAllLinesOptList(javaKeywordFile);
			this.stopList.addAll(keywords);

			ArrayList<String> langkeywords = ContentLoader
					.getAllLinesOptList(javaLangKeywordFile);
			this.stopList.addAll(langkeywords);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String removeSpecialChars(String sentence) {
		// removing special characters
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			refined += str.trim() + " ";
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public String removeTinyTerms(String sentence) {
		// removing special characters
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			if (str.length() >= 3)
				refined += str.trim() + " ";
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public boolean isAStopWord(String token) {
		if (stopList.contains(token))
			return true;
		if (stopList.contains(token.toLowerCase()))
			return true;
		if (stopList.contains(token.toUpperCase()))
			return true;
		return false;
	}

	public String getRefinedSentence(String sentence) {
		// get refined sentence
		String refined = new String();
		String temp = removeSpecialChars(sentence);
		ArrayList<String> tokens = MiscUtility.str2List(temp);
		for (String token : tokens) {
			if (isAStopWord(token)) {
				// do not add
			} else {
				refined += token + "\t";
			}
		}
		return refined.trim();
	}

	public ArrayList<String> getRefinedList(String[] words) {
		ArrayList<String> refined = new ArrayList<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())
					|| !this.stopList.contains(word)
					|| !this.stopList.contains(word.toUpperCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public ArrayList<String> getRefinedList(ArrayList<String> words) {
		ArrayList<String> refined = new ArrayList<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())
					|| !this.stopList.contains(word)
					|| !this.stopList.contains(word.toUpperCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public HashSet<String> getRefinedList(HashSet<String> words) {
		HashSet<String> refined = new HashSet<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StopWordManager manager = new StopWordManager(true);
		String str = "statement protected java Boolean lang expression Quick Invert operator omits AdvancedQuickAssistProcessor";
		// String modified=manager.removeSpecialChars(sentence);
		System.out.println(manager.getRefinedSentence(str));
	}
}

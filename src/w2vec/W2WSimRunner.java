package w2vec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import utility.ContentLoader;
import utility.ContentWriter;
import config.StaticData;

public class W2WSimRunner {

	ArrayList<String> words;
	String fastTextDir;

	public W2WSimRunner(ArrayList<String> words) {
		this.words = words;
		this.fastTextDir = StaticData.FASTTEXT_DIR;
	}

	protected boolean addWords2InputFile() {
		String outFile = this.fastTextDir + "/input/wordpair.txt";
		return ContentWriter.writeContent(outFile, words);
	}

	protected boolean execBatchCommand() {
		// String batchFile = "C:/tmp/w2vecRunner.bat";
		String batchFile = StaticData.BAT_FILE_PATH2;
		String command = "cmd /c " + batchFile;
		try {
			// System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return false;
		}
		return true;
	}

	public ArrayList<String> collectWordVector() {
		ArrayList<String> temp = new ArrayList<>();
		if (this.addWords2InputFile()) {
			if (this.execBatchCommand()) {
				String outputFile = StaticData.FASTTEXT_DIR
						+ "/output/wordvec.txt";
				temp = ContentLoader.getAllLinesOptList(outputFile);
			}
		}
		return temp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String candidate = "custom PeerGroup";
		ArrayList<String> words = new ArrayList<>(Arrays.asList(candidate
				.split("\\s+")));
		System.out.println(new W2WSimRunner(words).collectWordVector());
	}
}

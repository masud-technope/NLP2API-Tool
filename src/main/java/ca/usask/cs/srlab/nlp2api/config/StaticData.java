
/******
 * 
 * @author MasudRahman
 * Stores all static values and configurations used by the tool.
 */

package ca.usask.cs.srlab.nlp2api.config;

public class StaticData {
	//public static String EXP_HOME = System.getProperty("user.dir");//
	public static String EXP_HOME ="C:\\MyWorks\\MyResearch\\CodeSearch\\NLP2API\\NLP2API-Replication-Package";
	public static String FASTTEXT_DIR = EXP_HOME + "/fastText";
	public static String BAT_FILE_PATH = EXP_HOME + "/scripts/w2vecRunner.bat";
	public static String BAT_FILE_PATH2 ="fastText\\w2wSim.bat";
	
	//public static String PYTHON_HOME = "C:/Users/MasudRahman/Anaconda3"; 
	public static String PYTHON_HOME = "C:/Users/MasudRahman/Anaconda3/envs/python-module";
	
	public static int PRF_SIZE = 35;// 30;
	public static int PRF_QR_SIZE =16;

	public static double Borda_Weight = 1;// 0.65;
	public static double w2vec_Weight = 1;// 0.35;

	// IMPORTANT DIRECTORIES/FILES
	public static String ORACLE_FILE = "oracle-310.txt";
	public static int TOTAL_CASE_COUNT = 310;
	public static boolean COMBINE_PR_TFIDF = true;
	public static boolean COMBINE_QA_CODES = false;
	public static boolean PR_ONLY = false;
	public static String CODE_EXAMPLE_INDEX = "code-ext-index";
	public static String SO_QA_INDEX = "qa-corpus-ext-index";
	public static String SO_QA_DIR = "qeck-corpus-ext";
	public static String SO_QUESTION_DIR = "question-ext";
	public static String SO_QUESTION_CODE_INDEX = "question-norm-code-ext-index";
	public static String SO_ANSWER_DIR = "answer-ext";
	public static String SO_ANSWER_CODE_INDEX = "question-norm-code-ext-index";
}

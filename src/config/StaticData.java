package config;

public class StaticData {
	public static String EXP_HOME = System.getProperty("user.dir");// "C:/My MSc/ThesisWorks/BigData_Code_Search/CodeSearchBDA/Replication-package";
	public static String FASTTEXT_DIR = EXP_HOME + "/fastText";
	public static String BAT_FILE_PATH = EXP_HOME + "/scripts/w2vecRunner.bat";
	public static String BAT_FILE_PATH2 ="fastText\\w2wSim.bat";
	
	public static String Database_name = "stackoverflow2014p7";
	
	public static String connectionString = "jdbc:sqlserver://localhost:1433;databaseName="
			+ Database_name + ";integratedSecurity=true";
	public static String Driver_name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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

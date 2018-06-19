package stackdata.coll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import config.StaticData;
import utility.ContentWriter;

public class QuestionTitleAnswerCollector {

	String databaseName;
	String tagName;
	String outputFile;

	public QuestionTitleAnswerCollector(String databaseName, String tagName) {
		this.databaseName = databaseName;
		this.tagName = tagName;
		this.outputFile = StaticData.EXP_HOME + "/dataset";
		// dynamically updating the database
		StaticData.Database_name = databaseName;
	}

	protected void saveItems(ArrayList<String> items) {
		ContentWriter.appendContent(outputFile, items);
	}

	protected void collectQuestionTitles() {
		Connection conn = ConnectionManager.getConnection();
		if (conn != null) {
			String titleSelect = "SELECT Title, Body from Posts where PostTypeId=1 and Tags like '%<"
					+ tagName + ">%'";
			ArrayList<String> lines = new ArrayList<>();
			try {
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery(titleSelect);
				while (results.next()) {
					String title = results.getString("Title");
					String body = results.getString("Body");
					body = body.replace("\n", "\t").trim();
					lines.add(title + "\t" + body);
					if (lines.size() == 10000) {
						saveItems(lines);
						lines.clear();
					}
				}
				if (!lines.isEmpty()) {
					saveItems(lines);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void collectQAThread() {
		Connection conn = ConnectionManager.getConnection();
		if (conn != null) {
			String titleSelect = "select p1.title,p1.Body as b1, p2.Body as b2 from Posts as p1, Posts as p2 where "
					+ " p1.PostTypeId=1 and p1.Tags like '%<"
					+ tagName
					+ ">%'"
					+ " and p2.PostTypeId=2 and "
					+ " p1.AcceptedAnswerId=p2.Id";
			ArrayList<String> lines = new ArrayList<>();
			try {
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery(titleSelect);
				while (results.next()) {
					String title = results.getString("title");
					String qbody = results.getString("b1");
					String abody = results.getString("b2");
					qbody = qbody.replaceAll("[\n\r]", "\t").trim();
					abody = abody.replaceAll("[\n\r]", "\t").trim();
					lines.add(title + "\t" + qbody + "\t" + abody);
					if (lines.size() == 10000) {
						saveItems(lines);
						lines.clear();
					}
				}
				if (!lines.isEmpty()) {
					saveItems(lines);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	

	protected void collectAnswerBody() {
		Connection conn = ConnectionManager.getConnection();
		if (conn != null) {
			String titleSelect = "SELECT Body from Posts where PostTypeId=2 and Id in (SELECT AcceptedAnswerId  from Posts"
					+ " where PostTypeId=1 and Tags like '%<"
					+ tagName
					+ ">%')";
			ArrayList<String> lines = new ArrayList<>();
			try {
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery(titleSelect);
				while (results.next()) {
					String body = results.getString("Body");
					body = body.replace("\n", "\t").trim();
					lines.add(body);
					if (lines.size() == 10000) {
						saveItems(lines);
						lines.clear();
					}
				}
				if (!lines.isEmpty()) {
					saveItems(lines);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void saveSOItem(int questionID, String content, String key) {
		// saving the item
		String outputFile = this.outputFile;
		switch (key) {
		case "title":
			outputFile += "/question-title/" + questionID + ".txt";
			break;
		case "question":
			outputFile += "/question/" + questionID + ".txt";
			break;
		case "answer":
			outputFile += "/answer/" + questionID + ".txt";
			break;
		default:
			break;
		}
		ContentWriter.writeContent(outputFile, content);
	}

	protected void collectTQATriplets() {
		// collect question answer and title
		Connection conn = ConnectionManager.getConnection();
		if (conn != null) {
			String titleSelect = "select p1.Id, p1.title, p1.body as b1, p2.Body as b2 from Posts as p1, Posts as p2 where "
					+ "p1.PostTypeId=1 and p1.Tags like '%<"+tagName+">%' "
					+ "and p2.PostTypeId=2 and "
					+ "p1.AcceptedAnswerId=p2.Id "
					+ "and p2.Body like '%<code>%'";
			try {
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery(titleSelect);
				while (results.next()) {
					int questionID = results.getInt("Id");
					String title = results.getString("title");
					String qbody = results.getString("b1");
					String abody = results.getString("b2");
					
					// qbody = qbody.replaceAll("[\n\r]", "\t").trim();
					// abody = abody.replaceAll("[\n\r]", "\t").trim();
					
					saveSOItem(questionID, title, "title");
					saveSOItem(questionID, qbody, "question");
					saveSOItem(questionID, abody, "answer");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String databaseName = "stackoverflow2014p7";
		StaticData.Database_name=databaseName;
		String tagName = "java";
		new QuestionTitleAnswerCollector(databaseName, tagName).collectTQATriplets();
		System.out.println("Done!");
	}
}

package utility;

import java.util.ArrayList;

public class SelectedTasks {

	public static ArrayList<Integer> loadSelectedTasks() {
		String taskIDFile = "./selected/selectedtask.txt";
		return ContentLoader.getAllLinesInt(taskIDFile);
	}
}

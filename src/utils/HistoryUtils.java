package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.ide.util.PropertiesComponent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import Models.Command;

/**
 * Created by vfarafonov on 08.09.2015.
 */
public class HistoryUtils {
	private static final int HISTORY_COUNT = 10;
	private static final String HISTORY_JSON = "HISTORY_JSON";

	/**
	 * Saves command to history list
	 */
	public static void saveCommand(@NotNull Command command) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
		String historyJson = propertiesComponent.getValue(HISTORY_JSON);
		List<Command> commandList;
		Gson gson = new Gson();
		if (historyJson != null) {
			commandList = gson.fromJson(historyJson, new TypeToken<List<Command>>() {
			}.getType());
			while (commandList.size() >= HISTORY_COUNT) {
				commandList.remove(commandList.size() - 1);
			}
		} else {
			commandList = new ArrayList<Command>();
		}
		commandList.add(0, command);
		historyJson = gson.toJson(commandList);
		propertiesComponent.setValue(HISTORY_JSON, historyJson);
	}

	/**
	 * Picks commands from history
	 */
	@SuppressWarnings("unchecked")
	public static List<Command> getCommandsFromHistory() {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
		String historyJson = propertiesComponent.getValue(HISTORY_JSON);
		List<Command> commandList = new ArrayList<Command>();
		Gson gson = new Gson();
		if (historyJson != null) {
			commandList.addAll((List<Command>) gson.fromJson(historyJson, new TypeToken<List<Command>>() {
			}.getType()));
		}
		return commandList;
	}
}

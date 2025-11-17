package vv0ta3fa9.plugin.kAdminMode.utils;

import vv0ta3fa9.plugin.kAdminMode.kAdminMode;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.Colorizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
    private final kAdminMode plugin;
    private File logFile;
    public Colorizer COLORIZER;

    public LogManager(kAdminMode plugin) {
        this.plugin = plugin;
        createLogFile();
    }

    private void createLogFile() {
        logFile = new File(plugin.getDataFolder(), "log.txt");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для записи лога.
     * @param time Время события
     * @param playerName Ник игрока
     * @param status Реферальный код
     */
    public void writeLog(String time, String playerName, String status) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(time + " | " + playerName + " | " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

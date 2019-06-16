package com.robotgenerate.core;

import com.robotgenerate.model.Robot;
import com.robotgenerate.model.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RobotWriter {

    private static String SETTINGS = "************************************ Settings *****************************************\n";

    private static String VARIABLES = "************************************* Variables ***************************************\n";

    private static String KEYWORDS = "************************************* Keywords *****************************************\n";

    private static String TASKS = "******************************************** Tasks **********************************************\n";

    /**
     * generate robot file
     *
     * @param robots
     */
    public static void generateRobotFile(List<Robot> robots, String dst) {

        robots.forEach(robot -> {
            File file = new File(dst + File.separator + robot.getControllerClass().getSimpleName() + ".robot");
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                writeSettings(fileOutputStream, robot);
                writeVariables(fileOutputStream, robot);
                writeKeywords(fileOutputStream, robot);
                writeTasks(fileOutputStream, robot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeSettings(OutputStream outputStream, Robot robot) throws IOException {
        outputStream.write(SETTINGS.getBytes());
        Settings settings = robot.getSettings();
        settings.getLibrary().forEach(library -> {
            try {
                outputStream.write(String.format("Library    %s\n", library).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeVariables(OutputStream outputStream, Robot robot) throws IOException {
        outputStream.write(VARIABLES.getBytes());
        robot.getVariables().forEach((k, v) -> {
            try {
                outputStream.write(String.format("%s    %s\n", k, v).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeKeywords(OutputStream outputStream, Robot robot) throws IOException {
        outputStream.write(KEYWORDS.getBytes());
        robot.getKeywords().forEach(keyword -> {
            try {
                outputStream.write(keyword.getName().getBytes());
                outputStream.write("\n".getBytes());
                outputStream.write("\t[Arguments]".getBytes());
                outputStream.write("\n".getBytes());
                outputStream.write(String.format("\t[Return]    %s", keyword.getReturnValue()).getBytes());
                outputStream.write("\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeTasks(OutputStream outputStream, Robot robot) throws IOException {
        outputStream.write(TASKS.getBytes());
    }

}

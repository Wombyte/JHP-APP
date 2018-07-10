package mc.wombyte.marcu.jhp_app;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Classes.Schedule;
import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Classes.Subject;

/**
 * Created by marcu on 13.08.2017.
 */

public class FileLoader {

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    Activity activity;

    String current_semester = "";

    public FileLoader() { }

    public void readData() {
        if(!Storage.READ_EXTERNAL_STORAGE_ALLOWED) {
            return;
        }

        //read the current semester
        readSemesterData();
        current_semester = Storage.current_semester;
        for(int i = 0; i < Storage.semester.size(); i++) {
            readSemester(i);
        }

        //read the schedule if the file exists
        try {
            readSchedule();
        } catch(IOException e) {
            Log.d("FileLoader", "file schedule not found, or error while reading it");
            e.printStackTrace();
        }

        //read the subjects if the file exists
        try {
            readSubjects();
        } catch(IOException e) {
            Log.d("FileLoader", "file subjects not found, or error while reading it");
            e.printStackTrace();
        }

        //read the homework if the file exists
        try {
            readHomework();
        } catch(IOException e) {
            Log.d("FileLoader", "file homework not found, or error while reading it");
            e.printStackTrace();
        }

        //read the grades if the file exists
        try {
            readGrades();
        } catch(IOException e) {
            Log.d("FileLoader", "file grades not found, or error while reading it");
            e.printStackTrace();
        }

        //read the settings if the file exists
        readSettings();
    }

    //******************************************************* schedule *******************************************************//

    /**
     * reads the data from the current schedule folder
     * clears times and lessons
     * if the current times and lessons file exist the belonging reading methdos are called
     * and the results are saved to storage
     * @throws IOException: readTimes and readLessons
     */
    private void readSchedule() throws IOException {
        Storage.schedule.clearTimes();
        Storage.schedule.clearLessons();

        File times_file = new File("/storage/emulated/0/JHP/" + current_semester + "/schedule/times.txt");
        if(times_file.exists()) {
            Storage.schedule.setTimes( Schedule.readTimes(times_file));
        }

        File lessons_file = new File("/storage/emulated/0/JHP/" + current_semester + "/schedule/lessons.txt");
        if(lessons_file.exists()) {
            Storage.schedule.setLessons( Schedule.readLessons(lessons_file));
        }
    }

    //******************************************************* subjects *******************************************************//

    /**
     * read the subjects from the current general subject folder
     * clear subject list from storage
     * if the current general subject folder does not exist, the method stops
     * main: iterating over all specific subject folders to read their data.txt
     * @throws IOException: readSubject()
     */
    private void readSubjects() throws IOException {
        Storage.subjects.clear();

        File subjects_folder = new File("/storage/emulated/0/JHP/" + current_semester + "/subjects");
        if(!subjects_folder.exists()) {
            return;
        }

        for(File subject_folder: subjects_folder.listFiles()) {
            Storage.addSubject( Subject.readSubject(new File(subject_folder, "data.txt")));
        }
    }

    //******************************************************* homework *******************************************************//

    /**
     * reads all data.txt of the existing homework folders
     * first the existing homework list in Storage is cleared
     * if the general homework folder does not exist, the method stops
     * main: iterating over all specific homework folders
     * and read the data.txt file
     * @throws IOException: readHomeworkData()
     */
    private void readHomework() throws IOException{
        Storage.clearHomework();

        File homework_folder = new File("/storage/emulated/0/JHP/homework");
        if(!homework_folder.exists()) {
            return;
        }

        for(File f: homework_folder.listFiles()) {
            Homework homework = Homework.readHomeworkData(new File(f, "data.txt"));
            if(homework != null) {
                Storage.addHomework(homework.getSubjectindex(), homework);
            }
        }
    }

    //******************************************************* grades *******************************************************//

    /**
     * reads all data.txt of the existing grade folders
     * first the existing grade list in Storage is cleared
     * if the general grade folder does not exist, the method stops
     * main: iterating over all specific grade folders
     * and read the data.txt file
     * @throws IOException: readGradeData()
     */
    private void readGrades() throws IOException {
        Storage.clearGrades();

        File grades_folder = new File("/storage/emulated/0/JHP/" + current_semester + "/grades");
        if(!grades_folder.exists()) {
            return;
        }

        for(File grade_folder: grades_folder.listFiles()) {
            Grade grade = Grade.readGradeData(new File(grade_folder, "data.txt"));
            if(grade != null) {
                Storage.addGrade(grade.getSubjectindex(), grade);
            }
        }
    }

    //******************************************************* settings *******************************************************//
    /*
     * read the settings from the transferred BufferedReader
     */
    public void readSettings() {
        Storage.settings = new Settings();
        String line;
        String[] segment = new String[3]; //path, type, value
        int start, end;
        try {
            File file_settings = new File("/storage/emulated/0/JHP/settings.txt");
            if(!file_settings.exists()) {
                return;
            }

            FileReader fr = new FileReader(file_settings);
            BufferedReader br = new BufferedReader(fr);

            while((line = br.readLine()) != null) {
                //getting path, type and value of setting
                end = -1;
                for(int i = 0; i < 3; i++) {
                    start = end + 1;
                    end = line.indexOf(":", start);
                    segment[i] = line.substring(start, end);
                }

                //saving the value to the settings
                if(segment[1].equals("int")) {
                    Integer value = Integer.parseInt(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("double")) {
                    Double value = Double.parseDouble(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("string")) {
                    String value = segment[2];
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("boolean")) {
                    Boolean value = Boolean.parseBoolean(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("byte")) {
                    Byte value = Byte.parseByte(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("arraylist")) {
                    ArrayList<String> value = stringToList(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
                if(segment[1].equals("title_strings")) {
                    int[] value = stringToArray(segment[2]);
                    Storage.settings.setChildValue(stringToPath(segment[0]), value);
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //******************************************************* semester *******************************************************//

    /*
     * read all data from the folders
     */
    private void readSemesterData() {
        Storage.semester.clear();

        File mainfolder = new File("/storage/emulated/0/JHP");
        for (File file : mainfolder.listFiles()) {
            if (file.isDirectory() && !file.getName().equals("Backup") && !file.getName().equals("homework")) {
                Storage.semester.add(new Semester(file.getName()));
            }
        }
        Storage.setHighestSemester();
    }

    /*
     * read data from the semester file
     */
    private void readSemester(int i) {
        File file = new File("/storage/emulated/0/JHP/" + Storage.semester.get(i).getName() + "/semester.txt");

        try {
            if (file.exists()) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                Storage.semester.get(i).number_of_subjects = Integer.parseInt(br.readLine());
                Storage.semester.get(i).average = Double.parseDouble(br.readLine());

                br.close();
            }
        } catch(IOException e) {
            Log.d("FileLoader", "file grades not found, or error while reading it");
            e.printStackTrace();
        }

        System.out.println(Storage.semester.get(i).getName() + ": " + Storage.semester.get(i).getNumberOfSubjects() + " " + Storage.semester.get(i).getAverage());
    }

    //******************************************************* methods *******************************************************//

    /*
     * converts the the transferred String into a String list
     */
    private ArrayList<String> stringToList(String string) {
        if(string.charAt(1) == ']') {
            return new ArrayList<>();
        }

        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        int end;

        while((end = string.indexOf(",", start)) != -1) {
            result.add(string.substring(start+1, end));
            start = end+1;
        }
        end = string.length();
        result.add(string.substring(start+1, end-1));

        return result;
    }

    /*
     * converts the the transferred String into a int[]
     */
    private int[] stringToArray(String string) {
        ArrayList<String> stringlist = stringToList(string);
        int[] result = new int[stringlist.size()];
        for(int i = 0; i < stringlist.size(); i++) {
            result[i] = Integer.parseInt(stringlist.get(i));
        }
        return result;
    }

    /*
     * converts the the transferred String into a path
     */
    private ArrayList<Integer> stringToPath(String string) {
        ArrayList<String> string_list = stringToList(string);
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < string_list.size(); i++) {
            result.add(Integer.parseInt(string_list.get(i)));
        }
        return result;
    }

    /**
     * converts a data line into a String array consisting of the tag and the data itself
     * @param line: String, that will be converted
     * @return String[]: parts of the line { tag, data }
     */
    public static String[] getLineData(String line) {
        if(line == null || line.equals("")) {
            return null;
        }

        String tag = line.substring(0, 3);
        String data = line.substring(5);
        return new String[] { tag, data };
    }
}

package mc.wombyte.marcu.jhp_app;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Classes.Schedule;
import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Classes.Subject;

/**
 * Created by marcu on 13.08.2017.
 */

public class FileLoader {

    Activity activity;

    String current_semester = "";

    public FileLoader() { }

    /**
     * reads all the data that is necessary for the fluid working of the app
     * if the permission 'read external Storage' is not allowed, the method stops
     * 1. the semester data is read to get the current semester
     * 2. schedule, subjects, homework, grades and settings are read
     */
    public void readData() {
        File jhp_folder = new File("/storage/emulated/0/JHP");
        if(!Storage.READ_EXTERNAL_STORAGE_ALLOWED || !jhp_folder.exists()) {
            return;
        }

        //read the current semester
        readSemesterData();
        current_semester = Storage.current_semester;
        for(int i = 0; i < Storage.semester.size(); i++) {
            readSemester(i);
        }

        //read the subjects if the file exists
        //have to be read first, as they are the base
        try {
            readSubjects();
        } catch(IOException e) {
            Log.d("FileLoader", "file subjects not found, or error while reading it");
            e.printStackTrace();
        }

        //read the schedule if the file exists
        try {
            readSchedule();
        } catch(IOException e) {
            Log.d("FileLoader", "file schedule not found, or error while reading it");
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
        try {
            readSettings();
        } catch(IOException e) {
            Log.d("FileLoader", "file settings not found, or error while reading it");
            e.printStackTrace();
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// schedule ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

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

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// subjects ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * read the subjects from the current general subject folder
     * clear subject list from storage
     * if the current general subject folder does not exist, the method stops
     * main: iterating over all specific subject folders to read their data.txt
     * @throws IOException: readSubject()
     */
    private void readSubjects() throws IOException {
        Storage.subjects.clear();
        Storage.grades.clear();

        File subjects_folder = new File("/storage/emulated/0/JHP/" + current_semester + "/subjects");
        if(!subjects_folder.exists()) {
            return;
        }

        for(File subject_folder: subjects_folder.listFiles()) {
            Storage.addSubject( Subject.readSubject(new File(subject_folder, "data.txt")));
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// homework ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

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

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);

        for(File f: homework_folder.listFiles()) {
            Homework homework = Homework.readHomeworkData(new File(f, "data.txt"), c);
            if(homework != null) {
                Storage.addHomework(homework.getSubjectindex(), homework);
            }
        }

        //deleting homework
        for(Homework homework: Storage.getHomeworkList()) {
            if(!c.getTime().before(homework.getDate().date())) {
                 Storage.deleteHomework(homework);
            }
        }
    }

    /**
     * reads all uris from the images folder that fulfil these conditions
     * 1. first 3 letters are "HDE"
     * 3. the following String = grade name
     * if the image folder doesn't exist, nothing will happen
     * @param homework_name: indices of the grade
     * @return list of all description images of this grade
     */
    public ArrayList<Uri> readHomeworkDescriptionImages(String homework_name) {
        ArrayList<Uri> result = new ArrayList<>();

        File folder = new File("/storage/emulated/0/JHP/images");
        if(!folder.exists()) {
            return result;
        }

        for(File file: folder.listFiles(File::isFile)) {
            String name = file.getName();
            if(name.substring(0, 3).equals("HDE")) {
                if(getNameFromImage(3, name).equals(homework_name)) {
                    result.add(Uri.fromFile(file));
                }
            }
        }

        return result;
    }

    /**
     * reads all uris from the images folder that fulfil these conditions
     * 1. first 3 letters are "HDE"
     * 3. the following String = grade name
     * if the image folder doesn't exist, nothing will happen
     * @param homework_name: indices of the grade
     * @return list of all description images of this grade
     */
    public ArrayList<Uri> readHomeworkSolutionImages(String homework_name) {
        ArrayList<Uri> result = new ArrayList<>();

        File folder = new File("/storage/emulated/0/JHP/images");
        if(!folder.exists()) {
            return result;
        }

        for(File file: folder.listFiles(File::isFile)) {
            String name = file.getName();
            if(name.substring(0, 3).equals("HSO")) {
                if(getNameFromImage(3, name).equals(homework_name)) {
                    result.add(Uri.fromFile(file));
                }
            }
        }

        return result;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// grades ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads all data.txt of the existing grade folders
     * first the existing grade list in Storage is cleared
     * if the general grade folder does not exist, the method stops
     * main: iterating over all specific grade folders
     * and read the data.txt file
     * @throws IOException: readGradeData()
     */
    private void readGrades() throws IOException {
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

    /**
     * reads all uris from the images folder that fulfil these conditions
     * 1. first 3 letters are "GDE"
     * 2. next 3 letters are the current semester
     * 3. the following name = grade name
     * if the image folder doesn't exist, nothing will happen
     * @param grade_name: indices of the grade
     * @return list of all description images of this grade
     */
    public ArrayList<Uri> readGradeImages(String grade_name) {
        ArrayList<Uri> result = new ArrayList<>();

        File folder = new File("/storage/emulated/0/JHP/images");
        if(!folder.exists()) {
            return result;
        }

        for(File file: folder.listFiles(Grade.fileFilter)) {
            if(getNameFromImage(6, file.getName()).equals(grade_name)) {
                result.add(Uri.fromFile(file));
            }
        }

        return result;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// settings ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the settings from the settings.txt
     * first the list is cleared
     * if the mentioned file does not exist the method stops
     * else the method {@link Settings#readSettings(File)} is called
     * @throws IOException: {@link Settings#readSettings(File)}
     */
    public void readSettings() throws IOException {
        Storage.settings = new Settings();

        File file_settings = new File("/storage/emulated/0/JHP/settings.txt");
        if(!file_settings.exists()) {
            return;
        }

        Settings.readSettings(file_settings);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// semester ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the names of all existing semester folders
     * first the current list is clears
     * afterwards the highest semester is calculated and set
     */
    private void readSemesterData() {
        Storage.semester.clear();

        File mainfolder = new File("/storage/emulated/0/JHP");
        for (File file : mainfolder.listFiles()) {
            if (file.isDirectory()
                    && !file.getName().equals("schedule")
                    && !file.getName().equals("homework")
                    && !file.getName().equals("images")) {
                Storage.semester.add(new Semester(file.getName()));
            }
        }
        Storage.setHighestSemester();
    }

    /**
     * reads the semester data from the different data.txt files
     * if the current data.txt does not exist, the method stops
     * @param semester_id: index of the semester in the Storage list
     */
    private void readSemester(int semester_id) {
        File file = new File("/storage/emulated/0/JHP/" + Storage.semester.get(semester_id).getName() + "/semester.txt");
        if(!file.exists()) {
            return;
        }

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            Storage.semester.get(semester_id).number_of_subjects = Integer.parseInt(br.readLine());
            Storage.semester.get(semester_id).average = Double.parseDouble(br.readLine());

            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

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

    /**
     * returns the grade/homework name included in the filename
     * the start index of the grade/homework name is transmitted while
     * the end index is the location of the second '_'
     * @param start: length of the pre-defined string
     * @param name: name of the file
     * @return String: name of the matching grade/homework
     */
    private static String getNameFromImage(int start, String name) {
        int t = name.indexOf('_');
        String temp = name.substring(t+1, name.length());
        int end = t + temp.indexOf('_')+1;
        return name.substring(start, end);
    }
}

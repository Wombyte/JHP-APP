package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Classes.Schedule;
import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Classes.Subject;
import mc.wombyte.marcu.jhp_app.Reuseables.BitmapSaveTask;


/**
 * Created by marcu on 13.08.2017.
 */

public class FileSaver {

    private Context context;

    /**
     * Constructor
     * @param context: important for the load image asyncTask
     */
    public FileSaver(Context context) {
        this.context = context;
    }

    /**
     * saves all the data stored in the Storage class
     * checks for permission
     * if the app is started new, the old data is read
     * if the app is started the first time, a new semester file is created
     * unnecessary files are deleted
     * data is saved
     */
    public void saveData() {
        if (!Storage.WRITE_EXTERNAL_STORAGE_ALLOWED) {
            System.out.println("external write permission not allowed");
            return;
        }

        //reading data, if the app is started new
        if (!Storage.read_already) {
            System.out.println("load data");
            Storage.read_already = true;
            FileLoader fileReader = new FileLoader();
            fileReader.readData();
        }
        //creating a new semester if it is the first
        if(Storage.semester.size() == 0 && Storage.current_semester != null && !Storage.current_semester.equals("")) {
            Storage.semester.add(new Semester(Storage.current_semester, Storage.subjects.size(), Semester.calculateTotalAverage()));
        }

        createFile(new File("/storage/emulated/0/JHP"));
        createFile(new File("/storage/emulated/0/JHP/" + Storage.current_semester));
        writeOnlySemester();

        //schedule
        try {
            writeSchedule();
        } catch(IOException e) {
            Log.e("FileSaver", "error while writing to the schedule files");
            e.printStackTrace();
        }

        //subjects
        try {
            writeSubjects();
        } catch(IOException e) {
            Log.e("FileSaver", "error while writing to the subjects file");
            e.printStackTrace();
        }

        //homework
        try {
            writeHomework();
        } catch(IOException e) {
            Log.e("FileSaver", "error while writing to the homework file");
            e.printStackTrace();
        }

        //grades
        try {
            writeGrades();
        } catch(IOException e) {
            Log.e("FileSaver", "error while writing to the grades file");
            e.printStackTrace();
        }

        //settings
        try {
            writeSettings();
        } catch(IOException e) {
            Log.e("FileSaver", "error while writing to the settings file");
            e.printStackTrace();
        }
    }

    /*
     * creates/updates the backup folder as a subfolder of the main JHP-folder
     */
    public void saveBackup() {
        if (!Storage.WRITE_EXTERNAL_STORAGE_ALLOWED || Storage.subjects.size() == 0) {
            return;
        }

        File main = new File("/storage/emulated/0/JHP");
        if(!main.exists()) {
            return;
        }

        //reading data, if the app is started new
        if (!Storage.read_already) {
            System.out.println("load data");
            Storage.read_already = true;
            FileLoader fileReader = new FileLoader();
            fileReader.readData();
        }
    }

    /**
     * creates the transmitted file if it does not exist already
     * @param file: file that is created if necessary
     */
    private void createFile(File file) {
        try {
            if (!file.exists()) {
                System.out.println("folder created: " + file.mkdirs());
            }
        }
        catch(Exception e) {
            Log.d("FileSaver", "error while creating: " + file.getName());
            e.printStackTrace();
        }
    }

    //******************************************************* Schedule *******************************************************//

    /*
     * saves the schedule (time and lesson)
     */
    private void writeSchedule() throws IOException {
        if(Storage.schedule == null) {
            return;
        }

        File schedule_folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/schedule");
        if(!schedule_folder.exists()) {
            if(!schedule_folder.mkdirs()) {
                return;
            }
        }

        File time_file = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/schedule/times.txt");
        if(time_file.exists() || time_file.createNewFile()) {
            Schedule.writeTimes( time_file, Storage.schedule.getTime() );
        }
        else {
            throw new IOException("error while creating/writing to the time file");
        }

        File lesson_file = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/schedule/lessons.txt");
        if(lesson_file.exists() || lesson_file.createNewFile()) {
            Schedule.writeLessons( lesson_file, Storage.schedule.getLessons());
        }
        else {
            throw new IOException("error while creating/writing to the lesson file");
        }
    }

    //******************************************************* Subjects *******************************************************//

    /**
     * save the data of all subjects
     * if there is no subject, nothing has to be saved
     * if the general subject folder does not exist, it is created
     * @throws IOException: from writeSubjectData()
     */
    private void writeSubjects() throws IOException{
        if(Storage.subjects.size() == 0) {
            return;
        }

        File subject_folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/subjects");
        if(!subject_folder.exists()) {
            boolean success = subject_folder.mkdir();
            Log.d("write subjects", "creating subject folder successful? " + success);
        }

        for(Subject subject: Storage.subjects) {
            File folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/subjects/" + subject.getIndex());
            if(!folder.exists()) {
                boolean success = folder.mkdirs();
                Log.d("write subject", "creating a specific subject folder successful? " + success);
            }

            File file = new File(folder, "data.txt");
            if(!file.exists()) {
                boolean success = file.createNewFile();
                Log.d("write subject", "creating the data file of a subject successful? " + success);
            }

            Subject.writeSubjectData(file, subject);
        }
    }

    /**
     * deletes the transmitted subject in the Storage and
     * @param subject: object that is deleted
     */
    public static void deleteSubject(Subject subject) {
        Storage.deleteSubject(subject.getIndex());

        File subject_folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/subjects");
        for (File f : subject_folder.listFiles()) {
            if (f.getName().equals(String.valueOf(subject.getIndex()))) {
                boolean success = f.delete();
                Log.d("delete subject", "deleting subject folder succesful? " + success);
            }
        }
    }

    //******************************************************* grades *******************************************************//

    /**
     * preparing a new grade folder, when the grade activity is opened
     * including the general grade folder, the new grade and its image folder
     * if the "newGrade" folder already exists, the images are deleted
     * and the folder can be reused
     */
    public void prepareNewGradeFolder() {
        File image_folder = new File("/storage/emulated/0/JHP/homework/newHomework/images/description");

        if(image_folder.exists()) {
            for(File f: image_folder.listFiles()) {
                boolean success = f.delete();
                Log.d("prepare grade", "deleting existing image in description folder successful? " + success);
            }
        }
        else {
            boolean success = image_folder.mkdirs();
            Log.d("prepare grade", "creating path for description folder successful? " + success);
        }
    }

    /**
     * renames the "newGrade" folder into 'name'
     * @param name: name of the new folder
     */
    public void renameNewGradeFolderTo(String name) {
        File old = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/newGrade");
        File file = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/" + name);
        Log.d("rename new grade folder", "renaming the new homework folder was: " + old.renameTo(file));
    }

    public static final int GRADE_MINUS_SUBJECT_INDEX = 0;
    public static final int GRADE_MINUS_GRADE_INDEX = 1;
    /**
     * renames the folder of the transmitted grade depending on the mode
     * 1. the subject index is decreased by 1
     * 2. the grade index is decreased by 1
     * @param grade: object of the renaming folder
     * @param mode: kind of name change
     */
    public static void renameGradeFolder(Grade grade, int mode) {
        File old = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/" + grade.getFileName());
        if(mode == GRADE_MINUS_SUBJECT_INDEX) {
            grade.setSubjectIndex( grade.getSubjectindex()-1);
        }
        if(mode == GRADE_MINUS_GRADE_INDEX) {
            grade.setIndex( grade.getIndex()-1);
        }
        File file = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/" + grade.getFileName());

        boolean success = old.renameTo(file);
        Log.d("rename homework", "renaming a grade folder successful" + success);
    }

    /**
     * saving all grades to the current semester folder
     * if there is no grade, nothing will be saved
     * if the general grade folder does not exist, it will be created
     * @throws IOException: from writeGradeData()
     */
    private void writeGrades() throws IOException {
        if(Storage.grades.size() == 0) {
            return;
        }

        File grades_folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/grades");
        if(!grades_folder.exists()) {
            boolean success = grades_folder.mkdir();
            Log.d("write subjects", "creating subject folder successful? " + success);
        }

        for(ArrayList<Grade> list: Storage.grades) {
            for(Grade grade: list) {
                File folder = new File("/storage/emulated/0/JHP/" + Storage.current_semester + "/grades/" + grade.getFileName());
                if(!folder.exists()) {
                    boolean success = folder.mkdirs();
                    Log.d("write grade", "creating a specific grade folder successful? " + success);
                }

                File file = new File(folder.getPath() + "/data.txt");
                if(!file.exists()) {
                    boolean success = file.createNewFile();
                    Log.d("write grade", "creating the data file of a grade successful? " + success);
                }
                Grade.writeGradeData(file, grade);
            }
        }
    }

    /**
     * deletes the grade and its files
     * @param grade: object which contains the data that has to be deleted
     */
    public static void deleteGrade(Grade grade) {
        Storage.deleteGrade(grade);

        for(File file: folder_homework.listFiles()) {
            if(file.getName().equals(grade.getFileName())) {
                boolean success = file.delete();
                Log.d("delete grade", "deleting grade folder successful? " + success);
            }
        }
    }

    //******************************************************* Homework *******************************************************//

    private static File folder_homework = new File("/storage/emulated/0/JHP/homework");

    /**
     * preparing a new homework folder, when the homework activity is opened
     * including the general homework folder, the new homework and its image folders
     * if the "newHomework" folder already exists, the images are deleted
     * and the folder can be reused
     */
    public void prepareNewHomeworkFolder() {
        File description_image_folder = new File("/storage/emulated/0/JHP/homework/newHomework/images/description");
        File solution_image_folder = new File("/storage/emulated/0/JHP/homework/newHomework/images/solution");

        if(description_image_folder.exists()) {
            for(File f: description_image_folder.listFiles()) {
                boolean success = f.delete();
                Log.d("prepare Homework", "deleting existing image in description folder successful? " + success);
            }
        }
        else {
            boolean success = description_image_folder.mkdirs();
            Log.d("prepare Homework", "creating path for description folder successful? " + success);
        }

        if(solution_image_folder.exists()) {
            for(File f: solution_image_folder.listFiles()) {
                boolean success = f.delete();
                Log.d("prepare Homework", "deleting existing image in description folder successful? " + success);
            }
        }
        else {
            boolean success = solution_image_folder.mkdirs();
            Log.d("prepare Homework", "creating path for description folder successful? " + success);
        }
    }

    /**
     * renames the "newHomework" folder into 'name'
     * @param name: name of the new folder
     */
    public void renameNewHomeworkFolderTo(String name) {
        File old = new File("/storage/emulated/0/JHP/homework/newHomework");
        File file = new File("/storage/emulated/0/JHP/homework/" + name);
        Log.d("FileSaver", "renaming the new homework folder was: " + old.renameTo(file));
    }

    public static final int HOMEWORK_MINUS_SUBJECT_INDEX = 0;
    public static final int HOMEWORK_MINUS_HOMEWORK_INDEX = 1;
    /**
     * renames the folder of the transmitted homework depending on the mode
     * 1. the subject index is decreased by 1
     * 2. the homework index is decreased by 1
     * @param homework: object of the renaming folder
     * @param mode: kind of name change
     */
    public static void renameHomeworkFolder(Homework homework, int mode) {
        File old = new File("/storage/emulated/0/JHP/homework/" + homework.getFileName());
        if(mode == HOMEWORK_MINUS_SUBJECT_INDEX) {
            homework.setSubjectIndex( homework.getSubjectindex()-1);
        }
        if(mode == HOMEWORK_MINUS_HOMEWORK_INDEX) {
            homework.setIndex( homework.getIndex()-1);
        }
        File file = new File("/storage/emulated/0/JHP/homework/"  + homework.getFileName());

        boolean success = old.renameTo(file);
        Log.d("rename homework", "renaming a homework folder successful" + success);
    }

    /**
     * saves the image described by uri to the solution image file of the transmitted homework folder
     * @param uri: uri that points at the image
     * @param homework_name: name of the homework folder
     */
    public void saveSolutionImage(Uri uri, String homework_name) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        String name = "PNG_" + timeStamp + ".png";
        File file = Homework.getDestinationImageFile(Homework.SOLUTION_IMAGE, homework_name, name);

        new BitmapSaveTask(context, file).execute(uri);
    }

    /**
     * saves the image described by uri to the description image file of the transmitted homework folder
     * @param uri: uri that points at the image
     * @param homework_name: name of the homework folder
     */
    public void saveDescriptionImage(Uri uri, String homework_name) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        String name = "PNG_" + timeStamp + ".png";
        File file = Homework.getDestinationImageFile(Homework.DESCRIPTION_IMAGE, homework_name, name);

        new BitmapSaveTask(context, file).execute(uri);
    }

    /**
     * saves all homework in the homework folder
     * @throws IOException: writing and creating files during process
     */
    private void writeHomework() throws IOException{
        if(Storage.homework.size() == 0) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);

        for(ArrayList<Homework> list: Storage.homework) {
            for(Homework homework: list) {
                File folder = new File(folder_homework.getPath() + "/" + homework.getFileName());
                if(!folder.exists()) {
                    boolean success = folder.mkdirs();
                    Log.d("write homework", "creating a specific homework folder successful? " + success);
                }

                File file = new File(folder.getPath() + "/data.txt");
                if(!file.exists()) {
                    boolean success = file.createNewFile();
                    Log.d("write homework", "creating the data file of a homework successful? " + success);
                }

                Homework.writeHomeworkData(file, homework, c);
            }
        }
    }

    /**
     * deletes the homework and its files
     * @param homework: object which contains the data that has to be deleted
     */
    public static void deleteHomework(Homework homework) {
        Storage.deleteHomework(homework);

        for(File file: folder_homework.listFiles()) {
            if(file.getName().equals(homework.getFileName())) {
                boolean success = file.delete();
                Log.d("delete Homework", "deleting homework folder successful? " + success);
            }
        }
    }

    //******************************************************* Settings *******************************************************//

    /**
     * saving all settings by calling the print method of the root setting element
     * this method will call the print method of its kids and so on
     * if the settings are null or do not have children the method stops
     * if the settings file does not exist, it is created
     * @throws IOException: writing to a file
     */
    private void writeSettings() throws IOException{
        if(!Storage.WRITE_EXTERNAL_STORAGE_ALLOWED) {
            return;
        }
        if(Storage.settings == null || Storage.settings.getChildSettings().size() == 0) {
            return;
        }

        File file = new File("/storage/emulated/0/JHP/settings.txt");
        if(!file.exists()) {
            boolean success = file.delete();
            Log.d("delete Homework", "creating settings file successful? " + success);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        try {
            Storage.settings.print(bw);
        } catch(Exception e) {
            e.printStackTrace();
        }
        bw.close();
    }

    //******************************************************* semester *******************************************************//

    /*
     * saves only the semesters (important for archive)
     */
    public void writeOnlySemester() {
        FileWriter fw;
        BufferedWriter bw;

        for(Semester semester: Storage.semester) {
            File file = new File("/storage/emulated/0/JHP/" + semester.getName() + "/semester.txt");
            try {
                if(!file.exists()) {
                    System.out.println("subjects created: " + file.createNewFile());
                }
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);

                double average = semester.getAverage();
                if(semester.getName().equals(Storage.current_semester)) {
                    average = Semester.calculateTotalAverage();
                }

                bw.write( String.valueOf(semester.getNumberOfSubjects()));
                bw.newLine();
                bw.write( String.valueOf(average));

                bw.close();
            } catch(IOException e) {
                Log.d("FileSaver", "error while writing to the semester file");
                e.printStackTrace();
            }
        }
    }

    /**
     * creates a string for all files including a tag and the matching data
     * simplifies future extensions
     * @param tag: tag
     * @param data: data of the line
     * @return data line: String containing tag, ":", data, linebreak
     */
    public static String data(String tag, String data) {
        return tag + ": " + data + '\n';
    }
}
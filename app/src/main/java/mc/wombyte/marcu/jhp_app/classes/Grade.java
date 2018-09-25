package mc.wombyte.marcu.jhp_app.classes;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.FileLoader;
import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.Storage;

public class Grade implements Serializable {

    int number;
    Date date_written;
    Date date_got;
    String description;
    String short_description;
    String misc = "";
    boolean exam = false;
    int subject_index;
    int index;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public Grade(int index, int subject_index, int number, Date date_written, Date date_got, String description, String short_description, String misc, boolean exam) {
        this.index = index;
        this.subject_index = subject_index;
        this.number = number;
        this.date_written = date_written;
        this.date_got = date_got;
        this.description = description;
        this.short_description = short_description;
        this.misc = misc;
        this.exam = exam;
    }

    public Grade(int subject_index, int number, Date date_written, Date date_got, String description, String short_description, String misc, boolean exam) {
        this.subject_index = subject_index;
        this.number = number;
        this.date_written = date_written;
        this.date_got = date_got;
        this.description = description;
        this.short_description = short_description;
        this.misc = misc;
        this.exam = exam;
    }

    public Grade(int subject_index, int index) {
        this.subject_index = subject_index;
        this.index = index;
    }

    public void setNumber(int number) { this.number = number; }
    public int getNumber() { return number; }

    public void setWrittenDate(Date date_written) { this.date_written = date_written; }
    public Date getWrittenDate() { return date_written; }

    public void setGotDate(Date date_got) { this.date_got = date_got; }
    public Date getGotDate() { return date_got; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setShortDescription(String short_description) { this.short_description = short_description; }
    public String getShortDescription() { return short_description; }

    public void setMisc(String misc) { this.misc = misc; }
    public String getMisc() { return misc; }

    public void setExam(boolean exam) { this.exam = exam; }
    public boolean isExam() { return exam; }

    public void setSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public void subSubjectIndex() { this.subject_index--; }
    public int getSubjectindex() { return subject_index; }

    public void setIndex(int index) { this.index = index; }
    public int getIndex() { return index; }

    /**
     * calculates the grade, which is likely the next
     * depending on {@link Subject#average} of the transmitted subject_index
     * if the average is not set yet, the method returns
     * 3 for grades and 10 for points
     * @param subject_index: index of the subject whose average is taken
     * @return likely next grade of the subject
     */
    public static int getPredictedGrade(int subject_index) {
        double average = Storage.subjects.get(subject_index).getAverage();

        if(average == 0) {
            if(Storage.settings.grades_isRatingInGrades()) {
                return 3;
            }
            else {
                return 10;
            }
        }

        average += 0.5;
        if((int) average == 0) {
            average = 10;
        }
        return (int) average;
    }

    /**
     * File filter that checks for the following aspects
     * 1. is a file
     * 2. tag is GDE
     * 3. semester is current semester
     */
    public static final FileFilter fileFilter = (file) -> {
        if(file.isDirectory()) return false;
        String name = file.getName();
        if(!name.substring(0, 3).equals("GDE")) return false;
        if(!name.substring(3, 6).equals(Storage.current_semester)) return false;
        return true;
    };

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// saving ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * @return String: name of the belonging file consisting of subject &- grade index
     */
    public String getFileName() { return subject_index + "_" + index; }

    /**
     * write the data of the transferred grade into the matching data file
     * if the file does not exist, it will be created
     * @param grade: object which data has to be saved
     * @param des: destination file in which the data is written
     * @throws IOException: writing to a file
     */
    public static void writeGradeData(File des, Grade grade) throws IOException {
        if(grade == null || !des.exists()) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        bw.write( FileSaver.data("NUM", String.valueOf(grade.getNumber())));
        bw.write( FileSaver.data("DWR", sdf.format(grade.getWrittenDate())));
        bw.write( FileSaver.data("DGO", sdf.format(grade.getGotDate())));
        bw.write( FileSaver.data("LDE", grade.getDescription()));
        bw.write( FileSaver.data("SDE", grade.getShortDescription()));
        bw.write( FileSaver.data("MIS", grade.getMisc()));
        bw.write( FileSaver.data("EXA", String.valueOf(grade.isExam())));
        bw.close();
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// reading ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public static Grade readGradeData(File des) throws IOException{
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String[] line_data;
        int[] ids = Grade.getIndicesFromFileName(des.getParentFile().getName());

        Grade result = new Grade(ids[0], ids[1]);
        while((line_data = FileLoader.getLineData(br.readLine())) != null) {
            switch(line_data[0]) {
                case "NUM":
                    result.setNumber( Integer.parseInt(line_data[1]));
                    break;
                case "DWR":
                    try { result.setWrittenDate( sdf.parse(line_data[1])); }
                    catch(ParseException e) { e.printStackTrace(); }
                    break;
                case "DGO":
                    try { result.setGotDate( sdf.parse(line_data[1])); }
                    catch(ParseException e) { e.printStackTrace(); }
                    break;
                case "LDE":
                    result.setDescription( line_data[1]);
                    break;
                case "SDE":
                    result.setShortDescription( line_data[1]);
                    break;
                case "MIS":
                    result.setMisc( line_data[1]);
                    break;
                case "EXA":
                    result.setExam( Boolean.parseBoolean( line_data[1]));
                    break;
            }
        }

        return result;
    }

    /**
     * {@link FileLoader#readGradeImages(String)}
     * @param name: name of the grade folder
     * @return ArrayList: list of all image uris
     */
    public static ArrayList<Uri> readGradeImages(String name) {
        return (new FileLoader()).readGradeImages(name);
    }

    /**
     * if the transmitted values can be converted to a grade
     * the matching grade name is transmitted to {@link FileLoader#readGradeImages(String)}
     * and the size of the returned list is returned
     * @param subject_index: subject index of the grade
     * @param grade_index: index of the grade
     * @return int: amount of subfiles
     */
    public static int readImageAmount(int subject_index, int grade_index) {
        if(subject_index < 0 || grade_index < 0) {
            return -1;
        }

        String name = (new Grade(subject_index, grade_index).getFileName());
        return (new FileLoader()).readGradeImages(name).size();
    }

    /**
     * converts a file name created by 'getFileName' back into subject -& grade index
     * @param name: file name
     * @return int[]: { subject index, grade index }
     */
    private static int[] getIndicesFromFileName(String name) {
        int _id = name.indexOf('_');
        int sid = Integer.parseInt(name.substring(0, _id));
        int gid = Integer.parseInt(name.substring(_id+1));
        return new int[] { sid, gid };
    }
}

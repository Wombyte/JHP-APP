package mc.wombyte.marcu.jhp_app.Classes;

import android.graphics.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.FileLoader;
import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.Storage;

/**
 * Created by marcu on 04.07.2017.
 */

public class Subject implements Serializable {

    int index;
    String name;
    String abbreviation = "";
    int color;
    int color_dark;
    double average = 0;
    String teacher = "";
    String notice = "";

    public Subject(String name, int color, int index) {
        this.index = index;
        this.name = name;
        this.color = color;
        predictAbbreviation(name);
        calculateDarkerColor(color);
    }

    public Subject(int index, String name, String abbreviation, int color, int color_dark, double average, String teacher, String notice) {
        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
        this.color = color;
        this.color_dark = color_dark;
        this.average = average;
        this.teacher = teacher;
        this.notice = notice;
    }

    private Subject(int index) {
        this.index = index;
    }

    /*
     * calculates the average of all grades included in the subject
     */
    public void calculateAverage() {
        int grades_amount = 0;
        int exam_amount = 0;
        double grades_average = 0;
        double exam_average = 0;

        if (Storage.grades.get(index).size() == 0) {
            average = 0;
            return;
        }

        //if there are not more than 3 grades: all grades count the same
        if(Storage.grades.get(index).size() <= 3) {
            for(int i = 0; i < Storage.grades.get(index).size(); i++) {
                grades_average += Storage.grades.get(index).get(i).getNumber();
                grades_amount++;
            }
            average = grades_average / grades_amount;
            return;
        }
        //if there are more than 3 grades: exams are 1/3
        else {
            for(int i = 0; i < Storage.grades.get(index).size(); i++) {
                if(Storage.grades.get(index).get(i).isExam()) {
                    exam_amount++;
                    exam_average += Storage.grades.get(index).get(i).getNumber();
                }
                else {
                    grades_amount++;
                    grades_average += Storage.grades.get(index).get(i).getNumber();
                }
            }

            exam_average = exam_average / exam_amount;
            grades_average = grades_average / grades_amount;

            if(exam_amount > 0) {
                if(grades_amount < 0) {
                    grades_average = exam_average;
                }
            }
            else {
                exam_average = grades_average;
            }
            average = exam_average * 1/3 + grades_average * 2/3;
        }
    }

    /*
     * tries to predict the chosen abbreviation for the subject
     * if the name consists of multiple words, the fist letters are taken together
     * else the first 2 chars build the abbreviation
     */
    private void predictAbbreviation(String name) {
        if(name.charAt(name.length()-1) == ' ') {
            name = name.substring(0, name.length()-2);
        }
        try {
            if(name.contains(" ")) {
                name = name.toUpperCase();
                this.abbreviation += name.charAt(0);
                while(name.contains(" ") && abbreviation.length() < 3) {
                    abbreviation += name.charAt( name.indexOf(" ") + 1);
                    name = name.substring(name.indexOf(" ") + 1);
                }
            }
            else {
                this.abbreviation = name.substring(0, 3);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * calculates a darker (or a lighter) color
     */
    private void calculateDarkerColor(int color) {
        double factor = 0.5;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        if(r < 50 && g < 50 && b < 50) {
            r += 50;
            g += 50;
            b += 50;
            factor = 1 / factor;
        }
        this.color_dark =  Color.rgb(
                (int) (factor * r),
                (int) (factor * g),
                (int) (factor * b)
        );
    }
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTeacher() {
        return teacher;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getNotice() {
        return notice;
    }
    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color, boolean calculateDarkColorSeparated) {
        this.color = color;
        if(!calculateDarkColorSeparated) calculateDarkerColor(color);
    }

    public int getDarkColor() {
        return color_dark;
    }
    public void setDarkColor(int color_dark) {
        this.color_dark = color_dark;
    }

    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }

    public ArrayList<Lesson> getLessons() {
        ArrayList<Lesson> lessons = new ArrayList<>();
        for(Lesson[] x: Storage.schedule.schedule) {
            for(Lesson y: x) {
                if(y.getSubjectIndex() == index) {
                    lessons.add(y);
                }
            }
        }
        return lessons;
    }

    public ArrayList<Grade> getGrades() {
        return Storage.grades.get(index);
    }

    public ArrayList<Homework> getHomework() {
        return Storage.homework.get(index);
    }
    public void addHomework(Homework homework) {
        Storage.homework.get(index).add(homework);
    }

    //******************************************************* saving *******************************************************//

    /**
     * saves the data of the transmitted subject to the matching folder
     * if necessary the folder and file are created
     * @param des: destination file in which the data is written in
     * @param subject: subject that has to be saved
     * @throws IOException: writing to a file
     */
    public static void writeSubjectData(File des, Subject subject) throws IOException{
        if(!des.exists() || subject == null) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        bw.write( FileSaver.data("NAM", subject.getName()));
        bw.write( FileSaver.data("ABB", subject.getAbbreviation()));
        bw.write( FileSaver.data("LCO", String.valueOf(subject.getColor())));
        bw.write( FileSaver.data("DCO", String.valueOf(subject.getDarkColor())));
        bw.write( FileSaver.data("TEA", subject.getTeacher()));
        bw.write( FileSaver.data("NOT", subject.getNotice()));
        bw.close();
    }

    //******************************************************* reading *******************************************************//

    /**
     * trys to read the data from the transmitted file as a subject
     * if the file does not exist the method returns null
     * @param des: destination file from which the data is read
     * @return Subject: object that was saved
     * @throws IOException: reading from a file
     */
    public static Subject readSubject(File des) throws IOException {
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String[] line_data;

        Subject result = new Subject( Integer.parseInt(des.getParentFile().getName()));
        while((line_data = FileLoader.getLineData(br.readLine())) != null) {
            switch(line_data[0]) {
                case "NAM":
                    result.setName( line_data[1]);
                    break;
                case "ABB":
                    result.setAbbreviation( line_data[1]);
                    break;
                case "LCO":
                    result.setColor( Integer.parseInt( line_data[1]), true);
                    break;
                case "DCO":
                    result.setDarkColor( Integer.parseInt( line_data[1]));
                    break;
                case "TEA":
                    result.setTeacher( line_data[1]);
                    break;
                case "NOT":
                    result.setNotice( line_data[1]);
                    break;
            }
        }

        return result;
    }
}

package mc.wombyte.marcu.jhp_app.classes;

import java.text.DecimalFormat;

import mc.wombyte.marcu.jhp_app.Storage;

/**
 * Created by marcu on 21.01.2018.
 */

public class Semester {

    public boolean active = false;
    public String name;
    public int name_class = 0;
    public int name_semester = 0;
    public int number_of_subjects = 0;
    public double average = 0.0;

    DecimalFormat df = new DecimalFormat("000");

    public Semester(String name, int number_of_subjects, double average) {
        setName(name);
        this.number_of_subjects = number_of_subjects;
        this.average = average;
    }

    public Semester(int c, int semester, int number_of_subjects, double average) {
        //set name
        setName( df.format(c*10 + semester));
        this.name_class = c;
        this.name_semester = semester;
        this.number_of_subjects = number_of_subjects;
        this.average = average;
    }

    public Semester(String name) {
        setName(name);
        this.number_of_subjects = 0;
        this.average = 0;
    }

    /*
     * converts the data of this into an String
     */
    public String toString() {
        return name + " " + number_of_subjects + " " + average;
    }

    /*
     * calculates the total average of all subjects currently loaded
     */
    public static double calculateTotalAverage() {
        double average = 0;
        int legit_subjects = 0;

        for(Subject s: Storage.subjects) {
            double a = s.average;
            average += a;
            if(a != 0.0) {
                legit_subjects++;
            }
        }

        if(legit_subjects == 0) {
            return 0;
        }

        return average / (double) legit_subjects;
    }

    //Getter
    public boolean isActive() { return active; }
    public String getName() { return name; }
    public int getSchoolClass() { return name_class; }
    public int getSemester() { return name_semester; }
    public int getNumberOfSubjects() { return number_of_subjects; }
    public double getAverage() { return average; }

    //Setter
    public void setActive(boolean active) { this.active = active; }
    public void setName(String name) {
        this.name = name;

        int n = 0;
        try {
            n = df.parse(name).intValue();
        } catch(Exception e) {
            e.printStackTrace();
        }

        this.name_class = n/10;
        this.name_semester = n%10;
    }
}

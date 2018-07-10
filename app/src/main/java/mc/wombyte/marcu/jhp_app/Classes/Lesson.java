package mc.wombyte.marcu.jhp_app.Classes;

import java.io.Serializable;

public class Lesson implements Serializable {

    int day;
    int time;
    String room = "";
    int subject_index;
    String teacher = "";

    public Lesson(int day, int time, String room, int subject_index, String teacher) {
        this.day = day;
        this.time = time;
        this.room = room;
        this.subject_index = subject_index;
        this.teacher = teacher;
    }

    public Lesson(int day, int time, String room, int subject_index) {
        this.day = day;
        this.time = time;
        this.room = room;
        this.subject_index = subject_index;
    }
    public Lesson() {}

    public void setDay(int day) { this.day = day; }
    public int getDay() { return day; }

    public void setTime(int time) { this.time = time; }
    public int getTime() { return time; }

    public void setRoom(String room) { this.room = room; }
    public String getRoom() { return room; }

    public void setTeacher(String teacher) { this.teacher = teacher; }
    public String getTeacher() { return teacher; }

    public void setSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public int getSubjectIndex() { return subject_index; }
}

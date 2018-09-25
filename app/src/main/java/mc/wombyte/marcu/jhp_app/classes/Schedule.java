package mc.wombyte.marcu.jhp_app.classes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mc.wombyte.marcu.jhp_app.FileLoader;
import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.Storage;

public class Schedule implements Serializable {

    int[][][] times = new int[9][2][2];
    public static int[][][] predef_time = new int[][][] {
            {{ 7, 40}, { 8, 25}},
            {{ 8, 30}, { 9, 15}},
            {{ 9, 35}, {10, 20}},
            {{10, 25}, {11, 10}},
            {{11, 30}, {12, 15}},
            {{12, 20}, {13,  5}},
            {{13, 10}, {13, 55}},
            {{14,  0}, {14, 45}},
            {{14, 50}, {15, 35}},
    };
    static Lesson[][] schedule = new Lesson[5][9];

    public Schedule() {}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /*
     * return the number today
     * monday: 0
     * sunday: 6
     */
    public static int getTodaysNumber() {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK); //return mo = 2
        day_of_week = (day_of_week-2+7)%7;
        return day_of_week;
    }

    /*
     * @return: returns next lesson || null
     */
    public static Lesson getNextLesson() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        int day;
        int lesson = -1;
        boolean end = false;

        if((day = getTodaysNumber()) < 5) {
            for(int i = 0; i < 9 && !end; i++) {
                c.set(Calendar.HOUR_OF_DAY, Storage.schedule.times[i][0][0]);
                c.set(Calendar.MINUTE, Storage.schedule.times[i][0][1]);
                if(now.before(c.getTime())) {
                    lesson = i;
                    end = true;
                }
            }

            if(lesson == -1) {
                return null;
            }

            //checks for a free lesson
            if(schedule[day][lesson] == null && lesson < 8) {
                if(schedule[day][lesson+1] != null) {
                    lesson++;
                }
            }
            return schedule[day][lesson];
        }
        return null;
    }

    /*
     * returns the list for the schedule adapter
     * including all subjects
     */
    public ArrayList<Object> getScheduleListWithTimes() {
        ArrayList<Object> result = new ArrayList<>();
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 6; x++) {
                if(x == 0) {
                    result.add( getTime(y));
                }
                else {
                    result.add( getLesson(x-1, y));
                }
            }
        }
        return result;
    }

    /*
     * @return: gridview list for a schedule without the times and all subjects
     */
    public ArrayList<Object> getScheduleListWithoutTimes() {
        ArrayList<Object> result = new ArrayList<>();
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 5; x++) {
                result.add( getLesson(x, y));
            }
        }
        return result;
    }

    /*
     * returns the list for the schedule adapter
     * including only the subject with the transferred index
     */
    public ArrayList<Object> getScheduleListWithTimes(int subject_index) {
        ArrayList<Object> result = new ArrayList<>();
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 6; x++) {
                if(x == 0) {
                    result.add( getTime(y));
                }
                else {
                    if( getLesson(x-1, y) != null) {
                        if( getLesson(x-1, y).getSubjectIndex() == subject_index) {
                            result.add( getLesson(x-1, y));
                        }
                        else {
                            result.add(null);
                        }
                    }
                    else {
                        result.add(null);
                    }
                }
            }
        }
        return result;
    }

    /*
     * returns the list for the schedule adapter
     * including only the subject with the transferred index
     */
    public ArrayList<Object> getScheduleListWithoutTimes(int subject_index) {
        ArrayList<Object> result = new ArrayList<>();
        for(int y = 0; y < 9; y++) {
            for(int x = 0; x < 5; x++) {
                if( getLesson(x, y) != null) {
                    if( getLesson(x, y).getSubjectIndex() == subject_index) {
                        result.add( getLesson(x, y));
                    }
                    else {
                        result.add(null);
                    }
                }
                else {
                    result.add(null);
                }
            }
        }
        return result;
    }

    /*
     * clears all lessons (for creating a new semester)
     */
    public void clearLessons() {
        for(int y = 0; y < 9; y++) {
            for (int x = 0; x < 5; x++) {
                schedule[x][y] = null;
            }
        }
    }

    /**
     * sets the times array to an empty array
     */
    public void clearTimes() {
        times = new int[9][2][2];
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////// saving methods ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * saves the time of 'schedule' to 'des' file
     * @param des: file in which the data is written
     * @param times: data that is written to des
     * @throws IOException: writing into a file
     */
    public static void writeTimes(File des, int[][][] times) throws IOException {
        if(!des.exists()) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        for(int lesson = 0; lesson < 9; lesson++) {
            for(int when = 0; when < 2; when++) {
                for(int time_instance = 0; time_instance < 2; time_instance++) {
                    bw.write( FileSaver.data("TIM", String.valueOf(times[lesson][when][time_instance])));
                }
            }
        }
        bw.close();
    }

    /**
     * saving the transmitted lessons array (schedule) to the 'des' file
     * @param des: destination file in which the data wil be written in
     * @param lessons: data that has to be saved
     * @throws IOException: writing to a file
     */
    public static void writeLessons(File des, Lesson[][] lessons) throws IOException {
        if(!des.exists()) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(des));
        for(int day = 0; day < 5; day++) {
            for(int lesson = 0; lesson < 9; lesson++) {
                if(lessons[day][lesson] == null) {
                    bw.write( FileSaver.data("NOT", "null"));
                }
                else {
                    bw.write( FileSaver.data("ROM", lessons[day][lesson].getRoom()));
                    bw.write( FileSaver.data("SID", String.valueOf( lessons[day][lesson].getSubjectIndex())));
                    bw.write( FileSaver.data("TEA", lessons[day][lesson].getTeacher()));
                }
            }
        }
        bw.close();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// reading methods ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the schedule times from the transmitted file
     * if the file does not exist, the method returns null
     * iterating over the 922-int-array of the schedule
     * @param des: destination file from which the data is read
     * @return int[][][]: times array
     * @throws IOException: reading from a file
     */
    public static int[][][] readTimes(File des) throws IOException {
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String[] line_data;

        int[][][] result = new int[9][2][2];
        for(int lesson = 0; lesson < 9; lesson++) {
            for(int when = 0; when < 2; when++) {
                for(int time_instance = 0; time_instance < 2; time_instance++) {
                    if((line_data = FileLoader.getLineData(br.readLine())) == null) {
                        return null;
                    }
                    result[lesson][when][time_instance] = Integer.parseInt(line_data[1]);
                }
            }
        }

        return result;
    }

    /**
     * reads the lesson array from the transmitted file
     * if the file does not exist, the method returns null
     * iterating over an empty schedule (59-array) and reading the matching data
     * if one line = null or "" the method stops
     * @param des: destination file from which the data is read
     * @return Lesson[][]: schedule
     * @throws IOException: reading from a file
     */
    public static Lesson[][] readLessons(File des) throws IOException {
        if(!des.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(des));
        String[] line_data;
        Lesson l;

        Lesson[][] result = new Lesson[5][9];
        for(int day = 0; day < 5; day++) {
            for(int lesson = 0; lesson < 9; lesson++) {
                if((line_data = FileLoader.getLineData(br.readLine())) == null) {
                    return null;
                }

                l = new Lesson();
                if(line_data[0].equals("NOT")) {
                    l = null;
                    Log.d("Schedule", "reading lessons: " + day + "|" + lesson + " = null");
                }
                else {
                    l.setRoom( line_data[1]);

                    line_data = FileLoader.getLineData(br.readLine());
                    l.setSubjectIndex( Integer.parseInt(line_data[1]));

                    line_data = FileLoader.getLineData(br.readLine());
                    l.setTeacher( line_data[1]);

                    l.setDay(day);
                    l.setTime(lesson);

                    Log.d("Schedule", "reading lessons: " + day + "|" + lesson
                            + " = [" + l.getRoom() + ", " + l.getSubjectIndex() + ", " + l.getTeacher() + "]");
                }
                result[day][lesson] = l;
            }
        }

        return result;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////// Lessons: Getter & Setter ///////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public Lesson getLesson(int day, int lessonCount) {
        return schedule[day][lessonCount];
    }
    public Lesson[][] getLessons() { return schedule; }

    /**
     * iterates through the schedule and sets the lessons
     * via {@link this#setLesson(Lesson)} & {@link this#setLessonToNull(int, int)}
     * @param lessons
     */
    public void setLessons(Lesson[][] lessons) {
        for(int day = 0; day < 5; day++) {
            for(int l = 0; l < 9; l++) {
                if(lessons[day][l] != null) {
                    setLesson(lessons[day][l]);
                }
                else {
                    setLessonToNull(day, l);
                }
            }
        }
    }

    /**
     * reduces the previous subject lesson amount by 1
     * and increases the new one by 1
     * @param lesson: new lesson (contains day and time)
     */
    public void setLesson(Lesson lesson) {
        int d = lesson.getDay();
        int l = lesson.getTime();
        if(schedule[d][l] != null) {
            int sid = schedule[d][l].getSubjectIndex();
            Storage.subjects.get(sid).subLessonAmount();
        }
        schedule[lesson.getDay()][lesson.getTime()] = lesson;
        if(schedule[d][l] != null) {
            int sid = schedule[d][l].getSubjectIndex();
            if(Storage.subjects.size() <= sid) {
                return;
            }
            Storage.subjects.get(sid).addLessonAmount();
        }
    }

    /**
     * reduces the previous subject lesson amount by 1
     * @param day: day number of the lesson that will be deleted
     * @param lessonCount: lesson number of the lesson that will be deleted
     */
    public void setLessonToNull(int day, int lessonCount) {
        if(schedule[day][lessonCount] != null) {
            int sid = schedule[day][lessonCount].getSubjectIndex();
            Storage.subjects.get(sid).subLessonAmount();
        }
        schedule[day][lessonCount] = null;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// Times: Getter & Setter ////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public int getTime(int lessonCount, int when, int time_instance) { return times[lessonCount][when][time_instance]; }
    public int[] getTime(int lessonCount, int when) {
        return times[lessonCount][when];
    }
    public int[][] getTime(int lessonCount) { return times[lessonCount]; }
    public int[][][] getTime() { return times; }

    public void setTime(int lessonCount, int when, int time_instance, int value) {
        times[lessonCount][when][time_instance] = value;
    }
    public void setTime(int lessonCount, int when, int[] time) {
        times[lessonCount][when] = time;
    }
    public void setTime(int lessonCount, int[] from, int[] to) {
        times[lessonCount][0] = from;
        times[lessonCount][1] = to;
    }
    public void setTimes(int[][][] times) {
        for(int i = 0; i < 9; i++) {
            setTime(i, times[i][0], times[i][1]);
        }
    }
}

package mc.wombyte.marcu.jhp_app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Classes.HomeworkDate;
import mc.wombyte.marcu.jhp_app.Classes.Schedule;
import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Classes.Subject;

/**
 * Created by marcu on 05.07.2017.
 */

public class Storage {

    static boolean read_already = false;

    public static final int ALL_SUBJECTS = -2;

    //permission
    public static boolean WRITE_EXTERNAL_STORAGE_ALLOWED = false;
    public static boolean READ_EXTERNAL_STORAGE_ALLOWED = false;
    public static boolean INTERNET_ALLOWED = false;
    public static boolean CAMERA_ALLOWED = false;

    public static ArrayList<Semester> semester = new ArrayList<>();
    public static String current_semester = "";
    public static ArrayList<Subject> subjects = new ArrayList<>();
    public static Schedule schedule = new Schedule();
    public static ArrayList<ArrayList<Homework>> homework = new ArrayList<>();
    public static ArrayList<ArrayList<Grade>> grades = new ArrayList<>();
    public static Settings settings = new Settings();


    //******************************************************* subjects *******************************************************//
    /*
     * creating a new subject and the belonging lists for
     * grades and homework
     */
    public static void addSubject(Subject subject) {
        subjects.add(subject);
        homework.add(new ArrayList<Homework>());
        grades.add(new ArrayList<Grade>());
    }

    /*
     * deletes the subject with the index i and all itema
     * belonging to it: lessons, homework, grades
     */
    public static void deleteSubject(int i) {
        //delete grades
        grades.remove(i);
        for(ArrayList<Grade> list: grades) {
            for(Grade grade: list) {
                if(grade.getSubjectindex() > i) {
                    FileSaver.renameGradeFolder(grade, FileSaver.GRADE_MINUS_SUBJECT_INDEX);
                    grade.setSubjectIndex(grade.getSubjectindex()-1);
                }
            }
        }

        //delete homework
        homework.remove(i);
        for(ArrayList<Homework> list: homework) {
            for(Homework homework: list) {
                if(homework.getSubjectindex() > i) {
                    FileSaver.renameHomeworkFolder(homework, FileSaver.HOMEWORK_MINUS_SUBJECT_INDEX);
                    homework.setSubjectIndex(homework.getSubjectindex()-1);
                }
            }
        }

        //delete lessons
        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 5; y++) {
                if(schedule.getLesson(x, y) != null) {
                    if (schedule.getLesson(x, y).getSubjectIndex() >= i) {
                        if (schedule.getLesson(x, y).getSubjectIndex() == i) {
                            schedule.setLesson(x, y, null);
                        }
                        else {
                            schedule.getLesson(x, y).setSubjectIndex(schedule.getLesson(x, y).getSubjectIndex()-1);
                        }
                    }
                }
            }
        }

        //delete subject
        subjects.remove(i);
    }

    /*
     * @return: a list of all different colors among the subjects
     */
    public static ArrayList<Integer> getSubjectsColorList() {
        ArrayList<Integer> result = new ArrayList<>();

        for(Subject subject: subjects) {
            Integer color = subject.getColor();
            if(!result.contains(color)) {
                result.add(color);
            }
        }

        return result;
    }

    /*
     * changes the order of all subjects, homework and grades
     * @param 'id': selected id, that defines how the lists should be reordered
     *
    public static void changeSubjectsOrder(byte id) {
        ArrayList<Integer> index_order;

        switch(id) {
            case Settings.SUBJECT_ORDER_ALPHABETICALLY_ASC:
                index_order = getSubjectOrderAlphabetically(true);
                break;
            case Settings.SUBJECT_ORDER_ALPHABETICALLY_DESC:
                index_order = getSubjectOrderAlphabetically(false);
                break;
            case Settings.SUBJECT_ORDER_AVERAGE_ASC:
                index_order = getSubjectOrderAverage(true);
                break;
            case Settings.SUBJECT_ORDER_AVERAGE_DESC:
                index_order = getSubjectOrderAverage(false);
                break;
            default: index_order = new ArrayList<>();
        }

        if(indexOrderRemains(index_order)) {
            changeScheduleIndices(index_order);
            changeSubjectOrder(index_order);
            changeHomeworkIndices(index_order);
            changeGradeIndices(index_order);
        }
    }*/

    /*
     * @return: the index order of the subject list being ordered alphabetically
     * @param 'asc': defines whether the list should be ordered ascending
     *
     * saves all the subject names in a list
     * rearranges the list ascending/descending
     * saves the indices of the subjects in the result list
     *
    private static ArrayList<Integer> getSubjectOrderAlphabetically(final boolean asc) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for(Subject subject: subjects) {
            names.add(subject.name);
        }

        names.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if(asc) {
                    return s1.compareToIgnoreCase(s2);
                }
                else {
                    return s2.compareToIgnoreCase(s1);
                }
            }
        });

        for(int i = 0; i < names.size(); i++) {
            int id = 0;
            for(Subject subject: subjects) {
                if(names.get(i).equals(subject.name)) {
                    id = subject.index;
                }
            }
            result.add(id);
        }

        return result;
    }*/

    /*
     * @return: the index order of the subject list being ordered alphabetically
     * @param 'asc': defines whether the list should be ordered ascending
     *
     * saves all the subject names in a list
     * rearranges the list ascending/descending
     * saves the indices of the subjects in the result list
     *
    private static ArrayList<Integer> getSubjectOrderAverage(boolean asc) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Subject> subjects_clone = (ArrayList<Subject>) subjects.clone();
        double xaverage;
        int id = 0;

        for(int i = 0; i < subjects_clone.size(); i++) {
            xaverage = asc ? 16.0 : -1.0;
            for(Subject subject: subjects_clone) {
                if(asc) {
                    if(subject.average <= xaverage) {
                        xaverage = subject.average;
                        id = subject.index;
                    }
                }
                else {
                    if(subject.average >= xaverage) {
                        xaverage = subject.average;
                        id = subject.index;
                    }
                }
            }
            subjects_clone.remove(id);
            result.add(id);
        }

        return result;
    }*/

    /*
     * @return: checks whether the transmitted index order is the same as the current
     * @param 'index_order': new index order
     */
    private static boolean indexOrderRemains(ArrayList<Integer> index_order) {
        for(int i = 0; i < index_order.size(); i++) {
            if(index_order.get(i) != subjects.get(i).getIndex()) {
                return false;
            }
        }
        return true;
    }

    /*
     * changes the indices of the lessons according to the new subject_order
     * @param 'index_order': new order of the subjects
     */
    private static void changeScheduleIndices(ArrayList<Integer> index_order) {
        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 9; y++) {
                if(schedule.getLesson(x, y) != null) {
                    int newid = index_order.indexOf(schedule.getLesson(x, y).getSubjectIndex());
                    schedule.getLesson(x, y).setSubjectIndex(newid);
                }
            }
        }
    }

    /*
     * changes the indices of the subjects according to the new subject_order
     * @param 'index_order': new order of the subjects
     */
    private static void changeSubjectOrder(ArrayList<Integer> index_order) {
        for(Subject subject: subjects) {
            subject.setIndex(index_order.indexOf(subject.getIndex()));
        }
    }

    /*
     * changes the order of the homework lists and the homework indices
     * @param 'index_order': new order of the subjects
     */
    private static void changeHomeworkIndices(ArrayList<Integer> index_order) {
        ArrayList<ArrayList<Homework>> clone = new ArrayList<>(homework.size());

        for(int i = 0; i < homework.size(); i++) {
            int newid = index_order.indexOf(i);
            clone.add(newid, homework.get(i));
            for(Homework h: clone.get(newid)) {
                h.setIndex(newid);
            }
        }

        homework = (ArrayList<ArrayList<Homework>>) clone.clone();
    }

    /*
     * changes the order of the grade lists and the homework indices
     * @param 'index_order': new order of the subjects
     */
    private static void changeGradeIndices(ArrayList<Integer> index_order) {
        ArrayList<ArrayList<Grade>> clone = new ArrayList<>(grades.size());

        for(int i = 0; i < grades.size(); i++) {
            int newid = index_order.indexOf(i);
            clone.add(newid, grades.get(i));
            for(Grade g: clone.get(newid)) {
                g.setIndex(newid);
            }
        }

        grades = (ArrayList<ArrayList<Grade>>) clone.clone();
    }

    //******************************************************* homework *******************************************************//
    /*
     * returns a well ordered list of the upcoming homework
     * deletes all the past homework
     */
    public static ArrayList<Homework> getHomeworkList() {
        ArrayList<Homework> result = new ArrayList<>();
        int count;

        //goes thru all homework in every subject
        for(ArrayList<Homework> subject_list: Storage.homework) {
            for(Homework homework: subject_list) {
                count = 0;
                if(result.size() != 0) {
                    while(homework.getDate().after(result.get(count).getDate())) {
                        count++;
                        if(count == result.size()) {
                            break;
                        }
                    }
                }
                if(settings.homework_isShowDoneHomework() || !homework.getSolution().isFinished()) {
                    result.add(count, homework);
                }
            }
        }
        return result;
    }

    /*
     * returns a well ordered list of the upcoming homework
     */
    public static ArrayList<Homework> getHomeworkList(int subject_index) {
        ArrayList<Homework> result = new ArrayList<>();
        int count;

        //goes thru all homework in this subject
        for(Homework homework: Storage.homework.get(subject_index)) {
            count = 0;

            if(homework.isPast()) {
                homework.getSolution().setState(true);
            }

            if(result.size() != 0) {
                while(homework.getDate().after(result.get(count).getDate())) {
                    count++;
                    if(count == result.size()) {
                        break;
                    }
                }
            }
            if(settings.homework_isShowDoneHomework() || !homework.getSolution().isFinished()) {
                result.add(count, homework);
            }
        }

        return result;
    }

    /*
     * fills the title_strings dates with the upcoming lessons of the specific subject
     */
    public static ArrayList<HomeworkDate> getFutureDates(int subject_index) {
        Calendar c = Calendar.getInstance();
        c.getTime();
        ArrayList<HomeworkDate> result = new ArrayList<>();
        ArrayList<Integer> day_difference = new ArrayList<>();
        ArrayList<Integer> lessons_count = new ArrayList<>();
        int current_day = schedule.getTodaysNumber();
        int last_day = current_day;
        boolean end;
        int dif;

        //getting the difference (in days) for the next lessons in 4 weeks
        int days = Storage.settings.homework_getNumberOfWeeks() * 5;
        for(int i = 0; i < days; i++) {
            current_day++;
            if(current_day > 4)
                current_day = 0;
            end = false;
            for(int y = 0; y < 9 && !end; y++) {
                if(Storage.schedule.getLesson(current_day, y) != null) {
                    if(Storage.schedule.getLesson(current_day, y).getSubjectIndex() == subject_index) {
                        if((dif = current_day - last_day) <= 0) {
                            dif += 7;
                        }
                        day_difference.add(dif);
                        lessons_count.add(y+1);
                        last_day = current_day;
                        end = true;
                    }
                }
            }
        }

        //deleting the measurements below day
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        //writing the correct dates
        for(int day = 0; day < day_difference.size(); day++) {
            c.add(Calendar.DATE, day_difference.get(day));
            result.add(new HomeworkDate(c.getTime(), lessons_count.get(day)));
        }
        return result;
    }

    /*
     * returns the id of the last subject the user had according to the schedule
     */
    public static int getLastSubjectId() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        int day;
        int lesson = -1;
        boolean end = false;

        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyy HH:mm", Locale.GERMAN);

        if((day = schedule.getTodaysNumber()) < 5) {
            for(int i = 0; i < 9 && !end; i++) {
                c.set(Calendar.HOUR_OF_DAY, Storage.schedule.getTime(i, 0, 0));
                c.set(Calendar.MINUTE, Storage.schedule.getTime(i, 0, 0));
                if(now.before(c.getTime())) {
                    lesson = i-1;
                    end = true;
                }
            }
            if(lesson == -1) {
                return -1;
            }
            if(Storage.schedule.getLesson(day, lesson) == null) {
                return -1;
            }
            return Storage.schedule.getLesson(day, lesson).getSubjectIndex();
        }
        else {
            return -1;
        }
    }

    /*
     * adds a new homework to the transferred homework list
     */
    public static void addHomework(int subject_index, Homework h) {
        homework.get(subject_index).add(h);
    }

    /*
     * deletes the homework at the specific position
     * and shifts all indices fo the following homework
     */
    public static void deleteHomework(Homework h) {
        int subject_index = h.getSubjectindex();
        int homework_index = h.getIndex();

        FileSaver.deleteHomework(homework.get(subject_index).get(homework_index));
        homework.get(subject_index).remove(homework_index);

        for(int i = homework_index; i < Storage.homework.get(subject_index).size(); i++) {
            FileSaver.renameHomeworkFolder(Storage.homework.get(subject_index).get(i), FileSaver.HOMEWORK_MINUS_HOMEWORK_INDEX);
            Storage.homework.get(subject_index).get(i).setIndex(i);
        }
    }

    /*
     * clears all grades (important for creating a new semester)
     */
    public static void clearHomework() {
        for(int i = 0; i < homework.size(); i++) {
            homework.get(i).clear();
        }
    }

    //******************************************************* grades *******************************************************//
    /*
     * adds a new grade to the transferred grade list
     * and calculates the new average of the belonging subject
     */
    public static void addGrade(int subject_index, Grade grade) {
        grade.setIndex( Storage.grades.get(subject_index).size());
        grades.get(subject_index).add(grade);
        subjects.get(subject_index).calculateAverage();
    }

    /*
     * deletes a grade from the transferred list
     * and calculates the new average
     */
    public static void deleteGrade(Grade grade) {
        int subject_index = grade.getSubjectindex();
        int grade_index = grade.getIndex();
        grades.get(subject_index).remove(grade_index);
        for(int i = grade_index; i < grades.get(subject_index).size(); i++) {
            FileSaver.renameGradeFolder( Storage.grades.get(subject_index).get(i), FileSaver.GRADE_MINUS_GRADE_INDEX);
            grades.get(subject_index).get(i).setIndex(i);
        }
        subjects.get(subject_index).calculateAverage();
    }

    /*
     * clears all grades (important for creating a new semester)
     */
    public static void clearGrades() {
        for(int i = 0; i < grades.size(); i++) {
            grades.get(i).clear();
        }
        for(int i = 0; i < subjects.size(); i++) {
            subjects.get(i).setAverage(0);
        }
    }

    /*
     * calculates the average of all grades included in the subject
     * that are below the "end" index
     * this method is only used to create the grade-diagram
     */
    public static double calculateAverage(int subject_index, int end) {
        int grades_amount = 0;
        int exam_amount = 0;
        double grades_average = 0;
        double exam_average = 0;
        ArrayList<Grade> list;

        //defining the right list of grades
        if(subject_index != ALL_SUBJECTS) {
            list = grades.get(subject_index);
        }
        else {
            list = getGradeList();
        }

        if (list.size() == 0) {
            return 0;
        }

        for(int i = 0; i <= end; i++) {
            if(list.get(i).isExam()) {
                exam_amount++;
                exam_average += list.get(i).getNumber();
            }
            else {
                grades_amount++;
                grades_average += list.get(i).getNumber();
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
        return exam_average * 1/3 + grades_average * 2/3;
    }

    /*
     * returns a list of all grades in all subjects
     * it is ordered according to their got_date
     */
    public static ArrayList<Grade> getGradeList() {
        ArrayList<Grade> result = new ArrayList<>();

        int index = 0;
        for(ArrayList<Grade> list: grades) {
            for(Grade grade: list) {
                index = 0;
                if(result.size() > 0) {
                    while (index < result.size() && grade.getGotDate().after(result.get(index).getGotDate())) {
                        index++;
                    }
                }
                result.add(index, grade);
            }
        }

        return result;
    }

    /*
     * return a gridview list of all grades
     */
    public static ArrayList<Grade> getGradeGridViewList() {
        ArrayList<Grade> result = new ArrayList<>();
        for(ArrayList<Grade> list: grades) {
            for(int i = 0; i < getMaxAmountGrades()+2; i++) {
                //at pos 0 in each row the subject abbreviation is located
                if(i == 0) {
                    result.add(null);
                }
                //if there is no grade in the list any more, null is added
                else {
                    if(i-1 < list.size()) {
                        result.add(list.get(i-1));
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
     * fills the title_strings dates with the last lessons of the specific subject
     */
    public static ArrayList<Date> getPastDates(int subject_index) {
        Calendar c = Calendar.getInstance();
        c.getTime();
        ArrayList<Date> result = new ArrayList<>();
        ArrayList<Integer> day_difference = new ArrayList<>();
        int current_day = Schedule.getTodaysNumber();
        int last_day = current_day;
        boolean end;
        int dif;

        if(current_day > 4) {
            current_day = 4;
        }

        //getting the difference (in days) for the next lessons in 4 weeks
        int days = settings.grades_getNumberOfWeeks() * 5;
        for(int i = 0; i < days; i++) {
            if(current_day < 0)
                current_day = 4;
            end = false;
            for(int y = 0; y < 9 && !end; y++) {
                if(Storage.schedule.getLesson(current_day, y) != null) {
                    if(Storage.schedule.getLesson(current_day, y).getSubjectIndex() == subject_index) {
                        if((dif = last_day - current_day) <= 0) {
                            dif += 7;
                            if(day_difference.size() == 0 && dif == 7) {
                                dif = 0;
                            }
                        }
                        day_difference.add(dif);
                        last_day = current_day;
                        end = true;
                    }
                }
            }
            current_day--;
        }

        //deleting the measurements below day
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        //writing the correct dates
        for(int day = 0; day < day_difference.size(); day++) {
            c.add(Calendar.DATE, -day_difference.get(day));
            result.add(c.getTime());
        }
        return result;
    }

    /*
     * returns the amount of the most grades in a subject
     * if the number is less than 5, 5 is returned
     */
    public static int getMaxAmountGrades() {
        int max = 0;
        for(ArrayList<Grade> list: grades) {
            if(list.size() > max) {
                max = list.size();
            }
        }
        return Math.max(5, max);
    }

    /*
     * returns the highest grade in the specific subject
     * if a option_index is translated, its grade list is searched thru
     * else a list of grades of all subjects is searched thru
     */
    public static int getHeighestGrade(int subject_index) {
        //defining the right list of grades
        ArrayList<Grade> list;
        if(subject_index != ALL_SUBJECTS) {
            list = grades.get(subject_index);
        }
        else {
            list = getGradeList();
        }

        //getting highest grade of list
        int max = 0;
        if(list.size() == 0) {
            if(settings.grades_isRatingInGrades()) {
                return 6;
            }
            else {
                return 15;
            }
        }
        for(Grade grade: list) {
            if(grade.getNumber() > max) {
                max = grade.getNumber();
            }
        }
        return max;
    }

    /*
     * returns the lowest grade in the specific subject
     */
    public static int getLowestGrade(int subject_index) {
        //defining the right list of grades
        ArrayList<Grade> list;
        if(subject_index != ALL_SUBJECTS) {
            list = grades.get(subject_index);
        }
        else {
            list = getGradeList();
        }

        //getting highest grade of list

        int min = 15;
        if(list.size() == 0) {
            if(settings.grades_isRatingInGrades()) {
                return 1;
            }
            else {
                return 0;
            }
        }
        for(Grade grade: list) {
            if(grade.getNumber() < min) {
                min = grade.getNumber();
            }
        }
        return min;
    }


    //******************************************************* semesters *******************************************************//
    /*
     * finds the heighest semester and set it to the current semester
     */
    public static void setHighestSemester() {
        if(!current_semester.equals("")) {
            return;
        }

        Semester max = new Semester("0");
        for(int i = 0; i < semester.size(); i++) {
            int s1 = Integer.parseInt(semester.get(i).getName());
            int m = Integer.parseInt(max.getName());
            if(s1 > m) {
                max = semester.get(i);
            }
        }
        current_semester = max.getName();
    }

}

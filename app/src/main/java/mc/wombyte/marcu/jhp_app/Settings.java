package mc.wombyte.marcu.jhp_app;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Semester;

/**
 * Created by marcu on 08.10.2017.
 */

public class Settings extends SettingGroup {

    //final values to save the ViewId for the ViewSwitcher
    public static final int SETTINGS_VIEW_FRAGMENT = 0;
    public static final int SETTINGS_VIEW_SWITCH = 1;
    public static final int SETTINGS_VIEW_DIALOG = 2;
    public static final int SETTINGS_VIEW_SPACE = 3;

    //final values for general_menu
    public static final int LEFT_HAND_SIDE = 0;
    public static final int RIGHT_HAND_SIDE = 1;
    public static final byte SUBJECT_ORDER_ALPHABETICALLY_ASC = 0;
    public static final byte SUBJECT_ORDER_ALPHABETICALLY_DESC = 1;
    public static final byte SUBJECT_ORDER_AVERAGE_ASC = 2;
    public static final byte SUBJECT_ORDER_AVERAGE_DESC = 3;

    //schedule
    private static SettingGroup g_schedule;
    private static SettingField f_schedule_show_days;
    private static SettingField f_schedule_show_times;
    private static SettingGroup g_schedule_set_times;
    private static SettingField f_schedule_time1;
    private static SettingField f_schedule_time2;
    private static SettingField f_schedule_time3;
    private static SettingField f_schedule_time4;
    private static SettingField f_schedule_time5;
    private static SettingField f_schedule_time6;
    private static SettingField f_schedule_time7;
    private static SettingField f_schedule_time8;
    private static SettingField f_schedule_time9;

    private Settings_schedule_times_fragment schedule_time_fragment1 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment2 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment3 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment4 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment5 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment6 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment7 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment8 = new Settings_schedule_times_fragment();
    private Settings_schedule_times_fragment schedule_time_fragment9 = new Settings_schedule_times_fragment();

    private static final String SCHEDULE_SHOW_DAYS = "TSD";
    private static final String SCHEDULE_SHOW_TIMES = "TST";

    //subjects
    private static SettingGroup g_subjects;
    private static SettingField f_subjects_view;
    private static SettingField f_subjects_color;
    private static SettingField f_subjects_average;
    private static SettingField f_subjects_arrangement;

    private static final String SUBJECTS_VIEW = "SVI";
    private static final String SUBJECTS_COLOR = "SCO";
    private static final String SUBJECTS_AVERAGE = "SAV";
    private static final String SUBJECTS_ARRANGEMENT = "SAR";

    //homework
    private static SettingGroup g_homework;
    private static SettingField f_homework_view;
    private static SettingField f_homework_show_done;
    private static SettingField f_homework_weeks;
    private static SettingField f_homework_arrangement;

    private static final String HOMEWORK_VIEW = "HVI";
    private static final String HOMEWORK_SHOWDONE = "HSD";
    private static final String HOMEWORK_WEEKS = "HWE";
    private static final String HOMEWORK_ARRANGEMENT = "HAR";

    //grades
    private static SettingGroup g_grades;
    private static SettingField f_grades_view;
    private static SettingField f_grades_average;
    private static SettingField f_grades_weeks;
    private static SettingField f_grades_add_grade;

    private static final String GRADES_VIEW = "GVI";
    private static final String GRADES_RATING = "GRA";
    private static final String GRADES_AVERAGE = "GAV";
    private static final String GRADES_WEEKS = "GWE";
    private static final String GRADES_ADDGRADE = "GAG";

    //general
    private static SettingGroup g_general;
    private static SettingField f_general_menu;
    private static SettingField f_general_setup;
    private static SettingField f_general_backup;

    private static final String GENERAL_MENU = "AMN";
    private static final String GENERAL_SETUP = "ASP";
    private static final String GENERAL_BACKUP = "ABP";

    private SettingField f_about;

    public Settings() {
        setFragmentToThis();
        refresh();
    }

    /**
     * creates all the settings, including typ, description, symbol and all fragments
     */
    public SettingGroup refresh() {
        System.out.println("settings are refreshed");
        //******************************************************* creating structures *******************************************************//
        this.setDescriptionId(R.string.settings_heading);

        //schedule
        g_schedule = new SettingGroup(R.string.settings_schedule, R.drawable.symbol_schedule);

        f_schedule_show_days = new SettingField(SCHEDULE_SHOW_DAYS, SettingField.BOOLEAN, R.string.settings_schedule_show_days, R.drawable.symbol_settings_show);
        f_schedule_show_days.setValue(true);
        f_schedule_show_times = new SettingField(SCHEDULE_SHOW_TIMES, SettingField.BOOLEAN, R.string.settings_schedule_show_times, R.drawable.symbol_settings_show);
        f_schedule_show_times.setValue(true);
        g_schedule_set_times = new SettingGroup(R.string.settings_schedule_set_times, R.drawable.symbol_settings_settime);
        f_schedule_time1 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time1, R.drawable.symbol_setting_schedule_time1);
        schedule_time_fragment1.setLessonCount(0);
        f_schedule_time1.setFragment(schedule_time_fragment1);
        f_schedule_time2 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time2, R.drawable.symbol_setting_schedule_time2);
        schedule_time_fragment2.setLessonCount(1);
        f_schedule_time2.setFragment(schedule_time_fragment2);
        f_schedule_time3 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time3, R.drawable.symbol_setting_schedule_time3);
        schedule_time_fragment3.setLessonCount(2);
        f_schedule_time3.setFragment(schedule_time_fragment3);
        f_schedule_time4 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time4, R.drawable.symbol_setting_schedule_time4);
        schedule_time_fragment4.setLessonCount(3);
        f_schedule_time4.setFragment(schedule_time_fragment4);
        f_schedule_time5 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time5, R.drawable.symbol_setting_schedule_time5);
        schedule_time_fragment5.setLessonCount(4);
        f_schedule_time5.setFragment(schedule_time_fragment5);
        f_schedule_time6 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time6, R.drawable.symbol_setting_schedule_time6);
        schedule_time_fragment6.setLessonCount(5);
        f_schedule_time6.setFragment(schedule_time_fragment6);
        f_schedule_time7 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time7, R.drawable.symbol_setting_schedule_time7);
        schedule_time_fragment7.setLessonCount(6);
        f_schedule_time7.setFragment(schedule_time_fragment7);
        f_schedule_time8 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time8, R.drawable.symbol_setting_schedule_time8);
        schedule_time_fragment8.setLessonCount(7);
        f_schedule_time8.setFragment(schedule_time_fragment8);
        f_schedule_time9 = new SettingField("", SettingField.NONE, R.string.settings_schedule_time9, R.drawable.symbol_setting_schedule_time9);
        schedule_time_fragment9.setLessonCount(8);
        f_schedule_time9.setFragment(schedule_time_fragment9);

        //subjects
        g_subjects = new SettingGroup(R.string.settings_subjects, R.drawable.symbol_subjects);

        f_subjects_view = new SettingField(SUBJECTS_VIEW, SettingField.BOOLEAN, R.string.settings_subjects_view, R.drawable.symbol_settings_view);
        f_subjects_view.addLongDescription(R.string.settings_subjects_view_long);
        f_subjects_view.setValue(true);
        f_subjects_color = new SettingField(SUBJECTS_COLOR, SettingField.BOOLEAN, R.string.settings_subjects_color, R.drawable.symbol_settings_color);
        f_subjects_color.addLongDescription(R.string.settings_subjects_color_long);
        f_subjects_color.setValue(true);
        f_subjects_average = new SettingField(SUBJECTS_AVERAGE, SettingField.INTEGER, R.string.settings_subjects_average, R.drawable.symbol_settings_average);
        f_subjects_average.setLongDescriptionId(R.string.settings_subjects_average_long);
        f_subjects_average.setBounds(1, 2);
        f_subjects_average.setValue(2);
        f_subjects_arrangement = new SettingField(SUBJECTS_ARRANGEMENT, SettingField.BYTE, R.string.settings_subjects_arrangement, R.drawable.symbol_settings_arrangement);
        f_subjects_arrangement.setLongDescriptionId(R.string.settings_subejcts_arrangement_long);
        f_subjects_arrangement.setArrayId(R.array.settings_subjects_arrangement_options);
        f_subjects_arrangement.setValue((byte) 0);
        f_subjects_arrangement.setOnOptionChangeListener(new SettingField.OptionChangeListener() {
            @Override public void onOptionChange(byte id) {
                //Storage.changeSubjectsOrder(id);
            }
        });

        //homework
        g_homework = new SettingGroup(R.string.settings_homework, R.drawable.symbol_homework);

        f_homework_view = new SettingField(HOMEWORK_VIEW, SettingField.BOOLEAN, R.string.settings_homework_view, R.drawable.symbol_settings_view);
        f_homework_view.addLongDescription(R.string.settings_homework_view_long);
        f_homework_view.setValue(true);
        f_homework_show_done = new SettingField(HOMEWORK_SHOWDONE, SettingField.BOOLEAN, R.string.settings_homework_show_done, R.drawable.symbol_settings_show);
        f_homework_show_done.setValue(true);
        f_homework_weeks = new SettingField(HOMEWORK_WEEKS, SettingField.INTEGER, R.string.settings_homework_weeks, R.drawable.symbol_settings_weeks);
        f_homework_weeks.addLongDescription(R.string.settings_homework_weeks_long);
        f_homework_weeks.setBounds(1, 8);
        f_homework_weeks.setValue(4);
        f_homework_arrangement = new SettingField(HOMEWORK_ARRANGEMENT, SettingField.BYTE, R.string.settings_homework_arrangement, R.drawable.symbol_settings_arrangement);
        f_homework_arrangement.addLongDescription(R.string.settings_homework_arrangement_long);
        f_homework_arrangement.setArrayId(R.array.settings_homework_arrangement_options);
        f_homework_arrangement.setValue((byte) 0);
        f_homework_arrangement.setOnOptionChangeListener(new SettingField.OptionChangeListener() {
            @Override public void onOptionChange(byte id) {
                //TODO: arrange homework
            }
        });

        //grades
        g_grades = new SettingGroup(R.string.settings_grades, R.drawable.symbol_grades);

        f_grades_view = new SettingField(GRADES_VIEW, SettingField.BOOLEAN, R.string.settings_grades_view, R.drawable.symbol_settings_view);
        f_grades_view.addLongDescription(R.string.settings_grades_view_long);
        f_grades_view.setValue(true);
        f_grades_weeks = new SettingField(GRADES_WEEKS, SettingField.INTEGER, R.string.settings_grades_weeks, R.drawable.symbol_settings_weeks);
        f_grades_weeks.addLongDescription(R.string.settings_grades_weeks_long);
        f_grades_weeks.setBounds(1, 8);
        f_grades_weeks.setValue(4);
        f_grades_add_grade = new SettingField(GRADES_ADDGRADE, SettingField.BOOLEAN, R.string.settings_grades_add_grade, R.drawable.symbol_grades_add);
        f_grades_add_grade.addLongDescription(R.string.settings_grades_add_grade_long);
        f_grades_add_grade.setValue(true);
        f_grades_average = new SettingField(GRADES_AVERAGE, SettingField.INTEGER, R.string.settings_grades_average, R.drawable.symbol_settings_average);
        f_grades_average.setBounds(1, 2);
        f_grades_average.setValue(2);

        //general
        g_general = new SettingGroup(R.string.settings_general, R.drawable.symbol_settings_general);

        f_general_menu = new SettingField(GENERAL_MENU, SettingField.INTEGER_ARRAY, R.string.settings_general_scrollmenu, R.drawable.symbol_settings_menu);
        f_general_menu.addLongDescription(R.string.settings_general_scrollmenu_long);
        f_general_menu.setFragment(new Settings_menu_fragment().inSettingActivity());
        f_general_menu.setValue(new int[] {0, 0, 0, LEFT_HAND_SIDE});
        f_general_setup = new SettingField(GENERAL_SETUP, SettingField.BOOLEAN, R.string.settings_general_setup, R.drawable.symbol_setting_setup);
        f_general_setup.setLongDescriptionId(R.string.settings_general_setup_long);
        f_general_setup.setValue(true);
        f_general_backup = new SettingField(GENERAL_BACKUP, SettingField.BOOLEAN, R.string.settings_general_backup, R.drawable.symbol_backup);
        f_general_backup.addLongDescription(R.string.settings_general_backup_long);

        //about the app
        f_about = new SettingField("", SettingField.NONE, R.string.settings_about, R.drawable.symbol_settings_about);
        f_about.setFragment(new Settings_info_fragment());

        //******************************************************* add structures  *******************************************************//
        //******************** have to be written in that order and after the creation to create the right paths ********************
        this.child_settings = new ArrayList<>();
        this.addChild(g_schedule);
        {
            g_schedule.addChild(f_schedule_show_days);
            g_schedule.addChild(f_schedule_show_times);
            g_schedule.addChild(g_schedule_set_times);
            {
                g_schedule_set_times.addChild(f_schedule_time1);
                g_schedule_set_times.addChild(f_schedule_time2);
                g_schedule_set_times.addChild(f_schedule_time3);
                g_schedule_set_times.addChild(f_schedule_time4);
                g_schedule_set_times.addChild(f_schedule_time5);
                g_schedule_set_times.addChild(f_schedule_time6);
                g_schedule_set_times.addChild(f_schedule_time7);
                g_schedule_set_times.addChild(f_schedule_time8);
                g_schedule_set_times.addChild(f_schedule_time9);
            }
        }
        this.addChild(g_subjects);
        {
            g_subjects.addChild(f_subjects_view);
            g_subjects.addChild(f_subjects_color);
            g_subjects.addChild(f_subjects_average);
            g_subjects.addChild(f_subjects_arrangement);
        }
        this.addChild(g_homework);
        {
            //g_homework.addChild(f_homework_view);
            g_homework.addChild(f_homework_show_done);
            g_homework.addChild(f_homework_weeks);
            g_homework.addChild(f_homework_arrangement);
        }
        this.addChild(g_grades);
        {
            //g_grades.addChild(f_grades_view);
            g_grades.addChild(f_grades_weeks);
            g_grades.addChild(f_grades_add_grade);
            g_grades.addChild(f_grades_average);
        }
        this.addChild(g_general);
        {
            g_general.addChild(f_general_menu);
            g_general.addChild(f_general_setup);
            //g_general.addChild(f_general_backup);
        }
        this.addChild(f_about);

        return this;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// Getter ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public SettingStructure getStructureByPath(ArrayList<Integer> path) {
        SettingStructure result = this;
        for(Integer i: path) {
            if(result instanceof SettingGroup) {
                result = ((SettingGroup) result).child_settings.get(i);
            }
        }
        return result;
    }

    //schedule
    public boolean schedule_daysShown() { return f_schedule_show_days.getBooleanValue(); }
    public boolean schedule_timesShown() { return f_schedule_show_times.getBooleanValue(); }

    //subjects
    public boolean subjects_isShowInList() { return f_subjects_view.getBooleanValue(); }
    public boolean subjects_calculateColorsSeperated() { return f_subjects_color.getBooleanValue(); }
    public DecimalFormat subjects_getAverageDecimalFormat() {
        String format = "#0.0";
        for(int i = 0; i < f_subjects_average.getIntValue()-1; i++) {
            format += "#";
        }
        return new DecimalFormat(format);
    }
    public int[] subjects_getArrangement() { return f_subjects_arrangement.get1DIntArrayValue(); }

    //homework
    public boolean homework_isShowInList() { return f_homework_view.getBooleanValue(); }
    public boolean homework_isShowDoneHomework() { return f_homework_show_done.getBooleanValue(); }
    public int homework_getNumberOfWeeks() { return f_homework_weeks.getIntValue(); }
    public byte homework_getArrangement() { return f_homework_arrangement.getByteValue(); }

    //grades
    public boolean grades_isShownInList() { return f_grades_view.getBooleanValue(); }
    public boolean grades_isRatingInGrades() {
        return new Semester(Storage.current_semester).getSchoolClass() <= 10;
    }
    public int grades_getNumberOfWeeks() { return f_grades_weeks.getIntValue(); }
    public boolean grades_isSpecificGradePossible() { return f_grades_add_grade.getBooleanValue(); }
    public DecimalFormat grades_getAverageDecimalFormat() {
        String format = "#0.0";
        for(int i = 0; i < f_grades_average.getIntValue()-1; i++) {
            format += "#";
        }
        return new DecimalFormat(format);
    }

    //general
    public int[] general_getCirclePos() { return f_general_menu.get1DIntArrayValue(); }
    public boolean general_newSetup() { return f_general_setup.getBooleanValue(); }
    public boolean general_BackupNeeded() { return f_general_backup.getBooleanValue(); }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// Setter ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    //subjects
    public void subjects_setArrangement(ArrayList<Integer> list) {
        ArrayList<String> result = new ArrayList<>();
        for(int i: list) {
            result.add(String.valueOf(i));
        }
        f_subjects_arrangement.setValue(result);
    }

    //general
    public void general_setCirclePos(int[] params) { f_general_menu.setValue(params); }
    public void general_setNewSetup(boolean b) { f_general_setup.setValue(b); }
    public void general_setBackup(boolean b) { f_general_backup.setValue(b); }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// reading methods ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the settings from the transmitted file
     * iterates through all lines and matches the values
     * to the read abbreviations of the settings
     * @param des: settings.txt
     * @throws IOException: reading from a file
     */
    public static void readSettings(File des) throws IOException {
        if(!des.exists()) {
            return;
        }

        String[] line_data;
        FileReader fr = new FileReader(des);
        BufferedReader br = new BufferedReader(fr);

        while((line_data = FileLoader.getLineData(br.readLine())) != null) {
            switch (line_data[0]) {
                case SCHEDULE_SHOW_DAYS:
                    f_schedule_show_days.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case SCHEDULE_SHOW_TIMES:
                    f_schedule_show_times.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case SUBJECTS_VIEW:
                    f_subjects_view.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case SUBJECTS_COLOR:
                    f_subjects_color.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case SUBJECTS_AVERAGE:
                    f_subjects_average.setValue(Integer.parseInt(line_data[1]));
                    break;
                case SUBJECTS_ARRANGEMENT:
                    f_subjects_arrangement.setValue(Byte.parseByte(line_data[1]));
                    break;
                case HOMEWORK_VIEW:
                    f_homework_view.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case HOMEWORK_SHOWDONE:
                    f_homework_show_done.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case HOMEWORK_WEEKS:
                    f_homework_weeks.setValue(Integer.parseInt(line_data[1]));
                    break;
                case HOMEWORK_ARRANGEMENT:
                    f_homework_arrangement.setValue(Byte.parseByte(line_data[1]));
                    break;
                case GRADES_VIEW:
                    f_grades_view.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case GRADES_AVERAGE:
                    f_grades_average.setValue(Integer.parseInt(line_data[1]));
                    break;
                case GRADES_WEEKS:
                    f_grades_weeks.setValue(Integer.parseInt(line_data[1]));
                    break;
                case GRADES_ADDGRADE:
                    f_grades_add_grade.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case GENERAL_MENU:
                    f_general_menu.setValue(stringToArray(line_data[1]));
                    break;
                case GENERAL_SETUP:
                    f_general_setup.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
                case GENERAL_BACKUP:
                    f_general_backup.setValue(Boolean.parseBoolean(line_data[1]));
                    break;
            }
        }

        br.close();

    }

    /**
     * converts the the transferred String into a String list
     * if the second char is a ']' an empty list is returned
     * iterating over all elements in the string (divided by ',')
     * the last element is read separately
     * @param string: string element of the array
     * @return ArrayList<String>
     */
    private static ArrayList<String> stringToList(String string) {
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

    /**
     * converts the the transferred String into a int[]
     * the string is converted to a string arraylist
     * the list is converted to an int[]
     * @param string: String which contains the array
     * @return int[]
     */
    private static int[] stringToArray(String string) {
        ArrayList<String> stringlist = stringToList(string);
        int[] result = new int[stringlist.size()];
        for(int i = 0; i < stringlist.size(); i++) {
            result[i] = Integer.parseInt(stringlist.get(i));
        }
        return result;
    }
}

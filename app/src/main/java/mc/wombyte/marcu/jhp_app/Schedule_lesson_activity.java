package mc.wombyte.marcu.jhp_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Lesson;

/**
 * Created by marcu on 15.07.2017.
 */

public class Schedule_lesson_activity extends JHP_Activity {

    RelativeLayout container;
    ImageButton b_back;
    TextView tv_heading;
    Spinner spinner_subjects;
    EditText ed_room;
    EditText ed_teacher;

    ArrayList<String> subjects = new ArrayList<String>();
    int day, lesson_count;
    int selected_subject = 0;
    boolean allow_spinner = true;
    AdapterView adapterView;
    Class previous_class;

    final boolean mode_pl = false;
    final boolean mode_sg = true;
    boolean mode = mode_pl;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_lesson_activity);

        //initialisation
        container = (RelativeLayout) findViewById(R.id.container_lesson_schedule);
        b_back = (ImageButton) findViewById(R.id.b_back_schedule_lesson);
        tv_heading = (TextView) findViewById(R.id.tv_heading_schedule_lesson);
        spinner_subjects = (Spinner) findViewById(R.id.spinner_subject_schedule_lesson);
        ed_room = (EditText) findViewById(R.id.ed_room_schedule_lesson);
        ed_teacher = (EditText) findViewById(R.id.ed_teacher_schedule_lesson);

        //Intent
        day = (int) getIntent().getSerializableExtra("DAY");
        lesson_count = (int) getIntent().getSerializableExtra("LESSONCOUNT");
        allow_spinner = (boolean) getIntent().getSerializableExtra("SPINNER");
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");
        if(previous_class == MainActivity.class) {
            mode = mode_pl;
        }
        else {
            mode = mode_sg;
        }

        //content
        subjects.add( getResources().getString(R.string.spinner_choose));
        for(int i = 0; i < Storage.subjects.size(); i++) {
            subjects.add(Storage.subjects.get(i).getName());
        }
        spinner_subjects.setAdapter(new Subject_spinner_adapter(this, android.R.layout.simple_spinner_dropdown_item, subjects));

        drawActivity();

        //input_listener
        container.setOnTouchListener(new Swipe_listener(this) {
            @Override public void swipeRight() { left(); }
            @Override public void swipeLeft() { right(); }
            @Override public void swipeTop() { down(); }
            @Override public void swipeBottom() { up(); }
        });
        spinner_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                adapterView = av;
                selected_subject = i;
                changeSubject();
            }

            @Override public void onNothingSelected(AdapterView<?> av) {
                adapterView = av;
                selected_subject = 0;
                changeSubject();
            }
        });
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_back();
            }
        });

        //options
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));

        setMenuContainerId(R.id.lesson_scroll_container);
        setOptionId(0);
        setOptions();
    }

    //******************************************************* option *******************************************************//
    /*
     * actions for option list
     */
    @Override public void optionActions(int i) {
        switch(i) {
            case 0: onclick_back(); break;
        }
    }

    //******************************************************* method *******************************************************//

    @Override protected void onPause() {
        super.onPause();
        saveData();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /*
     * draws the important colors of the fragment
     */
    private void drawActivity() {
        tv_heading.setText(getDayAbbreviation(day) + ", " + (lesson_count+1) + ". Stunde");
        if(Storage.schedule.getLesson(day, lesson_count) != null) {
            selected_subject = Storage.schedule.getLesson(day, lesson_count).getSubjectIndex()+1;
            spinner_subjects.setSelection(selected_subject);
            spinner_subjects.setEnabled(allow_spinner);

            ed_room.setText( Storage.schedule.getLesson(day, lesson_count).getRoom());
            ed_teacher.setText( Storage.subjects.get(selected_subject-1).getTeacher());
        }
        else {
            selected_subject = 0;
            spinner_subjects.setSelection(selected_subject);
            spinner_subjects.setEnabled(allow_spinner);

            ed_room.setText("");
            ed_teacher.setText("");
        }
        changeSubject();
    }

    /*
     * all changes made by a new subject
     */
    private void changeSubject() {
        //color
        if(selected_subject != 0) {
            if(adapterView != null) {
                //getting the first item of the spinner (textview for selected item) and change text color & text style
                ((TextView) adapterView.getChildAt(0)).setTextColor( Storage.subjects.get(selected_subject-1).getColor());
                ((TextView) adapterView.getChildAt(0)).setTypeface(Typeface.DEFAULT_BOLD);
            }
            //changing the toolbar color
            b_back.getBackground().getCurrent().setColorFilter( Storage.subjects.get(selected_subject-1).getDarkColor(), PorterDuff.Mode.SRC_ATOP);
            b_back.setImageDrawable( changeSymbolColor( R.drawable.symbol_back, getResources().getColor(R.color.background)));
            tv_heading.getBackground().getCurrent().setColorFilter( Storage.subjects.get(selected_subject-1).getDarkColor(), PorterDuff.Mode.SRC_ATOP);
            tv_heading.setTextColor( getResources().getColor(R.color.background));
            //predict the teacher
            ed_teacher.setText( Storage.subjects.get(selected_subject-1).getTeacher());
        }
        else {
            if(adapterView != null) {
                //getting the first item of the spinner (textview for selected item) and change text color & text style
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            //changing the toolbar color
            b_back.setBackground(getResources().getDrawable(R.drawable.heading));
            b_back.setImageDrawable(getResources().getDrawable(R.drawable.symbol_back));
            tv_heading.setBackground(getResources().getDrawable(R.drawable.heading));
            tv_heading.setTextColor( getResources().getColor(R.color.colorPrimary));
        }
    }

    /*
     * saves all entered data
     */
    private void saveData() {
        if (selected_subject != 0) {
            Storage.schedule.setLesson(new Lesson(day, lesson_count, ed_room.getText().toString(), selected_subject-1));
            Storage.schedule.getLesson(day, lesson_count).setTeacher( ed_teacher.getText().toString());
        }
        else {
            Storage.schedule.setLesson(day, lesson_count, null);
        }
    }

    /*
     * scrolledBy to the next lesson
     */
    private void down() {
        saveData();
        //if the activity is called from the subject overview
        if(mode == mode_pl) {
            lesson_count = Math.min(8, lesson_count+1);
        }
        else {
            boolean end = false;
            //search for matching lesson in same day
            for(int y = lesson_count+1; y < 9 && !end; y++) {
                Lesson lesson = Storage.schedule.getLesson(day, y);
                if(lesson != null) {
                    if(lesson.getSubjectIndex() == selected_subject-1) {
                        end = true;
                        lesson_count = y;
                    }
                }
            }
            //searching for the nearest (vertical) lesson, runs each day from monday to friday
            for(int y = lesson_count+1; y < 9 && !end; y++) {
                for(int x = 0; x < 5 && !end; x++) {
                    Lesson lesson = Storage.schedule.getLesson(x, y);
                    if(lesson != null) {
                        if(lesson.getSubjectIndex() == selected_subject-1) {
                            end = true;
                            lesson_count = y;
                            day = x;
                        }
                    }
                }
            }
        }
        drawActivity();
    }

    /*
     * scrolledBy to the previous lesson
     */
    private void up() {
        saveData();
        //if the activity is called from the subject overview
        if(mode == mode_pl) {
            lesson_count = Math.max(0, lesson_count-1);
        }
        else {
            boolean end = false;
            //search for matching lesson in same day
            for(int y = lesson_count-1; y >= 0 && !end; y--) {
                Lesson lesson = Storage.schedule.getLesson(day, y);
                if(lesson != null) {
                    if(lesson.getSubjectIndex() == selected_subject-1) {
                        end = true;
                        lesson_count = y;
                    }
                }
            }
            //searching for the nearest (vertical) lesson, runs each day from monday to friday
            for(int y = lesson_count-1; y >= 0 && !end; y--) {
                for(int x = 0; x < 5 && !end; x++) {
                    Lesson lesson = Storage.schedule.getLesson(x, y);
                    if(lesson != null) {
                        if(lesson.getSubjectIndex() == selected_subject-1) {
                            end = true;
                            lesson_count = y;
                            day = x;
                        }
                    }
                }
            }
        }
        drawActivity();
    }

    /*
     * scrolledBy to the lesson of the next day
     */
    private void right() {
        saveData();
        //if the activity is called from the subject overview
        if(mode == mode_pl) {
            day = Math.min(4, day+1);
        }
        else {
            boolean end = false;
            //search for matching lesson in same time
            for(int x = day+1; x < 5 && !end; x++) {
                Lesson lesson = Storage.schedule.getLesson(x, lesson_count);
                if(lesson != null) {
                    if(lesson.getSubjectIndex() == selected_subject-1) {
                        end = true;
                        day = x;
                    }
                }
            }
            //searching for the nearest (horizontal) lesson, runs each time from 0 to 8
            for(int x = day+1; x < 5 && !end; x++) {
                for(int y = 0; y < 9 && !end; y++) {
                    Lesson lesson = Storage.schedule.getLesson(x, y);
                    if(lesson != null) {
                        if(lesson.getSubjectIndex() == selected_subject-1) {
                            end = true;
                            lesson_count = y;
                            day = x;
                        }
                    }
                }
            }
        }
        drawActivity();
    }

    /*
     * scrolledBy to the lesson of the previous day
     */
    private void left() {
        saveData();
        //if the activity is called from the subject overview
        if(mode == mode_pl) {
            day = Math.max(0, day-1);
        }
        else {
            boolean end = false;
            //search for matching lesson in same time
            for(int x = day-1; x >= 0 && !end; x--) {
                Lesson lesson = Storage.schedule.getLesson(x, lesson_count);
                if(lesson != null) {
                    if(lesson.getSubjectIndex() == selected_subject-1) {
                        end = true;
                        day = x;
                    }
                }
            }
            //searching for the nearest (horizontal) lesson, runs each time from 0 to 8
            for(int x = day-1; x >= 0 && !end; x--) {
                for(int y = 0; y < 9 && !end; y++) {
                    Lesson lesson = Storage.schedule.getLesson(x, y);
                    if(lesson != null) {
                        if(lesson.getSubjectIndex() == selected_subject-1) {
                            end = true;
                            lesson_count = y;
                            day = x;
                        }
                    }
                }
            }
        }
        drawActivity();
    }

    /*
     * onclick input_listener for the button back
     */
    private void onclick_back() {
        Intent toLastActivity = new Intent();
        toLastActivity.setClass(this, previous_class);
        toLastActivity.putExtra("FRAGMENT_INDEX", 0);
        toLastActivity.putExtra("SUBJECT_INDEX", selected_subject-1);
        this.startActivity(toLastActivity);
    }

    /*
     * return for the numbers 0-4 the abbreviations of the days
     */
    private String getDayAbbreviation(int i) {
        switch(i) {
            case 0: return getResources().getString(R.string.schedule_monday);
            case 1: return getResources().getString(R.string.schedule_tuesday);
            case 2: return getResources().getString(R.string.schedule_wednesday);
            case 3: return getResources().getString(R.string.schedule_thursday);
            case 4: return getResources().getString(R.string.schedule_friday);
            default: return "";
        }
    }

    //******************************************************* symbol *******************************************************//
    /*
     * change the color of a symbol
     * and returns it
     */
    private Drawable changeSymbolColor(int symbol_id, int color) {
        Drawable symbol = getResources().getDrawable(symbol_id);
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return symbol;
    }
}

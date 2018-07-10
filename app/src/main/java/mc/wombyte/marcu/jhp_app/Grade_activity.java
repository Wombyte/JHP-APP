package mc.wombyte.marcu.jhp_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Reuseables.TextArea;

/**
 * Created by marcu on 29.07.2017.
 */

public class Grade_activity extends JHP_Activity {

    TextView tv_heading_subject;
    TextView tv_heading_kind;
    ImageButton b_back;
    RelativeLayout container_swipe;
    EditText ed_grade;
    Button b_sub_grade;
    Button b_add_grade;
    DatePicker spinner_date_written;
    DatePicker spinner_date_got;
    RadioGroup radiogroup_kind;
    RadioButton radiobutton_misc;
    EditText ed_misc;
    TextArea ta_description;

    Class previous_class;

    int subject_index = -1;
    int index = -1;
    boolean existing = true;
    int active_kind_id = -1;
    ArrayList<Date> dates_written = new ArrayList<>();
    ArrayList<Date> dates_got = new ArrayList<>();
    int grade_max;
    int grade_min;
    AdapterView<?> adapterView;

    //draw methode
    int color_dark;

    @Override protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.grade_activity);

        //initialization
        tv_heading_subject = (TextView) findViewById(R.id.tv_heading_subject_grade);
        tv_heading_kind = (TextView) findViewById(R.id.tv_heading_kind_grade);
        b_back = (ImageButton) findViewById(R.id.b_back_grade);
        container_swipe = (RelativeLayout) findViewById(R.id.container_swipe_grade);
        ed_grade = (EditText) findViewById(R.id.ed_grade_grades);
        b_sub_grade = (Button) findViewById(R.id.b_sub_grade);
        b_add_grade = (Button) findViewById(R.id.b_add_grade);
        spinner_date_written = findViewById(R.id.spinner_date_written_grade);
        spinner_date_got = findViewById(R.id.spinner_date_got_grade);
        radiogroup_kind = (RadioGroup) findViewById(R.id.radiogroup_kind_grades);
        radiobutton_misc = (RadioButton) findViewById(R.id.radio_b_misc_grade);
        ed_misc = (EditText) findViewById(R.id.ed_misc_grades);
        ta_description = (TextArea) findViewById(R.id.ed_description_grade);

        //intent
        subject_index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        index = (int) getIntent().getSerializableExtra("INDEX");
        if(index == -1) {
            existing = false;
        }
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");

        askForPermissions();

        //input_listener
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_back();
            }
        });
        container_swipe.setOnTouchListener(new Swipe_listener(this) {
            @Override public void swipeRight() { left(); }
            @Override public void swipeLeft() { right(); }
            @Override public void swipeTop() {}
            @Override public void swipeBottom() {}
        });
        ed_grade.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkGradeRange(charSequence.toString());
            }
        });
        b_sub_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_subGrade();
            }
        });
        b_add_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_addGrade();
            }
        });
        spinner_date_written.setOnDateChangeListener(new DatePicker.onDateChangeListener() {
            @Override public void onDateChanged(Date date) {
                onclick_writtenDate(date);
            }
        });
        radiogroup_kind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup radioGroup, int i) {
                onclick_kind(i);
            }
        });

        //spinner
        if(existing) {
            spinner_date_written.setDate( Storage.grades.get(subject_index).get(index).getWrittenDate());
            spinner_date_written.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_written.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());

            spinner_date_got.setDate( Storage.grades.get(subject_index).get(index).getWrittenDate());
            spinner_date_got.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_got.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());
        }
        else {
            spinner_date_written.switchToSpinner();
            spinner_date_written.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_written.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());
            spinner_date_got.switchToSpinner();
            spinner_date_got.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_got.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());
        }

        //general content
        b_back.getBackground().getCurrent().setColorFilter( Storage.subjects.get(subject_index).getDarkColor(), PorterDuff.Mode.SRC_ATOP);
        tv_heading_subject.getBackground().getCurrent().setColorFilter( Storage.subjects.get(subject_index).getDarkColor(), PorterDuff.Mode.SRC_ATOP);
        tv_heading_kind.getBackground().getCurrent().setColorFilter( Storage.subjects.get(subject_index).getDarkColor(), PorterDuff.Mode.SRC_ATOP);

        if(Storage.settings.grades_isRatingInGrades()) {
            grade_min = 1;
            grade_max = 6;
        }
        else {
            grade_min = 0;
            grade_max = 15;
        }
        readData();

        //options
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_calendar_1),
                getResources().getString(R.string.grades_option_calendar1)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_calendar_2),
                getResources().getString(R.string.grades_option_calendar2)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_arrow_left),
                getResources().getString(R.string.grades_option_last_grade)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_arrow_right),
                getResources().getString(R.string.grades_option_next_grade)
        ));

        setMenuContainerId(R.id.grade_scroll_container);
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
            case 1: spinner_date_written.openDateDialog(); break;
            case 2: spinner_date_got.openDateDialog(); break;
            case 3: left(); break;
            case 4: right(); break;
        }
    }

    //******************************************************* saving methode *******************************************************//

    @Override protected void onPause() {
        super.onPause();
        saveData();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /*
     * saves the entered data in a grade
     */
    private void saveData() {
        if(existing) {
            if(!ta_description.getText().toString().equals("")) {
                Storage.grades.get(subject_index).get(index).setDescription(ta_description.getText().toString());
            }
            Storage.grades.get(subject_index).get(index).setNumber(Integer.parseInt(ed_grade.getText().toString()));
            Storage.grades.get(subject_index).get(index).setShortDescription(tv_heading_kind.getText().toString());
            if(tv_heading_kind.getText().toString().equals( getResources().getString(R.string.grades_kind_exam))) {
                Storage.grades.get(subject_index).get(index).setExam(true);
            }
            if(!ed_misc.getText().toString().equals("")) {
                Storage.grades.get(subject_index).get(index).setMisc(ed_misc.getText().toString());
            }
            Storage.subjects.get(subject_index).calculateAverage();
        }
        else {
            if(!ta_description.getText().toString().equals("")) {
                boolean exam = tv_heading_kind.getText().toString().equals( getResources().getString(R.string.grades_kind_exam));
                Storage.addGrade(subject_index, new Grade(
                        Storage.grades.get(subject_index).size(),
                        subject_index,
                        Integer.parseInt(ed_grade.getText().toString()),
                        spinner_date_written.getDate(),
                        spinner_date_got.getDate(),
                        ta_description.getText().toString(),
                        tv_heading_kind.getText().toString(),
                        ed_misc.getText().toString(),
                        exam
                ));
                index = Storage.grades.get(subject_index).size()-1;
                existing = true;
            }
        }
    }

    //******************************************************* input_listener *******************************************************//

    /*
     * onclick input_listener for the button back
     */
    private void onclick_back() {
        Intent toPreviousActivity = new Intent();
        toPreviousActivity.setClass(this, previous_class);
        toPreviousActivity.putExtra("FRAGMENT_INDEX", 3);
        if(previous_class == Subject_activity.class) {
            toPreviousActivity.putExtra("SUBJECT_INDEX", subject_index);
        }
        this.startActivity(toPreviousActivity);
    }

    /*
     * shows the grade left of the current
     */
    private void left() {
        saveData();
        if(index != 0) {
            if(index > 0) {
                index--;
            }
            if(index == -1) {
                index = Storage.grades.get(subject_index).size()-1;
            }
            existing = true;
            readData();
        }
    }

    /*
     * shows the grade right of the current
     */
    private void right() {
        saveData();
        if(index < Storage.grades.get(subject_index).size() && index > -1) {
            index++;
            if(index < Storage.grades.get(subject_index).size()) {
                existing = true;
            }
            else {
                existing = false;
                index = -1;
            }
            readData();
        }
    }

    /*
     * checks whether the written text is in the grade range
     * if not the text is set to max or min, depending on which border was crossed
     */
    private void checkGradeRange(String string) {
        int grade;
        try {
            grade = Integer.parseInt(string);
        }
        catch(Exception e) {
            ed_grade.setText(String.valueOf(predictedGrade()));
            return;
        }

        if(grade < grade_min) {
            ed_grade.setText(String.valueOf(grade_min));
            return;
        }
        if(grade > grade_max) {
            ed_grade.setText(String.valueOf(grade_max));
        }
    }

    /*
     * onclick input_listener for the button addGrade
     */
    private void onclick_addGrade() {
        int grade = Integer.parseInt(ed_grade.getText().toString());
        ed_grade.setText(String.valueOf(grade+1));
    }

    /*
     * onclick input_listener for the button subGrade
     */
    private void onclick_subGrade() {
        int grade = Integer.parseInt(ed_grade.getText().toString());
        ed_grade.setText(String.valueOf(grade-1));
    }

    /*
     * onclick input_listener when an item of the written_spinner is selected
     * a list for got_spinner is provided that reaches from the selected date until today
     */
    private void onclick_writtenDate(Date date) {
        if(!existing) { //creates a sublist of the written dates
            dates_got = getSubList(0, date);
            spinner_date_got.switchToSpinner();
            spinner_date_got.updateList( dateToString(dates_got));
            spinner_date_got.setSelection( Math.min(0, dates_got.size()));
        }
    }

    /*
     * onclick input_listener for the radiogroup kind
     */
    private void onclick_kind(int i) {
        if(active_kind_id == i) {
            return;
        }
        if(active_kind_id == -1) {
            active_kind_id = radiogroup_kind.getCheckedRadioButtonId();
        }

        //change color
        changeRadioButtonColor(active_kind_id, getResources().getColor(R.color.radio_button_inactive));
        active_kind_id = i;
        changeRadioButtonColor(active_kind_id, Storage.subjects.get(subject_index).getDarkColor());

        //change text
        if(i == R.id.radio_b_misc_grade) {
            tv_heading_kind.setText( getResources().getString(R.string.grades_kind_misc));
            ed_misc.setEnabled(true);
        }
        else {
            tv_heading_kind.setText(((TextView) findViewById(i)).getText().toString());
            ed_misc.setEnabled(false);
            ed_misc.setText("");
        }
    }

    /*
     * changes the color of the Drawable of the Radiobutton
     */
    private void changeRadioButtonColor(int button_id, int color) {
        RadioButton button = (RadioButton) radiogroup_kind.findViewById(button_id);
        Drawable symbol = button.getCompoundDrawables()[0];
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        button.setCompoundDrawables(symbol, null, null, null);
        //0 = left, setCompoundDrawables(left, top, right, bottom)
    }

    //******************************************************* methods *******************************************************//

    /*
     * reads all the data from the grade at option_index and index
     */
    private void readData() {
        dates_written.clear();
        dates_got.clear();

        tv_heading_subject.setText( Storage.subjects.get(subject_index).getName());
        ta_description.changeBorderColor( Storage.subjects.get(subject_index).getDarkColor());

        if(existing) {
            tv_heading_kind.setText( Storage.grades.get(subject_index).get(index).getShortDescription());
            ta_description.setText( Storage.grades.get(subject_index).get(index).getDescription());
            ed_grade.setText( String.valueOf(Storage.grades.get(subject_index).get(index).getNumber()));

            spinner_date_written.setDate( Storage.grades.get(subject_index).get(index).getWrittenDate());
            spinner_date_written.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_written.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());

            spinner_date_got.setDate( Storage.grades.get(subject_index).get(index).getGotDate());
            spinner_date_got.setLightColor( Storage.subjects.get(subject_index).getColor());
            spinner_date_got.setDarkColor( Storage.subjects.get(subject_index).getDarkColor());

            //set the correct kind of grade
            ed_misc.setEnabled(false);
            String des = Storage.grades.get(subject_index).get(index).getShortDescription();
            for(int i = 0; i < radiogroup_kind.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radiogroup_kind.getChildAt(i);
                if(des.equals(radioButton.getText())) {
                    radiogroup_kind.check(radioButton.getId());
                    active_kind_id = radioButton.getId();
                    if(radioButton.getId() == R.id.radio_b_misc_grade) {
                        ed_misc.setEnabled(true);
                        ed_misc.setText( Storage.grades.get(subject_index).get(index).getMisc());
                    }
                    break;
                }
            }
            changeRadioButtonColor(radiogroup_kind.getCheckedRadioButtonId(), Storage.subjects.get(subject_index).getDarkColor());
        }
        else {
            tv_heading_kind.setText( getResources().getString(R.string.grades_kind_test));
            ta_description.setText("");

            spinner_date_got.switchToSpinner();
            dates_written = Storage.getPastDates(subject_index);
            spinner_date_written.updateList(dateToString(dates_written));
            spinner_date_written.setDarkColor(color_dark);

            ed_grade.setText(String.valueOf(predictedGrade()));
            radiogroup_kind.check(R.id.radio_b_test_grade);
            active_kind_id = R.id.radio_b_test_grade;
            changeRadioButtonColor(radiogroup_kind.getChildAt(1).getId(), Storage.subjects.get(subject_index).getDarkColor());
            ed_misc.setEnabled(false);
            ed_misc.setText("");
        }
    }

    /*
     * converts the HomeworkDate title_strings into a string title_strings
     */
    private ArrayList<String> dateToString(ArrayList<Date> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM yyyy", Locale.GERMANY);
        ArrayList<String> string = new ArrayList<>();
        for(Date date: list) {
            string.add(sdf.format(date));
        }
        return string;
    }

    /*
     * converts a single date into a string
     */
    private String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);
        return sdf.format(date);
    }

    /*
     * tries to predict the grade, by rounding the average
     */
    private int predictedGrade() {
        double average = Storage.subjects.get(subject_index).getAverage();
        average += 0.5;
        if((int) average == 0) {
            average = 10;
        }
        return (int) average;
    }

    /*
     * return a sub ArrayList of date_written between the two params
     */
    private ArrayList<Date> getSubList(int a, Date date) {
        ArrayList<Date> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM yyyy", Locale.GERMANY);
        for(int i = a; i < dates_written.size() && !dates_written.get(i).before(date); i++) {
            result.add(dates_written.get(i));
        }
        return result;
    }
}

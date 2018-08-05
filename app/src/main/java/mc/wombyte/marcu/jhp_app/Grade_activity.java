package mc.wombyte.marcu.jhp_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;
import mc.wombyte.marcu.jhp_app.Reuseables.DatePicker;
import mc.wombyte.marcu.jhp_app.Reuseables.ImageListView;
import mc.wombyte.marcu.jhp_app.Reuseables.NumberPicker.NumberPicker;
import mc.wombyte.marcu.jhp_app.Reuseables.TextArea;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

/**
 * Created by marcu on 29.07.2017.
 */

public class Grade_activity extends JHP_Activity {

    TextView tv_heading_subject;
    TextView tv_heading_kind;
    ImageButton b_back;
    LinearLayout container_swipe;
    NumberPicker number_picker;
    ImageView image_description;
    TextArea ta_description;
    ImageButton b_add_image;
    ViewSwitcher vs_image_description;
    DatePicker spinner_date_written;
    DatePicker spinner_date_got;
    RadioGroup radiogroup_kind;
    RadioButton radiobutton_misc;
    EditText ed_misc;

    Class previous_class;

    int light_color, dark_color;
    ArrayList<Uri> des_images = new ArrayList<>();

    int subject_index = -1;
    int index = -1;
    boolean existing = true;
    int active_kind_id = -1;
    ArrayList<Date> dates_written = new ArrayList<>();
    ArrayList<Date> dates_got = new ArrayList<>();
    AdapterView<?> adapterView;


    @Override protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.grade_activity);

        //file structure
        new FileSaver(this).prepareNewGradeFolder();

        //initialization
        tv_heading_subject = findViewById(R.id.tv_heading_subject_grade);
        tv_heading_kind = findViewById(R.id.tv_heading_kind_grade);
        b_back = findViewById(R.id.b_back_grade);
        container_swipe = findViewById(R.id.container_swipe_grade);
        image_description = findViewById(R.id.imageview_text_description_grade);
        ta_description = findViewById(R.id.ed_description_grade);
        b_add_image = findViewById(R.id.b_images_description_grade);
        (vs_image_description = findViewById(R.id.vs_description_grade)).createView(this);
        number_picker = findViewById(R.id.number_picker_grade);
        spinner_date_written = findViewById(R.id.spinner_date_written_grade);
        spinner_date_got = findViewById(R.id.spinner_date_got_grade);
        radiogroup_kind = findViewById(R.id.radiogroup_kind_grades);
        radiobutton_misc = findViewById(R.id.radio_b_misc_grade);
        ed_misc = findViewById(R.id.ed_misc_grades);

        //intent
        subject_index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        index = (int) getIntent().getSerializableExtra("INDEX");
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");
        readData();

        //color
        light_color = Storage.subjects.get(subject_index).getColor();
        dark_color = Storage.subjects.get(subject_index).getDarkColor();

        //permission
        askForPermissions();

        //input_listener
        b_back.setOnClickListener((view) -> onclick_back());
        container_swipe.setOnTouchListener(new Swipe_listener(this) {
            @Override public void swipeRight() { left(); }
            @Override public void swipeLeft() { right(); }
            @Override public void swipeTop() {}
            @Override public void swipeBottom() {}
        });
        b_add_image.setOnClickListener((view) -> openImagePickerDialog());
        spinner_date_written.setOnDateChangeListener(this::onclick_writtenDate);
        radiogroup_kind.setOnCheckedChangeListener((radioGroup, i) -> activateRadioButton(i));

        //spinner
        if(existing) {
            spinner_date_written.setDate( Storage.grades.get(subject_index).get(index).getWrittenDate());
            spinner_date_got.setDate( Storage.grades.get(subject_index).get(index).getWrittenDate());
        }
        else {
            spinner_date_written.switchToSpinner();
            spinner_date_got.switchToSpinner();
        }
        spinner_date_written.setLightColor( light_color);
        spinner_date_written.setDarkColor( dark_color);
        spinner_date_got.setLightColor( light_color);
        spinner_date_got.setDarkColor( dark_color);

        //general content
        b_back.getBackground().getCurrent().setColorFilter( dark_color, PorterDuff.Mode.SRC_ATOP);
        tv_heading_subject.getBackground().getCurrent().setColorFilter( dark_color, PorterDuff.Mode.SRC_ATOP);
        tv_heading_subject.setText( Storage.subjects.get(subject_index).getName());
        tv_heading_kind.getBackground().getCurrent().setColorFilter( dark_color, PorterDuff.Mode.SRC_ATOP);
        image_description.setColorFilter(dark_color);
        b_add_image.setColorFilter(dark_color);

        if(Storage.settings.grades_isRatingInGrades()) {
            number_picker.setRange(1, 6);
            number_picker.setMode(NumberPicker.DESCENDING);
        }
        else {
            number_picker.setRange(0, 15);
        }
        number_picker.setColors(dark_color, light_color);
        number_picker.applyChanges();

        //kind
        activateRadioButton(active_kind_id);

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
                getResources().getDrawable(R.drawable.symbol_add_image),
                getResources().getString(R.string.homework_option_add_image)
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


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////// option method /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * defines the actions that are triggered after the options are clicked
     * 0: back to Main Activity
     * 1/2: open date written/got DateDialog
     * 3:4: get the next grade to the left/right
     * @param i: index of the option, which was clicked
     */
    @Override public void optionActions(int i) {
        switch(i) {
            case 0: onclick_back(); break;
            case 1: openImagePickerDialog(); break;
            case 2: spinner_date_written.openDateDialog(); break;
            case 3: spinner_date_got.openDateDialog(); break;
            case 4: left(); break;
            case 5: right(); break;
        }
    }

    /**
     * returns back to the MainActivity
     * and puts all extra got from it back
     */
    private void onclick_back() {
        saveData();
        Intent toPreviousActivity = new Intent();
        toPreviousActivity.setClass(this, previous_class);
        toPreviousActivity.putExtra("FRAGMENT_INDEX", 3);
        if(previous_class == Subject_activity.class) {
            toPreviousActivity.putExtra("SUBJECT_INDEX", subject_index);
        }
        this.startActivity(toPreviousActivity);
    }

    /**
     * opens a new instance of the image picker dialog
     */
    private void openImagePickerDialog() {
        new ImagePickerDialog(this, this, ImagePickerDialog.GRADE_DESCRIPTION_IMAGE).show();
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Description section: mainly for handling the image list view /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * deals with the intent from the gallery / camera
     * if 'picking a pic' was successful the uri is added to the list and saved
     * @param requestCode: defines whether the action was successful
     * @param resultCode: unnecessary as there is only one image action in this activity
     * @param imageReturnedIntent: intent which contains the selected/taken image as Uri
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK) {
            Uri uri = ImagePickerDialog.getUri(requestCode, imageReturnedIntent);

            String grade_name = "newGrade";
            if(existing) {
                grade_name = subject_index + "_" + index;
            }

            addImage(uri);
            new FileSaver(this).saveGradeDescriptionImage(uri, grade_name);
        }
    }

    /**
     * listener, that defines what should happen if an image in the HorizontalImageListView is clicked long
     * it opens an delete dialog, where the user can decide whether the images should
     * really be deleted
     */
    ImageListView.LongItemClickListener longItemClickListener = (pos) -> {
        String question = getResources().getString(R.string.boolean_dialog_delete_image);
        BooleanDialog dialog = new BooleanDialog(this, question);
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                FileSaver.deleteImageFromUri( des_images.get(pos));
                des_images.remove(pos);
                updateImageView();
            }
            @Override public void onNo() { }
        });
        dialog.show();
    };

    /**
     * updates the view switcher which contains the description images
     * reading the description image uris from the file
     * if the size of the read list is 0, the textview is activated
     * else the read list is set into the image adapter
     */
    private void updateImageView() {
        if(des_images.size() > 0) {
            if(ta_description.getText().toString().equals("")) {
                ta_description.setText(R.string.grades_description_default_image_text);
            }
            vs_image_description.switchToView(1);
            ImageListView listview = findViewById(R.id.listview_description_grade);
            listview.setItemLongClickListener(longItemClickListener);
            listview.setImageList(des_images);
        }
        else {
            String text = ta_description.getText().toString();
            String res = getResources().getString(R.string.grades_description_default_image_text);
            if(text.equals(res)) {
                ta_description.setText("");
            }
            vs_image_description.switchToView(0);
        }
    }

    /**
     * adds a image uri to the existing list
     * and activates the listview view of vs_image_description
     * @param uri: images that is added
     */
    public void addImage(Uri uri) {
        des_images.add(uri);
        updateImageView();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// Dates /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * onlclick listener when a new written date is selected
     * updates the got date picker
     * @param date: for {@link this#getSubList(int, Date)}
     */
    private void onclick_writtenDate(Date date) {
        if(existing) {
            return;
        }

        dates_got = getSubList(0, date);
        spinner_date_got.switchToSpinner();
        spinner_date_got.updateList( dateToString(dates_got));
        spinner_date_got.setSelection( Math.min(0, dates_got.size()));
    }

    /**
     * calculates a sub array list of {@link this#dates_written}
     * this list starts at index 'a' and ends at the first date
     * that is after 'date'
     * @param a: start index
     * @param date: date which defines the end of the list
     * @return sublist of {@link this#dates_written}
     */
    private ArrayList<Date> getSubList(int a, Date date) {
        ArrayList<Date> result = new ArrayList<>();
        for(int i = a; i < dates_written.size() && !dates_written.get(i).before(date); i++) {
            result.add(dates_written.get(i));
        }
        return result;
    }

    /**
     * converts the transmitted date array list into a string array list
     * @param list: transmitted date array list
     * @return string array list
     */
    private ArrayList<String> dateToString(ArrayList<Date> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM yyyy", Locale.GERMANY);
        ArrayList<String> string = new ArrayList<>();
        for(Date date: list) {
            string.add(sdf.format(date));
        }
        return string;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// kind methods /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * activates the radiobutton represented by 'index'
     * if 'index' is equal to the current active radio button id, nothing happens
     * then the old & the new active radio get a new color through {@link this#changeRadioButtonColor(int, int)}
     * and the {@link this#active_kind_id} is set to the transmitted id
     * if 'misc' radio button is active, the text field is enabled
     * else the text field is disabled and emptied
     * @param index: id of the new active radio button
     */
    private void activateRadioButton(int index) {

        //change color
        changeRadioButtonColor(active_kind_id, getResources().getColor(R.color.radio_button_inactive));
        active_kind_id = index;
        changeRadioButtonColor(active_kind_id, dark_color);

        //change text
        tv_heading_kind.setText(((TextView) findViewById(index)).getText().toString());
        if(index == R.id.radio_b_misc_grade) {
            ed_misc.setEnabled(true);
        }
        else {
            ed_misc.setEnabled(false);
            ed_misc.setText("");
        }
    }

    /**
     * changes the buttons left drawable to 'color'
     * if button id is -1, nothing happens, as this is the start value of {@link this#active_kind_id}
     * @param button_id: id of the radiobutton
     * @param color: color, in which the drawable should be painted
     */
    private void changeRadioButtonColor(int button_id, int color) {
        if(button_id == -1) {
            return;
        }
        RadioButton button = radiogroup_kind.findViewById(button_id);
        Drawable symbol = button.getCompoundDrawables()[0];
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        button.setCompoundDrawables(symbol, null, null, null);
        button.invalidate();
    }

    /**
     * returns the radiobutton of which the description is transmitted
     * iterating through the radiobutton: stops when the descriptions match
     * counter is then equal to the index of the correct radiobutton
     * @param des: description, which is equal to one of the radio buttons description
     * @return resource id of the radiobutton with the same description
     */
    private int getRadioButtonIDByDescription(String des) {
        int i = 0;
        while(!des.equals( ((RadioButton) radiogroup_kind.getChildAt(i)).getText().toString())) {
            i++;
        }

        return radiogroup_kind.getChildAt(i).getId();
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// controlling data (saving, reading, changing the grade) ////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the data from the grade represented by {@link this#subject_index} & {@link this#index}
     * first {@link this#existing} is defined depending on the {@link this#index}
     * if it exists all values from the new grade are loaded to the GUI
     * else the standard values are loaded
     */
    private void readData() {
        dates_written.clear();
        dates_got.clear();

        existing = index != -1;

        if(existing) {
            Grade grade = Storage.grades.get(subject_index).get(index);
            tv_heading_kind.setText( grade.getShortDescription());

            number_picker.setSelection( grade.getNumber());

            ta_description.setText( grade.getDescription());
            des_images = Grade.readGradeImages( grade.getFileName());
            updateImageView();

            spinner_date_written.setDate( grade.getWrittenDate());
            spinner_date_got.setDate( grade.getGotDate());

            int id = getRadioButtonIDByDescription( grade.getShortDescription());
            changeRadioButtonColor(id, dark_color);
            activateRadioButton(id);
            radiogroup_kind.check(id);
            if(active_kind_id == R.id.radio_b_misc_grade) {
                ed_misc.setText( grade.getMisc());
            }
        }
        else {
            tv_heading_kind.setText( getResources().getString(R.string.grades_kind_test));
            ta_description.setText("");

            int index = Grade.getPredictedGrade(subject_index);
            if(Storage.settings.grades_isRatingInGrades()) index--; //grades starts with 1 -> -1 to get the index of average
            number_picker.setSelection( index);

            spinner_date_got.switchToSpinner();
            dates_written = Storage.getPastDates(subject_index);
            spinner_date_written.updateList( dateToString(dates_written));

            activateRadioButton(R.id.radio_b_test_grade);
        }
        number_picker.applyChanges();
    }

    /**
     * saves the data of the current grade
     * if the grade already exists:
     * - all data from the GUI replace the old values of the grade
     * - a new average of the current subject is calculated
     * else (if the grade is valid = has a description text)
     * - all data from the GUI form a new grade, which is add to the Storage
     * - {@link this#existing} becomes true
     * - {@link this#index} is set
     * - the temporary grade folder is renamed to the new grade name
     */
    private void saveData() {
        FileSaver fileSaver = new FileSaver(this);
        int number = number_picker.getSelectedIndex();
        number += Storage.settings.grades_isRatingInGrades()? 1 : 0;

        if(existing) {
            if(!ta_description.getText().toString().equals("")) {
                Storage.grades.get(subject_index).get(index).setDescription(ta_description.getText().toString());
            }

            Storage.grades.get(subject_index).get(index).setNumber(number);

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
            if(ta_description.getText().toString().equals("")) {
                fileSaver.clearTempFolder();
                return;
            }

            boolean exam = tv_heading_kind.getText().toString().equals( getResources().getString(R.string.grades_kind_exam));
            Storage.addGrade(subject_index, new Grade(
                    Storage.grades.get(subject_index).size(),
                    subject_index,
                    number,
                    spinner_date_written.getDate(),
                    spinner_date_got.getDate(),
                    ta_description.getText().toString(),
                    tv_heading_kind.getText().toString(),
                    ed_misc.getText().toString(),
                    exam
            ));
            index = Storage.grades.get(subject_index).size()-1;
            existing = true;

            String name = Storage.grades.get(subject_index).get(index).getFileName();
            fileSaver.renameTempImages(name, FileSaver.GRADE_TEMP_IMAGE);
            fileSaver.renameNewGradeFolderTo(name);
        }
        fileSaver.saveData();
    }

    /**
     * loads the (timely) previous grade of the current one
     * first the new index is saved temporarily
     * - if a new grade was created, the index of the latest grade is loaded
     * - else the old index is decreased by 1 (except at 0)
     * at least the index is updated
     */
    private void left() {
        int last_index = Storage.grades.get(subject_index).size()-1;
        int i = (index == -1)? last_index : Math.max(index-1, 0);
        updateIndex(i);
    }

    /**
     * loads the (timely) next grade of the current one
     * first the new index is saved temporarily
     * - if a new grade was created or the latest grade was edited = -1
     * - else the old index is increased by 1
     * at least the index is updated
     */
    private void right() {
        int last_index = Storage.grades.get(subject_index).size()-1;
        int i = (index == last_index || index == -1)? -1 : index+1;
        updateIndex(i);
    }

    /**
     * updates the image to the transmitted new index
     * if the both indices are equal nothing happens
     * @param new_index: new index
     */
    private void updateIndex(int new_index) {
        if(new_index == index) {
            return;
        }

        saveData();
        index = new_index;
        readData();
    }
}

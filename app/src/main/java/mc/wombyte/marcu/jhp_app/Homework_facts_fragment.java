package mc.wombyte.marcu.jhp_app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.HomeworkDate;
import mc.wombyte.marcu.jhp_app.Classes.Lesson;
import mc.wombyte.marcu.jhp_app.Reuseables.HorizontalImageListView.HorizontalImageListView;
import mc.wombyte.marcu.jhp_app.Reuseables.TextArea;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

/**
 * Created by marcu on 08.05.2018.
 */

public class Homework_facts_fragment extends Fragment {

    Context context;
    View view;

    ViewSwitcher spinner_subject;
    DatePicker spinner_date;
    ImageView image_text_description;
    TextArea ta_description;
    ImageButton b_add_image;
    ViewSwitcher vs_image_description;
    RadioGroup radiogroup_kind;
    EditText ed_misc;

    int subject_index = -1;
    int homework_index = -1;
    int active_kind_id = -1;
    HomeworkDate date;
    String description;
    ArrayList<Uri> des_images = new ArrayList<>();
    String short_description;
    String misc;
    boolean existing;  //defines whether the homework is already existing and only in change
    boolean is_single_mode;

    ArrayList<String> subjects = new ArrayList<>();
    ArrayList<HomeworkDate> dates = new ArrayList<>();
    AdapterView adapterView_subject;

    final boolean mode_pl = false;
    final boolean mode_sg = true;
    boolean mode = mode_pl;

    //draw methode
    int color_light;
    int color_heading;
    int color_dark;
    int color_text;

    //******************************************************* Override methods *******************************************************//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homework_facts_fragment, container, false);
        context = container.getContext();

        //initialization
        spinner_subject = ((ViewSwitcher) view.findViewById(R.id.spinner_subject_homework)).createView(context);
        spinner_date = view.findViewById(R.id.spinner_date_homework);
        image_text_description = view.findViewById(R.id.imageview_text_description_homework);
        ta_description = view.findViewById(R.id.ed_description_homework);
        b_add_image = view.findViewById(R.id.b_images_description_homework);
        (vs_image_description = view.findViewById(R.id.vs_description_homework)).createView(context);
        radiogroup_kind = view.findViewById(R.id.radiogroup_kind_homework);
        ed_misc = view.findViewById(R.id.ed_misc_homework);

        //listener
        ((Spinner) spinner_subject.getView(0)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> av, View view, int i, long l) { onclick_subject_spinner(av, i); }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        b_add_image.setOnClickListener((view) -> openImagePickerDialog());
        radiogroup_kind.setOnCheckedChangeListener(((radioGroup, i) -> onclick_kind(i)));

        //subject spinner
        if(!existing && !is_single_mode) {
            subjects.clear();
            subjects.add( getResources().getString(R.string.spinner_choose));
            for(int i = 0; i < Storage.subjects.size(); i++) {
                subjects.add(Storage.subjects.get(i).getName());
            }

            spinner_subject.switchToView(0);
            Spinner spinner = (Spinner) spinner_subject.getActiveView();
            spinner.setAdapter(new Subject_spinner_adapter(context, android.R.layout.simple_spinner_dropdown_item, subjects));
            spinner.setSelection(subject_index+1);
        }
        else {
            spinner_subject.switchToView(1);
            TextView textView = (TextView) spinner_subject.getActiveView();
            textView.setText( Storage.subjects.get(subject_index).getName());
        }

        //date spinner
        dates = Storage.getFutureDates(subject_index);
        spinner_date.updateList(dateToString(dates));
        if(date == null) {
            if(dates.size() > 0) {
                date = dates.get(0);
            }
            else {
                date = new HomeworkDate(new Date(), -1);
            }

        }
        int id = indexOfDate(date);
        if(id == -1) {
            spinner_date.setDate(date.date());
        }
        else {
            spinner_date.setSelection(id);
        }

        //description
        ta_description.setText(description);
        updateImageView();

        //kind
        active_kind_id = getActiveKindRadioButton();
        radiogroup_kind.check(active_kind_id);
        ed_misc.setEnabled(active_kind_id == R.id.radio_b_misc);

        //color
        if(subject_index != -1) {
            subjectSelected();
        }
        else {
            noItemSelected();
        }
        drawFragment();

        return view;
    } //end of on create

    /*
     * save data
     */
    @Override public void onPause() {
        methods.setSubjectIndex(subject_index);
        methods.setDate(new HomeworkDate(spinner_date.getDate(), getLesson(spinner_date.getDate(), subject_index)));
        methods.setDescription(ta_description.getText().toString());
        methods.setDescriptionImages(des_images);
        methods.setShortDescription(short_description);
        methods.setMisc(ed_misc.getText().toString());
        super.onPause();
    }

    //******************************************************* onclick listener *******************************************************//
    /*
     * onclick listener fo the subject spinner
     * decides how the fragment is color depending on the selected subject
     */
    private void onclick_subject_spinner(AdapterView<?> av, int i) {
        adapterView_subject = av;
        if (i > 0) {
            subject_index = i-1;
            subjectSelected();
        } else {
            subject_index = -1;
            noItemSelected();
        }
        drawFragment();
    }

    /*
     * onclick input_listener for the event that an item is selected
     */
    private void subjectSelected() {
        color_light = Storage.subjects.get(subject_index).getColor();
        color_heading = Storage.subjects.get(subject_index).getDarkColor();
        color_dark = Storage.subjects.get(subject_index).getDarkColor();
        color_text = getResources().getColor(R.color.background);

        //adapter for date spinner
        dates.clear();
        dates = Storage.getFutureDates(subject_index);
        spinner_date.setDarkColor(color_dark);
        spinner_date.setLightColor(color_light);
        spinner_date.updateList(dateToString(dates));
        int id = indexOfDate(date);
        if(id == -1 && existing) {
            spinner_date.setDate(date.date());
        }
        else {
            spinner_date.setSelection(id);
        }

    }

    /*
     * onclick input_listener for the event that no item is selected
     */
    private void noItemSelected() {
        color_light = getResources().getColor(R.color.colorPrimary);
        color_heading = getResources().getColor(R.color.background);
        color_dark = getResources().getColor(R.color.colorPrimaryDark);
        color_text = getResources().getColor(R.color.colorPrimaryDark);

        //content
        dates.clear();
        spinner_date.setDateToTomorrow();
    }

    /*
     * (re)draw the activity to show all new colors and texts
     * light_color, dark_color and text_color are necessary
     */
    private void drawFragment() {
        //light color
        TextView textView = (TextView) spinner_subject.getView(1);
        textView.setTextColor(color_light);
        if(adapterView_subject != null && adapterView_subject.getChildAt(0) != null) {
            //getting the first item of the spinner (textview for selected item) and change text color & text style
            TextView adapterText = (TextView) adapterView_subject.getChildAt(0);
            adapterText.setTextColor(color_light);
            adapterText.setTypeface(Typeface.DEFAULT_BOLD);
        }

        //dark color
        ta_description.changeBorderColor(color_dark);
        image_text_description.setColorFilter(color_dark, PorterDuff.Mode.SRC_ATOP);
        b_add_image.setColorFilter(color_dark, PorterDuff.Mode.SRC_ATOP);
        radiogroup_kind.check(active_kind_id);
        onclick_kind(radiogroup_kind.getCheckedRadioButtonId());

        methods.onSubjectChanged(subject_index);
    }

    /**
     * opens a new instance of the image picker dialog
     */
    public void openImagePickerDialog() {
        new ImagePickerDialog(context, getActivity(), ImagePickerDialog.HOMEWORK_DESCRIPTION_IMAGE).show();
    }

    /*
     * oncheck input_listener for the radiogroup "kind"
     * enables, disables the ed_misc & changes heading
     */
    private void onclick_kind(int i) {
        if(active_kind_id == -1) {
            active_kind_id = radiogroup_kind.getCheckedRadioButtonId();
        }

        //change color
        changeRadioButtonColor(active_kind_id, getResources().getColor(R.color.radio_button_inactive));
        active_kind_id = i;
        changeRadioButtonColor(active_kind_id, color_dark);

        short_description = ((TextView) view.findViewById(i)).getText().toString();
        methods.onKindChanged(short_description);
        if(i == R.id.radio_b_misc) {
            ed_misc.setEnabled(true);
        }
        else {
            ed_misc.setEnabled(false);
            ed_misc.setText("");
        }
    }

    //******************************************************* methods *******************************************************//

    /*
     * changes the color of the Drawable of the Radiobutton
     */
    private void changeRadioButtonColor(int button_id, int color) {
        RadioButton button = radiogroup_kind.findViewById(button_id);
        Drawable symbol = button.getCompoundDrawables()[0];
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        button.setCompoundDrawables(symbol, null, null, null);
    }

    /*
     * converts the HomeworkDate title_strings into a string title_strings
     */
    private ArrayList<String> dateToString(ArrayList<HomeworkDate> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM yyyy", Locale.GERMANY);
        ArrayList<String> string = new ArrayList<>();
        for(HomeworkDate date: list) {
            string.add(sdf.format(date.date()));
        }
        return string;
    }

    /*
     * returns the index of the date in "dates", that matches with the transferred
     */
    private int indexOfDate(HomeworkDate date) {
        for(int i = 0; i < dates.size(); i++) {
            if(dates.get(i).date().equals(date.date())) {
                return i;
            }
        }
        return -1;
    }

    /*
    * @return: first lesson of the transferred subject on this day (-1 if not exist)
    */
    private int getLesson(Date date, int subject_index) {
        int result = -1;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_WEEK)-2;
        if(day < 0 || day > 4) {
            return result;
        }

        for(int i = 8; i >= 0; i--) {
            Lesson lesson = Storage.schedule.getLesson(day, i);
            if(lesson != null && lesson.getSubjectIndex() == subject_index) {
                result = i;
            }
        }

        return result;
    }

    /*
     * finds the id of the active radiobutton (kind)
     */
    private int getActiveKindRadioButton() {
        int i = 0;
        RadioButton radio_button = (RadioButton) radiogroup_kind.getChildAt(i);
        while(!short_description.equals(radio_button.getText().toString())) {
            i++;
            radio_button = (RadioButton) radiogroup_kind.getChildAt(i);
        }
        return radio_button.getId();
    }

    /**
     * updates the view switcher which contains the description images
     * reading the description image uris from the file
     * if the size of the read list is 0, the textview is activated
     * else the read list is set into the image adapter
     */
    private void updateImageView() {
        if(des_images.size() > 0) {
            vs_image_description.switchToView(1);
            HorizontalImageListView listview = view.findViewById(R.id.listview_description_homework);
            listview.setImageList(des_images);
        }
        else {
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

    //******************************************************* communication with activity *******************************************************//
    public CallbackMethods methods;
    public interface CallbackMethods {
        void onSubjectChanged(int subject_index);
        void onKindChanged(String kind);
        void setDate(HomeworkDate date);
        void setSubjectIndex(int subject_index);
        void setDescription(String description);
        void setDescriptionImages(ArrayList<Uri> images_description);
        void setShortDescription(String short_description);
        void setMisc(String misc);
    }
    public void setCallBackMethods(CallbackMethods methods) {
        this.methods = methods;
    }


    public void openDatePickerDialog() {
        spinner_date.openDateDialog();
    }
    public void setSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public void setHomeworkDate(HomeworkDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setImageDescription(ArrayList<Uri> des_images) { this.des_images = des_images; }
    public void setShortDescription(String short_description) { this.short_description = short_description; }
    public void setMisc(String misc) { this.misc = misc; }
    public void setExisting(boolean existing) { this.existing = existing; }
    public void setMode(boolean is_single_mode) { this.is_single_mode = is_single_mode; }
    public void getHomework() {
        if(methods != null) {
            methods.setSubjectIndex(subject_index);
            methods.setDate(new HomeworkDate(spinner_date.getDate(), getLesson(spinner_date.getDate(), subject_index)));
            methods.setDescription(ta_description.getText().toString());
            methods.setShortDescription(short_description);
            methods.setMisc(ed_misc.getText().toString());
        }
    }
}

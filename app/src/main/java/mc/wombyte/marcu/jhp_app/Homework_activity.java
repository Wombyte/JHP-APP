package mc.wombyte.marcu.jhp_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Classes.HomeworkDate;
import mc.wombyte.marcu.jhp_app.Classes.HomeworkSolution;

/**
 * Created by marcu on 19.07.2017.
 */

public class Homework_activity extends JHP_Activity {

    ImageButton b_back;
    ImageButton b_fragment;
    TextView tv_heading_subject;
    TextView tv_heading_kind;

    FragmentManager fm = getFragmentManager();
    Homework_facts_fragment facts_fragment;
    Homework_edit_fragment edit_fragment;
    boolean facts_fragment_active = true;

    ArrayList<Option> facts_options = new ArrayList<>();
    ArrayList<Option> edit_options = new ArrayList<>();
    Option false_option;
    Option true_option;

    int subject_index = -1;
    int homework_index = -1;
    HomeworkDate date;
    String description = "";
    ArrayList<Uri> des_images = new ArrayList<>();
    String short_description = "";
    String misc = "";
    HomeworkSolution solution = new HomeworkSolution();

    Class previous_class;
    boolean existing;
    final boolean mode_pl = false;
    final boolean mode_sg = true;
    boolean mode = mode_pl;

    int light_color;
    int dark_color;
    int text_color;
    int heading_color;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_activity);

        //file structure
        new FileSaver(this).prepareNewHomeworkFolder();

        //initialization
        b_back = findViewById(R.id.b_back_homework);
        b_fragment = findViewById(R.id.b_fragment_homework);
        tv_heading_subject = findViewById(R.id.tv_heading_subject_homework);
        tv_heading_kind = findViewById(R.id.tv_heading_kind_homework);

        //callback: facts fragment
        facts_fragment = new Homework_facts_fragment();
        facts_fragment.setCallBackMethods(new Homework_facts_fragment.CallbackMethods() {
            @Override public void onSubjectChanged(int subject_index) { changeActivityColor(subject_index);}
            @Override public void onKindChanged(String kind) { setKindText(kind); }
            @Override public void setDate(HomeworkDate date) { setHomeworkDate(date); }
            @Override public void setSubjectIndex(int subject_index) { setHomeworkSubjectIndex(subject_index); }
            @Override public void setDescription(String description) { setHomeworkDescription(description); }
            @Override public void setDescriptionImages(ArrayList<Uri> des_images) { setHomeworkImageDescription(des_images); }
            @Override public void setShortDescription(String short_description) { setHomeworkShortDescription(short_description); }
            @Override public void setMisc(String misc) { setHomeworkMisc(misc); }
        });

        //callback: edit_fragment
        edit_fragment = new Homework_edit_fragment();
        edit_fragment.setCallbackMethods(new Homework_edit_fragment.CallbackMethods() {
            @Override public void setSolution(HomeworkSolution solution) {
                setHomeworkSolution(solution);
            }
            @Override public void setState(boolean isfinished) {
                changeStateOption(isfinished);
            }
        });

        //intent
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");
        if(getIntent().hasExtra("SUBJECT_INDEX")) {
            subject_index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        }
        if(getIntent().hasExtra("HOMEWORK_INDEX")) {
            homework_index = (int) getIntent().getSerializableExtra("HOMEWORK_INDEX");
        }

        //vars
        if(previous_class == MainActivity.class) {
            mode = mode_pl;
        }
        else {
            mode = mode_sg;
        }

        existing = false;
        if(subject_index < Storage.homework.size() && subject_index >= 0) { //defining whether the homework already exists
            if(homework_index < Storage.homework.get(subject_index).size() && homework_index >= 0) {
                existing = true;
            }
        }
        if(existing) {
            solution = Storage.homework.get(subject_index).get(homework_index).getSolution();
            solution.setImages( Storage.homework.get(subject_index).get(homework_index).readSolutionImages());

            date = Storage.homework.get(subject_index).get(homework_index).getDate();
            description = Storage.homework.get(subject_index).get(homework_index).getDescription();
            des_images = Storage.homework.get(subject_index).get(homework_index).readDescriptionImages();
            short_description = Storage.homework.get(subject_index).get(homework_index).getShortDescription();
            misc = Storage.homework.get(subject_index).get(homework_index).getMisc();
        }
        else {
            short_description = getResources().getString(R.string.homework_kind_homework);
        }
        changeActivityColor(subject_index);
        tv_heading_kind.setText(short_description);


        //input_listener
        b_back.setOnClickListener((view) -> onclick_back());
        b_fragment.setOnClickListener((view) -> onclick_fragment());

        //fragment
        FragmentTransaction ft = fm.beginTransaction();
        facts_fragment.setSubjectIndex(subject_index);
        facts_fragment.setHomeworkDate(date);
        facts_fragment.setDescription(description);
        facts_fragment.setImageDescription(des_images);
        facts_fragment.setShortDescription(short_description);
        facts_fragment.setMisc(misc);
        facts_fragment.setExisting(existing);
        facts_fragment.setMode(mode);
        ft.add(R.id.container_fragment_homework, facts_fragment);
        ft.commit();

        //options
        facts_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        facts_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_edit),
                getResources().getString(R.string.homework_option_fragment_edit)
        ));
        facts_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_date_picker),
                getResources().getString(R.string.homework_option_calendar)
        ));
        facts_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_add_image),
                getResources().getString(R.string.homework_option_add_image)
        ));
        options = (ArrayList<Option>) facts_options.clone();

        edit_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        edit_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_homework_facts),
                getResources().getString(R.string.homework_option_fragment_facts)
        ));
        edit_options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_homework_text),
                getResources().getString(R.string.homework_option_edit_text)
        ));
        edit_options.add(new Option(
                getResources().getColor(R.color.homework_solution_images_light),
                getResources().getColor(R.color.homework_solution_images),
                getResources().getDrawable(R.drawable.symbol_homework_image),
                getResources().getString(R.string.homework_option_edit_images)
        ));
        edit_options.add(new Option(
                getResources().getColor(R.color.homework_solution_docs_light),
                getResources().getColor(R.color.homework_solution_docs),
                getResources().getDrawable(R.drawable.symbol_homework_docs),
                getResources().getString(R.string.homework_option_edit_docs)
        ));
        edit_options.add(new Option(
                getResources().getColor(R.color.homework_solution_sheets_light),
                getResources().getColor(R.color.homework_solution_sheets),
                getResources().getDrawable(R.drawable.symbol_homework_table),
                getResources().getString(R.string.homework_option_edit_sheets)
        ));
        edit_options.add(new Option(
                getResources().getColor(R.color.homework_solution_slides_light),
                getResources().getColor(R.color.homework_solution_slides),
                getResources().getDrawable(R.drawable.symbol_homework_slides),
                getResources().getString(R.string.homework_option_edit_slides)
        ));

        true_option = new Option(
                getResources().getColor(R.color.background),
                getResources().getColor(R.color.homework_true),
                getResources().getDrawable(R.drawable.symbol_homework_true),
                getResources().getString(R.string.homework_option_edit_true)
        );
        false_option = new Option(
                getResources().getColor(R.color.background),
                getResources().getColor(R.color.homework_false),
                getResources().getDrawable(R.drawable.symbol_homework_false),
                getResources().getString(R.string.homework_option_edit_false)
        );

        if(solution.isFinished()) {
            edit_options.add(false_option);
        }
        else {
            edit_options.add(true_option);
        }

        setMenuContainerId(R.id.homework_scroll_container);
        setOptionId(1);
        setOptions();
    }

    //******************************************************* option *******************************************************//
    /*
     * actions for option list
     */
    @Override public void optionActions(int i) {
        if(facts_fragment_active) {
            switch(i) {
                case 0: onclick_back(); break;
                case 1: onclick_fragment(); break;
                case 2: facts_fragment.openDatePickerDialog(); break;
                case 3: facts_fragment.openImagePickerDialog(); break;
            }
        }
        else {
            switch(i) {
                case 0: onclick_back(); break;
                case 1: onclick_fragment(); break;
                case 2: edit_fragment.activateView(0); break;
                case 3: edit_fragment.activateView(1); break;
                case 4: edit_fragment.activateView(2); break;
                case 5: edit_fragment.activateView(3); break;
                case 6: edit_fragment.activateView(4); break;
                case 7: edit_fragment.changeState(); break;
            }
        }
    }

    //******************************************************* activity *******************************************************//
    /*
     * changes the color of the toolbar
     */
    public void changeActivityColor(int subject_index) {
        this.subject_index = subject_index;

        if(subject_index > -1) {
            light_color = Storage.subjects.get(subject_index).getColor();
            dark_color = Storage.subjects.get(subject_index).getDarkColor();
            text_color = getResources().getColor(R.color.background);
            heading_color = Storage.subjects.get(subject_index).getDarkColor();
        }
        else {
            light_color = getResources().getColor(R.color.background);
            dark_color = getResources().getColor(R.color.colorPrimary);
            text_color = getResources().getColor(R.color.colorPrimaryDark);
            heading_color = getResources().getColor(R.color.background);
        }

        //text_color
        changeImageButtonColor(b_back.getId(), text_color);
        changeImageButtonColor(b_fragment.getId(), text_color);
        tv_heading_subject.setTextColor(text_color);
        tv_heading_kind.setTextColor(text_color);

        //heading color
        b_back.getBackground().getCurrent().setColorFilter( heading_color, PorterDuff.Mode.SRC_ATOP);
        b_back.invalidate();
        b_fragment.getBackground().getCurrent().setColorFilter( heading_color, PorterDuff.Mode.SRC_ATOP);
        b_fragment.invalidate();
        tv_heading_kind.getBackground().getCurrent().setColorFilter( heading_color, PorterDuff.Mode.SRC_ATOP);
        tv_heading_subject.getBackground().getCurrent().setColorFilter( heading_color, PorterDuff.Mode.SRC_ATOP);

        //content
        if(subject_index != -1) {
            tv_heading_subject.setText(Storage.subjects.get(subject_index).getName());
        }
    }

    //******************************************************* com to facts fragment *******************************************************//

    /*
     * changes the text of the "kind" heading
     */
    public void setKindText(String kind) {
        tv_heading_kind.setText(kind);
    }

    /*
     * set Homework facts
     */
    public void setHomeworkDate(HomeworkDate date) { this.date = date; }
    public void setHomeworkSubjectIndex(int subject_index) { this.subject_index = subject_index; }
    public void setHomeworkDescription(String description) { this.description = description; }
    public void setHomeworkImageDescription(ArrayList<Uri> des_images) { this.des_images = des_images; }
    public void setHomeworkShortDescription(String short_description) { this.short_description = short_description; }
    public void setHomeworkMisc(String misc) { this.misc = misc; }

    //******************************************************* com to edit fragment *******************************************************//
    /*
     * set Homework solution
     */
    public void setHomeworkSolution(HomeworkSolution solution) { this.solution = solution; }
    public void changeStateOption(boolean isfinished) {
        solution.setState(isfinished);
        int index = options.size()-1;

        if(solution.isFinished()) {
            edit_options.set(index, false_option);
        }
        else {
            edit_options.set(index, true_option);
        }
        options = (ArrayList<Option>) edit_options.clone();
        setOptions();
    }

    /*
     * deals with the intent from the gallery / camera
     * adds the image to edit_fragments listview
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK) {
            Uri uri = ImagePickerDialog.getUri(requestCode, imageReturnedIntent);

            String homework_name = "newHomework";
            if(existing) {
                homework_name = subject_index + "_" + homework_index;
            }

            switch(requestCode/10 * 10) {
                case ImagePickerDialog.HOMEWORK_SOLUTION_IMAGE:
                    edit_fragment.addImage(uri);
                    new FileSaver(this).saveHomeworkSolutionImage(uri, homework_name);
                    break;
                case ImagePickerDialog.HOMEWORK_DESCRIPTION_IMAGE:
                    facts_fragment.addImage(uri);
                    new FileSaver(this).saveHomeworkDescriptionImage(uri, homework_name);
                    break;
            }
        }
    }

    //******************************************************* methods *******************************************************//
    /*
     * changes the color of the Drawable of an Imagebutton
     */
    private void changeImageButtonColor(int image_id, int color) {
        ImageButton b_image = findViewById(image_id);
        Drawable symbol = b_image.getDrawable();
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        b_image.setImageDrawable(symbol);
    }

    //******************************************************* onclick input_listener *******************************************************//
    /*
     * onclick input_listener for the button back
     */
    private void onclick_back() {
        saveData();
        Intent toPreviousClass = new Intent();
        toPreviousClass.setClass(this, previous_class);
        toPreviousClass.putExtra("FRAGMENT_INDEX", 2);
        if(mode == mode_sg) {
            toPreviousClass.putExtra("SUBJECT_INDEX", subject_index);
        }
        this.startActivity(toPreviousClass);
    }

    /**
     * saves all the data entered by the user
     * the homework from {@link Homework_edit_fragment} is saved before
     * if the date is defined
     *      if the homework exists, data is overwritten
     *      else new homework is created
     */
    private void saveData() {
        FileSaver fileSaver = new FileSaver(this);
        if(facts_fragment_active) {
            facts_fragment.getHomework();
        }

        if(date != null) {
            if(existing) {
                Storage.homework.get(subject_index).get(homework_index).setDate(date);
                Storage.homework.get(subject_index).get(homework_index).setShortDescription(short_description);
                if(short_description.equals( getResources().getString(R.string.homework_kind_misc))) {
                    Storage.homework.get(subject_index).get(homework_index).setMisc(misc);
                }
                Storage.homework.get(subject_index).get(homework_index).setDescription(description);
                Storage.homework.get(subject_index).get(homework_index).setSolution(solution);
            }
            else {
                if(subject_index == -1 || description.equals("")) {
                    fileSaver.clearTempFolder();
                    return;
                }
                Storage.homework.get(subject_index).add(new Homework(
                        subject_index,
                        Storage.homework.get(subject_index).size(),
                        date,
                        tv_heading_kind.getText().toString(),
                        misc,
                        description,
                        solution
                ));
                existing = true;
                homework_index = Storage.homework.get(subject_index).size()-1;

                String name = Storage.homework.get(subject_index).get(homework_index).getFileName();
                fileSaver.renameTempImages(name, FileSaver.HOMEWORK_TEMP_IMAGE);
                fileSaver.renameNewHomeworkFolderTo(name);
            }
            fileSaver.saveData();
        }

    }

    /*
     * changes the fragment
     */
    private void onclick_fragment() {
        if(subject_index == -1) {
            return;
        }

        facts_fragment_active = !facts_fragment_active;

        if(facts_fragment_active) {
            FragmentTransaction ft = fm.beginTransaction();
            facts_fragment.setSubjectIndex(subject_index);
            facts_fragment.setHomeworkDate(date);
            facts_fragment.setDescription(description);
            facts_fragment.setImageDescription(des_images);
            facts_fragment.setShortDescription(short_description);
            facts_fragment.setMisc(misc);
            facts_fragment.setExisting(existing);
            facts_fragment.setMode(mode);
            ft.replace(R.id.container_fragment_homework, facts_fragment);
            ft.commit();

            b_fragment.setImageResource(R.drawable.symbol_edit);
            changeImageButtonColor(b_fragment.getId(), text_color);
            options = (ArrayList<Option>) facts_options.clone();
        }
        else {
            FragmentTransaction ft = fm.beginTransaction();
            edit_fragment.setSolution(solution);
            edit_fragment.setLightColor(light_color);
            edit_fragment.setDarkColor(dark_color);
            ft.replace(R.id.container_fragment_homework, edit_fragment);
            ft.commit();

            b_fragment.setImageResource(R.drawable.symbol_homework_facts);
            changeImageButtonColor(b_fragment.getId(), text_color);
            options = (ArrayList<Option>) edit_options.clone();
        }
        setOptions();
    }
}
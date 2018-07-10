package mc.wombyte.marcu.jhp_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.Classes.Subject;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

public class Subject_activity extends JHP_Activity {

    FragmentManager fm_menu = getFragmentManager();
    FragmentManager fm_scroll = getFragmentManager();

    RelativeLayout toolbar;
    ViewSwitcher vs_heading;
    RelativeLayout fragment_container;
    LinearLayout menu_container;
    RelativeLayout scroll_container;
    RelativeLayout[] menu_part;
    ImageView[] menu_image;
    TextView[] menu_text;
    FragmentSubject[] fragment = new FragmentSubject[] {
            new Schedule_sg_fragment(),
            new Subjects_sg_fragment(),
            new Homework_sg_fragment(),
            new Grades_sg_fragment()
    };
    Activity_scroll_fragment scroll_fragment = new Activity_scroll_fragment();

    //variables
    int current_fragment_index = 1;
    int index = -1;
    int[] symbol1_id = new int[] {
            R.drawable.symbol_schedule,
            R.drawable.symbol_subjects,
            R.drawable.symbol_homework,
            R.drawable.symbol_grades
    };
    int[] symbol2_id = new int[] {
            R.drawable.symbol_schedule,
            R.drawable.symbol_subjects_change,
            R.drawable.symbol_homework_add,
            R.drawable.symbol_grades_add
    };
    int symbol_subject_name = R.drawable.symbol_true;
    String[] string1;
    String[] string2;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_activity);

        //Intent
        index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        current_fragment_index = (int) getIntent().getSerializableExtra("FRAGMENT_INDEX");

        //declaring variables
        toolbar = (RelativeLayout) findViewById(R.id.toolbar_subject);
        vs_heading = (ViewSwitcher) findViewById(R.id.viewswitcher_heading_subject);
        fragment_container = (RelativeLayout) findViewById(R.id.fragment_container_subject);
        menu_container = (LinearLayout) findViewById(R.id.menu_container_subject);
        scroll_container = (RelativeLayout) findViewById(R.id.scroll_container_subject);
        menu_part = new RelativeLayout[] {
                (RelativeLayout) findViewById(R.id.menu_schedule_container_subject),
                (RelativeLayout) findViewById(R.id.menu_subjects_container_subject),
                (RelativeLayout) findViewById(R.id.menu_homework_container_subject),
                (RelativeLayout) findViewById(R.id.menu_grades_container_subject)
        };
        menu_image = new ImageView [] {
                (ImageView) findViewById(R.id.image_menu_schedule_subject),
                (ImageView) findViewById(R.id.image_menu_subjects_subject),
                (ImageView) findViewById(R.id.image_menu_homework_subject),
                (ImageView) findViewById(R.id.image_menu_grades_subject)
        };
        menu_text = new TextView[] {
                (TextView) findViewById(R.id.tv_menu_schedule_subject),
                (TextView) findViewById(R.id.tv_menu_subjects_subject),
                (TextView) findViewById(R.id.tv_menu_homework_subject),
                (TextView) findViewById(R.id.tv_menu_grades_subject)
        };

        string1 = new String[] {
                getResources().getString(R.string.schedule_subject_menu),
                getResources().getString(R.string.subjects_subject_menu),
                getResources().getString(R.string.homework_subject_menu),
                getResources().getString(R.string.grades_subject_menu)
        };
        string2 = new String[] {
                getResources().getString(R.string.schedule_subject_menu),
                getResources().getString(R.string.subjects_name_subject_menu),
                getResources().getString(R.string.homework_new_subject_menu),
                getResources().getString(R.string.grades_new_subject_menu)
        };

        //colors
        toolbar.setBackground( changeDrawableItemColor(R.drawable.heading, R.id.heading_background_color, Storage.subjects.get(index).getDarkColor()));
        for(int i = 0; i < 4; i++) {
            if(i != current_fragment_index) {
                enableMenuItem(i);
            }
            else {
                disableMenuItem(i);
            }
        }

        //text
        vs_heading.createView(this).switchToView(0);
        TextView tv = (TextView) vs_heading.getView(0);
        tv.setText( Storage.subjects.get(index).getName() );

        //input_listener
        menu_container.setOnTouchListener(new Menu_listener(getApplicationContext()) {
            @Override public void onSwipeLeft() {
                swipeToRight();
            }
            @Override public void onSwipeRight() {
                swipeToLeft();
            }
            @Override public void onPart(int part) { onclick_menu(part); }
        });

        //set the first fragment
        FragmentTransaction ft_menu = fm_menu.beginTransaction();
        fragment[current_fragment_index].setSubjectIndex(index);
        ft_menu.add(R.id.fragment_container_subject, fragment[current_fragment_index], "GENERAL");
        ft_menu.commit();

        //options
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_home),
                getResources().getString(R.string.option_back_home)
        ));
        for(int i = 0; i < Storage.subjects.size(); i++) {
            Subject subject = Storage.subjects.get(i);
            options.add(new Option(
                    subject.getColor(),
                    subject.getDarkColor(),
                    subject.getAbbreviation().toUpperCase(),
                    subject.getName()
            ));
        }

        setMenuContainerId(R.id.subject_scroll_container);
        setOptionId(index+1);
        setOptions();
    }

    @Override protected void onPause() {
        super.onPause();
        fragment[current_fragment_index].saveData();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    //******************************************************* option *******************************************************//
    /*
     * actions for option list
     */
    @Override public void optionActions(int i) {
        if(i > 0) {
            if(i != symbol_subject_name+1) {
                Intent toSubjectActivity = new Intent();
                toSubjectActivity.setClass(this, Subject_activity.class);
                toSubjectActivity.putExtra("SUBJECT_INDEX", i - 1);
                toSubjectActivity.putExtra("FRAGMENT_INDEX", current_fragment_index);
                this.startActivity(toSubjectActivity);
            }
        }
        else {
            Intent toMainActivity = new Intent();
            toMainActivity.setClass(this, MainActivity.class);
            toMainActivity.putExtra("FRAGMENT_INDEX", current_fragment_index);
            this.startActivity(toMainActivity);
        }
    }

    //******************************************************* method *******************************************************//

    /*
     * opens a new Fragment where the user can scrolledBy thru all the subjects
     */
    public void openScrollFragment() {
        //save written data
        fragment[current_fragment_index].saveData();
    }

    /*
     * seeks the fragment right of the current fragment and replaces it
     */
    private void swipeToRight() {
        int part = Math.min(current_fragment_index+1, 3);
        onclick_menu(part);
    }

    /*
     * seeks the fragment left of the current fragment and replaces it
     */
    private void swipeToLeft() {
        int part = Math.max(current_fragment_index-1, 0);
        onclick_menu(part);
    }

    /*
     * onclick input_listener for all the menu buttons
     */
    public void onclick_menu(int part) {
        if(part != current_fragment_index) {
            enableMenuItem(current_fragment_index);

            FragmentTransaction tf_menu = fm_menu.beginTransaction();
            fragment[part].setSubjectIndex(index);
            tf_menu.replace(R.id.fragment_container_subject, fragment[part]);
            current_fragment_index = part;
            tf_menu.commit();

            disableMenuItem(current_fragment_index);
        }
        else {
            if(part == 1) {
                if(vs_heading.isView(0)) {
                    menu_image[1].setImageDrawable( changeSymbolColor(symbol_subject_name, getResources().getColor(R.color.background)));
                    vs_heading.switchToView(1);
                    EditText ed = (EditText) vs_heading.getView(1);
                    ed.setText(Storage.subjects.get(index).getName());
                    ed.setBackground(changeDrawableItemColor(R.drawable.textarea, R.id.textarea_border, Storage.subjects.get(index).getDarkColor()));
                    ed.requestFocus();
                    ed.selectAll();
                }
                else {
                    menu_image[1].setImageDrawable( changeSymbolColor(symbol2_id[1], getResources().getColor(R.color.background)));
                    vs_heading.switchToView(0);
                    Storage.subjects.get(index).setName( ((EditText)vs_heading.getView(1)).getText().toString());
                    TextView tv = (TextView) vs_heading.getView(0);
                    tv.setText(Storage.subjects.get(index).getName());
                }
            }
            fragment[part].extraFunction();
        }
    }

    /*
     * enable the second function of the menu part
     * changes color, text and symbol
     */
    private void disableMenuItem(int part) {
        int background_color = Storage.subjects.get(index).getDarkColor();
        int symbol_color = getResources().getColor(R.color.background);
        int symbol_id = symbol2_id[part];
        int text_color = getResources().getColor(R.color.font);

        menu_part[part].setBackgroundColor(background_color);
        menu_image[part].setImageDrawable( changeSymbolColor(symbol_id, symbol_color));
        menu_text[part].setText(string2[part]);
        menu_text[part].setTextColor(text_color);
    }

    /*
     * disenable the second function of the menu part
     * and enables the fist one to swap to the fragment
     * changes color, text and symbol
     */
    private void enableMenuItem(int part) {
        int background_color = getResources().getColor(R.color.background);
        int symbol_color = Storage.subjects.get(index).getDarkColor();
        int symbol_id = symbol1_id[part];
        int text_color = Storage.subjects.get(index).getDarkColor();

        menu_part[part].setBackgroundColor(background_color);
        menu_image[part].setImageDrawable( changeSymbolColor(symbol_id, symbol_color));
        menu_text[part].setText(string1[part]);
        menu_text[part].setTextColor(text_color);
    }

    /*
     * change the color of a drawable with item-list
     */
    private Drawable changeDrawableItemColor(int drawable_id, int layer_id, int color) {
        Drawable drawable = getResources().getDrawable(drawable_id);
        GradientDrawable layer = (GradientDrawable) ((LayerDrawable) drawable).findDrawableByLayerId(layer_id);
        layer.setColor(color);
        return drawable;
    }

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

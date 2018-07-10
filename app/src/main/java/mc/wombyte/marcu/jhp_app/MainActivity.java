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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.Classes.Subject;

public class MainActivity extends JHP_Activity {

    FragmentManager fm_menu = getFragmentManager();

    //views
    RelativeLayout toolbar;
    ImageButton b_settings;
    ImageButton b_archive;
    //Button b_test;
    RelativeLayout fragment_container;
    LinearLayout menu_container;
    RelativeLayout scroll_container;
    RelativeLayout[] menu_part;
    ImageView[] menu_image;
    TextView[] menu_text;
    FragmentMain[] fragment = new FragmentMain[] {
            new Schedule_pl_fragment(),
            new Subjects_pl_fragment(),
            new Homework_pl_fragment(),
            new Grades_pl_fragment()
    };
    Activity_scroll_fragment scroll_fragment = new Activity_scroll_fragment();

    //variables
    int current_fragment_index = 1;
    int[] symbol1_id = new int[] {
            R.drawable.symbol_schedule,
            R.drawable.symbol_subjects,
            R.drawable.symbol_homework,
            R.drawable.symbol_grades
    };
    int[] symbol2_id = new int[] {
            R.drawable.symbol_schedule,
            R.drawable.symbol_subjects_add,
            R.drawable.symbol_homework_add,
            R.drawable.symbol_grades_add
    };
    int symbol_grades_diagram = R.drawable.symbol_grades_diagram;
    String[] string1;
    String[] string2;
    String string_grades_diagram;

    //******************************************************* Override methods *******************************************************//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //declaring variables
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        b_archive = findViewById(R.id.b_archive);
        b_settings = (ImageButton) findViewById(R.id.b_settings);
        //b_test = findViewById(R.id.b_test);
        fragment_container = (RelativeLayout) findViewById(R.id.fragment_container);
        menu_container = (LinearLayout) findViewById(R.id.menu_container);
        scroll_container = (RelativeLayout) findViewById(R.id.scroll_container);
        menu_part = new RelativeLayout[] {
                (RelativeLayout) findViewById(R.id.menu_schedule_container),
                (RelativeLayout) findViewById(R.id.menu_subjects_container),
                (RelativeLayout) findViewById(R.id.menu_homework_container),
                (RelativeLayout) findViewById(R.id.menu_grades_container)
        };
        menu_image = new ImageView [] {
                (ImageView) findViewById(R.id.image_menu_schedule),
                (ImageView) findViewById(R.id.image_menu_subjects),
                (ImageView) findViewById(R.id.image_menu_homework),
                (ImageView) findViewById(R.id.image_menu_grades)
        };
        menu_text = new TextView[] {
                (TextView) findViewById(R.id.tv_menu_schedule),
                (TextView) findViewById(R.id.tv_menu_subjects),
                (TextView) findViewById(R.id.tv_menu_homework),
                (TextView) findViewById(R.id.tv_menu_grades)
        };

        string1 = new String[] {
                getResources().getString(R.string.schedule_menu),
                getResources().getString(R.string.subjects_menu),
                getResources().getString(R.string.homework_menu),
                getResources().getString(R.string.grades_menu)
        };
        string2 = new String[] {
                getResources().getString(R.string.schedule_menu),
                getResources().getString(R.string.subjects_new),
                getResources().getString(R.string.homework_new),
                getResources().getString(R.string.grades_new)
        };
        string_grades_diagram = getResources().getString(R.string.grades_diagram);

        //Intent
        if(getIntent().hasExtra("FRAGMENT_INDEX")) {
            current_fragment_index = (int) getIntent().getSerializableExtra("FRAGMENT_INDEX");
        }

        askForPermissions();
        saveBackupIfNeeded();

        //options
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_home),
                getResources().getString(R.string.main_option_home)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_semesters),
                getResources().getString(R.string.main_option_archive)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_settings),
                getResources().getString(R.string.main_option_settings)
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

        //input_listener
        b_settings.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                openSettingsActivity();
            }
        });
        b_archive.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                openArchiveActivity();
            }
        });
        //b_test.setOnClickListener((v) -> openTestAcivity());
        menu_container.setOnTouchListener(new Menu_listener(getApplicationContext()) {
            @Override public void onSwipeLeft() {
                swipeToRight();
            }
            @Override public void onSwipeRight() {
                swipeToLeft();
            }
            @Override public void onPart(int part) { onclick_menu(part); }
        });

        //colors
        toolbar.setBackground( changeDrawableItemColor(
                R.drawable.heading,
                R.id.heading_background_color,
                getResources().getColor(R.color.background))
        );
        for(int i = 0; i < 4; i++) {
            if(i != current_fragment_index) {
                enableMenuItem(i);
            }
            else {
                disableMenuItem(i);
            }
        }

        //set the first fragment
        FragmentTransaction ft_menu = fm_menu.beginTransaction();
        ft_menu.add(R.id.fragment_container, fragment[current_fragment_index], "SUBJECTS");
        ft_menu.commit();
        disableMenuItem(current_fragment_index);

        setMenuContainerId(R.id.scroll_container);
        setOptionId(0);
        setOptions();
    }

    @Override protected void onPause() {
        super.onPause();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /*
     * actions for option list
     */
    @Override public void optionActions(int i) {
        if(i > 2) {
            Intent toSubjectActivity = new Intent();
            toSubjectActivity.setClass(this, Subject_activity.class);
            toSubjectActivity.putExtra("SUBJECT_INDEX", i - 3);
            toSubjectActivity.putExtra("FRAGMENT_INDEX", current_fragment_index);
            this.startActivity(toSubjectActivity);
        }
        else {
            switch(i) {
                case 0: break;
                case 1: openArchiveActivity(); break;
                case 2: openSettingsActivity(); break;
            }
        }
    }

    //******************************************************* input_listener *******************************************************//

    /*
     * opens the archive activity
     */
    private void openArchiveActivity() {
        Intent toArchiveActivity = new Intent();
        toArchiveActivity.setClass(this, Archive_activity.class);
        this.startActivity(toArchiveActivity);
    }

    /*
     * onclick method for b_settings
     * opens the activity for the settings
     */
    private void openSettingsActivity() {
        Intent toSettingsActivity = new Intent();
        toSettingsActivity.setClass(this, Settings_activity.class);
        this.startActivity(toSettingsActivity);
    }

    private void openTestAcivity() {
        Intent toTestActivity = new Intent();
        toTestActivity.setClass(this, DriveActivity.class);
        this.startActivity(toTestActivity);
    }

    //******************************************************* Methods *******************************************************//

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
     * checks whether the index has changed
     * if: change fragment
     * else: call extra-function of this fragment
     */
    private void onclick_menu(int part) {
        if(part != current_fragment_index) {
            enableMenuItem(current_fragment_index);

            FragmentTransaction tf_menu = fm_menu.beginTransaction();
            tf_menu.replace(R.id.fragment_container, fragment[part]);
            current_fragment_index = part;
            tf_menu.commit();

            disableMenuItem(current_fragment_index);
        }
        else {
            fragment[part].extraFunction();
        }
    }

    /*
     * enable the second function of the menu part
     * changes color, text and symbol
     */
    private void disableMenuItem(int part) {
        int background_color = getResources().getColor(R.color.colorPrimary);
        int symbol_color = getResources().getColor(R.color.background);
        Drawable symbol = getResources().getDrawable(symbol2_id[part]);
        int text_color = getResources().getColor(R.color.font);
        String text = string2[part];

        if(part == 2) {
            int subject_id = Storage.getLastSubjectId();
            if (subject_id != -1) {
                background_color = Storage.subjects.get(subject_id).getColor();
            }
        }
        if(part == 3) {
            int subject_id = Storage.getLastSubjectId();
            if (subject_id != -1 && Storage.settings.grades_isSpecificGradePossible()) {
                background_color = Storage.subjects.get(subject_id).getColor();
            }
            else {
                symbol = getResources().getDrawable(symbol_grades_diagram);
                text = string_grades_diagram;
            }
        }

        menu_part[part].setBackgroundColor(background_color);
        symbol.setColorFilter(symbol_color, PorterDuff.Mode.SRC_ATOP);
        menu_image[part].setImageDrawable(symbol);
        menu_text[part].setText(text);
        menu_text[part].setTextColor(text_color);
    }

    /*
     * disenable the second function of the menu part
     * and enables the fist one to swap to the fragment
     * changes color, text and symbol
     */
    private void enableMenuItem(int part) {
        int background_color = getResources().getColor(R.color.background);
        int symbol_color = getResources().getColor(R.color.colorPrimaryDark);
        Drawable symbol = getResources().getDrawable(symbol1_id[part]);
        int text_color = getResources().getColor(R.color.colorPrimary);

        menu_part[part].setBackgroundColor(background_color);
        symbol.setColorFilter(symbol_color, PorterDuff.Mode.SRC_ATOP);
        menu_image[part].setImageDrawable(symbol);
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
     * saves all datas into the backup folder
     */
    private void saveBackupIfNeeded() {
        if(!Storage.settings.general_BackupNeeded()) {
            return;
        }

        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveBackup();

        Storage.settings.general_setBackup(false);
    }
}

package mc.wombyte.marcu.jhp_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by marcu on 15.07.2017.
 */

public class Schedule_time_activity extends JHP_Activity {

    FragmentManager fm = getFragmentManager();

    TextView tv_heading;
    ImageButton b_back;
    RelativeLayout container;

    Settings_schedule_times_fragment fragment = new Settings_schedule_times_fragment();

    Class previous_class;
    int lesson_count = 0;
    int subject_index = -1; //only to get to the last subject activity

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_time_activity);

        //initialisation
        tv_heading = findViewById(R.id.tv_heading_schedule_time);
        b_back = findViewById(R.id.b_back_schedule_time);
        container = findViewById(R.id.fragment_container_schedule_time);

        //Extras from Intent
        lesson_count = (int) getIntent().getSerializableExtra("LESSONCOUNT");
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");
        if(getIntent().hasExtra("SUBJECT_INDEX")) {
            subject_index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        }

        //execute fragment
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setLessonCount(lesson_count);
        fragment.setOnLessonChangeListener((lesson_count -> tv_heading.setText((1+lesson_count) + ". " + getResources().getString(R.string.schedule_time_lesson))));
        ft.add(R.id.fragment_container_schedule_time, fragment);
        ft.commit();

        //text
        tv_heading.setText((1+lesson_count) + ". " + getResources().getString(R.string.schedule_time_lesson));

        //input_listener
        b_back.setOnClickListener((v) -> onclick_back());

        //options
        options.add(new Option(
                getResources().getColor(R.color.option_default_background),
                getResources().getColor(R.color.option_default_foreground),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        options.add(new Option(
                getResources().getColor(R.color.option_default_background),
                getResources().getColor(R.color.option_default_foreground),
                getResources().getDrawable(R.drawable.symbol_arrow_down),
                getResources().getString(R.string.schedule_lesson_option_down)
        ));
        options.add(new Option(
                getResources().getColor(R.color.option_default_background),
                getResources().getColor(R.color.option_default_foreground),
                getResources().getDrawable(R.drawable.symbol_arrow_up),
                getResources().getString(R.string.schedule_lesson_option_up)
        ));

        setMenuContainerId(R.id.time_scroll_container);
        setOptionId(0);
        setOptions();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////// option action /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override public void optionActions(int i) {
        switch(i) {
            case 0: onclick_back(); break;
            case 1: toNextLesson(); break;
            case 2: toPreviousLesson(); break;
        }
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override public void onPause() {
        super.onPause();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /**
     * forces the time fragment to display the previous time
     * {@link Math#max(int, int)} forces the new lesson count to be > -1
     */
    private void toPreviousLesson() {
        lesson_count = Math.max(0, lesson_count-1);
        fragment.setLessonCountTo(lesson_count);
    }

    /**
     * forces the time fragment to display the next time
     * {@link Math#max(int, int)} forces the new lesson count to be < 9
     */
    private void toNextLesson() {
        lesson_count = Math.min(8, lesson_count-1);
        fragment.setLessonCountTo(lesson_count);
    }

    /**
     * onclick input_listener for the button back
     */
    private void onclick_back() {
        Intent toMainActivity = new Intent();
        toMainActivity.setClass(this, previous_class);
        toMainActivity.putExtra("FRAGMENT_INDEX", 0);
        if(subject_index != -1) {
            toMainActivity.putExtra("SUBJECT_INDEX", subject_index);
        }
        this.startActivity(toMainActivity);
    }
}

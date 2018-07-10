package mc.wombyte.marcu.jhp_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by marcu on 15.07.2017.
 */

public class Schedule_time_activity extends JHP_Activity {

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
        tv_heading = (TextView) findViewById(R.id.tv_heading_schedule_time);
        b_back = (ImageButton) findViewById(R.id.b_back_schedule_time);
        container = (RelativeLayout) findViewById(R.id.fragment_container_schedule_time);

        //Extras from Intent
        lesson_count = (int) getIntent().getSerializableExtra("LESSONCOUNT");
        previous_class = (Class) getIntent().getSerializableExtra("PREVIOUS_CLASS");
        if(getIntent().hasExtra("SUBJECT_INDEX")) {
            subject_index = (int) getIntent().getSerializableExtra("SUBJECT_INDEX");
        }

        //open fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setLessonCount(lesson_count);
        fragment.setOnLessonChangeListener(new Settings_schedule_times_fragment.OnLessonCountChangeListener() {
            @Override public void onLessonCountChange(int lesson_count) {
                tv_heading.setText((1+lesson_count) + ". " + getResources().getString(R.string.schedule_time_lesson));
            }
        });
        ft.add(R.id.fragment_container_schedule_time, fragment);
        ft.commit();

        //text
        tv_heading.setText((1+lesson_count) + ". " + getResources().getString(R.string.schedule_time_lesson));

        //input_listener
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        setMenuContainerId(R.id.time_scroll_container);
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

    @Override public void onPause() {
        super.onPause();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /*
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

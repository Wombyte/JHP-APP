package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.Reuseables.TimePicker;

/**
 * Created by marcu on 19.12.2017.
 */

public class Settings_schedule_times_fragment extends SettingFragment {

    Context context;

    TextView[] tv_time = new TextView[9];
    TimePicker timepicker_from;
    TimePicker timepicker_to;
    RelativeLayout swipe_container;

    int lesson_count;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.settings_schedule_times_fragment, container, false);
        context = container.getContext();

        //initialisation
        tv_time[0] = (TextView) view.findViewById(R.id.tv_1_schedule_time_fragment);
        tv_time[1] = (TextView) view.findViewById(R.id.tv_2_schedule_time_fragment);
        tv_time[2] = (TextView) view.findViewById(R.id.tv_3_schedule_time_fragment);
        tv_time[3] = (TextView) view.findViewById(R.id.tv_4_schedule_time_fragment);
        tv_time[4] = (TextView) view.findViewById(R.id.tv_5_schedule_time_fragment);
        tv_time[5] = (TextView) view.findViewById(R.id.tv_6_schedule_time_fragment);
        tv_time[6] = (TextView) view.findViewById(R.id.tv_7_schedule_time_fragment);
        tv_time[7] = (TextView) view.findViewById(R.id.tv_8_schedule_time_fragment);
        tv_time[8] = (TextView) view.findViewById(R.id.tv_9_schedule_time_fragment);
        timepicker_from = (TimePicker) view.findViewById(R.id.timepicker1_schedule);
        timepicker_to = (TimePicker) view.findViewById(R.id.timepicker2_schedule);
        swipe_container = (RelativeLayout) view.findViewById(R.id.container_schedule_time_fragment);

        //content
        timepicker_from.setHeading( getResources().getString(R.string.schedule_time_from));
        timepicker_to.setHeading( getResources().getString(R.string.schedule_time_to));
        activateLesson(lesson_count, lesson_count);

        //input_listener
        swipe_container.setOnTouchListener(new Swipe_listener(context) {
            @Override public void swipeRight() {}
            @Override public void swipeLeft() {}
            @Override public void swipeTop() { down(); }
            @Override public void swipeBottom() { up(); }
        });
        timepicker_from.setTimeListener(new TimePicker.TimeListener() {
            @Override public void onTimeChangeListener(int h, int min) {
                timepicker_to.setMinTime(h, min);
            }
        });
        timepicker_to.setTimeListener(new TimePicker.TimeListener() {
            @Override
            public void onTimeChangeListener(int h, int min) {
                timepicker_from.setMaxTime(h, min);
            }
        });

        return view;
    }

    @Override public void onPause() {
        super.onPause();
        saveData();
    }

    /*
     * says the lesson which lesson it should show
     * @param 'lessonCount': 'lessonCount'th lesson
     */
    public void setLessonCount(int lesson_count) {
        this.lesson_count = lesson_count;
    }

    /*
     * scrolledBy to the next lesson
     */
    private void down() {
        saveData();
        int new_lesson_count = Math.min(lesson_count+1, 8);
        if(listener != null)
            listener.onLessonCountChange(lesson_count);

        activateLesson(lesson_count, new_lesson_count);
        lesson_count = new_lesson_count;

        if(input_listener != null && lesson_count == 8)
            input_listener.onLegitInput();
    }

    /*
     * scrolledBy to the previous lesson
     */
    private void up() {
        saveData();
        int new_lesson_count = Math.max(lesson_count-1, 0);
        if(listener != null)
            listener.onLessonCountChange(lesson_count);

        activateLesson(lesson_count, new_lesson_count);
        lesson_count = new_lesson_count;
    }

    /*
     * enables the new lesson and disables the last one
     * changes color of the textview
     */
    private void activateLesson(int last_lesson, int new_lesson) {
        tv_time[last_lesson].setTextColor( context.getResources().getColor(R.color.schedule_lesson_count_inactive));
        tv_time[new_lesson].setTextColor( context.getResources().getColor(R.color.schedule_lesson_count_active));

        timepicker_from.setTime(new_lesson, 0);
        timepicker_to.setTime(new_lesson, 1);
    }

    /*
     * saves all the changed data entered by the user
     */
    private void saveData() {
        //save entered data
        if(timepicker_from.getTime() != new int[] {0, 0}) {
            Storage.schedule.setTime(lesson_count, 0, timepicker_from.getTime());
        }
        if(timepicker_to.getTime() != new int[] {0, 0}) {
            Storage.schedule.setTime(lesson_count, 1, timepicker_to.getTime());
        }
    }

    /*
     * callback: when the lesson_count has changed
     */
    public OnLessonCountChangeListener listener = null;
    public interface OnLessonCountChangeListener {
        void onLessonCountChange(int lesson_count);
    }
    public void setOnLessonChangeListener(OnLessonCountChangeListener listener) {
        this.listener = listener;
    }
}

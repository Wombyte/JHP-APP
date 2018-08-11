package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import mc.wombyte.marcu.jhp_app.Reuseables.TimePicker;

/**
 * Created by marcu on 19.12.2017.
 */

public class Settings_schedule_times_fragment extends SettingFragment {

    Context context;

    Button[] b_times;
    TimePicker timepicker_from;
    TimePicker timepicker_to;
    RelativeLayout swipe_container;

    int lesson_count;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.settings_schedule_times_fragment, container, false);
        context = container.getContext();

        //initialisation
        b_times = new Button[] {
                view.findViewById(R.id.b_1_schedule_time_fragment),
                view.findViewById(R.id.b_2_schedule_time_fragment),
                view.findViewById(R.id.b_3_schedule_time_fragment),
                view.findViewById(R.id.b_4_schedule_time_fragment),
                view.findViewById(R.id.b_5_schedule_time_fragment),
                view.findViewById(R.id.b_6_schedule_time_fragment),
                view.findViewById(R.id.b_7_schedule_time_fragment),
                view.findViewById(R.id.b_8_schedule_time_fragment),
                view.findViewById(R.id.b_9_schedule_time_fragment)
        };
        timepicker_from = view.findViewById(R.id.timepicker1_schedule);
        timepicker_to = view.findViewById(R.id.timepicker2_schedule);
        swipe_container = view.findViewById(R.id.container_schedule_time_fragment);

        //content
        timepicker_from.setHeading(getResources().getString(R.string.schedule_time_from));
        timepicker_to.setHeading(getResources().getString(R.string.schedule_time_to));
        activateLesson(lesson_count, lesson_count);

        //input_listener
        swipe_container.setOnTouchListener(new Swipe_listener(context) {
            @Override public void swipeRight() {}
            @Override public void swipeLeft() {}
            @Override public void swipeTop() { down(); }
            @Override public void swipeBottom() { up(); }
        });
        if(!(getActivity() instanceof SetupActivity)) {
            for(int i = 0; i < 9; i++) {
                final int n = i;
                b_times[i].setOnClickListener((v -> setLessonCountTo(n)));
            }
        }
        timepicker_from.setTimeListener((h, min) -> timepicker_to.setMinTime(h, min));
        timepicker_to.setTimeListener((h, min) -> timepicker_from.setMaxTime(h, min));

        return view;
    }

    @Override public void onPause() {
        super.onPause();
        saveData();
    }

    /**
     * says the lesson which lesson it should show
     * @param lesson_count: lessonCount'th lesson
     */
    public void setLessonCount(int lesson_count) {
        this.lesson_count = lesson_count;
    }

    /**
     * changes the lesson to the transmitted lesson count
     * first data is saved, then the new lesson count
     * is bounded to [0|8]
     * {@link this#activateLesson(int, int)} is called
     * {@link this#lesson_count} is set to the new one
     * @param new_lc: number of the next lesson
     */
    private void setLessonCountTo(int new_lc) {
        saveData();

        new_lc = Math.min(new_lc, 8);
        new_lc = Math.max(new_lc, 0);

        if(listener != null)
            listener.onLessonCountChange(new_lc);

        activateLesson(lesson_count, new_lc);
        lesson_count = new_lc;
    }

    /**
     * sets the the fragment to the next lesson
     * if the next lesson is the last one,
     * the setup fragment was successful
     */
    private void down() {
        setLessonCountTo(lesson_count+1);

        if(input_listener != null && lesson_count == 8)
            input_listener.onLegitInput();
    }

    /**
     * sets the the fragment to the previous lesson
     */
    private void up() {
        setLessonCountTo(lesson_count-1);
    }

    /**
     * enables the new lesson and disables the last one
     * changes text color of the button
     * @param last_lesson: number of the last lesson
     * @param new_lesson: number of the new lesson
     */
    private void activateLesson(int last_lesson, int new_lesson) {
        b_times[last_lesson].setTextColor( context.getResources().getColor(R.color.schedule_lesson_count_inactive));
        b_times[new_lesson].setTextColor( context.getResources().getColor(R.color.schedule_lesson_count_active));

        timepicker_from.setTime(new_lesson, 0);
        timepicker_to.setTime(new_lesson, 1);
    }

    /**
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

    //callback
    public OnLessonCountChangeListener listener = null;
    public interface OnLessonCountChangeListener {
        void onLessonCountChange(int lesson_count);
    }
    public void setOnLessonChangeListener(OnLessonCountChangeListener listener) {
        this.listener = listener;
    }
}

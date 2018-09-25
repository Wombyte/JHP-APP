package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Lesson;

/**
 * Created by marcu on 5.8.2017.
 */

public class Schedule_gridview_adapter extends ArrayAdapter<Object> {

    Context context;

    Lesson lesson;
    TextView tv_subject;
    TextView tv_room;

    int[][] time;
    TextView tv_from;
    TextView tv_char;
    TextView tv_to;

    int x, y;
    int column_number = 0;
    DecimalFormat df = new DecimalFormat("00");

    public Schedule_gridview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Schedule_gridview_adapter(Context context, int resource, ArrayList<Object> items, int column_number) {
        super(context, resource, items);
        this.column_number = column_number;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        context = container.getContext();

        x = position%column_number -1;
        y = position/column_number;

        if(getItem(position) instanceof Lesson || getItem(position) == null) {
            lesson = (Lesson) getItem(position);

            view = inflater.inflate(R.layout.schedule_lesson_button, null);
            tv_subject = (TextView) view.findViewById(R.id.tv_subject_schedule);
            tv_room = (TextView) view.findViewById(R.id.tv_room_schedule);

            if(lesson != null) {
                tv_subject.setText(Storage.subjects.get(lesson.getSubjectIndex()).getAbbreviation());
                tv_subject.setBackgroundColor(Storage.subjects.get(lesson.getSubjectIndex()).getColor());
                tv_subject.setTextColor( getTextColor(lesson.getSubjectIndex()));
                tv_room.setText(lesson.getRoom());
                tv_room.setTextColor(Storage.subjects.get(lesson.getSubjectIndex()).getDarkColor());
            }
        }
        else {
            time = (int[][]) getItem(position);

            view = inflater.inflate(R.layout.schedule_time_button, null);
            tv_from = (TextView) view.findViewById(R.id.tv_from_schedule_time);
            tv_char = (TextView) view.findViewById(R.id.tv_char_schedule_time);
            tv_to = (TextView) view.findViewById(R.id.tv_to_schedule_time);

            if(timeIsSet(y)) {
                tv_from.setText( time[0][0] + ":" + df.format(time[0][1]));
                tv_char.setText("-");
                tv_to.setText( time[1][0] + ":" + df.format(time[1][1]));
            }
            else {
                tv_from.setText("");
                tv_char.setText("?");
                tv_to.setText("");
            }
        }

        return view;
    }

    /*
     * returns an adapted text color
     */
    private int getTextColor(int index) {
        float[] hsv = new float[3];
        int color = Storage.subjects.get(index).getColor();
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);

        if(hsv[1] > 0.9) {
            return context.getResources().getColor(R.color.font);
        }
        else {
            return context.getResources().getColor(R.color.colorPrimary);
        }
    }

    /**
     * checks whether the current lesson time is set
     * checks if both sums of the times aren't equal to 0
     * @param lesson: lesson of the time, that has to be checked
     * @return boolean
     */
    private boolean timeIsSet(int lesson) {
        int[][] values = Storage.schedule.getTime(lesson);
        return values[0][0] + values[0][1] != 0 && values[1][0] + values[1][1] != 0;
    }

}

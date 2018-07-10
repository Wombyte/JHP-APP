package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by marcu on 15.07.2017.
 */

public class Schedule_time_picker extends RelativeLayout {

    Context context;
    LayoutInflater inflater;
    DecimalFormat df = new DecimalFormat("00");
    TimeListener listener;

    TextView tv_heading;
    TextView tv_h;
    Button b_add_h;
    Button b_sub_h;
    TextView tv_min;
    Button b_add_min;
    Button b_sub_min;

    int min_time = 0; // = h*60 + min
    int max_time = 23*60 + 55; // = h*60 + min
    int lesson_count = -1;
    int time = -1;  //whether its start or end of lesson (0 = start, 1 = end)
    int h = 0;
    int min = 0;

    /*
     * constructors needed for xml
     */
    public Schedule_time_picker(Context context) {
        super(context);
        this.context = context;
        onCreateView();
    }

    public Schedule_time_picker(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        onCreateView();
    }

    public Schedule_time_picker(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
        this.context = context;
        onCreateView();
    }

    /*
     * method which shows the time picker
     */
    private void onCreateView() {
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.schedule_time_picker, this, true);

        tv_heading = (TextView) findViewById(R.id.tv_heading_time_picker);
        tv_h = (TextView) findViewById(R.id.tv_h_time_picker);
        b_add_h = (Button) findViewById(R.id.b_add_h_time_picker);
        b_sub_h = (Button) findViewById(R.id.b_sub_h_time_picker);
        tv_min = (TextView) findViewById(R.id.tv_min_time_picker);
        b_add_min = (Button) findViewById(R.id.b_add_min_time_picker);
        b_sub_min = (Button) findViewById(R.id.b_sub_min_time_picker);

        b_add_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_add_h();
            }
        });
        b_sub_h.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_sub_h();
            }
        });
        b_add_min.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_add_min();
            }
        });
        b_sub_min.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_sub_min();
            }
        });
    }
    
    /*
     * onclick input_listener for the button add_h
     */
    private void onclick_add_h() {
        int h_back = h;
        h += 1;
        if(h == 24)
            h = 0;
        //checks whether the new time is below the minimum
        if(h*60 + min < min_time)
            h = h_back;
        //checks whether the new time is after the maximum
        if(h*60 + min > max_time)
            h = h_back;
        tv_h.setText(df.format(h));
        //calls the callback
        if(listener != null)
            listener.onTimeChangeListener(h, min);
    }

    /*
     * onclick input_listener for the button sub_h
     */
    private void onclick_sub_h() {
        int h_back = h;
        h -= 1;
        if(h == -1) {
            h = 23;
        }
        //checks whether the new time is below the minimum
        if(h*60 + min < min_time)
            h = h_back;
        //checks whether the new time is after the maximum
        if(h*60 + min > max_time)
            h = h_back;
        tv_h.setText(df.format(h));

        //calls the callback
        if(listener != null)
            listener.onTimeChangeListener(h, min);
    }

    /*
     * onclick input_listener for the button add_min
     */
    private void onclick_add_min() {
        int min_back = min;
        min += 5;
        if(min == 60) {
            min = 0;
            onclick_add_h();
        }
        //checks whether the new time is below the minimum
        if(h*60 + min < min_time)
            min = min_back;
        //checks whether the new time is after the maximum
        if(h*60 + min > max_time)
            min = min_back;
        tv_min.setText(df.format(min));

        //calls the callback
        if(listener != null)
            listener.onTimeChangeListener(h, min);
    }

    /*
     * onclick input_listener for the button sub_min
     */
    private void onclick_sub_min() {
        int min_back = min;
        min -= 5;
        if(min == -5) {
            min = 55;
            onclick_sub_h();
        }
        //checks whether the new time is below the minimum
        if(h*60 + min < min_time)
            min = min_back;
        //checks whether the new time is after the maximum
        if(h*60 + min > max_time)
            min = min_back;
        tv_min.setText(df.format(min));

        //calls the callback
        if(listener != null)
            listener.onTimeChangeListener(h, min);
    }

    /*
     * fill in all data
     * only called when the position was set
     * defines min-time, max-time and time
     */
    private void fillData() {
        if(lesson_count != -1 && time != -1) {
            //if a later time is set, the start-time of the subject becomes the max-time for the time-picker
            int i = lesson_count+1;
            max_time = 23*60 + 55;
            min_time = 0;
            boolean end = false;
            while(i < 9 && !end) {
                if( timeIsSet(i, 0)) {
                    h = Storage.schedule.getTime(i, 0)[0];  // i. hour, from (0), h (0)
                    min = Storage.schedule.getTime(i, 0)[1]; // i. hour, from (0), h (1)
                    setMaxTime(h, min);
                    end = true;
                }
                i++;
            }
            //if a previous time is set, the end-time of this subject becomes the min-time for the time picker
            i = lesson_count-1;
            end = false;
            while(i >= 0 && !end) {
                if( timeIsSet(i, 1)) {
                    h = Storage.schedule.getTime(i, 1)[0];  // i. hour, from (0), h (0)
                    min = Storage.schedule.getTime(i, 1)[1]; // i. hour, from (0), h (1)
                    setMinTime(h, min);
                    end = true;
                }
                i--;
            }
            //if an own time is already entered, this time is read
            if( timeIsSet(lesson_count, time)) {
                h = Storage.schedule.getTime(lesson_count, time, 0);
                min = Storage.schedule.getTime(lesson_count, time, 1);
            }
        }
        tv_h.setText(df.format(h));
        tv_min.setText(df.format(min));
    }

    /*
     * if the current time is before the min time
     * the min time is set as time for the time picker
     */
    public void setMinTime(int h, int min) {
        min_time = h*60 + min;
        if(this.h*60 + this.min < min_time) {
            this.h = h;
            this.min = min;
            tv_h.setText(df.format(h));
            tv_min.setText(df.format(min));
        }
    }

    /*
     * if the current time is after the max time
     * the max time is set as time for the time picker
     */
    public void setMaxTime(int h, int min) {
        if(h*60 + min > 0 && h*60 + min != this.h*60 + this.min) {
            max_time = h * 60 + min;
            if(this.h*60 + this.min > max_time) {
                this.h = h;
                this.min = min;
                tv_h.setText(df.format(h));
                tv_min.setText(df.format(min));
            }
        }
    }

    /**
     * checks whether the transmitted time is equals 0
     * @param lesson_count: first time id
     * @param when: second time id
     * @return boolean: time is set
     */
    public boolean timeIsSet(int lesson_count, int when) {
        int[] values = Storage.schedule.getTime(lesson_count, when);
        return values[0] + values[1] != 0;
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

    //getter and setter
    public void setHeading(String heading) {
        tv_heading.setText(heading);
    }
    public void setTime(int lesson_count, int time) {
        this.lesson_count = lesson_count;
        this.time = time;
        fillData();
    }

    public int[] getTime() {
        return new int[] {h, min};
    }

    //provides the callback opportunity
    public interface TimeListener {
        public void onTimeChangeListener(int h, int min);
    }
    public void setTimeListener(TimeListener listener) {
        this.listener = listener;
    }
}

package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by marcu on 12.07.2017.
 */

public class Schedule_sg_fragment extends FragmentSubject {

    Context context;
    View view;

    GridView gridview;
    LinearLayout container_days;
    TextView tv_time;

    Schedule_gridview_adapter adapter;

    public Schedule_sg_fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment, container, false);
        context = container.getContext();

        //initialization
        gridview = (GridView) view.findViewById(R.id.gridview_schedule);
        container_days = (LinearLayout) view.findViewById(R.id.row_headers_schedule);
        tv_time = (TextView) view.findViewById(R.id.tv_time_schedule);

        //content
        if(!Storage.settings.schedule_daysShown()) {
            container_days.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
        }
        if(Storage.settings.schedule_timesShown()) {
            loadContentWithTimes();
        }
        else {
            loadContentWithoutTimes();
        }

        return view;
    }

    /*
     * loads the whole content of this fragment by showing also the times
     */
    private void loadContentWithTimes() {
        adapter = new Schedule_gridview_adapter(
                context,
                R.id.gridview_schedule,
                Storage.schedule.getScheduleListWithTimes(getSubjectIndex()),
                6
        );
        gridview.setAdapter(adapter);

        //input_listener
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i%6 == 0) {
                    Intent toTimeActivity = new Intent();
                    toTimeActivity.setClass(context, Schedule_time_activity.class);
                    toTimeActivity.putExtra("LESSONCOUNT", i/6);
                    toTimeActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
                    context.startActivity(toTimeActivity);
                }
                else {
                    Intent toLessonActivity = new Intent();
                    toLessonActivity.setClass(context, Schedule_lesson_activity.class);
                    toLessonActivity.putExtra("DAY", i%6-1);
                    toLessonActivity.putExtra("LESSONCOUNT", i/6);
                    toLessonActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
                    toLessonActivity.putExtra("SPINNER", true);
                    context.startActivity(toLessonActivity);
                }
            }
        });
    }

    /*
     * loads the whole content of this fragment by showing also the times
     */
    private void loadContentWithoutTimes() {
        tv_time.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f));
        gridview.setNumColumns(5);

        adapter = new Schedule_gridview_adapter(
                context,
                R.id.gridview_schedule,
                Storage.schedule.getScheduleListWithoutTimes(getSubjectIndex()),
                5
        );
        gridview.setAdapter(adapter);

        //input_listener
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent toLessonActivity = new Intent();
                toLessonActivity.setClass(context, Schedule_lesson_activity.class);
                toLessonActivity.putExtra("DAY", i%6-1);
                toLessonActivity.putExtra("LESSONCOUNT", i/6);
                toLessonActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
                toLessonActivity.putExtra("SPINNER", true);
                context.startActivity(toLessonActivity);
            }
        });
    }

    @Override public void extraFunction() {

    }
}

package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Semester;

/**
 * Created by marcus on 26.1.2018.
 */

public class Archive_listview_adapter extends ArrayAdapter<Semester> {

    Context context;
    int pos;

    CheckBox cb_active;
    TextView tv_name;
    TextView tv_subjects;
    TextView tv_average;

    DecimalFormat df = new DecimalFormat("#0.00");

    public Archive_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Archive_listview_adapter(Context context, int resource, ArrayList<Semester> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.archive_listview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Semester p = getItem(position);

        if (p != null) {
            cb_active = view.findViewById(R.id.checkbox_listview_archive);
            tv_name = view.findViewById(R.id.tv_name_listview_archive);
            tv_subjects = view.findViewById(R.id.tv_subjects_listview_archive);
            tv_average = view.findViewById(R.id.tv_average_listview_archive);

            //set name
            String pattern = view.getResources().getString(R.string.archive_class_pattern);
            if(p.getSemester() == 0) {
                tv_name.setText( p.getSchoolClass() + pattern);
            }
            else {
                tv_name.setText( p.getSchoolClass() + "/" + p.getSemester());
            }

            //set number of subjects
            tv_subjects.setText( String.valueOf(p.getNumberOfSubjects()));

            //set average
            tv_average.setText( df.format(p.getAverage()));

            //set active checkbox
            System.out.println("item: " + p.isActive());
            cb_active.setChecked( p.isActive());
        }

        return view;
    }

}

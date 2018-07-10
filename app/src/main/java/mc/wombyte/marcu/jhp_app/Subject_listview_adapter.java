package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Subject;

public class Subject_listview_adapter extends ArrayAdapter<Subject> {

    TextView tv_average;
    TextView tv_name;
    TextView tv_grades;

    Context context;
    int pos;

    public Subject_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Subject_listview_adapter(Context context, int resource, ArrayList<Subject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.subject_listview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Subject p = getItem(position);

        if (p != null) {
            tv_average = (TextView) view.findViewById(R.id.tv_subjects_subject_average);
            tv_name = (TextView) view.findViewById(R.id.tv_subjects_subject_name);
            tv_grades = (TextView) view.findViewById(R.id.tv_subjects_subject_grades);

            tv_name.setText(p.getName());
            tv_name.setTextColor(p.getColor());
            tv_average.setTextColor(p.getDarkColor());
            tv_average.setText( Storage.settings.subjects_getAverageDecimalFormat().format(p.getAverage()));
            tv_grades.setText(gradesToString());
        }

        return view;
    }

    //******************************************************* methods *******************************************************//
    /*
     * converts the grades into a string, that is devided by ","
     */
    private String gradesToString() {
        String result = "";
        for(int i = 0; i < Storage.grades.get(pos).size(); i++) {
            result += String.valueOf(Storage.grades.get(pos).get(i).getNumber());
            if(i != Storage.grades.get(pos).size()-1) {
                result += ", ";
            }
        }
        return result;
    }
}


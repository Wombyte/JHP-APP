package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Grade;

/**
 * Created by marcu on 07.07.2017.
 */

public class Grade_listview_adapter extends ArrayAdapter<Grade> {

    //Grades
    TextView tv_date;
    TextView tv_grade;
    TextView tv_short_description;
    TextView tv_long_description;

    Context context;
    int posGrade;
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);

    public Grade_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Grade_listview_adapter(Context context, int resource, ArrayList<Grade> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.grade_listview_fragment, null);
            context = container.getContext();
        }

        posGrade = position;
        Grade p = getItem(position);

        //if it is a grade
        if (p != null) {
            //Initialize
            tv_date = (TextView) view.findViewById(R.id.tv_date_listview_grade);
            tv_grade = (TextView) view.findViewById(R.id.tv_grade_listview_grade);
            tv_short_description = (TextView) view.findViewById(R.id.tv_short_description_listview_grade);
            tv_long_description = (TextView) view.findViewById(R.id.tv_long_description_listview_grade);

            //content
            tv_date.setText(sdf.format(p.getWrittenDate()));
            tv_date.setTextColor( Storage.subjects.get(p.getSubjectindex()).getDarkColor());
            tv_grade.setTextColor( Storage.subjects.get(p.getSubjectindex()).getColor());
            tv_grade.setText(String.valueOf(p.getNumber()));
            if(p.isExam()) {
                tv_grade.setTypeface(null, Typeface.BOLD);
            }
            if(!p.getShortDescription().equals( context.getResources().getString(R.string.grades_kind_misc))) {
                tv_short_description.setText(p.getShortDescription());
            }
            else {
                tv_short_description.setText(p.getMisc());
            }

            tv_long_description.setText(p.getDescription());
        }

        return view;
    }
}

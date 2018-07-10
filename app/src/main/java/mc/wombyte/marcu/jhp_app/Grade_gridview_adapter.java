package mc.wombyte.marcu.jhp_app;

/**
 * Created by marcu on 24.07.2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Grade;

/**
 * Created by marcu on 24.07.
 */

public class Grade_gridview_adapter extends ArrayAdapter<Grade> {

    Context context;

    TextView tv;

    int pos;
    int columns;

    public Grade_gridview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Grade_gridview_adapter(Context context, int resource, ArrayList<Grade> items, int columns) {
        super(context, resource, items);
        this.columns = columns;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.grades_gridview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Grade p = getItem(position);

        tv = (TextView) view.findViewById(R.id.tv_text_grade_gridview);

        if (p != null) {
            tv.setText( String.valueOf(p.getNumber()));
            if(p.isExam()) {
                tv.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
        else {
            if(position%columns == 0) {
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setTextColor( Storage.subjects.get(position/columns).getColor());
                tv.setText( Storage.settings.grades_getAverageDecimalFormat().format(Storage.subjects.get(position/columns).getAverage()));
            }
            if(position%columns == Storage.grades.get(position/columns).size()+1) {
                tv.setTextColor( context.getResources().getColor(R.color.colorAccent));
                tv.setText("+");
            }
        }

        return view;
    }

}

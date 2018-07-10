package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marcu on 16.07.2017.
 * shows only the name in the spezified color
 * subject_listview_adapter.java shows the whole subject
 */

public class Subject_spinner_adapter extends ArrayAdapter<String> {

    Context context;

    TextView tv_subject_name;

    ArrayList<String> subjects;

    /*
     * constructor for xml
     */
    public Subject_spinner_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public Subject_spinner_adapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.context = context;
        subjects = items;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        tv_subject_name = view.findViewById(android.R.id.text1);
        tv_subject_name.setTypeface(Typeface.DEFAULT_BOLD);
        tv_subject_name.setGravity(Gravity.CENTER);

        if(position != 0) {
            tv_subject_name.setTextColor(Storage.subjects.get(position - 1).getColor());
        }
        else {
            tv_subject_name.setTextColor( context.getResources().getColor(R.color.colorPrimary));
        }
        return view;
    }

}

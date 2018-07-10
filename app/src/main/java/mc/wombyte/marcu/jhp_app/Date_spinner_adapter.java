package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by marcu on 20.07.2017.
 */

public class Date_spinner_adapter extends ArrayAdapter<String>{

    Context context;

    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMMMM", Locale.GERMANY);

    TextView tv_date;
    ArrayList<String> dates;
    int color;

    /*
     * constructor for xml
     */
    public Date_spinner_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public Date_spinner_adapter(Context context, int resource, ArrayList<String> items, int color) {
        super(context, resource, items);
        this.context = context;
        dates = items;
        this.color = color;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        tv_date = (TextView)view.findViewById(android.R.id.text1);
        tv_date.setText(dates.get(position));
        tv_date.setTextColor(color);

        return view;
    }

}

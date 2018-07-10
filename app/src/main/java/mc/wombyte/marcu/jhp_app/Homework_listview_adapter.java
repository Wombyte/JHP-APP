package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Classes.Homework;

/**
 * Created by marcu on 19.7.2017.
 */

public class Homework_listview_adapter extends ArrayAdapter<Homework> {

    Context context;

    TextView tv_date;
    TextView tv_subject;
    ImageView image_state;
    TextView tv_short_description;
    TextView tv_long_description;

    int pos;
    ArrayList<Homework> list;
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);

    public Homework_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Homework_listview_adapter(Context context, int resource, ArrayList<Homework> items) {
        super(context, resource, items);
        list = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.homework_listview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Homework p = getItem(position);

        if (p != null) {
            tv_date = (TextView) view.findViewById(R.id.tv_date_listview_homework);
            tv_subject = (TextView) view.findViewById(R.id.tv_abbreviation_listview_homework);
            image_state = (ImageView) view.findViewById(R.id.image_state_listview_homework);
            tv_short_description = (TextView) view.findViewById(R.id.tv_short_description_listview_homework);
            tv_long_description = (TextView) view.findViewById(R.id.tv_long_description_listview_homework);

            //hide tv_date if the previous homework has the same date
            if(pos != 0) {
                if(list.get(pos).getDate().date().equals(list.get(pos-1).getDate().date())) {
                    ViewGroup.LayoutParams params = tv_date.getLayoutParams();
                    params.height = 0;
                    tv_date.setLayoutParams(params);
                }
            }

            tv_date.setText(sdf.format( p.getDate().date()));
            tv_subject.setText( Storage.subjects.get( p.getSubjectindex()).getAbbreviation());
            tv_subject.setTextColor( Storage.subjects.get( p.getSubjectindex()).getColor());
            if(p.getSolution().isFinished()) {
                image_state.setImageResource(R.drawable.symbol_homework_true);
            }
            else {
                image_state.setImageResource(R.drawable.symbol_homework_false);
            }
            if(p.getShortDescription().equals( getContext().getResources().getString(R.string.homework_kind_misc))) {
                tv_short_description.setText( p.getMisc());
            }
            else {
                tv_short_description.setText( p.getShortDescription());
            }
            tv_short_description.setTextColor( Storage.subjects.get( p.getSubjectindex()).getDarkColor());
            tv_long_description.setText( p.getDescription());
        }

        return view;
    }

}

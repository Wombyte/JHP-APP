package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.classes.Grade;

/**
 * Created by marcu on 07.07.2017.
 */

public class Grade_listview_adapter extends ArrayAdapter<Grade> {

    Context context;
    ViewHolder holder;

    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);
    private String photo_emoji = new String(Character.toChars(0x1F4F8));

    public Grade_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Grade_listview_adapter(Context context, int resource, ArrayList<Grade> items) {
        super(context, resource, items);
    }

    @NonNull @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        context = container.getContext();
        Grade p = getItem(position);

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.grade_listview_fragment, null);

            holder = new ViewHolder();
            holder.tv_date = view.findViewById(R.id.tv_date_listview_grade);
            holder.tv_grade = view.findViewById(R.id.tv_grade_listview_grade);
            holder.tv_short_description = view.findViewById(R.id.tv_short_description_listview_grade);
            holder.tv_long_description = view.findViewById(R.id.tv_long_description_listview_grade);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_date.setText(sdf.format(p.getWrittenDate()));
        holder.tv_date.setTextColor( Storage.subjects.get(p.getSubjectindex()).getDarkColor());
        holder.tv_grade.setTextColor( Storage.subjects.get(p.getSubjectindex()).getColor());
        holder.tv_grade.setText(String.valueOf(p.getNumber()));
        if(p.isExam()) {
            holder.tv_grade.setTypeface(null, Typeface.BOLD);
        }
        if(!p.getShortDescription().equals( context.getResources().getString(R.string.grades_kind_misc))) {
            holder.tv_short_description.setText(p.getShortDescription());
        }
        else {
            holder.tv_short_description.setText(p.getMisc());
        }

        //long description
        int des_images_amount = Grade.readImageAmount(p.getSubjectindex(), p.getIndex());
        String des = p.getDescription();
        if(des_images_amount == 1) {
            des = des + " ... " + photo_emoji;
        }
        if(des_images_amount > 1) {
            des = des + " ... " + des_images_amount + photo_emoji;
        }
        holder.tv_long_description.setText(des);

        return view;
    }

    /**
     * ViewHolder for this class
     */
    private class ViewHolder {
        TextView tv_date;
        TextView tv_grade;
        TextView tv_short_description;
        TextView tv_long_description;
    }
}

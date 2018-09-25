package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.classes.Homework;

/**
 * Created by marcu on 19.7.2017.
 */

public class Homework_listview_adapter extends ArrayAdapter<Homework> {

    private Context context;

    private ViewHolder holder;

    private String photo_emoji = new String(Character.toChars(0x1F4F8));

    private int pos;
    private ArrayList<Homework> list;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM", Locale.GERMANY);

    public Homework_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Homework_listview_adapter(Context context, int resource, ArrayList<Homework> items) {
        super(context, resource, items);
        list = items;
    }

    @NonNull @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        context = container.getContext();
        Homework p = getItem(position);
        pos = position;

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.homework_listview_fragment, null);

            holder = new ViewHolder();
            holder.tv_date = view.findViewById(R.id.tv_date_listview_homework);
            holder.tv_subject = view.findViewById(R.id.tv_abbreviation_listview_homework);
            holder.tv_short_description = view.findViewById(R.id.tv_short_description_listview_homework);
            holder.tv_long_description = view.findViewById(R.id.tv_long_description_listview_homework);
            holder.list_solution = view.findViewById(R.id.solution_list_listview_homework);
            holder.cover = view.findViewById(R.id.cover_done_homework_listview_homework);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        //hide tv_date if the previous homework has the same date
        if(pos != 0) {
            if(list.get(pos).getDate().date().equals(list.get(pos-1).getDate().date())) {
                ViewGroup.LayoutParams params = holder.tv_date.getLayoutParams();
                params.height = 0;
                holder.tv_date.setLayoutParams(params);
            }
        }

        //date
        holder.tv_date.setText(sdf.format( p.getDate().date()));

        //subject
        holder.tv_subject.setText( Storage.subjects.get( p.getSubjectindex()).getAbbreviation());
        holder.tv_subject.setTextColor( Storage.subjects.get( p.getSubjectindex()).getColor());

        //short description
        if(p.getShortDescription().equals( getContext().getResources().getString(R.string.homework_kind_misc))) {
            holder.tv_short_description.setText( p.getMisc());
        }
        else {
            holder.tv_short_description.setText( p.getShortDescription());
        }
        holder.tv_short_description.setTextColor( Storage.subjects.get( p.getSubjectindex()).getDarkColor());

        //long description
        int des_images_amount = Homework.readImageAmount(Homework.DESCRIPTION_IMAGE, p.getSubjectindex(), p.getIndex());
        String des = p.getDescription();
        if(des_images_amount == 1) {
            des = des + " ... " + photo_emoji;
        }
        if(des_images_amount > 1) {
            des = des + " ... " + des_images_amount + photo_emoji;
        }
        holder.tv_long_description.setText(des);

        //solution list
        holder.list_solution.setImageAmount( Homework.readImageAmount(Homework.SOLUTION_IMAGE, p.getSubjectindex(), p.getIndex()));
        holder.list_solution.setSolution(p.getSolution());
        if(p.getSolution().isFinished()) {
            Log.d("bla", "homework is finished");
            holder.cover.setBackgroundColor( context.getResources().getColor(R.color.homework_cover));
        }

        return view;
    }

    /**
     * ViewHolder for this class
     */
    private class ViewHolder {
        TextView tv_date;
        TextView tv_subject;
        TextView tv_short_description;
        TextView tv_long_description;
        Homework_solution_list list_solution;
        RelativeLayout cover;
    }

}

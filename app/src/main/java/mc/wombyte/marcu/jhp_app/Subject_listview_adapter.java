package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Homework;
import mc.wombyte.marcu.jhp_app.classes.Subject;

public class Subject_listview_adapter extends ArrayAdapter<Subject> {

    Context context;
    ViewHolder holder;

    public Subject_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Subject_listview_adapter(Context context, int resource, ArrayList<Subject> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        context = container.getContext();
        Subject p = getItem(position);

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.subject_listview_fragment, null);

            holder = new ViewHolder();
            holder.tv_name = view.findViewById(R.id.tv_name_subject_list);
            holder.sym_lessons = view.findViewById(R.id.sym_lessons_subject_list);
            holder.tv_lessons = view.findViewById(R.id.tv_lessons_subject_list);
            holder.sym_tasks = view.findViewById(R.id.sym_tasks_subject_list);
            holder.tv_tasks = view.findViewById(R.id.tv_tasks_subject_list);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        int lesson = p.getLessonAmount();
        int tasks = 0;
        if(Storage.homework.size() > p.getIndex()) {
            for(Homework h : Storage.homework.get(p.getIndex())) {
                tasks += h.getSolution().isFinished()? 0 : 1;
            }
        }

        //name
        holder.tv_name.setText(p.getName());
        holder.tv_name.setTextColor(p.getColor());

        //lesson
        if(lesson != 0) {
            holder.tv_lessons.setText( String.valueOf(lesson));
            holder.tv_lessons.setTextColor( context.getResources().getColor(R.color.colorAccent));
            holder.sym_lessons.setColorFilter(p.getDarkColor());
        }
        else {
            holder.tv_lessons.setTextColor( context.getResources().getColor(R.color.background));
            holder.sym_lessons.setColorFilter( context.getResources().getColor(R.color.background));
        }

        //tasks
        if(tasks != 0) {
            holder.tv_tasks.setText( String.valueOf(tasks));
            holder.tv_tasks.setTextColor( context.getResources().getColor(R.color.colorAccent));
            holder.sym_tasks.setColorFilter(p.getDarkColor());
        }
        else {
            holder.tv_tasks.setTextColor( context.getResources().getColor(R.color.background));
            holder.sym_tasks.setColorFilter( context.getResources().getColor(R.color.background));
        }

        return view;
    }

    /**
     * ViewHolder for this class
     */
    private class ViewHolder {
        TextView tv_name;
        ImageView sym_lessons;
        TextView tv_lessons;
        ImageView sym_tasks;
        TextView tv_tasks;
    }
}


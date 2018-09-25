package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Grade;
import mc.wombyte.marcu.jhp_app.classes.Subject;

/**
 * Created by marcus on 24.07.2018.
 */
public class Grade_pl_vertical_list extends ArrayAdapter<ArrayList<Grade>> {

    private Context context;
    private ViewHolder holder;

    public Grade_pl_vertical_list(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Grade_pl_vertical_list(Context context, int resource, ArrayList<ArrayList<Grade>> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        context = container.getContext();
        ArrayList<Grade> p = getItem(position);

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.grades_pl_vertical_list, null);

            holder = new ViewHolder();
            holder.tv_subject = view.findViewById(R.id.tv_subject_grades_pl_list);
            holder.grade_list = view.findViewById(R.id.grade_list_grades_pl_list);
            holder.b_diagram = view.findViewById(R.id.b_diagram_grades_pl_list);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Subject s = Storage.subjects.get(position);

        //text
        String abb = String.format(
                context.getResources().getString(R.string.grades_abbreviation_pl_listview),
                s.getAbbreviation()
        );
        holder.tv_subject.setText(abb);
        String av = String.format(
                context.getResources().getString(R.string.grades_average_pl_listview),
                Storage.settings.grades_getAverageDecimalFormat().format(s.getAverage())
        );
        holder.b_diagram.setText(av);
        holder.grade_list.setList(p, position);

        //color
        changeBackgroundColor(holder.tv_subject, s.getColor());
        holder.b_diagram.setTextColor(s.getDarkColor());

        //listener
        holder.b_diagram.setOnClickListener((v) -> openDiagramDialog(s.getIndex()));

        return view;
    }

    /**
     * changes the background color of a drawable only bounded to <shape></shape>
     * @param view: view that background drawable should be changed
     * @param color: new color for the background
     */
    private void changeBackgroundColor(View view, int color) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(color);
    }

    /**
     * opens a dialog with the diagram about the curse of all grades fo the subject
     * @param subject_index: index of the subject, that will be displayed
     */
    private void openDiagramDialog(int subject_index) {
        Grades_dialog d = new Grades_dialog(context, subject_index);
        d.show();
    }

    /**
     * View Holder class for this adapter
     */
    private class ViewHolder {
        TextView tv_subject;
        Grade_horizontal_list grade_list;
        Button b_diagram;
    }
}

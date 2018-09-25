package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Grade;
import mc.wombyte.marcu.jhp_app.reuseables.BooleanDialog;
import mc.wombyte.marcu.jhp_app.reuseables.ViewSwitcher;

/**
 * Created by marcus on 23.07.2018.
 */
public class Grade_horizontal_list extends RecyclerView {

    private Context context;
    private LinearLayoutManager manager;
    private GradeAdapter adapter;
    private ArrayList<Grade> list = new ArrayList<>();

    //constructor
    public Grade_horizontal_list(Context context) {
        super(context);
        onCreateView(context);
    }
    public Grade_horizontal_list(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context);
    }
    public Grade_horizontal_list(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView(context);
    }

    /**
     * called in all constructors, defines all necessary things
     * sets context and the LayoutManager to a horizontal one
     * @param context: new context
     */
    private void onCreateView(Context context) {
        this.context = context;
        this.manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.adapter = new GradeAdapter(list, 0);
        this.setLayoutManager(manager);
        this.setAdapter(adapter);
    }

    /**
     * sets the list of this class and of the adapter
     * as well as the adapter itself
     * @param list: new content list
     * @param subject_index: subject index of the grade list
     */
    public void setList(ArrayList<Grade> list, int subject_index) {
        this.list = (ArrayList<Grade>) list.clone();
        this.list.add(null);
        this.adapter = new GradeAdapter(this.list, subject_index);
        setAdapter(adapter);
    }
}



    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// Adapter ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class GradeAdapter extends RecyclerView.Adapter<GradeViewHolder> {

    private Context context;
    private ArrayList<Grade> items;
    private int subject_index;

    GradeAdapter(ArrayList<Grade> items, int subject_index) {
        this.items = items;
        this.subject_index = subject_index;
    }

    @NonNull
    @Override public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grades_pl_horizontal_list_item, parent, false);
        return new GradeViewHolder(view, context);
    }

    @Override public void onBindViewHolder(@NonNull final GradeViewHolder holder, int pos) {
        if(items.get(pos) != null) {
            holder.tv_number.setText( String.valueOf(items.get(pos).getNumber()));
            holder.tv_number.setTextColor( getDarkColorFromPosition(pos));
            if(items.get(pos).isExam())
                holder.tv_number.setTypeface(null, Typeface.BOLD);

            holder.image_kind.setImageDrawable( getDrawable(pos));

            holder.container.setOnClickListener((v) -> openGradeActivity(subject_index, pos));
            holder.container.setOnLongClickListener((v) -> openDeleteDialog(items.get(pos)));
        }
        else {
            holder.switcher.switchToView(1);
            holder.container.setOnClickListener((v) -> openGradeActivity(subject_index, -1));
        }
    }

    @Override public int getItemCount() {
        return items.size();
    }

    /**
     * opens the GradeActivity for the transmitted Grade
     * as this list is only used in MainActivity, the class and fragment index are fix
     * @param subject_index: subject index of the opened grade
     * @param index: index of the opened grade (-1 if null)
     */
    private void openGradeActivity(int subject_index, int index) {
        Intent toGradeActivity = new Intent();
        toGradeActivity.setClass(context, Grade_activity.class);
        toGradeActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
        toGradeActivity.putExtra("SUBJECT_INDEX", subject_index);
        toGradeActivity.putExtra("INDEX", index);
        toGradeActivity.putExtra("FRAGMENT_INDEX", 3);
        context.startActivity(toGradeActivity);

    }

    /**
     * opens a boolean dialog, that asks the user whether the grade should be deleted
     * @param grade: grade that could be deleted
     */
    private boolean openDeleteDialog(final Grade grade) {
        BooleanDialog dialog = new BooleanDialog(context, context.getResources().getString(R.string.grades_delete_question));
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                FileSaver.deleteGrade( Storage.grades.get(grade.getSubjectindex()).get(grade.getIndex()));
                items.remove(grade);
                updateList();
            }
            @Override public void onNo() {}
        });
        dialog.show();
        return true;
    }

    /**
     * updates the images list, as it is not possible
     * to call {@link RecyclerView.Adapter#notifyDataSetChanged()}
     * from the dialog callbacks
     */
    private void updateList() {
        notifyDataSetChanged();
    }

    /**
     * returns the matching drawable to the transmitted grade
     * @param pos: position of the grade in {@link this#items}
     * @return matching drawable (c. {@link Grade_activity})
     */
    private Drawable getDrawable(int pos) {
        String des = items.get(pos).getShortDescription();
        switch(des) {
            case "Klausur":
                return context.getResources().getDrawable(R.drawable.symbol_grade_exam);
            case "Leistungskontrolle":
                return context.getResources().getDrawable(R.drawable.symbol_grade_test);
            case "Mündliche Lk":
                return context.getResources().getDrawable(R.drawable.symbol_grade_oral_test);
            case "Vortrag":
                return context.getResources().getDrawable(R.drawable.symbol_grade_presentation);
            case "Epochalnote":
                return context.getResources().getDrawable(R.drawable.symbol_grade_assesment);
            case "Sonstiges":
                return context.getResources().getDrawable(R.drawable.symbol_grade_misc);
        }
        return context.getResources().getDrawable(R.drawable.symbol_grade_misc);
    }

    /**
     * returns the dark color of the subject, of which the transmitted grade is
     * @param pos: position of the Grade in {@link this#items}
     * @return color: dark color
     */
    private int getDarkColorFromPosition(int pos) {
        int subject_index = items.get(pos).getSubjectindex();
        return Storage.subjects.get(subject_index).getDarkColor();
    }
}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// View holder //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class GradeViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout container;
    public ViewSwitcher switcher;
    public TextView tv_number;
    public ImageView image_kind;

    GradeViewHolder(View v, Context context) {
        super(v);
        (switcher = v.findViewById(R.id.vs_grade_horizontal_list)).createView(context);
        container = v.findViewById(R.id.container_grade_horizontal_list);
        tv_number = v.findViewById(R.id.tv_number_grade_horizontal_list);
        image_kind = v.findViewById(R.id.image_kind_grade_horizontal_list);
    }
}

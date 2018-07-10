package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Grade;
import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;

public class Grades_pl_fragment extends FragmentMain {

    GridView gridview;
    Context context;

    ArrayList<Grade> grades;
    Grade_gridview_adapter adapter;
    int columns;

    public Grades_pl_fragment() {}

    @Override public void extraFunction() {
        int subject_id = Storage.getLastSubjectId();
        //open Grade Activity
        if(subject_id != -1 && Storage.settings.grades_isSpecificGradePossible()) {
            openGradeActivity(subject_id, -1);
        }
        //open Diagram of all subjects
        else {
            openDiagrammDialog(Storage.ALL_SUBJECTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grades_pl_fragment, container, false);
        context = container.getContext();

        gridview = (GridView) view.findViewById(R.id.gridview_grades);

        //content
        columns = Storage.getMaxAmountGrades()+2;
        gridview.setNumColumns(columns);
        grades = Storage.getGradeGridViewList();
        adapter = new Grade_gridview_adapter(context, android.R.layout.simple_list_item_1, grades, columns);
        gridview.setAdapter(adapter);

        //input_listener
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i%columns == 0) {
                    openDiagrammDialog(i/columns);
                }
                else {
                    if(grades.get(i) == null) {
                        openGradeActivity(i/columns, -1);
                    }
                    else {
                        openGradeActivity(grades.get(i).getSubjectindex(), grades.get(i).getIndex());
                    }
                }
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openBooleanDialog(i);
                return true;
            }
        });

        return view;
    }

    /*
     * opens a dialog with the diagramm about the curse of all grades fo the subject
     */
    private void openDiagrammDialog(int subject_index) {
        Grades_dialog dialog = new Grades_dialog(context, subject_index);
        dialog.show();
    }

    /*
     * opens the activity for a grade
     * if the second param is a -1, the activity is prepared to create a new grade
     * else the information of the grade are shown
     */
    private void openGradeActivity(int subject_index, int index) {
        Intent toGradeActivity = new Intent();
        toGradeActivity.setClass(context, Grade_activity.class);
        toGradeActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
        toGradeActivity.putExtra("SUBJECT_INDEX", subject_index);
        toGradeActivity.putExtra("INDEX", index);
        context.startActivity(toGradeActivity);
    }

    /*
     * opens a boolean dialog that asked whether the user is sure to delete this grade
     */
    private void openBooleanDialog(final int i) {
        if(grades.get(i) != null && i%columns != 0) {
            BooleanDialog dialog = new BooleanDialog(context, getResources().getString(R.string.grades_delete_question));
            dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
                @Override public void onYes() {
                    int subject_index = grades.get(i).getSubjectindex();
                    int index = grades.get(i).getIndex();
                    FileSaver.deleteGrade( Storage.grades.get(subject_index).get(index));
                    grades = Storage.getGradeGridViewList();
                    adapter.notifyDataSetChanged();
                }
                @Override public void onNo() {}
            });
            dialog.show();
        }
    }

}

package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Grade;

/**
 * Created by marcu on 30.07.2017.
 */

public class Grades_dialog extends Dialog {

    public final static int ALL_SUBJECTS = -2;

    RelativeLayout container;
    Grades_diagram diagram;
    TextView tv_heading;
    TextView tv_grade;
    TextView tv_average;

    ArrayList<Grade> grades_list;
    int subject_index;
    String heading;
    double average;
    int light_color;
    int dark_color;

    public Grades_dialog(Context context, int subject_index) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.grades_dialog);
        this.subject_index = subject_index;

        //getting the right list of grades
        if(subject_index < 0) {
            grades_list = Storage.getGradeList();
            heading = context.getResources().getString(R.string.grades_diagram_heading);
            average = Storage.calculateAverage(subject_index, grades_list.size()-1);
            light_color = context.getResources().getColor(R.color.colorPrimary);
            dark_color = context.getResources().getColor(R.color.colorPrimaryDark);
        }
        else {
            grades_list = Storage.grades.get(subject_index);
            heading = Storage.subjects.get(subject_index).getName();
            average = Storage.calculateAverage(subject_index, grades_list.size()-1);
            light_color = Storage.subjects.get(subject_index).getColor();
            dark_color = Storage.subjects.get(subject_index).getDarkColor();
        }

        //initialization
        tv_heading = (TextView) findViewById(R.id.tv_heading_grade_dialog);
        diagram = (Grades_diagram) findViewById(R.id.diagram_grades);
        tv_grade = (TextView) findViewById(R.id.tv_grades_grade_diagram);
        tv_average = (TextView) findViewById(R.id.tv_average_grade_diagram);

        //content
        tv_heading.setText(heading);
        tv_heading.setTextColor(dark_color);
        tv_grade.setTextColor(light_color);
        tv_average.setTextColor(dark_color);
        tv_average.setText( context.getResources().getString(R.string.grades_diagram_average) + ": " + average);

        diagram.setDiagramDatas(subject_index, grades_list, light_color, dark_color);

        //listeneer
        diagram.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_dialog();
            }
        });
    }

    /*
     * onclick input_listener for the dialog
     */
    private void onclick_dialog() {
        dismiss();
    }
}
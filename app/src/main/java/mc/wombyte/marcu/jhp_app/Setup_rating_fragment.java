package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

/**
 * Created by marcu on 23.12.2017.
 */

public class Setup_rating_fragment extends SettingFragment {

    Context context;

    Spinner spinner_classes;
    ViewSwitcher vs_semester;
    Button b_grades;
    Button b_points;

    ArrayList<String> classes = new ArrayList<>();
    ArrayList<String> semester = new ArrayList<>();
    int current_class = 1;
    int current_semester = 1;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_rating_fragment, container, false);
        context = container.getContext();

        //initialization
        spinner_classes = view.findViewById(R.id.spinner_class_setup_fragment);
        vs_semester = ((ViewSwitcher) view.findViewById(R.id.spinner_semester_setup_fragment)).createView(context);
        b_grades = view.findViewById(R.id.b_grades_setup_rating_fragment);
        b_points = view.findViewById(R.id.b_points_setup_rating_fragment);

        //variables
        readData();
        for(String s: context.getResources().getStringArray(R.array.first_use_classes_array)) {
            classes.add(s);
        }
        for(String s: context.getResources().getStringArray(R.array.first_use_semester_array)) {
            semester.add(s);
        }

        //content
        spinner_classes.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, classes));
        spinner_classes.setSelection(current_class-1);
        if(Storage.semester.size() > 0) {
            spinner_classes.setEnabled(false);
        }

        if(Storage.settings.grades_isRatingInGrades()) {
            activateGrades();
        }
        else {
            activatePoints();
        }

        //listener
        b_grades.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                activateGrades();
            }
        });
        b_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activatePoints();
            }
        });
        spinner_classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                classSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return view;
    }

    //******************************************************* onclick listener *******************************************************//

    /*
     * called when a new class is set
     * @param 'i': index of the class (= class-1)
     */
    private void classSelected(int i) {
        String semester_name;
        if(i < 10) {
            vs_semester.switchToView(0);
            TextView textView = (TextView) vs_semester.getActiveView();
            textView.setText("---");

            semester_name = String.valueOf(i+1);
            activateGrades();
        }
        else {
            vs_semester.switchToView(1);
            Spinner spinner = (Spinner) vs_semester.getActiveView();
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semester));
            spinner.setSelection(current_semester-1);
            if(Storage.semester.size() > 0) {
                spinner.setEnabled(false);
            }

            semester_name = String.valueOf((i+1) * 10 + current_semester);

            activatePoints();
        }

        //update storage value
        Storage.current_semester = semester_name;
    }

    /*
     * lights up the 'grades' button and lights down the 'points' button
     */
    private void activateGrades() {
        b_grades.setTextColor( getResources().getColor(R.color.colorPrimary));
        b_grades.setEnabled(false);

        Storage.settings.grades_setRatingMode(true);
        if(input_listener != null) {
            input_listener.onLegitInput();
        }

        b_points.setTextColor( getResources().getColor(R.color.radio_button_inactive));
        b_points.setEnabled(true);
    }

    /*
     * lights up the 'points' button and lights down the 'grades' button
     */
    private void activatePoints() {
        b_points.setTextColor( getResources().getColor(R.color.colorPrimary));
        b_points.setEnabled(false);

        Storage.settings.grades_setRatingMode(false);
        if(input_listener != null) {
            input_listener.onLegitInput();
        }

        b_grades.setTextColor( getResources().getColor(R.color.radio_button_inactive));
        b_grades.setEnabled(true);
    }

    //******************************************************* methods *******************************************************//

    /*
     * reads the data from the current folder name
     */
    private void readData() {
        String name = Storage.current_semester;
        if(name.equals("")) {
            return;
        }

        Semester s = new Semester(name, 0, 0.0);
        current_class = s.getSchoolClass();
        current_semester = s.getSemester();
    }
}

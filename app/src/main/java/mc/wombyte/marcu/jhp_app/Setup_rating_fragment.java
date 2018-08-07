package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
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

        //listener
        spinner_classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                classSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return view;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// onclick listener ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * called when a new class is set
     * if the selected class is smaller than 11
     * the textview is activated
     * else the semester spinner is activated
     * @param i: index of the class (= class-1)
     */
    private void classSelected(int i) {
        DecimalFormat df = new DecimalFormat("000");
        String semester_name;
        if(i < 10) {
            vs_semester.switchToView(0);
            TextView textView = (TextView) vs_semester.getActiveView();
            textView.setText("---");

            semester_name = df.format((i+1)*10);
        }
        else {
            vs_semester.switchToView(1);
            Spinner spinner = (Spinner) vs_semester.getActiveView();
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semester));
            spinner.setSelection(current_semester-1);
            if(Storage.semester.size() > 0) {
                spinner.setEnabled(false);
            }

            semester_name = df.format((i+1) * 10 + current_semester);
        }

        if(input_listener != null) {
            input_listener.onLegitInput();
        }

        //update storage value
        Storage.current_semester = semester_name;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads the data from the current folder name
     * if there is no current semester, nothing happens
     * else the data from it is read
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

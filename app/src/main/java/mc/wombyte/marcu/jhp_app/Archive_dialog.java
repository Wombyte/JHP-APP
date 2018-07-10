package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Semester;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

/**
 * Created by marcu on 26.01.2018.
 */

public class Archive_dialog extends Dialog {

    Context context;

    Spinner spinner_classes;
    ViewSwitcher vs_semester;
    CheckBox cb_subjects;
    CheckBox cb_schedule;
    Button b_cancel;
    Button b_ok;

    ArrayList<String> classes = new ArrayList<>();
    ArrayList<String> semester = new ArrayList<>();
    int current_class = 0;
    int current_semester = 1;

    OnNewSemesterListener listener = null;

    public Archive_dialog(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.archive_dialog);

        //initialization
        spinner_classes = findViewById(R.id.spinner_class_archive_dialog);
        vs_semester = ((ViewSwitcher) findViewById(R.id.spinner_semester_archive_dialog)).createView(context);
        cb_subjects = findViewById(R.id.checkbox_with_subjects_archive_dialog);
        cb_schedule = findViewById(R.id.checkbox_with_schedule_archive_dialog);
        b_cancel = findViewById(R.id.b_cancel_archive_dialog);
        b_ok = findViewById(R.id.b_finish_archive_dialog);

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
        ((Spinner) vs_semester.getView(1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                current_semester = i+1;
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
            }
        });
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_ok();
            }
        });
    }

    //******************************************************* onclick listener *******************************************************//

    /*
     * called when a new class is set
     * @param 'i': index of the class (= class-1)
     */
    private void classSelected(int i) {
        current_class = i+1;
        if(i < 10) {
            vs_semester.switchToView(0);
            TextView textView = (TextView) vs_semester.getActiveView();
            textView.setText("---");
            current_semester = 0;
        }
        else {
            vs_semester.switchToView(1);
            Spinner spinner = (Spinner) vs_semester.getActiveView();
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semester));
            spinner.setSelection(current_semester-1);
        }
    }

    /*
     * onclick listener for the button ok (user want to create a new semester)
     * uses the callback
     */
    private void onclick_ok() {
        System.out.println("ok clicked");
        if(listener != null)
            listener.onNewSemester(current_class, current_semester, cb_subjects.isChecked(), cb_schedule.isChecked());
        dismiss();
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

    //******************************************************* callback *******************************************************//
    public interface OnNewSemesterListener {
        void onNewSemester(int c, int s, boolean b, boolean b2);
    }
    public void setOnNewSemesterListener(OnNewSemesterListener listener) {
        this.listener = listener;
    }

}

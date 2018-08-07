package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Grades_pl_fragment extends FragmentMain {

    ListView listview;
    Context context;

    public Grades_pl_fragment() {}

    @Override public void extraFunction() {
        int subject_id = Storage.getLastSubjectId();
        //open Grade Activity
        if(subject_id != -1 && Storage.settings.grades_isSpecificGradePossible()) {
            openGradeActivity(subject_id);
        }
        //open Diagram of all subjects
        else {
            openDiagramDialog();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grades_pl_fragment, container, false);
        context = container.getContext();

        listview = view.findViewById(R.id.listview_grades_pl);
        Log.d("GradesPLFragment", "GradeSize: " + Storage.grades.size());
        listview.setAdapter( new Grade_pl_vertical_list(context, android.R.layout.simple_list_item_1, Storage.grades));

        return view;
    }

    /**
     * opens a dialog with the diagram about the curse of all grades
     * of all subjects, as {@link Grades_dialog#ALL_SUBJECTS} is transmitted
     */
    private void openDiagramDialog() {
        Grades_dialog d = new Grades_dialog(context, Grades_dialog.ALL_SUBJECTS);
        d.show();
    }

    /**
     * opens the GradeActivity for the transmitted Grade
     * as this list is only used in MainActivity, the class and fragment index are fix
     * as the user wants to create a new grade, -1 is transmitted as grade index
     * @param subject_index: index of the subject, where a new grade should be created
     */
    private void openGradeActivity(int subject_index) {
        Intent toGradeActivity = new Intent();
        toGradeActivity.setClass(context, Grade_activity.class);
        toGradeActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
        toGradeActivity.putExtra("SUBJECT_INDEX", subject_index);
        toGradeActivity.putExtra("INDEX", -1);
        toGradeActivity.putExtra("FRAGMENT_INDEX", 3);
        context.startActivity(toGradeActivity);
    }
}

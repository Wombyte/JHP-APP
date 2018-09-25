package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import mc.wombyte.marcu.jhp_app.reuseables.BooleanDialog;

/**
 * Created by marcu on 12.07.2017.
 */

public class Grades_sg_fragment extends FragmentSubject {

    Context context;

    Grades_diagram diagram;
    ListView listview;

    Grade_listview_adapter adapter;

    public Grades_sg_fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grades_sg_fragment, container, false);
        context = container.getContext();

        diagram = (Grades_diagram) view.findViewById(R.id.diagram_grades_sg);
        listview = (ListView) view.findViewById(R.id.list_grade_sg);

        //content
        diagram.setDiagramDatas(
                getSubjectIndex(),
                Storage.grades.get(getSubjectIndex()),
                Storage.subjects.get(getSubjectIndex()).getColor(),
                Storage.subjects.get(getSubjectIndex()).getDarkColor()
        );

        adapter = new Grade_listview_adapter(context, R.id.list_grade_sg, Storage.grades.get(getSubjectIndex()));
        listview.setAdapter(adapter);
        setRetainInstance(true);

        //input_listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openGradeActivity(i);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                longClick_listview(i);
                return true;
            }
        });

        return view;
    }

    /*
     * user can add a new grade for this subject
     */
    @Override public void extraFunction() {
        addNewGrade();
    }

    //******************************************************* methods *******************************************************//

    /*
     * opens the grade activity with the clicked grade
     */
    private void openGradeActivity(int i) {
        Intent toGradeActivity = new Intent();
        toGradeActivity.setClass(context, Grade_activity.class);
        toGradeActivity.putExtra("PREVIOUS_CLASS", Subject_activity.class);
        toGradeActivity.putExtra("SUBJECT_INDEX", getSubjectIndex());
        toGradeActivity.putExtra("INDEX", Storage.grades.get(getSubjectIndex()).get(i).getIndex());
        toGradeActivity.putExtra("FRAGMENT_INDEX", 3);
        startActivity(toGradeActivity);
    }

    /*
     * longclick input_listener for an item of the listview
     */
    private void longClick_listview(final int i) {
        BooleanDialog dialog = new BooleanDialog(context, getResources().getString(R.string.grades_delete_question));
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                int subject_index = Storage.grades.get(getSubjectIndex()).get(i).getSubjectindex();
                int index = Storage.grades.get(getSubjectIndex()).get(i).getIndex();
                FileSaver.deleteGrade( Storage.grades.get(subject_index).get(index));
                adapter.notifyDataSetChanged();
            }
            @Override public void onNo() {}
        });
        dialog.show();
    }

    /*
     * onclick input_listener for the button add
     */
    private void addNewGrade() {
        Intent toGradeActivity = new Intent();
        toGradeActivity.setClass(context, Grade_activity.class);
        toGradeActivity.putExtra("PREVIOUS_CLASS", Subject_activity.class);
        toGradeActivity.putExtra("SUBJECT_INDEX", getSubjectIndex());
        toGradeActivity.putExtra("INDEX", -1);
        toGradeActivity.putExtra("FRAGMENT_INDEX", 3);
        startActivity(toGradeActivity);
    }
}

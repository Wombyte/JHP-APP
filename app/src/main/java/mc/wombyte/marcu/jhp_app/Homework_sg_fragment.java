package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Homework;
import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;

/**
 * Created by marcu on 12.07.2017.
 */

public class Homework_sg_fragment extends FragmentSubject {

    Context context;

    ListView listview;
    Homework_listview_adapter adapter;

    ArrayList<Homework> homework_list;

    public Homework_sg_fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homework_fragment, container, false);
        context = container.getContext();

        listview = (ListView) view.findViewById(R.id.list_homework);

        //arrayadapter
        homework_list = Storage.getHomeworkList(getSubjectIndex());
        adapter = new Homework_listview_adapter(context, R.layout.homework_listview_fragment, homework_list);
        listview.setAdapter(adapter);

        //onclick input_listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openHomeworkActivity(i);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openBooleanDialog(i);
                return true;
            }
        });

        return view;
    }

    /*
     * add a new Homework of this subject
     */
    @Override public void extraFunction() {
        addNewHomework();
    }

    //******************************************************* input_listener *******************************************************//

    /*
     * onclick input_listener for the button add
     */
    private void addNewHomework() {
        Intent toHomeworkActivity = new Intent();
        toHomeworkActivity.setClass(context, Homework_activity.class);
        toHomeworkActivity.putExtra("PREVIOUS_CLASS", Subject_activity.class);
        toHomeworkActivity.putExtra("SUBJECT_INDEX", getSubjectIndex());
        context.startActivity(toHomeworkActivity);
    }

    /*
     * opens the homework activity for the clicked homework
     */
    private void openHomeworkActivity(int i) {
        Intent toHomeworkActivity = new Intent();
        toHomeworkActivity.setClass(context, Homework_activity.class);
        toHomeworkActivity.putExtra("PREVIOUS_CLASS", Subject_activity.class);
        toHomeworkActivity.putExtra("SUBJECT_INDEX", subject_index);
        toHomeworkActivity.putExtra("HOMEWORK_INDEX", homework_list.get(i).getIndex());
        this.startActivity(toHomeworkActivity);
    }

    /*
     * opens a boolean dialog, which asks if the user really want to delete the homework
     */
    private void openBooleanDialog(final int i) {
        BooleanDialog dialog = new BooleanDialog(context, getResources().getString(R.string.homework_delete_question));
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                FileSaver.deleteHomework(homework_list.get(i));
            }

            @Override public void onNo() {}
        });
        dialog.show();
    }
}

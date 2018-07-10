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

public class Homework_pl_fragment extends FragmentMain {

    Context context;

    ListView listview;
    Homework_listview_adapter adapter;

    int last_subject_index;
    ArrayList<Homework> homework_list;

    public Homework_pl_fragment() {}


    @Override public void extraFunction() {
        last_subject_index = Storage.getLastSubjectId();
        addHomework();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homework_fragment, container, false);
        context = container.getContext();

        listview = (ListView) view.findViewById(R.id.list_homework);

        homework_list = Storage.getHomeworkList();
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

    //******************************************************* input_listener *******************************************************//

    /*
     * onclick input_listener for the button add
     */
    private void addHomework() {
        Intent toHomeworkActivity = new Intent();
        toHomeworkActivity.setClass(context, Homework_activity.class);
        toHomeworkActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
        if(last_subject_index != -1) {
            toHomeworkActivity.putExtra("SUBJECT_INDEX", last_subject_index);
        }
        context.startActivity(toHomeworkActivity);
    }

    /*
     * opens the homework activity for the clicked homework
     */
    private void openHomeworkActivity(int i) {
        Intent toHomeworkActivity = new Intent();
        toHomeworkActivity.setClass(context, Homework_activity.class);
        toHomeworkActivity.putExtra("PREVIOUS_CLASS", MainActivity.class);
        toHomeworkActivity.putExtra("SUBJECT_INDEX", homework_list.get(i).getSubjectindex());
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
                Storage.deleteHomework(homework_list.get(i));
                //update: notifyDataSetChange doesnt work
                homework_list = Storage.getHomeworkList();
                adapter = new Homework_listview_adapter(context, R.layout.homework_listview_fragment, homework_list);
                listview.setAdapter(adapter);
            }
            @Override public void onNo() {}
        });
        dialog.show();
    }
}

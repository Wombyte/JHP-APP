package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Subject;
import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

public class Subjects_pl_fragment extends FragmentMain {

    ViewSwitcher vs_view;
    ListView listview;
    GridView gridview;

    Subject_listview_adapter listview_adapter;
    Subject_gridview_adapter gridview_adapter;
    Context context;

    InputMethodManager imm;
    public static boolean KEYBOARD_SHOWN = false;

    public Subjects_pl_fragment() {}

    @Override public void extraFunction() {
        openSubjectDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subjects_pl_fragment, container, false);
        context = container.getContext();

        //initialize
        vs_view = ((ViewSwitcher) view.findViewById(R.id.vs_view_subjects)).createView(context);

        //content
        if(Storage.settings.subjects_isShowInList()) {
            vs_view.switchToView(0);
            listview = (ListView) vs_view.getActiveView();

            listview.setLongClickable(true);
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openBooleanDialog(i);
                    return true;
                }
            });

            listview_adapter = new Subject_listview_adapter(
                    getActivity(),
                    R.id.container_list_subjects,
                    (ArrayList<Subject>) Storage.subjects.clone());
            listview.setAdapter(listview_adapter);
            setRetainInstance(true);
        }
        else {
            vs_view.switchToView(1);
            gridview = (GridView) vs_view.getActiveView();

            gridview.setLongClickable(true);
            gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openBooleanDialog(i);
                    return true;
                }
            });

            gridview_adapter = new Subject_gridview_adapter(getActivity(), R.id.container_list_subjects, Storage.subjects);
            gridview.setAdapter(gridview_adapter);
        }

        return view;
    }

    //************************************************* OnClick-Listener ***********************************************************//

    /*
     * opens a boolean dialog to ask the user if (s)he is sure to delete subject
     */
    public void openBooleanDialog(final int i) {
        String string = getResources().getString(R.string.subject_delete_question);
        string = string.replace("_", Storage.subjects.get(i).getName());
        BooleanDialog booleanDialog = new BooleanDialog(context, string);
        booleanDialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                FileSaver.deleteSubject(Storage.subjects.get(i));
                listview_adapter.notifyDataSetChanged();
            }
            @Override public void onNo() {}
        });
        booleanDialog.show();
    }

    /*
     * opens a subject dialog to create a new Subject
     */
    private void openSubjectDialog() {
        Subject_dialog dialog = new Subject_dialog(context);
        dialog.setCreateSubjectListener(new Subject_dialog.OnCreateSubjectListener() {
            @Override public void onCreateSubject(String name, int color) {
                Subject subject = new Subject(name, color, Storage.subjects.size());
                Storage.addSubject(subject);
                listview_adapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }
}

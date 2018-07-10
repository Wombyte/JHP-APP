package mc.wombyte.marcu.jhp_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.Semester;

/**
 * Created by marcu on 26.01.2018.
 */

public class Archive_activity extends JHP_Activity {

    ImageButton b_back;
    ImageButton b_new;
    ListView listview;

    ArrayList<Semester> semesters = new ArrayList<>();
    Archive_listview_adapter adapter;
    int active_semester_index = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_activity);

        //initialization
        b_back = findViewById(R.id.b_back_archive);
        b_new = findViewById(R.id.b_add_archive);
        listview = findViewById(R.id.listview_archive_activity);

        //adapter
        semesters = Storage.semester;
        findActiveSemester();
        adapter = new Archive_listview_adapter(this, R.id.listview_archive_activity, semesters);
        listview.setAdapter(adapter);

        //listener
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_back();
            }
        });
        b_new.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_new();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onclick_listviewItem(i);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("long click");
                return true;
            }
        });

        //options
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120,120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120,120, 120),
                getResources().getDrawable(R.drawable.symbol_add),
                getResources().getString(R.string.archive_option_new_semester)
        ));

        setMenuContainerId(R.id.archive_scroll_container);
        setOptionId(0);
        setOptions();
    }

    //******************************************************* Options *******************************************************//
    @Override public void optionActions(int i) {
        switch(i) {
            case 0: onclick_back(); break;
            case 1: onclick_new(); break;
        }
    }

    //******************************************************* onclick methods *******************************************************//
    /*
     * onclick listener for b_back
     * starts the MainActivity
     */
    private void onclick_back() {
        Intent toMainActivity = new Intent();
        toMainActivity.setClass(this, MainActivity.class);
        this.startActivity(toMainActivity);
    }

    /*
     * onclick listener for b_new
     * starts the archive_dialog to add a new semester
     */
    private void onclick_new() {
        Archive_dialog dialog = new Archive_dialog(this);
        dialog.setOnNewSemesterListener(new Archive_dialog.OnNewSemesterListener() {
            @Override public void onNewSemester(int c, int semester, boolean transferSubjects, boolean transferSchedule) {
                createNewSemester(c, semester, transferSubjects, transferSchedule);
            }
        });
        dialog.show();
    }

    /*
     * onclick listener for an item of the listview
     * the checkbox of the item is changed
     * and the name of the clicked item becomes the current
     */
    private void onclick_listviewItem(int index) {
        System.out.println("listview item clicked");
        boolean isActive = semesters.get(index).isActive();

        if(isActive) {
            System.out.println("was active");
            return;
        }

        active_semester_index = index;

        //saving old Data
        new FileSaver(this).saveData();

        Storage.current_semester = semesters.get(active_semester_index).getName();
        System.out.println("list 0: " + semesters.get(0).getName());
        System.out.println("list 1: " + semesters.get(1).getName());
        System.out.println("current semester name: " + Storage.current_semester);

        //loading new Data
        new FileSaver(this).writeOnlySemester();
        new FileLoader().readData();

        System.out.println(Storage.subjects.size() + " " + Storage.homework.size() + " " + Storage.grades.size());
        findActiveSemester();
        adapter.notifyDataSetChanged();
    }

    //******************************************************* methods *******************************************************//
    /*
     * goes thru all saved semesters and checks whether they are active
     */
    private void findActiveSemester() {
        String active_name = Storage.current_semester;
        for(int i = 0; i < semesters.size(); i++) {
            System.out.println(active_name + " = " + semesters.get(i).getName());
            if(active_name.equals(semesters.get(i).getName())) {
                active_semester_index = i;
                semesters.get(i).setActive(true);
                semesters.get(i).average = Semester.calculateTotalAverage();
                semesters.get(i).number_of_subjects = Storage.subjects.size();
                System.out.println("true");
            }
            else {
                semesters.get(i).setActive(false);
                System.out.println("false");
            }
        }
    }

    /*
     * creates a new semester (folder structure) and saves the old one
     * @param 'c': class in int
     * @param 'semester': number of semester (only for 11/12) else 0
     * @param 'transferSubjects': defines whether the subjects should be transferred from the current semester
     */
    private void createNewSemester(int c, int semester, boolean transferSubjects, boolean transferSchedule) {
        //saving current semester
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();

        //creating a new one
        Semester new_semester = new Semester(
                c,
                semester,
                transferSubjects? Storage.subjects.size() : 0,
                0.0
        );
        Storage.semester.add(new_semester);
        Storage.current_semester = new_semester.getName();
        for(int i = 0; i < Storage.semester.size(); i++) {
            System.out.println(Storage.semester.get(i).getName() + " " + Storage.semester.get(i).getNumberOfSubjects() + " " + Storage.semester.get(i).getAverage());
        }
        adapter.notifyDataSetChanged();

        //preparing the data for the new one
        if(!transferSchedule) {
            Storage.schedule.clearLessons();
        }
        if(transferSubjects) {
            Storage.clearGrades();
        }
        else {
            for(int i = 0; i < Storage.subjects.size(); i++) {
                Storage.deleteSubject(i);
            }
        }

        //creates the new folder structure for the new semester
        for(int i = 0; i < Storage.semester.size(); i++) {
            System.out.println(Storage.semester.get(i).getName() + " " + Storage.semester.get(i).getNumberOfSubjects() + " " + Storage.semester.get(i).getAverage());
        }
        fileSaver.saveData();
        onclick_listviewItem(semesters.size()-1);
        for(int i = 0; i < Storage.semester.size(); i++) {
            System.out.println(Storage.semester.get(i).getName() + " " + Storage.semester.get(i).getNumberOfSubjects() + " " + Storage.semester.get(i).getAverage());
        }
    }
}

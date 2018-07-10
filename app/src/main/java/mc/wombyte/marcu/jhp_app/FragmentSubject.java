package mc.wombyte.marcu.jhp_app;

import android.app.Fragment;

/**
 * Created by marcu on 12.07.2017.
 * abstract class for all fragments included in the subject and Main activity
 */

public abstract class FragmentSubject extends Fragment {

    int subject_index = 0;

    public FragmentSubject() {}

    @Override public void onDetach() {
        super.onDetach();
        saveData();
    }

    /*
     * provides an extra function for each fragment
     * this is called from the menu in SubjectActivity
     */
    public void extraFunction() {}

    /*
     * called at onDetach and when scrolledBy fragment is opened
     */
    public void saveData() {}

    public void setSubjectIndex(int i) {
        subject_index = i;
    }
    public int getSubjectIndex() { return subject_index; }
}

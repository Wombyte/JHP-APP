package mc.wombyte.marcu.jhp_app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by marcu on 17.11.2017.
 */

abstract public class SettingFragment extends Fragment {

    ArrayList<Integer> path = new ArrayList<>();

    /*
     * callback: when the Fragment contains a legit input
     */
    public OnInputListener input_listener = null;
    public interface OnInputListener {
        void onLegitInput();
    }
    public void setInputListener(OnInputListener listener) {
        this.input_listener = listener;
    }

    //******************************************************* fragment *******************************************************//
    /*
     * replaces the current fragment by the fragment saved in the SettingStructur
     * if it is a Group the same fragment is called, but with the clicked group as content
     * else a field requires a whole fragment to perform its action
     */
    public void changeSettingFragment(SettingStructure settingStructure) {
        this.path = settingStructure.path;
        ((Settings_activity) getActivity()).fragment = settingStructure.getFragment();
        ((Settings_activity) getActivity()).setSettingOptions();

        //fragment
        SettingFragment f = settingStructure.getFragment();
        f.setPath(path);

        //Transaction
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(Settings_activity.CONTAINER_ID, settingStructure.getFragment());
        //ft.addToBackStack( getResources().getString(settingStructure.description_id));
        ft.commit();
    }


    //******************************************************* setter *******************************************************//
    public void setPath(ArrayList<Integer> path) {
        this.path = (ArrayList<Integer>) path.clone();
    }

    //******************************************************* getter *******************************************************//
    public ArrayList<Integer> getPath() { return path; }


}

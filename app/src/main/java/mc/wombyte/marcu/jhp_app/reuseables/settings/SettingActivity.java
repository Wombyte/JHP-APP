package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.FileSaver;
import mc.wombyte.marcu.jhp_app.JHP_Activity;
import mc.wombyte.marcu.jhp_app.MainActivity;
import mc.wombyte.marcu.jhp_app.Option;
import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 20.08.2018.
 */
public abstract class SettingActivity extends JHP_Activity {

    SharedPreferences.Editor editor;

    SettingGroup root = new SettingGroup();
    ArrayList<Integer> current_setting = new ArrayList<>();
    ArrayList<Integer> available_settings = new ArrayList<>();

    ImageButton b_back;

    FragmentManager fm = getFragmentManager();
    Fragment fragment;
    final static int CONTAINER_ID = R.id.fragment_container_settings;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //prepare saving
        editor = getPreferences(Context.MODE_PRIVATE).edit();

        //view
        b_back = findViewById(R.id.b_back_settings);
        b_back.setOnClickListener(v -> onclick_back());

        //root
        setRootElement(getSettingTree());
    }

    @Override protected void onPause() {
        super.onPause();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    /**
     * provides the setting tree (should be overwritten in the subclass)
     * @return SettingGroup: root and therefore the whole tree of settings
     */
    public abstract SettingGroup getSettingTree();

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// options ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * defines the actions that should be performed after the click on an option
     * 1. go back
     * 2. return to parent
     * >2. go the "id"th group of this child
     * @param id: id od the option
     */
    @Override public void optionActions(int id) {
        switch(id) {
            case 0: onclick_back(); break;
            case 1: returnToParent(); break;
            default: openSetting(available_settings.get(id-2)); break;
        }
    }

    /**
     * sets all options depending on the current SettingGroup
     * 1. return to home, 2. parent SettingGroup, >2. children setting
     */
    public void setSettingOptions() {
        options.clear();
        options.add(new Option(
                getResources().getColor(R.color.option_default_background),
                getResources().getColor(R.color.option_default_foreground),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        options.add(new Option(
                getResources().getColor(R.color.option_default_background),
                getResources().getColor(R.color.option_default_foreground),
                getResources().getDrawable(R.drawable.symbol_number_picker_minus),
                getResources().getString(R.string.settings_option_back)
        ));

        available_settings.clear();
        if(root.getChildByPath(current_setting) instanceof SettingGroup) {
            SettingGroup group = (SettingGroup) root.getChildByPath(current_setting);
            for(int i = 0; i < group.getChildren().size(); i++) {
                if(group.getChildren().get(i).isAvailable()) {
                    available_settings.add(i);
                    options.add(new Option(
                            getResources().getColor(R.color.option_default_background),
                            getResources().getColor(R.color.option_default_foreground),
                            getResources().getDrawable(group.getChildren().get(i).getSymbolId()),
                            getResources().getString(group.getChildren().get(i).getDescriptionId())
                    ));
                }
            }
        }

        setOptionId(1);
        setOptions();
    }

    /**
     * returns to the current settings parent by getting ist parent setting
     * and transmit it to {@link this#openSettingFragment(SettingStructure)}
     */
    public void returnToParent() {
        SettingGroup parent = root.getChildByPath(current_setting).getParent(root);
        openSettingFragment(parent);

    }

    /**
     * changes the setting Fragment depending on the transmitted structure
     * if the structure is a group, the next fragment is the list fragment of this group
     * else the fields fragment is the next one (if there is one)
     * {@link this#current_setting} is updated
     * @param structure: setting structure that should be displayed
     */
    public void openSettingFragment(SettingStructure structure) {

        //saving old values
        if(structure instanceof SettingGroup) {
            ((SettingGroup) root.getChildByPath(current_setting)).saveChildrenData(this);
        }

        //change fragment or perform OpenAction
        FragmentTransaction ft = fm.beginTransaction();

        if(structure instanceof SettingGroup) {
            fragment = (new SettingListFragment()).setSettingGroup(root, (SettingGroup) structure);
        }
        else {
            if(((Setting) structure).getFragment() == null) {
                return;
            }
            fragment = ((Setting) structure).getFragment();
        }
        current_setting = structure.getPath();
        setSettingOptions();

        Log.d("Activity", "Setting: " + "current setting: " + root.getPath() + " " + current_setting);

        ft.replace(CONTAINER_ID, fragment);
        ft.commit();

        //reading new values
        if(structure instanceof SettingGroup) {
            ((SettingGroup) root.getChildByPath(current_setting)).readChildrenData(this);
        }
    }

    /**
     * executes the action of the clicked setting structure if the OpenCondition allows it
     * if the structure is a group, the fragment is changed to display it
     * else the OpenAction of the Setting is executed if there is one
     * @param index: index of the structure under the current SettingGroup
     *             this method is only called when the option index is >1 and this works only
     *             if the current structure is a group
     */
    public void openSetting(int index) {
        SettingStructure structure = ((SettingGroup) root.getChildByPath(current_setting)).getChildAt(index);
        if(!structure.isAvailable()) {
            Toast.makeText(this, structure.getOpenCondition().errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if(hasFragment(structure)) {
            openSettingFragment(structure);
        }
        else if(structure instanceof Setting){
            ((Setting) structure).performAction(this, editor);
        }
    }

    /**
     * checks whether the transmitted structure has a fragment
     * either it is a group or the settings fragment var is not null
     * @param structure: SettingStructure that has to be checked
     * @return boolean: has the structure a fragment?
     */
    private boolean hasFragment(SettingStructure structure) {
        if(structure instanceof SettingGroup) {
            return true;
        }

        Setting s = (Setting) structure;
        return s.getFragment() != null;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// onclick methods ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * onclick listener for the button "back"
     * returns to {@link MainActivity}
     */
    private void onclick_back() {
        Intent toMainActivity = new Intent();
        toMainActivity.setClass(this, MainActivity.class);
        this.startActivity(toMainActivity);
    }

    /**
     * handling {@link this#root}: Getter & Setter
     * Setter: sets the var, fragment and the options
     */
    public void setRootElement(SettingGroup root) {
        this.root = root;

        //fragment
        FragmentTransaction ft = fm.beginTransaction();
        fragment = (new SettingListFragment()).setSettingGroup(root, root);
        ft.add(CONTAINER_ID, fragment);
        ft.commit();

        //options
        setSettingOptions();
        setMenuContainerId(R.id.setting_scroll_container);
    }
    public SettingGroup getRootElement() { return root; }

}

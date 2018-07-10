package mc.wombyte.marcu.jhp_app;

import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 * Created by marcu on 08.10.2017.
 */

public class SettingGroup extends SettingStructure {

    ArrayList<SettingStructure> child_settings;

    /*
     * Constructor
     */
    public SettingGroup(int description_id) {
        child_settings = new ArrayList<>();
        this.description_id = description_id;
        setFragmentToThis();
    }

    public SettingGroup(int description_id, int symbol_id) {
        child_settings = new ArrayList<>();
        this.description_id = description_id;
        this.symbol_id = symbol_id;
        setFragmentToThis();
    }

    public SettingGroup(int description_id, ArrayList<SettingStructure> child_settings) {
        this.description_id = description_id;
        this.child_settings = child_settings;
        setFragmentToThis();
    }

    public SettingGroup(int description_id, int symbol_id, ArrayList<SettingStructure> child_settings) {
        this.symbol_id = symbol_id;
        this.description_id = description_id;
        this.child_settings = child_settings;
        setFragmentToThis();
    }

    //empty constructor
    public SettingGroup() {
        this.child_settings = new ArrayList<>();
    }

    //******************************************************* methods *******************************************************//
    /*
     * sets the next fragment to a Setting_fragment
     * which shows the children of this SettingsGroup
     */
    public void setFragmentToThis() {
        this.fragment = new Settings_list_fragment();
        this.has_fragment = true;
        this.settings_view_id = Settings.SETTINGS_VIEW_FRAGMENT;
    }

    /*
     * return the fragment after its refreshes the next fragment
     * the refresh prevents the fragment to show nothing
     */
    @Override public SettingFragment getFragment() {
        ((Settings_list_fragment) this.fragment).setSettingGroup(this.path);
        return fragment;
    }

    /*
     * prints the content of all child-settings into the transferred bw
     */
    @Override public void print(BufferedWriter bw) {
        for(SettingStructure structure: child_settings) {
            structure.print(bw);
        }
    }

    /*
     * returns this group with the transmitted int as long_description_id
     */
    public SettingGroup addLongDescription(int description_long_id) {
        this.description_long_id = description_long_id;
        return this;
    }

    //******************************************************* working with list *******************************************************//
    /*
     * returns the child at the transferred position
     */
    public SettingStructure getChildAt(int i) {
        return child_settings.get(i);
    }

    /*
     * adds a new SettingStructure at the transferred position
     */
    public void addChildAt(int i, SettingStructure settingStructure) {
        child_settings.add(i, settingStructure);
    }

    /*
     * adds a new SettingStructure at the last position
     */
    public void addChild(SettingStructure settingStructure) {
        ArrayList<Integer> path = (ArrayList<Integer>) this.path.clone();
        path.add(child_settings.size());
        settingStructure.setPath(path);
        child_settings.add(settingStructure);
    }

    /*
     * sets the transferred SettingStructure at the position
     */
    public void setChildAt(int i, SettingStructure settingStructure) {
        child_settings.set(i, settingStructure);
    }

    /*
     * sets the value of the setting with the transferred path
     * !: should only be called on the group, that was saved as root
     * this project: Storage.settings
     */
    public void setChildValue(ArrayList<Integer> path, Object value) {
        SettingGroup parent = this;
        for(int i = 0; i < path.size(); i++) {
            if(parent.child_settings.get(path.get(i)).isSettingGroup()) {
                parent = (SettingGroup) parent.child_settings.get(path.get(i));
            }
            else {
                SettingField field = (SettingField) parent.child_settings.get(path.get(i));
                field.setValue(value);
            }
        }
    }

    //Getter
    public ArrayList<SettingStructure> getChildSettings() { return child_settings; }
    public ArrayList<ArrayList<Integer>> getChildPaths() {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(SettingStructure s: child_settings) {
            result.add(s.getPath());
        }
        return result;
    }

    //Setter
    public void setChildSettings(ArrayList<SettingStructure> child_settings) { this.child_settings = child_settings; }
}

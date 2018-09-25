package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;

import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 * Created by marcu on 08.10.2017.
 */

public abstract class SettingStructure{

    public static final int NONE = -1;

    int symbol_id = NONE;
    int description_id = NONE;
    int description_long_id = NONE;
    int settings_view_id = Settings.SETTINGS_VIEW_SPACE;

    boolean has_fragment = false;
    boolean has_dialog = false;

    ArrayList<Integer> path = new ArrayList<>();
    SettingFragment fragment;
    Dialog dialog;


    /*
     * checks if this object is a settings group
     */
    public boolean isSettingGroup() {
        return this instanceof SettingGroup;
    }

    /*
     * checks if this object is a settings field
     */
    public boolean isSettingField() {
        return this instanceof SettingField;
    }

    /*
     * provides a print method to save the content of the Setting
     */
    public void print(BufferedWriter bw) {}

    //Getter
    public int getSymbolId() { return symbol_id; }
    public int getDescriptionId() { return description_id; }
    public int getLongDescriptionId() { return description_long_id; }
    public int getSettingsView() { return settings_view_id; }
    public SettingFragment getFragment() { return fragment; }
    public ArrayList<Integer> getPath() { return path; }

    //Setter
    public void setSymbolId(int symbol_id) { this.symbol_id = symbol_id; }
    public void setDescriptionId(int description_id) { this.description_id = description_id; }
    public void setLongDescriptionId(int description_long_id) { this.description_long_id = description_long_id; }
    public void setSettingsView(int settings_view_id) { this.settings_view_id = settings_view_id; }
    public void setFragment(SettingFragment fragment) { this.fragment = fragment; }
    public void setPath(ArrayList<Integer> path) { this.path = path; }
}

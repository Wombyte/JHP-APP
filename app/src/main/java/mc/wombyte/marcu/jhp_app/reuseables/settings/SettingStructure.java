package mc.wombyte.marcu.jhp_app.reuseables.settings;

import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 * Created by marcus on 19.08.2018.
 */
public abstract class SettingStructure {

    public static final int NO_ACTION_ICON = -1;

    private ArrayList<Integer> path = new ArrayList<>();

    private OpenCondition openCondition = null;

    private int symbol_id = 0;
    private int description_id = 0;
    private int long_description_id = 0;
    private int action_symbol_id = 0;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public SettingStructure(int symbol, int description, int action_symbol_id) {
        this.symbol_id = symbol;
        this.description_id = description;
        this.action_symbol_id = action_symbol_id;
    }

    public SettingStructure(int symbol, int description, int long_description, int action_symbol_id) {
        this.symbol_id = symbol;
        this.description_id = description;
        this.long_description_id = long_description;
        this.action_symbol_id = action_symbol_id;
    }

    public SettingStructure() {}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Getter & Setter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * handling {@link this#path}: Getter & Setter
     */
    public void setPath(ArrayList<Integer> path) { this.path = path; }
    public ArrayList<Integer> getPath() { return path; }

    /**
     * handling {@link this#openCondition}: Setter
     */
    public void setOpenCondition(OpenCondition openCondition) { this.openCondition = openCondition; }
    public OpenCondition getOpenCondition() { return openCondition; }

    /**
     * handling {@link this#symbol_id}: Getter & Setter
     */
    public void setSymbolId(int symbol_id) { this.symbol_id = symbol_id; }
    public int getSymbolId() { return symbol_id; }

    /**
     * handling {@link this#description_id}: Getter & Setter
     */
    public void setDescriptionId(int description_id) { this.description_id = description_id; }
    public int getDescriptionId() { return description_id; }

    /**
     * handling {@link this#long_description_id}: Getter & Setter
     */
    public void setLongDescriptionId(int long_description_id) { this.long_description_id = long_description_id; }
    public int getLongDescriptionId() { return long_description_id; }

    /**
     * handling {@link this#action_symbol_id}: Getter & Setter
     */
    public void setActionSymbolId(int action_symbol_id) { this.action_symbol_id = action_symbol_id; }
    public int getActionSymbolId() { return action_symbol_id; }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// General Methods ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * checks whether this class is an instance of Setting
     */
    public boolean isSetting() { return this instanceof Setting; }

    /**
     * checks whether this class is an instance of SettingGroup
     */
    public boolean isSettingGroup() { return this instanceof SettingGroup; }

    /**
     * checks if the structure can be clicked
     * checks if the open condition exists and is not fulfilled
     * in this case the structure is not available,
     * in all over cases it is
     * @return boolean: availability
     */
    public boolean isAvailable() {
        return !(openCondition != null && !openCondition.checkCondition());
    }

    /**
     * returns the path of the parent
     * as the path saves the tree coordinates the last index just have to be removed
     * if the path is empty, an empty path is returned too
     * @return path of the parent structure
     */
    public ArrayList<Integer> getParentPath() {
        if(path.size() == 0) {
            return path;
        }

        ArrayList<Integer> result = (ArrayList<Integer>) path.clone();
        result.remove(result.size()-1);
        return result;
    }

    /**
     * returns the parent of the current setting
     * gets the path from {@link this#getParentPath()}
     * if the returned structure is a group it is returned
     * else the transmitted root is returned
     * @param root: root setting of the tree
     * @return SettingGroup: parent of this Structure
     */
    public SettingGroup getParent(SettingGroup root) {
        ArrayList<Integer> path = getParentPath();
        SettingStructure parent = root.getChildByPath(path);
        if(parent == null || parent instanceof Setting) {
            return root;
        }
        return (SettingGroup) parent;
    }

    /**
     * provides a print method to save the content of the Setting
     * for settingGroups: this method is called for all children
     * for SettingFields: this method is a custom interface
     * @param bw : BufferedWriter to save the content
     */
    public void printToFile(BufferedWriter bw) {}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////// interfaces //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * interface which controls the onClickAction
     * defines under which condition an action is performed
     */
    public interface OpenCondition{
        boolean checkCondition();
        int errorMessage();
    }

}

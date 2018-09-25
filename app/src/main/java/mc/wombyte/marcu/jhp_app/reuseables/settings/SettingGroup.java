package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedWriter;
import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 19.08.2018.
 */
public class SettingGroup extends SettingStructure {

    private ArrayList<SettingStructure> children = new ArrayList<>();

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public SettingGroup(int symbol, int description) {
        super(symbol, description, R.drawable.symbol_continue);
    }

    public SettingGroup(int symbol, int description, int long_description) {
        super(symbol, description, long_description, R.drawable.symbol_continue);
    }

    public SettingGroup() { }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Getter & Setter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * handling {@link this#children}: Getter & Setter
     */
    public void setChildren(ArrayList<SettingStructure> children) { this.children = children; }
    public ArrayList<SettingStructure> getChildren() { return children; }

    /**
     * returns a list of all children paths
     * iterates over the {@link this#children} list and saves the path in an ArrayLis
     * @return ArrayList<Path>: list of all children paths
     */
    public ArrayList<ArrayList<Integer>> getChildPaths() {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(SettingStructure child: children) {
            result.add(child.getPath());
        }
        return result;
    }

    /**
     * returns the path of the child at the transmitted position
     * if the position is out of bounds, an empty path is returned
     * @param index: index of the child in the {@link this#children} list
     * @return ArrayList<Integer>: path of the child
     */
    public ArrayList<Integer> getChildPath(int index) {
        if(index >= children.size() || index < 0) {
            return new ArrayList<>();
        }

        return children.get(index).getPath();
    }

    /**
     * returns the child at the transmitted position
     * if the position is out of bounds, null is returned
     * @param index: position of the child
     * @return SettingStructure, null
     */
    public SettingStructure getChildAt(int index) {
        if(index >= children.size() || index < 0) {
            return null;
        }

        return children.get(index);
    }

    /**
     * adds the transmitted settingStructure to the child list
     * and sets the new path to the child
     * @param newChild: new child in the {@link this#children} list
     */
    public void addChild(SettingStructure newChild) {
        ArrayList<Integer> newPath = (ArrayList<Integer>) getPath().clone();
        newPath.add(children.size());
        newChild.setPath(newPath);
        children.add(newChild);
    }

    /**
     * replaces the settingStructure at the transmitted position
     * and sets the new path to the child
     * if the transmitted index is out of bounds, nothing happens
     * @param newChild: new child in the {@link this#children} list
     */
    public void setChildAt(SettingStructure newChild, int index) {
        if(index >= children.size() || index < 0) {
            return;
        }

        ArrayList<Integer> newPath = (ArrayList<Integer>) getPath().clone();
        newPath.add(index);
        newChild.setPath(newPath);
        children.set(index, newChild);
    }

    /**
     * clears the children list
     */
    public void clearChildren() {
        children.clear();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * all children are forced to write down their content
     * works recursively to ensure that all fields are saved
     * @param bw : BufferedWriter to save the content
     */
    @Override public void printToFile(BufferedWriter bw) {
        for(SettingStructure child: children) {
            child.printToFile(bw);
        }
    }

    /**
     * searches the SettingStructure with the transmitted relative to this group
     * it starts with the path of this object, then it iterates over all items in the path
     * and replaces the result var with its child at the current path position
     * @param path: path of the item
     * @return SettingStructure: child of this group
     */
    public SettingStructure getChildByPath(ArrayList<Integer> path) {
        SettingStructure result = this;
        for(Integer i: path) {
            if(result instanceof SettingGroup) {
                if(((SettingGroup) result).getChildren().size() > i) {
                    result = ((SettingGroup) result).getChildren().get(i);
                }
                else {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * searches for the setting with the transmitted {@link Setting#id}
     * calls {@link this#listSettings(SettingGroup)} to get the list
     * and iterates over it to find the matching setting
     * @param id: id to look for
     * @return matching setting
     */
    public Setting getSettingByID(ArrayList<ArrayList<Integer>> list, String id) {
        for(ArrayList<Integer> path : list) {
            Setting setting = (Setting) this.getChildByPath(path);
            if(setting.getId().equals(id)) {
                return setting;
            }
        }
        return null;
    }

    /**
     * recursive function that lists all settings in the transmitted group
     * iterates through all children structures
     * if the child is a group this method is called for it
     * else this setting is added to the result list
     * @param group: root element which settings should be listed
     * @return List of all settings
     */
    public static ArrayList<ArrayList<Integer>> listSettings(SettingGroup group) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for(SettingStructure structure : group.getChildren()) {
            if(structure instanceof SettingGroup) {
                result.addAll( listSettings( (SettingGroup) structure) );
            }
            if(structure instanceof Setting) {
                result.add(structure.getPath());
            }
        }
        return result;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// Saving and Reading //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public void saveChildrenData(Activity context) {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        for(SettingStructure structure: children) {
            if(structure instanceof Setting) {
                ((Setting) structure).saveToPreference(editor);
            }
        }

        editor.apply();
    }

    public void readChildrenData(Activity context) {
        SharedPreferences sp = context.getPreferences(Context.MODE_PRIVATE);

        for(SettingStructure structure: children) {
            if(structure instanceof Setting) {
                ((Setting) structure).readFromPreference(sp);
            }
        }
    }
}

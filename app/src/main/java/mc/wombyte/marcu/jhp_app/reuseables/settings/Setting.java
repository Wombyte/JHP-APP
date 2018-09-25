package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

/**
 * Created by marcus on 19.08.2018.
 */
public abstract class Setting extends SettingStructure {

    //id to identify the setting
    private String id = "";

    //fragment which is opened
    private Fragment fragment = null;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public Setting(String id, int symbol, int description, int action_symbol) {
        super(symbol, description, action_symbol);
        this.id = id;
    }

    public Setting(String id, int symbol, int description, int long_description, int action_symbol) {
        super(symbol, description, long_description, action_symbol);
        this.id = id;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * handling {@link this#id}: Getter & Setter
     */
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    /**
     * handling {@link this#fragment}: Getter & Setter
     */
    public void setFragment(Fragment fragment) { this.fragment = fragment; }
    public Fragment getFragment() { return fragment; }

    /**
     * defines the view which is shown at the top left corner
     * instead of the action symbol
     * @param context: context from {@link SettingListViewAdapter}
     * @return view (or null, if there should be no view)
     */
    public abstract View getContentView(Context context);

    /**
     * sets all resources for the list view item
     * @param symbol: setting specific symbol at the left side
     * @param description: bold main description of the setting
     * @param action_symbol: symbol at the right side
     */
    public void setViewResources(int symbol, int description, int action_symbol) {
        this.setSymbolId(symbol);
        this.setDescriptionId(description);
        this.setActionSymbolId(action_symbol);
    }

    /**
     * sets all resources for the list view item
     * @param symbol: setting specific symbol at the left side
     * @param description: bold main description of the setting
     * @param long_description: closer description below the main
     * @param action_symbol: symbol at the right side
     */
    public void setViewResources(int symbol, int description, int long_description, int action_symbol) {
        this.setSymbolId(symbol);
        this.setDescriptionId(description);
        this.setLongDescriptionId(long_description);
        this.setActionSymbolId(action_symbol);
    }

    /**
     * provides a method that is called when the list view item is clicked
     * @param context: context to open dialogs, get resources...
     * @param editor: transmit the next view a {@link SharedPreferences.Editor} object
     *              to save its content
     */
    public abstract void performAction(Context context, SharedPreferences.Editor editor);

    /**
     * provides a print method to save the content of the Setting to SharedPreferences
     * @param editor: editor of the SharedPreference
     */
    public abstract void saveToPreference(SharedPreferences.Editor editor);

    /**
     * provides a reading method to read the content from the SharedPreferences
     * @param sp: SharedPreference
     */
    public abstract void readFromPreference(SharedPreferences sp);
}

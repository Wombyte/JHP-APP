package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by marcus on 27.08.2018.
 */
public class StringSetting extends Setting {

    private String stringValue;
    private boolean textIsShown = false;

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public StringSetting(String id, int symbol, int description, int action_symbol, String default_value) {
        super(id, symbol, description, action_symbol);
        this.stringValue = default_value;
    }

    public StringSetting(String id, int symbol, int description, int long_description, int action_symbol, String default_value) {
        super(id, symbol, description, long_description, action_symbol);
        this.stringValue = default_value;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Override Methods ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override
    public void performAction(Context context, SharedPreferences.Editor editor) {
        StringSettingDialog dialog = new StringSettingDialog(context, editor, this);
        dialog.show();
    }

    @Override
    public View getContentView(Context context) {
        if (!this.textIsShown) {
            return null;
        }

        TextView textView = new TextView(context);
        textView.setText( this.stringValue );
        textView.setGravity(Gravity.END);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        return textView;
    }

    @Override
    public void saveToPreference(SharedPreferences.Editor editor) {
        editor.putString(getId(), this.stringValue);
    }

    @Override
    public void readFromPreference(SharedPreferences sp) {
        this.stringValue = sp.getString(getId(), this.stringValue);
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// Getter and Setter ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * handling {@link this#stringValue}: Getter & Setter
     */
    public void setStringValue(String stringValue) { this.stringValue = stringValue; }
    public String getStringValue() { return this.stringValue; }

    /**
     * handling {@link this#textIsShown}: show text / symbol
     */
    public void showText() { this.textIsShown = true; }
    public void showSymbol() { this.textIsShown = false; }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/



}

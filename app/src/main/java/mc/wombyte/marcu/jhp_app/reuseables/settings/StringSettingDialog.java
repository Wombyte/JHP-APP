package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by marcus on 29.08.2018.
 */
public class StringSettingDialog extends SettingDialog {

    private InputMethodManager imm;

    private EditText editText;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructor //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * Constructor
     *
     * Sets {@link this#imm}
     * and the value of the setting to {@link this#editText}.
     * Then the text is focused, selected and the keyboard is forced to show up
     *
     *
     * @param context: to get the {@link InputMethodManager}
     * @param setting: setting that is shown in the dialog
     */
    public StringSettingDialog(Context context, SharedPreferences.Editor editor, StringSetting setting) {
        super(context, editor, setting);
        this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        editText.setText( ((StringSetting) this.setting).getStringValue() );
        showKeyboard(editText);
        editText.selectAll();
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Override methods ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override
    public View getContentView() {
        editText = new EditText( this.context );
        return editText;
    }

    @Override
    public void onYes() {
        ((StringSetting) setting).setStringValue( editText.getText().toString() );
        dismiss();
    }

    @Override
    public void dismiss() {
        hideKeyboard(editText);
        super.dismiss();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// handling keyboard ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * Opens the keyboard for the transmitted EditText.
     * Only works if {@link this#imm} is not null.
     * @param editText: view which will be focused
     * @see InputMethodManager
     */
    private void showKeyboard(EditText editText) {
        if (imm == null) {
            return;
        }

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * Hides the keyboard for a specific EditText.
     * Only works if {@link this#imm} is not null.
     * @param editText: view which was focused
     * @see InputMethodManager
     */
    private void hideKeyboard(EditText editText) {
        if (imm == null) {
            return;
        }

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}

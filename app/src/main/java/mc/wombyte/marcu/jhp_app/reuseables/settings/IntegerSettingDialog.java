package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;
import mc.wombyte.marcu.jhp_app.reuseables.numberPicker.NumberPicker;

/**
 * Created by marcus on 14.09.2018.
 */
public class IntegerSettingDialog extends SettingDialog {

    private NumberPicker numberPicker;

    public IntegerSettingDialog(Context context, SharedPreferences.Editor editor, IntegerSetting setting) {
        super(context, editor, setting);
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Override methods ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override
    public View getContentView() {
        numberPicker = new NumberPicker(context);
        numberPicker.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.settings_numberpicker_height)
        ));
        return numberPicker;
    }

    @Override
    public void onYes() {
        ((IntegerSetting) setting).setIntegerValue( numberPicker.getSelectedNumber() );
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// controlling the NumberPicker /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/


    /**
     * @see NumberPicker#setNumberList(int[])
     */
    public void setNumberList(ArrayList<Integer> list) {
        if(list == null) {
            return;
        }

        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }

        numberPicker.setNumberList(result);
        numberPicker.applyChanges();
    }

    /**
     * @see NumberPicker#setSelectionByIndex(int)
     */
    public void setSelectionByIndex(int index) {
        numberPicker.setSelectionByIndex(index);
        numberPicker.applyChanges();
    }

    /**
     * @see NumberPicker#setSelectionByNumber(int)
     */
    public void setSelectionByNumber(int number) {
        numberPicker.setSelectionByNumber(number);
    }

    /**
     * @see NumberPicker#setMode(int)
     */
    public void setMode(int mode) {
        numberPicker.setMode(mode);
        numberPicker.applyChanges();
    }

    /**
     * @see NumberPicker#setColors(int, int)
     */
    public void setButtonColors(int leftColor, int rightColor) {
        numberPicker.setColors(leftColor, rightColor);
        numberPicker.applyChanges();
    }
}

package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.reuseables.numberPicker.NumberPicker;

/**
 * Created by marcus on 14.09.2018.
 */
public class IntegerSetting extends Setting {

    private int intValue = -1;
    private boolean numberIsShown = false;

    private ArrayList<Integer> valueList = new ArrayList<>();
    private int selectedIndex = 0;
    private int left_color = Color.BLACK;
    private int right_color = Color.BLACK;
    private int mode = ASCENDING;

    public static final int ASCENDING = NumberPicker.ASCENDING;
    public static final int DESCENDING = NumberPicker.DESCENDING;

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructors /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public IntegerSetting(String id, int symbol, int description, int action_symbol, int defaultValue) {
        super(id, symbol, description, action_symbol);
        this.intValue = defaultValue;
    }

    public IntegerSetting(String id, int symbol, int description, int long_description, int action_symbol, int defaultValue) {
        super(id, symbol, description, long_description, action_symbol);
        this.intValue = defaultValue;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Override methods ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @Override
    public void performAction(Context context, SharedPreferences.Editor editor) {
        IntegerSettingDialog dialog = new IntegerSettingDialog(context, editor, this);
        dialog.setNumberList(valueList);
        dialog.setSelectionByIndex(selectedIndex);
        dialog.setMode(mode);
        dialog.setButtonColors(left_color, right_color);
        dialog.show();
    }

    @Override
    public View getContentView(Context context) {
        if (!this.numberIsShown) {
            return null;
        }

        TextView textView = new TextView(context);
        textView.setText( String.valueOf(this.intValue) );
        textView.setGravity(Gravity.END);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        return textView;
    }

    @Override
    public void saveToPreference(SharedPreferences.Editor editor) {
        editor.putInt(this.getId(), intValue);
    }

    @Override
    public void readFromPreference(SharedPreferences sp) {
        intValue = sp.getInt(this.getId(), intValue);
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Getter & Setter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * handling {@link this#intValue}: Getter & Setter
     */
    public void setIntegerValue(int intValue) { this.intValue = intValue; }
    public int getIntegerValue() { return intValue; }

    /**
     * handling {@link this#numberIsShown}: show number / symbol
     */
    public void showNumber() { this.numberIsShown = true; }
    public void showSymbol() { this.numberIsShown = false; }

    /**
     * handling {@link this#left_color}: Getter & Setter
     */
    public void setLeftNUmberPickerColor(int left_color) { this.left_color = left_color; }
    public int getLeftNUmberPickerColor() { return left_color; }

    /**
     * handling {@link this#right_color}: Getter & Setter
     */
    public void setRightNumberPickerColor(int right_color) { this.right_color = right_color; }
    public int getRightNumberPickerColor() { return right_color; }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// methods from numberpicker ///////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * defines {@link this#valueList} as a sequence of integers where
     * each "step"th number is taken.
     * If 'min' is bigger than 'max' the value are swapped.
     * if the current selected index is not in range
     * it is set to the average
     *
     * @param min: new smallest number in the list
     * @param max: new largest number int the list
     */
    public void setNumberList(int min, int max, int step) {
        //swap bounds if necessary
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }

        //fill numbers
        valueList.clear();
        for (int i = min; i <= max; i += step) {
            valueList.add(i);
        }

        //change selection if necessary
        if (selectedIndex < 0 || selectedIndex > valueList.size()) {
            selectedIndex = valueList.size() / 2;
        }
    }

    /**
     * shorter method for {@link this#setNumberList(int, int, int)} with
     * the predefined step value of 1
     *
     * @param min: smallest value of the list
     * @param max: highest value of the list
     */
    public void setNumberList(int min, int max) {
        setNumberList(min, max, 1);
    }

    /**
     * defines {@link this#valueList} by the transmitted int-array.
     * If the list is null, nothing happens.
     *
     * @param list: array which is converted to a arraylist
     */
    public void setNumberList(int[] list) {
        if (list == null) {
            return;
        }

        valueList.clear();
        for(int i : list) {
            valueList.add(i);
        }
    }

    /**
     * sets the new selected index
     * if the index is not in range, nothing happens
     *
     * @param index: new selected index
     */
    public void setSelectionByIndex(int index) {
        if (index < 0 || index > valueList.size() - 1) {
            return;
        }

        this.selectedIndex = index;
    }

    /**
     * sets the selection to the transmitted number from
     * {@link this#valueList}
     * @param number: item from the list
     */
    public void setSelectionByNumber(int number) {
        setSelectionByIndex( valueList.indexOf(number) );
        intValue = valueList.get(selectedIndex);
    }

    /**
     * defines the mode to be {@link NumberPicker#DESCENDING}
     * or {@link NumberPicker#ASCENDING}
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * sets {@link this#left_color} & {@link this#right_color}
     *
     * @param left_color: color of the left button and start color of the gradient
     * @param right_color color of the right button and end color of the gradient
     */
    public void setNumberPickerColors(int left_color, int right_color) {
        this.left_color = left_color;
        this.right_color = right_color;
    }
}

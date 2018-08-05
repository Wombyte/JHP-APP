package mc.wombyte.marcu.jhp_app.Reuseables.NumberPicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 17.07.2018.
 */
public class NumberPicker extends RelativeLayout {

    private Context context;

    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;

    private NumberRecyclerView number_list;
    private ImageButton b_left;
    private ImageButton b_right;

    private int selected_index = 0;
    private int min = 0;
    private int max = 99;
    private int mode;
    private int left_color;
    private int right_color;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructor //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * custom constructor, which sets the context, min as well as max value of the list
     * at the end {@link this#onCreateView(Context)} is called
     * @param context: context of the parent view, to gain access to the Resources
     * @param min: smallest value of the list
     * @param max: largest value of the list
     */
    public NumberPicker(Context context, int min, int max, int left_color, int right_color) {
        super(context);
        this.mode = ASCENDING;
        setColors(left_color, right_color);
        setRange(min, max);
        onCreateView(context);
    }

    /**
     * custom constructor, which sets the context, min & max value
     * as well as the mode of the list
     * at the end {@link this#onCreateView(Context)} is called
     * @param context: context of the parent view, to gain access to the Resources
     * @param min: smallest value of the list
     * @param max: largest value of the list
     */
    public NumberPicker(Context context, int min, int max, int left_color, int right_color, int mode) {
        super(context);
        this.mode = mode;
        setColors(left_color, right_color);
        setRange(min, max);
        onCreateView(context);
    }

    /**
     * xml constructor, transmitting the context and an attributeSet
     * mode, min and max are read from the attributeSet
     * {@link this#onCreateView(Context)} is called
     * @param context: context of the parent view, to gain access to the Resources
     * @param attrs: contains the custom attributes
     */
    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker);
        readAttributeSet(a);
        a.recycle();
    }

    /**
     * xml constructor, transmitting the context and an attributeSet
     * mode, min and max are read from the attributeSet
     * {@link this#onCreateView(Context)} is called
     * @param context: context of the parent view, to gain access to the Resources
     * @param attrs: contains the custom attributes
     * @param defStyle: default style element (unnecessary)
     */
    public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker, defStyle, 0);
        readAttributeSet(a);
        a.recycle();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////// Constructor methods //////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * reads all attributes from NumberPicker included in a
     * @param a: TypedArray containing all attributes of this class
     */
    private void readAttributeSet(TypedArray a) {
        String color;
        color = a.getString(R.styleable.NumberPicker_startColor);
        this.left_color = color == null? context.getResources().getColor(R.color.colorAccent) : Color.parseColor(color);
        color = a.getString(R.styleable.NumberPicker_endColor);
        this.right_color = color == null? context.getResources().getColor(R.color.colorAccent) : Color.parseColor(color);

        mode = a.getInt(R.styleable.NumberPicker_LtoR_order, ASCENDING);
        setRange(
                a.getInt(R.styleable.NumberPicker_minValue, min),
                a.getInt(R.styleable.NumberPicker_maxValue, max)
        );
    }

    /**
     * method that is called after all constructors
     * defines the layout.xml, initialize the views and sets the listeners
     * @param context: new context from constructors
     */
    private void onCreateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.number_picker, this, true);
        this.context = context;

        //initialization
        number_list = view.findViewById(R.id.number_list_number_picker);
        b_left = view.findViewById(R.id.b_left_number_number_picker);
        b_right = view.findViewById(R.id.b_right_number_number_picker);

        //listener
        b_left.setOnClickListener((v) -> number_list.changeSelectionBy((mode == ASCENDING)? -1 : 1));
        b_right.setOnClickListener((v) -> number_list.changeSelectionBy((mode == ASCENDING)? 1 : -1));

        //content
        applyChanges();
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * updates this view by applying all changes made
     * set mode, color, list and changes the color of the buttons
     */
    public void applyChanges() {
        number_list.setMode(mode);
        number_list.setColors(left_color, right_color);
        number_list.setNumberList( getNumberList(), selected_index);
        number_list.applyChanges();

        changeBackgroundColor(b_left, left_color);
        changeBackgroundColor(b_right, right_color);
    }

    /**
     * creates the number list for the recycler view
     * depending on the current min and max values
     * @return list of numbers in range
     */
    private ArrayList<Integer> getNumberList() {
        ArrayList<Integer> result = new ArrayList<>();
        for(Integer i = min; i <= max; i++) {
            result.add(i);
        }
        return result;
    }

    /**
     * changes the background color of a drawable only bounded to <shape></shape>
     * @param button: image button
     * @param color: new color for the background
     */
    private void changeBackgroundColor(ImageButton button, int color) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground();
        drawable.setColor(color);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Getter & Setter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * sets the range of the number list
     * if 'min' is bigger than 'max' the value are set reversed
     * if the current selected index is not in range
     * it is set to the average
     * @param min: new smallest number in the list
     * @param max: new largest number int the list
     */
    public void setRange(int min, int max) {
        this.min = (min <= max)? min : max;
        this.max = (min <= max)? max : min;

        if(selected_index < min || selected_index > max) {
            this.selected_index = (min + max) / 2;
        }
    }

    /**
     * sets the new selected index
     * if the index is not in range, nothing happens
     * @param index: new selected index
     */
    public void setSelection(int index) {
        if(index > max || index < min) {
            return;
        }

        this.selected_index = index;
    }

    /**
     * defines the mode to be {@link this#DESCENDING}
     * or {@link this#ASCENDING}
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * sets {@link this#left_color} & {@link this#right_color}
     * @param left_color: color of the left button and start color of the gradient
     * @param right_color color of the right button and end color of the gradient
     */
    public void setColors(int left_color, int right_color) {
        this.left_color = left_color;
        this.right_color = right_color;
    }

    /**
     * @return selected index of the number list
     */
    public int getSelectedIndex() {
        return number_list.getActualSelectedIndex();
    }
}

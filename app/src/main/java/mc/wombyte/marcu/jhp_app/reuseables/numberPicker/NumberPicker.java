package mc.wombyte.marcu.jhp_app.reuseables.numberPicker;

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

    private NumberRecyclerView numberRecyclerView;
    private ImageButton b_left;
    private ImageButton b_right;

    private int selected_index = -1;
    private ArrayList<Integer> numbers = new ArrayList<>();
    private int mode;
    private int left_color;
    private int right_color;


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// constructor //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * Smallest Constructor which sets only the default values
     * At the end {@link this#onCreateView(Context)} is called
     *
     * @param context: context of the parent view, to gain access to the Resources
     */
    public NumberPicker(Context context) {
        super(context);
        this.mode = ASCENDING;
        int c = context.getResources().getColor(R.color.colorAccent);
        setColors(c, c);
        setNumberList(1, 100, 1);
        onCreateView(context);
    }

    /**
     * Custom constructor, which sets the context, min & max value
     * as well as the mode of the list.
     * At the end {@link this#onCreateView(Context)} is called
     *
     * @param context: context of the parent view, to gain access to the Resources
     * @param min:     smallest value of the list
     * @param max:     largest value of the list
     */
    public NumberPicker(Context context, int min, int max, int left_color, int right_color, int mode) {
        super(context);
        this.mode = mode;
        setColors(left_color, right_color);
        setNumberList(min, max, 1);
        onCreateView(context);
    }

    /**
     * Constructor that defines the number list with a min and a max, where
     * every "step"th number is saved to the list using {@link this#setNumberList(int, int, int)}.
     * At the end {@link this#onCreateView(Context)} is called.
     *
     * @param context:     context from parent
     * @param min:         smallest number in the list
     * @param max:         highest number in the list if = min + n*step
     * @param step:        difference between the numbers
     * @param left_color:  color on the left side
     * @param right_color: color on the right side
     * @param mode:        direction of the list
     */
    public NumberPicker(Context context, int min, int max, int step, int left_color, int right_color, int mode) {
        super(context);
        this.mode = mode;
        setColors(left_color, right_color);
        setNumberList(min, max, step);
        onCreateView(context);
    }

    /**
     * Constructor that defines the number list as an int-array
     * with {@link this#setNumberList(int[])}.
     * At the end {@link this#onCreateView(Context)} is called.
     *
     * @param context:     context from parent
     * @param list:        list object
     * @param left_color:  color on the left side
     * @param right_color: color on the right side
     * @param mode:        direction of the list
     */
    public NumberPicker(Context context, int[] list, int left_color, int right_color, int mode) {
        super(context);
        this.mode = mode;
        setColors(left_color, right_color);
        setNumberList(list);
        onCreateView(context);
    }

    /**
     * Custom constructor, which sets the context, min & max value
     * as well as the mode of the list. Both colors are set to the
     * accentColor.
     * At the end {@link this#onCreateView(Context)} is called
     *
     * @param context: context of the parent view, to gain access to the Resources
     * @param min:     smallest value of the list
     * @param max:     largest value of the list
     */
    public NumberPicker(Context context, int min, int max, int mode) {
        super(context);
        this.mode = mode;
        int c = context.getResources().getColor(R.color.colorAccent);
        setColors(c, c);
        setNumberList(min, max, 1);
        onCreateView(context);
    }

    /**
     * Constructor that defines the number list with a min and a max, where
     * every "step"th number is saved to the list using {@link this#setNumberList(int, int, int)}.
     * Both colors are set to the accentColor
     * At the end {@link this#onCreateView(Context)} is called.
     *
     * @param context:     context from parent
     * @param min:         smallest number in the list
     * @param max:         highest number in the list if = min + n*step
     * @param step:        difference between the numbers
     * @param mode:        direction of the list
     */
    public NumberPicker(Context context, int min, int max, int step, int mode) {
        super(context);
        this.mode = mode;
        int c = context.getResources().getColor(R.color.colorAccent);
        setColors(c, c);
        setNumberList(min, max, step);
        onCreateView(context);
    }

    /**
     * Constructor that defines the number list as an int-array
     * with {@link this#setNumberList(int[])}. The Colors are both set
     * to be the AccentColor
     * At the end {@link this#onCreateView(Context)} is called.
     *
     * @param context:     context from parent
     * @param list:        list object
     * @param mode:        direction of the list
     */
    public NumberPicker(Context context, int[] list, int mode) {
        super(context);
        this.mode = mode;
        int c = context.getResources().getColor(R.color.colorAccent);
        setColors(c, c);
        setNumberList(list);
        onCreateView(context);
    }

    /**
     * xml constructor, transmitting the context and an attributeSet
     * mode, min and max are read from the attributeSet
     * {@link this#onCreateView(Context)} is called
     *
     * @param context: context of the parent view, to gain access to the Resources
     * @param attrs:   contains the custom attributes
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
     *
     * @param context:  context of the parent view, to gain access to the Resources
     * @param attrs:    contains the custom attributes
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
     *
     * @param a: TypedArray containing all attributes of this class
     */
    private void readAttributeSet(TypedArray a) {
        String color;
        color = a.getString(R.styleable.NumberPicker_startColor);
        this.left_color = color == null ? context.getResources().getColor(R.color.colorAccent) : Color.parseColor(color);
        color = a.getString(R.styleable.NumberPicker_endColor);
        this.right_color = color == null ? context.getResources().getColor(R.color.colorAccent) : Color.parseColor(color);

        int min = a.getInt(R.styleable.NumberPicker_minValue, Integer.MAX_VALUE);
        int max = a.getInt(R.styleable.NumberPicker_maxValue, Integer.MIN_VALUE);
        int step = a.getInt(R.styleable.NumberPicker_step, -1);
        if (min != Integer.MAX_VALUE && max != Integer.MIN_VALUE) {
            if (step == -1) {
                setNumberList(min, max, 1);
            } else {
                setNumberList(min, max, step);
            }
        }

        mode = a.getInt(R.styleable.NumberPicker_LtoR_order, ASCENDING);
    }

    /**
     * method that is called after all constructors
     * defines the layout.xml, initialize the views and sets the listeners
     *
     * @param context: new context from constructors
     */
    private void onCreateView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.number_picker, this, true);
        this.context = context;

        //initialization
        numberRecyclerView = view.findViewById(R.id.number_list_number_picker);
        b_left = view.findViewById(R.id.b_left_number_number_picker);
        b_right = view.findViewById(R.id.b_right_number_number_picker);

        //listener
        b_left.setOnClickListener((v) -> numberRecyclerView.changeSelectionBy((mode == ASCENDING) ? -1 : 1));
        b_right.setOnClickListener((v) -> numberRecyclerView.changeSelectionBy((mode == ASCENDING) ? 1 : -1));

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
        numberRecyclerView.setMode(mode);
        numberRecyclerView.setColors(left_color, right_color);
        numberRecyclerView.setNumberList(this.getNumberList(), selected_index);
        numberRecyclerView.applyChanges();

        changeBackgroundColor(b_left, left_color);
        changeBackgroundColor(b_right, right_color);
    }

    /**
     * creates the number list for the recycler view
     * depending on the current min and max values
     *
     * @return list of numbers in range
     */
    private ArrayList<Integer> getNumberList() {
        return numbers;
    }

    /**
     * changes the background color of a drawable only bounded to <shape></shape>
     *
     * @param button: image button
     * @param color:  new color for the background
     */
    private void changeBackgroundColor(ImageButton button, int color) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground();
        drawable.setColor(color);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Getter & Setter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * defines {@link this#numbers} as a sequence of integers where
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
        numbers.clear();
        for (int i = min; i <= max; i += step) {
            numbers.add(i);
        }

        //change selection if necessary
        if (selected_index < 0 || selected_index > numbers.size()) {
            selected_index = numbers.size() / 2;
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
     * defines {@link this#numbers} by the transmitted int-array.
     * If the list is null, nothing happens.
     *
     * @param list: array which is converted to a arraylist
     */
    public void setNumberList(int[] list) {
        if (list == null) {
            return;
        }

        numbers.clear();
        for(int i : list) {
            numbers.add(i);
        }
    }

    /**
     * sets the new selected index
     * if the index is not in range, nothing happens
     *
     * @param index: new selected index
     */
    public void setSelectionByIndex(int index) {
        if (index < 0 || index > numbers.size() - 1) {
            return;
        }

        this.selected_index = index;
    }

    /**
     * sets the selection to the transmitted number from
     * {@link this#numbers}
     * @param number: item from the list
     */
    public void setSelectionByNumber(int number) {
        setSelectionByIndex( numbers.indexOf(number) );
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
     *
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
        return numberRecyclerView.getActualSelectedIndex();
    }

    /**
     * @return selected number
     */
    public int getSelectedNumber() {
        return numbers.get( this.getSelectedIndex() );
    }
}

package mc.wombyte.marcu.jhp_app.Reuseables.NumberPicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 17.07.2018.
 */
public class NumberRecyclerView extends RecyclerView {

    private Context context;
    private ArrayList<String> list = new ArrayList<>();
    private LinearLayoutManager manager;

    private int left_color;
    private int right_color;
    private boolean reverseLayout;
    private int unselected_border_color;
    private int unselected_text_color;
    private int selected_index = 0;

    //constructor
    public NumberRecyclerView(Context context, boolean reverseLayout) {
        super(context);
        onCreateView(context, reverseLayout);
    }
    public NumberRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context, false);
    }
    public NumberRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView(context, false);
    }

    /**
     * called in all constructors, defines all necessary things
     * sets context and the LayoutManager to a horizontal one
     * @param context: new context
     * @param reverseLayout: defines in which direction the items should be loaded
     */
    private void onCreateView(Context context, boolean reverseLayout) {
        this.context = context;
        this.manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, reverseLayout);
        this.setLayoutManager(manager);

        left_color = right_color = context.getResources().getColor(R.color.colorAccent);
        unselected_border_color = context.getResources().getColor(R.color.background);
        unselected_text_color = context.getResources().getColor(R.color.colorAccent);
    }

    /**
     * applies all changes made to the vars
     * sets a new adapter and new manager
     */
    public void applyChanges() {
        this.setAdapter(new NumberAdapter(
                list,
                selected_index,
                reverseLayout,
                new int[] {
                        left_color,
                        right_color,
                        unselected_border_color,
                        unselected_text_color
                },
                this::setSelection
        ));
        this.manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, this.reverseLayout);
        this.setLayoutManager(manager);
        this.manager.scrollToPositionWithOffset(selected_index, 30);
    }

    /**
     * sets the direction in which the items are loaded
     * {@link NumberPicker#ASCENDING} --> left to right
     * {@link NumberPicker#DESCENDING} --> right to left
     * @param mode: modes of number picker mentioned above
     */
    public void setMode(int mode) {
        this.reverseLayout = mode == NumberPicker.DESCENDING;
    }

    /**
     * defines the colors of the gradient, the selection borders build
     * @param left_color: start color of the gradient
     * @param right_color: end color of the gradient
     */
    public void setColors(int left_color, int right_color) {
        this.left_color = left_color;
        this.right_color = right_color;
    }

    /**
     * updates the number list to the transmitted one
     * if the list is null or empty nothing happens
     * @param numbers: list of the numbers
     */
    public void setNumberList(ArrayList<Integer> numbers, int start_index) {
        if(numbers == null || numbers.size() == 0) {
            return;
        }
        if(start_index < 0 || start_index > numbers.size()-1) {
            return;
        }

        list.clear();
        list.add("");
        for(Integer i: numbers) list.add( String.valueOf(i));
        list.add("");

        this.selected_index = start_index + 1;
    }

    /**
     * changes the selection index and the selection itself
     * finds the holder at the last index and deactivates it
     * and activates the holder at the new index
     * {@link this#selected_index} & {@link NumberAdapter#selected_index} are updated in between
     * at the end it is scrolled to the new index
     * @param index: new index
     */
    public void setSelection(int index) {
        NumberViewHolder holder;
        NumberAdapter adapter = (NumberAdapter) this.getAdapter();

        if((holder = ((NumberViewHolder) this.findViewHolderForAdapterPosition(selected_index))) != null) {
            NumberViewHolder.changeHolderColor(holder, unselected_border_color, unselected_text_color);
        }
        adapter.notifyItemChanged(selected_index);

        selected_index = index;
        adapter.setSelection(index);

        if((holder = ((NumberViewHolder) this.findViewHolderForAdapterPosition(selected_index))) != null) {
            int color = adapter.getSelectionColor(index);
            NumberViewHolder.changeHolderColor(holder, color, color);
        }
    }

    /**
     * changes the selection index by the transmitted difference
     * if the resulting index is out of bounds, nothing is changed
     * else {@link this#setSelection(int)} is called
     * @param dif: difference by which the index is changed
     */
    public void changeSelectionBy(int dif) {
        int index = selected_index + dif;
        if(index < 1 || index > list.size()-2) {
            return;
        }
        setSelection(index);
    }

    /**
     * returns the index of the list, that was originally transmitted
     * int {@link this#setNumberList(ArrayList, int)}
     * @return mentioned index
     */
    public int getActualSelectedIndex() {
        return selected_index-1;
    }
}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////// Number Adapter ////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class NumberAdapter extends RecyclerView.Adapter<NumberViewHolder> {
    
    private Context context;
    private ArrayList<String> items;

    private boolean isReverseLayout;
    private int left_color;
    private int right_color;
    private int unselected_border_color;
    private int unselected_text_color;
    private int selected_index;

    NumberAdapter(ArrayList<String> items, int start_index, boolean reverseLayout, int[] colors, OnSelectionChangedListener listener) {
        this.items = items;
        this.selected_index = start_index;
        this.isReverseLayout = reverseLayout;
        this.left_color = colors[0];
        this.right_color = colors[1];
        this.unselected_border_color = colors[2];
        this.unselected_text_color = colors[3];
        this.listener = listener;
    }

    @NonNull
    @Override public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.number_picker_item, parent, false);
        return new NumberViewHolder(view, context);
    }

    @Override public void onBindViewHolder(@NonNull final NumberViewHolder holder, int position) {
        holder.tv_number.setText( items.get(position));
        holder.container.setOnClickListener((view) -> selectIndex(position));

        if(position == selected_index) {
            int color = getSelectionColor(position);
            NumberViewHolder.changeHolderColor(holder, color, color);
        }
        else {
            NumberViewHolder.changeHolderColor(holder, unselected_border_color, unselected_text_color);
        }
    }

    @Override public int getItemCount() {
        return items.size();
    }

    /**
     * returns the selection color of the currently selected index
     * if index = 0, the left_color is returned
     * if index = list.size, the right color is returned
     * in between a linear gradient is created
     * @return color
     */
    public int getSelectionColor(int pos) {
        pos = isReverseLayout? items.size()-1-pos : pos;
        float factor = ((float) pos) / ((float) items.size()-1);
        int red = (int) (factor * (Color.red(right_color) - Color.red(left_color)));
        int green = (int) (factor * (Color.green(right_color) - Color.green(left_color)));
        int blue = (int) (factor * (Color.blue(right_color) - Color.blue(left_color)));
        return Color.rgb(
                Color.red(left_color) + red,
                Color.green(left_color) + green,
                Color.blue(left_color) + blue
        );
    }

    /**
     * sets the new index (only for private issues)
     * if the index is out of bounds, nothing happens
     * else the index is updated and the listener is called
     * @param index: new index
     */
    private void selectIndex(int index) {
        if(index < 0 || index > items.size()-2) {
            return;
        }
        selected_index = index;
        listener.onSelectionChanged(index);
    }

    /**
     * sets the new index (only for {@link NumberRecyclerView}
     * @param index: new index
     */
    public void setSelection(int index) {
        selected_index = index;
    }

    //listener
    public OnSelectionChangedListener listener;
    interface OnSelectionChangedListener {
        void onSelectionChanged(int new_index);
    }
}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// View holder //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class NumberViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout container;
    public TextView tv_number;

    private static float width;
    
    NumberViewHolder(View v, Context context) {
        super(v);
        width = context.getResources().getDimensionPixelSize(R.dimen.number_picker_selector_border_width);
        container = v.findViewById(R.id.container_number_picker_item);
        tv_number = v.findViewById(R.id.tv_number_number_picker_item);
    }

    /**
     * changes the stroke color of a drawable only bounded to <shape></shape>
     * and the text color of the holder number
     * @param holder: object which colors should be changed
     * @param border_color: new color of the selector border
     * @param text_color: new color of the text
     */
    public static void changeHolderColor(NumberViewHolder holder, int border_color, int text_color) {
        GradientDrawable drawable = (GradientDrawable) holder.container.getBackground();
        drawable.setStroke((int) width, border_color);
        holder.tv_number.setTextColor(text_color);
    }

}

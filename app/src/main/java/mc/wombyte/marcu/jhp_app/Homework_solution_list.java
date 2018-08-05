package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.Classes.HomeworkSolution;

/**
 * Created by marcus on 11.07.2018.
 */
public class Homework_solution_list extends LinearLayout {

    private Context context;
    private int image_amount = 0;

    public Homework_solution_list(Context context) {
        super(context);
        this.context = context;
    }
    public Homework_solution_list(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
    }
    public Homework_solution_list(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
        this.context = context;
    }

    /**
     * sets the amount of solution images
     * this has to be done extra, as the images are only read in the homework activity
     * it has to be called before the {@link Homework_solution_list#setSolution(HomeworkSolution)}
     * method, as the values is applied there
     * @param amount: amount of images read in {@link Homework_listview_adapter}
     */
    public void setImageAmount(int amount) {
        this.image_amount = amount;
    }


    /**
     * fills the list with the numbers of the transmitted solution
     * all sizes are saved in an array
     * iterating over all sizes: if the size > 0 the belonging field is added
     * the integers 0-4 matching to the vars defined in {@link ListItem}
     * @param solution: object that contains all data
     */
    public void setSolution(HomeworkSolution solution) {
        int[] size = new int[] {
                solution.getText().length(),
                image_amount,
                solution.getDocs().size(),
                solution.getSheets().size(),
                solution.getSlides().size()
        };

        for(int i = 0; i < 5; i++) {
            if(size[i] != 0) {
                addView(new ListItem(context, i, size[i]));
            }
        }
    }
}

/**
 * item class
 * including an amount field and a symbol
 */
class ListItem extends LinearLayout {

    private Context context;

    public static final int TEXT = 0;
    public static final int IMAGES = 1;
    public static final int DOCS = 2;
    public static final int SHEETS = 3;
    public static final int SLIDES = 4;

    ListItem(Context context, int mode, int amount) {
        super(context);
        this.context = context;
        onCreate(mode, amount);
    }
    ListItem(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
    }
    ListItem(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
        this.context = context;
    }

    /**
     * fills the list item with the transmitted data
     * colors both subviews depending on the mode
     * @param mode: kind of solution
     * @param amount: amount of the solution kind
     */
    private void onCreate(int mode, int amount) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.homework_solution_list_item, this, true);

        //initialization
        TextView tv_amount = findViewById(R.id.tv_amount_homework_solution_list);
        ImageView img_symbol = findViewById(R.id.img_symbol_homework_solution_list);

        //define ids depending on mode
        int color_id = 0;
        int drawable_id = 0;
        switch (mode) {
            case TEXT:
                color_id = R.color.homework_solution_text;
                drawable_id = R.drawable.symbol_homework_text;
                break;
            case IMAGES:
                color_id = R.color.homework_solution_images;
                drawable_id = R.drawable.symbol_homework_image;
                break;
            case DOCS:
                color_id = R.color.homework_solution_docs;
                drawable_id = R.drawable.symbol_homework_docs;
                break;
            case SHEETS:
                color_id = R.color.homework_solution_sheets;
                drawable_id = R.drawable.symbol_homework_table;
                break;
            case SLIDES:
                color_id = R.color.homework_solution_slides;
                drawable_id = R.drawable.symbol_homework_slides;
                break;
        }

        //apply values
        if(amount > 1 && mode != TEXT) {
            tv_amount.setText( String.valueOf(amount));
            tv_amount.setTextColor( getResources().getColor(color_id));
        }

        Drawable d = getResources().getDrawable(drawable_id).mutate();
        img_symbol.setImageDrawable(d);
        img_symbol.setColorFilter( getResources().getColor(color_id), PorterDuff.Mode.SRC_ATOP);
    }
}

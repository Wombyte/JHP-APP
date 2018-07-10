package mc.wombyte.marcu.jhp_app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Reuseables.Vector2D;

/**
 * Created by marcu on 09.07.2017.
 */

public class Activity_scroll_fragment extends Fragment {

    Context context;
    RelativeLayout scroll_container;
    DrawLayout layout;
    int current_option_index;

    int circle_r;

    Option[] option;
    Option click_option = new Option(
            Color.argb(0, 255, 255, 255),
            Color.rgb(120, 120, 120),
            "", ""
    );

    public Activity_scroll_fragment() {}

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scroll_fragment, container, false);
        context = container.getContext();

        circle_r = Storage.settings.general_getCirclePos()[2];

        scroll_container = view.findViewById(R.id.scroll_container);
        scroll_container.setOnTouchListener(new Scroll_fragment_listener(context) {
            @Override public void activateMenu() { layout.activateMenu(); }
            @Override public void tabbedOn(double gamma, boolean wasInMenuRing) { onSingleTab(gamma, wasInMenuRing); }
            @Override public void scrolledBy(double gamma) { layout.scrollMenu(gamma); }
            @Override public void deactivateMenu() { layout.deactivateMenu(); }
        });

        //adding the custom draw view to the fragment
        layout = new DrawLayout(getActivity(), option, current_option_index);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        scroll_container.addView(layout);

        return view;
    }

    /*
     * sets the option list, shown by this fragment
     * transfers the Option-Arraylist of  this fragment to an Option-Array in 'layout'
     * adds an
     */
    public void setOptionList(ArrayList<Option> options) {
        option = new Option[options.size()+1];
        option[0] = click_option;
        for(int i = 0; i < options.size(); i++) {
            option[i+1] = options.get(i);
        }

        if(layout == null) {
            return;
        }
        layout.setOption(option);
    }

    /*
     * sets the index of the current subject
     * to move the circles to the right position
     */
    public void setOptionIndex(int current_option_index) {
        this.current_option_index = current_option_index;
    }

    /*
     * decides if and which subject was clicked and changes to belonging activity
     */
    public void onSingleTab(double gamma, boolean wasInMenuRing) {
        if(!wasInMenuRing) {
            layout.deactivateMenu();
            return;
        }

        //find closest subject
        int closest_i = 0;
        double min_angle = Math.PI;
        double angle;
        for(int i = 0; i < layout.alpha.length; i++) {
            angle = Math.abs(layout.alpha[i] - gamma);
            if(angle < min_angle) {
                min_angle = angle;
                closest_i = i;
            }
        }

        if(listener != null) {
            listener.onOptionClick(closest_i-1);
        }

    }

    //callback
    public OnOptionClickListener listener = null;
    public interface OnOptionClickListener {
        void onOptionClick(int i);
    }
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.listener = listener;
    }
}

/*********************************** new class to draw on the fragment  ***********************************/
class DrawLayout extends View {
    private Context context;

    //array to display
    Option[] option;

    //material for displaying
    private Paint p_option = new Paint();
    private Paint p_text = new Paint();
    private Drawable[] drawable_option;

    //dimensions, coordinates
    private DisplayMetrics metrics;
    private int display_width;
    private int display_heigth;
    int option_circle_r;
    double start_alpha;
    static double[] alpha;
    static double delta_alpha;
    int[] option_x;
    int[] option_y;
    int[] description_x;
    int[] description_y;
    int description_r;
    int circle_x, circle_y, circle_r;

    //misk
    int option_index;
    boolean menu_active;
    int size;
    boolean left_handside;

    /*
     * constructor to use it in code
     * defines and calculates all vars
     */
    public DrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawLayout(Context context, Option[] option, int option_index) {
        super(context);

        this.context = context;
        metrics = context.getResources().getDisplayMetrics();
        display_width = metrics.widthPixels;
        display_heigth = metrics.heightPixels;

        this.option_index = option_index;

        //start value
        menu_active = false;
        this.setBackgroundColor( Color.argb(0, 0, 0, 0));

        //read vars
        circle_x = Storage.settings.general_getCirclePos()[0];
        circle_y = Storage.settings.general_getCirclePos()[1];
        circle_r = Storage.settings.general_getCirclePos()[2];
        left_handside = Storage.settings.general_getCirclePos()[3] == Storage.settings.LEFT_HAND_SIDE;
        option_circle_r = (int) getResources().getDimension(R.dimen.scroll_option_radius);
        description_r = circle_r + (int) getResources().getDimension(R.dimen.scroll_option_text_radius);

        //paints
        p_option.setStyle(Paint.Style.FILL);
        p_option.setTextAlign(Paint.Align.CENTER);
        p_option.setTextSize( getResources().getDimension(R.dimen.scroll_text_size));
        p_option.setStrokeWidth( getResources().getDimension(R.dimen.scroll_option_stroke_size));
        p_option.setTypeface(Typeface.DEFAULT_BOLD);

        p_text.setColor( getResources().getColor(R.color.colorPrimaryDark));
        p_text.setStyle(Paint.Style.FILL);
        p_text.setTextAlign(Paint.Align.LEFT);
        p_text.setTextSize( getResources().getDimension(R.dimen.scroll_option_text_size));

        setOption(option);

        //to refresh the angles
        settingStartCoordinates();
        scrollMenu(0);
    }

    /*
     * set a new option array and calculate all necessay values
     */
    public void setOption(Option[] option) {
        this.option = option;

        size = option.length;
        alpha = new double[size];
        option_x = new int[size];
        option_y = new int[size];
        description_x = new int[size];
        description_y = new int[size];
        drawable_option = new Drawable[size];
        Rect bounds; //for calculate the text width in px

        //calculating the vars
        delta_alpha = getDeltaAlpha();
        start_alpha = getStartAlpha();
        for(int i = 0; i < size; i++) {
            drawable_option[i] = getCircleDrawable(option[i].getBackgroundColor(), option[i].getForegroundColor());

            bounds = new Rect();
            p_text.getTextBounds(option[i].getDescription(), 0, option[i].getDescription().length(), bounds);
            option[i].setDescriptionOffsetX(bounds.width() / 2);

            if(option[i].isContentText()) {
                bounds = new Rect();
                p_option.getTextBounds(option[i].getText(), 0, option[i].getText().length(), bounds);
                option[i].setTextOffsetY(bounds.height() / 2);
            }
        }

        settingStartCoordinates();
    }

    /*
     * activates the menu
     */
    public void activateMenu() {
        menu_active = true;
        this.setBackgroundColor( context.getResources().getColor(R.color.transparent_background));
        scrollMenu(0);
    }

    /*
     * deactivates the menu
     */
    public void deactivateMenu() {
        menu_active = false;
        this.setBackgroundColor( Color.argb(0, 0, 0, 0));
        settingStartCoordinates();
        scrollMenu(0);
    }

    /*
     * calculates the start values for option_x, option_y and angle
     */
    public void settingStartCoordinates() {
        for(int i = 0; i < size; i++) {
            alpha[i] = getAlpha(start_alpha, i);
            option_x[i] = circle_x + (int) (Math.cos(alpha[i]) * circle_r);
            option_y[i] = circle_y + (int) (Math.sin(alpha[i]) * circle_r);
            description_x[i] = circle_x + (int) (Math.cos(alpha[i]) * description_r);
            description_x[i] -= left_handside? 0 : 2*option[i].getDescriptionOffsetX();
            description_y[i] = circle_y + (int) (Math.sin(alpha[i]) * description_r);
        }
    }

    /*
     * @return: colored drawable of 'scroll_menu_option_circle'
     * @param 'color1': color of the background
     * @param 'color2': color of the circle
     */
    private Drawable getCircleDrawable(int color1, int color2) {
        LayerDrawable result = (LayerDrawable) getResources().getDrawable(R.drawable.scroll_menu_option_circle);

        //getting both layers
        GradientDrawable background = (GradientDrawable) result.findDrawableByLayerId(R.id.option_circle_background);
        GradientDrawable foreground = (GradientDrawable) result.findDrawableByLayerId(R.id.option_circle_ring);

        //coloring
        background.setColor(color1);
        foreground.setStroke( getResources().getDimensionPixelSize(R.dimen.scroll_ring_width), color2);

        return result;
    }

    /*
     * @return  the angle, in which this subject has to be drawn
     * @param 'start' moves the whole row of subjects to this angle (to be visible)
     * @param 'i' position of this subject
     */
    private double getAlpha(double start, int i) {
        double result = start;

        //to mirror the rows depending of the handside
        int sign = left_handside ? -1 : 1;

        //adding the difference between position and the current subject
        //therefore the current subject is in the middle
        result = addAngle(result, sign * delta_alpha * (i - option_index));
        return result;
    }

    /*
     * calculates the distance angle between the subjects
     */
    private double getDeltaAlpha() {
        return Math.acos((Math.pow(3* option_circle_r, 2) - 2*Math.pow(circle_r, 2))/(-2 * Math.pow(circle_r, 2)));
    }

    /*
     * returns the angle to the start option of the circle
     * it is located a half option_circle_r away from the crossing point of rim and circle
     */
    private double getStartAlpha() {
        //calculate crossing point of menu_circle and the vertical border
        int x_rim = left_handside? 0 : display_width;
        int y_rim = (int) -Math.sqrt(circle_r*circle_r - Math.pow(x_rim - circle_x, 2)) + circle_y;

        //getting its angle
        Vector2D rim = new Vector2D(circle_x, circle_y, x_rim, y_rim);
        double gamma = rim.basicAngle();

        int sign = left_handside? 1 : -1;

        return gamma + sign*delta_alpha*2/3;
    }

    /*
     * method to draw (is called on invalidate())
     * used general vars are changed in scrolledBy()
     */
    @Override public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(menu_active) {
            for(int i = 0; i < size; i++) {
                //fill circle
                drawable_option[i].setBounds(option_x[i]- option_circle_r, option_y[i]- option_circle_r, option_x[i]+ option_circle_r, option_y[i]+ option_circle_r);
                drawable_option[i].setAlpha(255);
                drawable_option[i].draw(canvas);

                //description
                canvas.drawText(option[i].getDescription(), description_x[i], description_y[i], p_text);

                //content
                if(option[i].isContentSymbol()) {
                    option[i].setSymbolBounds(option_x[i], option_y[i], option_circle_r);
                    option[i].setSymbolAlpha(255);
                    option[i].getSymbol().draw(canvas);
                }
                else {
                    p_option.setColor(option[i].getForegroundColor());
                    canvas.drawText(option[i].getText(), option_x[i], option_y[i] + option[i].getTextOffsetY(), p_option);
                }
            }
        }
        else {
            int i = option_index;
            drawable_option[i].setBounds(option_x[i]- option_circle_r, option_y[i]- option_circle_r, option_x[i]+ option_circle_r, option_y[i]+ option_circle_r);
            drawable_option[i].setAlpha(64);
            drawable_option[i].draw(canvas);
            if(option[i].isContentSymbol()) {
                option[i].setSymbolBounds(option_x[i], option_y[i], option_circle_r);
                option[i].setSymbolAlpha(64);
                option[i].getSymbol().draw(canvas);
            }
            else {
                p_option.setColor(option[i].getForegroundColor());
                p_option.setAlpha(64);
                canvas.drawText(option[i].getText(), option_x[i], option_y[i] + option[i].getTextOffsetY(), p_option);
                p_option.setAlpha(255);
            }
        }

    }

    /*
     * moves the menu
     * performance: if outside of for to remove redundant conditions
     */
    public void scrollMenu(double gamma) {
        //double menu_start = getMenuStart();
        //double menu_end = getMenuEnd();
        //double menu_angle = getMenuAngle();
        //double space_angle = 2*Math.PI - menu_angle;

        //left
        if(left_handside) {
            for(int i = 0; i < size; i++) {
                alpha[i] = addAngle(alpha[i], -gamma);
                option_x[i] = circle_x + (int) (Math.cos(alpha[i]) * circle_r);
                option_y[i] = circle_y + (int) (Math.sin(alpha[i]) * circle_r);
                description_x[i] = circle_x + (int) (Math.cos(alpha[i]) * description_r);
                description_x[i] -= left_handside? 0 : 2*option[i].getDescriptionOffsetX();
                description_y[i] = circle_y + (int) (Math.sin(alpha[i]) * description_r);
            }
        }
        //right
        else {
            for(int i = 0; i < size; i++) {
                alpha[i] = addAngle(alpha[i], gamma);
                option_x[i] = circle_x + (int) (Math.cos(alpha[i]) * circle_r);
                option_y[i] = circle_y + (int) (Math.sin(alpha[i]) * circle_r);
                description_x[i] = circle_x + (int) (Math.cos(alpha[i]) * description_r);
                description_x[i] -= left_handside? 0 : 2*option[i].getDescriptionOffsetX();
                description_y[i] = circle_y + (int) (Math.sin(alpha[i]) * description_r);
            }
        }
        invalidate();
    }

    /*
     * @return: the sum of both angles (always between 0 and 2PI)
     */
    public double addAngle(double alpha, double beta) {
        double result = alpha + beta;
        while(result > 2*Math.PI) {
            result -= 2*Math.PI;
        }
        while(result < 0) {
            result += 2*Math.PI;
        }
        return result;
    }

    /*
     * return: angle of the whole menu
     * including all options + 4 delta-alpha (gab between first and last option)
     */
    public double getMenuAngle() {
        double result = Math.abs(alpha[0] - alpha[option.length-1]);
        return addAngle(result, 3 * getDeltaAlpha());
    }

    /*
     * return: angle of the crossing point of the circle and the vertical axis
     */
    public double getMenuStart() {
        double result = getStartAlpha();
        int sign = left_handside? 1 : -1;
        return addAngle(result, sign*2*getDeltaAlpha());
    }

    /*
     * return: angle of the end of the menu
     * if the angle would be visible, it is set to the closest non-visible position
     */
    public double getMenuEnd() {
        int sign = left_handside? 1 : -1;

        //calculate crossing point with horizontal axis and its angle
        double x = sign * Math.sqrt( circle_r*circle_r - Math.pow(display_heigth - circle_y, 2)) + circle_x;
        double min_alpha = (new Vector2D(circle_x, circle_y, (int) x, display_heigth)).basicAngle();
        min_alpha += sign*getDeltaAlpha();

        //compare with menu_angle
        double alpha = addAngle(getStartAlpha(),sign*getMenuAngle());
        if(left_handside == alpha < min_alpha) {
            return alpha;
        }
        return min_alpha;
    }

    /**
     * @return angle between the two transferred angles
     */
    public double angle(double alpha, double beta) {
        double result = beta - alpha;
        if(alpha > beta) {
            return 2*Math.PI - result;
        }
        return result;
    }
}

package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import mc.wombyte.marcu.jhp_app.reuseables.Vector2D;

/**
 * Created last_y marcu on 08.07.2017.
 */

public class Scroll_fragment_listener implements View.OnTouchListener {

    private GestureDetector gd;
    private DisplayMetrics metrics;
    private Context context;

    private int display_height, display_width;

    //open scrolledBy fragment
    private boolean in_menu = false;
    private int option_r;
    private int circle_r;
    private boolean left_handside;
    private long start_of_long_click;

    //singleTabUp
    private boolean already_swiped = false;

    //Vector
    private Vector2D menu_circle;
    private Vector2D start_point = new Vector2D(0, 0);
    private Vector2D current_mouse_pos; //relative to circle -> right angle
    private Vector2D last_mouse_pos; //relative to circle -> right angle

    //fling + scrolledBy
    private CountDownTimer fling;
    private long fling_end = 0;
    private long fling_tick_rate = (long) 20;

    private double scroll_velocity;
    private double gamma;


    /*
     * Constructor
     */
    public Scroll_fragment_listener(Context context) {
        this.context = context;
        gd = new GestureDetector(context, new GestureListener());

        metrics = context.getResources().getDisplayMetrics();
        display_height = metrics.heightPixels;
        display_width = metrics.widthPixels;

        //load from settings
        menu_circle = new Vector2D(Storage.settings.general_getCirclePos()[0], Storage.settings.general_getCirclePos()[1]);
        option_r = context.getResources().getDimensionPixelOffset(R.dimen.scroll_option_radius);
        circle_r = Storage.settings.general_getCirclePos()[2];
        left_handside = Storage.settings.general_getCirclePos()[3] == Storage.settings.LEFT_HAND_SIDE;

        //calculating variables
        start_point = getStartPoint();
    }

    /*
     * @return: the vector from (0|0) to
     */
    private Vector2D getStartPoint() {
        //calculate crossing point of menu_circle and the vertical border
        int x_rim = left_handside? 0 : display_width;
        int y_rim = (int) -Math.sqrt(circle_r*circle_r - Math.pow(x_rim - menu_circle.x, 2)) + menu_circle.y;

        //getting its angle
        Vector2D rim = new Vector2D(x_rim, y_rim, menu_circle.x, menu_circle.y);
        double gamma = rim.basicAngle();

        int sign = left_handside? 1 : -1;

        gamma += sign*getDeltaAlpha()*2/3;

        return new Vector2D(
                menu_circle.x - (int) (Math.cos(gamma)*circle_r),
                menu_circle.y - (int) (Math.sin(gamma)*circle_r)
        );
    }

    /*
     * calculates the distance angle between the subjects
     */
    private double getDeltaAlpha() {
        return Math.acos((Math.pow(3*option_r, 2) - 2*Math.pow(circle_r, 2))/(-2 * Math.pow(circle_r, 2)));
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        return gd.onTouchEvent(e);
    }

    /*
     * GestureListener to detect all action performed by the user
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override public boolean onDown(MotionEvent e) {
            if(Subjects_pl_fragment.KEYBOARD_SHOWN) {
                return false;
            }
            return down(e);
        }

        @Override public boolean onSingleTapUp(MotionEvent e) {
            return singleTap();
        }

        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { //called several times while scrolledBy
            if(!in_menu) {
                return false;
            }
            scroll(e2);
            already_swiped = true;
            return true;
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!in_menu) {
                return false;
            }
            return fling(e1, e2, velocityX, velocityY);
        }
    }

    //******************************************************* methods *******************************************************//


    /*
     * called: onDown of GestureListener
     * @return: return value for onDown
     * @param 'e': position of the down event
     *
     * current and last position is saved relative to the M(circle)
     * if the menu is active: fling is stopped
     * else: menu can be opened by a click in the near of a special point
     */
    private boolean down(MotionEvent e) {
        current_mouse_pos = last_mouse_pos = getVectorFromPointToMouse(menu_circle, e);

        if(in_menu) {
            if(isInClickOption(e)) {
                return false;
            }
            //cancel fling on touch
            if(fling != null) fling.cancel();
            return true;
        }
        else {
            if(isInVisibleOption(e)) {
                activateMenu();
                in_menu = true;
                return true;
            }
        }

        return false;
    }

    /*
     * called: onSingleTap of GestureListener
     * @return: return value of onSingleTap
     * @param 'e': position of the singleTap event
     */
    private boolean singleTap() {
        boolean isInMenuRing = Math.abs(circle_r - current_mouse_pos.length) < 2 * context.getResources().getDimension(R.dimen.scroll_option_radius) && already_swiped;
        tabbedOn( current_mouse_pos.basicAngle(), isInMenuRing); // 'PI-' to correct the angle
        in_menu = false;
        deactivateMenu();

        return true;
    }

    /*
     * called: onScroll of GestureListener
     * @return: return value of onScroll
     * @param 'e': position of current scroll position
     */
    private boolean scroll(MotionEvent e) {
        scroll_velocity = 1.5;
        current_mouse_pos = getVectorFromPointToMouse(menu_circle, e);

        //angle between the vectors
        gamma = Vector2D.angle(current_mouse_pos, last_mouse_pos);
        if(gamma > Math.PI) {
            gamma = 2*Math.PI - gamma;
        }
        //defines the sign of the angle
        gamma *= (last_mouse_pos.y < current_mouse_pos.y) ? -scroll_velocity : scroll_velocity;

        scrolledBy(gamma);

        last_mouse_pos = current_mouse_pos;

        return true;
    }

    /*
     * called: onFling of GestureListener
     * @return: return value of onFling
     * @param 'e1': position of the start of the fling
     * @param 'e2': position of the end of the fling
     * @param 'vx, xy': velocities in option_x/option_y direction
     */
    private boolean fling(final MotionEvent e1, final MotionEvent e2, float vx, float vy) {
        //slow factor = 1000
        scroll_velocity = (new Vector2D((int) vx, (int) vy).length)/1000;
        scroll_velocity = Math.min(scroll_velocity, 2);

        fling_end = (long) scroll_velocity * 2000;

        fling = new CountDownTimer(fling_end, fling_tick_rate) {
            @Override public void onTick(long t) {
                //get the new count value (next tick)
                t = (fling_end - t) / 1000;
                gamma = speedFunction(t);

                if(gamma < 0) {
                    cancel();
                }

                //defines sign of angle
                gamma *= (e2.getY() > e1.getY()) ? -1 : 1;

                scrolledBy(gamma);
            }

            @Override public void onFinish() {}
        }.start();

        return true;
    }

    /*
     * @return: whether the transferred motionevent is in the visible area
     * of the active option circle
     */
    private boolean isInVisibleOption(MotionEvent e) {
        Vector2D d = getVectorFromPointToMouse(start_point, e);
        return d.length < option_r;
    }

    /*
     * @return: decides whether the touch event occured in the click option
     */
    private boolean isInClickOption(MotionEvent e) {
        double alpha = DrawLayout.alpha[0];
        Vector2D click = getVectorFromPointToMouse(menu_circle, e);

        boolean rightAngle = Math.abs(alpha - click.basicAngle()) < DrawLayout.delta_alpha;
        boolean rightLength = Math.abs(click.getLength() - circle_r) < context.getResources().getDimension(R.dimen.scroll_option_radius);

        return rightAngle && rightLength;
    }

    /*
     * @return vector between Vector and MouseEvent
     * @param 'v': instance of Vector2D
     * @param 'e': instance of MotionEvent (only option_x and option_y are important)
     */
    private Vector2D getVectorFromPointToMouse(Vector2D v, MotionEvent e) {
        return new Vector2D(v.x, v.y, (int) e.getX(), (int) e.getY());
    }

    /*
     * speedfunction: f(0) = max & f(fling_end) = 0
     * @return: option_y-value of the speed function = difference in angle
     * @param 'option_x': option_x-value = time from start in milli seconds
     */
    private double speedFunction(double x) {
        double result;
        double end = fling_end / 1000;

        //quadratic function with f(fling_end) = 0
        result = Math.pow(x - end, 2);

        //quadratic function with f(0) = max
        result *= scroll_velocity / Math.pow(end, 2);

        //compresses function to interpret it as angles
        return result * 0.05;
    }

    //******************************************************* callback methods *******************************************************//
    /*
     * called when a singled tap was registered, that is on the circles rim
     * @param 'gamma': is the basic angle of single tap point relative to the circle's central point
     */
    public void tabbedOn(double gamma, boolean wasInMenuRing) { }

    /*
     * called on every onScroll Event
     * @param 'gamma': difference of the angles of the current and the last mouse position
     */
    public void scrolledBy(double gamma) {}

    /*
     * called when onDown is called in the area around the intersecction between circle and current handside
     */
    public void activateMenu() {}

    /*
     * called when a singleTab occurred outside of the circles rim
     */
    public void deactivateMenu() {}
}

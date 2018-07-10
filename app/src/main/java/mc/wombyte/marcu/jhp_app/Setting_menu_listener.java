package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by marcu on 17.07.2017.
 */

public class Setting_menu_listener implements View.OnTouchListener {

    Context context;
    GestureDetector gd;
    DisplayMetrics metrics;
    int display_h, display_w;

    boolean end = false;
    ArrayList<ArrayList<Integer>> points = new ArrayList<>();
    int a1, a2;
    int b1, b2;
    int c1, c2;

    int x, y, r;
    int side;
    boolean inSettingActivity;

    public Setting_menu_listener() { }

    public Setting_menu_listener(Context context, boolean inSettingActivity) {
        this.context = context;
        gd = new GestureDetector(context, new GestureListener());
        metrics = context.getResources().getDisplayMetrics();
        display_h = metrics.heightPixels;
        display_w = metrics.widthPixels;
        this.inSettingActivity = inSettingActivity;
    }

    public void onScrollFinished(int x, int y, int r, int side) {}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gd.onTouchEvent(motionEvent);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float vx, float vy) {
            if(end) {
                return false;
            }

            if(e2.getY() < 0.8*display_h || points.size() < 4) {
                //saves points
                ArrayList<Integer> p = new ArrayList<>();
                p.add((int) e2.getX());
                p.add((int) e2.getY());
                points.add(p);
                return true;
            }
            else {
                end = true;
                //get 3 points
                a1 = (int) e1.getX();
                a2 = (int) e1.getY();
                b1 = points.get( points.size()/2).get(0);
                b2 = points.get( points.size()/2).get(1);
                c1 = (int) e2.getX();
                c2 = (int) e2.getY();
                calculateCircle();
                onScrollFinished(x, y, r, side);
                return false;
            }
        }
    }

    /*
     * calculates the coordinates and the radius of the circle
     */
    public void calculateCircle() {
        double d;
        d = 2 * (a1*(b2-c2) + b1*(c2-a2) + c1*(a2-b2));
        x = (int) (((Math.pow(a1, 2) + Math.pow(a2, 2))*(b2-c2)
                + (Math.pow(b1, 2) + Math.pow(b2, 2))*(c2-a2)
                + (Math.pow(c1, 2) + Math.pow(c2, 2))*(a2-b2)
                ) / d);
        y = (int) (((Math.pow(a1, 2) + Math.pow(a2, 2))*(c1-b1)
                + (Math.pow(b1, 2) + Math.pow(b2, 2))*(a1-c1)
                + (Math.pow(c1, 2) + Math.pow(c2, 2))*(b1-a1)
                ) / d);
        r = (int) (Math.sqrt( Math.pow(x-a1, 2) + Math.pow(y-a2, 2)));

        if(inSettingActivity) {
            y += context.getResources().getDimensionPixelOffset(R.dimen.setting_menu_toolbar_height);
        }

        //getting hand side
        if(x < display_w/2) {
            side = Storage.settings.LEFT_HAND_SIDE;
        }
        else {
            side = Storage.settings.RIGHT_HAND_SIDE;
        }
    }

    /*
     * defines whether the circle is draw in settings activity or not
     */
    public void inSettingActivity(boolean inSettingActivity) {
        this.inSettingActivity = inSettingActivity;
    }
}

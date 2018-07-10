package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by marcu on 02.10.2017.
 */

public class Menu_listener implements View.OnTouchListener {

    GestureDetector gd;
    int width;

    public Menu_listener() {}

    public Menu_listener(Context context) {
        gd = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    //part = number of the menu-sector
    public void onPart(int part) {}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        width = view.getWidth();
        return gd.onTouchEvent(motionEvent);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(e1.getX() < e2.getX()) {
                onSwipeRight();
            }
            else {
                onSwipeLeft();
            }

            return true;
        }

        @Override public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            onPart(x*4 / width);
            return true;
        }
    }

}

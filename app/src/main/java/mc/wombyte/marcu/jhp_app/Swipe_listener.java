package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by marcu on 17.07.2017.
 */

public class Swipe_listener implements View.OnTouchListener {

    GestureDetector gd;

    public Swipe_listener() { }

    public Swipe_listener(Context context) {
        gd = new GestureDetector(context, new GestureListener());
    }

    public void swipeRight() {}
    public void swipeLeft() {}
    public void swipeBottom() {}
    public void swipeTop() {}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gd.onTouchEvent(motionEvent);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float dx = Math.abs(e1.getX() - e2.getX());
            float dy = Math.abs(e1.getY() - e2.getY());

            if(dx > dy) {
                if(e1.getX() < e2.getX()) {
                    swipeRight();
                }
                else {
                    swipeLeft();
                }
            }
            else {
                if(e1.getY() < e2.getY()) {
                    swipeBottom();
                }
                else {
                    swipeTop();
                }
            }
            return true;
        }
    }
}

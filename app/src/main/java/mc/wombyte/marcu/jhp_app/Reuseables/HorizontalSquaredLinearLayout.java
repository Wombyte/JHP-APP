package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by marcu on 8.7.2018.
 */

public class HorizontalSquaredLinearLayout extends LinearLayout {
    public HorizontalSquaredLinearLayout(Context context) {
        super(context);
    }

    public HorizontalSquaredLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalSquaredLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}


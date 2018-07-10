package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by marcu on 22.12.2017.
 */

public class VerticalSquaredLinearLayout extends LinearLayout {
    public VerticalSquaredLinearLayout(Context context) {
        super(context);
    }

    public VerticalSquaredLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSquaredLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}

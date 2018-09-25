package mc.wombyte.marcu.jhp_app.reuseables;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcu on 8.7.2018.
 */

public class SquaredLayout extends LinearLayout {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    int square_type = 0;

    public SquaredLayout(Context context, int square_type) {
        super(context);
        setMode(square_type);
    }

    public SquaredLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquaredLayout);
        square_type = a.getInt(R.styleable.SquaredLayout_square, 0);
        a.recycle();

        setMode(square_type);
    }

    public SquaredLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquaredLayout, defStyle, 0);
        square_type = a.getInt(R.styleable.SquaredLayout_square, 0);
        a.recycle();

        setMode(square_type);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (square_type == VERTICAL)? widthMeasureSpec : heightMeasureSpec;
        super.onMeasure(size, size);
    }

    /**
     * sets the square mode to the transmitted value
     * {@link this#VERTICAL} or {@link this#HORIZONTAL}
     * @param square_type: new square type
     */
    public void setMode(int square_type) {
        this.square_type = square_type;
        switch(square_type) {
            case VERTICAL:
                this.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                ));
                break;
            case HORIZONTAL:
                this.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                ));
                break;
        }
    }
}


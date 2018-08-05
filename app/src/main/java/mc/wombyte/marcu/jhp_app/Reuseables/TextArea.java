package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.InputType;
import android.util.AttributeSet;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 29.09.2017.
 */

public class TextArea extends android.support.v7.widget.AppCompatEditText {

    Context context;

    LayerDrawable drawable;
    GradientDrawable border;

    /*
     * constructors needed for xml
     */
    public TextArea(Context context) {
        super(context);
        this.context = context;
        onCreateView();
    }

    public TextArea(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        onCreateView();
    }

    public TextArea(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
        this.context = context;
        onCreateView();
    }

    /*
     * method which shows the time picker
     */
    private void onCreateView() {
        this.setPadding(
                context.getResources().getDimensionPixelOffset(R.dimen.textarea_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.textarea_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.textarea_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.textarea_padding)
        );
        this.setSingleLine(false);
        this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        this.setBackground( getResources().getDrawable(R.drawable.textarea));
    }

    /*
     * changes the color of the border
     */
    public void changeBorderColor(int color) {
        drawable = (LayerDrawable) getResources().getDrawable(R.drawable.textarea);
        border = (GradientDrawable) drawable.findDrawableByLayerId(R.id.textarea_border);
        border.setColor(color);
        this.setBackground(drawable);
    }

}

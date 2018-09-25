package mc.wombyte.marcu.jhp_app.reuseables;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by marcu on 03.10.2017.
 */

public class ViewSwitcher extends LinearLayout {

    public int active_view;

    Context context;
    ArrayList<View> view = new ArrayList<>();
    ArrayList<ViewGroup.LayoutParams> params = new ArrayList<>();

    //******************************************************* xml constructors *******************************************************//

    /*
     * constructors needed for xml
     * the first view is automatically the visible one
     */
    public ViewSwitcher(Context context) {
        super(context);
    }
    public ViewSwitcher(Context context, AttributeSet set) {
        super(context, set);
    }
    public ViewSwitcher(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
    }

    //******************************************************* methods *******************************************************//

    /*
     * declares all variables
     * method called after the constructors
     */
    public ViewSwitcher createView(Context context) {
        this.context = context;
        for(int i = 0; i < this.getChildCount(); i++) {
            view.add( this.getChildAt(i));
            params.add( view.get(i).getLayoutParams());
        }
        switchToView(0);

        return this;
    }

    /*
     * switches to the translated view
     */
    public void switchToView(int i) {
        this.removeAllViews();
        view.get(i).setLayoutParams(params.get(i));
        this.addView(view.get(i));
        active_view = i;
    }

    /*
     * checks whether the view on position i is active
     */
    public boolean isView(int i) {
        return active_view == i;
    }

    //Getter
    public View getView(int i) { return view.get(i); }
    public ViewGroup.LayoutParams getLayoutParams(int i) { return params.get(i); }
    public View getActiveView() { return view.get(active_view); }
    public int getViewCount() { return view.size(); }
    public Class getViewClass(int i) { return view.get(i).getClass(); }

    //Setter
    public void setView(View view, int i) { this.view.set(i, view); }
    public void setLayoutParams(ViewGroup.LayoutParams params, int i) { this.params.set(i, params); }
}

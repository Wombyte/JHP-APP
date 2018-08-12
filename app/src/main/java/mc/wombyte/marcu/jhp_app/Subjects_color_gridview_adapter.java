package mc.wombyte.marcu.jhp_app;

/**
 * Created by marcu on 13.03.2018.
 */

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Reuseables.SquaredLayout;

public class Subjects_color_gridview_adapter extends ArrayAdapter<Integer> {

    SquaredLayout container;

    Context context;
    int pos;

    public Subjects_color_gridview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Subjects_color_gridview_adapter(Context context, int resource, ArrayList<Integer> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.subjects_color_gridview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Integer p = getItem(position);

        if (p != null) {
            container = view.findViewById(R.id.container_subject_color_gridview);

            int color = p;
            changeBackgroundColor(container, p);
        }

        return view;
    }

    /**
     * changes the background color of a drawable only bounded to <shape></shape>
     * @param v: view
     * @param color: new color for the background
     */
    private void changeBackgroundColor(View v, int color) {
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(color);
    }
}

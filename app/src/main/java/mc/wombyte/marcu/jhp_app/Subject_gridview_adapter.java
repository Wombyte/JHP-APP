package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.classes.Subject;
import mc.wombyte.marcu.jhp_app.reuseables.SquaredLayout;

/**
 * Created by Marcus Wunderlich on 21.12.2017.
 */

public class Subject_gridview_adapter extends ArrayAdapter<Subject> {

    Context context;
    int pos;

    SquaredLayout background;
    TextView tv_abbreviation;
    TextView tv_average;

    LayerDrawable drawable;
    GradientDrawable border;

    public Subject_gridview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Subject_gridview_adapter(Context context, int resource, ArrayList<Subject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.subjects_gridview_fragment, null);
            context = container.getContext();
        }

        pos = position;
        Subject p = getItem(position);

        if (p != null) {
            background = view.findViewById(R.id.background_subjects_gridview);
            tv_abbreviation = (TextView) view.findViewById(R.id.tv_abbreviation_subjects_gridview);
            tv_average = (TextView) view.findViewById(R.id.tv_average_subjects_gridview);

            //content
            changeBorderColor( p.getColor());
            tv_abbreviation.setText( p.getAbbreviation());
            tv_abbreviation.setTextColor( p.getColor());
            tv_average.setText( Storage.settings.subjects_getAverageDecimalFormat().format(p.getAverage()));
            tv_average.setTextColor( p.getColor());
        }

        return view;
    }

    /**
     * changes the color of the border
     * @param color: new color of the border
     */
    private void changeBorderColor(int color) {
        drawable = (LayerDrawable) background.getResources().getDrawable(R.drawable.subjects_gridview_background);
        border = (GradientDrawable) drawable.findDrawableByLayerId(R.id.border);
        border.setColor(color);
        background.setBackground(drawable);
    }

}

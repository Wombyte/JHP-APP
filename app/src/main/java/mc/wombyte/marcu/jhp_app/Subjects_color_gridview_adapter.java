package mc.wombyte.marcu.jhp_app;

/**
 * Created by marcu on 13.03.2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Reuseables.VerticalSquaredLinearLayout;

public class Subjects_color_gridview_adapter extends ArrayAdapter<Integer> {

    VerticalSquaredLinearLayout container;

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
            container.setBackgroundColor(color);
        }

        return view;
    }
}

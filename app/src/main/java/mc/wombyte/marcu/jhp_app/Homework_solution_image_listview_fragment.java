package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Reuseables.BitmapWorkerTask;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewHolder;

/**
 * Created by marcus on 15.5.2015.
 */

public class Homework_solution_image_listview_fragment extends ArrayAdapter<Uri> {

    Context context;
    int pos;
    boolean with_add_field;

    private ViewHolder holder;
    private View view1, view2;

    public Homework_solution_image_listview_fragment(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Homework_solution_image_listview_fragment(Context context, int resource, ArrayList<Uri> items, boolean with_add_field) {
        super(context, resource, items);
        this.with_add_field = with_add_field;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        context = container.getContext();
        pos = position;

        //add fragment
        if(pos == 0 && with_add_field) {
            if(view == null || !view.equals(view1)) {
                view1 = inflater.inflate(R.layout.homework_solution_add_file_listview_fragment, null);

                holder = new ViewHolder();
                holder.addViewList(new ArrayList<TextView>());
                holder.addView( view1.findViewById(R.id.tv_file_solution_homework), 0);
                holder.addViewList(new ArrayList<ImageView>());
                holder.addView( view1.findViewById(R.id.image_file_solution_homework), 1);

                view1.setTag(holder);
            }
            else {
                holder = (ViewHolder) view1.getTag();
            }

            ((TextView) holder.getView(0 ,0)).setText( context.getResources().getString(R.string.homework_solution_add_image));
            ((ImageView) holder.getView(1, 0)).setImageDrawable(getDrawable());
            return view1;
        }
        //image fragment
        else {
            if(view == null || !view.equals(view2)) {
                view2 = inflater.inflate(R.layout.homework_solution_image_listview_fragment, null);

                holder = new ViewHolder();
                holder.addViewList(new ArrayList<ImageView>());
                holder.addView( view2.findViewById(R.id.imageview_listview_fragment), 0);

                view2.setTag(holder);
            }
            else {
                holder = (ViewHolder) view2.getTag();
            }

            new BitmapWorkerTask(context, holder.getView(0, 0)).execute( getItem(position));

            return view2;
        }
    }

    /*
     * returns the right drawable for the add fragment
     */
    private Drawable getDrawable() {
        Drawable result = context.getResources().getDrawable(R.drawable.symbol_homework_image).mutate();
        result.setColorFilter( context.getResources().getColor(R.color.homework_solution_images), PorterDuff.Mode.SRC_ATOP);
        return result;
    }
}



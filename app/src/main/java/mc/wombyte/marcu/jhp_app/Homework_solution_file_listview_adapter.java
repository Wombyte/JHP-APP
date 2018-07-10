package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Reuseables.ViewHolder;

/**
 * Created by marcus on 14.5.2018.
 */

public class Homework_solution_file_listview_adapter extends ArrayAdapter<String> {

    Context context;
    int pos;

    int kind;
    int color_id;
    int drawable_id;

    private ViewHolder holder;
    private View view1, view2;

    public Homework_solution_file_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Homework_solution_file_listview_adapter(Context context, int resource, int kind, int drawable_id, int color_id, ArrayList<String> items) {
        super(context, resource, items);
        this.kind = kind;
        this.color_id = color_id;
        this.drawable_id = drawable_id;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        context = container.getContext();
        pos = position;

        //add fragment
        if(pos == 0) {
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

            ((TextView) holder.getView(0, 0)).setText(getText());
            ((ImageView) holder.getView(1, 0)).setImageDrawable(getDrawable());

            return view1;
        }
        //list fragment
        else {
            if(view == null || !view.equals(view2)) {
                view2 = inflater.inflate(R.layout.homework_solution_file_listview_fragment, null);

                holder = new ViewHolder();
                holder.addViewList(new ArrayList<TextView>());
                holder.addView( view2.findViewById(R.id.tv_file_solution_listview_homework), 0);
                holder.addViewList(new ArrayList<ImageView>());
                holder.addView( view2.findViewById(R.id.image_file_solution_listview_homework), 1);

                view2.setTag(holder);
            }
            else {
                holder = (ViewHolder) view2.getTag();
            }

            System.out.println(getItem(pos));
            ((TextView) holder.getView(0, 0)).setText( getItem(pos));
            ((ImageView) holder.getView(1, 0)).setImageDrawable(getDrawable());

            return view2;
        }
    }

    /*
     * returns the right drawable depending on the kind
     */
    private Drawable getDrawable() {
        int id = 0;
        switch (kind) {
            case 1:
                id = R.drawable.symbol_homework_docs; break;
            case 2:
                id = R.drawable.symbol_homework_table; break;
            case 3:
                id = R.drawable.symbol_homework_slides; break;
        }

        Drawable result = context.getResources().getDrawable(id);
        result.setColorFilter( context.getResources().getColor(color_id), PorterDuff.Mode.SRC_ATOP);
        return result;
    }

    /*
     * returns the right text depending on the kind
     */
    private String getText() {
        int id = 0;
        switch (kind) {
            case 1:
                id = R.string.homework_solution_add_docs; break;
            case 2:
                id = R.string.homework_solution_add_sheets; break;
            case 3:
                id = R.string.homework_solution_add_slides; break;
        }
        return context.getResources().getString(id);
    }

    //getting title from link https://stackoverflow.com/questions/28503418/is-there-a-way-to-get-the-name-of-a-file-using-the-google-drive-api
}

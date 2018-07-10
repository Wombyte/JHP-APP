package mc.wombyte.marcu.jhp_app.Reuseables.HorizontalImageListView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 08.07.2018.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    ViewHolder(View v) {
        super(v);
        imageView = v.findViewById(R.id.imageview_horizontal_image_listview);
    }
}

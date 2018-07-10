package mc.wombyte.marcu.jhp_app.Reuseables.HorizontalImageListView;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;
import mc.wombyte.marcu.jhp_app.Reuseables.BitmapWorkerTask;

/**
 * Created by marcus on 08.07.2018.
 */

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Uri> images;
    private Context context;

    public Adapter(ArrayList<Uri> images) {
        this.images = images;
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.horizontal_image_listview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        new BitmapWorkerTask(context, holder.imageView).execute( images.get(position));
    }

    @Override public int getItemCount() {
        return images.size();
    }
}

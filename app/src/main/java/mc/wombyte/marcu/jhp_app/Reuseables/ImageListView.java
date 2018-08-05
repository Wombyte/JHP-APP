package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import mc.wombyte.marcu.jhp_app.R;


/**
 * Created by marcus on 08.07.2018.
 */
public class ImageListView extends RecyclerView {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private Context context;
    private ArrayList<Uri> images = new ArrayList<>();
    private LayoutManager manager;
    private ListViewAdapter adapter;
    private int mode;

    public ImageListView(Context context, int mode, ArrayList<Uri> images) {
        super(context);
        this.images = images;
        this.mode = mode;
        onCreateView(context);
    }
    public ImageListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageListView);
        mode = a.getInt(R.styleable.ImageListView_mode, 0);
        a.recycle();

        onCreateView(context);
    }
    public ImageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageListView, defStyle, 0);
        mode = a.getInt(R.styleable.ImageListView_mode, 0);
        a.recycle();

        onCreateView(context);
    }

    /**
     * method that is called after each constructor
     * hasFixedSize improves performance
     * new horizontal layout manager is set
     * the image list is update to set the adapter
     */
    private void onCreateView(Context context) {
        this.setHasFixedSize(true);
        this.context = context;
        switch(mode) {
            case VERTICAL:
                manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                break;
            case HORIZONTAL:
                manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                break;
        }
        this.setLayoutManager(manager);
        setImageList(images);
    }

    /**
     * updates the image list and sets the matching adapter
     * if the list is null or empty the method stops
     * @param images: list of new image uris
     */
    public void setImageList(ArrayList<Uri> images) {
        if(images == null || images.size() == 0) {
            return;
        }
        this.images = images;

        adapter = new ListViewAdapter(images, mode);
        adapter.setItemShortClickListener(this::openImageDialog);
        adapter.setItemLongClickListener((pos) -> {
            if(longItemClickListener != null) {
                longItemClickListener.onItemLongClick(pos);
            }
        });
        this.setAdapter(adapter);
    }

    /**
     * opens the image dialog to zoom into the image
     * @param pos: position of the image -> uri -> image
     */
    private void openImageDialog(int pos) {
        ImageDialog dialog = new ImageDialog(context, images.get(pos));
        dialog.show();
    }

    //long click listener
    private LongItemClickListener longItemClickListener = null;
    public interface LongItemClickListener {
        void onItemLongClick(int pos);
    }
    public void setItemLongClickListener(LongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// Adapter ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class ListViewAdapter extends RecyclerView.Adapter<ImageViewHolder> {
    private ArrayList<Uri> images;
    private Context context;
    private int mode;
    private MultiTransformation imageTransformations = new MultiTransformation<>(
            new CropSquareTransformation(),
            new RoundedCorners(50)
    );

    ListViewAdapter(ArrayList<Uri> images, int mode) {
        this.images = images;
        this.mode = mode;
    }

    @NonNull
    @Override public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_listview_item, parent, false);

        return new ImageViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        holder.container.setMode(mode);
        holder.imageView.setOnClickListener((view) -> {
            if(shortClickListener != null) {
                shortClickListener.onItemClick(position);
            }
        });
        holder.imageView.setOnLongClickListener((view) -> {
            if(longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });
        new BitmapWorkerTask(context, holder.imageView, imageTransformations).execute( images.get(position));
    }

    @Override public int getItemCount() {
        return images.size();
    }

    //short click listener
    private ShortClickListener shortClickListener = null;
    interface ShortClickListener {
        void onItemClick(int pos);
    }
    public void setItemShortClickListener(ShortClickListener shortItemClickListener) {
        this.shortClickListener = shortItemClickListener;
    }

    //long click listener
    private LongClickListener longClickListener = null;
    interface LongClickListener {
        void onItemLongClick(int pos);
    }
    public void setItemLongClickListener(LongClickListener longItemClickListener) {
        this.longClickListener = longItemClickListener;
    }
}


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////// ViewHolder //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

class ImageViewHolder extends RecyclerView.ViewHolder {

    public SquaredLayout container;
    public ImageView imageView;

    ImageViewHolder(View v) {
        super(v);
        container = v.findViewById(R.id.container_image_listview);
        imageView = v.findViewById(R.id.imageview_image_listview);
    }
}


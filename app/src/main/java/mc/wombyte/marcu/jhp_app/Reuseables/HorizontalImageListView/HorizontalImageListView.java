package mc.wombyte.marcu.jhp_app.Reuseables.HorizontalImageListView;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;


/**
 * Created by marcus on 08.07.2018.
 */
public class HorizontalImageListView extends RecyclerView {

    private Context context;
    private ArrayList<Uri> images = new ArrayList<>();


    public HorizontalImageListView(Context context, ArrayList<Uri> images) {
        super(context);
        this.images = images;
        this.context = context;
        onCreateView();
    }
    public HorizontalImageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        onCreateView();
    }
    public HorizontalImageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        onCreateView();
    }

    /**
     * method that is called after each constructor
     * hasFixedSize improves performance
     * new horizontal layout manager is set
     * the image list is update to set the adapter
     */
    private void onCreateView() {
        this.setHasFixedSize(true);
        this.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
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
        this.setAdapter(new mc.wombyte.marcu.jhp_app.Reuseables.HorizontalImageListView.Adapter(images));
    }
}


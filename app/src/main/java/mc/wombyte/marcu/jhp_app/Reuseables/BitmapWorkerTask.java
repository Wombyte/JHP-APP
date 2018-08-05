package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;

import mc.wombyte.marcu.jhp_app.GlideApp;
import mc.wombyte.marcu.jhp_app.R;

/**
 * by marcus 08.07.2019
 */

public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private MultiTransformation transformations;
    private Context context;

    public BitmapWorkerTask(Context context, ImageView imageView, MultiTransformation transformations) {
        this.context = context;
        this.transformations = transformations;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
    }

    public BitmapWorkerTask(Context context, ImageView imageView) {
        this.context = context;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override protected Bitmap doInBackground(Uri... params){
        try {
            return GlideApp.with(context)
                    .asBitmap()
                    .load(params[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit()
                    .get();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("creating bitmap not successful");
            return null;
        }
    }

    @Override protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()) bitmap = null;

        ImageView imageView = imageViewReference.get();
        if(imageView == null) {
            return;
        }

        if (bitmap != null) {
            if(transformations == null) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(bitmap)
                        .into(imageView);
            }
            else {
                GlideApp.with(context)
                        .asBitmap()
                        .load(bitmap)
                        .transform(transformations)
                        .into(imageView);
            }
        }
        else {
            Drawable placeholder = context.getResources().getDrawable(R.drawable.symbol_homework_image).mutate();
            placeholder.setColorFilter( context.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            imageView.setImageDrawable(placeholder);
            System.out.println("bitmap sizes: null");
        }
    }
}
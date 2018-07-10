package mc.wombyte.marcu.jhp_app.Reuseables;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mc.wombyte.marcu.jhp_app.GlideApp;

/**
 * Created by marcus on 08.07.2018.
 */
public class BitmapSaveTask extends AsyncTask<Uri, Void, Bitmap> {
    private Context context;
    private File file;

    public BitmapSaveTask(Context context, File file) {
        this.context = context;
        this.file = file;
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

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


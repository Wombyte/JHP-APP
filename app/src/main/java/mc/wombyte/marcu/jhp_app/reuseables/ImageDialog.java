package mc.wombyte.marcu.jhp_app.reuseables;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Window;
import android.widget.ImageButton;

import com.github.chrisbanes.photoview.PhotoView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 05.08.2018.
 */
public class ImageDialog extends Dialog {

    private PhotoView image;
    private ImageButton b_back;

    public ImageDialog(Context context, Uri uri) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.image_dialog);

        //initialization
        image = findViewById(R.id.photoview_image_dialog);
        b_back = findViewById(R.id.b_back_image_dialog);

        //content
        new BitmapWorkerTask(context, image).execute(uri);

        //listener
        b_back.setOnClickListener((view) -> dismiss());
    }
}

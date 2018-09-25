package mc.wombyte.marcu.jhp_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by marcus on 01.06.2018.
 */

public class ImagePickerDialog extends Dialog {

    Context context;
    Activity activity;

    LinearLayout b_gallery;
    LinearLayout b_camera;
    ImageView image_camera;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private int use_code;
    public static final int HOMEWORK_SOLUTION_IMAGE = 10;
    public static final int HOMEWORK_DESCRIPTION_IMAGE = 20;
    public static final int GRADE_DESCRIPTION_IMAGE = 30;

    public ImagePickerDialog(Context context, Activity activity, int use_code) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.image_picker_dialog);
        this.context = context;
        this.activity = activity;
        this.use_code = use_code;

        b_gallery = findViewById(R.id.b_gallery_image_picker_dialog);
        b_camera = findViewById(R.id.b_camera_image_picker_dialog);

        b_gallery.setOnClickListener((v) -> onclick_gallery());
        b_camera.setOnClickListener((v) -> onclick_camera());

        if(!(this.activity.getPackageManager()).hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            image_camera = findViewById(R.id.image_camera_image_picker_dialog);
            image_camera.setColorFilter( context.getResources().getColor(R.color.colorAccent));
            b_camera.setEnabled(false);
        }
    }

    /*
     * onclick: gallery
     * opens the gallery activity
     */
    private void onclick_gallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto , use_code + GALLERY_REQUEST_CODE);
        dismiss();
    }

    /*
     * onclick b_camera: opens the camera
     * partly copied from: "https://developer.android.com/training/camera/photobasics"
     */
    private static Uri photo_uri = null;
    private void onclick_camera() {
        if(!Storage.CAMERA_ALLOWED) {
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();

        if (photoFile != null) {
            photo_uri = FileProvider.getUriForFile(context,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);
            activity.startActivityForResult(takePictureIntent, use_code + CAMERA_REQUEST_CODE);
        }
        dismiss();
    }

    /*
     * creates the file in which the image from the camera is saved to
     * copied from: "https://developer.android.com/training/camera/photobasics"
     */
    private File createImageFile() {
        File result = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        String name = "JPEG_" + timeStamp + "_";
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            result = File.createTempFile(name, ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /*
     * returns depending on the transferred data the uri of the selected/taken photo
     */
    public static Uri getUri(int requestCode, Intent imageReturnedIntent) {
        switch(requestCode%10) {
            case GALLERY_REQUEST_CODE: return imageReturnedIntent.getData();
            case CAMERA_REQUEST_CODE: return photo_uri;
            default: return null;
        }
    }
}

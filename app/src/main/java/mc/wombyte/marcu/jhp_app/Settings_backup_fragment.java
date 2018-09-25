package mc.wombyte.marcu.jhp_app;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by marcus on 15.08.2018.
 */
public class Settings_backup_fragment extends Settings_list_fragment {

    Settings_activity activity;

    TextView text;
    ImageButton b_backup;

    public Settings_backup_fragment() { }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.setup_text_fragment, container, false);

        //initialization
        text = view.findViewById(R.id.tv_message_setup_text_fragment);
        b_backup = view.findViewById(R.id.b_image_setup_text_fragment);

        //content
        text.setText(R.string.settings_general_backup_start_text);
        b_backup.setOnClickListener((v) -> startToBackup());

        activity = ((Settings_activity) getActivity());

        return view;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// methods to backup ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * onclick method to start the backup
     * text is changed to the loading text
     * backup is executed
     * fragment is closed and the activity returns to its parent
     */
    public void startToBackup() {
        text.setText(R.string.settings_general_backup_text);

        File src = new File(Environment.getExternalStorageDirectory().toString(), "JHP");
        File des = new File(Environment.getExternalStorageDirectory().toString(), "JHP Backup");
        copyFileOrDirectory(src, des);

        activity.returnToParent();
    }

    /**
     * recursive method, that copies the sub folders of src to the des folder
     * if the source does not exist, nothing happens
     * if the source is a dir, all sub files are copied
     * if the source is a file, {@link this#copyFile(File, File)} is called
     * @param src: file that will be copied
     * @param des: location of the copy
     */
    public void copyFileOrDirectory(File src, File des) {
        if(!src.exists()) {
            return;
        }

        try {
            if(src.isDirectory()) {
                for(File file : src.listFiles()) {
                    File des1 = new File(des, file.getName());
                    copyFileOrDirectory(file, des1);
                }
            } else {
                copyFile(src, des);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * copies the transmitted source file to the destination location
     * makes sure that the des file and its parent exist, else stops
     * copies the file via {@link FileChannel#transferFrom(ReadableByteChannel, long, long)}
     * @param src: file that should be copied
     * @param des: destination location of the copy
     * @throws IOException: creating/copying files/folder
     */
    public void copyFile(File src, File des) throws IOException {
        if (!des.getParentFile().exists()) {
            if(!des.getParentFile().mkdirs()) {
                return;
            }
        }

        if (!des.exists()) {
            if(!des.createNewFile()) {
                return;
            }
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(des).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}

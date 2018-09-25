package mc.wombyte.marcu.jhp_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by marcu on 28.12.2017.
 */

public abstract class JHP_Activity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final static int REQUEST_GRANTED = 0;

    public ArrayList<Option> options = new ArrayList<>();
    int menu_container_id;
    FragmentManager fm_scroll;

    //******************************************************* Menu Fragment *******************************************************//

    Activity_scroll_fragment scroll_fragment = new Activity_scroll_fragment();

    /*
     * sets the container for the menu_fragment
     */
    public void setMenuContainerId(int id) {
        menu_container_id = id;
    }

    /*
     * sets the option array in the menu fragment
     */
    public void setOptions() { scroll_fragment.setOptionList(options); }

    /*
     * sets the option id that should be shown during inactivity
     */
    public void setOptionId(int index) {
        scroll_fragment.setOptionIndex(index+1);
    }

    /*
     * Overrides the onStart method, which is called after onCreateView (in subclasses)
     * adds the menu_fragment to the container set before
     */
    @Override protected void onStart() {
        super.onStart();

        //set scroll fragment
        if(fm_scroll == null) {
            fm_scroll = getFragmentManager();
            FragmentTransaction ft_scroll = fm_scroll.beginTransaction();
            scroll_fragment.setOnOptionClickListener(new Activity_scroll_fragment.OnOptionClickListener() {
                @Override
                public void onOptionClick(int i) {
                    optionActions(i);
                }
            });
            ft_scroll.add(menu_container_id, scroll_fragment, "SCROLL");
            ft_scroll.commit();
        }

        //Notification
        NotificationJHP n = new NotificationJHP(this);
        n.show();
    }

    /*
     * method called, when the menu is clicked
     */
    public void optionActions(int i) {}


    //******************************************************* permissions *******************************************************//

    /*
     * checks whether the user allows to read from the file
     */
    public void askForPermissions() {
        System.out.println("asking for permission");
        boolean writePermissionAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean readPermissionAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean internetAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        boolean cameraAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean shouldShowWriteExplanation = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean shouldShowReadExplanation = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean shouldShowInternetExplanation = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.INTERNET);
        boolean shouldShowCameraExplanation = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA);

        System.out.println("JHP-Activity write: " + writePermissionAllowed);
        System.out.println("JHP-Activity read: " + readPermissionAllowed);
        System.out.println("JHP-Activity internet: " + internetAllowed);
        System.out.println("JHP-Activity camera: " + cameraAllowed);

        if(!writePermissionAllowed || !readPermissionAllowed || !internetAllowed || !cameraAllowed) {
            System.out.println("permission is granted");
            if(shouldShowWriteExplanation || shouldShowReadExplanation || shouldShowInternetExplanation || shouldShowCameraExplanation) {
                System.out.println("explanation");
                if(shouldShowWriteExplanation) {
                    showExplanationDialog(R.string.permission_write_explanation);
                }
                if(shouldShowReadExplanation) {
                    showExplanationDialog(R.string.permission_read_explanation);
                }
                if(shouldShowInternetExplanation) {
                    showExplanationDialog(R.string.permission_internet_explanation);
                }
                if(shouldShowCameraExplanation) {
                    showExplanationDialog(R.string.permission_camera_explanation);
                }
            }
            else {
                System.out.println("asking again");
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                android.Manifest.permission.INTERNET,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },
                        REQUEST_GRANTED
                );
            }
        }
        Storage.WRITE_EXTERNAL_STORAGE_ALLOWED = true;
        Storage.READ_EXTERNAL_STORAGE_ALLOWED = true;
        Storage.INTERNET_ALLOWED = true;
        Storage.CAMERA_ALLOWED = true;
        loadData();
    }

    /*
     * shows the explanation dialog
     */
    private void showExplanationDialog(int explanation_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(explanation_id);
        AlertDialog dialog = builder.create();
        builder.setPositiveButton(R.string.permission_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_GRANTED && grantResults.length > 3) {
            Storage.INTERNET_ALLOWED = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Storage.WRITE_EXTERNAL_STORAGE_ALLOWED = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            Storage.READ_EXTERNAL_STORAGE_ALLOWED = grantResults[2] == PackageManager.PERMISSION_GRANTED;
            Storage.CAMERA_ALLOWED = grantResults[3] == PackageManager.PERMISSION_GRANTED;

            if(Storage.READ_EXTERNAL_STORAGE_ALLOWED) {
                loadData();
            }
        }
    }

    /*
     * loads the necessary data for the user
     */
    private void loadData() {

        //reading data, if the app is started new
        if (!Storage.read_already) {
            System.out.println("load data");
            Storage.read_already = true;
            FileLoader fileReader = new FileLoader();
            fileReader.readData();
        }

        //initial setup if first used
        if (Storage.settings.general_newSetup()) {
            openSetupActivity();
        }
    }

    /*
     * opens the activity for the initial setup of the app
     */
    private void openSetupActivity() {
        Intent toSetupActivity = new Intent();
        toSetupActivity.setClass(this, SetupActivity.class);
        this.startActivity(toSetupActivity);
    }
}

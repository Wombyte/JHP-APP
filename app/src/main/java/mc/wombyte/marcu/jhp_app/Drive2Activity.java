package mc.wombyte.marcu.jhp_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by marcus on 10.06.2018.
 */
public class Drive2Activity extends Activity {

    GoogleSignInOptions signInOption;
    GoogleSignInClient signInClient;
    GoogleSignInAccount signInAccount;

    DriveFile file;

    private static final int RC_SIGN_IN = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_two_activity);

        //sign in
        if(signInClient == null) {
            signInOption = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            signInClient = GoogleSignIn.getClient(this, signInOption);
        }

        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    signInAccount = task.getResult(ApiException.class);
                } catch(ApiException e) {
                    Toast.makeText(this, "failed to login", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //******************************************************* listener *******************************************************//
    public void onSignIn(View view) {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //https://developers.google.com/drive/api/v3/manage-uploads
    public void onCreateFile(View view) {
        //https://developers.google.com/drive/android/appfolder
        final Task<DriveFolder> rootFolderTask = getDriveResourceClient().getRootFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write("Probe");
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("HelloWorld")
                            .setMimeType("text/plain")
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(
                        this,
                        driveFile -> {
                            file = driveFile;
                            Toast.makeText(this, "successful created a file: " + file.getDriveId().encodeToString(), Toast.LENGTH_SHORT).show();
                        }
                )
                .addOnFailureListener(
                        this,
                        e -> Toast.makeText(this, "failed to create a file", Toast.LENGTH_SHORT).show()
                );
    }

    //TODO: https://stackoverflow.com/questions/36612170/get-resource-id-for-google-drive-api-android
    public void openLink(String id) {
        String link = "https://drive.google.com/execute?id="+ id;

        System.out.println(link);
        Intent toDriveFile = new Intent(Intent.ACTION_VIEW);

        try {
            toDriveFile.setData(Uri.parse(link));
        }
        catch(Exception e) {
            Toast.makeText(this, "no valid link", Toast.LENGTH_LONG).show();
        }

        startActivity(toDriveFile);
    }

    public void onOpenFile(View view) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "HelloWorld"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.document"))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient()
                .query(query)
                .addOnSuccessListener(this, metadataBuffer -> {
                    if(metadataBuffer.getCount() > -1) {
                        openLink(metadataBuffer.get(0).getDriveId().getResourceId());
                    }
                    else {
                        System.out.println("size: 0");
                    }
                } )
                .addOnFailureListener(this, e ->
                    Toast.makeText(this, "error while searching for file", Toast.LENGTH_SHORT).show());
    }

    private DriveResourceClient getDriveResourceClient() {
        return Drive.getDriveResourceClient(this, signInAccount);
    }
}

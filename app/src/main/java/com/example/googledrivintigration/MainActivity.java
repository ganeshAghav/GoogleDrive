package com.example.googledrivintigration;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private boolean isAPÏConnected;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Google Drive API Client!
        connectAPIClient();

        findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start camera to take a picture

                if(mGoogleApiClient!=null && !mGoogleApiClient.isConnected())
                {
                    //Do your work
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
                }else{

                    Toast.makeText(getApplicationContext(),"Error Google API is disable or permissions are required!",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    //Create a new file and save it to Drive.
    private void saveFileToDrive1() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "saveFileToDrive() Creating new content.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(DriveContentsResult result) {
                // If the operation wasn't successful, return
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed to create new content!.");
                    return;
                }
                Log.i(TAG, "New content has been created.");
                // Get an output stream for the contents.
                OutputStream outputStream = result.getDriveContents().getOutputStream();
                // Write the bitmap data from it.
                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                try {
                    outputStream.write(bitmapStream.toByteArray());
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }
                // Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg").setTitle("myPhoto.png").build();
                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }
            }
        });
    }

    //Create a new file and save it to Drive.
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "saveFileToDrive() Creating new content.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveContentsResult driveContentsResult) {

                // If the operation wasn't successful, return
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed to create new content!.");
                    return;
                }
                Log.i(TAG, "New content has been created.");
                // Get an output stream for the contents.
                OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
                // Write the bitmap data from it.
                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                try {
                    outputStream.write(bitmapStream.toByteArray());
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }
                // Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg").setTitle("myPhoto.png").build();
                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContentsResult.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Disconnect only when the application is closed!
    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                    //saveFileToDrive();
                    check_folder_exists(BitmapToFile(mBitmapToSave));
                }
                break;
            case REQUEST_CODE_CREATOR:
                //Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) { //succesfully saved!.
                    Log.i(TAG, "Image successfully saved.");
                    mBitmapToSave = null;

                    Toast.makeText(getApplicationContext(),"Photo succesfully saved to Google Drive!",Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        isAPÏConnected = false;
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity. " + e.getMessage());
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "* API client connected !!!.");
        isAPÏConnected = true;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended.");
    }

    private synchronized void connectAPIClient(){
        if (mGoogleApiClient == null) {
            Log.i(TAG, "connectAPIClient().");
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER) // required to access app folder
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private synchronized void check_folder_exists(final File file){

        final String FOLDER_NAME="Ganesh";
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, FOLDER_NAME), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult result)
            {
                if (!result.getStatus().isSuccess())
                {
                    Log.e(TAG, "Cannot create folder in the root.");
                } else
                {
                    boolean isFound = false;
                    for (Metadata m : result.getMetadataBuffer())
                    {
                        if (m.getTitle().equals(FOLDER_NAME)) {
                            Log.e(TAG, "Folder exists");
                            isFound = true;
                            DriveId driveId = m.getDriveId();
                            UploadFile(driveId,file);
                            break;
                        }
                    }
                    if (!isFound)
                    {
                        Log.i(TAG, "Folder not found; creating it.");
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(FOLDER_NAME).build();
                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                .createFolder(mGoogleApiClient, changeSet)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                    @Override
                                    public void onResult(@NonNull DriveFolder.DriveFolderResult result)
                                    {
                                        if (!result.getStatus().isSuccess())
                                        {
                                            Log.e(TAG, "Error while trying to create the folder");
                                        } else {
                                            Log.i(TAG, "Created a folder");
                                            DriveId driveId = result.getDriveFolder().getDriveId();
                                            UploadFile(driveId,file);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    public File BitmapToFile(Bitmap bitmap){

        File file=null;

        try {
            file= new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "one.png");
            file.createNewFile();


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        catch (Exception er)
        {
            Log.e(TAG, er.toString());
        }
        return file;

    }

    private synchronized void UploadFile(final DriveId driveId, final File file) {

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                                                                @Override
                                                                                public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                                                                                    if (!driveContentsResult.getStatus().isSuccess()) {
                                                                                        Log.e(TAG, "U AR A MORON! Error while trying to create new file contents");
                                                                                        return;
                                                                                    }

                                                                                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
                                                                                    Toast.makeText(MainActivity.this, "Uploading to drive....", Toast.LENGTH_LONG).show();
                                                                                    //final File theFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyMobile_Videos/a.mov");
                                                                                    final File theFile = file;

                                                                                    try {
                                                                                        FileInputStream fileInputStream = new FileInputStream(theFile);
                                                                                        byte[] buffer = new byte[1024];
                                                                                        int bytesRead;
                                                                                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                                                                            outputStream.write(buffer, 0, bytesRead);
                                                                                        }
                                                                                    } catch (IOException e1) {
                                                                                        Log.i(TAG, "Unable to write file contents.");
                                                                                    }

                                                                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(theFile.getName()).setMimeType("video/mov").setStarred(false).build();
                                                                                    DriveFolder folder = driveId.asDriveFolder();
                                                                                    folder.createFile(mGoogleApiClient, changeSet, driveContentsResult.getDriveContents()).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                                                                                                                                                                 @Override
                                                                                                                                                                                                 public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                                                                                                                                                                                     if (!driveFileResult.getStatus().isSuccess()) {
                                                                                                                                                                                                         Log.e(TAG, "Error while trying to create the file");
                                                                                                                                                                                                         return;
                                                                                                                                                                                                     }
                                                                                                                                                                                                     Toast.makeText(MainActivity.this, "Created a file: " + driveFileResult.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

                                                                                                                                                                                                     String Folder_Id = driveId.getResourceId();
                                                                                                                                                                                                     System.out.println("The folder id: " + Folder_Id);

                                                                                                                                                                                                     //This is to get the file id from the listener
                                                                                                                                                                                                     DriveId File_Uncompleted_Id = driveFileResult.getDriveFile().getDriveId();
                                                                                                                                                                                                     DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, File_Uncompleted_Id);
                                                                                                                                                                                                     file.addChangeListener(mGoogleApiClient, changeListener);
                                                                                                                                                                                                 }

                                                                                                                                                                                                 //A listener to handle file change events.
                                                                                                                                                                                                 final private ChangeListener changeListener = new ChangeListener() {
                                                                                                                                                                                                     @Override
                                                                                                                                                                                                     public void onChange(ChangeEvent event) {
                                                                                                                                                                                                         String File_Completed_Id = event.getDriveId().getResourceId();
                                                                                                                                                                                                         System.out.println("The uploaded file id: " + File_Completed_Id);
                                                                                                                                                                                                         System.out.println("File URL: https://drive.google.com/open?id=" + File_Completed_Id);

                                                                                                                                                                                                     }
                                                                                                                                                                                                 };
                                                                                                                                                                                             }
                                                                                    );
                                                                                }

                                                                            }
        );
    }
}

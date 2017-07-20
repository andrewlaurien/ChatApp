package com.andrewlaurien.chatApp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.andrewlaurien.chatApp.R;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;


public class createClan extends AppCompatActivity {


    Context mcontext;

    ImageView imgLogo;
    EditText editClanName;
    EditText editMotto;
    EditText editInterest;
    Button btnCancel;
    Button btnSubmit;


    String clanID;
    String clanname;
    String clanmotto;
    String claninterest;

    final int SELECT_PICTURE = 00001;
    final int SELECT_GALLERY = 00002;
    final int SELECT_CAMERA = 00003;

    private static final String IMAGE_DIRECTORY = "/demonuts";

    private String selectedImagePath;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mcontext = this;

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        editClanName = (EditText) findViewById(R.id.editClanName);
        editMotto = (EditText) findViewById(R.id.editMotto);
        editInterest = (EditText) findViewById(R.id.editInterest);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clanname = editClanName.getText().toString();
                clanmotto = editMotto.getText().toString();
                claninterest = editInterest.getText().toString();
                if (clanname.isEmpty()) {
                    Toast.makeText(mcontext, "Please provide a clan name.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Create a storage reference from our app
                StorageReference storageRef = MainActivity.fbStorage.getReference();

                // Create a reference to "mountains.jpg"
                StorageReference mountainsRef = storageRef.child("mountains.jpg");

                // Create a reference to 'images/mountains.jpg'
                StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

                // While the file names are the same, the references point to different files
                mountainsRef.getName().equals(mountainImagesRef.getName());    // true
                mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false


                Log.d("ChatApp", selectedImagePath);
                Uri file = Uri.fromFile(new File(selectedImagePath));
                Log.d("chatApp", selectedImagePath);





//                InputStream stream = new FileInputStream(new File(selectedImagePath));
//
//                UploadTask uploadTask = mountainsRef.putFile(file);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        exception.printStackTrace();
//
//                        Toast.makeText(mcontext, "Faile to upload Image", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                        clanID = clanname + "" + Long.toString(commonFunc.getTimeStamp());
//                        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        Clan newclan = new Clan(clanID, clanname, userid, claninterest,
//                                "Cebu", MainActivity.mylocation.getLatitude(), MainActivity.mylocation.getLongitude(), clanmotto, commonFunc.getTimeStamp());
//                        FirebaseDatabase.getInstance().getReference("Clans").child(clanID).child("clandetails").setValue(newclan);
//
//                        FirebaseDatabase.getInstance().getReference("Members").child(clanID).child(userid).setValue(MainActivity.mUser);
//
//
//                        Toast.makeText(mcontext, "Clan successfully created", Toast.LENGTH_SHORT).show();
//                        finish();
//
//
//                    }
//                });


            }
        });


        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == SELECT_GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    selectedImagePath = saveImage(bitmap);
                    Toast.makeText(mcontext, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imgLogo.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mcontext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == SELECT_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imgLogo.setImageBitmap(thumbnail);
            selectedImagePath = saveImage(thumbnail);
            Toast.makeText(mcontext, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, SELECT_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);


        try {
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("ChatApp", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


}


//Logo
//Founder
//Clan Name
//Interest
//Date Created
//Motto
//Location


//Members

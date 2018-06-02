package hk.ust.cse.comp4521.watsup;

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hk.ust.cse.comp4521.watsup.models.Resources;

import static hk.ust.cse.comp4521.watsup.models.Activities.CALLING_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.ENROLLED_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.HOSTED_ACTIVITY;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk



public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private Uri imageCaptureUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        TextView profileName = (TextView) findViewById(R.id.profileName);
        if(Resources.userProfileImage == null) {
            Resources.userProfileImage = (ImageView) findViewById(R.id.profileImageView);
        }

        ImageView profileImage = (ImageView)findViewById(R.id.profileImageView);
        profileImage.setImageBitmap(( (BitmapDrawable) Resources.userProfileImage.getDrawable()).getBitmap());

        final AlertDialog dialog = createDialog();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        profileName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Button enrolledEvents = (Button) findViewById(R.id.enrolledEvents);
        enrolledEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EventListActivity.class);
                i.putExtra(CALLING_ACTIVITY, ENROLLED_ACTIVITY);
                startActivityForResult(i,1);
            }
        });


        Button myEvents = (Button) findViewById(R.id.myevents);
        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EventListActivity.class);
                i.putExtra(CALLING_ACTIVITY, HOSTED_ACTIVITY);
                startActivityForResult(i,1);
            }
        });


        new DownloadImageTask(Resources.userProfileImage)
                .execute(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
    }

    protected void onStart(){
        super.onStart();
        DataBaseCommunicator.getEventsList();
        DataBaseCommunicator.setEnrolled(FirebaseAuth.getInstance().getUid());
    }

    private AlertDialog createDialog(){
        verifyStoragePermissions(this);
        final String[] options = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this,android.R.layout.select_dialog_item,options);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Select an Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),"tmp_avatar"+String.valueOf(System.currentTimeMillis()) +".jpg");
                    imageCaptureUri = Uri.fromFile(file);
                    try{
                        i.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                        i.putExtra("returndata", true);
                        startActivityForResult(i,1);
                    }
                    catch (Exception e){

                    }
                    dialog.cancel();
                }
                else{
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i,"Complete Action Using"), 2);
                }
            }
        });
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 2){
                imageCaptureUri = data.getData();
            }
            try {
                handleSamplingAndRotationBitmap(ProfileActivity.this,imageCaptureUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onActivityResult: Succesfully obtained local Uri for the photo");
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("userProfilePhotos").child(imageCaptureUri.getLastPathSegment());
            mStorageRef.putFile(imageCaptureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri imageUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: Successfully stored at the Storage" + imageUrl);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(imageUrl)
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            new DownloadImageTask(Resources.userProfileImage)
                                    .execute(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                        }
                    });

                }
            });

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(selectedImage.getPath());
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}

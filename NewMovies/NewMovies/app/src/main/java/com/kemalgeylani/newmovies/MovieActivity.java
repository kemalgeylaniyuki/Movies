package com.kemalgeylani.newmovies;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kemalgeylani.newmovies.databinding.ActivityMovieBinding;

import java.io.ByteArrayOutputStream;

public class MovieActivity extends AppCompatActivity {

    private ActivityMovieBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncherIntent;
    ActivityResultLauncher<String> activityResultLauncherPermission;
    Bitmap selectedImage;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        sqLiteDatabase = this.openOrCreateDatabase("Movie",MODE_PRIVATE,null);
    }

    // fotoğraf boyutu ayarlama
    public Bitmap makeImageSmaller(Bitmap image, int maxSize){

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1){
            // Landscape Image
            width = maxSize;
            height = (int) (maxSize / bitmapRatio);
        }
        else {
            // Portrait Image
            height = maxSize;
            width = (int) (maxSize * bitmapRatio);
        }

        return image.createScaledBitmap(image,width,height,true);
    }

    public void save(View view){

        String explanationText = binding.explanationText.getText().toString();
        String nameText = binding.nameText.getText().toString();
        String urlText = binding.urlText.getText().toString();
        Bitmap smallImage = makeImageSmaller(selectedImage,300);

        // Image to Binary Numbers
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] imageArray = outputStream.toByteArray();

        try {

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS movie(id INTEGER PRIMARY KEY, nameText VARCHAR, explanationText VARCHAR, urlText VARCHAR, image BLOB)");
            String sqlString = "INSERT INTO movie(nameText,explanationText,urlText,image) VALUES (?,?,?,?)";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
            sqLiteStatement.bindString(1,nameText);
            sqLiteStatement.bindString(2,explanationText);
            sqLiteStatement.bindString(3,urlText);
            sqLiteStatement.bindBlob(4,imageArray);
            sqLiteStatement.execute();

        } catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(MovieActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Bu aktivite dahil tümünü kapat ve yeni açılan aktiviteyi çalıştır.
        startActivity(intent);

    }

    public void selectImage(View view){

        // Android 33+ -> READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            // if (READ_MEDIA_IMAGES izin verilmemiş ise)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){

                // if (İstek İzni Gerekçesini Göstermelidir ise)(Snackbar)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){

                    Snackbar.make(view,"Permission needed for take image",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request Permission
                            activityResultLauncherPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();

                }
                else {
                    // Request Permission
                    activityResultLauncherPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else {
                // Take image
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncherIntent.launch(intentToGallery);
            }

        }
        else {

            // Android 32- -> READ_EXTERNAL_STORAGE
            // if (READ_MEDIA_IMAGES izin verilmemiş ise)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                // if (İstek İzni Gerekçesini Göstermelidir ise)(Snackbar)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"Permission needed for take image",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request Permission
                            activityResultLauncherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                }
                else {
                    // Request Permission
                    activityResultLauncherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else {
                // Take image
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncherIntent.launch(intentToGallery);
            }

        }

    }

    public void registerLauncher(){

        activityResultLauncherIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        Uri imageData = intentFromResult.getData();

                        try {

                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(MovieActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                            else {
                                selectedImage = MediaStore.Images.Media.getBitmap(MovieActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        activityResultLauncherPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result){
                    // permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncherIntent.launch(intentToGallery);
                }
                else {
                    //permission denied
                    Toast.makeText(MovieActivity.this, "Permission Needed !", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
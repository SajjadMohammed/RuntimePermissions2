package com.itdawn.runtimepermissions2;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.itdawn.runtimepermissions2.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //

        mainBinding.openImage.setOnClickListener(view -> {
            // request runtime permission "open an image from gallery"
            if (ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery();
            } else {
                requestReadingPermission.launch(permission.READ_EXTERNAL_STORAGE);
            }
        });

        mainBinding.openCamera.setOnClickListener(view -> {
            // request runtime permission "open an image from gallery"
            if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                getImageFromCamera();
            } else {
                requestCameraPermission.launch(permission.CAMERA);
            }
        });

    }

    ActivityResultLauncher<String> requestReadingPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getImageFromGallery();
        }
    });

    ActivityResultLauncher<String> requestCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getImageFromCamera();
        }
    });

    private void getImageFromCamera() {
        //
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void getImageFromGallery() {
        //
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);

    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();
                mainBinding.myImage.setImageURI(imageUri);
            }
        }
    });

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            // Get the captured image data
            Intent data = result.getData();
            Bundle extras = Objects.requireNonNull(data).getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Do something with the image
            mainBinding.myImage.setImageBitmap(imageBitmap);
        }
    });
}
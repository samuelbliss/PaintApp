package com.samuelbliss.paintapp;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PaintActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    //set up initial static variables and the Drawer view
    private Drawer paintView;
    private static final String IMAGE_DIR = "/images";
    private static final int CAMERA = 2000;
    private static final int GALLERY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        // Android devices newer than SDK 23 needs to ask permission to Read/Write
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }

        //Camera Button creates intent that allows you to take a picture.
        findViewById(R.id.camera_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA);
            }
        });

        //Gallery Button creates intent that allows you to choose a photo from the gallery.
        findViewById(R.id.gallery_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });

        //Initialize paintView Drawer
        paintView = (Drawer) findViewById(R.id.paint_iv);


        //Clear button calls the Drawer clearDrawing function
        findViewById(R.id.clear_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clearDrawing();
            }
        });

        //Save button calls the Drawer saveDrawing function to get the edited image
                //Then calls the insertImage function to save in storage.
        findViewById(R.id.save_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = paintView.saveDrawing();
                MediaStore.Images.Media.insertImage(getContentResolver(), b, "" , "");
            }
        });

        //Initialize color button and set up the listener to change text and run changeColor() every click.
        final Button colorB = (Button) findViewById(R.id.color_bt);
        colorB.setText("RED");
        colorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorB.getText().toString() == "RED") {
                    colorB.setText("BLUE");
                } else if (colorB.getText().toString() == "BLUE") {
                    colorB.setText("GREEN");
                } else if (colorB.getText().toString() == "GREEN") {
                    colorB.setText("WHITE");
                } else if (colorB.getText().toString() == "WHITE") {
                    colorB.setText("BLACK");
                } else if (colorB.getText().toString() == "BLACK") {
                    colorB.setText("RED");
                }
                paintView.changeColor();
            }
        });

        //Initialize size button, setup the listener to change text and run changeSize() every click.
        final Button sizeB = (Button) findViewById(R.id.size_bt);
        sizeB.setText("SMALL");
        sizeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sizeB.getText().toString() == "SMALL"){
                    sizeB.setText("MEDIUM");
                } else if (sizeB.getText().toString() == "MEDIUM"){
                    sizeB.setText("LARGE");
                } else if (sizeB.getText().toString() == "LARGE"){
                    sizeB.setText("XLARGE");
                } else if (sizeB.getText().toString() == "XLARGE"){
                    sizeB.setText("XSMALL");
                } else if (sizeB.getText().toString() == "XSMALL"){
                    sizeB.setText("SMALL");
                }
                paintView.changeSize();
            }
        });

        //Initialize shape button, setup the listener to change text and run changeShape() every click.
        final Button shapeB = (Button) findViewById(R.id.shape_bt);
        shapeB.setText("LINE");
        shapeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shapeB.getText().toString() == "LINE"){
                    shapeB.setText("SQUARE");
                } else if (shapeB.getText().toString() == "SQUARE"){
                    shapeB.setText("LINE");
                }
                paintView.changeShape();
            }
        });

    }

    //When camera/gallery is clicked they call onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //When Camera button is clicked take a picture and send it to the Drawer
        if (requestCode == CAMERA) {
            if (data != null) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                paintView.setImageBitmap(image);
            }
        }
        //When Gallery button is clicked get a saved picture and send it to the Drawer
        else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                    paintView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Check permission for READ/WRITE
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(PaintActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //Request permission for READ/WRITE if it isn't already granted
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PaintActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(PaintActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(PaintActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
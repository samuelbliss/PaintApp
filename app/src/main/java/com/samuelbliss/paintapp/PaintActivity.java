package com.samuelbliss.paintapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PaintActivity extends AppCompatActivity {

    Button cameraBtn;
    Button galleryBtn;
    Drawer paintView;

    private static final String IMAGE_DIR = "/images";
    private static final int CAMERA = 2000;
    private static final int GALLERY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        cameraBtn = (Button) findViewById(R.id.camera_bt);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA);
            }
        });

        galleryBtn = (Button) findViewById(R.id.gallery_bt);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });

        paintView = (Drawer) findViewById(R.id.paint_iv);



        findViewById(R.id.clear_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clearDrawing();
            }
        });

        findViewById(R.id.save_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    paintView.saveDrawing();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA) {
            if (data != null) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                paintView.setImageBitmap(image);
            }
        } else if (requestCode == GALLERY) {
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
}
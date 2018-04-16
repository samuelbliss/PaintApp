package com.samuelbliss.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Drawer extends android.support.v7.widget.AppCompatImageView{

    public int width;
    public  int height;
    private float dpiPixels;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Path squarePath;
    private Paint   mBitmapPaint;
    private Paint mPaint;
    private Paint squarePaint;
    private enum Shape { LINE, SQUARE}
    private Shape currentShape;
    private DisplayMetrics dm = getResources().getDisplayMetrics();

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentShape = Shape.LINE;
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        mPath = new Path();
        squarePath = new Path();
        squarePaint = new Paint();
        squarePaint.setColor(Color.RED);
        squarePaint.setStyle(Paint.Style.FILL);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
        setDrawingCacheEnabled( true );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(squarePath, squarePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        if (currentShape == Shape.LINE) {
            mPath.reset();
            mPath.moveTo(x, y);
        } else if (currentShape == Shape.SQUARE) {
            squarePath.reset();
            squarePath.moveTo(x, y);
        }
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if (currentShape == Shape.LINE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            }
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        if (currentShape == Shape.LINE) {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        } else if (currentShape == Shape.SQUARE) {
           squarePath.addRect(mX, mY, mX + dpiPixels, mY + dpiPixels, Path.Direction.CW);
            // commit the path to our offscreen
            mCanvas.drawPath(squarePath, squarePaint);
            // kill this so we don't double draw
            squarePath.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void clearDrawing() {
    setDrawingCacheEnabled(false);

    onSizeChanged(width, height, width, height);
    invalidate();

    setDrawingCacheEnabled(true);
}

    //Currently Not Working ... getDrawingCache returns null for some reason.
    public void saveDrawing() throws IOException {
        Bitmap b = getDrawingCache();
        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        outStream = new FileOutputStream(outFile);
        b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        outStream.flush();
        outStream.close();
    }

    public void changeColor() {
        if (mPaint.getColor() == Color.RED) {
            mPaint.setColor(Color.BLUE);
            squarePaint.setColor(Color.BLUE);
        } else if (mPaint.getColor() == Color.BLUE) {
            mPaint.setColor(Color.GREEN);
            squarePaint.setColor(Color.GREEN);
        }
        else if (mPaint.getColor() == Color.GREEN) {
            mPaint.setColor(Color.WHITE);
            squarePaint.setColor(Color.WHITE);
        }else if (mPaint.getColor() == Color.WHITE) {
            mPaint.setColor(Color.BLACK);
            squarePaint.setColor(Color.BLACK);
        } else if (mPaint.getColor() == Color.BLACK) {
            mPaint.setColor(Color.RED);
            squarePaint.setColor(Color.RED);
        }
    }

    public void changeSize() {
        if (mPaint.getStrokeWidth() == 10) {
            mPaint.setStrokeWidth(15);
            dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        } else if (mPaint.getStrokeWidth() == 15) {
            mPaint.setStrokeWidth(20);
            dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, dm);
        } else if (mPaint.getStrokeWidth() == 20) {
            mPaint.setStrokeWidth(25);
            dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, dm);
        } else if (mPaint.getStrokeWidth() == 25) {
            mPaint.setStrokeWidth(5);
            dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
        } else if (mPaint.getStrokeWidth() == 5) {
            mPaint.setStrokeWidth(10);
            dpiPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, dm);
        }
    }

    public void changeShape() {
        if (currentShape == Shape.LINE) {
            currentShape = Shape.SQUARE;
        } else if (currentShape == Shape.SQUARE) {
            currentShape = Shape.LINE;
        }
    }

 @Override
 public void setImageBitmap(Bitmap bm)
 {
     Drawable drawable = new BitmapDrawable(bm);
     this.setBackgroundDrawable(drawable);
     clearDrawing();
 }
}


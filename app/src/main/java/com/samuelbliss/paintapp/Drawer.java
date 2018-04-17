package com.samuelbliss.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;

public class Drawer extends android.support.v7.widget.AppCompatImageView{

    //Declare class variables
    public int  width;
    public  int height;
    private float   dpiPixels;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Path    squarePath;
    private Paint   mBitmapPaint;
    private Paint   mPaint;
    private Paint   squarePaint;
    private enum Shape {LINE, SQUARE} //enum of possible shapes
    private Shape currentShape; //Shape object to store current chosen shape
    private DisplayMetrics dm = getResources().getDisplayMetrics(); //changes pixels to appropriate dpi


    //When Drawer is created, setup all initial variables.
    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundColor(Color.WHITE);
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
        setDrawingCacheEnabled( true ); //This is make sure the drawing can be saved.
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

        //Draw all canvas paths
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(squarePath, squarePaint);
    }

    //Variables to store x/y coordinates and the touch tolerance for drawing a line
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    //Set coordinates of initial press
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

    //When finger drags across screen draw a quadTo line if Shape.LINE is chosen
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

    //When finger is lifted off screen save the line drawn if Shape.LINE is chosen or
        //place square shape if Shape.SQUARE is chosen
    private void touch_up() {
        if (currentShape == Shape.LINE) {
            mPath.lineTo(mX, mY);
            // commit the path
            mCanvas.drawPath(mPath,  mPaint);
            // clear current path tracking
            mPath.reset();
        } else if (currentShape == Shape.SQUARE) {
            float drawPixels = dpiPixels / 2;
            squarePath.addRect(mX - drawPixels, mY -drawPixels,
                    mX + drawPixels, mY + drawPixels, Path.Direction.CW);
            // commit the path
            mCanvas.drawPath(squarePath, squarePaint);
            // clear current path tracking
            squarePath.reset();
        }
    }

    //Run events based on touching the screen
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

    //To clear the drawing, erase cache, force invalidate, and re-enable cache
    public void clearDrawing() {
    setDrawingCacheEnabled(false);

    onSizeChanged(width, height, width, height);
    invalidate();

    setDrawingCacheEnabled(true);
    }

    //send image of current drawing in a Bitmap
    public Bitmap saveDrawing() {
        Bitmap b = getDrawingCache();
        return b;
    }

    //changeColor is called to change the paint color. It rotates through five pre-set colors.
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

    //changeSize is called to change the paint size. It rotates through five pre-set sizes
        //strokeWidth is for drawing a line, dpiPixels is for resizing the square
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

    //changeShape is called to change the variable that determines the shape being drawn
    public void changeShape() {
        if (currentShape == Shape.LINE) {
            currentShape = Shape.SQUARE;
        } else if (currentShape == Shape.SQUARE) {
            currentShape = Shape.LINE;
        }
    }


    //setImageBitmap receives the Bitmap from either the camera or gallery and sets it as the base
            //of the drawing, then clears the canvas.
    @Override
    public void setImageBitmap(Bitmap bm)
    {
     Drawable drawable = new BitmapDrawable(bm);
     this.setBackgroundDrawable(drawable);
     clearDrawing();
    }
}


package com.prateek.drawcollab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.prateek.drawcollab.MainActivity.database;

public class SimpleDrawingView extends View {
    // setup initial color
    private final int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;
    DatabaseReference myPaintColor;
    DatabaseReference myCoordinates;
    DatabaseReference myStartCoordinates;
    String TAG = "TAG";

    Path path = new Path();

    String[] XY;
    String[] XY0;

    public SimpleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
//        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        myPaintColor = database.getReference("color");
        myCoordinates = database.getReference("coordinateXY");
        myStartCoordinates = database.getReference("coordinate0");
        if (XY != null && XY0 != null){
//            path.moveTo(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
            path.lineTo(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
            canvas.drawPath(path, drawPaint);
        }
//        if (XY0 != null){
//            path.moveTo(Float.parseFloat(XY0[0])+15, Float.parseFloat(XY0[1])+15);
//            canvas.drawPath(path, drawPaint);
//        }
//        myPaintColor.setValue(Color.RED);
// CUT PASTE       canvas.drawCircle(Float.parseFloat(coordinate[0]),Float.parseFloat(coordinate[1]), 5, drawPaint);
//
//        canvas.drawCircle(50, 50, 20, drawPaint);
//        drawPaint.setColor(Color.GREEN);
//        canvas.drawCircle(50, 150, 20, drawPaint);
//        drawPaint.setColor(Color.BLUE);
//        canvas.drawCircle(50, 250, 20, drawPaint);

        myPaintColor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Long value = dataSnapshot.getValue(Long.class);
                Log.d(TAG, "Value is: " + value);
                drawPaint.setColor(Integer.parseInt(String.valueOf(value)));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myCoordinates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                if (value != null){
                    XY = value.split(",");
                    postInvalidate();
//                    canvas.drawCircle(Float.parseFloat(coordinate[0]),Float.parseFloat(coordinate[1]), 5, drawPaint);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myStartCoordinates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Start Value is: " + value);
                if (value != null){
                    XY0 = value.split(",");
//                    postInvalidate();
//                    canvas.drawCircle(Float.parseFloat(coordinate[0]),Float.parseFloat(coordinate[1]), 5, drawPaint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        myStartCoordinates.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//                if (value != null){
//                    XY0 = value.split(",");
//                    postInvalidate();
////                    canvas.drawCircle(Float.parseFloat(coordinate[0]),Float.parseFloat(coordinate[1]), 5, drawPaint);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        myCoordinates.setValue(Math.round(touchX)+","+Math.round(touchY));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
//                path.moveTo(Float.parseFloat(XY[0]), Float.parseFloat(XY[1]));
                myStartCoordinates.setValue(Math.round(touchX)+","+Math.round(touchY));
                return true;
            case MotionEvent.ACTION_MOVE:
                myCoordinates.setValue(Math.round(touchX)+","+Math.round(touchY));
//                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }

//        circlePoints.add(new Point(Math.round(touchX), Math.round(touchY)));
        // indicate view should be redrawn
//        postInvalidate();
        return true;
    }
}

package com.example.siiapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class firmaDigital extends View {

    /// ATRIBUTOS ///

    Paint paint, pantallita;
    Path path;
    Canvas pantalla;
    Bitmap guardarCanvas;

    /// METODOS ///

    public firmaDigital(Context context, AttributeSet attSet) {
        super(context, attSet);

        paint = new Paint();
        path = new Path();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        pantallita = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        guardarCanvas = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        pantalla = new Canvas(guardarCanvas);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(guardarCanvas, 0, 0, pantallita);
        canvas.drawPath(path, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xPos, yPos);
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(xPos, yPos);
                break;

            case MotionEvent.ACTION_UP:
                path.lineTo(xPos, yPos);
                pantalla.drawPath(path, paint);
                path.reset();
                break;

            default:
                return false;

        }

        invalidate();
        return true;

    }

    public void borrarFirma() {
        pantalla.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();

    }

}

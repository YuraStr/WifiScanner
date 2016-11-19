package com.example.yuras.thirdapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;

public class DrawingActivity extends Activity {
    Button drawBtn;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawBtn = (Button) findViewById(R.id.btn);
        imageView = (ImageView) findViewById(R.id.iv);

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawARGB(80, 102, 204, 255);

                // настройка кисти
                // красный цвет
                Paint p = new Paint();
                Rect rect = new Rect();
                p.setColor(Color.RED);
                // толщина линии = 10
                p.setStrokeWidth(10);

                // рисуем точку (50,50)
                canvas.drawPoint(50, 50, p);

                // рисуем линию от (100,100) до (500,50)
                canvas.drawLine(100,100,500,50,p);

                // рисуем круг с центром в (100,200), радиус = 50
                canvas.drawCircle(100, 200, 50, p);

                // рисуем прямоугольник
                // левая верхняя точка (200,150), нижняя правая (400,200)
                canvas.drawRect(200, 150, 400, 200, p);

                // настройка объекта Rect
                // левая верхняя точка (250,300), нижняя правая (350,500)
                rect.set(250, 300, 350, 500);
                // рисуем прямоугольник из объекта rect
                canvas.drawRect(rect, p);

                imageView.setImageBitmap(bitmap);
            }
        });
    }



    /*class DrawView extends View {

        Paint paint;
        Bitmap bitmap;

        public DrawView(Context context) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);

            bitmap.setPixel(20, 20, Color.RED);
            bitmap.setPixel(70, 50, Color.RED);
            bitmap.setPixel(30, 80, Color.RED);

            int[] colors = new int[10*15];
            Arrays.fill(colors, 0, 10*15, Color.GREEN);
            bitmap.setPixels(colors, 0, 10, 40, 40, 10, 15);

            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            canvas.drawCircle(80, 80, 10, p);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawARGB(80, 102, 204, 255);
            canvas.drawBitmap(bitmap, 50, 50, paint);
        }
    }*/

    public void gotoSensorsDr(View view) {
        finish();
    }
}

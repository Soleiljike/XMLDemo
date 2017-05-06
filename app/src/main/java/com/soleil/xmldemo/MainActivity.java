package com.soleil.xmldemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private int change = 0;
    private ImageView image;
    private ImageView image1;
    private Button btn;
    private Drawable[] drawables;
    private TextView textView;
    private LinearLayout linearLayout;
    int[] ids = new int[]{
            R.drawable.and, R.drawable.ico1, R.drawable.ico2, R.drawable.ico3, R.drawable.ico5
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.and);
//        bitmapDrawable.setDither(true);
//        bitmapDrawable.setFilterBitmap(true);
//        bitmapDrawable.setAntiAlias(true);
//        image1 = (ImageView) findViewById(R.id.image1);
//        image1.setImageDrawable(bitmapDrawable);


//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        Drawable[] drawables = new Drawable[3];
//        drawables[0] = new BitmapDrawable(bitmap);
//        drawables[1] = new BitmapDrawable(bitmap);
//        drawables[2] = new BitmapDrawable(bitmap);
//        LayerDrawable layer = new LayerDrawable(drawables);
//        layer.setLayerInset(0,20,20,0,0);
//        layer.setLayerInset(1,40,40,0,0);
//        layer.setLayerInset(2,60,60,0,0);
//        image = (ImageView) findViewById(R.id.imageView);
//        image.setImageDrawable(layer);
        image = (ImageView) findViewById(R.id.imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果将其设为true的话，在decode时将会返回null,
        // 通过此设置可以去查询一个bitmap的属性，比如bitmap的长与宽，而不占用内存大小。
        BitmapFactory.decodeResource(getResources(), R.drawable.and, options);
        options.inSampleSize = computeSampleSize(options, -1, 500 * 500);
        options.inJustDecodeBounds = false;
        LevelListDrawable levelListDrawable = new LevelListDrawable();
        try {
            for (int i = 0; i < ids.length; i++) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), ids[i], options);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
                levelListDrawable.addLevel(i, i + 1, bitmapDrawable);
            }
            image.setImageDrawable(levelListDrawable);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }

        image.setImageLevel(1);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = image.getDrawable().getLevel();
                if (i >= 5) {
                    i = 0;
                }
                image.getDrawable().setLevel(++i);
            }
        });

        textView = (TextView) findViewById(R.id.button);
        final TransitionDrawable transitionDrawable = (TransitionDrawable) textView.getBackground();
        transitionDrawable.startTransition(10000);

        image1 = (ImageView) findViewById(R.id.image1);
        drawables = new Drawable[ids.length];
        try {
            for (int i = 0; i < ids.length; i++) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), ids[i], options);
                drawables[i] = new BitmapDrawable(bmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //开启线程，改变transitation
        new Thread(new MyRunnable()).start();

        InsetDrawable insetDrawable = new InsetDrawable(getResources().getDrawable(R.drawable.ico3), 20, 20, 20, 20);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
//        linearLayout.setBackgroundDrawable(insetDrawable); //api16以下使用
        linearLayout.setBackground(insetDrawable); //Api16以上使用


//        View testScale = findViewById(R.id.btn);
//        ScaleDrawable scaleDrawable = (ScaleDrawable) testScale.getBackground();
//        scaleDrawable.setLevel(1);

        ScaleDrawable scaleDrawable1 = new ScaleDrawable(getResources().getDrawable(R.drawable.and), 10, 0.5f, 0.5f);

        scaleDrawable1.setLevel(1);
        btn.setBackground(scaleDrawable1);


        ImageView testClip = (ImageView) findViewById(R.id.test_clip);
        ClipDrawable clipDrawable = (ClipDrawable) testClip.getDrawable();
        clipDrawable.setLevel(5000);
    }

    //处理transition的改变
    private Handler handler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            int duration = msg.arg1;
            TransitionDrawable transitionDrawable = null;
            transitionDrawable = new TransitionDrawable(new Drawable[]{
                    drawables[change % ids.length], drawables[(change + 1) % ids.length]
            });
            change++;
            image1.setImageDrawable(transitionDrawable);
            //返回true不再调用handler
            return false;
        }
    });


    private int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeIntialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private int computeIntialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        Log.d(TAG, "outWidth is " + w);
        Log.d(TAG, "outHeight is " + h);

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                int duration = 3000;
                Message message = handler.obtainMessage();
                message.arg1 = duration;
                handler.sendMessage(message);
                try {
                    Thread.sleep(duration);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




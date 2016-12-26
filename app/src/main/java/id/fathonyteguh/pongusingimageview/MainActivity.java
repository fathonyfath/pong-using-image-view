package id.fathonyteguh.pongusingimageview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private ImageView image;

    private Thread thread;

    private BallRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.container);
        image  = (ImageView) findViewById(R.id.image);

        runnable = new BallRunnable(image, 15f);
        thread = new Thread(runnable);
        
        image.post(new Runnable() {
            @Override
            public void run() {
                onImageReady();
            }
        });
        
        layout.post(new Runnable() {
            @Override
            public void run() {
                onContainerReady();
            }
        });
    }

    private void onContainerReady() {
        runnable.setBorders(layout.getMeasuredWidth(), layout.getMeasuredHeight());
        if(runnable.isRunnableReady()) thread.start();
    }

    private void onImageReady() {
        runnable.setImageBorder(image.getMeasuredWidth(), image.getMeasuredHeight());
        if(runnable.isRunnableReady()) thread.start();
    }

    private static class BallRunnable implements Runnable {

        private ImageView image;
        private float borderX, borderY;
        private float imageBorderX, imageBorderY;

        private boolean isBoundReady;
        private boolean isImageBorderReady;

        private boolean isRunning;

        private float speedX, speedY;
        private float speed;

        private Handler handler;

        public BallRunnable(ImageView image, float speed) {
            this.image = image;
            isRunning = true;

            speedX = 1f;
            speedY = 1f;

            this.speed = speed;
            handler = new Handler(Looper.getMainLooper());
        }

        public void setBorders(float borderX, float borderY) {
            this.borderX = borderX;
            this.borderY = borderY;
            isBoundReady = true;
        }

        public void setImageBorder(float imageBorderX, float imageBorderY) {
            this.imageBorderX = imageBorderX;
            this.imageBorderY = imageBorderY;
            isImageBorderReady = true;
        }

        public boolean isRunnableReady() {
            return  isBoundReady && isImageBorderReady;
        }

        public void stop() {
            isRunning = false;
        }

        @Override
        public void run() {
            while(isRunning) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (image.getX() < 0f) {
                            image.setX(0f);
                            speedX *= -1f;
                        }

                        if(image.getX() > borderX - imageBorderX) {
                            image.setX(borderX - imageBorderX);
                            speedX *= -1f;
                        }

                        if (image.getY() < 0) {
                            image.setY(0f);
                            speedY *= -1f;
                        }

                        if(image.getY() > borderY - imageBorderY) {
                            image.setY(borderY - imageBorderY);
                            speedY *= -1f;
                        }

                        image.setX(image.getX() + speed * speedX);
                        image.setY(image.getY() + speed * speedY);
                    }
                });
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

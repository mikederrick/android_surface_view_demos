package demos.surfaceview.collisions.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import demos.surfaceview.collisions.shapes.FmCircle;
import demos.surfaceview.collisions.threads.FmBubblesDrawThread;

/**
 * Created by mderrick on 3/19/16.
 */
public class CircleSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static float DAMPENING = 0.5f;
    public static float GRAVITY = 0.007f;
    private SurfaceHolder surfaceHolder;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private FmBubblesDrawThread drawThread;
    private Queue<FmCircle> circles;
    private int topLocation;
    private int leftLocation;
    private int rightLocation;
    private int bottomLocation;

    public CircleSurfaceView(Context context) {
        super(context);
        topLocation = 0;
        leftLocation = 0;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        circles = new ConcurrentLinkedQueue();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if((event.getX() < rightLocation*0.25f && event.getY() < bottomLocation*0.25f) && GRAVITY != 0) {
                GRAVITY = 0;
                DAMPENING = 1;
                Random rand = new Random();
                for(FmCircle circle : circles) {
                    circle.velocityX = (rand.nextInt(3)) - 1 + 2;
                    circle.velocityY = (float) Math.random()*5;
                }
            } else if((event.getX() < rightLocation*0.25f && event.getY() < bottomLocation*0.25f) && GRAVITY == 0) {
                GRAVITY = 0.007f;
                DAMPENING = 0.5f;
            } else if (surfaceHolder != null && surfaceHolder.getSurface().isValid()) {
                circles.add(new FmCircle(event.getX(), event.getY(), (float) Math.random()*60, 2, 2));
            }
        }
        return false;
    }

    public void updatePositions() {
        for(FmCircle circle : circles) {
            handleSideCollisions(circle);
            circle.x+=circle.velocityX;
            circle.y+=circle.velocityY;
        }
        calcualteCollisions();
    }

    private void handleSideCollisions(FmCircle circle) {
        float x = circle.x + circle.radius;
        float y = circle.y + circle.radius;
        if(x >= rightLocation) {
            circle.velocityX *= -1;
            circle.velocityX *= DAMPENING;
            circle.x = rightLocation - circle.radius;
        }
        if((circle. x - circle.radius) <= leftLocation) {
            circle.velocityX *= -1;
            circle.velocityX *= DAMPENING;
            circle.x = leftLocation + circle.radius;
        }

        if (y >= bottomLocation) {
            circle.velocityY *= -1;
            circle.velocityY *= DAMPENING;
            circle.y = bottomLocation - circle.radius;
        } else {
            circle.velocityY += circle.radius * GRAVITY;
        }

        if ((circle.y - circle.radius) <= topLocation) {
            circle.velocityY*= -1;
            circle.y = topLocation + circle.radius;
        }
    }

    private void calcualteCollisions() {
        for(FmCircle circle : circles) {

            for(FmCircle otherCircle : circles) {
                if(otherCircle == circle) {
                    continue;
                } else {
                    if(circle.x + circle.radius + otherCircle.radius > otherCircle.x
                            && circle.x < otherCircle.x + circle.radius + otherCircle.radius
                            && circle.y + circle.radius + otherCircle.radius > circle.y
                            && circle.y < otherCircle.y + circle.radius + otherCircle.radius) {

                        double distance = Math.sqrt(((otherCircle.x - circle.x) * (otherCircle.x - circle.x)) +
                                                    ((otherCircle.y - circle.y) * (otherCircle.y - circle.y)));
                        if (distance < 0) { distance = distance * -1; }
                        if(distance <= (circle.radius + otherCircle.radius)) {

                            float velocityX1 = otherCircle.velocityX;
                            float velocityY1 = otherCircle.velocityY;
                            float velocityX2 = circle.velocityX;
                            float velocityY2 = circle.velocityY;

                            circle.velocityX = velocityX1;
                            circle.velocityY = velocityY1;
                            otherCircle.velocityX = velocityX2;
                            otherCircle.velocityY = velocityY2;

                            circle.x += circle.velocityX;
                            circle.y += circle.velocityY;
                            otherCircle.x += otherCircle.velocityX;
                            otherCircle.y += otherCircle.velocityY;
                        }
                    }
                }
            }
        }
    }

    public void update() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if(canvas != null) {
                canvas.drawColor(Color.BLACK);
                for(FmCircle circle : circles) {
                    paint.setColor(circle.colorRes);
                    canvas.drawCircle(circle.x, circle.y, circle.radius, paint);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        bottomLocation = canvas.getHeight();
        rightLocation = canvas.getWidth();
        holder.unlockCanvasAndPost(canvas);
        drawThread = new FmBubblesDrawThread(surfaceHolder, this);
        drawThread.setRunnable(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunnable(false);
        while(retry) {
            try {
                drawThread.join();
                retry = false;
            } catch(InterruptedException ie) {
            }
            break;
        }
        drawThread = null;
    }
}

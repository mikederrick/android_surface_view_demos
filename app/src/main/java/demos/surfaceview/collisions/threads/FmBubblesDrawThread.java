package demos.surfaceview.collisions.threads;

import android.view.SurfaceHolder;

import demos.surfaceview.collisions.views.CircleSurfaceView;

/**
 * Created by mderrick on 3/19/16.
 */
public class FmBubblesDrawThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private CircleSurfaceView circleSurfaceView;
    private boolean run;

    public FmBubblesDrawThread(SurfaceHolder surfaceHolder, CircleSurfaceView circleSurfaceView) {
        this.surfaceHolder = surfaceHolder;
        this.circleSurfaceView = circleSurfaceView;
    }

    @Override
    public void run() {
        super.run();
        while (run) {
            synchronized (surfaceHolder) {
                try {
                    circleSurfaceView.updatePositions();
                    circleSurfaceView.update();
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRunnable(boolean run)
    {
        this.run = run;
    }
}

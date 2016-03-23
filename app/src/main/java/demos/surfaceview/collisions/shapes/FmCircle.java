package demos.surfaceview.collisions.shapes;

import android.graphics.Color;

/**
 * Created by mderrick on 3/17/16.
 */
public class FmCircle {

    public float x;
    public float y;
    public float radius;
    public float velocityX;
    public float velocityY;
    public int colorRes = Color.GREEN;
    public int mass;

    public FmCircle(float x, float y, float radius, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}

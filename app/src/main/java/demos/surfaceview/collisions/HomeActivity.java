package demos.surfaceview.collisions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import demos.surfaceview.collisions.views.CircleSurfaceView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CircleSurfaceView(this));
    }
}

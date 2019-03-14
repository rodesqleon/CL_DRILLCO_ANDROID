package com.example.spand.drillco;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.ImageView;

public class Rechazado extends AppCompatActivity {
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rechazado);

        imageView2 = (ImageView) findViewById(R.id.imageView2);
    }
}
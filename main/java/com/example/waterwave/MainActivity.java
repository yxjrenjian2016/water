package com.example.waterwave;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.waterwave.view.Waterwave;

/*import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;*/


public class MainActivity extends Activity {

    private Waterwave mWaterwave;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start:
                    mWaterwave.start();
                    break;
                case R.id.stop:
                    mWaterwave.stop();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterwave = (Waterwave) findViewById(R.id.waterwave);
        findViewById(R.id.start).setOnClickListener(mClickListener);
        findViewById(R.id.stop).setOnClickListener(mClickListener);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


}

package org.esteban.piano;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void retroceder(View v){finish(); System.exit(0);}

}

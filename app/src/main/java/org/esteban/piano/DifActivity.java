package org.esteban.piano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.logging.Level;

public class DifActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dif);
    }

    public void choose_dif1(View v)
    {
        try {
            Intent dif1 = new Intent(this, LevelActivity.class);
            dif1.putExtra("difficulty", 1);
            startActivity(dif1);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void choose_dif2(View v) {
        try{
        Intent dif2 = new Intent(this, LevelActivity.class);
        dif2.putExtra("difficulty", 2);
        startActivity(dif2);
    }
        catch (Exception e) {
        Toast.makeText(getBaseContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
    }
    public void choose_dif3(View v) {
        try{
        Intent dif3 = new Intent(this, LevelActivity.class);
        dif3.putExtra("difficulty", 3);
        startActivity(dif3);
    }
        catch (Exception e) {
        Toast.makeText(getBaseContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
    }

    public void retroceder(View v){startActivity(new Intent(this,LoginActivity.class));}
}

package org.esteban.piano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            File pastaNiveis = new File(Environment.getExternalStorageDirectory(),"niveis");
            if(!pastaNiveis.exists())
            {
                pastaNiveis.mkdir();
                File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/niveis/difficulty_1.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("1;Twinkle Little Star;8x8x15x15x17x17x15x13x13x12x12x10x8x15x15x13x13x12x12x10x15x15x13x13x12x12x10x8x8x15x17x15x13x13x12x12x10x8;0;\n2;Atirei o pau ao gato;8x6x5x3x5x6x8x8x8x10x8x6x6x6x8x6x5x5x5x1x1x10x10x10x12x10x8x8x8x5x6x8x5x6x8x6x5x3x1x13x13;0;\n3;Happy Birthday;8x8x10x8x13x12x8x8x10x8x15x8x8x20x17x13x12x10x18x18x17x13x15x13x13x15x13x18x17x13x13x15x13x20x18x13x13;0;");
                myOutWriter.close();
                fOut.close();
                myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/niveis/difficulty_2.txt");
                myFile.createNewFile();
                fOut = new FileOutputStream(myFile);
                myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("1;Frére Jaques;13x15x17x13x13x15x17x13x17x18x20x17x18x20x20x22x20x18x17x13x20x22x20x18x17x13x15x8x13x15x8x13x13x15x17x13x13x15x17x13x17x18x20;0;\n2;Für Elise;17x16x17x16x17x12x15x13x10x5x10x12x5x12x13x17x16x17x16x17x12x15x13x10x5x10x12x5x13x12x10x12x13x15x17x8x18x17x15x6x17x15x13x5x15x13x12;0;\n3;Titanic;6x6x6x5x6x6x5x6x8x10x8x6x6x6x5x6x6x1x6x8x1x13x11x10x8x10x11x10x8x6x5x6x5x5x6x8x10x8x6;0;");
                myOutWriter.close();
                fOut.close();
                myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/niveis/difficulty_3.txt");
                myFile.createNewFile();
                fOut = new FileOutputStream(myFile);
                myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("1;Hino da Alegria;5x5x6x8x8x6x5x3x1x1x3x5x5x3x3x5x5x6x8x8x6x5x3x1x1x3x5x3x1x1x3x3x5x1x3x5x6x5x1x3x5x6x5x3x1x3x8x5x5x6x8x8x6x5x3x1x1x3x5x3x1x1;0;\n2;Silent Night;5x6x5x3x5x6x5x3x9x9x7x8x8x5x6x6x8x7x6x5x6x5x3x6x6x8x7x6x5x6x3x9x9x11x9x7x8x10x8x5x3x5x4x2x5x9x9x11x9x7x8x10x8x5x3x5x4x2;0;\n3;Jingle Bells Rocks;13x13x13x12x12x12x10x12x10x5x10x12x10x5x8x10x12x10x6x3x5x6x8x10x8x3x5x6x8x10x9x10x9x10x10x4x4x13x13x13x12x12x12x10x12x10x5x10x12x10x5x8x10x12x10x6x3x5x6x8x10x8x3x5x6x8x10x9x10x9x10x10x4x4;0");
                myOutWriter.close();
                fOut.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_login);

    }

    public void login(View v)
    {
        startActivity(new Intent(this,DifActivity.class));
    }

    public void setting(View v)
    {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    public void details(View v)
    {
        startActivity(new Intent(this,DetailsActivity.class));
    }

    public void exit(View v){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

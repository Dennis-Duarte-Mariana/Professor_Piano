package org.esteban.piano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends Activity {

    public static int dif;
    public static int dif_current;
    public String path = Environment.getExternalStorageDirectory() + "/niveis";
    public static List<NivelClass> nivelList;
    public static List<NivelClass> nivelList_2;
    public static List<NivelClass> nivelList_3;

    TextView m1,m2,m3,p1,p2,p3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        m1=(TextView)findViewById(R.id.textViewM1);
        m2=(TextView)findViewById(R.id.textViewM2);
        m3=(TextView)findViewById(R.id.textViewM3);
        p1=(TextView)findViewById(R.id.textViewP1);
        p2=(TextView)findViewById(R.id.textViewP2);
        p3=(TextView)findViewById(R.id.textViewP3);


        try {
            dif = getIntent().getIntExtra("difficulty", 1);
            dif_current = dif;

            //if (nivelList != null)
                nivelList=FromFiletoView(path + "/difficulty_1.txt");
            //if (nivelList_2 != null)
                nivelList_2=FromFiletoView(path + "/difficulty_2.txt");
            //if (nivelList_3 != null )
                nivelList_3=FromFiletoView(path + "/difficulty_3.txt");


            if(dif==1){
                NivelClass obj=nivelList.get(0);
                m1.setText(obj.getNome());
                p1.setText(obj.getPontuacao().toString());

                obj=nivelList.get(1);
                m2.setText(obj.getNome().toString());
                p2.setText(obj.getPontuacao().toString());

                obj=nivelList.get(2);
                m3.setText(obj.getNome().toString());
                p3.setText(obj.getPontuacao().toString());
            }
            if(dif==2)
            {
                NivelClass obj=nivelList_2.get(0);
                m1.setText(obj.getNome());
                p1.setText(obj.getPontuacao().toString());

                obj=nivelList_2.get(1);
                m2.setText(obj.getNome().toString());
                p2.setText(obj.getPontuacao().toString());

                obj=nivelList_2.get(2);
                m3.setText(obj.getNome().toString());
                p3.setText(obj.getPontuacao().toString());
            }
            if(dif==3)
            {
                NivelClass obj=nivelList_3.get(0);
                m1.setText(obj.getNome());
                p1.setText(obj.getPontuacao().toString());

                obj=nivelList_3.get(1);
                m2.setText(obj.getNome().toString());
                p2.setText(obj.getPontuacao().toString());

                obj=nivelList_3.get(2);
                m3.setText(obj.getNome().toString());
                p3.setText(obj.getPontuacao().toString());
            }
        }

        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void choose_level_1(View v)
    {
        try {
            NivelClass obj = nivelList.get(0); // Inicialização
            if (dif == 1)
                obj = nivelList.get(0);
            if (dif == 2)
                obj = nivelList_2.get(0);
            if (dif == 3)
                obj = nivelList_3.get(0);

            Intent level = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("nivel", obj.getNivel());
            bundle.putString("nome", obj.getNome());
            bundle.putString("sequencia_notas", obj.getSequenciaNotas());
            //ACRESCENTEI DENNIS 18/05
            bundle.putString("pontuacao", obj.getPontuacao().toString());

            bundle.putInt("dificuldade", dif);
            level.putExtras(bundle);
            startActivity(level);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }
    public void choose_level_2(View v)
    {
        try {
            NivelClass obj = nivelList.get(1); // Inicialização
            if (dif == 1)
                obj = nivelList.get(1);
            if (dif == 2)
                obj = nivelList_2.get(1);
            if (dif == 3)
                obj = nivelList_3.get(1);

            Intent level = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("nivel", obj.getNivel());
            bundle.putString("nome", obj.getNome());
            bundle.putString("sequencia_notas", obj.getSequenciaNotas());
            //ACRESCENTEI DENNIS 18/05
            bundle.putString("pontuacao", obj.getPontuacao().toString());
            bundle.putInt("dificuldade", dif);
            level.putExtras(bundle);
            startActivity(level);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    public void choose_level_3(View v)
    {
        try {
            NivelClass obj = nivelList.get(2); // Inicialização
            if (dif == 1)
                obj = nivelList.get(2);
            if (dif == 2)
                obj = nivelList_2.get(2);
            if (dif == 3)
                obj = nivelList_3.get(2);

            Intent level = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("nivel", obj.getNivel());
            bundle.putString("nome", obj.getNome());
            bundle.putString("sequencia_notas", obj.getSequenciaNotas());
            //ACRESCENTEI DENNIS 18/05
            bundle.putString("pontuacao", obj.getPontuacao().toString());

            bundle.putInt("dificuldade", dif);
            level.putExtras(bundle);
            startActivity(level);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    public List<NivelClass> FromFiletoView(String filepath)
    {
        List<NivelClass> list = new ArrayList<NivelClass>();
        File file=new File(filepath);
        String[] loadText = Load(file);

        String finalString="";

        for (int i=0;i<loadText.length;i++)
        {
            // Cria uma obj NivelList para poder adicionar o obj à lista de niveis
            NivelClass obj = new NivelClass();
            finalString = loadText[i];
            String[] separated = finalString.split(";");
            // Insere os dados lidos no obj
            obj.setNivel(Integer.parseInt(separated[0]));
            obj.setNome(separated[1]);
            obj.setSequenciaNotas(separated[2]);
            obj.setPontuacao(Integer.parseInt(separated[3]));
            // adiciona o obj ao nivelList
            list.add(obj);
        }
        return list;
    }

    public void Save(File file)
    {
        try {
            FileOutputStream fp = openFileOutput(file.toString(), MODE_WORLD_READABLE);
            try {
                for (int i = 0; i < nivelList.size(); i++) {
                    String str = nivelList.get(i).getNivel()+";"+nivelList.get(i).getNome()+";"+nivelList.get(i).getSequenciaNotas()+";"+nivelList.get(i).getPontuacao();

                    fp.write(str.getBytes());
                    fp.flush();
                }
                fp.close();
            } catch (IOException e) { e.printStackTrace();}
        }
        catch (FileNotFoundException e2) { e2.printStackTrace();}
    }

    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int x=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                x++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[x];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }

    public void retroceder(View v){finish(); System.exit(0);}
}

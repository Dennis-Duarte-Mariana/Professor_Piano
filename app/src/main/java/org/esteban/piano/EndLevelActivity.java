package org.esteban.piano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 18/05/2016.
 */
public class EndLevelActivity extends Activity
{
    public String path = Environment.getExternalStorageDirectory() + "/niveis";
    public static List<NivelClass> nivelList;
    ImageButton next;
    ImageButton replay;
    int nivel;
    int dif;



    //if (nivelList_3 != null )
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endlevel);


        String avisoEndDIFIC="";//avisa que já chegou ao fim da dificuldade
        int pontuacao=  getIntent().getExtras().getInt("pontuacao");
        String pontuacaoAntiga=  getIntent().getExtras().getString("pontuacaoAntiga");
        nivel =  getIntent().getExtras().getInt("nivel");
        NivelClass auxNivel;
        dif = getIntent().getExtras().getInt("dificuldade");
        nivelList=FromFiletoView(path + "/difficulty_" + dif + ".txt");
        auxNivel= nivelList.get(nivel-1);//depois de ter lido do ficheiro os níveis actuais, vou agora mostrar e/ou atualizar a pontuação do jogador
        TextView LEVELTextView = (TextView)findViewById(R.id.textViewlevelENDNumber);
        LEVELTextView.setText(String.valueOf(nivel));

        if(pontuacao> auxNivel.getPontuacao())
        {
            TextView HighScoreTextView = (TextView)findViewById(R.id.textViewNewHighScore);

            if(nivel==nivelList.size()) {
                switch (dif)
                {
                    case 1:
                        avisoEndDIFIC="\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade INICIANTE ";
                        break;
                    case 2:
                        avisoEndDIFIC= "\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade INTERMÉDIA";
                        break;
                    case 3:
                        avisoEndDIFIC= "\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade AVANÇADA -> ÉS O MAIOR :V : ";
                        break;
                    default:
                        avisoEndDIFIC="";
                        break;
                }
            }
            HighScoreTextView.setText("!!!!!Nova Pontuação !!!!! : "+pontuacao+avisoEndDIFIC);

            TextView OldScoreTextView = (TextView)findViewById(R.id.textViewOldScore);
            OldScoreTextView.setText("Antiga Pontuacao : "+auxNivel.getPontuacao().toString());

            //guardar no ficheiro a nova pontuação
            auxNivel.setPontuacao(pontuacao);
            nivelList.set(nivel-1, auxNivel);
            File ficheiroParaGuardar =new File(path + "/difficulty_" + dif + ".txt");
            Save(ficheiroParaGuardar);

        }
        else {
            if (nivel == nivelList.size()) {
                switch (dif) {
                    case 1:
                        avisoEndDIFIC = "\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade INICIANTE ";
                        break;
                    case 2:
                        avisoEndDIFIC = "\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade INTERMÉDIA";
                        break;
                    case 3:
                        avisoEndDIFIC = "\n PARABÉNS, Conseguiu acabar todos os níveis na dificuldade AVANÇADA -> ÉS O MAIOR :V : ";
                        break;
                    default:
                        avisoEndDIFIC = "";
                }
            }
                TextView HighScoreTextView = (TextView) findViewById(R.id.textViewNewHighScore);
                HighScoreTextView.setText("Pontuação Actual : " + pontuacao + avisoEndDIFIC);
                TextView OldScoreTextView = (TextView) findViewById(R.id.textViewOldScore);
                OldScoreTextView.setText(pontuacaoAntiga);


        }

        // handlers para estar à escuta de clicks nos  botões
        next = (ImageButton) findViewById(R.id.ButtonNext);
        replay = (ImageButton) findViewById(R.id.ButtonReplay);
        next.setOnClickListener(myhandlerNext);
        replay.setOnClickListener(myhandlerReplay);


    }


    //escuta das acções dos botões

    View.OnClickListener myhandlerNext = new View.OnClickListener() {
        public void onClick(View v) {
            if(nivel==nivelList.size()) {
                Intent escolherNivel = new Intent(getBaseContext(), DifActivity.class);
                startActivity(escolherNivel);
            }
            else {
                Intent nivelNovo = new Intent(getBaseContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("nivel", nivelList.get(nivel).getNivel());
                bundle.putString("nome", nivelList.get(nivel).getNome());
                bundle.putString("sequencia_notas", nivelList.get(nivel).getSequenciaNotas());
                //ACRESCENTEI DENNIS 18/05
                bundle.putString("pontuacao", nivelList.get(nivel).getPontuacao().toString());
                bundle.putInt("dificuldade", dif);

                nivelNovo.putExtras(bundle);
                startActivity(nivelNovo);
            }
        }
    };
    View.OnClickListener myhandlerReplay = new View.OnClickListener() {
        public void onClick(View v) {
            Intent nivelNovo = new Intent(getBaseContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("nivel", nivelList.get(nivel-1).getNivel());
            bundle.putString("nome", nivelList.get(nivel-1).getNome());
            bundle.putString("sequencia_notas", nivelList.get(nivel-1).getSequenciaNotas());
            //ACRESCENTEI DENNIS 18/05
            bundle.putString("pontuacao", nivelList.get(nivel-1).getPontuacao().toString());
            bundle.putInt("dificuldade",dif);
            nivelNovo.putExtras(bundle);
            startActivity(nivelNovo);
        }
    };




    //carregar  o ficheiro todo, substituir a linha em causa e voltar a guardar, possivelmente terei que alterar os valores na classe level



    public void Save(File file)
    {
        try {
            if (file.exists())
                file.delete();
            FileOutputStream fp = new FileOutputStream(file);
            try {
                for (int i = 0; i < nivelList.size(); i++) {
                    String str = nivelList.get(i).getNivel()+";"+nivelList.get(i).getNome()+";"+nivelList.get(i).getSequenciaNotas()+";"+nivelList.get(i).getPontuacao()+";\n";

                    fp.write(str.getBytes());
                    fp.flush();
                }
                fp.close();
            } catch (IOException e) { e.printStackTrace();}
        }
        catch (FileNotFoundException e2) { e2.printStackTrace();}
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
    public void returnlevel(View v)
    {
        startActivity(new Intent(this,DifActivity.class));
    }
}

/*
		* This file is part of Piano.
		*
		* Piano is free software: you can redistribute it and/or modify
		* it under the terms of the GNU General Public License as published by
		* the Free Software Foundation, either version 3 of the License, or
		* (at your option) any later version.
		*
		* Piano is distributed in the hope that it will be useful,
		* but WITHOUT ANY WARRANTY; without even the implied warranty of
		* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
		* GNU General Public License for more details.
		*
		* You should have received a copy of the GNU General Public License
		* along with Piano.  If not, see <http://www.gnu.org/licenses/>.
		*/

/*
 * Piano
 * Virtual piano based on Hexiano (https://gitorious.org/hexiano)
 * Original sounds from http://theremin.music.uiowa.edu/MISpiano.html
 */

		package org.esteban.piano;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {

	// Custom view
	private PianoLayout pianoView;
	// List of formerly pressed keys
	private static Set<Integer> old_pressed;
	// Preference variables
	protected String lowerOctavePosition;
	protected String damper; // boolean preferred, but no boolean array resource possible
	protected String octaves;
	protected String orientation;
	private String sequencia;

	//ACRESCENTEI ->DENNIS->18/05/2016
	public int nivel;
	public double pontuacao;
	public int contadorERROS=0;
	public int pontuacaoAntiga;
	public int dificuldade;
	//int index = 0;
	//String[] sequenciaSplit = sequencia.split("x");
	List<String> sequenciaSplit;
	//int sequenciaInicialTamanho = sequenciaSplit.size(), aux;
	//static List<String> sequenciaPrimeiroBlocoAux=new ArrayList<String>(){{add("inicio");
	//	add("inicio");
	//	add("inicio");
	//	add("inicio");
	//	add("inicio");
	//}};

	//static List<String> sequenciaSegundoBlocoAux=new ArrayList<String>(){{add("inicio");
	//	add("inicio");
	//	add("inicio");
	//	add("inicio");
	//	add("inicio");
	//}};
	private boolean desenharTextoFeedbackUser = false;//serve para verificar INICIALMENTE se no método "OnDraw", é para desenhar o texto derivado do jogo(e.g. "ACERTOU","ERROU") SE JÁ HOUVER ALGUMA TECLA PREMIDA,
	private int  decisaoTextoFeedbackUser = 0;//se o user acertou fica a 1, se errou fica a 0, se ganhou fica a 2


	// Preference data interface
	static SharedPreferences sharedPreferences;
	// Flags to detect key presses
	private boolean upPressed;
	private boolean downPressed;

	//
	private static int MAX_CLICK_DURATION;
	private long startClickTime;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load preferences. The following:
//		sharedPreferences = getSharedPreferences("Piano", Activity.MODE_PRIVATE); // if the file doesn't exist it'll be created when retrieving an editor and commiting changes
		// ...didn't call the on change listener. So:
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		// Save preferences into variables
		//lowerOctavePosition = "@string/pref_rows_default_value";
		lowerOctavePosition = sharedPreferences.getString("pref_rows",this.getString(R.string.pref_rows_default_value));
		//damper = "@string/pref_damper_sustain_value";
		damper = sharedPreferences.getString("pref_damper",this.getString(R.string.pref_damper_default_value));
		//octaves = "@string/pref_octaves_default_value";
		octaves = sharedPreferences.getString("pref_octaves",this.getString(R.string.pref_octaves_default_value));
		//orientation = "@string/pref_orient_default_value";
		orientation = sharedPreferences.getString("pref_orient", this.getString(R.string.pref_orient_default_value));

		// Make volume button always control just the media volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Flags initialization
		upPressed = false;
		downPressed = false;

		// Show the view in full screen mode without title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Set preferred orientation
		if (orientation.equals(this.getString(R.string.pref_orient_landscape_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		if (orientation.equals(this.getString(R.string.pref_orient_portrait_value))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		//
		MAX_CLICK_DURATION = Integer.parseInt(sharedPreferences.getString("pref_mx_click",this.getString(R.string.pref_mx_click_default_value)));
		bundle = getIntent().getExtras();
		sequencia = bundle.getString("sequencia_notas");
		//ACRESCENTEI DENNIS 18/05
		nivel=bundle.getInt("nivel");
		pontuacaoAntiga=Integer.parseInt(bundle.getString("pontuacao"));
		dificuldade=bundle.getInt("dificuldade");
		sequenciaSplit = new ArrayList<String>(Arrays.asList(sequencia.split("x")));
		// Set view
		pianoView = new PianoLayout(this.getApplicationContext());
		setContentView(pianoView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		pianoView.destroy();
	}

	// Initialize options menu contents
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Not checking item argument because there's only one option in the menu
		startActivity(new Intent(MainActivity.this, SettingsActivity.class));
		return true;
	}

	// Implement the method that is called when a shared preference is changed, added or removed
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("pref_damper")) {
			damper = sharedPreferences.getString(key,
					this.getString(R.string.pref_damper_default_value));
		}
		if (key.equals("pref_rows")) {
			// See if the preference was really changed or just the dialog shown
			if (!(lowerOctavePosition.equals(sharedPreferences.getString(key,
					this.getString(R.string.pref_rows_default_value))))) {
				// Update variable
				lowerOctavePosition = sharedPreferences.getString(key,
						this.getString(R.string.pref_rows_default_value)	);
				// Swap rows
				for (int i = 0; i < (pianoView.numberOfNotes / 2); i++) {
					Collections.swap(pianoView.keys, i, i + 12);
				}
			}
		}
		if (key.equals("pref_octaves")) {
			// See if the preference was really changed or just the dialog shown
			if (!(octaves.equals(sharedPreferences.getString(key,
					this.getString(R.string.pref_octaves_default_value))))) {
				// Update variable
				octaves = sharedPreferences.getString(key,
						this.getString(R.string.pref_octaves_default_value));
				// Release old sounds and clear their identifications
				for (int id : pianoView.soundIds) {
					pianoView.pianoSounds.unload(id);
				}
				pianoView.soundIds.clear();
				// Load new sounds and save their identifications
				for (int i = 0; i < pianoView.numberOfNotes; i++) {
					int resourceId;
					if (octaves.equals(this.getString(R.string.pref_octaves_34_value))) {
						resourceId = this.getApplicationContext().getResources().
								getIdentifier("note"
												+ i,
										"raw", this.getApplicationContext().getPackageName());
						pianoView.soundIds.add(pianoView.pianoSounds.load(
								this.getApplicationContext(), resourceId, 1));
					}
					if (octaves.equals(MainActivity.this.getString(R.string.pref_octaves_45_value))) {
						resourceId = this.getApplicationContext().getResources().
								getIdentifier("note"
												+ Integer.toString(i + 12),
										"raw", this.getApplicationContext().getPackageName());
						pianoView.soundIds.add(pianoView.pianoSounds.load(
								this.getApplicationContext(), resourceId, 1));
					}
					if (octaves.equals(MainActivity.this.getString(R.string.pref_octaves_35_value))) {
						resourceId = this.getApplicationContext().getResources().
								getIdentifier("note"
												+ Integer.toString(i + (i / 12) * 12),
										"raw", this.getApplicationContext().getPackageName());
						pianoView.soundIds.add(pianoView.pianoSounds.load(
								this.getApplicationContext(), resourceId, 1));
					}
				}
			}
		}
		if (key.equals("pref_orient")) {
			// Update variable
			orientation = sharedPreferences.getString(key,
					this.getString(R.string.pref_orient_default_value));
			// Set preferred screen orientation
			if (orientation.equals(this.getString(R.string.pref_orient_landscape_value))) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			if (orientation.equals(this.getString(R.string.pref_orient_portrait_value))) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}

		if (key.equals("pref_mx_click"))
		{
			// Verifica se a opção foi modificada
			if(!(Integer.toString(MAX_CLICK_DURATION).equals(Integer.parseInt(sharedPreferences.getString(key, this.getString(R.string.pref_mx_click_default_value))))))
			{
				// Faz update à Variavel MAX_CLICK_DURATION
				MAX_CLICK_DURATION = Integer.parseInt(sharedPreferences.getString(key, this.getString(R.string.pref_mx_click_default_value)));
			}
		}
	}

	// Respond to special key press combination when there is no hardware menu key to show the menu
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Set flags according to key presses and releases
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				upPressed = true;
			}
			if (event.getAction() == KeyEvent.ACTION_UP) {
				upPressed = false;
			}
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				downPressed = true;
			}
			if (event.getAction() == KeyEvent.ACTION_UP) {
				downPressed = false;
			}
		}
		// Show the options menu when both volume keys are pressed and there is no hardware menu key
		if ((upPressed == true) && (downPressed == true)
// hasPermanentMenuKey is only available from API 14
//				&&
//				!(ViewConfiguration.get(this.getApplicationContext()).hasPermanentMenuKey())
				) {
			// reset flags
			upPressed = false;
			downPressed = false;
			// show the menu
			this.openOptionsMenu();
			// return
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	public class PianoLayout extends View {
		// a Paint object is needed to be able to draw anything
		private Paint pianoPaint;
		// view dimensions (fixed)
		private int pianoWidth, pianoHeight;
		// Path objects that define the shapes of the keys
		private Path symmetricWhiteKey, asymCFWhiteKey, asymEBWhiteKey, blackKey;
		// notes in the keyboard
		private int numberOfNotes;
		private int numberOfBlackKeys;
		private ArrayList<Integer> blackKeyNoteNumbers;
		// keys
		private ArrayList<Path> keys;
		// sounds
		private SoundPool pianoSounds;
		// sound identifications array, to associate a piano key with its sound
		private ArrayList<Integer> soundIds; // returned by sound pool load
		private ArrayList<Integer> playIds; // returned by sound pool play
		// objects needed to draw outside of onDraw
		private Bitmap pianoBitmap;
		private Canvas pianoCanvas;
		// list of keys that must be shown as pressed
		private ArrayList<Integer> justPressedKeys;

		// Constructor
		public PianoLayout(Context context) {
			super(context);

			// Initialization
			pianoPaint = new Paint();
			pianoPaint.setStrokeWidth(2.0f); // stroke width used when Style is Stroke or StrokeAndFill, in pixels?
			symmetricWhiteKey = new Path();
			asymCFWhiteKey = new Path();
			asymEBWhiteKey = new Path();
			blackKey = new Path();
			keys = new ArrayList<Path>();
			numberOfNotes = 24; // two octaves
			numberOfBlackKeys = 10; // two octaves
			blackKeyNoteNumbers = new ArrayList<Integer>();
			old_pressed = new HashSet<Integer>();
			justPressedKeys = new ArrayList<Integer>();
			pianoSounds = new SoundPool(24, AudioManager.STREAM_MUSIC, 0);
			soundIds = new ArrayList<Integer>();
			playIds = new ArrayList<Integer>();
			for (int i = 0; i < numberOfNotes; i++) {
				int resourceId;
				// Load the sound of each note, saving the identifications
				// (audio files saved as res/raw/note0.ogg etc.)
				if (octaves.equals(MainActivity.this.getString(R.string.pref_octaves_34_value))) {
					resourceId = context.getResources().getIdentifier("note"
									+ Integer.toString(i),
							"raw", context.getPackageName());
					soundIds.add(pianoSounds.load(context, resourceId, 1));
				}
				if (octaves.equals(MainActivity.this.getString(R.string.pref_octaves_45_value))) {
					resourceId = context.getResources().getIdentifier("note"
									+ Integer.toString(i + 12),
							"raw", context.getPackageName());
					soundIds.add(pianoSounds.load(context, resourceId, 1));
				}
				if (octaves.equals(MainActivity.this.getString(R.string.pref_octaves_35_value))) {
					resourceId = context.getResources().getIdentifier("note"
									+ Integer.toString(i + (i / 12) * 12),
							"raw", context.getPackageName());
					soundIds.add(pianoSounds.load(context, resourceId, 1));
				}
				playIds.add(null);
				// Create key objects
				keys.add(new Path());
				// Record the note numbers of the black keys
				switch (i % 12) {
					case 1:
					case 3:
					case 6:
					case 8:
					case 10:
						blackKeyNoteNumbers.add(Integer.valueOf(i));
						break;
					default:
						break;
				}
			}
			pianoCanvas = new Canvas();
		}

		// Draw on canvas, from bitmap
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			// draw the keyboard on the bitmap
			drawOnBitmap();
			// draw the bitmap to the real canvas c
			canvas.drawBitmap(pianoBitmap, 0, pianoHeight, null);

				//*******CÓDIGO QUE ACRESCENTEI-> em baixo até à string "Bem-Vindos..." estão os métodos para desenhar o titulo, porque como esta view é "customizada" tem que ser assim
				Paint paint = new Paint();
				paint.setColor(Color.BLUE);
				int scaledSize = getResources().getDimensionPixelSize(R.dimen.numerosKeyboard);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
				paint.setTextSize(scaledSize);
				//canvas.drawText("Bem-Vindos ao Piano Teclas(muito superior ao Piano Tiles ou como aquela porcaria se chama :v )", 0, +pianoHeight + pianoHeight / 16, paint);

				//Agora vou desenhar os números por cima das teclas brancas superiores
				canvas.drawText("1", pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("3", 1 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("5", 2 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("6", 3 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("8", 4 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("10", 5 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);
				canvas.drawText("12", 6 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight*7/6, paint);


				//Agora vou desenhar os números por cima das teclas brancas inferiores
				canvas.drawText("13", pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("15", 1 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("17", 2 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("18", 3 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("20", 4 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("22", 5 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);
				canvas.drawText("24", 6 * pianoWidth / 7 + pianoWidth / 14, pianoHeight / 4 + pianoHeight / 2 + pianoHeight*7/6, paint);


				paint.setColor(Color.WHITE);
				//Agora vou desenhar os números por cima das teclas pretas superiores
				canvas.drawText("2", pianoWidth / 7, pianoHeight / 6 + pianoHeight, paint);
				canvas.drawText("4", 2 * pianoWidth / 7, pianoHeight / 6 + pianoHeight, paint);
				canvas.drawText("7", 4 * pianoWidth / 7, pianoHeight / 6 + pianoHeight, paint);
				canvas.drawText("9", 5 * pianoWidth / 7, pianoHeight / 6 + pianoHeight, paint);
				canvas.drawText("11", 6 * pianoWidth / 7, pianoHeight / 6 + pianoHeight, paint);

				//Agora vou desenhar os números por cima das teclas pretas superiores
				canvas.drawText("14", pianoWidth /7, pianoHeight / 6 + pianoHeight / 2 + pianoHeight, paint);
				canvas.drawText("16", 2 * pianoWidth /7, pianoHeight / 6 + pianoHeight / 2 + pianoHeight, paint);
				canvas.drawText("19", 4 * pianoWidth /7, pianoHeight / 6 + pianoHeight / 2 + pianoHeight, paint);
				canvas.drawText("21", 5 * pianoWidth /7, pianoHeight / 6 + pianoHeight / 2 + pianoHeight, paint);
				canvas.drawText("23", 6 * pianoWidth /7, pianoHeight / 6 + pianoHeight / 2 + pianoHeight, paint);
			if(decisaoTextoFeedbackUser !=2 )
			{//*******************************************************************************************
				//*************Inicio do nível******
				//***************************************************************************************

				paint.setColor(Color.BLUE);
					scaledSize = getResources().getDimensionPixelSize(R.dimen.feedbackUser);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
					paint.setTextSize(scaledSize);
				canvas.drawText("Pressione a tecla a azul ", pianoWidth / 3, pianoHeight / 10, paint);
				canvas.drawText("Dificuldade: "+dificuldade+"\tNível "+nivel+" \n", pianoWidth/3, pianoHeight / 5,paint);



			}
				//aqui vou desenhar o texto para dar um feednack ao user("ACERTOU","ERROU") consoante a tecla que for premida
				if(desenharTextoFeedbackUser) {
					if (decisaoTextoFeedbackUser==0) {
						paint.setColor(Color.RED);
						scaledSize = getResources().getDimensionPixelSize(R.dimen.feedbackUser);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
						paint.setTextSize(scaledSize);
						contadorERROS++;
						canvas.drawText("ERROU! O nível vai recomeçar", pianoWidth/4, pianoHeight/3, paint);
					}else if(decisaoTextoFeedbackUser==1) {
						paint.setColor(Color.GREEN);
						scaledSize = getResources().getDimensionPixelSize(R.dimen.feedbackUser);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
						paint.setTextSize(scaledSize);
						canvas.drawText("ACERTOU!", 2*pianoWidth/5, pianoHeight/2, paint);
					}
					else
					{paint.setColor(Color.parseColor("#66FF66"));
						scaledSize = getResources().getDimensionPixelSize(R.dimen.feedbackUser);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
						paint.setTextSize(scaledSize);
						canvas.drawText("PARABÉNS !!! Chegou ao fim deste nível!!!", pianoWidth/4, pianoHeight / 10 ,paint);
						Intent endlevel = new Intent(getBaseContext(), EndLevelActivity.class);
						endlevel.putExtra("nivel", nivel);

						//ACRESCENTEI DENNIS ->18/05/2016
						//aqui vou medir a pontuação do utilizador depois de ter chegado ao fim do nivel, se ele tiver mais erros do que o numero de notas na musica, então é zero, senão fica o numero de erros a dividir pelo numero de notas
						if(sequenciaSplit.size()<=contadorERROS)
						{
							pontuacao=0;

						}
						else if( contadorERROS ==0)
						{
							pontuacao=100;

						}
						else
						{
							pontuacao=contadorERROS*1.0/sequenciaSplit.size();//converte para double(*1.0)
							pontuacao=(1-pontuacao)*100;//a linha anterior dá me a percentagem de erros, e eu aqui transformo para a pontuacao de eficacia
						}
						endlevel.putExtra("sequencia",sequencia);
						endlevel.putExtra("pontuacao",(int)pontuacao);
						endlevel.putExtra("pontucaoAntiga",pontuacaoAntiga);
						endlevel.putExtra("dificuldade",dificuldade);
						startActivity(endlevel);
						//*******************************************************************************************
						//*************Aqui é que acaba o nível, por isso deve ser aqui que volta para a activity END-LEVEL******
						//***************************************************************************************

					}
				}
		/*	/*//***************
			//PRIMEIRO BLOCO
			/*//****************


			//serve para "reconstruir" o array de string para poder mostrar ao utilizador a sequencia actual
			StringBuilder builder = new StringBuilder();

			//Nesta condição, verifico se a partir do tamanho inicial da sequencia da musica com a sequencia actual, tem uma "Resto" de 5 ==0, isto
			//permite-me identificar de forma eficaz a sequencia da música de 5 em 5 notas, isto tudo porque quero apresentar a sequencia em blocos de 5
			if((sequenciaInicialTamanho-sequenciaSplit.size())% 5==0 )
			{
				if (sequenciaSplit.size() >= 5) {//se o tamanho actual da sequencia não for maior que 5, eu não posso apresentar no bloco 5 notas
					for (int i = 0; i < 5; i++) {
						if (sequenciaSplit.get(i) != null) {

							sequenciaPrimeiroBlocoAux.set(i, sequenciaSplit.get(i));

						}

					}
				} else {//aqui estou ao chegar ao final do nivel, por isso nos ultimos caracteres meti a string "FIM"
					for (aux=0; aux < sequenciaSplit.size(); aux++) {
						if (sequenciaSplit.get(aux) != null) {
							//se o caracter não tiver a null(por causa da remoção de caracteres consoante o progresso no jogo para poder avaliar as outras teclas)
							sequenciaPrimeiroBlocoAux.set(aux, sequenciaSplit.get(aux));
						}
					}

					aux=sequenciaSplit.size();
					while(aux<5)
					{
						sequenciaPrimeiroBlocoAux.set(aux, "Fim");
						aux++;
					}


				}

			}
			int flagTeclaSeguinte=33;//numero aleatorio
			aux=0;//aux=contadorCicloAcrescentarString
			switch (sequenciaInicialTamanho%5-sequenciaSplit.size()%5) {
				case 0: flagTeclaSeguinte=0;
					break;
				case 1:	flagTeclaSeguinte=1;
					break;
				case 2:	flagTeclaSeguinte=2;
					break;
				case 3:	flagTeclaSeguinte=3;
					break;
				case 4:	flagTeclaSeguinte=4;
					break;
			}
			for(String s : sequenciaPrimeiroBlocoAux) {
				if(s!=null) {
					//se o caracter não tiver a null(por causa da remoção de caracteres consoante o progresso no jogo para poder avaliar as outras teclas)

					builder.append(s);
					if(flagTeclaSeguinte==aux)//aux=contadorCicloAcrescentarString
						builder.append("*");//com as flags do switch em cima, consigo identificar qual a nota seguinte, e nessa nota acrescento-lhe um "*" para se realçar melhor
					if(aux!=4)//aux=contadorCicloAcrescentarString
						builder.append(" -> ");
					aux++;//aux=contadorCicloAcrescentarString
				}

			}



			//Aqui desenho o primeiro bloco
			scaledSize = getResources().getDimensionPixelSize(R.dimen.feedbackUser);//a dimensão é relativa, logo dá para qualquer tamanho do dispositivo que corra(sp->independent size) e está definida no dimens.xml
			paint.setTextSize(scaledSize);
			Paint paintRect = new Paint();
			paintRect.setColor(Color.GREEN);
			paint.setColor(Color.WHITE);

			canvas.drawRect(pianoWidth / 17,  pianoHeight / 4, pianoWidth / 2,  pianoHeight / 2, paintRect);
			canvas.drawText(builder.toString(), pianoWidth / 16,  pianoHeight / 3, paint);

			/*//***************
			//SEGUNDO BLOCO
			/*//****************
			//Aqui verifico se as teclas seguintes são as últimas duas do primeiro bloco, se forem então desenho o segundo bloco
			if((flagTeclaSeguinte==3 || flagTeclaSeguinte==4) && sequenciaSplit.size()>5)
			{paintRect.setColor(Color.YELLOW);
				if(flagTeclaSeguinte==3)
				{
					if (sequenciaSplit.size() >= 10) {
						for (int i = 2; i < 7; i++) {
							if (sequenciaSplit.get(i) != null) {

								sequenciaSegundoBlocoAux.set(i - 2, sequenciaSplit.get(i));

							}

						}
					} else {
						for (int i = 2; i < sequenciaSplit.size(); i++) {
							if (sequenciaSplit.get(i) != null) {
								//se o caracter não tiver a null(por causa da remoção de caracteres consoante o progresso no jogo para poder avaliar as outras teclas)
								sequenciaSegundoBlocoAux.set(i - 2, sequenciaSplit.get(i));
							}
						}

					}
				}
				else if(flagTeclaSeguinte==4) {
					if (sequenciaSplit.size() >= 10) {
						for (int i = 1; i < 6; i++) {
							if (sequenciaSplit.get(i) != null) {

								sequenciaSegundoBlocoAux.set(i - 1, sequenciaSplit.get(i));

							}

						}
					} else {
						for (int i = 1; i < sequenciaSplit.size(); i++) {
							if (sequenciaSplit.get(i) != null) {
								//se o caracter não tiver a null(por causa da remoção de caracteres consoante o progresso no jogo para poder avaliar as outras teclas)
								sequenciaSegundoBlocoAux.set(i - 1, sequenciaSplit.get(i));
							}
						}

					}

				}
				builder.setLength(0);
				for(String s : sequenciaSegundoBlocoAux) {
					if(s!=null) {
						//se o caracter não tiver a null(por causa da remoção de caracteres consoante o progresso no jogo para poder avaliar as outras teclas)
						builder.append(s);
						if(aux!=4)//aux=contadorCicloAcrescentaString
							builder.append(" -> ");
					}

				}
				paint.setColor(Color.BLACK);
				canvas.drawRect(pianoWidth / 17,  pianoHeight / 2, pianoWidth / 2,  3*pianoHeight / 4, paintRect);
				canvas.drawText(builder.toString(), pianoWidth / 16,  2*pianoHeight /3 , paint);
			}
*/
		}







		//*****************************************
		//ATENCAO AO PROXIMO METODO (IMPORTANTE PARA O MECANISMO DO JOGO)
		//*****************************************


		// React when the user touches, stops touching, or touches in a new way,
		// the piano keyboard
		@SuppressLint("UseSparseArrays")
		public boolean onTouchEvent(MotionEvent event){//Neste método é que tem que se por a verificação da sequencia da musica(pois aqui verifica qual a tecla que foi tocada!!!!!!)
			// React only to some actions, ignoring the others
			int action = event.getAction();
			int actionCode = action & MotionEvent.ACTION_MASK;
			if (!(
					actionCode == MotionEvent.ACTION_DOWN ||
							actionCode == MotionEvent.ACTION_POINTER_DOWN ||
							actionCode == MotionEvent.ACTION_UP ||
							actionCode == MotionEvent.ACTION_POINTER_UP ||
							actionCode == MotionEvent.ACTION_MOVE
			)) {
				return false;
			}
			//
			boolean isClick=false;
			switch (actionCode) {
				case MotionEvent.ACTION_DOWN: {
					startClickTime = Calendar.getInstance().getTimeInMillis();
					break;
				}
				case MotionEvent.ACTION_UP: {
					long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
					if (clickDuration < MAX_CLICK_DURATION) {
						isClick = true;
					}
					break;
				}
			}

				// Use of maps to keep track of:
			//       all affected keys:  pressed_map
			//       pressed keys:       new_pressed
			Set<Integer> new_pressed = new HashSet<Integer>();
			HashMap<Integer, Float> pressed_map = new HashMap<Integer, Float>();



				// For every pointer, see which key the point belongs to
				for (int pointerIndex = 0; pointerIndex < event.getPointerCount(); pointerIndex++) {
					// Get coordinates of the point for the current pointer
					int x = (int) event.getX(pointerIndex);
					int y = (int) event.getY(pointerIndex);
					int keyWasFound = 0; // flag to do something with a key
					int noteFound = 0; // variable to store the note number of the affected key
					// Check black keys first, because white keys' bounding rectangles overlap with
					// black keys
					int blackKeyFound = 0; //   flag to skip white keys check
					// Check black keys

					for (int i = 0; i < numberOfBlackKeys; i++) {
						RectF bounds = new RectF();
						keys.get(blackKeyNoteNumbers.get(i)).computeBounds(bounds, true);
						if (bounds.contains((float) x, (float) y)) {
							blackKeyFound = 1;
							keyWasFound = 1;
							noteFound = blackKeyNoteNumbers.get(i);
						}
					}
					// Check white keys, if necessary
					for (int i = 0; (i < numberOfNotes) && (blackKeyFound == 0); i++) {
						if (blackKeyNoteNumbers.contains(i)) {
							continue; // skip black keys -already checked
						}

						RectF bounds = new RectF();
						keys.get(i).computeBounds(bounds, true);
						if (bounds.contains((float) x, (float) y)) {
							keyWasFound = 1;
							noteFound = i;

						}

					}


					// Save found key
					if (keyWasFound == 1) {
						// save note number and pressure in all affected keys map
						if (pressed_map.containsKey(noteFound)) {
							pressed_map.put(noteFound,
									Math.max(event.getPressure(pointerIndex),
											pressed_map.get(noteFound)));
						} else {
							pressed_map.put(noteFound, event.getPressure(pointerIndex));
						}
						// if appropriate, save note number in pressed keys map
						if ((pointerIndex != event.getActionIndex() || (
								actionCode != MotionEvent.ACTION_UP &&
										actionCode != MotionEvent.ACTION_POINTER_UP))) {
							new_pressed.add(noteFound);
						}


						//aqui é que se vai verificar a sequencia da musica com a tecla pressionada
						//*************************************************************************
						//VERIFICAÇÃO SE A TECLA PREMIDA COINCIDE COM A SEQUENCIA DA MUSICA, DEPOIS
						//DISSO SERÁ DADO UM FEEDBACK AO UTILIZADOR
						//*************************************************************************
						if(Integer.parseInt(sequenciaSplit.get(0))==noteFound+1)
						{
							// Verifica se foi efetuado o CLICK!
							if (isClick == true) {
								sequenciaSplit.remove(0);

							}
							//as duas linhas anteriores vão remover do array de string o caracter premido com sucesso,
							// para assim mais à frente poder avaliar outro caracter
							if(sequenciaSplit.isEmpty())
							{decisaoTextoFeedbackUser = 2;//flags para o método onDraw
								sequenciaSplit = new ArrayList<String>( Arrays.asList(sequencia.split("x")));
							}
							else
							{
								decisaoTextoFeedbackUser = 1;//flags para o método onDraw
								desenharTextoFeedbackUser = true;//flags para o método onDraw
							}
						} else {//se chegou a esta parte, quer dizer que a tecla premida não coincide com  a sequencia da musica

							sequenciaSplit = new ArrayList<String>( Arrays.asList(sequencia.split("x")));//volta a construir a sequencia de novo
							decisaoTextoFeedbackUser = 0;//flags para o método onDraw
							desenharTextoFeedbackUser = true;//flags para o método onDraw


						}
					}


				// Map of newly pressed keys (pressed keys that weren't already pressed)
				Set<Integer> just_pressed = new HashSet<Integer>(new_pressed);
				just_pressed.removeAll(old_pressed);
				// Play the sound of each newly pressed key
				Iterator<Integer> it = just_pressed.iterator();
				justPressedKeys.clear(); // empty the list of just pressed keys (used to draw them)
				while (it.hasNext()) {
					int i = it.next();
					justPressedKeys.add(i); // add the key (note number) to the list so that it can be shown as pressed
					try {
						playIds.set(i, pianoSounds.play(soundIds.get(i), 1.0f, 1.0f, 1, 0, 1.0f));
					} catch (Exception e) {
						Log.e("PianoLayout.onTouchEvent", "Key " + i + " not playable");
					}
				}

				// Stop the sound of released keys
				if (damper.equals(
						MainActivity.this.getString(R.string.pref_damper_dampen_value))) {
					Set<Integer> just_released = new HashSet<Integer>(old_pressed);
					just_released.removeAll(new_pressed);
					it = just_released.iterator();
					while (it.hasNext()) {
						int i = it.next();
						pianoSounds.stop(playIds.get(i));
					}
				}
			}
			// Update map of pressed keys
			old_pressed = new_pressed;
			// Force a call to onDraw() to give visual feedback to the user
			this.invalidate();

			return true;
		}

		// Create shapes
		private void createShapes() {
			// Define the shapes
			// ___
			// |  |
			// |  |_
			// |    |
			// |____|
			//
			asymCFWhiteKey.moveTo(0.0f, 0.0f);
			asymCFWhiteKey.lineTo((float) pianoWidth * 17 / 168, 0.0f);
			asymCFWhiteKey.lineTo((float) pianoWidth * 17 / 168, (float) pianoHeight / 4);
			asymCFWhiteKey.lineTo((float) pianoWidth / 7, (float) pianoHeight / 4);
			asymCFWhiteKey.lineTo((float) pianoWidth / 7, (float) pianoHeight / 2);
			asymCFWhiteKey.lineTo(0.0f, (float) pianoHeight / 2);
			asymCFWhiteKey.lineTo(0.0f, 0.0f);
			asymCFWhiteKey.offset(0.0f, (float) pianoHeight);
			//    __
			//   |  |
			//  _|  |
			// |    |
			// |____|
			//
			asymEBWhiteKey.moveTo((float) pianoWidth / 24, 0.0f);
			asymEBWhiteKey.lineTo((float) pianoWidth / 7, 0.0f);
			asymEBWhiteKey.lineTo((float) pianoWidth / 7, (float) pianoHeight / 2);
			asymEBWhiteKey.lineTo(0.0f, (float) pianoHeight / 2);
			asymEBWhiteKey.lineTo(0.0f, (float) pianoHeight / 4);
			asymEBWhiteKey.lineTo((float) pianoWidth / 24, (float) pianoHeight / 4);
			asymEBWhiteKey.lineTo((float) pianoWidth / 24, 0.0f);
			asymEBWhiteKey.offset((float) pianoWidth * 2 / 7, pianoHeight);
			//  __
			// |  |
			// |  |
			// |__|
			//
			blackKey.addRect(0.0f, 0.0f, (float) pianoWidth / 12, (float) pianoHeight / 4, Path.Direction.CW);
			blackKey.offset((float) pianoWidth * 17 / 168, pianoHeight);
			//
			//    __
			//   |  |
			//  _|  |_
			// |      |
			// |______|
			//
			symmetricWhiteKey.moveTo((float) pianoWidth / 24, 0.0f);
			symmetricWhiteKey.lineTo((float) pianoWidth * 17 / 168, 0.0f);
			symmetricWhiteKey.lineTo((float) pianoWidth * 17 / 168, (float) pianoHeight / 4);
			symmetricWhiteKey.lineTo((float) pianoWidth / 7, (float) pianoHeight / 4);
			symmetricWhiteKey.lineTo((float) pianoWidth / 7, (float) pianoHeight / 2);
			symmetricWhiteKey.lineTo(0.0f, (float) pianoHeight / 2);
			symmetricWhiteKey.lineTo(0.0f, (float) pianoHeight / 4);
			symmetricWhiteKey.lineTo((float) pianoWidth / 24, (float) pianoHeight / 4);
			symmetricWhiteKey.lineTo((float) pianoWidth / 24, 0.0f);
			symmetricWhiteKey.offset((float) pianoWidth / 7, pianoHeight);
			// Save the shapes as keys
			for (int i = 0; i < (numberOfNotes / 2); i++) {
				if (blackKeyNoteNumbers.contains(i)) {
					// Black keys
					switch (i) {
						case 3: // D# = Eb
						case 8: // G# = Ab
						case 10: // A# = Bb
							blackKey.offset((float) pianoWidth / 7, 0.0f);
							break;
						case 6: // F# = Gb
							blackKey.offset((float) pianoWidth * 2 / 7, 0.0f);
							break;
						default:
							break;
					}
					keys.get(i).set(blackKey);
					blackKey.offset(0.0f, (float) pianoHeight / 2);
					keys.get(i + 12).set(blackKey);
					blackKey.offset(0.0f, (float) -pianoHeight / 2);
				} else {
					// White keys
					if ((i == 0) || (i == 5)) {
						// CF key
						if (i == 5) { // F
							asymCFWhiteKey.offset((float) pianoWidth * 3 / 7, 0.0f);
						}
						keys.get(i).set(asymCFWhiteKey);
						asymCFWhiteKey.offset(0.0f, (float) pianoHeight / 2);
						keys.get(i + 12).set(asymCFWhiteKey);
						asymCFWhiteKey.offset(0.0f, (float) -pianoHeight / 2);
					}
					if ((i == 2) || (i == 7) || (i == 9)) {
						// symmetric key
						switch (i) {
							case 7: // G
								symmetricWhiteKey.offset((float) pianoWidth * 3 / 7, 0.0f);
								break;
							case 9: // A
								symmetricWhiteKey.offset((float) pianoWidth / 7, 0.0f);
								break;
							default:
								break;
						}
						keys.get(i).set(symmetricWhiteKey);
						symmetricWhiteKey.offset(0.0f, (float) pianoHeight / 2);
						keys.get(i + 12).set(symmetricWhiteKey);
						symmetricWhiteKey.offset(0.0f, (float) -pianoHeight / 2);
					}
					if ((i == 4) || (i == 11)) {
						// EB key
						if (i == 11) { // B
							asymEBWhiteKey.offset((float) pianoWidth * 4 / 7, 0.0f);
						}
						keys.get(i).set(asymEBWhiteKey);
						asymEBWhiteKey.offset(0.0f, (float) pianoHeight / 2);
						keys.get(i + 12).set(asymEBWhiteKey);
						asymEBWhiteKey.offset(0.0f, (float) -pianoHeight / 2);
					}
				}
			}
			// Exchange rows depending on preference
			if (lowerOctavePosition.equals(
					MainActivity.this.getString(
							R.string.pref_rows_higher_octave_in_upper_row_value))) {
				// Swap rows
				for (int i = 0; i < (pianoView.numberOfNotes / 2); i++) {
					Collections.swap(pianoView.keys, i, i + 12);
				}
			}
		}



		//******************************************************************************************************************************
		//Nesta função, vou acrecentar o código para aparecer a tecla a ser tocada noutra cor(verde -> porque será :) )
		//******************************************************************************************************************************
		// Draw on bitmap
		protected void drawOnBitmap() {
			// Erase the canvas
			pianoCanvas.drawColor(Color.WHITE);
			// Draw the keys
			for (int i = 0; i < numberOfNotes; i++) {
					if (blackKeyNoteNumbers.contains(i)) {
					// Black keys
					pianoPaint.setStyle(Paint.Style.FILL); // all filled; ignore all stroke-related settings in the paint

						if (justPressedKeys.contains(i)) {
						pianoPaint.setColor(Color.LTGRAY);
						} else
						{
						pianoPaint.setColor(Color.BLACK);
							if(Integer.parseInt(sequenciaSplit.get(0))-1==i)//vou buscar o numero da tecla a ser tocada a seguir, depois nessa tecla preencho a cor
							{

									if(dificuldade!=3)//se estiver na dificuldade avançada, em vez de pintar a tecla toda, pinto só o contorno naquela tecla que deve ser tocada a segur
									{
										pianoPaint.setStyle(Paint.Style.FILL);
										pianoPaint.setColor(Color.parseColor("#A6E9FD"));
									}
									else {
										pianoPaint.setStyle(Paint.Style.STROKE);
										pianoPaint.setColor(Color.BLUE);
									}


							}
						}
				} else {
					// White keys
					if (justPressedKeys.contains(i)) {
						pianoPaint.setStyle(Paint.Style.FILL); // all filled; ignore all stroke-related settings in the paint
						pianoPaint.setColor(Color.DKGRAY);
					} else {
						pianoPaint.setStyle(Paint.Style.STROKE); // stroked
						pianoPaint.setColor(Color.BLACK);
						if(Integer.parseInt(sequenciaSplit.get(0))-1==i)//vou buscar o numero da tecla a ser tocada a seguir, depois nessa tecla preencho a cor
						{
							if(dificuldade!=3)//se estiver na dificuldade avançada, em vez de pintar a tecla toda, pinto só o contorno naquela tecla que deve ser tocada a segur
							{
								pianoPaint.setStyle(Paint.Style.FILL);
								pianoPaint.setColor(Color.parseColor("#A6E9FD"));
							}
							else {
								pianoPaint.setStyle(Paint.Style.STROKE);
								pianoPaint.setColor(Color.BLUE);
							}
						}
					}
				}


				keys.get(i).offset(0.0f,-pianoHeight);
				pianoCanvas.drawPath(keys.get(i), pianoPaint);
				keys.get(i).offset(0.0f,pianoHeight);
			}
		}

		// Free resources
		public void destroy() {
			if (pianoBitmap != null) {
				pianoBitmap.recycle(); // mark the bitmap as dead
			}
			if (pianoSounds != null) {
				pianoSounds.release(); // release the sound pool resources
			}
		}





		//*******************************
		//O PROXIMO MÉTODO É CHAMADO QUANDO O DISPOSITIVO DESCOBRE O TAMANHO REAL DO DISPOSITIVO, POR ISSO É AQUI QUE É DEFINIDO O TAMANHO DO ECRA
		//AQUI É QUE VOU ALTERAR PARA SÓ METER METADE DO ECRA,!!!ATENCAO, ISTO REPRESENTA O INCIO DA APLICAÇÃO, SENDO QUE DEPOIS É QUE SÃO DESENHADAS AS FORMAS
		//*******************************

		// Deal with view size changes
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			if (pianoBitmap != null) {
				pianoBitmap.recycle(); // mark the bitmap as dead
			}
			pianoWidth = w;
			pianoHeight = h/2;//metade da largura real -> ACRESCENTEI!!!
			pianoBitmap = Bitmap.createBitmap(pianoWidth, pianoHeight, Bitmap.Config.ARGB_8888);

			pianoCanvas.setBitmap(pianoBitmap);

			this.createShapes();//MÉTODO PARA DESENHAR AS FORMAS
			this.drawOnBitmap();



		}
	}
}

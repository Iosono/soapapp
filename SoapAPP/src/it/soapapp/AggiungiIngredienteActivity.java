package it.soapapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AggiungiIngredienteActivity extends Activity {


	/*
	private static final String[] RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION = new String[] {
		SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE };
	*/
	
	private SimpleDateFormat dataFormat;
	private ArrayList<String> listaTipiIng = new ArrayList<String>();
	//private ListView lvIngType;
	
	// riferimenti agli oggetti del layout
	private Spinner spTipoIng;

	// variabili che memorizzano il valore dei campi Store
	private String nomeIng, aliasIng, descrizioneIng, negozioIng, noteIng;
	private double costoLordoIng, costoNettoIng, costoGrammoIng, pesoLordoIng, pesoNettoIng;
	//private Date ingBuyDate, ingMaturityDate;

	private EditText etNomeIng, etAliasIng, etDescrizioneIng, etCostoLordoIng,
			etCostoNettoIng, etCostoGrammoIng, etPesoLordoIng, etPesoNettoIng,
			etNegozioIng, etNoteIng;

	/*
	private EditText tvIngName, tvIngAlias, tvIngDesc, tvIngGrossPrice, tvIngNetPrice, tvIngPriceGram,
		tvIngGrossWeight, tvIngNetWeight, tvIngBuyDate, tvIngMaturityDate, tvIngShop, tvIngNotes;
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aggiungi_ingrediente);
		
		
		//lvIngType = (ListView) findViewById(R.id.lv_ingredientType);
		
		// EditText contenenti i valori inseriti dall'utente
		spTipoIng = (Spinner) findViewById(R.id.sp_ingredientType);
		etNomeIng = (EditText) findViewById(R.id.et_nomeIng);
		etAliasIng = (EditText) findViewById(R.id.et_aliasIng);
		etDescrizioneIng = (EditText) findViewById(R.id.et_descrizioneIng);
		etCostoLordoIng = (EditText) findViewById(R.id.et_costoLordoIng);
		etCostoNettoIng = (EditText) findViewById(R.id.et_costoNettoIng);
		etCostoGrammoIng = (EditText) findViewById(R.id.et_costoGrammoIng);
		etPesoLordoIng = (EditText) findViewById(R.id.et_pesoLordoIng);
		etPesoNettoIng = (EditText) findViewById(R.id.et_pesoNettoIng);
		//etIngBuyDate = (EditText) findViewById(R.id.et_ingBuyDate);
		//etIngMaturityDate = (EditText) findViewById(R.id.et_ingMaturityDate);
		etNegozioIng = (EditText) findViewById(R.id.et_nomeNegozioIng);
		etNoteIng = (EditText) findViewById(R.id.et_noteIng);
		
		
		// formato della data
		dataFormat = new SimpleDateFormat("yyyy/MM/dd");
		

		// popola la lista contenente le tipologie di ingredienti
		popolaTipiIng();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	/** Metodo chiamato per prelevare nella tabella "tipi ingredienti" i valori: tipi ingredienti*/
	private void popolaTipiIng() {
		
		ContentResolver resolver = getContentResolver();
		
		Uri uri = SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI;
		
		Cursor cursor = resolver.query(uri, null, null, null, null);

		int indiceColonna;
		//String nameColumn;
		int tipoColonna;
		String tipoRiga;
		
		/*
		//RISCRITTO IL CICLO PER CONCATENARE TUTTI GLI ATTRIBUTI DI UNA TUPLA IN UN UNICA STRINGA
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				rowType = "";
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					indexColumn = cursor.getColumnIndex(RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION[i]);
					//nameColumn = mCursor.getColumnName(indexColumn);
					typeColumn = cursor.getType(indexColumn);

					switch (typeColumn) {
					case Cursor.FIELD_TYPE_BLOB:
						rowType = rowType + "BLOB";
						break;

					case Cursor.FIELD_TYPE_FLOAT:
						float dvaloreColumn = cursor.getFloat(indexColumn);
						rowType = rowType + dvaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_INTEGER:
						int ivaloreColumn = cursor.getInt(indexColumn);
						rowType = rowType + ivaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_STRING:
						String svaloreColumn = (String) cursor.getString(indexColumn);
						rowType = rowType + svaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_NULL:
						rowType = rowType + " ";
						break;

					default:
						rowType = rowType + "";
					}
				}
				listIngType.add(rowType);
				cursor.moveToNext();
			}
			*/
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				indiceColonna = 1;
				tipoColonna = cursor.getType(indiceColonna);
				if(tipoColonna == Cursor.FIELD_TYPE_STRING){
					listaTipiIng.add((String) cursor.getString(indiceColonna));
				} else {
					listaTipiIng.add("ERRORE NEL PRELIEVO DEL NOME DELL'INGREDIENTE");
				}
				cursor.moveToNext();
			}
		} else {
			cursor.close();
			tipoRiga = "NESSUNA RIGA ESTRATTA DALLA TABELLA TIPI INGREDIENTI";
			listaTipiIng.add(tipoRiga);
		}
		
		
		// se non funziona provare:
		// ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listIngType);
		
		// popolo un arrayAdapter di stringhe con la lista precedentemente usata
		String[] arrIngType = new String[listaTipiIng.size()];
		listaTipiIng.toArray(arrIngType);
		ArrayAdapter<String> ingAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrIngType);		
		spTipoIng.setAdapter(ingAdapter);
	}
	
	/** Metodo chiamato quando si preme il bottone Salva */
	public void salvaMagazzino(View view)
	{
		// TODO
		// FINIRE DI GESTIRE TUTTE LE ECCEZIONI
		//try {
		nomeIng = etNomeIng.getText().toString();
		aliasIng = etAliasIng.getText().toString();
		descrizioneIng = etDescrizioneIng.getText().toString();
		costoLordoIng = Double.parseDouble(etCostoLordoIng.getText().toString());
		costoNettoIng = Double.parseDouble(etCostoNettoIng.getText().toString());
		costoGrammoIng = Double.parseDouble(etCostoGrammoIng.getText().toString());
		pesoLordoIng = Double.parseDouble(etPesoLordoIng.getText().toString());
		pesoNettoIng = Double.parseDouble(etPesoNettoIng.getText().toString());
		//ingBuyDate = dataFormat.parse(etIngBuyDate.getText().toString());
		//ingMaturityDate = dataFormat.parse(etIngMaturityDate.getText().toString());
		negozioIng = etNegozioIng.getText().toString();
		noteIng = etNoteIng.getText().toString();
		//} catch (ParseException e) {
		//	Toast.makeText(this, "Errore nel prelevare i valori della data", Toast.LENGTH_SHORT).show();
		//	e.printStackTrace();
		//}
	}
	
	private void updateUI()
	{
		// TODO
		// aggiornare listView tipologia ingredienti, usare arrayAdapter
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aggiungi_ingrediente, menu);
		return true;
	}
	

	
	
	// TODO
	// METODI DA IMPLEMENTARE
	
	@Override
	protected void onStart() {
		// Metodo chiamato quando l’activity diventa visibile all’utente
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		// Metodo  chiamato quando l’attuale activity viene messa in pausa e un’activity precedente viene ripristinata
		super.onPause();		
	}
	
	@Override
	protected void onStop() {
		// Metodo chiamato quando l’activity non è più visibile all’utente
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// Metodo chiamato prima che l’activity venga distrutta, manualmente o dal sistema operativo per liberare memoria
		super.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		// Metodo chiamato dopo che l’activity era stata stoppata e quando è pronta ad essere ripristinata
		super.onRestart();
	}
		
	@Override
	public void onBackPressed() {
		// Metodo utile per eliminare dati salvati temporaneamente prima che si cambi activity
		super.onBackPressed();
	}	
	
	/*
	 
	@Override
	protected void onSaveInstanceState(Bundle outState) {	
	}	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	}	
	protected boolean onKeyDown(int keyCode, KeyEvent event) {		
	}	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
	}	
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	*/


}

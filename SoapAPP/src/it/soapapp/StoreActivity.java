package it.soapapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class StoreActivity extends Activity {

	/*
	private static final String[] RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION = new String[] {
		SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE };
	*/
	
	private ArrayList<String> listIngType = new ArrayList<String>();
	//private ListView lvIngType;
	private Spinner spIngType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
				
		//lvIngType = (ListView) findViewById(R.id.lv_ingredientType);
		spIngType = (Spinner) findViewById(R.id.sp_ingredientType);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();

		// popola la lista contenente le tipologie di ingredienti
		populateIngType();
	}
	
	/** Metodo chiamato per prelevare nella tabella "tipi ingredienti" i valori: tipi ingredienti*/
	private void populateIngType() {
		
		ContentResolver resolver = getContentResolver();
		
		Uri uri = SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI;
		
		Cursor cursor = resolver.query(uri, null, null, null, null);

		int indexColumn;
		//String nameColumn;
		int typeColumn;
		String rowType;
		
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
				indexColumn = 1;
				typeColumn = cursor.getType(indexColumn);
				if(typeColumn == Cursor.FIELD_TYPE_STRING){
					listIngType.add((String) cursor.getString(indexColumn));
				} else {
					listIngType.add("ERRORE NEL PRELIEVO DEL NOME DELL'INGREDIENTE");
				}
				cursor.moveToNext();
			}
		} else {
			cursor.close();
			rowType = "NESSUNA RIGA ESTRATTA DALLA TABELLA TIPI INGREDIENTI";
			listIngType.add(rowType);
		}
		
		
		// se non funziona provare:
		// ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listIngType);
		
		// popolo un arrayAdapter di stringhe con la lista precedentemente usata
		String[] arrIngType = new String[listIngType.size()];
		listIngType.toArray(arrIngType);
		ArrayAdapter<String> ingAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrIngType);		
		spIngType.setAdapter(ingAdapter);
	}
	
	private void updateUI()
	{
		// TODO
		// aggiornare listView tipologia ingredienti, usare arrayAdapter
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store, menu);
		return true;
	}

}

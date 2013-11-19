package it.soapapp;

import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StoreActivity extends Activity {

	private List<String> listIngType;
	private ListView lvIngType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
				
		lvIngType = (ListView) findViewById(R.id.lv_ingredientType);
		
		// popola la lista contenente le tipologie di ingredienti
		populateIngType();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
		
	}
	
	/** Metodo chiamato per prelevare nella tabella "tipi ingredienti" i valori: tipi ingredienti*/
	private void populateIngType() {
		
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI");
		Cursor cursor = resolver.query(uri, null, null, null, null);

		int i;
		int j = cursor.getColumnCount();
		String rowType;
		
		// prelevo i valori inseriti nella tabella: tipi ingredienti
		if (cursor.moveToFirst() && j > 0) {

			i = 0;
			rowType = "";

			// ciclo barbaro per prendere i valori di tutte le colonne
			while (i < j) {
				rowType = rowType + " " + cursor.getString(i);
				i++;
			}

			listIngType.add(rowType);

		}
		while (cursor.moveToNext());
		
		// se non funziona provare:
		// ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listIngType);
		
		// popolo un arrayAdapter di stringhe con la lista precedentemente usata
		String[] arrIngType = new String[listIngType.size()];
		listIngType.toArray(arrIngType);
		ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrIngType);		
		lvIngType.setAdapter(adapter);
	}	
		
	
	
	/*
	private void populateList() {
		listIngType.add("Farina");
		listIngType.add("Olio");
		listIngType.add("Liquido");
	}
	*/
	
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

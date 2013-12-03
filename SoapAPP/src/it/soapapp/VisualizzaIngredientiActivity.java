package it.soapapp;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VisualizzaIngredientiActivity extends Activity {
	
	private static final String[] COLONNE_RICETTE_SAPONI_MAGAZZINO = new String[] {
		SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
		SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE};
	
	private ArrayList<String> listaIngredienti = new ArrayList<String>();
	
	// riferimenti agli oggetti del layout
	private ListView lvIngredienti;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizza_ingredienti);
		
		// metodo che popola la lista contenente gli ingredienti
		popolaListaIng();
		
		// listView contenente gli ingredienti del magazzino
		lvIngredienti = (ListView) findViewById(R.id.lv_listaIng);
		
		lvIngredienti.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long idIng) {
				// id indica il numero dell'ingrediente che è stato selezionato
				Intent intent = new Intent(parent.getContext(), DettagliIngredienteActivity.class);
				intent.putExtra("idIng", idIng);
				startActivity(intent);
            }
        });
		
		
	}
	
	@Override
	protected void onResume() {
		// metodo che popola il layout
		super.onResume();
		updateLayout();
	}
	
	/** Metodo chiamato per prelevare la lista degli ingredienti*/
	private void popolaListaIng() {
		
		//ContentValues getListaIngContent = new ContentValues();
		ContentResolver resolver = getContentResolver();
		Uri uri = SoapAPPContract.RicetteSaponiMagazzino.CONTENT_URI;
		Cursor cursore = resolver.query(uri, null, null, null, null);
		
		int indiceColonna;
		int tipoColonna;
		String rigaIng;
		
		
		if (cursore != null) {
			cursore.moveToFirst();
			while (!cursore.isAfterLast()) {
				rigaIng = "";
				
				for(int i = 0; i < COLONNE_RICETTE_SAPONI_MAGAZZINO.length; i++)
				{
					indiceColonna = cursore.getColumnIndex(COLONNE_RICETTE_SAPONI_MAGAZZINO[i]);
					tipoColonna = cursore.getType(indiceColonna);
					
					switch(tipoColonna) {
					case Cursor.FIELD_TYPE_BLOB:
						rigaIng = rigaIng + "BLOB";
						break;

					case Cursor.FIELD_TYPE_FLOAT:
						float dvaloreColonna = cursore.getFloat(indiceColonna);
						rigaIng = rigaIng + dvaloreColonna + " ";
						break;

					case Cursor.FIELD_TYPE_INTEGER:
						int ivaloreColonna = cursore.getInt(indiceColonna);
						rigaIng = rigaIng + ivaloreColonna + " ";
						break;

					case Cursor.FIELD_TYPE_STRING:
						String svaloreColonna = (String) cursore
								.getString(indiceColonna);
						rigaIng = rigaIng + svaloreColonna + " ";
						break;

					case Cursor.FIELD_TYPE_NULL:
						rigaIng = rigaIng + " ";
						break;

					default:
						rigaIng = rigaIng + "campo mancante ";
					}

					listaIngredienti.add(rigaIng);
					cursore.moveToNext();
				}
			}
		} else {
			listaIngredienti.add(getString(R.string.errore_prelievo_lista_ingredienti));
		}
		
		cursore.close();
	}

	/** Metodo chiamato per popolare ogni componente del layout */
	private void updateLayout() {
		
		// popolo un arrayAdapter di stringhe con la lista degli ingredienti e carico nella listView
		ArrayAdapter<String> ingAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaIngredienti);
		lvIngredienti.setAdapter(ingAdapter);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visualizza_ingredienti, menu);
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

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

/*
 * ESTENDERE LA CLASSE NON CON ACTIVITY MA CON ListActivity
 * DA RISCRIVERE TUTTA VISUALIZZANDO UNA LISTA DI ELEMENTI PRESENTI NELLA TABELLA RICETTESAPONIMAGAZZINO
 * USANDO SIMPLECURSORADAPTER
 */
public class VisualizzaIngredientiActivity extends Activity {

	private static final String[] COLONNE_RICETTE_SAPONI_MAGAZZINO = new String[] {
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE };

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

		lvIngredienti
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long idIng) {
						// id indica il numero dell'ingrediente che è stato
						// selezionato
						Intent intent = new Intent(parent.getContext(),
								DettagliIngredienteActivity.class);
						intent.putExtra("idIng", idIng);
						startActivity(intent);
					}
				});

	}

	@Override
	// metodo che popola il layout
	protected void onResume() {
		super.onResume();
		// updateLayout();
	}

	/** Metodo chiamato per prelevare la lista degli ingredienti */
	private void popolaListaIng() {

		ContentResolver resolver = getContentResolver();

		Uri uri = SoapAPPContract.RicetteSaponiMagazzino.CONTENT_URI;

		Cursor cursore = resolver.query(uri, COLONNE_RICETTE_SAPONI_MAGAZZINO,
				null, null,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME
						+ " ASC"); // l'ultimo parametro è la colonna su cui
									// fare l'ordinamento. Se si passa null il
									// provider, per adesso, fornisce
									// l'ordinamento di default che è sulla
									// colonna _ID. non essendo presente la
									// colonna _ID tra nella projection la query
									// da errore. COMUNQUE SAREBBE DA RECUPERARE
									// ANCHE L'_ID PERCHè ALTRIMENTI COME FAI A
									// SALTARE SUL DETTAGLIO DELL'INGREDIENTE
									// SELEZIONATO

		// DA RISCRIVERE TUTTO DA QUA IN POI USANDO SIMPLECURSORADAPTER E
		// ListView - setListAdapter(adapter);
		int indiceColonna;
		int tipoColonna;
		String rigaIng;

		if (cursore != null) {
			cursore.moveToFirst();
			while (!cursore.isAfterLast()) {
				rigaIng = "";

				for (int i = 0; i < COLONNE_RICETTE_SAPONI_MAGAZZINO.length; i++) {
					indiceColonna = cursore
							.getColumnIndex(COLONNE_RICETTE_SAPONI_MAGAZZINO[i]);
					tipoColonna = cursore.getType(indiceColonna);

					switch (tipoColonna) {
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
			cursore.close();
			listaIngredienti
					.add(getString(R.string.errore_prelievo_lista_ingredienti));

		}

		ArrayAdapter<String> ingAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listaIngredienti);
		lvIngredienti.setAdapter(ingAdapter);
	}

	/**
	 * Metodo chiamato per popolare ogni componente del layout 
	 * private void updateLayout() {
	 * 
	 * // popolo un arrayAdapter di stringhe con la lista degli ingredienti e //
	 * carico nella listView
	 * 
	 * }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visualizza_ingredienti, menu);
		return true;
	}

	// TODO
	// METODI DA IMPLEMENTARE

	@Override
	// Metodo chiamato quando l’activity diventa visibile all’utente
	protected void onStart() {
		super.onStart();
	}

	@Override
	// Metodo chiamato quando l’attuale activity viene messa in pausa e
	// un’activity precedente viene ripristinata
	protected void onPause() {
		super.onPause();
	}

	@Override
	// Metodo chiamato quando l’activity non è più visibile all’utente
	protected void onStop() {
		super.onStop();
	}

	@Override
	// Metodo chiamato prima che l’activity venga distrutta, manualmente o
	// dal sistema operativo per liberare memoria
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	// Metodo chiamato dopo che l’activity era stata stoppata e quando è
	// pronta ad essere ripristinata
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	// Metodo utile per eliminare dati salvati temporaneamente prima che si
	// cambi activity
	public void onBackPressed() {
		super.onBackPressed();
	}

	/*
	 * 
	 * @Override protected void onSaveInstanceState(Bundle outState) { }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) { }
	 * protected boolean onKeyDown(int keyCode, KeyEvent event) { } protected
	 * void onActivityResult(int requestCode, int resultCode, Intent data) { }
	 * 
	 * @Override public void onProviderDisabled(String provider) { }
	 * 
	 * @Override public void onProviderEnabled(String provider) { }
	 * 
	 * @Override public void onStatusChanged(String provider, int status, Bundle
	 * extras) { }
	 */

}

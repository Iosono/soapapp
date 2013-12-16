package it.soapapp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private ArrayList<ContentProviderOperation> popolamentoIniziale = new ArrayList<ContentProviderOperation>();

	private ContentProviderResult[] risultatoPopolamentoIniziale = null;

	/*
	 * CREARE GLI OGGETTI ContentValues DA FILE XML private static ContentValues
	 * createInitialContentValues(Uri initialUri) {
	 * 
	 * Date date = new Date(); SimpleDateFormat sdf = new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); String formattedDate =
	 * sdf.format(new Timestamp(date.getTime()));
	 * 
	 * ContentValues insertRicetteSaponi = new ContentValues();
	 * 
	 * insertRicetteSaponi.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
	 * "Prima Ricetta Prova"); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS, "Primo Alias Prova");
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
	 * "Prima Descrizione Prova"); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
	 * "Prima Patch file Sistem Prova"); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA, 1000);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA, 330);
	 * insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA, 0.0);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA, 0.0);
	 * insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
	 * 0.0); insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA
	 * , 0.0); insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA
	 * , 0.0); insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
	 * 0.0); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA, 0.0);
	 * insertRicetteSaponi
	 * .put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
	 * 0.0); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA, 0.0);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
	 * "Prima Nota Prova"); insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE, 0);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE, 0);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE, formattedDate);
	 * insertRicetteSaponi.put(
	 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE,
	 * formattedDate); return insertRicetteSaponi; }
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		popolamentoIniziale
				.add(ContentProviderOperation
						.newInsert(SoapAPPContract.RicetteSaponi.CONTENT_URI)
						.withValues(
								createInitialContentValues(SoapAPPContract.RicetteSaponi.CONTENT_URI)) // DA
																										// FORNIRE
																										// OGGETTO
																										// ContentValues
																										// CREATO
																										// DA
																										// FILE
																										// XML
						.withYieldAllowed(true).build());

		try {
			risultatoPopolamentoIniziale = getContentResolver().applyBatch(
					SoapAPPContract.AUTHORITY, popolamentoIniziale);
		} catch (RemoteException e) {
			// do s.th.
		} catch (OperationApplicationException e) {
			// do s.th.
		}
	}

	/** Metodo chiamato quando si preme il bottone Magazzino */
	public void startMagazzino(View view) {
		Intent intent = new Intent(this, MagazzinoActivity.class);
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	/** Metodo chiamato quando si preme il bottone Ricette */
	public void startRicette(View view) {
		Intent intent = new Intent(this, RicetteActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// TODO
	// METODI DA IMPLEMENTARE
	// Metodo chiamato quando l’activity diventa visibile all’utente
	@Override
	protected void onStart() {
		super.onStart();
	}

	// Metodo chiamato quando l’activity inizia ad interagire con l’utente
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	// Metodo chiamato quando l’attuale activity viene messa in pausa e
	// un’activity precedente viene ripristinata
	protected void onPause() {
		super.onPause();
	}

	// Metodo chiamato quando l’activity non è più visibile all’utente
	@Override
	protected void onStop() {
		super.onStop();
	}

	// Metodo chiamato prima che l’activity venga distrutta, manualmente o
	// dal sistema operativo per liberare memoria
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Metodo chiamato dopo che l’activity era stata stoppata e quando è
	// pronta ad essere ripristinata
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	// Metodo utile per eliminare dati salvati temporaneamente prima che si
	// cambi activity
	@Override
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

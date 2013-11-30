package it.soapapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.view.Menu;

public class VisualizzaIngredientiActivity extends Activity {

	private ArrayList<String> listaIngredienti = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizza_ingredienti);
		
		popolaListaIng();
	}
	
	@Override
	protected void onResume() {
		// metodo che popola il layout
		updateLayout();
		super.onResume();
	}
	
	/** Metodo chiamato per prelevare la lista degli ingredienti*/
	private void popolaListaIng() {
		
		ContentValues getListaIngContent = new ContentValues();
		
		
	}
	
	/** Metodo chiamato per popolare ogni componente del layout */
	private void updateLayout()	{
		
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

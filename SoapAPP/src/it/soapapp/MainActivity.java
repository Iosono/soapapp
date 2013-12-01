package it.soapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	//private Button btnAddIngredient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * MOMENTANEAMENTE COMMENTATO // riferimenti agli oggetti del layout
		 * btnAddIngredient = (Button) findViewById(R.id.btn_insertStore);
		 * 
		 * // aggiungo un ingrediente al magazzino (gestione del click sul
		 * bottone btn_insert_store) btnAddIngredient.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { Intent addIngredient = new
		 * Intent(view.getContext(), StoreActivity.class);
		 * startActivity(addIngredient); } });
		 */

	}

	/*
	// Metodo chiamato quando si preme il bottone Store
	public void startStoreActivity(View view) {
		Intent intent = new Intent(this, ReStoreActivity.class);
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	*/

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
	
	@Override
	protected void onStart() {
		// Metodo chiamato quando l’activity diventa visibile all’utente
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		// Metodo chiamato quando l’activity inizia ad interagire con l’utente
		super.onResume();
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

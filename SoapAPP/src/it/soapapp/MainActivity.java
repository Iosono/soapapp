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

	/** Metodo chiamato quando si preme il bottone Store */
	public void startStoreActivity(View view) {
		Intent intent = new Intent(this, StoreActivity.class);
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	/** Metodo chiamato quando si preme il bottone Magazzino */
	public void startMagazzino(View view) {
		Intent intent = new Intent(this, MagazzinoActivity.class);
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

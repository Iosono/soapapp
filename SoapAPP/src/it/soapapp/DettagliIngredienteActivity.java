package it.soapapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DettagliIngredienteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dettagli_ingrediente);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dettagli_ingrediente, menu);
		return true;
	}

}

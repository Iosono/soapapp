package it.soapapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MagazzinoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magazzino);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.magazzino, menu);
		return true;
	}

}

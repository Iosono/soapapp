package it.soapapp;

import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class StoreActivity extends Activity {

	private List<String> listIngType;
	private ListView lvIngType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		
		// popola la lista contenente le tipologie di ingredienti
		populateList();
		
		lvIngType = (ListView) findViewById(R.id.lv_ingredientType);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
		
	}

	private void populateList() {
		listIngType.add("Farina");
		listIngType.add("Olio");
		listIngType.add("Liquido");
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

package it.soapapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StoreActivity extends Activity {

	/*
	private static final String[] RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION = new String[] {
		SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE };
	*/
	
	private SimpleDateFormat dataFormat;
	private ArrayList<String> listIngType = new ArrayList<String>();
	//private ListView lvIngType;
	
	// riferimenti agli oggetti del layout
	private Spinner spIngType;

	// variabili che memorizzano il valore dei campi Store
	private String ingName, ingAlias, ingDesc, ingShop, ingNotes;
	private double ingGrossPrice, ingNetPrice, ingPriceGram, ingGrossWeight, ingNetWeight;
	private Date ingBuyDate, ingMaturityDate;

	private EditText etIngName, etIngAlias, etIngDesc, etIngGrossPrice,
			etIngNetPrice, etIngPriceGram, etIngGrossWeight, etIngNetWeight,
			etIngBuyDate, etIngMaturityDate, etIngShop, etIngNotes;

	/*
	private EditText tvIngName, tvIngAlias, tvIngDesc, tvIngGrossPrice, tvIngNetPrice, tvIngPriceGram,
		tvIngGrossWeight, tvIngNetWeight, tvIngBuyDate, tvIngMaturityDate, tvIngShop, tvIngNotes;
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
				
		//lvIngType = (ListView) findViewById(R.id.lv_ingredientType);
		
		// EditText contenenti i valori inseriti dall'utente
		spIngType = (Spinner) findViewById(R.id.sp_ingredientType);
		etIngName = (EditText) findViewById(R.id.et_ingName);
		etIngAlias = (EditText) findViewById(R.id.et_ingAlias);
		etIngDesc = (EditText) findViewById(R.id.et_ingDesc);
		etIngGrossPrice = (EditText) findViewById(R.id.et_ingGrossPrice);
		etIngNetPrice = (EditText) findViewById(R.id.et_ingNetPrice);
		etIngPriceGram = (EditText) findViewById(R.id.et_ingPriceGram);
		etIngGrossWeight = (EditText) findViewById(R.id.et_ingGrossWeight);
		etIngNetWeight = (EditText) findViewById(R.id.et_ingNetWeight);
		etIngBuyDate = (EditText) findViewById(R.id.et_ingBuyDate);
		etIngMaturityDate = (EditText) findViewById(R.id.et_ingMaturityDate);
		etIngShop = (EditText) findViewById(R.id.et_ingShop);
		etIngNotes = (EditText) findViewById(R.id.et_ingNotes);
		
		
		// formato della data
		dataFormat = new SimpleDateFormat("yyyy/MM/dd");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();

		// popola la lista contenente le tipologie di ingredienti
		populateIngType();
	}
	
	/** Metodo chiamato per prelevare nella tabella "tipi ingredienti" i valori: tipi ingredienti*/
	private void populateIngType() {
		
		ContentResolver resolver = getContentResolver();
		
		Uri uri = SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI;
		
		Cursor cursor = resolver.query(uri, null, null, null, null);

		int indexColumn;
		//String nameColumn;
		int typeColumn;
		String rowType;
		
		/*
		//RISCRITTO IL CICLO PER CONCATENARE TUTTI GLI ATTRIBUTI DI UNA TUPLA IN UN UNICA STRINGA
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				rowType = "";
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					indexColumn = cursor.getColumnIndex(RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION[i]);
					//nameColumn = mCursor.getColumnName(indexColumn);
					typeColumn = cursor.getType(indexColumn);

					switch (typeColumn) {
					case Cursor.FIELD_TYPE_BLOB:
						rowType = rowType + "BLOB";
						break;

					case Cursor.FIELD_TYPE_FLOAT:
						float dvaloreColumn = cursor.getFloat(indexColumn);
						rowType = rowType + dvaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_INTEGER:
						int ivaloreColumn = cursor.getInt(indexColumn);
						rowType = rowType + ivaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_STRING:
						String svaloreColumn = (String) cursor.getString(indexColumn);
						rowType = rowType + svaloreColumn + " ";
						break;

					case Cursor.FIELD_TYPE_NULL:
						rowType = rowType + " ";
						break;

					default:
						rowType = rowType + "";
					}
				}
				listIngType.add(rowType);
				cursor.moveToNext();
			}
			*/
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				indexColumn = 1;
				typeColumn = cursor.getType(indexColumn);
				if(typeColumn == Cursor.FIELD_TYPE_STRING){
					listIngType.add((String) cursor.getString(indexColumn));
				} else {
					listIngType.add("ERRORE NEL PRELIEVO DEL NOME DELL'INGREDIENTE");
				}
				cursor.moveToNext();
			}
		} else {
			cursor.close();
			rowType = "NESSUNA RIGA ESTRATTA DALLA TABELLA TIPI INGREDIENTI";
			listIngType.add(rowType);
		}
		
		
		// se non funziona provare:
		// ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listIngType);
		
		// popolo un arrayAdapter di stringhe con la lista precedentemente usata
		String[] arrIngType = new String[listIngType.size()];
		listIngType.toArray(arrIngType);
		ArrayAdapter<String> ingAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrIngType);		
		spIngType.setAdapter(ingAdapter);
	}
	
	/** Metodo chiamato quando si preme il bottone Salva */
	public void saveStore(View view)
	{
		// TODO
		// FINIRE DI GESTIRE TUTTE LE ECCEZIONI
		try {
		ingName = etIngName.getText().toString();
		ingAlias = etIngAlias.getText().toString();
		ingDesc = etIngDesc.getText().toString();
		ingGrossPrice = Double.parseDouble(etIngGrossPrice.getText().toString());
		ingNetPrice = Double.parseDouble(etIngNetPrice.getText().toString());
		ingPriceGram = Double.parseDouble(etIngPriceGram.getText().toString());
		ingGrossWeight = Double.parseDouble(etIngGrossWeight.getText().toString());
		ingNetWeight = Double.parseDouble(etIngNetWeight.getText().toString());
		ingBuyDate = dataFormat.parse(etIngBuyDate.getText().toString());
		ingMaturityDate = dataFormat.parse(etIngMaturityDate.getText().toString());
		ingShop = etIngShop.getText().toString();
		ingNotes = etIngNotes.getText().toString();
		} catch (ParseException e) {
			Toast.makeText(this, "Errore nel prelevare i valori della data", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
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

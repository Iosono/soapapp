package it.soapapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class AggiungiIngredienteActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	/*
	 * private static final String[] RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION
	 * = new String[] { SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
	 * SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
	 * SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
	 * SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
	 * SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
	 * SoapAPPContract
	 * .RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE };
	 */

	private static final String[] COLONNA_TIPI_INGREDIENTI = new String[] { SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME };

	private static final String[] COLONNE_COEFFICIENTI_SAPONIFICAZIONE = new String[] {
			SoapAPPContract.CoefficientiSaponificazione._ID,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH };

	private SimpleDateFormat dataFormat;
	private ArrayList<String> listaTipiIng = new ArrayList<String>();
	private ArrayList<String> listaCoeffSap = new ArrayList<String>();

	// cursori
	private Cursor cursoreTipi, cursoreCoeff;
	private SimpleCursorAdapter cAdapterCoeff;

	// riferimenti agli oggetti del layout
	private Spinner spTipoIng, spCoeffSapon;
	private EditText etNomeIng, etAliasIng, etDescrizioneIng, etCostoLordoIng,
			etCostoNettoIng, etCostoGrammoIng, etPesoLordoIng, etPesoNettoIng,
			etNegozioIng, etNoteIng;

	// variabili che memorizzano il valore dei campi Store
	private String nomeIng, aliasIng, descrizioneIng, negozioIng, noteIng;
	private double costoLordoIng, costoNettoIng, costoGrammoIng, pesoLordoIng,
			pesoNettoIng;

	// private Date ingBuyDate, ingMaturityDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aggiungi_ingrediente);

		// EditText contenenti i valori inseriti dall'utente
		spTipoIng = (Spinner) findViewById(R.id.sp_ingredientType);
		spCoeffSapon = (Spinner) findViewById(R.id.sp_coeffSapon);
		etNomeIng = (EditText) findViewById(R.id.et_nomeIng);
		etAliasIng = (EditText) findViewById(R.id.et_aliasIng);
		etDescrizioneIng = (EditText) findViewById(R.id.et_descrizioneIng);
		etCostoLordoIng = (EditText) findViewById(R.id.et_costoLordoIng);
		etCostoNettoIng = (EditText) findViewById(R.id.et_costoNettoIng);
		etCostoGrammoIng = (EditText) findViewById(R.id.et_costoGrammoIng);
		etPesoLordoIng = (EditText) findViewById(R.id.et_pesoLordoIng);
		etPesoNettoIng = (EditText) findViewById(R.id.et_pesoNettoIng);
		// etIngBuyDate = (EditText) findViewById(R.id.et_ingBuyDate);
		// etIngMaturityDate = (EditText) findViewById(R.id.et_ingMaturityDate);
		etNegozioIng = (EditText) findViewById(R.id.et_nomeNegozioIng);
		etNoteIng = (EditText) findViewById(R.id.et_noteIng);

		// formato della data
		dataFormat = new SimpleDateFormat("yyyy/MM/dd");

		// metodo che popola la lista contenente le tipologie di ingredienti
		popolaTipiIng();

		// ATTENZIONE: DA ERRORE
		// RENDERE SELEZIONABILE LO SPINNER SOLO QUANDO È SELEZIONATO IL GRASSO
		// metodo che popola la lista dei coefficienti di saponificazione
		popolaCoeffSapon();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// metodo che popola il layout
		updateLayout();
	}

	@Override
	protected void onPause() {
		// Metodo chiamato quando l’attuale activity viene messa in pausa e
		// un’activity precedente viene ripristinata
		super.onPause();
		cursoreCoeff.close();
		cursoreTipi.close();
	}

	/** Metodo chiamato per popolare ogni componente del layout */
	private void updateLayout() {

	}

	/**
	 * Metodo chiamato per prelevare nella tabella "tipi ingredienti" i valori:
	 * tipi ingredienti
	 */
	private void popolaTipiIng() {

		ContentResolver resolverTipi = getContentResolver();

		Uri uriTipi = SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI;

		// LEGGERE IL METODO QUERY DELLA CLASSE SoapAPPProvider X CAPIRE COSA
		// SONO GLI ULTIMI TRE PARAMETRI. IN QUESTO CASO NON DEVONO ESSERE
		// VALORIZZATI E VA BENE COSì
		cursoreTipi = resolverTipi.query(uriTipi, COLONNA_TIPI_INGREDIENTI,
				null, null,
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME
						+ " ASC");
		/*
		 * DA RISCRIVERE USANDO SIMPLECURSORADAPTER DA RISCRIVERE USANDO
		 * SIMPLECURSORADAPTER DA RISCRIVERE USANDO SIMPLECURSORADAPTER DA
		 * RISCRIVERE USANDO SIMPLECURSORADAPTER DA RISCRIVERE USANDO
		 * SIMPLECURSORADAPTER
		 */
		int indiceColonna;
		int tipoColonna;

		if (cursoreTipi != null) {
			cursoreTipi.moveToFirst();
			while (!cursoreTipi.isAfterLast()) {
				indiceColonna = cursoreTipi
						.getColumnIndex(COLONNA_TIPI_INGREDIENTI[0]);
				tipoColonna = cursoreTipi.getType(indiceColonna);
				if (tipoColonna == Cursor.FIELD_TYPE_STRING) {
					listaTipiIng.add((String) cursoreTipi
							.getString(indiceColonna));
				} else {
					listaTipiIng
							.add(getString(R.string.errore_prelievo_riga_tipo_ing));
				}
				cursoreTipi.moveToNext();
			}
		} else {
			listaTipiIng.add(getString(R.string.errore_prelievo_tipi_ing));
			cursoreTipi.close();
		}

		// popolo un arrayAdapter di stringhe con la lista dei tipi ingrediente
		// e carico nello spinner
		ArrayAdapter<String> ingAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listaTipiIng);
		spTipoIng.setAdapter(ingAdapter);
	}

	/** Metodo chiamato per prelevare i valori dei coefficienti saponificazione */
	private void popolaCoeffSapon() {

		ContentResolver resolverCoeff = getContentResolver();

		Uri uriCoeff = SoapAPPContract.CoefficientiSaponificazione.CONTENT_URI;

		cursoreCoeff = resolverCoeff.query(uriCoeff,
				COLONNE_COEFFICIENTI_SAPONIFICAZIONE, null, null, null);

		int[] spinnerCoeff = new int[] { R.id.sp_coeffSapon }; // DEVE ESSERE
																// UNA TEXTVIEW
																// E NON
																// SPINNER.
																// ESSENDO TRE
																// LE COLONNE DA
																// GESTIRE,
																// DEVONO ESSERE
																// TRE ANCHE LE
																// TEXTVIEW DA
																// FORNIRE
		/*
		 * IN REALTA LA COLONNA _ID NON ANDREBBE FATTA VEDERE A VIDEO PERCIò LE
		 * TEXT VIEW DA FORNIRE A cAdapterCoeff SONO SOLO DUE. BISOGNA PERò
		 * RECUPERARE IL CORRISPONDENTE VALORE _ID PER IL NOME DEL COEFFICIENTE
		 * DI SAPONIFICAZIONE SELEZIONATO NELLO SPINNER
		 */
		getLoaderManager().initLoader(0, null, this);

		cAdapterCoeff = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_spinner_item,
				cursoreCoeff,
				new String[] {
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH },
				spinnerCoeff, 0);

		cAdapterCoeff
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spCoeffSapon.setAdapter(cAdapterCoeff);

		/*
		 * ContentResolver resolverCoeff = getContentResolver(); Uri uri =
		 * SoapAPPContract.CoefficientiSaponificazione.CONTENT_URI; cursoreCoeff
		 * = resolverCoeff.query(uri, null, null, null, null); int
		 * indiceColonna; int tipoColonna; String rigaIng;
		 * 
		 * if (cursoreCoeff != null) { cursoreCoeff.moveToFirst(); while
		 * (!cursoreCoeff.isAfterLast()) { rigaIng = "";
		 * 
		 * for(int i = 0; i < COLONNE_COEFFICIENTI_SAPONIFICAZIONE.length; i++)
		 * { indiceColonna =
		 * cursoreCoeff.getColumnIndex(COLONNE_COEFFICIENTI_SAPONIFICAZIONE[i]);
		 * tipoColonna = cursoreCoeff.getType(indiceColonna);
		 * 
		 * switch(tipoColonna) { case Cursor.FIELD_TYPE_BLOB: rigaIng = rigaIng
		 * + "BLOB"; break;
		 * 
		 * case Cursor.FIELD_TYPE_FLOAT: float dvaloreColonna =
		 * cursoreCoeff.getFloat(indiceColonna); rigaIng = rigaIng +
		 * dvaloreColonna + " "; break;
		 * 
		 * case Cursor.FIELD_TYPE_INTEGER: int ivaloreColonna =
		 * cursoreCoeff.getInt(indiceColonna); rigaIng = rigaIng +
		 * ivaloreColonna + " "; break;
		 * 
		 * case Cursor.FIELD_TYPE_STRING: String svaloreColonna = (String)
		 * cursoreCoeff.getString(indiceColonna); rigaIng = rigaIng +
		 * svaloreColonna + " "; break;
		 * 
		 * case Cursor.FIELD_TYPE_NULL: rigaIng = rigaIng + " "; break;
		 * 
		 * default: rigaIng = rigaIng + "campo mancante "; }
		 * 
		 * listaCoeffSap.add(rigaIng); cursoreCoeff.moveToNext(); } } } else {
		 * listaCoeffSap
		 * .add(getString(R.string.errore_prelievo_lista_ingredienti));
		 * cursoreCoeff.close();
		 * 
		 * }
		 * 
		 * // popolo un arrayAdapter di stringhe con la lista dei coefficenti
		 * saponificazione e carico nello spinner ArrayAdapter<String>
		 * coeffAdapter= new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, listaCoeffSap);
		 * spCoeffSapon.setAdapter(coeffAdapter);
		 */
	}

	// crea un nuovo loader dopo la chiamata initLoader()
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {
				SoapAPPContract.CoefficientiSaponificazione._ID,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH };
		CursorLoader cursorLoader = new CursorLoader(this,
				SoapAPPContract.CoefficientiSaponificazione.CONTENT_URI,
				projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cAdapterCoeff.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		cAdapterCoeff.swapCursor(null);
	}

	/** Metodo chiamato quando si preme il bottone Salva */
	public void salvaMagazzino(View view) {
		// TODO
		// FINIRE DI GESTIRE TUTTE LE ECCEZIONI
		// try {
		nomeIng = etNomeIng.getText().toString();
		aliasIng = etAliasIng.getText().toString();
		descrizioneIng = etDescrizioneIng.getText().toString();
		costoLordoIng = Double
				.parseDouble(etCostoLordoIng.getText().toString());
		costoNettoIng = Double
				.parseDouble(etCostoNettoIng.getText().toString());
		costoGrammoIng = Double.parseDouble(etCostoGrammoIng.getText()
				.toString());
		pesoLordoIng = Double.parseDouble(etPesoLordoIng.getText().toString());
		pesoNettoIng = Double.parseDouble(etPesoNettoIng.getText().toString());
		// ingBuyDate = dataFormat.parse(etIngBuyDate.getText().toString());
		// ingMaturityDate =
		// dataFormat.parse(etIngMaturityDate.getText().toString());
		negozioIng = etNegozioIng.getText().toString();
		noteIng = etNoteIng.getText().toString();
		// } catch (ParseException e) {
		// Toast.makeText(this, "Errore nel prelevare i valori della data",
		// Toast.LENGTH_SHORT).show();
		// e.printStackTrace();
		// }

		/*
		 * // Inserimento ingrediente nel magazzino ContentValues valori = new
		 * ContentValues(); ContentResolver resolver = getContentResolver(); Uri
		 * uri = SoapAPPContract.RicetteSaponiMagazzino.CONTENT_URI;
		 * 
		 * 
		 * Calendar date = Calendar.getInstance(); SimpleDateFormat formatta =
		 * new SimpleDateFormat("yyyy-MM-dd"); String dataOdierna =
		 * formatta.format(date.getTime()); int VALOREPROVA = 1;
		 * 
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_TIPO_INGREDIENTE_ID, VALOREPROVA);
		 * valori.put(SoapAPPContract
		 * .RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
		 * VALOREPROVA);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
		 * nomeIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
		 * aliasIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION
		 * , descrizioneIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
		 * "Path"); valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_COSTO_LORDO_INGREDIENTE, costoLordoIng);
		 * valori.put(SoapAPPContract
		 * .RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
		 * costoNettoIng); valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_COSTO_TARA_INGREDIENTE, costoLordoIng - costoNettoIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO, costoGrammoIng);
		 * valori.put(SoapAPPContract
		 * .RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
		 * pesoLordoIng); valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_PESO_NETTO_INGREDIENTE, pesoNettoIng);
		 * valori.put(SoapAPPContract
		 * .RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
		 * pesoLordoIng - pesoNettoIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE, "2013-01-01 01:01:01");
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino.
		 * COLUMN_NAME_NOME_NEGOZIO_ACQUISTO, negozioIng);
		 * valori.put(SoapAPPContract
		 * .RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
		 * "2014-01-01 01:01:01");
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_NOTE_INGREDIENTE, noteIng);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_MODIFICABILE, 1);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_CARICATO_UTENTE, 1);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_CREATE_DATE, dataOdierna);
		 * valori.put(SoapAPPContract.RicetteSaponiMagazzino
		 * .COLUMN_NAME_MODIFICATION_DATE, dataOdierna);
		 * 
		 * resolver.insert(uri, valori);
		 */

	}

	private void updateUI() {
		// TODO
		// aggiornare listView tipologia ingredienti, usare arrayAdapter
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aggiungi_ingrediente, menu);
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
	protected void onStop() {
		// Metodo chiamato quando l’activity non è più visibile all’utente
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// Metodo chiamato prima che l’activity venga distrutta, manualmente o
		// dal sistema operativo per liberare memoria
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// Metodo chiamato dopo che l’activity era stata stoppata e quando è
		// pronta ad essere ripristinata
		super.onRestart();
	}

	@Override
	public void onBackPressed() {
		// Metodo utile per eliminare dati salvati temporaneamente prima che si
		// cambi activity
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

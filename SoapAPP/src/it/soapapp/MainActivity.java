package it.soapapp;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = "";

	private ArrayList<ContentProviderOperation> popolamentoIniziale = new ArrayList<ContentProviderOperation>();

	private ContentProviderResult[] risultatoPopolamentoIniziale = null;

	private final Context fContext = (Context) this;

	private ContentValues _Values = null;

	private Resources res = null;

	private XmlResourceParser xmlRicetteSaponi = null;
	private XmlResourceParser xmlCoefficientiSaponificazione = null;
	private XmlResourceParser xmlRicetteSaponiTipiIngredienti = null;
	private XmlResourceParser xmlRicetteSaponiMagazzino = null;
	private XmlResourceParser xmlRicetteSaponiMagazzinoRicetta = null;

	private Cursor cursore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// SCRIVERE QUALCOSA PER CREARE LE TABELLE, ES QUERY GENERICA X OGNI
		// TABELLA.
		/*cursore = getContentResolver().query(
				SoapAPPContract.RicetteSaponi.CONTENT_URI, null, null, null,
				null);
		cursore = getContentResolver().query(
				SoapAPPContract.CoefficientiSaponificazione.CONTENT_URI, null,
				null, null, null);
		cursore = getContentResolver().query(
				SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI, null,
				null, null, null);
		cursore = getContentResolver().query(
				SoapAPPContract.RicetteSaponiMagazzino.CONTENT_URI, null, null,
				null, null);
		cursore = getContentResolver().query(
				SoapAPPContract.RicetteSaponiMagazzinoRicetta.CONTENT_URI,
				null, null, null, null);*/

		_Values = new ContentValues();
		// Get xml resource file
		res = fContext.getResources();

		// DA GESTIRE LA VERIFICA SE LA TABELLA E' GIA' POPOLATA. SE POPOLATA
		// DEVE SALTARE IL CARICAMENTO DA FILE XML

		// Open xml file per la tabella ricette_saponi
		xmlRicetteSaponi = res.getXml(R.xml.ricette_saponi_tuple);
		try {
			// Check for end of document
			int eventType = xmlRicetteSaponi.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Search for record tags
				if ((eventType == XmlPullParser.START_TAG)
						&& (xmlRicetteSaponi.getName().equals("record"))) {
					// Record tag found, now get values and insert record

					String _Name = xmlRicetteSaponi.getAttributeValue(null,
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME);
					String _Alias = xmlRicetteSaponi.getAttributeValue(null,
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS);
					String _Description = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION);
					String _Image = xmlRicetteSaponi.getAttributeValue(null,
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE);
					String _Tot_Grassi_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA);
					String _Tot_Liquidi_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA);
					String _Tot_Soda_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA);
					String _Sconto_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA);
					String _Tot_Soda_Sconto_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA);
					String _Tot_Costo_Ingredienti_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA);
					String _Tot_Costo_Manodopera_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA);
					String _Tot_Costo_Varie_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA);
					String _Tot_Costo_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA);
					String _Tot_Etti_Stimati_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA);
					String _Costo_Etto_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA);
					String _Note_Ricetta = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA);
					String _Modificabile = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE);
					String _Caricato_Utente = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE);
					String _Create_Date = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE);
					String _Modification_Date = xmlRicetteSaponi
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE);

					_Values.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
							_Name);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS,
							_Alias);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
							_Description);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
							_Image);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA,
							Integer.valueOf(_Tot_Grassi_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA,
							Integer.valueOf(_Tot_Liquidi_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA,
							Double.valueOf(_Tot_Soda_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA,
							Double.valueOf(_Sconto_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
							Double.valueOf(_Tot_Soda_Sconto_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA,
							Double.valueOf(_Tot_Costo_Ingredienti_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA,
							Double.valueOf(_Tot_Costo_Manodopera_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
							Double.valueOf(_Tot_Costo_Varie_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA,
							Double.valueOf(_Tot_Costo_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
							Double.valueOf(_Tot_Etti_Stimati_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA,
							Double.valueOf(_Costo_Etto_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
							_Note_Ricetta);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE,
							Integer.valueOf(_Modificabile));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE,
							Integer.valueOf(_Caricato_Utente));
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE,
							_Create_Date);
					_Values.put(
							SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE,
							_Modification_Date);

					popolamentoIniziale
							.add(ContentProviderOperation
									.newInsert(
											SoapAPPContract.RicetteSaponi.CONTENT_URI)
									.withValues(_Values).withYieldAllowed(true)
									.build());

				}
				eventType = xmlRicetteSaponi.next();
			}
		}
		// Catch errors
		catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			// Close the xml file
			xmlRicetteSaponi.close();
		}

		_Values.clear();

		// DA GESTIRE LA VERIFICA SE LA TABELLA E' GIA' POPOLATA. SE POPOLATA
		// DEVE SALTARE IL CARICAMENTO DA FILE XML

		// Open xml file per la tabella coefficienti_saponificazione
		xmlCoefficientiSaponificazione = res
				.getXml(R.xml.coefficienti_saponificazione_tuple);
		try {
			// Check for end of document
			int eventType = xmlCoefficientiSaponificazione.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Search for record tags
				if ((eventType == XmlPullParser.START_TAG)
						&& (xmlCoefficientiSaponificazione.getName()
								.equals("record"))) {
					// Record tag found, now get values and insert record

					String _Name = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME);
					String _Inci = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI);
					String _Koh_96_98 = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98);
					String _Koh_80 = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80);
					String _Naoh = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH);
					String _Note_Coeff = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF);
					String _Modificabile = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE);
					String _Caricato_Utente = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE);
					String _Create_Date = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE);
					String _Modification_Date = xmlCoefficientiSaponificazione
							.getAttributeValue(
									null,
									SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE);

					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
							_Name);
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI,
							_Inci);
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98,
							Double.valueOf(_Koh_96_98));
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80,
							Double.valueOf(_Koh_80));
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH,
							Double.valueOf(_Naoh));
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
							_Note_Coeff);
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE,
							Integer.valueOf(_Modificabile));
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE,
							Integer.valueOf(_Caricato_Utente));
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
							_Create_Date);
					_Values.put(
							SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE,
							_Modification_Date);

					popolamentoIniziale
							.add(ContentProviderOperation
									.newInsert(
											SoapAPPContract.CoefficientiSaponificazione.CONTENT_URI)
									.withValues(_Values).withYieldAllowed(true)
									.build());

				}
				eventType = xmlCoefficientiSaponificazione.next();
			}
		}
		// Catch errors
		catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			// Close the xml file
			xmlCoefficientiSaponificazione.close();
		}

		_Values.clear();

		// DA GESTIRE LA VERIFICA SE LA TABELLA E' GIA' POPOLATA. SE POPOLATA
		// DEVE SALTARE IL CARICAMENTO DA FILE XML

		// Open xml file per la tabella ricettesaponi_tipi_ingredienti
		xmlRicetteSaponiTipiIngredienti = res
				.getXml(R.xml.ricettesaponi_tipi_ingredienti_tuple);
		try {
			// Check for end of document
			int eventType = xmlRicetteSaponiTipiIngredienti.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Search for record tags
				if ((eventType == XmlPullParser.START_TAG)
						&& (xmlRicetteSaponiTipiIngredienti.getName()
								.equals("record"))) {
					// Record tag found, now get values and insert record

					String _Name = xmlRicetteSaponiTipiIngredienti
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME);
					String _Modificabile = xmlRicetteSaponiTipiIngredienti
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE);
					String _Caricato_Utente = xmlRicetteSaponiTipiIngredienti
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE);
					String _Create_Date = xmlRicetteSaponiTipiIngredienti
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE);
					String _Modification_Date = xmlRicetteSaponiTipiIngredienti
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE);

					_Values.put(
							SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
							_Name);
					_Values.put(
							SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
							Integer.valueOf(_Modificabile));
					_Values.put(
							SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
							Integer.valueOf(_Caricato_Utente));
					_Values.put(
							SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
							_Create_Date);
					_Values.put(
							SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE,
							_Modification_Date);

					popolamentoIniziale
							.add(ContentProviderOperation
									.newInsert(
											SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_URI)
									.withValues(_Values).withYieldAllowed(true)
									.build());

				}
				eventType = xmlRicetteSaponiTipiIngredienti.next();
			}
		}
		// Catch errors
		catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			// Close the xml file
			xmlRicetteSaponiTipiIngredienti.close();
		}

		_Values.clear();

		// DA GESTIRE LA VERIFICA SE LA TABELLA E' GIA' POPOLATA. SE POPOLATA
		// DEVE SALTARE IL CARICAMENTO DA FILE XML

		// Open xml file per la tabella ricettesaponi_magazzino
		xmlRicetteSaponiMagazzino = res
				.getXml(R.xml.ricettesaponi_magazzino_tuple);
		try {
			// Check for end of document
			int eventType = xmlRicetteSaponiMagazzino.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Search for record tags
				if ((eventType == XmlPullParser.START_TAG)
						&& (xmlRicetteSaponiMagazzino.getName()
								.equals("record"))) {
					// Record tag found, now get values and insert record

					String _Tipo_Ingrediente_Id = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID);
					String _CoefficienteSaponificazione_Id = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID);
					String _Name = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME);
					String _Alias = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS);
					String _Description = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION);
					String _Image = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE);
					String _Costo_Lordo_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE);
					String _Costo_Netto_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE);
					String _Costo_Tara_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE);
					String _Costo_Ingrediente_Grammo = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO);
					String _Peso_Lordo_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE);
					String _Peso_Netto_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE);
					String _Peso_Tara_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE);
					String _Data_Acquisto_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE);
					String _Nome_Negozio_Acquisto = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO);
					String _Data_Scadenza_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE);
					String _Note_Ingrediente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE);
					String _Modificabile = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE);
					String _Caricato_Utente = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE);
					String _Create_Date = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE);
					String _Modification_Date = xmlRicetteSaponiMagazzino
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE);

					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
							Integer.valueOf(_Tipo_Ingrediente_Id));
					if (_CoefficienteSaponificazione_Id.equals("NULL")) {
						_Values.putNull(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID);
					} else {
						_Values.put(
								SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
								Integer.valueOf(_CoefficienteSaponificazione_Id));
					}
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
							_Name);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
							_Alias);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
							_Description);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
							_Image);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
							Double.valueOf(_Costo_Lordo_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
							Double.valueOf(_Costo_Netto_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
							Double.valueOf(_Costo_Tara_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
							Double.valueOf(_Costo_Ingrediente_Grammo));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
							Double.valueOf(_Peso_Lordo_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
							Double.valueOf(_Peso_Netto_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
							Double.valueOf(_Peso_Tara_Ingrediente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
							_Data_Acquisto_Ingrediente);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
							_Nome_Negozio_Acquisto);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
							_Data_Scadenza_Ingrediente);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
							_Note_Ingrediente);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
							Integer.valueOf(_Modificabile));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
							Integer.valueOf(_Caricato_Utente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
							_Create_Date);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
							_Modification_Date);

					popolamentoIniziale
							.add(ContentProviderOperation
									.newInsert(
											SoapAPPContract.RicetteSaponiMagazzino.CONTENT_URI)
									.withValues(_Values).withYieldAllowed(true)
									.build());

				}
				eventType = xmlRicetteSaponiMagazzino.next();
			}
		}
		// Catch errors
		catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			// Close the xml file
			xmlRicetteSaponiMagazzino.close();
		}

		_Values.clear();

		// DA GESTIRE LA VERIFICA SE LA TABELLA E' GIA' POPOLATA. SE POPOLATA
		// DEVE SALTARE IL CARICAMENTO DA FILE XML

		// Open xml file per la tabella ricettesaponi_magazzino_ricetta
		xmlRicetteSaponiMagazzinoRicetta = res
				.getXml(R.xml.ricettesaponi_magazzino_ricetta_tuple);
		try {
			// Check for end of document
			int eventType = xmlRicetteSaponiMagazzinoRicetta.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// Search for record tags
				if ((eventType == XmlPullParser.START_TAG)
						&& (xmlRicetteSaponiMagazzinoRicetta.getName()
								.equals("record"))) {
					// Record tag found, now get values and insert record

					String _Ricettesaponi_Id = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID);
					String _Ricettesaponi_Magazzino_Id = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID);
					String _Percentuale_Grasso_Ricetta = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA);
					String _Peso_Ingrediente_Ricetta = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA);
					String _Soda_Grasso_Ricetta = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA);
					String _Costo_Ingrediente_Ricetta = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA);
					String _Modificabile = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE);
					String _Caricato_Utente = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE);
					String _Create_Date = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE);
					String _Modification_Date = xmlRicetteSaponiMagazzinoRicetta
							.getAttributeValue(
									null,
									SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE);

					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID,
							Integer.valueOf(_Ricettesaponi_Id));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID,
							Integer.valueOf(_Ricettesaponi_Magazzino_Id));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA,
							Double.valueOf(_Percentuale_Grasso_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA,
							Double.valueOf(_Peso_Ingrediente_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA,
							Double.valueOf(_Soda_Grasso_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA,
							Double.valueOf(_Costo_Ingrediente_Ricetta));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE,
							Integer.valueOf(_Modificabile));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE,
							Integer.valueOf(_Caricato_Utente));
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE,
							_Create_Date);
					_Values.put(
							SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE,
							_Modification_Date);

					popolamentoIniziale
							.add(ContentProviderOperation
									.newInsert(
											SoapAPPContract.RicetteSaponiMagazzinoRicetta.CONTENT_URI)
									.withValues(_Values).withYieldAllowed(true)
									.build());

				}
				eventType = xmlRicetteSaponiMagazzinoRicetta.next();
			}
		}
		// Catch errors
		catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			// Close the xml file
			xmlRicetteSaponiMagazzinoRicetta.close();
		}

		_Values.clear();

		try {
			// Comando per lanciare effettivamente il caricamento
			risultatoPopolamentoIniziale = getContentResolver().applyBatch(
					SoapAPPContract.AUTHORITY, popolamentoIniziale);
		} catch (RemoteException e) {
			// do s.th.
		} catch (OperationApplicationException e) {
			// do s.th.
		}

		// eliminare l'oggetto _Values
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
	// Metodo chiamato quando l�activity diventa visibile all�utente
	@Override
	protected void onStart() {
		super.onStart();
	}

	// Metodo chiamato quando l�activity inizia ad interagire con l�utente
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	// Metodo chiamato quando l�attuale activity viene messa in pausa e
	// un�activity precedente viene ripristinata
	protected void onPause() {
		super.onPause();
	}

	// Metodo chiamato quando l�activity non � pi� visibile all�utente
	@Override
	protected void onStop() {
		super.onStop();
	}

	// Metodo chiamato prima che l�activity venga distrutta, manualmente o
	// dal sistema operativo per liberare memoria
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Metodo chiamato dopo che l�activity era stata stoppata e quando �
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

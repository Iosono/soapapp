/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.soapapp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class SoapAPPProvider extends ContentProvider {
	// Used for debugging and logging
	private static final String TAG = "SoapAPPProvider";

	private static final String DATABASE_NAME = "soap_app.db";

	private static final int DATABASE_VERSION = 1;

	private static final String TYPE_DOUBLE = "Double";
	private static final String TYPE_INTEGER = "Integer";
	private static final String TYPE_STRING = "String";
	private static final String CONSTRAINT_NULL = "";
	private static final String CONSTRAINT_NOT_NULL = "NOT NULL";

	// Lista di tutti i nomi di colonna per la tabella RicetteSaponi
	private static final String[] READ_RICETTE_SAPONI_PROJECTION = new String[] {
			SoapAPPContract.RicetteSaponi._ID,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE,
			SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE };

	private static final String[] READ_COEFFICIENTI_SAPONIFICAZIONE_PROJECTION = new String[] {
			SoapAPPContract.CoefficientiSaponificazione._ID,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
			SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE };

	private static final String[] READ_RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION = new String[] {
			SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
			SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
			SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
			SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
			SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
			SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE };

	private static final String[] READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION = new String[] {
			SoapAPPContract.RicetteSaponiMagazzino._ID,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
			SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE };

	private static final String[] READ_RICETTE_SAPONI_MAGAZZINO_RICETTA_PROJECTION = new String[] {
			SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE };

	// Oggetti che servono per usare i metodi getReadableDatabase() o
	// getWritableDatabase() per farsi restituire un oggetto SQLiteDatabase
	private RicetteSaponiHelper mRicetteSaponiHelper;
	private CoefficientiSaponificazioneHelper mCoefficientiSaponificazioneHelper;
	private RicetteSaponiTipiIngredientiHelper mRicetteSaponiTipiIngredientiHelper;
	private RicetteSaponiMagazzinoHelper mRicetteSaponiMagazzinoHelper;
	private RicetteSaponiMagazzinoRicettaHelper mRicetteSaponiMagazzinoRicettaHelper;

	// Oggetti che servono per mappare i nomi delle colonne delle varie tabelle
	private static HashMap<String, String> ricetteSaponiProjectionMap;
	private static HashMap<String, String> coefficientiSaponificazioneProjectionMap;
	private static HashMap<String, String> ricetteSaponiTipiIngredientiProjectionMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoProjectionMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoRicettaProjectionMap;

	// Oggetti che servono per mappare il nome della colonna con il proprio tipo
	// dato
	private static HashMap<String, String> ricetteSaponiTypeMap;
	private static HashMap<String, String> coefficientiSaponificazioneTypeMap;
	private static HashMap<String, String> ricetteSaponiTipiIngredientiTypeMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoTypeMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoRicettaTypeMap;

	// Oggetti che servono per mappare il nome della colonna con il proprio
	// constraint not null DA FINIRE DI IMPLEMENTARE
	private static HashMap<String, String> ricetteSaponiCnstrNotNullMap;
	private static HashMap<String, String> coefficientiSaponificazioneCnstrNotNullMap;
	private static HashMap<String, String> ricetteSaponiTipiIngredientiCnstrNotNullMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoCnstrNotNullMap;
	private static HashMap<String, String> ricetteSaponiMagazzinoRicettaCnstrNotNullMap;

	/*
	 * Constants used by the Uri matcher to choose an action based on the
	 * pattern of the incoming URI
	 */
	private static final int URI_MATCH_RICETTESAPONI = 1;
	private static final int URI_MATCH_RICETTESAPONI_ID = 2;
	private static final int URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE = 3;
	private static final int URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID = 4;
	private static final int URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI = 5;
	private static final int URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID = 6;
	private static final int URI_MATCH_RICETTE_SAPONI_MAGAZZINO = 7;
	private static final int URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID = 8;
	private static final int URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA = 9;
	private static final int URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID = 10;

	private static final UriMatcher sUriMatcher;

	private Cursor queryCursor;

	/**
	 * A block that instantiates and sets static objects
	 */
	static {

		/*
		 * Creates and initializes the URI matcher
		 */
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponi.TABLE_NAME,
				URI_MATCH_RICETTESAPONI);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponi.TABLE_NAME + "/#",
				URI_MATCH_RICETTESAPONI_ID);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME,
				URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME + "/#",
				URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME,
				URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME + "/#",
				URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME,
				URI_MATCH_RICETTE_SAPONI_MAGAZZINO);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + "/#",
				URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
				SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME,
				URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA);

		sUriMatcher
				.addURI(SoapAPPContract.AUTHORITY,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME
								+ "/#",
						URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID);

		/*
		 * Creates and initializes a projection map that returns all columns
		 */

		// HashMap per la tabella RicetteSaponi
		ricetteSaponiProjectionMap = new HashMap<String, String>();

		ricetteSaponiProjectionMap.put(SoapAPPContract.RicetteSaponi._ID,
				SoapAPPContract.RicetteSaponi._ID);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA);

		ricetteSaponiProjectionMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA);

		ricetteSaponiProjectionMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA);

		ricetteSaponiProjectionMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA);

		ricetteSaponiProjectionMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA);

		ricetteSaponiProjectionMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE);

		ricetteSaponiProjectionMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE,
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE);

		// HashMap per la tabella CoefficientiSaponificazione
		coefficientiSaponificazioneProjectionMap = new HashMap<String, String>();

		coefficientiSaponificazioneProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione._ID,
				SoapAPPContract.CoefficientiSaponificazione._ID);

		coefficientiSaponificazioneProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME);

		coefficientiSaponificazioneProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98);

		coefficientiSaponificazioneProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80);

		coefficientiSaponificazioneProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE);

		coefficientiSaponificazioneProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE);

		// HashMap per la tabella RicetteSaponiTipiIngredienti
		ricetteSaponiTipiIngredientiProjectionMap = new HashMap<String, String>();

		ricetteSaponiTipiIngredientiProjectionMap.put(
				SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
				SoapAPPContract.RicetteSaponiTipiIngredienti._ID);

		ricetteSaponiTipiIngredientiProjectionMap.put(
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME);

		ricetteSaponiTipiIngredientiProjectionMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE);

		ricetteSaponiTipiIngredientiProjectionMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE);

		ricetteSaponiTipiIngredientiProjectionMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE);

		ricetteSaponiTipiIngredientiProjectionMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE);

		// HashMap per la tabella RicetteSaponiMagazzino
		ricetteSaponiMagazzinoProjectionMap = new HashMap<String, String>();

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino._ID,
				SoapAPPContract.RicetteSaponiMagazzino._ID);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID);

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME);

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS);

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION);

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE);

		ricetteSaponiMagazzinoProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE);

		ricetteSaponiMagazzinoProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE);

		// HashMap per la tabella RicetteSaponiMagazzinoRicetta
		ricetteSaponiMagazzinoRicettaProjectionMap = new HashMap<String, String>();

		ricetteSaponiMagazzinoRicettaProjectionMap.put(
				SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID,
				SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE);

		ricetteSaponiMagazzinoRicettaProjectionMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE);

		// TYPE TYPE TYPE TYPE
		// HashMap tra nome colonna e il relativo tipo dato per la tabella
		// RicetteSaponi
		ricetteSaponiTypeMap = new HashMap<String, String>();

		ricetteSaponiTypeMap.put(SoapAPPContract.RicetteSaponi._ID,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_ID);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_NAME);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_ALIAS);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_DESCRIPTION);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_IMAGE);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_GRASSI_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_LIQUIDI_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_SODA_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_SCONTO_RICETTA);

		ricetteSaponiTypeMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_SODA_SCONTO_RICETTA);

		ricetteSaponiTypeMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_COSTO_INGREDIENTI_RICETTA);

		ricetteSaponiTypeMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_COSTO_MANODOPERA_RICETTA);

		ricetteSaponiTypeMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_COSTO_VARIE_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_COSTO_RICETTA);

		ricetteSaponiTypeMap
				.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
						SoapAPPContract.RicetteSaponi.COLUMN_TYPE_TOT_ETTI_STIMATI_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_COSTO_ETTO_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_NOTE_RICETTA);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_MODIFICABILE);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_CARICATO_UTENTE);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_CREATE_DATE);

		ricetteSaponiTypeMap.put(
				SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE,
				SoapAPPContract.RicetteSaponi.COLUMN_TYPE_MODIFICATION_DATE);

		// HashMap tra nome colonna e il relativo tipo dato per la tabella
		// CoefficientiSaponificazione
		coefficientiSaponificazioneTypeMap = new HashMap<String, String>();

		coefficientiSaponificazioneTypeMap.put(
				SoapAPPContract.CoefficientiSaponificazione._ID,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_ID);

		coefficientiSaponificazioneTypeMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_NAME);

		coefficientiSaponificazioneTypeMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_INCI);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_KOH_96_98);

		coefficientiSaponificazioneTypeMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_KOH_80);

		coefficientiSaponificazioneTypeMap.put(
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH,
				SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_NAOH);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_NOTE_COEFF);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_MODIFICABILE);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_CARICATO_UTENTE);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_CREATE_DATE);

		coefficientiSaponificazioneTypeMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_TYPE_MODIFICATION_DATE);

		// HashMap tra nome colonna e il relativo tipo dato per la tabella
		// RicetteSaponiTipiIngredienti
		ricetteSaponiTipiIngredientiTypeMap = new HashMap<String, String>();

		ricetteSaponiTipiIngredientiTypeMap.put(
				SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_ID);

		ricetteSaponiTipiIngredientiTypeMap.put(
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_NAME);

		ricetteSaponiTipiIngredientiTypeMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_MODIFICABILE);

		ricetteSaponiTipiIngredientiTypeMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_CARICATO_UTENTE);

		ricetteSaponiTipiIngredientiTypeMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_CREATE_DATE);

		ricetteSaponiTipiIngredientiTypeMap
				.put(SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_TYPE_MODIFICATION_DATE);

		// HashMap tra nome colonna e il relativo tipo dato per la tabella
		// RicetteSaponiMagazzino
		ricetteSaponiMagazzinoTypeMap = new HashMap<String, String>();

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino._ID,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_ID);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_TIPO_INGREDIENTE_ID);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_COEFFICIENTESAPONIFICAZIONE_ID);

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_NAME);

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_ALIAS);

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_DESCRIPTION);

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_IMAGE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_COSTO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_COSTO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_COSTO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_COSTO_INGREDIENTE_GRAMMO);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_PESO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_PESO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_PESO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_DATA_ACQUISTO_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_NOME_NEGOZIO_ACQUISTO);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_DATA_SCADENZA_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_NOTE_INGREDIENTE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_MODIFICABILE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_CARICATO_UTENTE);

		ricetteSaponiMagazzinoTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
				SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_CREATE_DATE);

		ricetteSaponiMagazzinoTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_TYPE_MODIFICATION_DATE);

		// HashMap tra nome colonna e il relativo tipo dato per la tabella
		// RicetteSaponiMagazzinoRicetta
		ricetteSaponiMagazzinoRicettaTypeMap = new HashMap<String, String>();

		ricetteSaponiMagazzinoRicettaTypeMap.put(
				SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID,
				SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_ID);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_RICETTESAPONI_ID);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_RICETTESAPONI_MAGAZZINO_ID);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_PERCENTUALE_GRASSO_RICETTA);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_PESO_INGREDIENTE_RICETTA);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_SODA_GRASSO_RICETTA);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_COSTO_INGREDIENTE_RICETTA);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_MODIFICABILE);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_CARICATO_UTENTE);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_CREATE_DATE);

		ricetteSaponiMagazzinoRicettaTypeMap
				.put(SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_TYPE_MODIFICATION_DATE);

		// CONSTRAINT NOT NULL

		// HashMap tra nome colonna e il relativo constraint NOT NULL per la
		// tabella
		// RicetteSaponiMagazzino
		ricetteSaponiMagazzinoCnstrNotNullMap = new HashMap<String, String>();

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino._ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_ID);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_TIPO_INGREDIENTE_ID);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_COEFFICIENTESAPONIFICAZIONE_ID);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_NAME);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_ALIAS);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_DESCRIPTION);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_IMAGE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_COSTO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_COSTO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_COSTO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_COSTO_INGREDIENTE_GRAMMO);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_PESO_LORDO_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_PESO_NETTO_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_PESO_TARA_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_DATA_ACQUISTO_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_NOME_NEGOZIO_ACQUISTO);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_DATA_SCADENZA_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_NOTE_INGREDIENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_MODIFICABILE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_CARICATO_UTENTE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_CREATE_DATE);

		ricetteSaponiMagazzinoCnstrNotNullMap
				.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.RicetteSaponiMagazzino.COLUMN_CNSTR_NOT_NULL_MODIFICATION_DATE);

	}

	/**
	 * Classe per gestire la tabella ricettesaponi
	 */
	static class RicetteSaponiHelper extends SQLiteOpenHelper {

		RicetteSaponiHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/*
			 * Query DDL per creare la tabella ricettesaponi
			 */
			try {
				db.execSQL("CREATE TABLE IF NOT EXISTS "

						+ SoapAPPContract.RicetteSaponi.TABLE_NAME
						+ " ("
						+ SoapAPPContract.RicetteSaponi._ID
						+ " INTEGER PRIMARY KEY ASC,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME
						+ " TEXT UNIQUE,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS
						+ " TEXT UNIQUE,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION
						+ " TEXT DEFAULT NULL,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE
						+ " TEXT NOT NULL DEFAULT 'ImmagineRicettaStandar',"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA
						+ " INTEGER NOT NULL DEFAULT 0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA
						+ " INTEGER NOT NULL DEFAULT 0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA
						+ " REAL NOT NULL DEFAULT 0.0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA
						+ " TEXT DEFAULT 'note ricetta',"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE
						+ " INTEGER NOT NULL DEFAULT 0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE
						+ " INTEGER NOT NULL DEFAULT 0,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE
						+ " TEXT DEFAULT NULL,"
						+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE
						+ " TEXT DEFAULT NULL" + ");");
			} catch (SQLException e) {
				Log.e(DATABASE_NAME, e.toString());
			}

			/*
			 * Query DDL per creare gli indici della tabella ricettesaponi
			 */
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponi.NAME_RICETTESAPONI_IDX
					+ " ON " + SoapAPPContract.RicetteSaponi.TABLE_NAME + " ("
					+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME + " ASC);");

			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponi.ALIAS_RICETTESAPONI_IDX
					+ " ON " + SoapAPPContract.RicetteSaponi.TABLE_NAME + " ("
					+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS
					+ " ASC);");

			/*
			 * Query per popolare con una riga nella tabella ricettesaponi
			 */

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = sdf.format(new Timestamp(date.getTime()));

			ContentValues insertRicetteSaponi = new ContentValues();

			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME,
					"Prima Ricetta Prova");
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS,
					"Primo Alias Prova");
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION,
					"Prima Descrizione Prova");
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE,
					"Prima Patch file Sistem Prova");
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA,
							1000);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA,
							330);
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA,
					0.0);
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA,
					0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA,
							0.0);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA,
							0.0);
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA,
					"Prima Nota Prova");
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE, 0);
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE,
					0);
			insertRicetteSaponi.put(
					SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE,
					formattedDate);
			insertRicetteSaponi
					.put(SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE,
							formattedDate);
			try {
				long rowID = db.insertOrThrow(
						SoapAPPContract.RicetteSaponi.TABLE_NAME, null,
						insertRicetteSaponi);
			}

			catch (SQLiteException sql) {
				Log.e(TAG + " " + DATABASE_NAME, sql.toString());
			}
			/*
			 * Riscritto il codice per l'inserimento di due righe nella tabella
			 * RicetteSaponi db.execSQL("INSERT INTO " +
			 * SoapAPPContract.RicetteSaponi.TABLE_NAME + " (" +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA +
			 * ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA +
			 * ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA
			 * + ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA
			 * + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA
			 * + ", " + SoapAPPContract.RicetteSaponi.
			 * COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA + ", " +
			 * SoapAPPContract
			 * .RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA
			 * + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA +
			 * ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA
			 * + ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA +
			 * ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA +
			 * ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE +
			 * ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE
			 * + ", " + SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE +
			 * ", " +
			 * SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE +
			 * ") VALUES (\'Prima Ricetta Prova\', \'Primo Alias Prova\', \'Prima Descrizione Prova\', \'Prima Patch file Sistem Prova\', 1000, 330, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, \'Prima Nota Prova\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
			 * +
			 * "	(\'Seconda Ricetta Prova\', \'Seconda Alias Prova\', \'Seconda Descrizione Prova\', \'Seconda Patch file Sistem Prova\', 1000, 330, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, \'Seconda Nota Prova\', 0, 0, datetime(\'now\'), datetime(\'now\'));"
			 * );
			 */

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/*
			 * Query DDL per eliminare gli indici della tabella ricettesaponi
			 */
			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponi.NAME_RICETTESAPONI_IDX
					+ ";");

			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponi.ALIAS_RICETTESAPONI_IDX
					+ ";");

			/*
			 * Query DDL per eliminare lo schema della tabella ricettesaponi
			 */
			db.execSQL("DROP TABLE IF EXISTS "
					+ SoapAPPContract.RicetteSaponi.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/**
	 * Classe per gestire la tabella coefficienti_saponificazione
	 */
	static class CoefficientiSaponificazioneHelper extends SQLiteOpenHelper {

		CoefficientiSaponificazioneHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/*
			 * Query DDL per creare la tabella coefficienti_saponificazione
			 */
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ " ("
					+ SoapAPPContract.CoefficientiSaponificazione._ID
					+ " INTEGER PRIMARY KEY ASC,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME
					+ " TEXT DEFAULT 'NO NAME',"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI
					+ " TEXT DEFAULT 'NO INCI',"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE
					+ " TEXT DEFAULT NULL" + ");");

			/*
			 * Query DDL per creare gli indici della tabella
			 * coefficienti_saponificazione
			 */
			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.NAME_COEFFICIENTI_SAPONIFICAZIONE_IDX
					+ " ON "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ " ("
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME
					+ " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.INCI_COEFFICIENTI_SAPONIFICAZIONE_IDX
					+ " ON "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ " ("
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI
					+ " ASC);");

			/*
			 * Query per popolare la tabella coefficienti_saponificazione DA
			 * RISCRIVERE USANDO IL METODO INSERT E NON execSQL
			 */
			db.execSQL("INSERT INTO "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ " ("
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE
					+ ", "
					+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE
					+ ") VALUES (\'Aringa (olio)\', \'NO INCI Aringa\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ " (\'Bue (sego)\', \'NO INCI Bue\', 0.197, 0.246, 0.140, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ " (\'Burro Caprino - Ovino\', \'NO INCI Burro Caprino\', 0.234, 0.293, 0.167, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Burro Vaccino\', \'NO INCI Burro Vaccino\', 0.227, 0.284, 0.162, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Capra (sego)\', \'NO INCI Capra\', 0.194, 0.243, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cera Api\', \'NO INCI Api\', 0.095, 0.119, 0.068, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Emu (olio)\', \'NO INCI Emu\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Fegato di Merluzzo (olio)\', \'NO INCI Merluzzo\', 0.185, 0.231, 0.132, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Lana, grasso di (lanolina)\', \'NO INCI Lanolina\', 0.104, 0.130, 0.074, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Maiale (strutto)\', \'NO INCI Maiale\', 0.193, 0.241, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Oca (grasso)\', \'NO INCI Oca\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Pecora, Montone (sego)\', \'NO INCI Montone\', 0.194, 0.243, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Piede di Bue (olio)\', \'NO INCI Piede Bue\', 0.198, 0.248, 0.141, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Pollo (grasso)\', \'NO INCI Pollo\', 0.195, 0.244, 0.139, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Struzzo (olio, grasso)\', \'NO INCI Struzzo\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Albicocca, armellinaa\', \'Prunus Armeniaca\', 0.189, 0.236, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Arachidi\', \'Arachis ipogaea\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Argania\', \'Argania Spinosa\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Avocado\', \'Persea gratissima, P. americana\', 0.186, 0.233, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Borragine\', \'Borago Officinalis\', 0.188, 0.235, 0.134, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cacao\', \'Theobroma cacao\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Canapa\', \'Cannabis Sativa\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Canola\', \'Brassica napus, B. campestris\', 0.174, 0.218, 0.124, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Carnauba (cera)\', \'Copernicia prunifera, C. cerifera\', 0.075, 0.094, 0.053, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cartamo\', \'Carthamus tinctorius\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cartamo > 70% oleico\', \'Carthamus tinctorius2\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cera o sego del Giappone\', \'Rhus Succedanea\', 0.215, 0.269, 0.153, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cocco, copra\', \'Cocos nucifera\', 0.258, 0.323, 0.184, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cocco frazionato\', \'Caprylic, capric triglyceride\', 0.330, 0.413, 0.235, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cocco Vergine\', \'Cocos nucifera2\', 0.258, 0.323, 0.184, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Colza, Ravizzone\', \'Brassica napus, B. campestris, B.tournefortii\', 0.174, 0.218, 0.124, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Comino nero, Grano nero\', \'Nigella sativa\', 0.189, 0.236, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cotone\', \'Gossypium spp.\', 0.194, 0.243, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Cotono di Java, kapok\', \'Ceiba Pentandra\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Egoma, perilla\', \'Perilla Frutescens\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Enotera, Onagra\', \'Oenothera Biennis\', 0.187, 0.234, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Germe di Grano\', \'Triticum aestivum, T. durum\', 0.184, 0.230, 0.131, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Gingelly, Sesamo\', \'Sesamum Orientalis\', 0.187, 0.234, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Girasole\', \'Helianthus annuus\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Girasole > 75% oleico\', \'Helianthus annuus2\', 0.188, 0.235, 0.134, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Guizotia, semi del Niger\', \'Guizotia Abyssinica\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Jojoba\', \'Simmondsia Chinensis\', 0.083, 0.104, 0.059, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Karite\', \'Butyrospermum parkii, Vitellaria Paradoxa\', 0.179, 0.224, 0.128, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Lino\', \'Linum Usitatissimum\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Macadamia\', \'Macadamia integrifolia, M. ternifolia\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Mais\', \'Zea Mays\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Mandorle Dolci\', \'Prunus Amygdalus dulcis\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Mango - noccioli\', \'Mangifera indica\', 0.186, 0.233, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Marula\', \'Sclerocarya birrea\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Mowrah, bassia\', \'Madhuca latifolia, M. longifolia\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Nem, Neem\', \'Melia Azadirachta, Azadirachta Indica\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Nigella\', \'Nigella Sativa\', 0.189, 0.236, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Nocciole\', \'Corylus Avellana\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Noci\', \'Juglans Regia\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Noci Brasiliane\', \'Bertholletia Excelsa\', 0.193, 0.241, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Oiticica\', \'Licania Rigida\', 0.193, 0.241, 0.138, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Oliva, Sansa\', \'Olea Europaea\', 0.188, 0.235, 0.134, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Olivello Spinoso\', \'Hippophae Rhamnoides\', 0.195, 0.244, 0.139, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Onagra, Enotera\', \'Oenothera Biennis\', 0.187, 0.234, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Ouricouri (grasso), Cohune (olio)\', \'Orbignya Cohune\', 0.250, 0.313, 0.178, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Palma\', \'Elaeis Guineensis\', 0.199, 0.249, 0.142, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Palma - Noccioli\', \'Elaeis Guineensis\', 0.230, 0.288, 0.164, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Palma - Oleina\', \'Elaeis Guineensis\', 0.198, 0.248, 0.141, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Palma - Stearina\', \'Elaeis Guineensis\', 0.204, 0.255, 0.145, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Passiflora\', \'Passiflora Incarnata\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Perilla, Egoma\', \'Perilla Ocymoides\', 0.192, 0.240, 0.137, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Pesca\', \'Prunus Persica\', 0.191, 0.239, 0.136, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Pistacchio\', \'Pistacia Vera\', 0.189, 0.236, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Pongamia\', \'Pongamia Glabra\', 0.185, 0.231, 0.132, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Prugna\', \'Prunus Domestica\', 0.180, 0.225, 0.128, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Ravizzone, Colza\', \'Brassica Napus, B. Campestris, B. Tournefortii\', 0.174, 0.218, 0.124, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Ricino\', \'Ricinus Communis\', 0.180, 0.225, 0.128, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Ricino Solfonato\', \'Sulfonated castor oil\', 0.178, 0.223, 0.127, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Riso\', \'Oryza Sativa\', 0.183, 0.229, 0.130, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Sesamo\', \'Sesamum Indicum\', 0.187, 0.234, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Soia\', \'Glycine Max\', 0.190, 0.238, 0.135, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Vinaccioli, Semi d'Uva\', \'Vitis Vinifera\', 0.181, 0.226, 0.129, NULL, 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'Zucca\', \'Cucurbita Maxima\', 0.187, 0.234, 0.133, NULL, 0, 0, datetime(\'now\'), datetime(\'now\'));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/*
			 * Query DDL per eliminare gli indici della tabella
			 * coefficienti_saponificazione
			 */
			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.NAME_COEFFICIENTI_SAPONIFICAZIONE_IDX
					+ ";");

			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.INCI_COEFFICIENTI_SAPONIFICAZIONE_IDX
					+ ";");

			/*
			 * Query DDL per eliminare lo schema della tabella
			 * coefficienti_saponificazione
			 */
			db.execSQL("DROP TABLE IF EXISTS "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ ";");

			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/**
	 * Classe per gestire la tabella ricettesaponi_tipi_ingredienti
	 */
	static class RicetteSaponiTipiIngredientiHelper extends SQLiteOpenHelper {

		RicetteSaponiTipiIngredientiHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/*
			 * Query DDL per creare la tabella ricettesaponi_tipi_ingredienti
			 */
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiTipiIngredienti._ID
					+ " INTEGER PRIMARY KEY ASC,"
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME
					+ " TEXT UNIQUE,"
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE
					+ " TEXT DEFAULT NULL" + ");");

			/*
			 * Query DDL per creare gli indici della tabella
			 * ricettesaponi_tipi_ingredienti
			 */
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.NAME_RICETTE_SAPONI_TIPI_INGREDIENTI_IDX
					+ " ON "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME
					+ " ASC);");

			/*
			 * Query per popolare con due righe la tabella
			 * ricettesaponi_tipi_ingredienti DA RISCRIVERE USANDO IL METODO
			 * INSERT E NON execSQL
			 */
			db.execSQL("INSERT INTO "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME
					+ ", "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE
					+ ", "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE
					+ ", "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE
					+ ", "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE
					+ ") VALUES (\'GRASSO\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'LIQUIDO\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'FARINA\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'OLIO ESSENZIALE\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
					+ "	(\'ALCALE\', 0, 0, datetime(\'now\'), datetime(\'now\'));");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/*
			 * Query DDL per eliminare gli indici della tabella
			 * ricettesaponi_tipi_ingredienti
			 */
			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.NAME_RICETTE_SAPONI_TIPI_INGREDIENTI_IDX
					+ ";");

			/*
			 * Query DDL per eliminare lo schema della tabella
			 * ricettesaponi_tipi_ingredienti
			 */
			db.execSQL("DROP TABLE IF EXISTS "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME
					+ ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/**
	 * Classe per gestire la tabella ricettesaponi_magazzino
	 */
	static class RicetteSaponiMagazzinoHelper extends SQLiteOpenHelper {

		RicetteSaponiMagazzinoHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/*
			 * Query DDL per creare la tabella ricettesaponi_magazzino
			 */
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzino._ID
					+ " INTEGER PRIMARY KEY ASC,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID
					+ " INTEGER NOT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID
					+ " INTEGER,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME
					+ " TEXT DEFAULT 'Default Nome Ingrediente',"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS
					+ " TEXT DEFAULT 'Default Alias Ingrediente',"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE
					+ " TEXT NOT NULL DEFAULT 'ImmagineIngredienteStandar',"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE
					+ " TEXT DEFAULT 'note ingrediente',"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE
					+ " TEXT DEFAULT NULL,"
					+ " CONSTRAINT "
					+ SoapAPPContract.RicetteSaponiMagazzino.FK_COEFFSAPONIFICAZIONE
					+ " FOREIGN KEY ("
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID
					+ ")"
					+ " REFERENCES "
					+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME
					+ " ("
					+ SoapAPPContract.CoefficientiSaponificazione._ID
					+ ")"
					+ " ON DELETE RESTRICT"
					+ " ON UPDATE CASCADE,"
					+ " CONSTRAINT "
					+ SoapAPPContract.RicetteSaponiMagazzino.FK_TIPO_INGREDIENTE
					+ " FOREIGN KEY ("
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID
					+ ")" + " REFERENCES "
					+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME
					+ " (" + SoapAPPContract.RicetteSaponiTipiIngredienti._ID
					+ ")" + " ON DELETE RESTRICT" + " ON UPDATE CASCADE);");

			/*
			 * Query DDL per creare gli indici della tabella
			 * ricettesaponi_magazzino
			 */
			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.TIPO_INGREDIENTE_ID_RICETTESAPONI_MAGAZZINO_IDX
					+ " ON "
					+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID
					+ " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.COEFFSAPONIFICAZIONE_ID_RICETTESAPONI_MAGAZZINO_IDX
					+ " ON "
					+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID
					+ " ASC);");

			/*
			 * Query per popolare con una riga nella tabella
			 * ricettesaponi_magazzino_ricetta
			 */

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = sdf.format(new Timestamp(date.getTime()));

			ContentValues insertRicetteSaponiMagazzino = new ContentValues();
			// Popolo la prima tupla
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
							1);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID,
							57);
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
					"Olio Oliva Default");
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
					"Olio Oliva Default Alias");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
							"Olio Oliva Default Description");
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
					"Patch file Sistem Image Oliva");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
							5.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
							4.5);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
							0.5);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
							0.0045);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
							1100.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
							1000.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
							100.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
							"Negozio di Fiducia");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
							"Nota Olio Oliva Default");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
							0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
							0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
							formattedDate);
			try {
				long rowID = db.insertOrThrow(
						SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME,
						null, insertRicetteSaponiMagazzino);
			}

			catch (SQLiteException sql) {
				Log.e(TAG + " " + DATABASE_NAME, sql.toString());
			}

			insertRicetteSaponiMagazzino.clear();

			// Popolo la seconda tupla
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID,
							2);
			insertRicetteSaponiMagazzino
					.putNull(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID);
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME,
					"Acqua Default");
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS,
					"Acqua Default Alias");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION,
							"Acqua Default Description");
			insertRicetteSaponiMagazzino.put(
					SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE,
					"Patch file Sistem Image Acqua");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE,
							1.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE,
							0.9);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE,
							0.1);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO,
							0.0009);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE,
							1010.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE,
							1000.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE,
							10.0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO,
							"Negozio di Fiducia");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE,
							"Nota Acqua Default");
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE,
							0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE,
							0);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE,
							formattedDate);
			insertRicetteSaponiMagazzino
					.put(SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE,
							formattedDate);
			try {
				long rowID = db.insertOrThrow(
						SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME,
						null, insertRicetteSaponiMagazzino);
			}

			catch (SQLiteException sql) {
				Log.e(TAG + " " + DATABASE_NAME, sql.toString());
			}

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/*
			 * Query DDL per eliminare gli indici della tabella
			 * ricettesaponi_magazzino
			 */
			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.TIPO_INGREDIENTE_ID_RICETTESAPONI_MAGAZZINO_IDX
					+ ";");

			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.COEFFSAPONIFICAZIONE_ID_RICETTESAPONI_MAGAZZINO_IDX
					+ ";");

			/*
			 * Query DDL per eliminare lo schema della tabella
			 * ricettesaponi_magazzino
			 */
			db.execSQL("DROP TABLE IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/**
	 * Classe per gestire la tabella ricettesaponi_magazzino_ricetta
	 */
	static class RicetteSaponiMagazzinoRicettaHelper extends SQLiteOpenHelper {

		RicetteSaponiMagazzinoRicettaHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/*
			 * Query DDL per creare la tabella ricettesaponi_magazzino_ricetta
			 */
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID
					+ " INTEGER PRIMARY KEY ASC,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID
					+ " INTEGER NOT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID
					+ " INTEGER NOT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA
					+ " REAL NOT NULL DEFAULT 0.0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE
					+ " INTEGER NOT NULL DEFAULT 0,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE
					+ " TEXT DEFAULT NULL,"
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE
					+ " TEXT DEFAULT NULL,"
					+ " UNIQUE ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID
					+ ", "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID
					+ "),"
					+ " CONSTRAINT "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.FK_RICETTESAPONI
					+ " FOREIGN KEY ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID
					+ ")"
					+ " REFERENCES "
					+ SoapAPPContract.RicetteSaponi.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponi._ID
					+ ")"
					+ " ON DELETE RESTRICT"
					+ " ON UPDATE CASCADE,"
					+ " CONSTRAINT "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.FK_RICETTESAPONI_MAGAZZINO
					+ " FOREIGN KEY ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID
					+ ")" + " REFERENCES "
					+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + " ("
					+ SoapAPPContract.RicetteSaponiMagazzino._ID + ")"
					+ " ON DELETE RESTRICT" + " ON UPDATE CASCADE);");

			/*
			 * Query DDL per creare gli indici della tabella
			 * ricettesaponi_magazzino_ricetta
			 */
			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX
					+ " ON "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID
					+ " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_MAGAZZINO_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX
					+ " ON "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME
					+ " ("
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID
					+ " ASC);");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/*
			 * Query DDL per eliminare gli indici della tabella
			 * ricettesaponi_magazzino_ricetta
			 */
			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX
					+ ";");

			db.execSQL("DROP INDEX IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_MAGAZZINO_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX
					+ ";");

			/*
			 * Query DDL per eliminare lo schema della tabella
			 * ricettesaponi_magazzino_ricetta
			 */
			db.execSQL("DROP TABLE IF EXISTS "
					+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME
					+ ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/**
	 * 
	 * Initializes the provider by creating a new
	 * CoefficientiSaponificazioneHelper. onCreate() is called automatically
	 * when Android creates the provider in response to a resolver request from
	 * a client.
	 */
	@Override
	public boolean onCreate() {

		mRicetteSaponiHelper = new RicetteSaponiHelper(getContext());
		mCoefficientiSaponificazioneHelper = new CoefficientiSaponificazioneHelper(
				getContext());
		mRicetteSaponiTipiIngredientiHelper = new RicetteSaponiTipiIngredientiHelper(
				getContext());
		mRicetteSaponiMagazzinoHelper = new RicetteSaponiMagazzinoHelper(
				getContext());
		mRicetteSaponiMagazzinoRicettaHelper = new RicetteSaponiMagazzinoRicettaHelper(
				getContext());

		return true;
	}

	/**
	 * This method is called when a client calls
	 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)}
	 * . Queries the database and returns a cursor containing the results.
	 * 
	 * @return A cursor containing the results of the query. The cursor exists
	 *         but is empty if the query returns no results or an exception
	 *         occurs.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		String[] projectionPrivate;
		String selectionPrivate;
		String[] selectionArgsPrivate;
		String sortOrderPrivate;
		boolean existsProjection = true;

		switch (sUriMatcher.match(uri)) {

		case URI_MATCH_RICETTESAPONI:
			// da completare
			break;

		case URI_MATCH_RICETTESAPONI_ID:
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:
			// Controlli sulla variabile String[] projection
			if (projection == null || projection.length == 0) {
				projectionPrivate = READ_COEFFICIENTI_SAPONIFICAZIONE_PROJECTION;
			} else {
				for (int i = 0; i < projection.length; i++) {
					existsProjection = coefficientiSaponificazioneProjectionMap
							.containsKey(projection[i]);
					if (!existsProjection) {
						throw new IllegalArgumentException(
								"Unknown projection "
										+ projection[i]
										+ " for table "
										+ SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME);
					}
				}
				projectionPrivate = projection;
			}

			// Controlli sulla variabile String selection
			if (TextUtils.isEmpty(selection)) {
				selectionPrivate = null;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// la selezione
				// Esiste la colonna fornita per la selezione su questa
				// tabella?
				selectionPrivate = selection;
			}

			// Controlli sulla variabile String[] selectionArgs
			if (selectionArgs == null || selectionArgs.length == 0) {
				selectionArgsPrivate = null;
			} else {
				// da inserire verifiche sul contenuto dell'array di stringhe
				// contenente la lista delle colonne da estrarre
				// Esistono tutte le colonne fornite per questa tabella?
				selectionArgsPrivate = selectionArgs;
			}

			// Controlli sulla variabile String sortOrder
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrderPrivate = SoapAPPContract.CoefficientiSaponificazione.DEFAULT_SORT_ORDER;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// l'ordinamento
				// Esiste la colonna fornita per l'ordinamento su questa
				// tabella?
				sortOrderPrivate = sortOrder;
			}

			SQLiteDatabase dbCoefficientiSaponificazione = mCoefficientiSaponificazioneHelper
					.getReadableDatabase();

			queryCursor = dbCoefficientiSaponificazione.query(
					SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME,
					projectionPrivate, selectionPrivate, selectionArgsPrivate,
					null, null, sortOrderPrivate);

			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI:
			// Controlli sulla variabile String[] projection
			if (projection == null || projection.length == 0) {
				projectionPrivate = READ_RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION;
			} else {
				for (int i = 0; i < projection.length; i++) {
					existsProjection = ricetteSaponiTipiIngredientiProjectionMap
							.containsKey(projection[i]);
					if (!existsProjection) {
						throw new IllegalArgumentException(
								"Unknown projection "
										+ projection[i]
										+ " for table "
										+ SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME);
					}
				}
				projectionPrivate = projection;
			}

			// Controlli sulla variabile String selection
			if (TextUtils.isEmpty(selection)) {
				selectionPrivate = null;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// la selezione
				// Esiste la colonna fornita per la selezione su questa
				// tabella?
				selectionPrivate = selection;
			}

			// Controlli sulla variabile String[] selectionArgs
			if (selectionArgs == null || selectionArgs.length == 0) {
				selectionArgsPrivate = null;
			} else {
				// da inserire verifiche sul contenuto dell'array di stringhe
				// contenente la lista delle colonne da estrarre
				// Esistono tutte le colonne fornite per questa tabella?
				selectionArgsPrivate = selectionArgs;
			}

			// Controlli sulla variabile String sortOrder
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrderPrivate = SoapAPPContract.RicetteSaponiTipiIngredienti.DEFAULT_SORT_ORDER;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// l'ordinamento
				// Esiste la colonna fornita per l'ordinamento su questa
				// tabella?
				sortOrderPrivate = sortOrder;
			}

			SQLiteDatabase dbRicetteSaponiTipiIngredienti = mRicetteSaponiTipiIngredientiHelper
					.getReadableDatabase();

			queryCursor = dbRicetteSaponiTipiIngredienti.query(
					SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME,
					projectionPrivate, selectionPrivate, selectionArgsPrivate,
					null, null, sortOrderPrivate);
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO:
			// Controlli sulla variabile String[] projection
			if (projection == null || projection.length == 0) {
				projectionPrivate = READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION;
			} else {
				for (int i = 0; i < projection.length; i++) {
					existsProjection = ricetteSaponiMagazzinoProjectionMap
							.containsKey(projection[i]);
					if (!existsProjection) {
						throw new IllegalArgumentException(
								"Unknown projection "
										+ projection[i]
										+ " for table "
										+ SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME);
					}
				}
				projectionPrivate = projection;
			}

			// Controlli sulla variabile String selection
			if (TextUtils.isEmpty(selection)) {
				selectionPrivate = null;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// la selezione
				// Esiste la colonna fornita per la selezione su questa
				// tabella?
				selectionPrivate = selection;
			}

			// Controlli sulla variabile String[] selectionArgs
			if (selectionArgs == null || selectionArgs.length == 0) {
				selectionArgsPrivate = null;
			} else {
				// da inserire verifiche sul contenuto dell'array di stringhe
				// contenente la lista delle colonne da estrarre
				// Esistono tutte le colonne fornite per questa tabella?
				selectionArgsPrivate = selectionArgs;
			}

			// Controlli sulla variabile String sortOrder
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrderPrivate = SoapAPPContract.RicetteSaponiMagazzino.DEFAULT_SORT_ORDER;
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// l'ordinamento
				// Esiste la colonna fornita per l'ordinamento su questa
				// tabella?
				sortOrderPrivate = sortOrder;
			}

			SQLiteDatabase dbRicetteSaponiMagazzino = mRicetteSaponiMagazzinoHelper
					.getReadableDatabase();

			queryCursor = dbRicetteSaponiMagazzino.query(
					SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME,
					projectionPrivate, selectionPrivate, selectionArgsPrivate,
					null, null, sortOrderPrivate);

			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID:
			// da completare
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (queryCursor.getCount() <= 0) {
			queryCursor = null;
		}
		return queryCursor;
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#getType(Uri)}. Returns the MIME
	 * data type of the URI given as a parameter.
	 * 
	 * @param uri
	 *            The URI whose MIME type is desired.
	 * @return The MIME type of the URI.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public String getType(Uri uri) {

		switch (sUriMatcher.match(uri)) {

		case URI_MATCH_RICETTESAPONI:
			return SoapAPPContract.RicetteSaponi.CONTENT_TYPE;

		case URI_MATCH_RICETTESAPONI_ID:
			return SoapAPPContract.RicetteSaponi.CONTENT_ITEM_TYPE;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:
			return SoapAPPContract.CoefficientiSaponificazione.CONTENT_TYPE;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID:
			return SoapAPPContract.CoefficientiSaponificazione.CONTENT_ITEM_TYPE;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI:
			return SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_TYPE;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID:
			return SoapAPPContract.RicetteSaponiTipiIngredienti.CONTENT_ITEM_TYPE;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO:
			return SoapAPPContract.RicetteSaponiMagazzino.CONTENT_TYPE;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID:
			return SoapAPPContract.RicetteSaponiMagazzino.CONTENT_ITEM_TYPE;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA:
			return SoapAPPContract.RicetteSaponiMagazzinoRicetta.CONTENT_TYPE;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID:
			return SoapAPPContract.RicetteSaponiMagazzinoRicetta.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#insert(Uri, ContentValues)}.
	 * Inserts a new row into the database. This method sets up default values
	 * for any columns that are not included in the incoming map. If rows were
	 * inserted, then listeners are notified of the change.
	 * 
	 * @return The row ID of the inserted row.
	 * @throws SQLException
	 *             if the insertion fails.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri uriPrivate = Uri.EMPTY;
		long idPrivate = 0;
		boolean existsValues = true;
		ContentValues valuesPrivate;
		Object checkValues;
		String typeColumn = "";
		String cnstrNotNullColumn = "";
		String caratteriSpeciali = "";

		switch (sUriMatcher.match(uri)) {

		case URI_MATCH_RICETTESAPONI:
			// da completare
			break;

		case URI_MATCH_RICETTESAPONI_ID:
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:

			// SQLiteDatabase dbCoefficientiSaponificazione =
			// mCoefficientiSaponificazioneHelper.getWritableDatabase();
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO:

			if (initialValues.size() <= 0) {
				throw new IllegalArgumentException("Empty ContentValues "
						+ initialValues.toString());
			} else {

				valuesPrivate = new ContentValues(
						READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION.length);
				// Il ciclo for parte dalla seconda colonna saltando la verifica
				// sulla colonna _ID. Per il metodo insert non si fornisce il
				// valore della colonna _ID, lo calcola il metodo insertOrThrow
				// se non fornito
				for (int i = 1; i < READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION.length; i++) {

					existsValues = initialValues
							.containsKey(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);

					if (existsValues) {

						checkValues = initialValues
								.get(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);

						if (checkValues == null) {

							cnstrNotNullColumn = ricetteSaponiMagazzinoCnstrNotNullMap
									.get(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);
							if (!cnstrNotNullColumn.equals(CONSTRAINT_NOT_NULL)) {
								valuesPrivate
										.putNull(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);
							} else {
								throw new IllegalArgumentException(
										"Il dato fornito "
												+ checkValues
												+ " e\' nullo, mentre la colonna "
												+ READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]
												+ " non accetta valori nulli "
												+ typeColumn);
							}

						} else if (checkValues instanceof Double) {

							typeColumn = ricetteSaponiMagazzinoTypeMap
									.get(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);

							if (typeColumn.equals(TYPE_DOUBLE)) {
								valuesPrivate
										.put(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i],
												initialValues
														.getAsDouble(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]));
							} else {
								throw new IllegalArgumentException(
										"Il dato fornito "
												+ checkValues
												+ " e\' di tipo Double, mentre la colonna "
												+ READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]
												+ " e\' definita come "
												+ typeColumn);
							}

						} else if (checkValues instanceof Integer) {

							typeColumn = ricetteSaponiMagazzinoTypeMap
									.get(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);

							if (typeColumn.equals(TYPE_INTEGER)) {
								valuesPrivate
										.put(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i],
												initialValues
														.getAsInteger(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]));
							} else {
								throw new IllegalArgumentException(
										"Il dato fornito "
												+ checkValues
												+ " e\' di tipo Integer, mentre la colonna "
												+ READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]
												+ " e\' definita come "
												+ typeColumn);
							}

						} else if (checkValues instanceof String) {

							typeColumn = ricetteSaponiMagazzinoTypeMap
									.get(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]);

							if (typeColumn.equals(TYPE_STRING)) {

								caratteriSpeciali = DatabaseUtils
										.sqlEscapeString(initialValues
												.getAsString(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]));

								valuesPrivate
										.put(READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i],
												caratteriSpeciali);
							} else {
								throw new IllegalArgumentException(
										"Il dato fornito "
												+ checkValues
												+ " e\' di tipo String, mentre la colonna "
												+ READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]
												+ " e\' definita come "
												+ typeColumn);
							}

						} else {
							throw new IllegalArgumentException(
									"Il dato fornito "
											+ checkValues
											+ " per la colonna "
											+ READ_RICETTE_SAPONI_MAGAZZINO_PROJECTION[i]
											+ " non appartiene a nessun tipo di dato tra quelli gestiti dalla tabella");
						}
					} else {
						throw new IllegalArgumentException(
								"Colonna sbagliata o non fornita "
										+ initialValues.toString());
						// Obbligatorio fornire tutte le colonne della tabella
						// tranne la prima _ID
					}
				}

			}

			try {
				SQLiteDatabase dbRicetteSaponiMagazzino = mRicetteSaponiMagazzinoHelper
						.getWritableDatabase();

				idPrivate = dbRicetteSaponiMagazzino.insertOrThrow(
						SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME,
						null, valuesPrivate);

				if (idPrivate > 0) {
					uriPrivate = Uri
							.withAppendedPath(
									SoapAPPContract.RicetteSaponiMagazzino.CONTENT_ID_URI_BASE,
									String.valueOf(idPrivate));
					getContext().getContentResolver().notifyChange(uriPrivate,
							null);
				}

			} catch (NullPointerException npe) {
				Log.e(TAG + " " + DATABASE_NAME, npe.toString());
			} catch (SQLiteException sql) {
				Log.e(TAG + " " + DATABASE_NAME, sql.toString());
			}

			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID:
			// da completare
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// if (mCursor.getCount() <= 0) {
		// mCursor = null;
		// }

		return uriPrivate; // da riscivere
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#delete(Uri, String, String[])}.
	 * Deletes records from the database. If the incoming URI matches the note
	 * ID URI pattern, this method deletes the one record specified by the ID in
	 * the URI. Otherwise, it deletes a a set of records. The record or records
	 * must also match the input selection criteria specified by where and
	 * whereArgs.
	 * 
	 * If rows were deleted, then listeners are notified of the change.
	 * 
	 * @return If a "where" clause is used, the number of rows affected is
	 *         returned, otherwise 0 is returned. To delete all rows and get a
	 *         row count, use "1" as the where clause.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// DA PREVEDERE UNA VERIFICA CHE LE RIGHE DA CANCELLARE NON ABBIANO LA
		// COLONNA MODIFICABILE A ZERO, ALTRIMENTI QUELLE RIGHE NON POSSONO
		// ESSERE MODIFICATE.
		String wherePrivate;

		String[] whereArgsPrivate;

		int count = 0;

		switch (sUriMatcher.match(uri)) {

		case URI_MATCH_RICETTESAPONI:
			// da completare
			break;

		case URI_MATCH_RICETTESAPONI_ID:
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:
			// Controlli sulla variabile String where
			if (TextUtils.isEmpty(where)) {
				wherePrivate = "1";
			} else {
				// da inserire verifiche sul contenuto della stringa per
				// l'ordinamento
				// Esiste la colonna fornita per l'ordinamento su questa
				// tabella?
				wherePrivate = where;
			}

			// Controlli sulla variabile String[] whereArgs
			if (whereArgs == null || whereArgs.length == 0) {
				whereArgsPrivate = null;
			} else {
				// da inserire verifiche sul contenuto dell'array di stringhe
				// contenente la lista delle colonne da estrarre
				// Esistono tutte le colonne fornite per questa tabella?
				whereArgsPrivate = whereArgs;
			}

			SQLiteDatabase dbCoefficientiSaponificazione = mCoefficientiSaponificazioneHelper
					.getWritableDatabase();
			// the number of rows affected if a whereClause is passed in, 0
			// otherwise. To remove all rows and get a count pass "1" as the
			// whereClause.
			count = dbCoefficientiSaponificazione.delete(
					SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME,
					wherePrivate, whereArgsPrivate);
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID:
			// da completare
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// getContext().getContentResolver().notifyChange(uri, null); ?????????

		return count;
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#update(Uri,ContentValues,String,String[])}
	 * Updates records in the database. The column names specified by the keys
	 * in the values map are updated with new data specified by the values in
	 * the map. If the incoming URI matches the note ID URI pattern, then the
	 * method updates the one record specified by the ID in the URI; otherwise,
	 * it updates a set of records. The record or records must match the input
	 * selection criteria specified by where and whereArgs. If rows were
	 * updated, then listeners are notified of the change.
	 * 
	 * @param uri
	 *            The URI pattern to match and update.
	 * @param values
	 *            A map of column names (keys) and new values (values).
	 * @param where
	 *            An SQL "WHERE" clause that selects records based on their
	 *            column values. If this is null, then all records that match
	 *            the URI pattern are selected.
	 * @param whereArgs
	 *            An array of selection criteria. If the "where" param contains
	 *            value placeholders ("?"), then each placeholder is replaced by
	 *            the corresponding element in the array.
	 * @return The number of rows updated.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		int count = 0;

		switch (sUriMatcher.match(uri)) {

		case URI_MATCH_RICETTESAPONI:
			// da completare
			break;

		case URI_MATCH_RICETTESAPONI_ID:
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:
			// da completare
			break;

		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA:
			// da completare
			break;

		case URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID:
			// da completare
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	/**
	 * A test package can call this to get a handle to the database underlying
	 * NotePadProvider, so it can insert test data into the database. The test
	 * case class is responsible for instantiating the provider in a test
	 * context; {@link android.test.ProviderTestCase2} does this during the call
	 * to setUp()
	 * 
	 * @return a handle to the database helper object for the provider's data.
	 */
	CoefficientiSaponificazioneHelper getOpenHelperForTest() {
		return mCoefficientiSaponificazioneHelper;
	}
}

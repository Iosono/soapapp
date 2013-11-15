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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.ContentProvider.PipeDataWriter;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class SoapAPPProvider extends ContentProvider implements
		PipeDataWriter<Cursor> {
	// Used for debugging and logging
	private static final String TAG = "SoapAPPProvider";

	private static final String DATABASE_NAME = "soap_app.db";

	private static final int DATABASE_VERSION = 1;

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
		SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE
	};
	
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
		SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE
	};
	
	private static final String[] READ_RICETTE_SAPONI_TIPI_INGREDIENTI_PROJECTION = new String[] {
		SoapAPPContract.RicetteSaponiTipiIngredienti._ID,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE,
		SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE
	};
	
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
		SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE
	};
	
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
		SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE
	};
	
	private RicetteSaponiHelper mRicetteSaponiHelper;
	private CoefficientiSaponificazioneHelper mCoefficientiSaponificazioneHelper;
	private RicetteSaponiTipiIngredientiHelper mRicetteSaponiTipiIngredientiHelper;
	private RicetteSaponiMagazzinoHelper mRicetteSaponiMagazzinoHelper;
	private RicetteSaponiMagazzinoRicettaHelper mRicetteSaponiMagazzinoRicettaHelper;
	
	private static final int READ_NOTE_NOTE_INDEX = 1;
	private static final int READ_NOTE_TITLE_INDEX = 2;

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
	
	
	
		
	private Cursor mCursor;
	
	private static HashMap<String, String> coeffProjectionMap;
	/**
	 * A block that instantiates and sets static objects
	 */
	static {

		/*
		 * Creates and initializes the URI matcher
		 */
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponi.TABLE_NAME, URI_MATCH_RICETTESAPONI);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponi.TABLE_NAME + "/#", URI_MATCH_RICETTESAPONI_ID);
				
		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME + "/#", URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE_ID);
				
		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME, URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME + "/#", URI_MATCH_RICETTE_SAPONI_TIPI_INGREDIENTI_ID);
				
		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME, URI_MATCH_RICETTE_SAPONI_MAGAZZINO);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + "/#", URI_MATCH_RICETTE_SAPONI_MAGAZZINO_ID);
		
		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME, URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA);

		sUriMatcher.addURI(SoapAPPContract.AUTHORITY,
			SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME + "/#", URI_MATCH_RICETTE_SAPONI_MAGAZZINO_RICETTA_ID);

		/*
		 * Creates and initializes a projection map that returns all columns
		 */

		coeffProjectionMap = new HashMap<String, String>();

		coeffProjectionMap.put(
				SoapAPPContract.CoefficientiSaponificazione._ID,
				SoapAPPContract.CoefficientiSaponificazione._ID);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE);

		coeffProjectionMap
				.put(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE,
						SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE);

	}

	
	/** Classe per gestire la tabella ricettesaponi
	*/
	static class RicetteSaponiHelper extends SQLiteOpenHelper {

		RicetteSaponiHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/* Query DDL per creare la tabella ricettesaponi
			*/
			db.execSQL("CREATE TABLE IF NOT EXISTS " + SoapAPPContract.RicetteSaponi.TABLE_NAME + " ("
                + SoapAPPContract.RicetteSaponi._ID + " INTEGER PRIMARY KEY ASC,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME + " TEXT UNIQUE,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS + " TEXT UNIQUE,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE + " TEXT NOT NULL DEFAULT 'ImmagineRicettaStandar',"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA + " TEXT DEFAULT 'note ricetta',"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE + " TEXT DEFAULT NULL"
                + ");");
			
			/* Query DDL per creare gli indici della tabella ricettesaponi
			*/
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponi.NAME_RICETTESAPONI_IDX + " ON " 
                + SoapAPPContract.RicetteSaponi.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME 
                + " ASC);");
                   
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponi.ALIAS_RICETTESAPONI_IDX + " ON " 
                + SoapAPPContract.RicetteSaponi.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS 
                + " ASC);");
			
			/* Query per popolare con due righe la tabella ricettesaponi
			*/
			db.execSQL("INSERT INTO " 
				+ SoapAPPContract.RicetteSaponi.TABLE_NAME 
				+ " (" 
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_NAME 
				+ ", " 
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_ALIAS
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_DESCRIPTION
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_IMAGE
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_GRASSI_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_LIQUIDI_RICETTA
				+ ", "        
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_SCONTO_RICETTA 
				+ ", " 
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_SODA_SCONTO_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_VARIE_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_COSTO_RICETTA 
				+ ", " 
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_COSTO_ETTO_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_NOTE_RICETTA
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICABILE
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_CARICATO_UTENTE
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_CREATE_DATE
				+ ", "
				+ SoapAPPContract.RicetteSaponi.COLUMN_NAME_MODIFICATION_DATE
				+ ") VALUES (\'Prima Ricetta Prova\', \'Primo Alias Prova\', \'Prima Descrizione Prova\', \'Prima Patch file Sistem Prova\', 1000, 330, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, \'Prima Nota Prova\', 0, 0, datetime(\'now\'), datetime(\'now\')),"
				+ "	(\'Seconda Ricetta Prova\', \'Seconda Alias Prova\', \'Seconda Descrizione Prova\', \'Seconda Patch file Sistem Prova\', 1000, 330, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, \'Seconda Nota Prova\', 0, 0, datetime(\'now\'), datetime(\'now\'));");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/* Query DDL per eliminare gli indici della tabella ricettesaponi
			*/
			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponi.NAME_RICETTESAPONI_IDX + ";");

			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponi.ALIAS_RICETTESAPONI_IDX + ";");

			/* Query DDL per eliminare lo schema della tabella ricettesaponi
			*/
			db.execSQL("DROP TABLE IF EXISTS " + SoapAPPContract.RicetteSaponi.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}
	
	/** Classe per gestire la tabella coefficienti_saponificazione
	*/
	static class CoefficientiSaponificazioneHelper extends SQLiteOpenHelper {

		CoefficientiSaponificazioneHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/* Query DDL per creare la tabella coefficienti_saponificazione
			*/
			db.execSQL("CREATE TABLE IF NOT EXISTS " + SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME + " ("
                + SoapAPPContract.CoefficientiSaponificazione._ID + " INTEGER PRIMARY KEY ASC,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME + " TEXT DEFAULT 'NO NAME',"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI + " TEXT DEFAULT 'NO INCI',"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_96_98 + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_KOH_80 + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAOH + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF + " TEXT DEFAULT NULL,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICABILE + " INTEGER NOT NULL DEFAULT 0,"
				+ SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CARICATO_UTENTE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE + " TEXT DEFAULT NULL"
                + ");");

			/* Query DDL per creare gli indici della tabella coefficienti_saponificazione
			*/
			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.CoefficientiSaponificazione.NAME_COEFFICIENTI_SAPONIFICAZIONE_IDX + " ON " 
                + SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME 
                + " ("
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME 
                + " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.CoefficientiSaponificazione.INCI_COEFFICIENTI_SAPONIFICAZIONE_IDX + " ON " 
                + SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME 
                + " ("
                + SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_INCI 
                + " ASC);");
			
			/* Query per popolare la tabella coefficienti_saponificazione
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

			/* Query DDL per eliminare gli indici della tabella coefficienti_saponificazione
			*/
			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.CoefficientiSaponificazione.NAME_COEFFICIENTI_SAPONIFICAZIONE_IDX + ";");

			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.CoefficientiSaponificazione.INCI_COEFFICIENTI_SAPONIFICAZIONE_IDX + ";");

			/* Query DDL per eliminare lo schema della tabella coefficienti_saponificazione
			*/
			db.execSQL("DROP TABLE IF EXISTS " + SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME + ";");

			// Recreates the database with a new version
			onCreate(db);
		}
	}

	/** Classe per gestire la tabella ricettesaponi_tipi_ingredienti
	*/
	static class RicetteSaponiTipiIngredientiHelper extends SQLiteOpenHelper {

		RicetteSaponiTipiIngredientiHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/* Query DDL per creare la tabella ricettesaponi_tipi_ingredienti
			*/
			db.execSQL("CREATE TABLE IF NOT EXISTS " + SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME + " ("
                + SoapAPPContract.RicetteSaponiTipiIngredienti._ID + " INTEGER PRIMARY KEY ASC,"
                + SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME + " TEXT UNIQUE,"
                + SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICABILE + " INTEGER NOT NULL DEFAULT 0,"
				+ SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CARICATO_UTENTE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_CREATE_DATE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_MODIFICATION_DATE + " TEXT DEFAULT NULL"
                + ");");
			
			/* Query DDL per creare gli indici della tabella ricettesaponi_tipi_ingredienti
			*/
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponiTipiIngredienti.NAME_RICETTE_SAPONI_TIPI_INGREDIENTI_IDX + " ON " 
                + SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponiTipiIngredienti.COLUMN_NAME_NAME 
                + " ASC);");
			
			/* Query per popolare con due righe la tabella ricettesaponi_tipi_ingredienti
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

			/* Query DDL per eliminare gli indici della tabella ricettesaponi_tipi_ingredienti
			*/
			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponiTipiIngredienti.NAME_RICETTE_SAPONI_TIPI_INGREDIENTI_IDX + ";");

			/* Query DDL per eliminare lo schema della tabella ricettesaponi_tipi_ingredienti
			*/
			db.execSQL("DROP TABLE IF EXISTS " + SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}
	
	/** Classe per gestire la tabella ricettesaponi_magazzino
	*/
	static class RicetteSaponiMagazzinoHelper extends SQLiteOpenHelper {

		RicetteSaponiMagazzinoHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/* Query DDL per creare la tabella ricettesaponi_magazzino
			*/
			db.execSQL("CREATE TABLE IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + " ("
                + SoapAPPContract.RicetteSaponiMagazzino._ID + " INTEGER PRIMARY KEY ASC,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID + " INTEGER NOT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID + " INTEGER,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NAME + " TEXT DEFAULT 'Default Nome Ingrediente',"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_ALIAS + " TEXT DEFAULT 'Default Alias Ingrediente',"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DESCRIPTION + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_IMAGE + " TEXT NOT NULL DEFAULT 'ImmagineIngredienteStandar',"
				+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_LORDO_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_NETTO_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_TARA_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_LORDO_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_NETTO_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_PESO_TARA_INGREDIENTE + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOME_NEGOZIO_ACQUISTO + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_DATA_SCADENZA_INGREDIENTE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_NOTE_INGREDIENTE + " TEXT DEFAULT 'note ingrediente',"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICABILE + " INTEGER NOT NULL DEFAULT 0,"
				+ SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CARICATO_UTENTE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_CREATE_DATE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_MODIFICATION_DATE + " TEXT DEFAULT NULL,"
                + " CONSTRAINT " + SoapAPPContract.RicetteSaponiMagazzino.FK_COEFFSAPONIFICAZIONE
	            + " FOREIGN KEY (" + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID + ")"
		        + " REFERENCES " + SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME + " (" + SoapAPPContract.CoefficientiSaponificazione._ID + ")"
		        + " ON DELETE RESTRICT"
		        + " ON UPDATE CASCADE,"
                + " CONSTRAINT " + SoapAPPContract.RicetteSaponiMagazzino.FK_TIPO_INGREDIENTE
		        + " FOREIGN KEY (" + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID + ")"
		        + " REFERENCES " + SoapAPPContract.RicetteSaponiTipiIngredienti.TABLE_NAME + " (" + SoapAPPContract.RicetteSaponiTipiIngredienti._ID + ")"
		        + " ON DELETE RESTRICT"
		        + " ON UPDATE CASCADE);");
			
			/* Query DDL per creare gli indici della tabella ricettesaponi_magazzino
			*/
			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.TIPO_INGREDIENTE_ID_RICETTESAPONI_MAGAZZINO_IDX + " ON " 
                + SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_TIPO_INGREDIENTE_ID 
                + " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.COEFFSAPONIFICAZIONE_ID_RICETTESAPONI_MAGAZZINO_IDX + " ON " 
                + SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponiMagazzino.COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID 
                + " ASC);");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/* Query DDL per eliminare gli indici della tabella ricettesaponi_magazzino
			*/
			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.TIPO_INGREDIENTE_ID_RICETTESAPONI_MAGAZZINO_IDX + ";");

			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.COEFFSAPONIFICAZIONE_ID_RICETTESAPONI_MAGAZZINO_IDX + ";");

			/* Query DDL per eliminare lo schema della tabella ricettesaponi_magazzino
			*/
			db.execSQL("DROP TABLE IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}
	
	/** Classe per gestire la tabella ricettesaponi_magazzino_ricetta
	*/
	static class RicetteSaponiMagazzinoRicettaHelper extends SQLiteOpenHelper {

		RicetteSaponiMagazzinoRicettaHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			/* Query DDL per creare la tabella ricettesaponi_magazzino_ricetta
			*/
			db.execSQL("CREATE TABLE IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME + " ("
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID + " INTEGER PRIMARY KEY ASC,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID + " INTEGER NOT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID + " INTEGER NOT NULL,"
				+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_PESO_INGREDIENTE_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
				+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_SODA_GRASSO_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_COSTO_INGREDIENTE_RICETTA + " REAL NOT NULL DEFAULT 0.0,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICABILE + " INTEGER NOT NULL DEFAULT 0,"
				+ SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CARICATO_UTENTE + " INTEGER NOT NULL DEFAULT 0,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_CREATE_DATE + " TEXT DEFAULT NULL,"
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_MODIFICATION_DATE + " TEXT DEFAULT NULL,"
                + " UNIQUE (" + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID + ", " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID + "),"
	            + " CONSTRAINT " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.FK_RICETTESAPONI
		        + " FOREIGN KEY (" + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID + ")"
		        + " REFERENCES " + SoapAPPContract.RicetteSaponi.TABLE_NAME + " (" + SoapAPPContract.RicetteSaponi._ID + ")"
		        + " ON DELETE RESTRICT"
		        + " ON UPDATE CASCADE,"
	            + " CONSTRAINT " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.FK_RICETTESAPONI_MAGAZZINO
		        + " FOREIGN KEY (" + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID + ")"
		        + " REFERENCES " + SoapAPPContract.RicetteSaponiMagazzino.TABLE_NAME + " (" + SoapAPPContract.RicetteSaponiMagazzino._ID + ")"
		        + " ON DELETE RESTRICT"
		        + " ON UPDATE CASCADE);");
			
			/* Query DDL per creare gli indici della tabella ricettesaponi_magazzino_ricetta
			*/
			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX + " ON " 
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_ID 
                + " ASC);");

			db.execSQL("CREATE INDEX IF NOT EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_MAGAZZINO_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX + " ON " 
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME 
                + " ("
                + SoapAPPContract.RicetteSaponiMagazzinoRicetta.COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID 
                + " ASC);");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			/* Query DDL per eliminare gli indici della tabella ricettesaponi_magazzino_ricetta
			*/
			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX + ";");

			db.execSQL("DROP INDEX IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.RICETTESAPONI_MAGAZZINO_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX + ";");

			/* Query DDL per eliminare lo schema della tabella ricettesaponi_magazzino_ricetta
			*/
			db.execSQL("DROP TABLE IF EXISTS " + SoapAPPContract.RicetteSaponiMagazzinoRicetta.TABLE_NAME + ";");
			// Recreates the database with a new version
			onCreate(db);
		}
	}
	
	/**
	 * 
	 * Initializes the provider by creating a new CoefficientiSaponificazioneHelper. onCreate() is
	 * called automatically when Android creates the provider in response to a
	 * resolver request from a client.
	 */
	@Override
	public boolean onCreate() {

		mRicetteSaponiHelper = new RicetteSaponiHelper(getContext());
		mCoefficientiSaponificazioneHelper = new CoefficientiSaponificazioneHelper(getContext());
		mRicetteSaponiTipiIngredientiHelper = new RicetteSaponiTipiIngredientiHelper(getContext());
		mRicetteSaponiMagazzinoHelper = new RicetteSaponiMagazzinoHelper(getContext());
		mRicetteSaponiMagazzinoRicettaHelper = new RicetteSaponiMagazzinoRicettaHelper(getContext());

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

		switch (sUriMatcher.match(uri)) {
		
		case URI_MATCH_RICETTESAPONI:
			// da completare
			break;
		
		case URI_MATCH_RICETTESAPONI_ID:
			// da completare
			break;
		
		case URI_MATCH_COEFFICIENTI_SAPONIFICAZIONE:
			
			SQLiteDatabase db = mCoefficientiSaponificazioneHelper.getReadableDatabase();
			
			String orderBy;
			
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = SoapAPPContract.CoefficientiSaponificazione.DEFAULT_SORT_ORDER;
			} else {
				// otherwise, uses the incoming sort order
				orderBy = sortOrder;
			}

			mCursor = db
					.query(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME,
							READ_COEFFICIENTI_SAPONIFICAZIONE_PROJECTION, selection, selectionArgs, null, null,
							orderBy);
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
		if (mCursor.getCount() <= 0) {
			mCursor = null;
		}
		return mCursor;
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

		/**
		 * Chooses the MIME type based on the incoming URI pattern
		 */
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
	 * This describes the MIME types that are supported for opening a note URI
	 * as a stream.
	 */
	static ClipDescription NOTE_STREAM_TYPES = new ClipDescription(null,
			new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN });

	/**
	 * Returns the types of available data streams. URIs to specific notes are
	 * supported. The application can convert such a note to a plain text
	 * stream.
	 * 
	 * @param uri
	 *            the URI to analyze
	 * @param mimeTypeFilter
	 *            The MIME type to check for. This method only returns a data
	 *            stream type for MIME types that match the filter. Currently,
	 *            only text/plain MIME types match.
	 * @return a data stream MIME type. Currently, only text/plan is returned.
	 * @throws IllegalArgumentException
	 *             if the URI pattern doesn't match any supported patterns.
	 */
	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		/**
		 * Chooses the data stream type based on the incoming URI pattern.
		 */
		switch (sUriMatcher.match(uri)) {

		// If the pattern is for notes, return null. Data streams are not
		// supported for this type of URI.
		case COEFFICIENTI:
			return null;

			// If the pattern is for note IDs and the MIME filter is text/plain,
			// then return
			// text/plain
		case COEFFICIENTE_ID:
			return NOTE_STREAM_TYPES.filterMimeTypes(mimeTypeFilter);

			// If the URI pattern doesn't match any permitted patterns, throws
			// an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * Returns a stream of data for each supported stream type. This method does
	 * a query on the incoming URI, then uses
	 * {@link android.content.ContentProvider#openPipeHelper(Uri, String, Bundle, Object, PipeDataWriter)}
	 * to start another thread in which to convert the data into a stream.
	 * 
	 * @param uri
	 *            The URI pattern that points to the data stream
	 * @param mimeTypeFilter
	 *            A String containing a MIME type. This method tries to get a
	 *            stream of data with this MIME type.
	 * @param opts
	 *            Additional options supplied by the caller. Can be interpreted
	 *            as desired by the content provider.
	 * @return AssetFileDescriptor A handle to the file.
	 * @throws FileNotFoundException
	 *             if there is no file associated with the incoming URI.
	 */
	@Override
	public AssetFileDescriptor openTypedAssetFile(Uri uri,
			String mimeTypeFilter, Bundle opts) throws FileNotFoundException {

		// Checks to see if the MIME type filter matches a supported MIME type.
		String[] mimeTypes = getStreamTypes(uri, mimeTypeFilter);

		// If the MIME type is supported
		if (mimeTypes != null) {

			// Retrieves the note for this URI. Uses the query method defined
			// for this provider,
			// rather than using the database query method.
			Cursor c = query(uri, // The URI of a note
					READ_COEFFICIENTI_SAPONIFICAZIONE_PROJECTION, // Gets a projection containing the
											// note's ID, title,
											// and contents
					null, // No WHERE clause, get all matching records
					null, // Since there is no WHERE clause, no selection
							// criteria
					null // Use the default sort order (modification date,
							// descending
			);

			// If the query fails or the cursor is empty, stop
			if (c == null || !c.moveToFirst()) {

				// If the cursor is empty, simply close the cursor and return
				if (c != null) {
					c.close();
				}

				// If the cursor is null, throw an exception
				throw new FileNotFoundException("Unable to query " + uri);
			}

			// Start a new thread that pipes the stream data back to the caller.
			return new AssetFileDescriptor(openPipeHelper(uri, mimeTypes[0],
					opts, c, this), 0, AssetFileDescriptor.UNKNOWN_LENGTH);
		}

		// If the MIME type is not supported, return a read-only handle to the
		// file.
		return super.openTypedAssetFile(uri, mimeTypeFilter, opts);
	}

	/**
	 * Implementation of {@link android.content.ContentProvider.PipeDataWriter}
	 * to perform the actual work of converting the data in one of cursors to a
	 * stream of data for the client to read.
	 */
	@Override
	public void writeDataToPipe(ParcelFileDescriptor output, Uri uri,
			String mimeType, Bundle opts, Cursor c) {
		// We currently only support conversion-to-text from a single note
		// entry,
		// so no need for cursor data type checking here.
		FileOutputStream fout = new FileOutputStream(output.getFileDescriptor());
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(fout, "UTF-8"));
			pw.println(c.getString(READ_NOTE_TITLE_INDEX));
			pw.println("");
			pw.println(c.getString(READ_NOTE_NOTE_INDEX));
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, "Ooops", e);
		} finally {
			c.close();
			if (pw != null) {
				pw.flush();
			}
			try {
				fout.close();
			} catch (IOException e) {
			}
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

		// Validates the incoming URI. Only the full provider URI is allowed for
		// inserts.
		if (sUriMatcher.match(uri) != COEFFICIENTI) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// A map to hold the new record's values.
		ContentValues values;

		// If the incoming values map is not null, uses it for the new values.
		if (initialValues != null) {
			values = new ContentValues(initialValues);

		} else {
			// Otherwise, create a new value map
			values = new ContentValues();
		}

		// Gets the current system time in milliseconds
		Long now = Long.valueOf(System.currentTimeMillis());

		// If the values map doesn't contain the creation date, sets the value
		// to the current time.
		if (values
				.containsKey(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE) == false) {
			values.put(
					SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_CREATE_DATE,
					now);
		}

		// If the values map doesn't contain the modification date, sets the
		// value to the current
		// time.
		if (values
				.containsKey(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE) == false) {
			values.put(
					SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_MODIFICATION_DATE,
					now);
		}

		// If the values map doesn't contain a title, sets the value to the
		// default title.
		if (values
				.containsKey(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME) == false) {
			Resources r = Resources.getSystem();
			values.put(
					SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NAME,
					r.getString(android.R.string.untitled));
		}

		// If the values map doesn't contain note text, sets the value to an
		// empty string.
		if (values
				.containsKey(SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF) == false) {
			values.put(
					SoapAPPContract.CoefficientiSaponificazione.COLUMN_NAME_NOTE_COEFF,
					"");
		}

		// Opens the database object in "write" mode.
		SQLiteDatabase db = mCoefficientiSaponificazioneHelper.getWritableDatabase();

		// Performs the insert and returns the ID of the new note.
		long rowId = db
				.insert(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, // The
																								// table
																								// to
																								// insert
																								// into.
						null, // A hack, SQLite sets this column value to null
								// if values is empty.
						values // A map of column names, and the values to
								// insert
								// into the columns.
				);

		// If the insert succeeded, the row ID exists.
		if (rowId > 0) {
			// Creates a URI with the note ID pattern and the new row ID
			// appended to it.
			Uri noteUri = ContentUris
					.withAppendedId(
							SoapAPPContract.CoefficientiSaponificazione.CONTENT_ID_URI_BASE,
							rowId);

			// Notifies observers registered against this provider that the data
			// changed.
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		// If the insert didn't succeed, then the rowID is <= 0. Throws an
		// exception.
		throw new SQLException("Failed to insert row into " + uri);
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

		// Opens the database object in "write" mode.
		SQLiteDatabase db = mCoefficientiSaponificazioneHelper.getWritableDatabase();
		String finalWhere;

		int count;

		// Does the delete based on the incoming URI pattern.
		switch (sUriMatcher.match(uri)) {

		// If the incoming pattern matches the general pattern for notes, does a
		// delete
		// based on the incoming "where" columns and arguments.
		case COEFFICIENTI:
			count = db
					.delete(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, // The
																									// database
																									// table
																									// name
							where, // The incoming where clause column names
							whereArgs // The incoming where clause values
					);
			break;

		// If the incoming URI matches a single note ID, does the delete based
		// on the
		// incoming data, but modifies the where clause to restrict it to the
		// particular note ID.
		case COEFFICIENTE_ID:
			/*
			 * Starts a final WHERE clause by restricting it to the desired note
			 * ID.
			 */
			finalWhere = SoapAPPContract.CoefficientiSaponificazione._ID
					+ // The ID column name
					" = "
					+ // test for equality
					uri.getPathSegments()
							. // the incoming note ID
							get(SoapAPPContract.CoefficientiSaponificazione.COEFFICIENTI_SAPONIFICAZIONE_ID_PATH_POSITION);

			// If there were additional selection criteria, append them to the
			// final
			// WHERE clause
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}

			// Performs the delete.
			count = db
					.delete(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, // The
																									// database
																									// table
																									// name.
							finalWhere, // The final WHERE clause
							whereArgs // The incoming where clause values.
					);
			break;

		// If the incoming pattern is invalid, throws an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*
		 * Gets a handle to the content resolver object for the current context,
		 * and notifies it that the incoming URI changed. The object passes this
		 * along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows deleted.
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

		// Opens the database object in "write" mode.
		SQLiteDatabase db = mCoefficientiSaponificazioneHelper.getWritableDatabase();
		int count;
		String finalWhere;

		// Does the update based on the incoming URI pattern
		switch (sUriMatcher.match(uri)) {

		// If the incoming URI matches the general notes pattern, does the
		// update based on
		// the incoming data.
		case COEFFICIENTI:

			// Does the update and returns the number of rows updated.
			count = db
					.update(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, // The
																									// database
																									// table
																									// name.
							values, // A map of column names and new values to
									// use.
							where, // The where clause column names.
							whereArgs // The where clause column values to
										// select on.
					);
			break;

		// If the incoming URI matches a single note ID, does the update based
		// on the incoming
		// data, but modifies the where clause to restrict it to the particular
		// note ID.
		case COEFFICIENTE_ID:
			// From the incoming URI, get the note ID
			// ?????String noteId =
			// uri.getPathSegments().get(SoapAPPContract.CoefficientiSaponificazione.COEFFICIENTI_SAPONIFICAZIONE_ID_PATH_POSITION);

			/*
			 * Starts creating the final WHERE clause by restricting it to the
			 * incoming note ID.
			 */
			finalWhere = SoapAPPContract.CoefficientiSaponificazione._ID
					+ // The ID column name
					" = "
					+ // test for equality
					uri.getPathSegments()
							. // the incoming note ID
							get(SoapAPPContract.CoefficientiSaponificazione.COEFFICIENTI_SAPONIFICAZIONE_ID_PATH_POSITION);

			// If there were additional selection criteria, append them to the
			// final WHERE
			// clause
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}

			// Does the update and returns the number of rows updated.
			count = db
					.update(SoapAPPContract.CoefficientiSaponificazione.TABLE_NAME, // The
																									// database
																									// table
																									// name.
							values, // A map of column names and new values to
									// use.
							finalWhere, // The final WHERE clause to use
										// placeholders for whereArgs
							whereArgs // The where clause column values to
										// select on, or
										// null if the values are in the where
										// argument.
					);
			break;
		// If the incoming pattern is invalid, throws an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*
		 * Gets a handle to the content resolver object for the current context,
		 * and notifies it that the incoming URI changed. The object passes this
		 * along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows updated.
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

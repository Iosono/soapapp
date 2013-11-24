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

import android.net.Uri;
import android.provider.BaseColumns;

public final class SoapAPPContract {
	public static final String AUTHORITY = "it.soapapp.provider";

	// This class cannot be instantiated
	private SoapAPPContract() {
	}

	/**
	 * RicetteSaponi table contract
	 */
	public static final class RicetteSaponi implements BaseColumns {

		// This class cannot be instantiated
		private RicetteSaponi() {
		}

		public static final String TABLE_NAME = "ricettesaponi";

		/*
		 * URI definitions
		 */
		private static final String SCHEME = "content://";

		private static final String RICETTE_SAPONI = "/ricettesaponi";

		private static final String RICETTE_SAPONI_ID = "/ricettesaponi/";

		public static final int RICETTE_SAPONI_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ RICETTE_SAPONI);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_ID);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_ID + "#");

		/*
		 * MIME type definitions
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.it.soapapp.provider.ricettesaponi";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.it.soapapp.provider.ricettesaponi";

		public static final String DEFAULT_SORT_ORDER = SoapAPPContract.RicetteSaponi._ID
				+ " ASC";

		/*
		 * Column definitions
		 */
		public static final String COLUMN_NAME_NAME = "name";

		public static final String COLUMN_NAME_ALIAS = "alias";

		public static final String COLUMN_NAME_DESCRIPTION = "description";

		public static final String COLUMN_NAME_IMAGE = "image";

		public static final String COLUMN_NAME_TOT_GRASSI_RICETTA = "tot_grassi_ricetta";

		public static final String COLUMN_NAME_TOT_LIQUIDI_RICETTA = "tot_liquidi_ricetta";

		public static final String COLUMN_NAME_TOT_SODA_RICETTA = "tot_soda_ricetta";

		public static final String COLUMN_NAME_SCONTO_RICETTA = "sconto_ricetta";

		public static final String COLUMN_NAME_TOT_SODA_SCONTO_RICETTA = "tot_soda_sconto_ricetta";

		public static final String COLUMN_NAME_TOT_COSTO_INGREDIENTI_RICETTA = "tot_costo_ingredienti_ricetta";

		public static final String COLUMN_NAME_TOT_COSTO_MANODOPERA_RICETTA = "tot_costo_manodopera_ricetta";

		public static final String COLUMN_NAME_TOT_COSTO_VARIE_RICETTA = "tot_costo_varie_ricetta";

		public static final String COLUMN_NAME_TOT_COSTO_RICETTA = "tot_costo_ricetta";

		public static final String COLUMN_NAME_TOT_ETTI_STIMATI_RICETTA = "tot_etti_stimati_ricetta";

		public static final String COLUMN_NAME_COSTO_ETTO_RICETTA = "costo_etto_ricetta";

		public static final String COLUMN_NAME_NOTE_RICETTA = "note_ricetta";

		public static final String COLUMN_NAME_MODIFICABILE = "modificabile";

		public static final String COLUMN_NAME_CARICATO_UTENTE = "caricato_utente";

		public static final String COLUMN_NAME_CREATE_DATE = "create_date";

		public static final String COLUMN_NAME_MODIFICATION_DATE = "modified_date";

		/*
		 * index definitions
		 */
		public static final String NAME_RICETTESAPONI_IDX = "ricettesaponi_name_idx";

		public static final String ALIAS_RICETTESAPONI_IDX = "ricettesaponi_alias_idx";
	}

	/**
	 * CoefficientiSaponificazione table contract
	 */
	public static final class CoefficientiSaponificazione implements
			BaseColumns {

		// This class cannot be instantiated
		private CoefficientiSaponificazione() {
		}

		/**
		 * The table name offered by this provider
		 */
		public static final String TABLE_NAME = "coefficienti_saponificazione";

		/*
		 * URI definitions
		 */
		private static final String SCHEME = "content://";

		private static final String COEFFICIENTI_SAPONIFICAZIONE = "/coefficienti_saponificazione";

		private static final String COEFFICIENTI_SAPONIFICAZIONE_ID = "/coefficienti_saponificazione/";

		public static final int COEFFICIENTI_SAPONIFICAZIONE_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ COEFFICIENTI_SAPONIFICAZIONE);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + COEFFICIENTI_SAPONIFICAZIONE_ID);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + COEFFICIENTI_SAPONIFICAZIONE_ID + "#");

		/*
		 * MIME type definitions
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.it.soapapp.provider.coefficienti_saponificazione";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.it.soapapp.provider.coefficienti_saponificazione";

		public static final String DEFAULT_SORT_ORDER = SoapAPPContract.CoefficientiSaponificazione._ID
				+ " ASC";

		/*
		 * Column definitions
		 */
		public static final String COLUMN_NAME_NAME = "name";

		public static final String COLUMN_NAME_INCI = "inci";

		public static final String COLUMN_NAME_KOH_96_98 = "koh_96_98";

		public static final String COLUMN_NAME_KOH_80 = "koh_80";

		public static final String COLUMN_NAME_NAOH = "naoh";

		public static final String COLUMN_NAME_NOTE_COEFF = "note_coeff";

		public static final String COLUMN_NAME_MODIFICABILE = "modificabile";

		public static final String COLUMN_NAME_CARICATO_UTENTE = "caricato_utente";

		public static final String COLUMN_NAME_CREATE_DATE = "create_date";

		public static final String COLUMN_NAME_MODIFICATION_DATE = "modified_date";

		/*
		 * index definitions
		 */
		public static final String NAME_COEFFICIENTI_SAPONIFICAZIONE_IDX = "name_coefficienti_saponificazione_idx";

		public static final String INCI_COEFFICIENTI_SAPONIFICAZIONE_IDX = "inci_coefficienti_saponificazione_idx";
	}

	/**
	 * RicetteSaponiTipiIngredienti table contract
	 */
	public static final class RicetteSaponiTipiIngredienti implements
			BaseColumns {

		// This class cannot be instantiated
		private RicetteSaponiTipiIngredienti() {
		}

		/**
		 * The table name offered by this provider
		 */
		public static final String TABLE_NAME = "ricettesaponi_tipi_ingredienti";

		/*
		 * URI definitions
		 */
		private static final String SCHEME = "content://";

		private static final String RICETTE_SAPONI_TIPI_INGREDIENTI = "/ricettesaponi_tipi_ingredienti";

		private static final String RICETTE_SAPONI_TIPI_INGREDIENTI_ID = "/ricettesaponi_tipi_ingredienti/";

		public static final int RICETTE_SAPONI_TIPI_INGREDIENTI_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ RICETTE_SAPONI_TIPI_INGREDIENTI);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_TIPI_INGREDIENTI_ID);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_TIPI_INGREDIENTI_ID+ "#"); 

		/*
		 * MIME type definitions
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.it.soapapp.provider.ricettesaponi_tipi_ingredienti";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.it.soapapp.provider.ricettesaponi_tipi_ingredienti";

		public static final String DEFAULT_SORT_ORDER = SoapAPPContract.RicetteSaponiTipiIngredienti._ID
				+ " ASC";

		/*
		 * Column definitions
		 */
		public static final String COLUMN_NAME_NAME = "name";

		public static final String COLUMN_NAME_MODIFICABILE = "modificabile";

		public static final String COLUMN_NAME_CARICATO_UTENTE = "caricato_utente";

		public static final String COLUMN_NAME_CREATE_DATE = "create_date";

		public static final String COLUMN_NAME_MODIFICATION_DATE = "modified_date";

		/*
		 * index definitions
		 */
		public static final String NAME_RICETTE_SAPONI_TIPI_INGREDIENTI_IDX = "name_ricette_saponi_tipi_ingredienti_idx";

	}

	/**
	 * RicetteSaponiMagazzino table contract
	 */
	public static final class RicetteSaponiMagazzino implements BaseColumns {

		// This class cannot be instantiated
		private RicetteSaponiMagazzino() {
		}

		public static final String TABLE_NAME = "ricettesaponi_magazzino";

		/*
		 * URI definitions
		 */
		private static final String SCHEME = "content://";

		private static final String RICETTE_SAPONI_MAGAZZINO = "/ricettesaponi_magazzino";

		private static final String RICETTE_SAPONI_MAGAZZINO_ID = "/ricettesaponi_magazzino/";

		public static final int RICETTE_SAPONI_MAGAZZINO_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ RICETTE_SAPONI_MAGAZZINO);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_MAGAZZINO_ID);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_MAGAZZINO_ID + "#");

		/*
		 * MIME type definitions
		 */

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.it.soapapp.provider.ricettesaponi_magazzino";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.it.soapapp.provider.ricettesaponi_magazzino";

		public static final String DEFAULT_SORT_ORDER = SoapAPPContract.RicetteSaponiMagazzino._ID
				+ " ASC";

		/*
		 * Column definitions
		 */
		public static final String COLUMN_NAME_TIPO_INGREDIENTE_ID = "tipo_ingrediente_id";

		public static final String COLUMN_NAME_COEFFICIENTESAPONIFICAZIONE_ID = "coeffsaponificazione_id";

		public static final String COLUMN_NAME_NAME = "name";

		public static final String COLUMN_NAME_ALIAS = "alias";

		public static final String COLUMN_NAME_DESCRIPTION = "description";

		public static final String COLUMN_NAME_IMAGE = "image";

		public static final String COLUMN_NAME_COSTO_LORDO_INGREDIENTE = "costo_lordo_ingrediente";

		public static final String COLUMN_NAME_COSTO_NETTO_INGREDIENTE = "costo_netto_ingrediente";

		public static final String COLUMN_NAME_COSTO_TARA_INGREDIENTE = "costo_tara_ingrediente";

		public static final String COLUMN_NAME_COSTO_INGREDIENTE_GRAMMO = "costo_ingrediente_grammo";

		public static final String COLUMN_NAME_PESO_LORDO_INGREDIENTE = "peso_lordo_ingrediente";

		public static final String COLUMN_NAME_PESO_NETTO_INGREDIENTE = "peso_netto_ingrediente";

		public static final String COLUMN_NAME_PESO_TARA_INGREDIENTE = "peso_tara_ingrediente";

		public static final String COLUMN_NAME_DATA_ACQUISTO_INGREDIENTE = "data_acquisto_ingrediente";

		public static final String COLUMN_NAME_NOME_NEGOZIO_ACQUISTO = "nome_negozio_acquisto";

		public static final String COLUMN_NAME_DATA_SCADENZA_INGREDIENTE = "data_scadenza_ingrediente";

		public static final String COLUMN_NAME_NOTE_INGREDIENTE = "note_ingrediente";

		public static final String COLUMN_NAME_MODIFICABILE = "modificabile";

		public static final String COLUMN_NAME_CARICATO_UTENTE = "caricato_utente";

		public static final String COLUMN_NAME_CREATE_DATE = "create_date";

		public static final String COLUMN_NAME_MODIFICATION_DATE = "modified_date";

		/*
		 * index definitions
		 */
		public static final String TIPO_INGREDIENTE_ID_RICETTESAPONI_MAGAZZINO_IDX = "tipo_ingrediente_id_ricettesaponi_magazzino_idx";

		public static final String COEFFSAPONIFICAZIONE_ID_RICETTESAPONI_MAGAZZINO_IDX = "coeffsaponificazione_id_ricettesaponi_magazzino_idx";

		/*
		 * foreing key definitions
		 */
		public static final String FK_COEFFSAPONIFICAZIONE = "fk_coeffsaponificazione";

		public static final String FK_TIPO_INGREDIENTE = "fk_tipo_ingrediente";
	}

	/**
	 * RicetteSaponiMagazzinoRicetta table contract
	 */
	public static final class RicetteSaponiMagazzinoRicetta implements
			BaseColumns {

		// This class cannot be instantiated
		private RicetteSaponiMagazzinoRicetta() {
		}

		public static final String TABLE_NAME = "ricettesaponi_magazzino_ricetta";

		/*
		 * URI definitions
		 */
		private static final String SCHEME = "content://";

		private static final String RICETTE_SAPONI_MAGAZZINO_RICETTA = "/ricettesaponi_magazzino_ricetta";

		private static final String RICETTE_SAPONI_MAGAZZINO_RICETTA_ID = "/ricettesaponi_magazzino_ricetta/";

		public static final int RICETTE_SAPONI_MAGAZZINO_RICETTA_ID_PATH_POSITION = 1;

		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ RICETTE_SAPONI_MAGAZZINO_RICETTA);

		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_MAGAZZINO_RICETTA_ID);

		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + RICETTE_SAPONI_MAGAZZINO_RICETTA_ID + "#");

		/*
		 * MIME type definitions
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.it.soapapp.provider.ricettesaponi_magazzino_ricetta";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.it.soapapp.provider.ricettesaponi_magazzino_ricetta";

		public static final String DEFAULT_SORT_ORDER = SoapAPPContract.RicetteSaponiMagazzinoRicetta._ID
				+ " ASC";

		/*
		 * Column definitions
		 */
		public static final String COLUMN_NAME_RICETTESAPONI_ID = "ricettesaponi_id";

		public static final String COLUMN_NAME_RICETTESAPONI_MAGAZZINO_ID = "ricettesaponi_magazzino_id";

		public static final String COLUMN_NAME_PERCENTUALE_GRASSO_RICETTA = "percentuale_grasso_ricetta";

		public static final String COLUMN_NAME_PESO_INGREDIENTE_RICETTA = "peso_ingrediente_ricetta";

		public static final String COLUMN_NAME_SODA_GRASSO_RICETTA = "soda_grasso_ricetta";

		public static final String COLUMN_NAME_COSTO_INGREDIENTE_RICETTA = "costo_ingrediente_ricetta";

		public static final String COLUMN_NAME_MODIFICABILE = "modificabile";

		public static final String COLUMN_NAME_CARICATO_UTENTE = "caricato_utente";

		public static final String COLUMN_NAME_CREATE_DATE = "create_date";

		public static final String COLUMN_NAME_MODIFICATION_DATE = "modified_date";

		/*
		 * index definitions
		 */
		public static final String RICETTESAPONI_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX = "ricettesaponi_id_ricettesaponi_magazzino_ricetta_idx";

		public static final String RICETTESAPONI_MAGAZZINO_ID_RICETTESAPONI_MAGAZZINO_RICETTA_IDX = "ricettesaponi_magazzino_id_ricettesaponi_magazzino_ricetta_idx";

		/*
		 * foreing key definitions
		 */
		public static final String FK_RICETTESAPONI = "fk_ricettesaponi";

		public static final String FK_RICETTESAPONI_MAGAZZINO = "fk_ricettesaponi_magazzino";
	}

}

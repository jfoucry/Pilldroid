package net.foucry.pilldroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jfoucry on 5/25/16.
 */
public class DBMedoc  extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    File dbFile;
    private final static String DB_PATH = "/data/data/net.foucry.pilldroid/databases/";
    private static String dbName = "medicaments.db";

    private SQLiteDatabase myDataBase;
    Context myContext;

    private static final String TABLE_NAME  = "medicaments";
    private static final String MEDOC_CIS   = "cis";
    private static final String MEDOC_CIP13 = "cip13";
    private static final String MEDOC_ADMIN = "mode_administration";
    private static final String MEDOC_NOM   = "nom";
    private static final String MEDOC_PRES  = "presentation";

    private static final String[] COLUMNS_NAMES = {MEDOC_CIS, MEDOC_CIP13, MEDOC_ADMIN, MEDOC_NOM, MEDOC_PRES};

    public DBMedoc(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (!dbFile.exists()) {
            SQLiteDatabase db = super.getWritableDatabase();
            copyDatabase(db.getPath());
        }

        return  super.getWritableDatabase();
    }

    @Override
    public synchronized  SQLiteDatabase getReadableDatabase() {
        if (!dbFile.exists()) {
            SQLiteDatabase db = super.getReadableDatabase();
            copyDatabase(db.getPath());
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void copyDatabase(String dbPath) {
        try {
            InputStream assestDB = myContext.getAssets().open(dbName);
            OutputStream appDB = new FileOutputStream(dbPath, false);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assestDB.read(buffer)) > 0) {
                appDB.write(buffer,0, length);
            }

            appDB.flush();
            appDB.close();
            assestDB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDatabase() throws SQLiteException {
        Log.e(MedicamentListActivity.Constants.TAG, "openDatabase called");
        String myPath = DB_PATH + dbName;

        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
    }

    // private DBMedoc dbMedoc;

    public Medicament getMedocByCIP13(String cip13) {
        Log.e(MedicamentListActivity.Constants.TAG, "getNedocByCIP13 - " + cip13);

        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_NAME,               // Which table
                COLUMNS_NAMES,                             // column names
                " cip13 =?",                              // selections
                new String[]{cip13},       // selections args
                null,                                      // group by
                null,                                      // having
                null,                                      // order by
                null);                                     // limits

        if (cursor != null)
            cursor.moveToFirst();

        // Build medicament object
        Medicament medicament = new Medicament();
        // medicament.setId(Integer.parseInt(cursor.getString(0)));
        assert cursor != null;
        medicament.setCis(cursor.getString(0));
        medicament.setCip13(cursor.getString(1));
        medicament.setMode_administration(cursor.getString(2));
        medicament.setNom(cursor.getString(3));
        medicament.setPresentation(cursor.getString(4));
        /*medicament.setStock(Double.parseDouble(cursor.getString(5)));
        medicament.setPrise(Double.parseDouble(cursor.getString(6)));
        medicament.setWarnThreshold(Integer.parseInt(cursor.getString(7)));
        medicament.setAlertThreshold(Integer.parseInt(cursor.getString(8)));*/

        // Set default values
        medicament.setStock(0);
        medicament.setPrise(0);
        medicament.setWarnThreshold(14);
        medicament.setAlertThreshold(7);

        // Log
        Log.d(MedicamentListActivity.Constants.TAG, "getDrug(" + cip13 + ")" + medicament.toString());

        // Return medicament

        cursor.close();
        return medicament;
    }
}

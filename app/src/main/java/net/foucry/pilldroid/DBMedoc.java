package net.foucry.pilldroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jfoucry on 5/25/16.
 */
public class DBMedoc  extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static String DATABASE_PATH = "/data/data/net.foucry.pilldroid/databases/";
    private static String DATABASE_NAME = "medicaments.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private static final String TABLE_NAME  = "medicaments";
//    private static final String MEDOC_ID    = "id";
    private static final String MEDOC_CIS   = "cis";
    private static final String MEDOC_CIP13 = "cip13";
//    private static final String MEDOC_CIP7  = "cip7";
    private static final String MEDOC_ADMIN = "mode_administration";
    private static final String MEDOC_NOM   = "nom";
    private static final String MEDOC_PRES  = "presentation";

    private static final String[] COLUMNS_NAMES = {MEDOC_CIS, MEDOC_CIP13, MEDOC_ADMIN, MEDOC_NOM, MEDOC_PRES};

    private static final String TAG = DBMedoc.class.getName();

    public DBMedoc(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if (dbExist) {
            Log.v("DB Exists", "db exists");
        } else {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error coping Database");
            }
        }
    }

    private boolean checkDatabase() {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "checkDatabase called");
        }

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database does not exists
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    private void copyDatabase() throws IOException {
        Log.e(TAG, "copyDatabase called");

        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];

        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer,0,length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDatabase() throws SQLiteException {
        Log.e(TAG, "openDatabase called");
        String myPath = DATABASE_PATH + DATABASE_NAME;

        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private DBMedoc dbMedoc;

    public Medicament getMedocByCIP13(String cip13) {
        Log.e(TAG, "getNedocByCIP13 - " + cip13);

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
        Log.d(TAG, "getDrug(" + cip13 + ")" + medicament.toString());

        // Return medicament

        return medicament;
    }
}

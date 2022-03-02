package net.foucry.pilldroid;


import android.content.Context;
import android.content.SharedPreferences;

    /**
     * Created by Lincoln on 05/05/16.
     */
    public class PrefManager {
        final SharedPreferences pref;
        SharedPreferences.Editor editor;

        // shared pref mode
        final int PRIVATE_MODE = 0;

        // Shared preferences file name
        private static final String PREF_NAME = "Pildroid-Prefs";
        private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
        private static final String DATABASE_VERSION = "DatabaseVersion";

        public PrefManager(Context context) {
            pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        }

        public void setFirstTimeLaunch(boolean isFirstTime) {
            editor = pref.edit();
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
            editor.apply();
        }

        public void setDatabaseVersion(int version) {
            editor = pref.edit();
            editor.putInt(DATABASE_VERSION, version);
            editor.apply();
        }

        public boolean isFirstTimeLaunch() {
            return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
        }
        public int getDatabaseVersion() {
            return  pref.getInt(DATABASE_VERSION, 0);
        }
    }

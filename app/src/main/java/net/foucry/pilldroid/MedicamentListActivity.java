package net.foucry.pilldroid;

import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.foucry.pilldroid.UtilDate.date2String;
import static net.foucry.pilldroid.Utils.intRandomExclusive;

/**
 * An activity representing a list of Medicaments. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MedicamentDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MedicamentListActivity extends AppCompatActivity {
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    // TODO: Change DEMO/DBDEMO form statci to non-static. In order to create fake data at only at launchtime
    private boolean mTwoPane;
    final Boolean DEMO = false;
    final Boolean DBDEMO = false;
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "Remove old notification");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private static final String TAG = MedicamentListActivity.class.getName();

    private DBHelper dbHelper;
    private DBMedoc dbMedoc;

    private List<Medicament> medicaments;

    private SimpleItemRecyclerViewAdapter mAdapter;

    public int getCount() {
        return medicaments.size();
    }

    public Medicament getItem(int position) {
        return medicaments.get(position);
    }

    public void constructMedsList()
    {
        Medicament currentMedicament;
        dbHelper = new DBHelper(getApplicationContext());

        if (!(medicaments == null)) {
            if (!medicaments.isEmpty()) {
                medicaments.clear();
            }
        }
        medicaments = dbHelper.getAllDrugs();

        Collections.sort(medicaments, new Comparator<Medicament>() {
            @Override
            public int compare(Medicament lhs, Medicament rhs) {
                return lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock());
            }
        });

        for (int position = 0 ; position < this.getCount() ; position++ ) {
            currentMedicament = this.getItem(position);
            currentMedicament.newStock(currentMedicament.getStock());
            dbHelper.updateDrug(currentMedicament);
        }

        View mRecyclerView = findViewById(R.id.medicament_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament_list);

        dbHelper = new DBHelper(this);
        dbMedoc = new DBMedoc(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        if (DEMO) {
            // Added to drop database each the app is launch.
            if (DBDEMO) {
                dbHelper.dropDrug();
            }
            if (dbHelper.getCount() == 0) {

                // String cis, String cip13, String nom, String mode_administration,
                // String presentation,double stock, double prise, int warn, int alert

                // Limit for randoms generator
                final int min_stock=5;
                final int max_stock=50;
                final int min_prise=0;
                final int max_prise=3;

                dbHelper.addDrug(new Medicament("60000011", "3400930000011", "Médicament test 01", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000012", "3400930000012", "Médicament test 02", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000013", "3400930000013", "Médicament test 03", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000014", "3400930000014", "Médicament test 04", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000015", "3400930000015", "Médicament test 05", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000016", "3400930000016", "Médicament test 06", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000017", "3400930000017", "Médicament test 07", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000018", "3400930000018", "Médicament test 08", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000019", "3400930000019", "Médicament test 09", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
                dbHelper.addDrug(new Medicament("60000010", "3400930000010", "Médicament test 10", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        intRandomExclusive(min_stock, max_stock), intRandomExclusive(min_prise, max_prise), 14, 7));
            }
        }

        constructMedsList();

        if (findViewById(R.id.medicament_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.help:
                //startActivity(new Intent(this, Help.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        scheduleJob();
    }

    public void onResume() {
        super.onResume();
    }

    /** scanNow
     *
     * @param view
     *  call ZXing Library to scan a new QR/EAN code
     */
    public void scanNow(View view) {
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == CUSTOMIZED_REQUEST_CODE) {
            Toast.makeText(this, "REQUEST_CODE = " + requestCode + "RESULT_CODE = " + resultCode, Toast.LENGTH_LONG).show();
            Log.d(TAG, "REQUEST_CODE = " + requestCode + " RESULT_CODE = " + resultCode);
            if (resultCode == 1) {
                constructMedsList();
            } else {
                Toast.makeText(this, "What are you doing here?", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "What are you doing here?");
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

            Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
            Log.d(TAG, "REQUEST_CODE = " + requestCode + "resultCode = " + resultCode);
            if (result.getContents() == null) {
                Intent originalIntent = result.getOriginalIntent();
                if (originalIntent == null) {
                    if (resultCode == 3) {
                        Toast.makeText(this, "Keyboard input", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Keyboard Input");
                        showInputDialog();
                    } else {
                        Log.d(TAG, "Cancelled scan");
                        Log.d(TAG, "REQUEST_CODE = " + requestCode + " RESULT_CODE = " + resultCode);
                    }
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                    Log.d(TAG, "Cancelled scan due to missing camera permission");
                    Log.d(TAG, "REQUEST_CODE = " + requestCode + " RESULT_CODE = " + resultCode);
                    Toast.makeText(this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "Scanned");
                Log.d(TAG, "REQUEST_CODE = " + requestCode + " RESULT_CODE = " + resultCode);
                Log.d(TAG, "result.getContents = " + result.getContents());
                Log.d(TAG, "format = " + result.getFormatName());

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String cip13;

                // Handle successful scan
                if (result.getFormatName().equals("CODE_128")) { //CODE_128
                    cip13 = result.getContents();
                } else {
                    cip13 = result.getContents().substring(4, 17);
                }

                // Get Medoc from database
                final Medicament scannedMedoc = dbMedoc.getMedocByCIP13(cip13);
                    askToAddInDB(scannedMedoc);
            }
        }
    }

    /**
     * show keybordInput dialoo
     */
    protected void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MedicamentListActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MedicamentListActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    String cip13 = editText.getText().toString();

                    Medicament med = dbMedoc.getMedocByCIP13(cip13);
                        askToAddInDB(med);
                })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /***
     * Ask if the medicament found in the database should be include in the
     * user database
     * @param Medicament med - medicament to be added
     */
    private void askToAddInDB(Medicament med) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(getString(R.string.app_name));

        if (med != null) {
            String msg = med.getNom() + " " + getString(R.string.msgFound);

            dlg.setMessage(msg);
            dlg.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> {
                // Nothing to do in case of cancel
            });
            dlg.setPositiveButton(getString(R.string.button_ok), (dialog, which) -> {
                // Add Medicament to DB then try to show it
                addMedToList(med);
            });
        } else {
            dlg.setMessage(getString(R.string.msgNotFound));
            dlg.setPositiveButton("OK", (dialog, which) -> {
                // nothing to do to just dismiss dialog
            });
        }
        dlg.show();
    }

    /**
     * Add New medimenant to the user database
     * @param Medicament med - medicament to be added
     */
    private void addMedToList(Medicament med)
    {
        med.setDateEndOfStock();
        mAdapter.addItem(med);
        dbHelper.addDrug(med);
    }
    public void scheduleJob() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        ComponentName componentName = new ComponentName(this, PillDroidJobService.class);
        JobInfo info = new JobInfo.Builder(24560, componentName)
                .setPersisted(true)
                .setPeriodic(15 *60 *1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, ("Job scheduled " + UtilDate.convertDate(now.getTime()+15 * 60*1000)));
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }
    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(24560);
        Log.d(TAG, "Job cancelled");
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapter = new SimpleItemRecyclerViewAdapter(medicaments);
        recyclerView.setAdapter(mAdapter);
    }

    private String getAppName() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException ignored) {}
        return (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
    }

    /**
     * SimpleItemRecyclerViewAdapter
     */
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Medicament> mValues;

        SimpleItemRecyclerViewAdapter(List<Medicament> items) {
            mValues = items;
        }

        void addItem(Medicament scannedMedoc) {
            if (!dbHelper.isMedicamentExist(scannedMedoc.getCip13())) {
                mValues.add(scannedMedoc);
                notifyDataSetChanged();
                dbHelper.addDrug(scannedMedoc);
            } else {
                Toast.makeText(getApplicationContext(), "aleready in the database", Toast.LENGTH_SHORT).show();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.medicament_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
            String dateEndOfStock = date2String(mValues.get(position).getDateEndOfStock(), dateFormat);

            Log.d(TAG, "dateEndOfStock == " + dateEndOfStock);
            Log.d(TAG, "stock == " + mValues.get(position).getStock());
            Log.d(TAG, "prise == " + mValues.get(position).getPrise());
            Log.d(TAG, "warn == " + mValues.get(position).getWarnThreshold());
            Log.d(TAG, "alert == " + mValues.get(position).getAlertThreshold());

            holder.mItem = mValues.get(position);
            holder.mIDView.setText(mValues.get(position).getCip13());
            holder.mContentView.setText(mValues.get(position).getNom());
            holder.mEndOfStock.setText(dateEndOfStock);

            // Test to change background programmaticaly
            if (mValues.get(position).getPrise() == 0) {
                holder.mView.setBackgroundResource(R.drawable.gradient_bg);
            } else {
                int remainingStock = (int) Math.floor(mValues.get(position).getStock() / mValues.get(position).getPrise());
                if (remainingStock <= mValues.get(position).getAlertThreshold()) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_alert);
                    holder.mIconView.setImageResource(R.drawable.lower_stock);
                } else if ((remainingStock > mValues.get(position).getAlertThreshold()) &&
                        (remainingStock <= (mValues.get(position).getWarnThreshold()))) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_warning);
                    holder.mIconView.setImageResource(R.drawable.warning_stock);
                } else {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_ok);
                    holder.mIconView.setImageResource(R.drawable.ok_stock);
                }
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Medicament medicamentCourant = mValues.get(position);
                    if (mTwoPane) {                                     // This part is used on tablets
                        Bundle arguments = new Bundle();
                        arguments.putSerializable("medicament", medicamentCourant);
                        MedicamentDetailFragment fragment = new MedicamentDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.medicament_detail_container, fragment)
                                .commit();
                    } else {                                            // This part is used on phones
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MedicamentDetailActivity.class);
                        intent.putExtra("medicament", medicamentCourant);
                        int requestCode =1;
                        startActivityForResult(intent, requestCode);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIDView;
            final TextView mContentView;
            final TextView mEndOfStock;
            final ImageView mIconView;

            Medicament mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIDView = view.findViewById(R.id.cip13);
                mContentView = view.findViewById(R.id.valeur);
                mEndOfStock = view.findViewById(R.id.endOfStock);
                mIconView = view.findViewById(R.id.list_image);
            }

            @NonNull
            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

/*
editText.addTextChangeListener( new TextWatcher() {
	@Override
	void afterTextChanged(Editable s){

	}
	void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}
	void onTextChanged(CharSequence s, int start, int before, int count) {

	}
});
 */
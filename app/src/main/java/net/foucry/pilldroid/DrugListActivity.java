package net.foucry.pilldroid;

import static net.foucry.pilldroid.UtilDate.date2String;
import static net.foucry.pilldroid.Utils.intRandomExclusive;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanOptions;

import net.foucry.pilldroid.dao.MedicDAO;
import net.foucry.pilldroid.models.Medic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An activity representing a list of Drugs is activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DrugDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class DrugListActivity extends AppCompatActivity {
    // Used for dev and debug
    final Boolean DEMO = true;
    final Boolean DBDEMO = false;

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    public final String BARCODE_FORMAT_NAME = "Barcode Format name";
    public final String BARCODE_CONTENT = "Barcode Content";

    private ActivityResultLauncher<ScanOptions> mBarcodeScannerLauncher;
    private static final String TAG = DrugListActivity.class.getName();

    private DBDrugs dbDrug;
    public PilldroidDatabase prescriptions;
    public PilldroidDatabase medications;

    private List<Medic> medics;         // used for prescriptions

    private SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    public void onStart() {
        super.onStart();

        medications = Room
                .databaseBuilder(getApplicationContext(), PilldroidDatabase.class, "medications")
                .createFromAsset("drugs.db")
                .build();

        // remove old notification
        Log.d(TAG, "Remove old notification and old job");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }

        // start tutorial
        Log.i(TAG, "Launch tutorial");
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Room database
        prescriptions = Room
                .databaseBuilder(getApplicationContext(), PilldroidDatabase.class, "prescriptions")
                .allowMainThreadQueries()
                .build();

        // Set view content
        setContentView(R.layout.activity_drug_list);

        DBHelper dbHelper = new DBHelper(this);
        dbDrug = new DBDrugs(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        // Added to drop database each the app is launch.
        if (DBDEMO) {
            dbHelper.dropDrug();
        }

        if (DEMO) {
          MedicDAO medicDAO = prescriptions.getMedicDAO();

          if (medicDAO.getMedicCount() == 0) {
              final int min_stock = 5;
              final int max_stock = 50;
              final int min_take = 0;
              final int max_take = 3;

              for (int i = 1; i < 9; i++) {
                  Medic medic = new Medic();
                  medic.setName("Medicament test " + i);
                  medic.setCip13("340093000001" + i);
                  medic.setCis("6000001" + i);
                  medic.setAdministration_mode("oral");
                  medic.setPresentation("plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)");
                  medic.setStock((float) intRandomExclusive(min_stock, max_stock));
                  medic.setTake((float) intRandomExclusive(min_take, max_take));
                  medic.setWarning(14);
                  medic.setAlert(7);
                  medic.setLast_update(UtilDate.dateAtNoon(new Date()).getTime());

                  medicDAO.insert(medic);
              }
              List<Medic> prescriptions = medicDAO.getAllMedics();
              System.out.println(prescriptions);
              Log.d(TAG, "prescriptions ==" + prescriptions);
          }
        }

        mBarcodeScannerLauncher = registerForActivityResult(new PilldroidScanContract(),
                result -> {
                    if (result.getContents() == null) {
                        Intent originalIntent = result.getOriginalIntent();
                        Bundle bundle = originalIntent.getExtras();
                        if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                            Log.d(TAG, "Missing camera permission");
                            Toast.makeText(this, R.string.missing_camera_permission, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "bundle == " + bundle.getInt("returnCode"));
                            int returnCode = bundle.getInt("returnCode");
                            int resultCode = bundle.getInt("resultCode");

                            if (resultCode != 1) {
                                if (returnCode == 3) {
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(this, "Keyboard input",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d(TAG, "Keyboard Input");
                                    showInputDialog();
                                } else if (returnCode == 2) {
                                    Toast.makeText(this, R.string.cancelled_scan, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d(TAG, "Scanned");
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(this, "Scanned: " + bundle.getString(BARCODE_FORMAT_NAME),
                                            Toast.LENGTH_LONG).show();
                                }
                                String cip13;

                                // Handle successful scan

                                Log.d(TAG, "formatName = " + bundle.getString(BARCODE_FORMAT_NAME));

                                switch (bundle.getString(BARCODE_FORMAT_NAME)) {
                                    case "CODE_128":
                                    case "EAN_13":  //CODE_128 || EAN 13
                                        cip13 = bundle.getString(BARCODE_CONTENT);
                                        break;
                                    case "CODE_39":
                                        cip13 = dbDrug.getCIP13FromCIP7(bundle.getString(BARCODE_CONTENT));
                                        break;
                                    case "DATA_MATRIX":
                                        cip13 = bundle.getString(BARCODE_CONTENT).substring(4, 17);
                                        break;
                                    default:
                                        scanNotOK();
                                        return;
                                }

                                // Get Drug from database
                                //final Drug scannedDrug = dbDrug.getDrugByCIP13(cip13);
                                MedicDAO medicationDAO = medications.getMedicDAO();
                                final Medic scannedMedication = medicationDAO.getMedicByCIP13(cip13);

                                // add Drug to prescription database
                                askToAddInDB(scannedMedication);
                            }
                        }
                    }
                });

        constructDrugsList();
    }

    public void constructDrugsList() {

        MedicDAO medicDAO = prescriptions.getMedicDAO();
        medics = medicDAO.getAllMedics();

        View mRecyclerView = findViewById(R.id.drug_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
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
                PrefManager prefManager = new PrefManager(this);
                prefManager.setFirstTimeLaunch(true);

                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CUSTOMIZED_REQUEST_CODE) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode +
                        "RESULT_CODE = " + resultCode, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "REQUEST_CODE = " + requestCode + " RESULT_CODE = " + resultCode);
            constructDrugsList();
        }
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        AlarmReceiver.scheduleAlarm(this);
    }

    public void onResume() {
        super.onResume();
    }


    // Launch scan
    public void onButtonClick() {
        Log.d(TAG, "add medication");
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.DATA_MATRIX, ScanOptions.CODE_39,
                ScanOptions.CODE_128);
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        //options.setTimeout(3600);
        options.setCaptureActivity(CustomScannerActivity.class);
        options.setBeepEnabled(true);
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.INVERTED_SCAN);

        Log.d(TAG, "scanOptions == " +  options);
        mBarcodeScannerLauncher.launch(options);
    }

    /**
     * show keyboardInput dialog
     */
    protected void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(DrugListActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DrugListActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = promptView.findViewById(R.id.edittext);
        editText.setHint("1234567890123");
        // setup a dialog window

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    String cip13 = editText.getText().toString();

                    MedicDAO medicationsDAO = medications.getMedicDAO();
                    Medic aMedic = medicationsDAO.getMedicByCIP13(cip13);
                    //Medic aMedic = medications.getDrugByCIP13(cip13);
                    askToAddInDB(aMedic);
                })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                alert.getButton(alert.BUTTON_POSITIVE).setEnabled(s.length() == 13);
            }
        });
        alert.show();
    }

    /**
     * Ask if the drug found in the database should be include in the
     * user database
     *
     * @param aMedic Medic- medication to be added
     */
    private void askToAddInDB(Medic aMedic) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(getString(R.string.app_name));

        if (aMedic != null) {
            String msg = aMedic.getName() + " " + getString(R.string.msgFound);

            dlg.setMessage(msg);
            dlg.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> {
                // Nothing to do in case of cancel
            });
            dlg.setPositiveButton(getString(R.string.button_ok), (dialog, which) -> {
                // Add Drug to DB then try to show it
                addDrugToList(aMedic);
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
     * Tell user that the barre code cannot be interpreted
     */
    private void scanNotOK() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(getString(R.string.app_name));

        dlg.setMessage(R.string.notInterpreted);
        dlg.setPositiveButton("OK", (dialog, which) -> {
            // Nothing to do just dismiss dialog
        });
        dlg.show();
    }

    /**
     * Add New drug to the user database
     *
     * @param aMedic Medic - medication to be added
     */

    @SuppressWarnings("deprecation")
    private void addDrugToList(Medic aMedic) {
        aMedic.getDateEndOfStock();
        mAdapter.addItem(aMedic);

        Log.d(TAG, "Call DrugDetailActivity");
        Context context = this;
        Intent intent = new Intent(context, DrugDetailActivity.class);
        intent.putExtra("medic", (Parcelable) aMedic);

        startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private String getAppName() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException ignored) {
        }
        return (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
    }

    /**
     * setupRecyclerView (list of drugs)
     *
     * @param recyclerView RecyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapter = new SimpleItemRecyclerViewAdapter(medics);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * SimpleItemRecyclerViewAdapter
     */
    public class SimpleItemRecyclerViewAdapter extends
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Medic> mValues;

        SimpleItemRecyclerViewAdapter(List<Medic> items) {
            mValues = items;
        }

        void addItem(Medic scannedMedic) {
            MedicDAO medicDAO = prescriptions.getMedicDAO();
            if (medicDAO.getMedicByCIP13(scannedMedic.getCip13()) == null) {
                mValues.add(scannedMedic);
                //notifyDataSetChanged();
                notifyItemInserted(mValues.size());
                medicDAO.insert(scannedMedic);
            } else {
                Toast.makeText(getApplicationContext(), "already in the database", Toast.LENGTH_LONG).show();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drug_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onBindViewHolder(final ViewHolder holder, int dummy) {
            final int position = holder.getBindingAdapterPosition();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
            String dateEndOfStock = date2String(mValues.get(position).getDateEndOfStock(), dateFormat);

            Log.d(TAG, "Drug name == " + mValues.get(position).getName());
            Log.d(TAG, "dateEndOfStock == " + dateEndOfStock);
            Log.d(TAG, "stock == " + mValues.get(position).getStock());
            Log.d(TAG, "take == " + mValues.get(position).getTake());
            Log.d(TAG, "warn == " + mValues.get(position).getWarnThreshold());
            Log.d(TAG, "alert == " + mValues.get(position).getAlertThreshold());

            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());
            holder.mEndOfStock.setText(dateEndOfStock);


            // Test to change background programmatically
            if (mValues.get(position).getTake() == 0) {
                holder.mView.setBackgroundResource(R.drawable.gradient_bg);
                holder.mIconView.setImageResource(R.drawable.ic_suspended_pill);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Medic aMedic = mValues.get(position);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DrugDetailActivity.class);
                        intent.putExtra("medic", (Parcelable) aMedic);
                        startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    }
                });
            } else {
                int remainingStock = (int) Math.floor(mValues.get(position).getStock() / mValues.get(position).getTake());
                if (remainingStock <= mValues.get(position).getAlertThreshold()) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_alert);
                    holder.mIconView.setImageResource(R.drawable.lower_stock_vect);
                } else if ((remainingStock > mValues.get(position).getAlertThreshold()) &&
                        (remainingStock <= (mValues.get(position).getWarnThreshold()))) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_warning);
                    holder.mIconView.setImageResource(R.drawable.warning_stock_vect);
                } else {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_ok);
                    holder.mIconView.setImageResource(R.drawable.ok_stock_vect);
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Medic medic = mValues.get(position);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DrugDetailActivity.class);
                        intent.putExtra("medic", medic);
                        startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            final TextView mEndOfStock;
            final ImageView mIconView;
            public Medic mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = view.findViewById(R.id.value);
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

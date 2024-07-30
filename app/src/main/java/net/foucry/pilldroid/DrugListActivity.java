package net.foucry.pilldroid;

import static net.foucry.pilldroid.UtilDate.date2String;
import static net.foucry.pilldroid.Utils.intRandomExclusive;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.zxing.client.android.BuildConfig;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanOptions;

import net.foucry.pilldroid.dao.MedicinesDAO;
import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.databases.MedicineDatabase;
import net.foucry.pilldroid.databases.PrescriptionDatabase;
import net.foucry.pilldroid.models.Medicine;
import net.foucry.pilldroid.models.Prescription;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * An activity representing a list of Drugs is activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DrugDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class DrugListActivity extends AppCompatActivity {
    private static final String TAG = DrugListActivity.class.getName();
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    public final String BARCODE_FORMAT_NAME = "Barcode Format name";
    public final String BARCODE_CONTENT = "Barcode Content";
    // Used for dev and debug
    final Boolean DEMO = false;
    public PrescriptionDatabase prescriptions;
    public MedicineDatabase medicines;
    private ActivityResultLauncher<ScanOptions> mBarcodeScannerLauncher;
    private List<Prescription> prescriptionList;         // used for prescriptions

    private RecyclerViewAdapter mAdapter;

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            int version = Build.VERSION.SDK_INT;
            String versionRelease = Build.VERSION.RELEASE;

            Log.e(TAG, "manufacturer " + manufacturer
                    + " \n model " + model
                    + " \n version " + version
                    + " \n versionRelease " + versionRelease
            );
        }

        // Create medicines Room database from drugs.db files
        medicines = MedicineDatabase.getInstanceDatabase(this);

        // Create prescriptions Room database
        prescriptions = PrescriptionDatabase.getInstanceDatabase(this);

        // Manually migrate old database to room
        PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();
        DBHelper dbHelper = new DBHelper(this);
        if (dbHelper.getCount() != 0) {
            List<Drug> drugs = dbHelper.getAllDrugs();
            for (int count = 0; count < dbHelper.getCount(); count++) {
                Drug drug = drugs.get(count);
                Prescription prescription = new Prescription();

                if (prescriptionsDAO.getMedicByCIP13(drug.getCip13()) == null) {
                    prescription.setName(drug.getName());
                    prescription.setCip13(drug.getCip13());
                    prescription.setCis(drug.getCis());
                    prescription.setPresentation(drug.getPresentation());
                    prescription.setAdministration_mode(drug.getAdministration_mode());
                    prescription.setStock((float) drug.getStock());
                    prescription.setTake((float) drug.getTake());
                    prescription.setWarning(drug.getWarnThreshold());
                    prescription.setAlert(drug.getAlertThreshold());
                    prescription.setLast_update(drug.getDateLastUpdate());

                    prescriptionsDAO.insert(prescription);
                } else {
                    Log.i(TAG, "Already in the database");
                }
            }
            dbHelper.dropDrug();
        }
        // remove old notification
        Log.d(TAG, "Remove old notification and old job");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }

        // start tutorial (only in non debug mode)
        // if(!net.foucry.pilldroid.BuildConfig.DEBUG) {
        Log.i(TAG, "Launch tutorial");
        startActivity(new Intent(this, WelcomeActivity.class));
        // }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (!AlarmReceiver.isAlarmScheduled(this)) {
            AlarmReceiver.scheduleAlarm(this);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Room database
        prescriptions = Room
                .databaseBuilder(getApplicationContext(), PrescriptionDatabase.class, "prescriptions")
                .allowMainThreadQueries()
                .build();

        // Set view content
        setContentView(R.layout.drug_list_activity);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }
        FloatingActionButton mFloatingActionButton = findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(v-> onButtonClick());

        if (DEMO) {
            PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();

            if (prescriptionsDAO.getMedicCount() == 0) {
                final int min_stock = 5;
                final int max_stock = 50;
                final int min_take = 0;
                final int max_take = 3;

                for (int i = 1; i < 9; i++) {
                    Prescription prescription = new Prescription();
                    prescription.setName("Medicament test " + i);
                    prescription.setCip13("340093000001" + i);
                    prescription.setCis("6000001" + i);
                    prescription.setAdministration_mode("oral");
                    prescription.setPresentation("plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)");
                    prescription.setStock((float) intRandomExclusive(min_stock, max_stock));
                    prescription.setTake((float) intRandomExclusive(min_take, max_take));
                    prescription.setWarning(14);
                    prescription.setAlert(7);
                    prescription.setLast_update(UtilDate.dateAtNoon(new Date()).getTime());

                    prescriptionsDAO.insert(prescription);
                }
                List<Prescription> prescriptions = prescriptionsDAO.getAllMedics();
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
                            assert bundle != null;
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
                                Log.d(TAG, "Scanned formatName = " + bundle.getString(BARCODE_FORMAT_NAME));
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(this, "Scanned: " + bundle.getString(BARCODE_FORMAT_NAME),
                                            Toast.LENGTH_LONG).show();
                                }

                                String cip13;
                                switch (Objects.requireNonNull(bundle.getString(BARCODE_FORMAT_NAME))) {
                                    case "CODE_128":
                                    case "EAN_13":  //CODE_128 || EAN 13
                                        cip13 = bundle.getString(BARCODE_CONTENT);
                                        break;
                                    case "DATA_MATRIX":
                                        cip13 = Objects.requireNonNull(bundle.getString(BARCODE_CONTENT)).substring(4, 17);
                                        break;
                                    default:
                                        scanNotOK();
                                        return;
                                }

                                // Get Drug from database
                                MedicinesDAO medicinesDAO = medicines.getMedicinesDAO();
                                final Medicine scannedMedicine = medicinesDAO.getMedicineByCIP13(cip13);

                                // add Drug to prescription database
                                askToAddInDB(scannedMedicine);
                            }
                        }
                    }
                });
        constructDrugsList();
    }

    public void constructDrugsList() {
        PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();
        prescriptionList = prescriptionsDAO.getAllMedics();

        // Sorting list by dateEndOfStock
        Utils.sortPrescriptionList(prescriptionList);

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
        int id = item.getItemId();
        if (id == R.id.about) {
            startActivity(new Intent(this, About.class));
            return true;
        } else if (id == R.id.help) {
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
        constructDrugsList();
    }

    public void onResume() {
        super.onResume();
    }

    // Launch scan
    public void onButtonClick() {
        Log.d(TAG, "add medication");
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.DATA_MATRIX, ScanOptions.CODE_128);
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setTimeout(60);
        options.setCaptureActivity(CustomScannerActivity.class);
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        options.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.INVERTED_SCAN);

        Log.d(TAG, "scanOptions == " + options);
        mBarcodeScannerLauncher.launch(options);
    }

    /**
     * show keyboardInput dialog
     */
    protected void showInputDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.input_dialog);

        MaterialButton ok = dialog.findViewById(R.id.agreed);
        MaterialButton cancel = dialog.findViewById(R.id.notagreed);
        ok.setEnabled(false);
        ok.setBackground(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.rounded_btn_disabled)));
        MaterialTextView title = dialog.findViewById(R.id.title);
        final EditText editText = dialog.findViewById(R.id.editcip13);
        String cip13 = String.valueOf(editText.getText());

        // TODO change the color of ok button when the number of character is correct.

        ok.setText(R.string.button_ok);
        cancel.setText(R.string.button_cancel);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //alert.getButton(alert.BUTTON_POSITIVE).setEnabled(s.length() == 8);
                if (s.length() == 8) {
                    ok.setEnabled(true);
                    ok.setBackground(Objects.requireNonNull(ContextCompat.getDrawable(editText.getContext(), R.drawable.rounded_btn)));
                }
                else {
                    ok.setEnabled(false);
                    ok.setBackground(Objects.requireNonNull(ContextCompat.getDrawable(editText.getContext(), R.drawable.rounded_btn_disabled)));
                }
            }
        });
        ok.setOnClickListener(v -> {
            dialog.cancel();
            Log.i("EditText Value",editText.getEditableText().toString());
            MedicinesDAO medicinesDAO = medicines.getMedicinesDAO();
            Medicine aMedicine = medicinesDAO.getMedicineByCIP13(cip13);
            askToAddInDB(aMedicine);
        });
        cancel.setOnClickListener(v -> {
            dialog.cancel();
            Log.i(TAG, "dismiss dialog");
        });

        dialog.show();
    }

    /**
     * Ask if the drug found in the database should be include in the
     * user database
     *
     * @param aMedicine Prescription- medication to be added
     */
    private void askToAddInDB(Medicine aMedicine) {
        final Dialog dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dlg.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg.setContentView(R.layout.custom_dialog_layout_one_button);
        dlg.setCancelable(true);
        MaterialTextView msg = dlg.findViewById(R.id.msg);
        String msgString;
        MaterialTextView cpl = dlg.findViewById(R.id.cpl);
        ShapeableImageView icon = dlg.findViewById(R.id.image);
        MaterialButton btn = dlg.findViewById(R.id.txtClose);
        dlg.show();

        if (aMedicine != null) {
            msgString = aMedicine.getName() + " " + getString(R.string.msgFound);
            msg.setText(msgString);
            cpl.setText(getString(R.string.addInList));
            icon.setImageResource(R.drawable.tickmark);
            btn.setText(getString(R.string.Yes));
            btn.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                dlg.dismiss();
                finish();
                addDrugToList(Utils.medicine2prescription(aMedicine));
            });
        } else {
            msgString = getString(R.string.msgNotFound);
            msg.setText(msgString);
            cpl.setText("");
            icon.setImageResource(R.drawable.tickcross);
            btn.setText(getString(R.string.button_close));
            btn.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                dlg.dismiss();
                finish();
            });
        }
        dlg.show();
    }

    /**
     * Tell user that the barre code cannot be interpreted
     */
    private void scanNotOK() {
        MaterialAlertDialogBuilder dlg = new MaterialAlertDialogBuilder(this);
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
     * @param aPrescription Prescription - medication to be added
     */

    @SuppressWarnings("deprecation")
    private void addDrugToList(Prescription aPrescription) {
        aPrescription.getDateEndOfStock();
        mAdapter.addItem(aPrescription);

        Log.d(TAG, "Call DrugDetailActivity");
        Context context = this;
        Intent intent = new Intent(context, DrugDetailActivity.class);
        intent.putExtra("prescription", aPrescription);

        startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * setupRecyclerView (list of drugs)
     *
     * @param recyclerView RecyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapter = new RecyclerViewAdapter(prescriptionList);
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, (ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();

                Prescription prescription = prescriptionList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    prescriptionList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    // Remove item form database
                    PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();
                    prescriptionsDAO.delete(prescription);
                } else {
                    // Call DetailView
                    Intent intent = new Intent(getApplicationContext(), DrugDetailActivity.class);
                    intent.putExtra("prescription", prescription);
                    startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
                }

                Snackbar.make(recyclerView, prescription.getName(),
                        Snackbar.LENGTH_LONG).setAction(R.string.Undo, v -> {
                            prescriptionList.add(position, prescription);
                            mAdapter.notifyItemInserted(position);
                        }).setActionTextColor(getResources().getColor(R.color.bg_screen1))
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Drawable icon;
                    icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_trash_can_outline);

                    int xMarkMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.fab_margin);

                    assert icon != null;
                    int intrinsicWidth = icon.getIntrinsicWidth();
                    int intrinsicHeight = icon.getIntrinsicHeight();
                    int itemHeight = itemView.getBottom() - itemView.getTop();

                    if (dX > 0) {
                        p.setColor(getColor(R.color.bg_screen3));
                        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_edit_black_48dp);

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        int xMarkLeft = itemView.getLeft() + xMarkMargin;
                        int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;// +xMarkTop;
                        assert icon != null;
                        icon.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                    } else {
                        p.setColor(getColor(R.color.bg_screen4));
                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);


                        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                        int xMarkRight = itemView.getRight() - xMarkMargin;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        icon.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                    }
                    icon.draw(c);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        }).attachToRecyclerView(recyclerView);

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
     * SimpleItemRecyclerViewAdapter
     */
    public class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final List<Prescription> mValues;

        RecyclerViewAdapter(List<Prescription> items) {
            mValues = items;
        }

        void addItem(Prescription scannedPrescription) {
            PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();
            if (prescriptionsDAO.getMedicByCIP13(scannedPrescription.getCip13()) == null) {
                mValues.add(scannedPrescription);
                //notifyDataSetChanged();
                notifyItemInserted(mValues.size());
                prescriptionsDAO.insert(scannedPrescription);
            } else {
                Toast.makeText(getApplicationContext(), "already in the database", Toast.LENGTH_LONG).show();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drug_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onBindViewHolder(@NonNull final ViewHolder holder, int dummy) {
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
            if (mValues.get(position).getTake() > 0) {
                holder.mEndOfStock.setText(dateEndOfStock);
            } else {
                holder.mEndOfStock.setText("");
            }

            // Test to change background programmatically
            if (mValues.get(position).getTake() == 0) {
                holder.mView.setBackgroundResource(R.drawable.gradient_bg);
                holder.mIconView.setImageResource(R.drawable.ic_suspended_pill);

                holder.mView.setOnClickListener(v -> {
                    Prescription aPrescription = mValues.get(position);
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DrugDetailActivity.class);
                    intent.putExtra("prescription", aPrescription);
                    startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

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

                holder.mView.setOnClickListener(v -> {
                    Prescription prescription = mValues.get(position);
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DrugDetailActivity.class);
                    intent.putExtra("prescription", prescription);
                    startActivityForResult(intent, CUSTOMIZED_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                });
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final MaterialTextView mContentView;
            final MaterialTextView mEndOfStock;
            final ShapeableImageView mIconView;
            public Prescription mItem;

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

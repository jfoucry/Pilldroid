package net.foucry.pilldroid;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static net.foucry.pilldroid.UtilDate.date2String;
import static net.foucry.pilldroid.Utils.doubleRandomInclusive;

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
    private boolean mTwoPane;
    final static Boolean DEMO = true;
    final static Random random = new Random();
    // Log TAG String
    public interface Constants {
        String TAG = "nef.foucry.pilldroid";
    }

    private static DBHelper dbHelper;
    private static DBMedoc dbMedoc;

    private SimpleCursorAdapter drugAdapter;
    private List<Medicament> medicaments;

    private View mRecyclerView;
    private SimpleItemRecyclerViewAdapter mAdapter;

    // For the notifications
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament_list);

        // Register for alarm

//        RegisterAlarmBroadcast();

        dbHelper = new DBHelper(this);
        dbMedoc = new DBMedoc(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        if (DEMO) {
            if (dbHelper.getCount() == 0) {

                // String cis, String cip13, String nom, String mode_administration,
                // String presentation,double stock, double prise, int warn, int alert

                dbHelper.addDrug(new Medicament("60000011", "3400930000011", "Médicament test 01", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000012", "3400930000012", "Médicament test 02", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000013", "3400930000013", "Médicament test 03", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000014", "3400930000014", "Médicament test 04", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000015", "3400930000015", "Médicament test 05", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000016", "3400930000016", "Médicament test 06", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000017", "3400930000017", "Médicament test 07", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000018", "3400930000018", "Médicament test 08", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000019", "3400930000019", "Médicament test 09", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
                dbHelper.addDrug(new Medicament("60000010", "3400930000010", "Médicament test 10", "orale",
                        "plaquette(s) thermoformée(s) PVC PVDC aluminium de 10 comprimé(s)",
                        doubleRandomInclusive(0, 100), doubleRandomInclusive(0, 10), 14, 7));
            }
        }

        if (this.medicaments == null) {
            this.medicaments = dbHelper.getAllDrugs();

            Collections.sort(this.medicaments, new Comparator<Medicament>() {
                @Override
                public int compare(Medicament lhs, Medicament rhs) {
                    return lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock());
                }
            });
        }

        mRecyclerView = findViewById(R.id.medicament_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);

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

//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
        scheduleNotification(getNotification("10 second delay"), 10000);
    }

/*    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }*/

    public void scanNow(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        //intent.putExtra("SCAN_MODE", "CODE_128");
        intent.putExtra("SCAN_FORMATS", "CODE_18,DATA_MATRIX");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Context context = getApplicationContext();
        String cip13 = null;
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.i(Constants.TAG, "Format:" + format);
                Log.i(Constants.TAG, "Content:" + contents);

                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle(context.getString(R.string.app_name));

                // Handle successful scan
                if (format.equals("CODE_128")) { //CODE_128
                    cip13 = contents;
                } else {
                    cip13 = contents.substring(4, 17);
                }

                dbMedoc.openDatabase();
                final Medicament scannedMedoc = dbMedoc.getMedocByCIP13(cip13);
                dbMedoc.close();

                if (scannedMedoc != null) {
                    String msg = scannedMedoc.getNom() + " " + getString(R.string.msgFound);

                    dlg.setMessage(msg);
                    dlg.setNegativeButton(context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing to do in case of cancel
                        }
                    });
                    dlg.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Add Medicament to DB then try to show it
                            scannedMedoc.setDateEndOfStock();
                            dbHelper.addDrug(scannedMedoc);
                            mAdapter.addItem(medicaments.size()-1,scannedMedoc);
                        }
                    });
                    dlg.show();
                } else {
                    dlg.setMessage(context.getString(R.string.msgNotFound));
                    dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nothing to do to just dismiss dialog
                        }
                    });

                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(context, "Scan annulé", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapter = (SimpleItemRecyclerViewAdapter) new SimpleItemRecyclerViewAdapter(medicaments);
        recyclerView.setAdapter(mAdapter);
    }

    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }
    // Received Alarm, display a toast
/*    private void RegisterAlarmBroadcast() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Vous devez passer à la pharmacie", Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(mReceiver, new IntentFilter("net.foucry.pilldroid"));
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("net.foucry.pilldroid"),0);
        alarmManager = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));
    }

    private void UnregisterAlarmBroadcast(){
        alarmManager.cancel(pendingIntent);
        getBaseContext().unregisterReceiver(mReceiver);
    }*/
    /**
     * SimpleItemRecyclerViewAdapter
     */
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Medicament> mValues;

        public SimpleItemRecyclerViewAdapter(List<Medicament> items) {
            mValues = items;
        }

        public void addItem(int position, Medicament scannedMedoc) {
            mValues.add(scannedMedoc);
            notifyDataSetChanged();
            dbHelper.addDrug(scannedMedoc);
        }

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

            Log.d(Constants.TAG, "dateEndOfStock == " + dateEndOfStock);
            Log.d(Constants.TAG, "stock == " + mValues.get(position).getStock());
            Log.d(Constants.TAG, "prise == " + mValues.get(position).getPrise());
            Log.d(Constants.TAG, "warn == " + mValues.get(position).getWarnThreshold());
            Log.d(Constants.TAG, "alert == " + mValues.get(position).getAlertThreshold());

            holder.mItem = mValues.get(position);
            holder.mIDView.setText(mValues.get(position).getCip13());
            holder.mContentView.setText(mValues.get(position).getNom());
            holder.mEndOfStock.setText(dateEndOfStock);

            // Test to change background programmaticaly
            if (mValues.get(position).getPrise() == 0) {
                holder.mView.setBackgroundResource(R.drawable.gradient_bg);
            } else {
                int remainingStock = (int) Math.floor(mValues.get(position).getStock()/mValues.get(position).getPrise());
                if (remainingStock <= mValues.get(position).getAlertThreshold()) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_alert);
                    holder.mIconView.setImageResource(R.drawable.stock_alert);
                } else if ((remainingStock > mValues.get(position).getAlertThreshold()) &&
                        (remainingStock <= (mValues.get(position).getWarnThreshold()))) {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_warning);
                    holder.mIconView.setImageResource(R.drawable.stock_warn);
                } else {
                    holder.mView.setBackgroundResource(R.drawable.gradient_bg_ok);
                    holder.mIconView.setImageResource(R.drawable.stock_ok);
                }
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Medicament medicamentCourant = (Medicament) mValues.get(position);
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable("medicament", medicamentCourant);
                        MedicamentDetailFragment fragment = new MedicamentDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.medicament_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MedicamentDetailActivity.class);
                        intent.putExtra("medicament", medicamentCourant);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIDView;
            public final TextView mContentView;
            public final TextView mEndOfStock;
            public final ImageView mIconView;

            public Medicament mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIDView = (TextView) view.findViewById(R.id.cip13);
                mContentView = (TextView) view.findViewById(R.id.valeur);
                mEndOfStock = (TextView) view.findViewById(R.id.endOfStock);
                mIconView = (ImageView) view.findViewById(R.id.list_image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

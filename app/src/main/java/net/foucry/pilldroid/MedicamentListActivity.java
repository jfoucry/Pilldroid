package net.foucry.pilldroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
    private SimpleCursorAdapter drugAdapter;
    private List<Medicament> medicaments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament_list);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Snackbar.make(view, "Will be used to add a drug to the list", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "CODE_128");
                //intent.putExtra("SCAN_FORMATS", "EAN_13,DATA_MATRIX");
                startActivityForResult(intent, 0);
            }
        });


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
        View recyclerView = findViewById(R.id.medicament_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.medicament_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(medicaments));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Medicament> mValues;

        public SimpleItemRecyclerViewAdapter(List<Medicament> items) {
            mValues = items;
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

            public void scanNow(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "CODE_128");
                //intent.putExtra("SCAN_FORMATS", "EAN_13,DATA_MATRIX");
                startActivityForResult(intent, 0);
            }

            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                if (requestCode == 0) {
                    if (resultCode == RESULT_OK) {
                        String contents = intent.getStringExtra("SCAN_RESULT");
                        String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                        Log.i("Prout", format);
                        Log.i("Prout", contents);
                        // Handle successful scan
                    } else if (resultCode == RESULT_CANCELED) {
                        // Handle cancel
                    }
                }
            }
        }
    }
}

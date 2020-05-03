package net.foucry.pilldroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static net.foucry.pilldroid.R.id.detail_toolbar;

/**
 * An activity representing a single Medicament detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MedicamentListActivity}.
 */
public class MedicamentDetailActivity extends AppCompatActivity {

    private static final String TAG = MedicamentDetailActivity.class.getName();

    Medicament medicament;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        /* fetching the string passed with intent using ‘extras’*/

        medicament = (Medicament) extras.getSerializable("medicament");

        Log.d(TAG, "medicament == " + medicament.toString());

        setContentView(R.layout.activity_medicament_detail);
        Toolbar toolbar = findViewById(detail_toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Will be use to save changes in a drug", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d(TAG, "Click on save icone");

                getMedicamentChanges();
                setResult(1);

                finish();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(medicament.getNom());
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable("medicament",
                    getIntent().getSerializableExtra("medicament"));
            MedicamentDetailFragment fragment = new MedicamentDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.medicament_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MedicamentListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMedicamentChanges()
    {
        Log.d(TAG, "Time to save new values");

        DBHelper dbHelper = new DBHelper(this);

        View stockView;
        View priseView;
        View warningView;
        View alertView;

        stockView = (View) findViewById(R.id.stock_cell);
        EditText stockTextView = stockView.findViewById(R.id.valeur);
        String stockValue = stockTextView.getText().toString();

        priseView = (View) findViewById(R.id.prise_cell);
        TextView priseTextView = priseView.findViewById(R.id.valeur);
        String priseValue = priseTextView.getText().toString();

        alertView = (View) findViewById(R.id.alert_cell);
        TextView alertTextView = alertView.findViewById(R.id.valeur);
        String alertValue = alertTextView.getText().toString();

        warningView = (View) findViewById(R.id.warning_cell);
        TextView warningTextView = warningView.findViewById(R.id.valeur);
        String warningValue = warningTextView.getText().toString();

        Log.d(TAG, "StockValue ==  "+ stockValue);
        Log.d(TAG, "PriseValue ==  "+ priseValue);
        Log.d(TAG, "AlertValue ==  "+ alertValue);
        Log.d(TAG, "WarningValue ==  "+ warningValue);
        Log.d(TAG, "medicamentID == "+ medicament.getId());
        Log.d(TAG, "medicament == "+ medicament.toString());

        medicament.setStock(Double.parseDouble(stockValue));
        medicament.setPrise(Double.parseDouble(priseValue));
        medicament.setWarnThreshold(Integer.parseInt(warningValue));
        medicament.setAlertThreshold(Integer.parseInt(alertValue));
        medicament.setDateLastUpdate();
        medicament.setDateEndOfStock();

        dbHelper.updateDrug(medicament);
        return;
    }
}

package net.foucry.pilldroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

import static net.foucry.pilldroid.R.id.detail_toolbar;

/**
 * An activity representing a single Drug detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link DrugListActivity}.
 */
public class DrugDetailActivity extends AppCompatActivity {

    private static final String TAG = DrugDetailActivity.class.getName();

    Drug drug;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        /* fetching the string passed with intent using ‘extras’*/

        assert extras != null;
        drug = (Drug) extras.getSerializable("drug");

        assert drug != null;
        Log.d(TAG, "drug == " + drug.toString());

        setContentView(R.layout.activity_durg_detail);
        Toolbar toolbar = findViewById(detail_toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Click on save icon");

                getMDrugChanges();
                setResult(1);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(drug.getName());
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
            arguments.putSerializable("drug",
                    getIntent().getSerializableExtra("drug"));
            DrugDetailFragment fragment = new DrugDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drug_detail_container, fragment)
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
            navigateUpTo(new Intent(this, DrugListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMDrugChanges()
    {
        Log.d(TAG, "Time to save new values");

        DBHelper dbHelper = new DBHelper(this);

        Drug newDrug = dbHelper.getDrugByCIP13(drug.getCip13());

        View stockView;
        View takeView;
        View warningView;
        View alertView;

        stockView = findViewById(R.id.stock_cell);
        EditText stockTextView = stockView.findViewById(R.id.value);
        String stockValue = stockTextView.getText().toString();

        takeView = findViewById(R.id.take_cell);
        TextView takeTextView = takeView.findViewById(R.id.value);
        String takeValue = takeTextView.getText().toString();

        alertView = findViewById(R.id.alert_cell);
        TextView alertTextView = alertView.findViewById(R.id.value);
        String alertValue = alertTextView.getText().toString();

        warningView = findViewById(R.id.warning_cell);
        TextView warningTextView = warningView.findViewById(R.id.value);
        String warningValue = warningTextView.getText().toString();


        Log.d(TAG, "StockValue ==  "+ stockValue);
        Log.d(TAG, "TakeValue ==  "+ takeValue);
        Log.d(TAG, "AlertValue ==  "+ alertValue);
        Log.d(TAG, "WarningValue ==  "+ warningValue);
        Log.d(TAG, "medicamentID == "+ drug.getId());
        Log.d(TAG, "drug == "+ drug.toString());

        newDrug.setStock(Double.parseDouble(stockValue));
        newDrug.setTake(Double.parseDouble(takeValue));
        newDrug.setWarnThreshold(Integer.parseInt(warningValue));
        newDrug.setAlertThreshold(Integer.parseInt(alertValue));
        // newDrug.setDateLastUpdate(UtilDate.dateAtNoon(new Date()).getTime());
        newDrug.setDateLastUpdate(new Date().getTime());
        newDrug.setDateEndOfStock();

        dbHelper.updateDrug(newDrug);
    }
}

package net.foucry.pilldroid;

import static net.foucry.pilldroid.R.id.detail_toolbar;

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

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.databases.PrescriptionDatabase;
import net.foucry.pilldroid.models.Prescription;

import java.util.Date;

/**
 * An activity representing a single Drug detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link DrugListActivity}.
 */
public class DrugDetailActivity extends AppCompatActivity {

    private static final String TAG = DrugDetailActivity.class.getName();

    Prescription aPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        aPrescription = (Prescription) bundle.get("prescription");
        Log.d(TAG, "aPrescription == " + aPrescription);

        setContentView(R.layout.drug_detail_activity);
        Toolbar toolbar = findViewById(detail_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Click on save icon");

                getDrugChanges();
                setResult(1);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(aPrescription.getName());
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
            arguments.putSerializable("prescription", aPrescription);
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

    private void getDrugChanges() {
        Log.d(TAG, "Time to save new values");

        PrescriptionDatabase prescriptions = PrescriptionDatabase.getInstanceDatabase(this);
        PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();

        Prescription newPrescription = prescriptionsDAO.getMedicByCIP13(aPrescription.getCip13());

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

        newPrescription.setStock(Float.parseFloat(stockValue));
        newPrescription.setTake(Float.parseFloat(takeValue));
        newPrescription.setWarning(Integer.parseInt(warningValue));
        newPrescription.setAlert(Integer.parseInt(alertValue));
        newPrescription.getDateEndOfStock();

        if (aPrescription.equals(newPrescription)) {
            Log.d(TAG, "medic and newPrescription are Equals");
        } else {
            Log.d(TAG, "medic and newPrescription are NOT Equals");
            newPrescription.setLast_update(new Date().getTime());
            prescriptionsDAO.update(newPrescription);
        }
    }
}

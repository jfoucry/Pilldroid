package net.foucry.pilldroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a single Medicament detail screen.
 * This fragment is either contained in a {@link MedicamentListActivity}
 * in two-pane mode (on tablets) or a {@link MedicamentDetailActivity}
 * on handsets.
 */
public class MedicamentDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "medicament";
    private static final String TAG = MedicamentListActivity.class.getName();

    /**
     * The dummy content this fragment is presenting.
     */
    private Medicament medicament;
    private DBHelper dbHelper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MedicamentDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            medicament = (Medicament) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(medicament.getNom());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.medicament_detail, container, false);
        View nameView;
        View adminModeView;
        View presentationView;
        View stockView;
        View priseView;
        View warningView;
        View alertView;

        // Show the dummy content as text in a TextView.
        if (medicament != null) {
            // Find each conponment of rootView
            nameView = detailView.findViewById(R.id.name_cell);
            TextView nameLabel = nameView.findViewById(R.id.label);
            TextView nameValeur = nameView.findViewById(R.id.valeur);
            nameLabel.setText("Nom");
            nameValeur.setText(medicament.getNom());

            presentationView = detailView.findViewById(R.id.presentation_cell);
            TextView presentationLabel = presentationView.findViewById(R.id.label);
            TextView presentationValeur = presentationView.findViewById(R.id.valeur);
            presentationLabel.setText("Presentation");
            presentationValeur.setText(medicament.getPresentation());

            adminModeView = detailView.findViewById(R.id.administration_cell);
            TextView adminModeLabel = adminModeView.findViewById(R.id.label);
            TextView adminModeValeur = adminModeView.findViewById(R.id.valeur);
            adminModeLabel.setText("Mode d'administration");
            adminModeValeur.setText(medicament.getMode_administration());

            stockView = detailView.findViewById(R.id.stock_cell);
            TextView stockLibelle = (stockView.findViewById(R.id.libelle));
            TextView stockValue = stockView.findViewById(R.id.valeur);
            stockLibelle.setText("Stock courant");
            stockValue.setText(Double.toString(medicament.getStock()));

            priseView = detailView.findViewById(R.id.prise_cell);
            TextView priseLibelle = priseView.findViewById(R.id.libelle);
            TextView priseValue = (priseView.findViewById(R.id.valeur));
            priseLibelle.setText("Prise");
            priseValue.setText(Double.toString(medicament.getPrise()));

            warningView = detailView.findViewById(R.id.warning_cell);
            TextView warningLibelle = warningView.findViewById(R.id.libelle);
            TextView warningValue = warningView.findViewById(R.id.valeur);
            warningLibelle.setText("Seuil d'alerte");
            warningValue.setText(Integer.toString(medicament.getWarnThreshold()));

            alertView = detailView.findViewById(R.id.alert_cell);
            TextView alertLibelle = alertView.findViewById(R.id.libelle);
            TextView alertValue = alertView.findViewById(R.id.valeur);
            alertLibelle.setText("Seuil critique");
            alertValue.setText(Integer.toString(medicament.getAlertThreshold()));
        }

        return detailView;
    }
    @Override
    public void onStop()
    {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        View currentView = getView();

        // View nameView;
        // View adminModeView;
        // View presentationView;
        View stockView;
        View priseView;
        View warningView;
        View alertView;

        assert currentView != null;
        stockView = currentView.findViewById(R.id.stock_cell);
        TextView stockTextView = stockView.findViewById(R.id.valeur);
        String stockValue = stockTextView.getText().toString();

        priseView = currentView.findViewById(R.id.prise_cell);
        TextView priseTextView = priseView.findViewById(R.id.valeur);
        String priseValue = priseTextView.getText().toString();

        alertView = currentView.findViewById(R.id.alert_cell);
        TextView alertTextView = alertView.findViewById(R.id.valeur);
        String alertValue = alertTextView.getText().toString();

        warningView = currentView.findViewById(R.id.warning_cell);
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

        dbHelper.updateDrug(medicament);
        super.onStop();
    }
}

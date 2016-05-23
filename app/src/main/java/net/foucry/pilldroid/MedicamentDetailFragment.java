package net.foucry.pilldroid;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.foucry.pilldroid.dummy.DummyContent;

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

    /**
     * The dummy content this fragment is presenting.
     */
    private Medicament medicament;

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
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(medicament.getNom());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            TextView nameLabel = (TextView) nameView.findViewById(R.id.label);
            TextView nameValeur = (TextView) nameView.findViewById(R.id.valeur);
            nameLabel.setText("Nom");
            nameValeur.setText(medicament.getNom());

            presentationView = detailView.findViewById(R.id.presentation_cell);
            TextView presentationLabel = (TextView) presentationView.findViewById(R.id.label);
            TextView presentationValeur = (TextView) presentationView.findViewById(R.id.valeur);
            presentationLabel.setText("Presentation");
            presentationValeur.setText(medicament.getPresentation());

            adminModeView = detailView.findViewById(R.id.administration_cell);
            TextView adminModeLabel = (TextView) adminModeView.findViewById(R.id.label);
            TextView adminModeValeur = (TextView) adminModeView.findViewById(R.id.valeur);
            adminModeLabel.setText("Mode d'administration");
            adminModeValeur.setText(medicament.getMode_administration());

            stockView = detailView.findViewById(R.id.stock_cell);
            TextView stockLibelle = (TextView) stockView.findViewById(R.id.libelle);
            TextView stockValue = (TextView) stockView.findViewById(R.id.valeur);
            stockLibelle.setText("Stock courant");
            stockValue.setText(Double.toString(medicament.getStock()));

            priseView = detailView.findViewById(R.id.prise_cell);
            TextView priseLibelle = (TextView) priseView.findViewById(R.id.libelle);
            TextView priseValue = (TextView) priseView.findViewById(R.id.valeur);
            priseLibelle.setText("Prise");
            priseValue.setText(Double.toString(medicament.getPrise()));

            warningView = detailView.findViewById(R.id.warning_cell);
            TextView warningLibelle = (TextView) warningView.findViewById(R.id.libelle);
            TextView warningValue = (TextView) warningView.findViewById(R.id.valeur);
            warningLibelle.setText("Seuil d'alerte");
            warningValue.setText(Integer.toString(medicament.getWarnThreshold()));

            alertView = detailView.findViewById(R.id.alert_cell);
            TextView alertLibelle = (TextView) alertView.findViewById(R.id.libelle);
            TextView alertValue = (TextView) alertView.findViewById(R.id.valeur);
            alertLibelle.setText("Seuil critique");
            alertValue.setText(Integer.toString(medicament.getAlertThreshold()));
        }

        return detailView;
    }
}

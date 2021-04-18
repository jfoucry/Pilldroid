package net.foucry.pilldroid;

import android.app.Activity;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Drug detail screen.
 * This fragment is either contained in a {@link DrugListActivity}
 * in two-pane mode (on tablets) or a {@link DrugDetailActivity}
 * on handsets.
 */
public class DrugDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "drug";

    /**
     * The dummy content this fragment is presenting.
     */
    private Drug drug;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrugDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            drug = (Drug) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            assert activity != null;
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(drug.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.drug_detail, container, false);
        View nameView;
        View adminModeView;
        View presentationView;
        View stockView;
        View takeView;
        View warningView;
        View alertView;

        // Show the dummy content as text in a TextView.
        if (drug != null) {
            // Find each conponment of rootView
            nameView = detailView.findViewById(R.id.name_cell);
            TextView nameLabel = nameView.findViewById(R.id.label);
            TextView nameValue = nameView.findViewById(R.id.value);
            nameLabel.setText(R.string.drug_name_label);
            nameValue.setText(drug.getName());

            presentationView = detailView.findViewById(R.id.presentation_cell);
            TextView presentationLabel = presentationView.findViewById(R.id.label);
            TextView presentationValue = presentationView.findViewById(R.id.value);
            presentationLabel.setText(R.string.drug_presention_labal);
            presentationValue.setText(drug.getPresentation());

            adminModeView = detailView.findViewById(R.id.administration_cell);
            TextView adminModeLabel = adminModeView.findViewById(R.id.label);
            TextView adminModeValue = adminModeView.findViewById(R.id.value);
            adminModeLabel.setText(R.string.drug_administationMode_label);
            adminModeValue.setText(drug.getAdministration_mode());

            stockView = detailView.findViewById(R.id.stock_cell);
            TextView stockLibelle = (stockView.findViewById(R.id.label));
            TextView stockValue = stockView.findViewById(R.id.value);
            stockLibelle.setText(R.string.drug_current_stock_label);
            stockValue.setText(Double.toString(drug.getStock()));
            stockValue.setHint(R.string.drug_current_stock_label);
            stockValue.setSelectAllOnFocus(true);

            takeView = detailView.findViewById(R.id.take_cell);
            TextView priseLabel = takeView.findViewById(R.id.label);
            TextView priseValue = (takeView.findViewById(R.id.value));
            priseLabel.setText(R.string.drug_take_label);
            priseValue.setText(Double.toString(drug.getTake()));
            priseValue.setHint(R.string.drug_take_label);
            priseValue.setSelectAllOnFocus(true);

            warningView = detailView.findViewById(R.id.warning_cell);
            TextView warningLibelle = warningView.findViewById(R.id.label);
            TextView warningValue = warningView.findViewById(R.id.value);
            warningLibelle.setText(R.string.drug_warningTherehold_label);
            warningValue.setText(Integer.toString(drug.getWarnThreshold()));
            warningValue.setHint(R.string.drug_warningTherehold_label);
            warningValue.setSelectAllOnFocus(true);

            alertView = detailView.findViewById(R.id.alert_cell);
            TextView alertLibelle = alertView.findViewById(R.id.label);
            TextView alertValue = alertView.findViewById(R.id.value);
            alertLibelle.setText(R.string.drug_alertTherehold_label);
            alertValue.setText(Integer.toString(drug.getAlertThreshold()));
            alertValue.setHint(R.string.drug_alertTherehold_label);
            alertValue.setSelectAllOnFocus(true);
        }

        return detailView;
    }
}

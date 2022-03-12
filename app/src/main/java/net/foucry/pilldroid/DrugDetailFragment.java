package net.foucry.pilldroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import net.foucry.pilldroid.models.Medic;

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
    public static final String ARG_ITEM_ID = "medic";

    /**
     * The dummy content this fragment is presenting.
     */
    private Medic medic;

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
            medic = (Medic) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            assert activity != null;
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(medic.getName());
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
        if (medic != null) {
            // Find each component of rootView
            nameView = detailView.findViewById(R.id.name_cell);
            TextView nameLabel = nameView.findViewById(R.id.label);
            TextView nameValue = nameView.findViewById(R.id.value);
            nameLabel.setText(R.string.drug_name_label);
            nameValue.setText(medic.getName());

            presentationView = detailView.findViewById(R.id.presentation_cell);
            TextView presentationLabel = presentationView.findViewById(R.id.label);
            TextView presentationValue = presentationView.findViewById(R.id.value);
            presentationLabel.setText(R.string.drug_presentation_label);
            presentationValue.setText(medic.getPresentation());

            adminModeView = detailView.findViewById(R.id.administration_cell);
            TextView adminModeLabel = adminModeView.findViewById(R.id.label);
            TextView adminModeValue = adminModeView.findViewById(R.id.value);
            adminModeLabel.setText(R.string.drug_administrationMode_label);
            adminModeValue.setText(medic.getAdministration_mode());

            stockView = detailView.findViewById(R.id.stock_cell);
            TextView stockLibelle = (stockView.findViewById(R.id.label));
            TextView stockValue = stockView.findViewById(R.id.value);
            stockLibelle.setText(R.string.drug_current_stock_label);
            stockValue.setText(Utils.fmt(medic.getStock()));
            stockValue.setHint(R.string.drug_current_stock_label);
            stockValue.setSelectAllOnFocus(true);

            takeView = detailView.findViewById(R.id.take_cell);
            TextView takeLabel = takeView.findViewById(R.id.label);
            TextView takeValue = (takeView.findViewById(R.id.value));
            takeLabel.setText(R.string.drug_take_label);
            //takeValue.setText(Double.toString(medic.getTake()));
            takeValue.setText(Utils.fmt(medic.getTake()));
            takeValue.setHint(R.string.drug_take_label);
            takeValue.setSelectAllOnFocus(true);

            warningView = detailView.findViewById(R.id.warning_cell);
            TextView warningLibelle = warningView.findViewById(R.id.label);
            TextView warningValue = warningView.findViewById(R.id.value);
            warningLibelle.setText(R.string.drug_warningThreshold_label);
            //warningValue.setText(Integer.toString(medic.getWarnThreshold()));
            warningValue.setText(Utils.fmt(medic.getWarning()));
            warningValue.setHint(R.string.drug_warningThreshold_label);
            warningValue.setSelectAllOnFocus(true);

            alertView = detailView.findViewById(R.id.alert_cell);
            TextView alertLibelle = alertView.findViewById(R.id.label);
            TextView alertValue = alertView.findViewById(R.id.value);
            alertLibelle.setText(R.string.drug_alertThreshold_label);
            //alertValue.setText(Integer.toString(medic.getAlertThreshold()));
            alertValue.setText(Utils.fmt(medic.getAlert()));
            alertValue.setHint(R.string.drug_alertThreshold_label);
            alertValue.setSelectAllOnFocus(true);
        }

        return detailView;
    }
}

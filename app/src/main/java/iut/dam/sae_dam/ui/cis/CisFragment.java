package iut.dam.sae_dam.ui.cis;
// CipFragment.java

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.databinding.FragmentCisBinding;
import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.medicaments.MedicamentAdapter;
import iut.dam.sae_dam.pharmacies.Pharmacie;
import iut.dam.sae_dam.pharmacies.PharmacieAdapter;
import iut.dam.sae_dam.ui.home.HomeFragment;
import iut.dam.sae_dam.saisies.Saisie;

public class CisFragment extends Fragment {
    private FragmentCisBinding binding;
    private CisViewModel cisViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cisViewModel = new ViewModelProvider(this, new CisViewModel.Factory(requireContext())).get(CisViewModel.class);

        // Code autocomplete //
        AutoCompleteTextView codeCompleteTextView = binding.cisFragmentCodeCisACTV;
        MedicamentAdapter codeAdapter = new MedicamentAdapter(requireContext(), DataHandling.getMedicaments());
        codeCompleteTextView.setAdapter(codeAdapter);

        codeCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicament selectedMedicine = (Medicament) parent.getItemAtPosition(position);
                TextView medicamentTV = binding.cisFragmentMedicamentNameTV;
                medicamentTV.setText(selectedMedicine.getDenomination());
            }
        });

        // Pharmacie autocomplete //
        AutoCompleteTextView pharmacieCompleteTextView = binding.cisFragmentPharmacieACTV;
        PharmacieAdapter pharmacieAdapter = new PharmacieAdapter(requireContext(), DataHandling.getPharmacies());
        pharmacieCompleteTextView.setAdapter(pharmacieAdapter);

        Button addButton = binding.cisFragmentSignalerBTN;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medCodeStr = codeCompleteTextView.getText().toString();
                Medicament med;
                int medicamentCode = 0;
                if (medCodeStr.isEmpty()) {
                    med = null;
                } else {
                    medicamentCode = Integer.parseInt(codeCompleteTextView.getText().toString());
                    med = DataHandling.getMedicamentByCode(medicamentCode);
                }
                String pharmacieName = pharmacieCompleteTextView.getText().toString();
                Pharmacie pharmacie = DataHandling.getPharmacieByName(pharmacieName);

                boolean error = false;
                if (med == null) {
                    Toast.makeText(requireContext(), "Médicament inconnu", Toast.LENGTH_SHORT).show();
                    codeCompleteTextView.setText("");
                    error = true;
                }

                if (pharmacie == null) {
                    pharmacieCompleteTextView.setText("");
                    Toast.makeText(requireContext(), "Pharmacie inconnue", Toast.LENGTH_SHORT).show();
                    error = true;
                }

                //TODO : errorhandling
                if (error) {
                    return;
                } else {
                    codeCompleteTextView.setText("");
                    pharmacieCompleteTextView.setText("");
                    binding.cisFragmentMedicamentNameTV.setText("");
                }

                Saisie saisie = new Saisie(med, pharmacie, new Date());
                DataHandling.addData(saisie);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

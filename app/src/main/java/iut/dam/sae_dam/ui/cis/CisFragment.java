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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cisViewModel = new ViewModelProvider(this, new CisViewModel.Factory(requireContext())).get(CisViewModel.class);

        cisViewModel.setMedicineList(DataHandling.getMedicineList());
        cisViewModel.setPharmacieList(DataHandling.getPharmacieList());

        // Code autocomplete //
        AutoCompleteTextView codeCompleteTextView = binding.autoCompleteTextView;
        MedicamentAdapter codeAdapter = new MedicamentAdapter(requireContext(), cisViewModel.getMedicineList());
        codeCompleteTextView.setAdapter(codeAdapter);

        codeCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicament selectedMedicine = (Medicament) parent.getItemAtPosition(position);
                TextView medicamentTV = binding.medicamentTV;
                medicamentTV.setText(selectedMedicine.getDenomination());
            }
        });

        // Pharmacie autocomplete //
        AutoCompleteTextView pharmacieCompleteTextView = binding.pharmacieACTV;
        PharmacieAdapter pharmacieAdapter = new PharmacieAdapter(requireContext(), cisViewModel.getPharmacieList());
        pharmacieCompleteTextView.setAdapter(pharmacieAdapter);

        pharmacieCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pharmacie selectedMedicine = (Pharmacie) parent.getItemAtPosition(position);
            }
        });

        Button addButton = binding.signalerB;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = codeCompleteTextView.getText().toString();
                String medicament = binding.medicamentTV.getText().toString();
                String pharmacie = pharmacieCompleteTextView.getText().toString();

                boolean error = false;
                if (code.isEmpty() || (!code.isEmpty() && !codeAdapter.contains(Integer.parseInt(code), medicament))) {
                    Toast.makeText(requireContext(), "Médicament inconnu", Toast.LENGTH_SHORT).show();
                    codeCompleteTextView.setText("");
                    error = true;
                }

                if (!pharmacieAdapter.contains(pharmacie)) {
                    Toast.makeText(requireContext(), "Pharmacie inconnue", Toast.LENGTH_SHORT).show();
                    pharmacieCompleteTextView.setText("");
                    error = true;
                }
                if (error)
                    return;
                else {
                    codeCompleteTextView.setText("");
                    pharmacieCompleteTextView.setText("");
                    binding.medicamentTV.setText("");
                }

                Saisie saisie = new Saisie(code, medicament, pharmacie);
                boolean test = HomeFragment.ajoutSaisie(saisie);
                if (test)
                    Toast.makeText(requireContext(), "Signalement ajouté", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(requireContext(), "Erreur dans le signalement", Toast.LENGTH_SHORT).show();
                }
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

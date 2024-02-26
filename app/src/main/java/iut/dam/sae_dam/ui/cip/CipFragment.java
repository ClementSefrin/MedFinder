package iut.dam.sae_dam.ui.cip;
// CipFragment.java

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import iut.dam.sae_dam.databinding.FragmentCipBinding;
import iut.dam.sae_dam.ui.cip.medicaments.Medicament;
import iut.dam.sae_dam.ui.cip.medicaments.MedicamentAdapter;
import iut.dam.sae_dam.ui.cip.pharmacies.Pharmacie;
import iut.dam.sae_dam.ui.cip.pharmacies.PharmacieAdapter;

public class CipFragment extends Fragment {

    private FragmentCipBinding binding;
    private CipViewModel cipViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCipBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cipViewModel = new ViewModelProvider(this, new CipViewModel.Factory(requireContext())).get(CipViewModel.class);

        // Code autocomplete //
        AutoCompleteTextView codeCompleteTextView = binding.autoCompleteTextView;
        MedicamentAdapter codeAdapter = new MedicamentAdapter(requireContext(), cipViewModel.getMedicineList());
        codeCompleteTextView.setAdapter(codeAdapter);

        Log.d("CipFragment", "Code adapter initialized with " + cipViewModel.getMedicineList().size() + " items.");

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
        PharmacieAdapter pharmacieAdapter = new PharmacieAdapter(requireContext(), cipViewModel.getPharmacieList());
        pharmacieCompleteTextView.setAdapter(pharmacieAdapter);

        Log.d("CipFragment", "Pharmacie adapter initialized with " + cipViewModel.getPharmacieList().size() + " items.");

        pharmacieCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pharmacie selectedMedicine = (Pharmacie) parent.getItemAtPosition(position);
                Toast.makeText(requireContext(), selectedMedicine.getName(), Toast.LENGTH_SHORT).show();
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

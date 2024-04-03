package iut.dam.sae_dam.ui.cis;
// CipFragment.java

import android.content.Intent;
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

import java.sql.Date;
import java.util.HashMap;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.data.villes.Ville;
import iut.dam.sae_dam.data.villes.VilleAdapter;
import iut.dam.sae_dam.databinding.FragmentCisBinding;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;
import iut.dam.sae_dam.data.medicaments.Medicament;
import iut.dam.sae_dam.data.medicaments.MedicamentAdapter;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;
import iut.dam.sae_dam.data.pharmacies.PharmacieAdapter;
import iut.dam.sae_dam.data.saisies.Saisie;
import iut.dam.sae_dam.scanDataMatrix.Scanner;

public class CisFragment extends Fragment {
    private FragmentCisBinding binding;
    private CisViewModel cisViewModel;

    AutoCompleteTextView codeCompleteTextView, pharmacieCompleteTextView, cityCompleteTextView;
    private TextView errorCisCodeTV, errorPharmacieTV, errorCityTV, medicamentNameTV;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    private Medicament med;
    private Pharmacie pharmacie;
    private Ville city, defaultCity;
    Button scanner;
    private String dataMatrix;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Bundle bundle = getArguments();

        scanner = binding.scannerB;
        cisViewModel = new ViewModelProvider(this, new CisViewModel.Factory(requireContext())).get(CisViewModel.class);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), Scanner.class);
                startActivity(intent);
            }
        });
        errors = new HashMap<>();
        errorMessagesViews = new HashMap<>();
        getViews();
        if (bundle != null) {
            dataMatrix = bundle.getString("dataMatrix");
            if (dataMatrix != null) {
                try {
                    int dataMatrixInt = Integer.parseInt(dataMatrix);
                    if (DataHandling.estUnMedicament(dataMatrixInt)) {
                        med = DataHandling.getMedicamentByCode(dataMatrixInt);
                        medicamentNameTV.setText(DataHandling.getMedicamentByCode(dataMatrixInt).getDenomination());
                        codeCompleteTextView.setText(dataMatrix);
                    } else {
                        Toast.makeText(requireContext(), "Le code DataMatrix ne correspond pas à un médicament", Toast.LENGTH_SHORT).show();
                        errorMessagesViews.get(codeCompleteTextView).setVisibility(View.VISIBLE);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Le code DataMatrix n'est pas valide", Toast.LENGTH_SHORT).show();
                    errorMessagesViews.get(codeCompleteTextView).setVisibility(View.VISIBLE);
                }
            }
        }

            // Code autocomplete //
            MedicamentAdapter codeAdapter = new MedicamentAdapter(requireContext(), DataHandling.getMedicaments());
            codeCompleteTextView.setAdapter(codeAdapter);

        codeCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicament selectedMedicine = (Medicament) parent.getItemAtPosition(position);
                medicamentNameTV.setText(selectedMedicine.getDenomination());
            }
        });

        // Pharmacie autocomplete //
        PharmacieAdapter pharmacieAdapter = new PharmacieAdapter(requireContext(), DataHandling.getPharmacies());
        pharmacieCompleteTextView.setAdapter(pharmacieAdapter);

        // City autocomplete //
        VilleAdapter cityAdapter = new VilleAdapter(requireContext(), DataHandling.getVilles());
        cityCompleteTextView.setAdapter(cityAdapter);


        Button addButton = binding.cisFragmentSignalerBTN;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errors = getErrors();
                if (!errors.isEmpty()) {
                    ErrorManager.updateBorder(getContext(), errors, errorMessagesViews);
                    medicamentNameTV.setText("");
                    return;
                } else {
                    ErrorManager.updateBorder(getContext(), errors, errorMessagesViews);
                    codeCompleteTextView.setText("");
                    pharmacieCompleteTextView.setText("");
                    medicamentNameTV.setText("");
                    cityCompleteTextView.setText("");
                }

                int userId = getActivity().getIntent().getIntExtra("userId", 0);
                if (city == null) {
                    city = defaultCity;
                }
                Saisie saisie = new Saisie(userId, med, pharmacie, new Date(System.currentTimeMillis()), city, city.getZipCode());
                DataHandling.addData(saisie);
                addButton.requestFocus();
            }
        });

        return root;
    }


    private void getViews() {
        codeCompleteTextView = binding.cisFragmentCodeCisACTV;
        pharmacieCompleteTextView = binding.cisFragmentPharmacieACTV;
        cityCompleteTextView = binding.cisFragmentCityACTV;

        errorCisCodeTV = binding.cisFragmentErrorCisCodeTV;
        errorPharmacieTV = binding.cisFragmentErrorPharmacieTV;
        errorCityTV = binding.cisFragmentErrorCityTV;

        medicamentNameTV = binding.cisFragmentMedicamentNameTV;

        errorMessagesViews.put(codeCompleteTextView, errorCisCodeTV);
        errorMessagesViews.put(pharmacieCompleteTextView, errorPharmacieTV);
        errorMessagesViews.put(cityCompleteTextView, errorCityTV);

        for (View view : errorMessagesViews.keySet()) {
            errorMessagesViews.get(view).setVisibility(View.GONE);
        }
    }

    private HashMap<View, Errors> getErrors() {
        HashMap<View, Errors> errors = new HashMap<>();

        String medCodeStr = codeCompleteTextView.getText().toString();
        if (medCodeStr.isEmpty()) {
            errors.put(codeCompleteTextView, Errors.EMPTY_FIELD);
        } else {
            int medicamentCode = Integer.parseInt(medCodeStr);
            med = DataHandling.getMedicamentByCode(medicamentCode);
            if (med == null) {
                errors.put(codeCompleteTextView, Errors.UNKNOWN_MEDICINE);
            }
        }

        String pharmacieName = pharmacieCompleteTextView.getText().toString();
        if (pharmacieName.isEmpty()) {
            errors.put(pharmacieCompleteTextView, Errors.EMPTY_FIELD);
        } else {
            pharmacie = DataHandling.getPharmacieByName(pharmacieName);
            if (pharmacie == null) {
                errors.put(pharmacieCompleteTextView, Errors.UNKNOWN_PHARMACY);
            }
        }

        String cityName = cityCompleteTextView.getText().toString();
        if (cityName.isEmpty()) {
            if (defaultCity == null)
                errors.put(cityCompleteTextView, Errors.EMPTY_FIELD);
        } else {
            int insee = Integer.parseInt(cityName.split(" - ")[1].trim());
            city = DataHandling.getCitybyInsee(insee);
            if (city == null) {
                errors.put(cityCompleteTextView, Errors.UNKNOWN_CITY);
            }
        }

        return errors;
    }

    @Override
    public void onResume() {
        super.onResume();

        defaultCity = DataHandling.getCitybyInsee(getActivity().getIntent().getIntExtra("city", 0));
        if (defaultCity != null)
            cityCompleteTextView.setHint(getString(R.string.hintCityDefault) + defaultCity.toString() + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

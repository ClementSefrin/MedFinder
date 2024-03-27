package iut.dam.sae_dam.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.pharmacies.Pharmacie;
import iut.dam.sae_dam.saisies.Saisie;
import iut.dam.sae_dam.saisies.SaisieAdapter;
import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        List<Saisie> saisies = new LinkedList<>();
        Medicament med = new Medicament(1, "Doliprane", "Comprimé", "Libre", "Autorisation de mise sur le marché", "Commercialisé", "Sanofi", false);
        Pharmacie pharma = new Pharmacie(1, "Pharmacie de la gare");
        saisies.add(new Saisie(med, pharma));

        ListView histoSaisie = binding.fragmentHomeHistoSaisieLV;
        SaisieAdapter adapter = new SaisieAdapter(getActivity(), R.layout.item_saisie, DataHandling.getUserSaisies());
        histoSaisie.setAdapter(adapter);

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

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

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.saisies.Saisie;
import iut.dam.sae_dam.saisies.SaisieAdapter;
import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private ListView histoSaisie;
    private FragmentHomeBinding binding;
    private static LinkedList<Saisie> saisies = new LinkedList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        saisies = DataHandling.getUserSaisies();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        histoSaisie = root.findViewById(R.id.fragmentHome_HistoSaisieLV);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Saisie saisie : saisies) {
            Log.e("AAAA Saisie", saisie.getMedicament().getDenomination() + " " + saisie.getPharmacie().getName() + " " + saisie.getDateSaisie());
        }
        SaisieAdapter adapter = new SaisieAdapter(getActivity(), R.layout.item_saisie, saisies);
        histoSaisie.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void addData(Saisie saisie) {
        saisies.add(saisie);
    }

}

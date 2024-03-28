package iut.dam.sae_dam.ui.home;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.saisies.SaisieAdapter;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);



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

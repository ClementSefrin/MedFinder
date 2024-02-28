package iut.dam.sae_dam.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.saisies.Saisie;
import iut.dam.sae_dam.saisies.SaisieAdapter;
import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private ListView histoSaisie;
    private static File saveFile;
    private static final Gson gson = new Gson();
    private static FileOutputStream fos;
    private static OutputStreamWriter osw;
    private static ArrayList<Saisie> items = new ArrayList<>();
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initSaveFile(requireContext());
        histoSaisie = binding.histoSaisieLV;

        SaisieAdapter adapter = new SaisieAdapter((Activity) requireContext(), R.layout.item_saisie, items);
        histoSaisie.setAdapter(adapter);
        return root;
    }

    private static void initSaveFile(Context context) {

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), MODE_PRIVATE);
        saveFile = new File(directory, "saveFile");
        items = DataHandling.loadData(saveFile, gson);
    }

    public static boolean ajoutSaisie(Saisie saisie) {
        if (DataHandling.addData(saveFile, gson, saisie)) {
            items.add(saisie);
            return true;
        } else {
            return false;
        }
    }

    public static boolean supprimerHisto() {
        if (DataHandling.deleteData(saveFile)) {
            items.clear();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

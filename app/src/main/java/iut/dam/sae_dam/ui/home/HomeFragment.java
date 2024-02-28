package iut.dam.sae_dam.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.ui.home.saisies.*;
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

        Button btn = binding.btn;


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Size " + items.size(), Toast.LENGTH_SHORT).show();
                StringBuilder sb = new StringBuilder();
                for (Saisie saisie : items) {
                    sb.append(saisie.getMedicament()).append("\n");
                }
                TextView text = binding.text;
                text.setText(sb.toString());
            }
        });
        return root;
    }

    private static ArrayList<Saisie> readSavedData() {
        ArrayList<Saisie> savedItems = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(saveFile);
            InputStreamReader isr = new InputStreamReader(fis);
            JsonReader reader = new JsonReader(isr);
            reader.setLenient(true); // Lenient mode to accept malformed JSON

            while (reader.hasNext()) {
                Saisie readObject = gson.fromJson(reader, Saisie.class);
                savedItems.add(readObject);
            }

            reader.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedItems;
    }


    private static void initSaveFile(Context context) {

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), MODE_PRIVATE);
        saveFile = new File(directory, "saveFile");
        items = readSavedData();
    }

    public static boolean ajoutSaisie(Saisie saisie) {
        items.add(saisie);
        try {
            fos = new FileOutputStream(saveFile, true);
            osw = new OutputStreamWriter(fos);

            String json = gson.toJson(saisie);
            osw.write(json + "\n");

            osw.close();
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e("FileWriteError", "Error writing to file: " + e.getMessage());
            return false;
        }
    }


    public static void supprimerHisto() {
        items.clear();
        try {
            fos = new FileOutputStream(saveFile);
            osw = new OutputStreamWriter(fos);

            osw.write("");

            osw.close();
            fos.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

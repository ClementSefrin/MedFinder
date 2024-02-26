package iut.dam.sae_dam.ui.cip;
// CipViewModel.java

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import iut.dam.sae_dam.ui.cip.medicaments.Medicament;
import iut.dam.sae_dam.ui.cip.pharmacies.Pharmacie;

public class CipViewModel extends ViewModel {

    private List<Medicament> medicineList = new ArrayList<>();
    private List<Pharmacie> pharmacieList = new ArrayList<>();

    public CipViewModel(Context context) {
        loadDataFromJson(context);
    }

    private void loadDataFromJson(Context context) {
        try {
            // Medicaments //
            InputStream inputStream = context.getAssets().open("medicaments.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Medicament medicine = new Medicament();
                medicine.setCIS(jsonObject.getInt("CIS"));
                medicine.setDenomination(jsonObject.getString("denomination"));

                medicineList.add(medicine);
            }

            // Pharmacies //
            inputStream = context.getAssets().open("pharmacies.json");
            size = inputStream.available();
            buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");

            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Pharmacie pharmacie = new Pharmacie();
                pharmacie.setId(jsonObject.getString("FID"));
                pharmacie.setName(jsonObject.getString("name"));

                pharmacieList.add(pharmacie);
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public List<Medicament> getMedicineList() {
        return medicineList;
    }

    public List<Pharmacie> getPharmacieList() {
        return pharmacieList;
    }

    // Factory class to create CipViewModel with parameters
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new CipViewModel(context);
        }
    }
}

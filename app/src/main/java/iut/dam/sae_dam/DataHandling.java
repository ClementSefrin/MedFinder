package iut.dam.sae_dam;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.pharmacies.Pharmacie;
import iut.dam.sae_dam.saisies.Saisie;

public class DataHandling implements Runnable {
    private Context context;
    private static List<Medicament> medicineList = new ArrayList<>();
    private static List<Pharmacie> pharmacieList = new ArrayList<>();
    private static FileOutputStream fos;
    private static OutputStreamWriter osw;

    public DataHandling(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
    }

    public void init() {
        try {
            Log.e("LOADING", "LOADING STARTED");
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

                Medicament medicine = new Medicament(jsonObject.getInt("CIS"), jsonObject.getString("denomination"));

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

                Pharmacie pharmacie = new Pharmacie(jsonObject.getString("name"));

                pharmacieList.add(pharmacie);
            }


            Log.e("LOADING", "LOADING ENDED");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Saisie> loadData(File saveFile, Gson gson) {
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

    public static boolean addData(File saveFile, Gson gson, Saisie saisie) {
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

    public static boolean deleteData(File saveFile) {
        try {
            fos = new FileOutputStream(saveFile);
            osw = new OutputStreamWriter(fos);

            osw.write("");

            osw.close();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static List<Medicament> getMedicineList() {
        return medicineList;
    }

    public static List<Pharmacie> getPharmacieList() {
        return pharmacieList;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}

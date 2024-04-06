package iut.dam.sae_dam.data.villes;

import android.util.Log;

import androidx.annotation.NonNull;

public class Ville {
    private int insee, departement, zipCode;
    private String nom, region;

    public Ville(int insee, String nom, int departement, String region, int zipCode) {
        this.insee = insee;
        this.nom = toProperCase(nom);
        this.departement = departement;
        this.region = region;
        this.zipCode = zipCode;
    }

    public int getInsee() {
        return insee;
    }

    public String getName() {
        return nom;
    }

    @NonNull
    @Override
    public String toString() {
        return nom + " - " + insee;
    }

    public String getRegion() {
        return region;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String toProperCase(String str) {
        String[] parts = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }

    public int getDepartement() {
        return departement;
    }
}

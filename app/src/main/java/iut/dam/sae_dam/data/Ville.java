package iut.dam.sae_dam.data;

public class Ville {
    private int insee, departement;
    private String nom, region;

    public Ville(int insee, String nom, int departement, String region) {
        this.insee = insee;
        this.nom = nom;
        this.departement = departement;
        this.region = region;
    }

    public String getName() {
        return nom;
    }
}

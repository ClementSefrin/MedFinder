package iut.dam.sae_dam.data.saisies;

import java.sql.Date;

import iut.dam.sae_dam.data.medicaments.Medicament;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;
import iut.dam.sae_dam.data.villes.Ville;

public class Saisie {

    private Medicament medicament;
    private Pharmacie pharmacie;
    private int userId, departement;
    private Ville ville;
    private Date dateSaisie;

    public Saisie(int userId, Medicament medicament, Pharmacie pharmacie, Date dateSaisie, Ville ville, int departement) {
        this.medicament = medicament;
        this.userId = userId;
        this.pharmacie = pharmacie;
        this.dateSaisie = dateSaisie;
        this.ville = ville;
        this.departement = departement;
    }


    public int getUserId() {
        return userId;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public Pharmacie getPharmacie() {
        return pharmacie;
    }

    public Date getDateSaisie() {
        return dateSaisie;
    }

    public Ville getCity() {
        return ville;
    }

    public Date getDate() {
        return dateSaisie;
    }
}

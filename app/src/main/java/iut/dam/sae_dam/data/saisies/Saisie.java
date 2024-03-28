package iut.dam.sae_dam.data.saisies;

import java.util.Date;

import iut.dam.sae_dam.data.medicaments.Medicament;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;

public class Saisie {

    private Medicament medicament;
    private Pharmacie pharmacie;
    private int userId, departement;
    private String ville;

    public int getUserId() {
        return userId;
    }

    private Date dateSaisie;

    public Saisie(int userId, Medicament medicament, Pharmacie pharmacie, Date dateSaisie, String ville, int departement) {
        this.medicament = medicament;
        this.userId = userId;
        this.pharmacie = pharmacie;
        this.dateSaisie = dateSaisie;
        this.ville = ville;
        this.departement = departement;
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
}

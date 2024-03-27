package iut.dam.sae_dam.saisies;

import java.util.Date;

import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.pharmacies.Pharmacie;

public class Saisie {

    private Medicament medicament;
    private Pharmacie pharmacie;
    private int userId;
    private String ville, departement;
    private Date dateSaisie;

    public Saisie(Medicament medicament, int userId ,Pharmacie pharmacie, Date dateSaisie, String ville, String departement) {
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

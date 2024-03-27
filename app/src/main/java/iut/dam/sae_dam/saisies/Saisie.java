package iut.dam.sae_dam.saisies;

import java.util.Date;

import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.pharmacies.Pharmacie;

public class Saisie {

    private Medicament medicament;
    private Pharmacie pharmacie;
    private Date dateSaisie;

    public Saisie(Medicament medicament, Pharmacie pharmacie) {
        this.medicament = medicament;
        this.pharmacie = pharmacie;
        dateSaisie = new Date();
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

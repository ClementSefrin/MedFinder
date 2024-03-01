package iut.dam.sae_dam.saisies;

import java.util.Date;

public class Saisie {

    private String code, medicament, pharmacie;

    private Date dateSaisie;

    public Saisie(String code, String medicament, String pharmacie) {
        this.code = code;
        this.medicament = medicament;
        this.pharmacie = pharmacie;
        dateSaisie = new Date();
    }


    public String getCode() {
        return code;
    }

    public String getMedicament() {
        return medicament;
    }

    public String getPharmacie() {
        return pharmacie;
    }

    public Date getDateSaisie() {
        return dateSaisie;
    }
}

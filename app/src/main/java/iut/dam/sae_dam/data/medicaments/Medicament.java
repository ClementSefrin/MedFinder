package iut.dam.sae_dam.data.medicaments;

import java.util.Objects;

public class Medicament {
    private int cisCode;
    private String denomination, formeAdministration, statusAdministration, procedureAutorisation, etatCommercialisation, titulaire;
    boolean surveillance;

    public Medicament(int cisCode, String denomination, String formeAdministration,
                      String statusAdministration, String procedureAutorisation,
                      String etatCommercialisation, String titulaire, boolean surveillance) {
        this.cisCode = cisCode;
        this.denomination = denomination;
        this.formeAdministration = formeAdministration;
        this.statusAdministration = statusAdministration;
        this.procedureAutorisation = procedureAutorisation;
        this.etatCommercialisation = etatCommercialisation;
        this.titulaire = titulaire;
        this.surveillance = surveillance;
    }

    public int getCisCode() {
        return cisCode;
    }

    public String getFormeAdministration() {
        return formeAdministration;
    }

    public String getStatusAdministration() {
        return statusAdministration;
    }

    public String getProcedureAutorisation() {
        return procedureAutorisation;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public boolean isSurveillance() {
        return surveillance;
    }

    public String getDenomination() {
        return denomination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicament that = (Medicament) o;
        return getCisCode() == that.getCisCode() && surveillance == that.surveillance
                && Objects.equals(getDenomination(), that.getDenomination())
                && Objects.equals(formeAdministration, that.formeAdministration)
                && Objects.equals(statusAdministration, that.statusAdministration)
                && Objects.equals(procedureAutorisation, that.procedureAutorisation)
                && Objects.equals(etatCommercialisation, that.etatCommercialisation)
                && Objects.equals(titulaire, that.titulaire);
    }

    @Override
    public String toString() {
        return String.valueOf(cisCode);
    }
}

// Medicament.java
package iut.dam.sae_dam.ui.cis.medicaments;

import java.util.Objects;

public class Medicament {
    private int CIS;
    private String denomination;

    public Medicament(int CIS, String denomination) {
        this.CIS = CIS;
        this.denomination = denomination;
    }

    public int getCIS() {
        return CIS;
    }

    public void setCIS(int CIS) {
        this.CIS = CIS;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicament that = (Medicament) o;
        return CIS == that.CIS && Objects.equals(denomination, that.denomination);
    }

    @Override
    public String toString() {
        return String.valueOf(CIS); // or return denomination; depending on your preference
    }
}

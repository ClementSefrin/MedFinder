// Medicament.java
package iut.dam.sae_dam.ui.cip.medicaments;

public class Medicament {
    private int CIS;
    private String denomination;

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
    public String toString() {
        return String.valueOf(CIS); // or return denomination; depending on your preference
    }
}

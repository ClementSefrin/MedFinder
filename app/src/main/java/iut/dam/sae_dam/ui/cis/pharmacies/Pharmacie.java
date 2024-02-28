package iut.dam.sae_dam.ui.cis.pharmacies;

import java.util.Objects;

public class Pharmacie {
    private String name;

    public Pharmacie(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pharmacie pharmacie = (Pharmacie) o;
        return Objects.equals(name, pharmacie.name);
    }
}

package iut.dam.sae_dam.data.pharmacies;

import java.util.Objects;

public class Pharmacie {
    int id;
    private String name;

    public Pharmacie(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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
        return Objects.equals(name, pharmacie.name) && Objects.equals(id, pharmacie.id);
    }
}

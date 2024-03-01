package iut.dam.sae_dam.ui.cis;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.medicaments.Medicament;
import iut.dam.sae_dam.pharmacies.Pharmacie;

public class CisViewModel extends ViewModel {
    private List<Medicament> medicineList = new ArrayList<>();
    private List<Pharmacie> pharmacieList = new ArrayList<>();

    public CisViewModel(Context context) {
    }


    public List<Medicament> getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(List<Medicament> medicineList) {
        this.medicineList = medicineList;
    }

    public List<Pharmacie> getPharmacieList() {
        return pharmacieList;
    }

    public void setPharmacieList(List<Pharmacie> pharmacieList) {
        this.pharmacieList = pharmacieList;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new CisViewModel(context);
        }
    }
}

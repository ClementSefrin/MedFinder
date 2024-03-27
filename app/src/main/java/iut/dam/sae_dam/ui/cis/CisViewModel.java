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
    public CisViewModel(Context context) {
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

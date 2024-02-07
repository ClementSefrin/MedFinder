package iut.dam.sae_dam.ui.cip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CipViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CipViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("CIP fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
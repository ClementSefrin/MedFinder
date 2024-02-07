package iut.dam.sae_dam.ui.scan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScanViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScanViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Scan fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
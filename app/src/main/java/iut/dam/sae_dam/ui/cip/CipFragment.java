package iut.dam.sae_dam.ui.cip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import iut.dam.sae_dam.databinding.FragmentCipBinding;

public class CipFragment extends Fragment {
    private FragmentCipBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CipViewModel cipViewModel = new ViewModelProvider(this).get(CipViewModel.class);

        binding = FragmentCipBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
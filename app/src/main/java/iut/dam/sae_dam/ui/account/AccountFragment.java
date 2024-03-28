package iut.dam.sae_dam.ui.account;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.accountFragmentSupprimerHistoTV;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new DialogDeleteHisto().show(getChildFragmentManager(), "SUPPRIMER_HISTO");
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_signin);

                Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DataHandling.supprimerHisto();
                        Toast.makeText(requireContext(), "Historique supprimé.", Toast.LENGTH_SHORT).show();

                    }
                });

                Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

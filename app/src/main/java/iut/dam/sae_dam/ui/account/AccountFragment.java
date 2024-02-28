package iut.dam.sae_dam.ui.account;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.databinding.FragmentAccountBinding;
import iut.dam.sae_dam.ui.home.HomeFragment;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.supprimerHistoTV;
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
                        HomeFragment.supprimerHisto();
                        Toast.makeText(getContext(), "Historique supprimé", Toast.LENGTH_SHORT).show();
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

    /*
    public static class DialogDeleteHisto extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Ce changement est irreversible")
                    .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HomeFragment.supprimerHisto();
                            Toast.makeText(getContext(), "Historique supprimé", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setTitle("Supprimer l'historique");
            // Create the AlertDialog object and return it.
            return builder.create();
        }

    }
    */
}

package iut.dam.sae_dam.ui.account;

import android.app.Dialog;
import android.content.Intent;
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

import iut.dam.sae_dam.activities.Login;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {
    private static final int REQUEST_CODE_CHANGE_PASSWORD = 1;

    public static int getRequestCodeChangePassword() {
        return REQUEST_CODE_CHANGE_PASSWORD;
    }

    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView deleteHistoryTV = binding.accountFragmentSupprimerHistoTV;
        TextView changePasswordTV = binding.accountFragmentChangePasswordTV;
        TextView statisticsTV = binding.accountFragmentStatisticsTV;
        TextView logOutTV = binding.accountFragmentLogOutTV;
        TextView deleteAccountTV = binding.accountFragmentDeleteAccountTV;

        //Suppression de l'historique
        deleteHistoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHisto();
            }
        });

        changePasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        if (!getActivity().getIntent().getBooleanExtra("admin", false)) {
            binding.accountFragmentStatisticsRL.setVisibility(View.GONE);
        }

        logOutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        deleteAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_delete_account);

                Button positiveButton = (Button) dialog.findViewById(R.id.dialogDeleteAccount_positiveButton);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DataHandling.deleteAccount();
                        getActivity().finishAffinity();
                        Intent intent = new Intent(getContext(), Login.class);
                        startActivity(intent);
                    }
                });

                Button negativeButton = (Button) dialog.findViewById(R.id.dialogDeleteAccount_negativeButton);
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

    private void deleteHisto() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_delete_history);

        Button positiveButton = (Button) dialog.findViewById(R.id.dialogDeleteHistory_positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DataHandling.supprimerHisto();
                Toast.makeText(requireContext(), "Historique supprimé.", Toast.LENGTH_SHORT).show();
            }
        });

        Button negativeButton = (Button) dialog.findViewById(R.id.dialogDeleteHistory_negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void changePassword() {
        Dialog dialog = new ChangePasswordDialog(getContext(), getActivity());
        dialog.show();
    }

    private void logout() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_log_out);

        Button positiveButton = (Button) dialog.findViewById(R.id.dialogLogOut_positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finishAffinity();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        Button negativeButton = (Button) dialog.findViewById(R.id.dialogLogOut_negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

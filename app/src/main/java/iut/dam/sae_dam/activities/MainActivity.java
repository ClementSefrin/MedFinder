package iut.dam.sae_dam.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.mainActivity_navView);

        NavController navController = Navigation.findNavController(this, R.id.mainActivity_hostFragment);
        NavigationUI.setupWithNavController(binding.mainActivityNavView, navController);


        final TextView headerTextView = findViewById(R.id.mainActivity_headerTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, Bundle arguments) {
                headerTextView.setText(destination.getLabel());
            }
        });
    }
}
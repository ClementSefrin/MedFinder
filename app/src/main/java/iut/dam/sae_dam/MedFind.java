package iut.dam.sae_dam;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.Duration;

import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.data.DatabaseConnection;

public class MedFind extends Application {
    private static final int MAX_CHAR_LIMIT = 30;

    public static int getMaxCharLimit() {
        return MAX_CHAR_LIMIT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DataHandling.loadData();
    }
}

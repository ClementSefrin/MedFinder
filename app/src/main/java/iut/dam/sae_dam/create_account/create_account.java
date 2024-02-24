package iut.dam.sae_dam.create_account;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;
import android.os.Bundle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DatabaseConnection;
public class create_account extends Activity {
    Connection connect;
    String ConnectionResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        new DatabaseTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DatabaseTask extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... voids) {
            try {
                return DatabaseConnection.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("StaticFieldLeak")
        protected void onPostExecute(Connection connection) {
            super.onPostExecute(connection);
            {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    if (connection != null) {
                        // La connexion à la base de données a été établie avec succès
                        Toast.makeText(create_account.this, "Connexion à la base de données réussie!", Toast.LENGTH_SHORT).show();
                        connect = connection;
                        String query = "SELECT * FROM user";
                        try {
                            Statement statement = connect.createStatement();
                            ResultSet resultSet = statement.executeQuery(query);
                            while (resultSet.next()) {
                                String id = resultSet.getString("id");
                                String password = resultSet.getString("password");
                                System.out.println("id: " + id + " password: " + password);
                                Toast.makeText(create_account.this, "id: " + id + " password: " + password, Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        // N'oubliez pas de fermer la connexion lorsque vous avez fini avec elle
                        DatabaseConnection.closeConnection(connection);
                    } else {
                        // La connexion à la base de données a échoué
                        Toast.makeText(create_account.this, "Échec de la connexion à la base de données!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}

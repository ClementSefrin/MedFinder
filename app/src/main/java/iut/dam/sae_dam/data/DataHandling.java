package iut.dam.sae_dam.data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import iut.dam.sae_dam.data.medicaments.Medicament;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;
import iut.dam.sae_dam.data.saisies.Saisie;

public class DataHandling {
    private static boolean dataLoaded = false;
    private static List<Medicament> medicaments = new LinkedList<>();
    private static List<Pharmacie> pharmacies = new LinkedList<>();
    private static LinkedList<Ville> villes = new LinkedList<>();
    private static LinkedList<Saisie> allSaisies = new LinkedList<>();
    private static LinkedList<Saisie> userSaisies = new LinkedList<>();
    private static int userId;
    private static boolean admin;
    private static String password, city;
    private static Context context;

    public static void loadData() {
        new LoadData().execute();
    }

    public static void getIntentData(int userId, String password, boolean admin, String city) {
        DataHandling.userId = userId;
        DataHandling.password = password;
        DataHandling.admin = admin;
        DataHandling.city = city;
        loadUserSaisies();
    }

    public static void addData(Saisie saisie) {
        allSaisies.add(saisie);
        userSaisies.add(saisie);
    }

    public static void supprimerHisto() {
        for (Saisie s : userSaisies) {
            allSaisies.remove(s);
        }
        userSaisies.clear();
    }

    public static boolean isDataLoaded() {
        return dataLoaded;
    }

    private static class LoadData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "SELECT * FROM Medicament";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int cisCode = resultSet.getInt("CodeCis");
                    String denomination = resultSet.getString("NomMedicament");
                    String formeAdministration = resultSet.getString("Forme_D_Administration");
                    String statusAdministration = resultSet.getString("Statut_Administration");
                    String procedureAutorisation = resultSet.getString("Procedure_autorisation");
                    String etatCommercialisation = resultSet.getString("Etat_Commercialisation");
                    String titulaire = resultSet.getString("Titulaire");
                    boolean surveillance = resultSet.getBoolean("Surveillance");
                    medicaments.add(new Medicament(cisCode, denomination, formeAdministration, statusAdministration, procedureAutorisation, etatCommercialisation, titulaire, surveillance));
                }

                query = "SELECT * FROM pharmacies";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    pharmacies.add(new Pharmacie(id, name));
                }

                query = "SELECT * FROM Medicament_Signalement";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int queryCisCode = resultSet.getInt("medicamentId");
                    int queryPharmacieId = resultSet.getInt("pharmacieId");
                    Date queryDate = resultSet.getDate("dateSignalement");
                    int queryUserId = resultSet.getInt("userId");
                    String queryVille = resultSet.getString("ville");
                    int queryDepartement = resultSet.getInt("departement");
                    Saisie saisie = new Saisie(queryUserId, getMedicamentByCode(queryCisCode), getPharmacieByName(pharmacies.get(queryPharmacieId).getName()), queryDate, queryVille, queryDepartement);
                    allSaisies.add(saisie);
                }

                query = "SELECT * FROM villes";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int insee = resultSet.getInt("insee");
                    String name = resultSet.getString("name");
                    int departement = resultSet.getInt("departement");
                    String region = resultSet.getString("region");
                    villes.add(new Ville(insee, name, departement, region));
                }

                preparedStatement.close();

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("Database Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataLoaded = true;
        }
    }

    public static void loadUserSaisies() {
        userSaisies.clear();
        for (Saisie saisie : allSaisies) {
            if (saisie.getUserId() == userId) {
                userSaisies.add(saisie);
            }
        }
    }

    public static List<Medicament> getMedicaments() {
        return medicaments;
    }

    public static List<Pharmacie> getPharmacies() {
        return pharmacies;
    }

    public static LinkedList<Saisie> getAllSaisies() {
        return allSaisies;
    }

    public static LinkedList<Saisie> getUserSaisies() {
        return userSaisies;
    }

    public static Medicament getMedicamentByCode(int cisCode) {
        for (Medicament medicament : medicaments) {
            if (medicament.getCisCode() == cisCode) {
                return medicament;
            }
        }
        return null;
    }

    public static Pharmacie getPharmacieByName(String name) {
        for (Pharmacie pharmacie : pharmacies) {
            if (pharmacie.getName().equals(name)) {
                return pharmacie;
            }
        }
        return null;
    }

    public static Saisie getSaisie(int medicament, int pharmacie) {
        for (Saisie saisie : allSaisies) {
            if (saisie.getMedicament().getCisCode() == medicament && saisie.getPharmacie().getId() == pharmacie) {
                return saisie;
            }
        }
        return null;
    }

}

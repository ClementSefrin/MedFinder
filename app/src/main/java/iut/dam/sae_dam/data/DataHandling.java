package iut.dam.sae_dam.data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import iut.dam.sae_dam.data.medicaments.Medicament;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;
import iut.dam.sae_dam.data.saisies.Saisie;
import iut.dam.sae_dam.data.villes.Ville;

public class DataHandling {
    private static boolean dataLoaded = false;
    private static int dataDeleted = 0;
    private static List<Medicament> medicaments = new LinkedList<>();
    private static List<Pharmacie> pharmacies = new LinkedList<>();
    private static LinkedList<Ville> villes = new LinkedList<>();
    private static LinkedList<Saisie> allSaisies = new LinkedList<>();
    private static LinkedList<Saisie> userSaisies = new LinkedList<>();
    private static LinkedList<Saisie> newSaisies = new LinkedList<>();
    private static int userId, city;
    private static boolean admin;
    private static String password;

    public static void loadData() {
        dataLoaded = false;
        new LoadData().execute();
    }

    public static void saveData() {
        new SaveData().execute();
    }

    public static void getIntentData(int userId, String password, boolean admin, int city) {
        DataHandling.userId = userId;
        DataHandling.password = password;
        DataHandling.admin = admin;
        DataHandling.city = city;
        loadUserSaisies();
    }

    public static void addData(Saisie saisie) {
        allSaisies.add(saisie);
        userSaisies.add(saisie);
        newSaisies.add(saisie);
    }

    public static void supprimerHisto() {
        new DeleteData().execute();
        for (Saisie s : userSaisies) {
            allSaisies.remove(s);
        }
        userSaisies.clear();
        newSaisies.clear();

    }

    public static boolean isDataLoaded() {
        return dataLoaded;
    }

    public static void deleteAccount() {
        int val = dataDeleted;
        supprimerHisto();
        while (val == dataDeleted) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new DeleteAccount().execute();
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
                    boolean surveillance = (resultSet.getString("Surveillance").equalsIgnoreCase("Non")) ? false : true;
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

                query = "SELECT * FROM villes";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int insee = resultSet.getInt("insee");
                    String name = resultSet.getString("name");
                    int departement = resultSet.getInt("departement");
                    String region = resultSet.getString("region");
                    Ville ville = new Ville(insee, name, departement, region, 0);
                    villes.add(ville);
                }

                query = "SELECT * FROM Medicament_Signalement";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int queryCisCode = resultSet.getInt("medicamentId");
                    int queryPharmacieId = resultSet.getInt("pharmacieId");
                    Date queryDate = resultSet.getDate("dateSignalement");
                    int queryUserId = resultSet.getInt("userId");
                    int queryVille = resultSet.getInt("ville");
                    Ville ville = getCitybyInsee(queryVille);
                    Saisie saisie = new Saisie(queryUserId, getMedicamentByCode(queryCisCode), getPharmacieById(queryPharmacieId), queryDate, ville, ville.getDepartement());
                    allSaisies.add(saisie);
                }

                preparedStatement.close();

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataLoaded = true;
        }
    }

    private static class SaveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "INSERT INTO Medicament_Signalement (medicamentId, userId , pharmacieId, dateSignalement, ville) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                for (Saisie saisie : newSaisies) {
                    preparedStatement.setInt(1, saisie.getMedicament().getCisCode());
                    preparedStatement.setInt(2, saisie.getUserId());
                    preparedStatement.setInt(3, saisie.getPharmacie().getId());
                    preparedStatement.setDate(4, saisie.getDate());
                    preparedStatement.setInt(5, saisie.getCity().getInsee());
                    preparedStatement.executeUpdate();
                }
                preparedStatement.close();

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newSaisies.clear();
        }
    }

    private static class DeleteData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "DELETE FROM Medicament_Signalement WHERE userId = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();

                preparedStatement.close();

                DatabaseConnection.closeConnection(connection);
                dataDeleted++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class DeleteAccount extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "DELETE FROM user WHERE Id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();

                preparedStatement.close();

                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
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

    public static LinkedList<Ville> getVilles() {
        return villes;
    }

    public static Medicament getMedicamentByCode(int cisCode) {
        for (Medicament medicament : medicaments) {
            if (medicament.getCisCode() == cisCode) {
                return medicament;
            }
        }
        return null;
    }

    public static boolean estUnMedicament(int cisCode) {
        for (Medicament medicament : medicaments) {
            if (medicament.getCisCode() == cisCode) {
                return true;
            }
        }
        return false;
    }

    public static Pharmacie getPharmacieByName(String name) {
        for (Pharmacie pharmacie : pharmacies) {
            if (pharmacie.getName().equals(name)) {
                return pharmacie;
            }
        }
        return null;
    }

    public static Ville getCitybyInsee(int insee) {
        for (Ville ville : villes) {
            if (ville.getInsee() == insee) {
                return ville;
            }
        }
        return null;
    }

    public static Pharmacie getPharmacieById(int id) {
        for (Pharmacie pharmacie : pharmacies) {
            if (pharmacie.getId() == id) {
                return pharmacie;
            }
        }
        return null;
    }
}

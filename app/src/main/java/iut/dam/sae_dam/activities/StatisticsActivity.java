package iut.dam.sae_dam.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import iut.dam.sae_dam.R;
import iut.dam.sae_dam.data.DataHandling;
import iut.dam.sae_dam.data.pharmacies.Pharmacie;
import iut.dam.sae_dam.data.saisies.Saisie;
import iut.dam.sae_dam.data.villes.Ville;

public class StatisticsActivity extends AppCompatActivity {
    private ScrollView mainSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();
        initButtons();
    }

    private void setListener(Button button, View view) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTo(view);
            }
        });
    }

    private void initButtons() {
        setListener(findViewById(R.id.statistics_medicamentBTN), findViewById(R.id.statistics_medicamentCV));
        setListener(findViewById(R.id.statistics_villeBTN), findViewById(R.id.statistics_cityCV));
        setListener(findViewById(R.id.statistics_departementBTN), findViewById(R.id.statistics_departementCV));
        setListener(findViewById(R.id.statistics_regionBTN), findViewById(R.id.statistics_regionCV));
        setListener(findViewById(R.id.statistics_pharmacieBTN), findViewById(R.id.statistics_pharmacieCV));
        setListener(findViewById(R.id.statistics_formeAdministrationBTN), findViewById(R.id.statistics_formeAdministrationCV));
        setListener(findViewById(R.id.statistics_statutAdministrationBTN), findViewById(R.id.statistics_statutCV));
        setListener(findViewById(R.id.statistics_procedureAutorisationBTN), findViewById(R.id.statistics_procedureAutorisationCV));
        setListener(findViewById(R.id.statistics_titulaireBTN), findViewById(R.id.statistics_titulaireCV));
        setListener(findViewById(R.id.statistics_surveillanceRenforceeBTN), findViewById(R.id.statistics_surveillanceCV));

        FloatingActionButton backBTN = findViewById(R.id.statistics_scrollToTopBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTo(findViewById(R.id.statistics_navigationButtonsLL));
            }
        });
    }

    private void initData() {
        mainSV = findViewById(R.id.statistics_mainSV);
        LinkedList<Saisie> allSaisies = DataHandling.getAllSaisies();

        //Entry ordered by medicament
        LinearLayout medicamentLegendLL = findViewById(R.id.statistics_medicamentLegendLL);
        PieChart medicamentPIE = findViewById(R.id.statistics_medicamentPIE);
        HashMap<String, Integer> saisiesByMedicament = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByMedicament.get(saisie.getMedicament().getDenomination());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByMedicament.put(saisie.getMedicament().getDenomination(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedMedicamentMap = getSortedMap(saisiesByMedicament);
        for (String medicament : sortedMedicamentMap.keySet()) {
            displayStats(medicamentLegendLL, medicamentPIE, medicament, sortedMedicamentMap.get(medicament));
        }
        medicamentPIE.startAnimation();

        //Entry ordered by city
        LinearLayout cityLegendLL = findViewById(R.id.statistics_cityLegendLL);
        PieChart cityPIE = findViewById(R.id.statistics_cityPIE);
        HashMap<Ville, Integer> saisiesByCity = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByCity.get(saisie.getCity());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByCity.put(saisie.getCity(), nbSaisies);
        }

        LinkedHashMap<Ville, Integer> sortedCityMap = getSortedMap(saisiesByCity);
        for (Ville ville : sortedCityMap.keySet()) {
            displayStats(cityLegendLL, cityPIE, ville.getName(), sortedCityMap.get(ville));
        }
        cityPIE.startAnimation();

        //Entry ordered by departement
        LinearLayout departementLegendLL = findViewById(R.id.statistics_departementLegendLL);
        PieChart departementPIE = findViewById(R.id.statistics_departementPIE);
        HashMap<Integer, Integer> saisiesByDepartement = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByDepartement.get(saisie.getCity().getDepartement());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByDepartement.put(saisie.getCity().getDepartement(), nbSaisies);
        }

        LinkedHashMap<Integer, Integer> sortedDepartementMap = getSortedMap(saisiesByDepartement);
        for (Integer departement : sortedDepartementMap.keySet()) {
            displayStats(departementLegendLL, departementPIE, departement.toString(), sortedDepartementMap.get(departement));
        }
        departementPIE.startAnimation();

        //Entry ordered by region
        LinearLayout regionLegendLL = findViewById(R.id.statistics_regionLegendLL);
        PieChart regionPIE = findViewById(R.id.statistics_regionPIE);
        HashMap<String, Integer> saisiesByRegion = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByRegion.get(saisie.getCity().getRegion());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByRegion.put(saisie.getCity().getRegion(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedRegionMap = getSortedMap(saisiesByRegion);
        for (String region : sortedRegionMap.keySet()) {
            displayStats(regionLegendLL, regionPIE, region, sortedRegionMap.get(region));
        }
        regionPIE.startAnimation();

        //Entry ordered by pharmacy
        LinearLayout pharmacyLegendLL = findViewById(R.id.statistics_pharmacieLegendLL);
        PieChart pharmacyPIE = findViewById(R.id.statistics_pharmaciePIE);
        HashMap<Pharmacie, Integer> saisiesByPharmacie = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByPharmacie.get(saisie.getPharmacie());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByPharmacie.put(saisie.getPharmacie(), nbSaisies);
        }

        LinkedHashMap<Pharmacie, Integer> sortedPharmacieMap = getSortedMap(saisiesByPharmacie);
        for (Pharmacie pharmacie : sortedPharmacieMap.keySet()) {
            displayStats(pharmacyLegendLL, pharmacyPIE, pharmacie.getName(), sortedPharmacieMap.get(pharmacie));
        }
        pharmacyPIE.startAnimation();

        //Entry ordered by forme d'administration
        LinearLayout formeAdministrationLegendLL = findViewById(R.id.statistics_formeAdministrationLegendLL);
        PieChart formeAdministrationPIE = findViewById(R.id.statistics_formeAdministrationPIE);
        HashMap<String, Integer> saisiesByFormeAdministration = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByFormeAdministration.get(saisie.getMedicament().getFormeAdministration());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByFormeAdministration.put(saisie.getMedicament().getFormeAdministration(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedFormeAdministrationMap = getSortedMap(saisiesByFormeAdministration);
        for (String formeAdministration : sortedFormeAdministrationMap.keySet()) {
            displayStats(formeAdministrationLegendLL, formeAdministrationPIE, formeAdministration, sortedFormeAdministrationMap.get(formeAdministration));
        }
        formeAdministrationPIE.startAnimation();

        //Entry ordered by statut
        LinearLayout statutLegendLL = findViewById(R.id.statistics_statutLegendLL);
        PieChart statutPIE = findViewById(R.id.statistics_statutPIE);
        HashMap<String, Integer> saisiesByStatut = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByStatut.get(saisie.getMedicament().getStatusAdministration());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByStatut.put(saisie.getMedicament().getStatusAdministration(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedStatutMap = getSortedMap(saisiesByStatut);
        for (String statut : sortedStatutMap.keySet()) {
            displayStats(statutLegendLL, statutPIE, statut, sortedStatutMap.get(statut));
        }
        statutPIE.startAnimation();

        //Entry ordered by procedure d'autorisation
        LinearLayout procedureAutorisationLegendLL = findViewById(R.id.statistics_procedureAutorisationLegendLL);
        PieChart procedureAutorisationPIE = findViewById(R.id.statistics_procedureAutorisationPIE);
        HashMap<String, Integer> saisiesByProcedureAutorisation = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByProcedureAutorisation.get(saisie.getMedicament().getProcedureAutorisation());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByProcedureAutorisation.put(saisie.getMedicament().getProcedureAutorisation(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedProcedureAutorisationMap = getSortedMap(saisiesByProcedureAutorisation);
        for (String procedureAutorisation : sortedProcedureAutorisationMap.keySet()) {
            displayStats(procedureAutorisationLegendLL, procedureAutorisationPIE, procedureAutorisation, sortedProcedureAutorisationMap.get(procedureAutorisation));
        }
        procedureAutorisationPIE.startAnimation();

        //Entry ordered by titulaire
        LinearLayout titulaireLegendLL = findViewById(R.id.statistics_titulaireLegendLL);
        PieChart titulairePIE = findViewById(R.id.statistics_titulairePIE);
        HashMap<String, Integer> saisiesByTitulaire = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesByTitulaire.get(saisie.getMedicament().getTitulaire());
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesByTitulaire.put(saisie.getMedicament().getTitulaire(), nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedTitulaireMap = getSortedMap(saisiesByTitulaire);
        for (String titulaire : sortedTitulaireMap.keySet()) {
            displayStats(titulaireLegendLL, titulairePIE, titulaire, sortedTitulaireMap.get(titulaire));
        }
        titulairePIE.startAnimation();

        //Entry ordered by surveillance
        LinearLayout surveillanceLegendLL = findViewById(R.id.statistics_surveillanceLegendLL);
        PieChart surveillancePIE = findViewById(R.id.statistics_surveillancePIE);
        HashMap<String, Integer> saisiesBySurveillance = new HashMap<>();

        for (Saisie saisie : allSaisies) {
            Integer nbSaisies = saisiesBySurveillance.get(saisie.getMedicament().isSurveillance() ? "Oui" : "Non");
            if (nbSaisies == null) {
                nbSaisies = 1;
            } else {
                ++nbSaisies;
            }
            saisiesBySurveillance.put(saisie.getMedicament().isSurveillance() ? "Oui" : "Non", nbSaisies);
        }

        LinkedHashMap<String, Integer> sortedSurveillanceMap = getSortedMap(saisiesBySurveillance);
        for (String surveillance : sortedSurveillanceMap.keySet()) {
            displayStats(surveillanceLegendLL, surveillancePIE, surveillance, sortedSurveillanceMap.get(surveillance));
        }
        surveillancePIE.startAnimation();
    }

    private void scrollTo(View viewToScrollTo) {
        mainSV.post(new Runnable() {
            @Override
            public void run() {
                mainSV.smoothScrollTo(0, viewToScrollTo.getTop() - 20 * (int) getResources().getDisplayMetrics().density);
            }
        });
    }

    private <E> LinkedHashMap<E, Integer> getSortedMap(HashMap<E, Integer> map) {
        List<Entry<E, Integer>> entryList = new ArrayList<>(map.entrySet());
        Collections.sort(entryList, new Comparator<Entry<E, Integer>>() {
            @Override
            public int compare(Entry<E, Integer> o1, Entry<E, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        LinkedHashMap<E, Integer> sortedMap = new LinkedHashMap<>();
        for (Entry<E, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private void displayStats(LinearLayout legendLL, PieChart pie, String contentText, Integer number) {
        int density = (int) getResources().getDisplayMetrics().density;
        int marginItem = 5 * density;
        int colorSize = 15 * density;
        int nbSize = 100 * density;
        Set<Integer> usedColors = new HashSet<>();

        RelativeLayout legendItemRL = new RelativeLayout(this);
        RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(marginItem, marginItem, marginItem, marginItem);
        legendItemRL.setPadding(marginItem, marginItem, marginItem, marginItem);
        legendItemRL.setBackground(getDrawable(R.drawable.stats_item_background));
        legendItemRL.setLayoutParams(itemParams);

        View color = new View(this);
        RelativeLayout.LayoutParams colorParams = new RelativeLayout.LayoutParams(colorSize, RelativeLayout.LayoutParams.MATCH_PARENT);
        colorParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        colorParams.addRule(RelativeLayout.CENTER_VERTICAL);
        colorParams.width = (int) (colorSize * density);
        colorParams.height = (int) (colorSize * density);
        int randomColor = getRandomColor(usedColors);
        usedColors.add(randomColor);
        color.setLayoutParams(colorParams);
        color.setBackground(getDrawable(R.drawable.stats_item_background));
        color.setBackgroundTintList(ColorStateList.valueOf(randomColor));
        color.setId(ViewCompat.generateViewId());
        legendItemRL.addView(color);

        TextView nbSaisie = new TextView(this);
        RelativeLayout.LayoutParams nbSaisieParams = new RelativeLayout.LayoutParams(nbSize, RelativeLayout.LayoutParams.WRAP_CONTENT);
        nbSaisieParams.addRule(RelativeLayout.CENTER_VERTICAL);
        nbSaisieParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        nbSaisie.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nbSaisie.setBackground(getDrawable(R.drawable.stats_item_background));
        LinearLayout ll = findViewById(R.id.statistics_cityLL);
        nbSaisie.setBackgroundTintList(ColorStateList.valueOf(((ColorDrawable) ll.getBackground()).getColor()));
        nbSaisie.setLayoutParams(nbSaisieParams);
        nbSaisie.setText(number.toString());
        nbSaisie.setId(ViewCompat.generateViewId());
        legendItemRL.addView(nbSaisie);

        TextView content = new TextView(this);
        RelativeLayout.LayoutParams cityNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cityNameParams.addRule(RelativeLayout.END_OF, color.getId());
        cityNameParams.addRule(RelativeLayout.START_OF, nbSaisie.getId());
        cityNameParams.addRule(RelativeLayout.CENTER_VERTICAL);
        cityNameParams.setMargins(marginItem, 0, marginItem, 0);
        content.setLayoutParams(cityNameParams);
        content.setText(contentText);
        legendItemRL.addView(content);

        legendLL.addView(legendItemRL);

        String colorString = "#" + Integer.toHexString(randomColor).substring(2);
        pie.addPieSlice(new PieModel(
                contentText,
                number,
                Color.parseColor(colorString)
        ));
    }

    private int getRandomColor(Set<Integer> usedColors) {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        while (usedColors.contains(color)) {
            color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        return color;
    }
}
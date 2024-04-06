package iut.dam.sae_dam.data.medicaments;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> implements Filterable {
    private List<Medicament> medicineListFull;

    public MedicamentAdapter(Context context, List<Medicament> medicineList) {
        super(context, android.R.layout.simple_dropdown_item_1line, medicineList);
        medicineListFull = new ArrayList<>(medicineList);
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return medicineFilter;
    }

    public boolean contains(int id, String name, String formeAdministration, String statusAdministration,
                            String procedureAutorisation, String etatCommercialisation, String titulaire, boolean surveillance) {
        return medicineListFull.contains(new Medicament(id, name, formeAdministration, statusAdministration,
                procedureAutorisation, etatCommercialisation, titulaire, surveillance));
    }

    private Filter medicineFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Medicament> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(medicineListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Medicament medicine : medicineListFull) {
                    if (String.valueOf(medicine.getCisCode()).contains(filterPattern) ||
                            medicine.getDenomination().toLowerCase().contains(filterPattern)) {
                        suggestions.add(medicine);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return String.valueOf(((Medicament) resultValue).getCisCode());
        }
    };
}

package iut.dam.sae_dam.ui.cip.medicaments;
// MedicineAdapter.java
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> implements Filterable {

    private List<Medicament> medicineListFull;  // Full list of medicines for filtering

    public MedicamentAdapter(Context context, List<Medicament> medicineList) {
        super(context, android.R.layout.simple_dropdown_item_1line, medicineList);
        medicineListFull = new ArrayList<>(medicineList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return medicineFilter;
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
                    if (String.valueOf(medicine.getCIS()).contains(filterPattern) ||
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
            return String.valueOf(((Medicament) resultValue).getCIS());
        }
    };
}

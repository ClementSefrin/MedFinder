package iut.dam.sae_dam.saisies;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.List;

import iut.dam.sae_dam.DataHandling;
import iut.dam.sae_dam.R;

public class SaisieAdapter extends ArrayAdapter<Saisie> {

    TextView nomMedicamentTV, codeCisTV, dateSaisieTV, pharmacieTV;
    private final Activity activity;
    private final int itemResourceId;
    private List<Saisie> items;

    public SaisieAdapter(Activity activity, int itemResourceId, List<Saisie> items) {
        super(activity, itemResourceId);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;

        if (layout == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);
        }

        nomMedicamentTV = (TextView) layout.findViewById(R.id.itemSaisie_nomMedicamentTV);
        codeCisTV = (TextView) layout.findViewById(R.id.itemSaisie_codeCisTV);
        dateSaisieTV = (TextView) layout.findViewById(R.id.itemSaisie_dateSaisieTV);
        pharmacieTV = (TextView) layout.findViewById(R.id.itemSaisie_pharmacieTV);

        Saisie currentSaisie = items.get(position);
        nomMedicamentTV.setText(currentSaisie.getMedicament().getDenomination());
        codeCisTV.setText(currentSaisie.getMedicament().getCisCode());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity.getApplicationContext());

        dateSaisieTV.setText(dateFormat.format(currentSaisie.getDateSaisie()));

        pharmacieTV.setText(currentSaisie.getPharmacie().getName());
        return layout;
    }
}

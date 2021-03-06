package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.Characteristic;
import org.smartregister.sync.helper.CharacteristicsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchPopulationCharacteristicsTask extends AsyncTask<Void, Void, List<Characteristic>> {

    private PopulationCharacteristicsContract.Presenter presenter;

    public FetchPopulationCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected List<Characteristic> doInBackground(final Void... params) {
        CharacteristicsHelper helper = new CharacteristicsHelper(Constants.PREF_KEY.POPULATION_CHARACTERISTICS);
        return helper.getCharacteristics();
    }

    @Override
    protected void onPostExecute(final List<Characteristic> result) {
        presenter.renderView(result);
    }
}

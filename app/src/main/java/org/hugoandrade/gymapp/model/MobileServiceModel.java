package org.hugoandrade.gymapp.model;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class MobileServiceModel extends MobileClientModelBase<MVP.RequiredMobileServicePresenterOps>

        implements MVP.ProvidedMobileServiceModelOps {

    @Override
    public IMobileClientService getService() {
        return super.getService();
    }

    @Override
    public void sendResults(MobileClientData data) {
        getPresenter().sendResults(data);
    }
}

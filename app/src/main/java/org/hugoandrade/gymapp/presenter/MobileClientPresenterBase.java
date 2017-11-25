package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.common.ContextView;
import org.hugoandrade.gymapp.common.PresenterOps;
import org.hugoandrade.gymapp.model.IMobileClientService;
import org.hugoandrade.gymapp.model.MobileServiceModel;

public abstract class MobileClientPresenterBase<RequiredMainOps extends ContextView>

        extends PresenterBase<RequiredMainOps,
                              MVP.RequiredMobileServicePresenterOps,
                              MVP.ProvidedMobileServiceModelOps,
                              MobileServiceModel>

        implements PresenterOps<RequiredMainOps>,
                   MVP.RequiredMobileServicePresenterOps {

    @Override
    public void onCreate(RequiredMainOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(view, MobileServiceModel.class, this);
    }

    @Override
    public void onResume() {
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(RequiredMainOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        getModel().onDestroy(isChangingConfiguration);
    }

    IMobileClientService getMobileClientService() {
        return getModel().getService();
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }
}

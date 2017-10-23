package org.hugoandrade.gymapp.presenter;

import android.util.Log;

import org.hugoandrade.gymapp.common.ModelOps;

import java.lang.ref.WeakReference;

public abstract class PresenterBase<ProvidedViewOps,
                                    RequiredPresenterOps,
                                    ProviderModelOps,
                                    ModelType extends ModelOps<RequiredPresenterOps>> {

    protected final String TAG = getClass().getSimpleName();

    private ModelType mOpsInstance;

    protected WeakReference<ProvidedViewOps> mView;

    public void onCreate(ProvidedViewOps view,
                         Class<ModelType> opsType,
                         RequiredPresenterOps presenter) {

        mView = new WeakReference<>(view);

        try {
            initialize(opsType, presenter);
        } catch (IllegalAccessException e) {
            Log.d(TAG, "IllegalAccessException: " + e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            Log.d(TAG, "InstantiationException: " + e);
            throw new RuntimeException(e);
        }
    }

    private void initialize(Class<ModelType> opsType, RequiredPresenterOps presenter)
            throws IllegalAccessException, InstantiationException {
        mOpsInstance = opsType.newInstance();

        mOpsInstance.onCreate(presenter);
    }

    @SuppressWarnings("unchecked")
    protected ProviderModelOps getModel(){
        return (ProviderModelOps) mOpsInstance;
    }

    /**
     * Return the ProvidedViewOps instance for use by the
     * application.
     */
    public ProvidedViewOps getView() {
        return mView.get();
    }

}

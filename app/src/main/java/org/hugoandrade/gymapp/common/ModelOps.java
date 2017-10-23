package org.hugoandrade.gymapp.common;


public interface ModelOps<RequiredPresenterOps> {
    void onCreate(RequiredPresenterOps presenter);
    void onDestroy(boolean isChangingConfigurations);
}

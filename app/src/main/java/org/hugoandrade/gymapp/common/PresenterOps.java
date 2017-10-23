package org.hugoandrade.gymapp.common;


public interface PresenterOps <ViewOps> {
    void onCreate(ViewOps view);
    void onResume();
    void onPause();
    void onDestroy(boolean isChangingConfiguration);
    void onConfigurationChange(ViewOps view);

}

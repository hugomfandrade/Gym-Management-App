package org.hugoandrade.gymapp.common;

import android.content.Context;

/**
 * Defines methods for obtaining Contexts used by all views in the "View" layer
 */
public interface ContextView {

    Context getActivityContext();

    Context getApplicationContext();
}

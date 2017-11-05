package org.hugoandrade.gymapp.view.helper;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDropped(int position);
}

package org.hugoandrade.gymapp.view.staff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.hugoandrade.gymapp.R;

/**
 * Created by Hugo Andrade on 22/10/2017.
 */

public class StaffMainActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, StaffMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_staff_main);
    }
}

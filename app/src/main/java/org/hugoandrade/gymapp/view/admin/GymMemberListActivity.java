package org.hugoandrade.gymapp.view.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.R;

public class GymMemberListActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, GymMemberListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_main);

        TextView tvAdminGreeting = (TextView) findViewById(R.id.tv_admin_greeting);
        tvAdminGreeting.setText(TextUtils.concat("Hello, ",
                                                 GlobalData.getUser().getUsername(),
                                                 "!!"));

        View tvGymMemberList = findViewById(R.id.tv_check_gym_members);
        tvGymMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GymMemberListActivity.makeIntent(GymMemberListActivity.this));
            }
        });

        View tvGymStaffList = findViewById(R.id.tv_check_gym_staff);
        tvGymStaffList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GymMemberListActivity.makeIntent(GymMemberListActivity.this));
            }
        });
    }

}

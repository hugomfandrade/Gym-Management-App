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
import org.hugoandrade.gymapp.data.User;

public class AdminMainActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, AdminMainActivity.class);
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
                startActivity(GymUserListActivity.makeIntent(AdminMainActivity.this, User.Credential.MEMBER));
            }
        });

        View tvGymStaffList = findViewById(R.id.tv_check_gym_staff);
        tvGymStaffList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GymUserListActivity.makeIntent(AdminMainActivity.this, User.Credential.STAFF));
            }
        });
    }

}

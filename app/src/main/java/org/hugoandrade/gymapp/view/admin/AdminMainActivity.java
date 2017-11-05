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

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
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

        // set up greeting text
        TextView tvAdminGreeting = (TextView) findViewById(R.id.tv_admin_greeting);
        tvAdminGreeting.setText(TextUtils.concat("Hello, ",
                                                 GlobalData.getUser().getUsername(),
                                                 "!!"));

        View tvGymMemberList = findViewById(R.id.tv_check_gym_members);
        tvGymMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Admin wants to check the gym member list. Start activity using MEMBER credential
                startActivity(GymUserListActivity.makeIntent(AdminMainActivity.this, User.Credential.MEMBER));
            }
        });

        View tvGymStaffList = findViewById(R.id.tv_check_gym_staff);
        tvGymStaffList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Admin wants to check the gym staff list. Start activity using STAFF credential
                startActivity(GymUserListActivity.makeIntent(AdminMainActivity.this, User.Credential.STAFF));
            }
        });

        View tvExercisesList = findViewById(R.id.tv_check_exercises);
        tvExercisesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Admin wants to check the exercises of the gym.
                startActivity(ExerciseListActivity.makeIntent(AdminMainActivity.this));
            }
        });
    }

}

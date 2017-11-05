package org.hugoandrade.gymapp.view.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.utils.UIUtils;


public class MemberMainActivity extends AppCompatActivity {

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, MemberMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_main);

        // set up greeting text
        TextView tvAdminGreeting = (TextView) findViewById(R.id.tv_member_greeting);
        tvAdminGreeting.setText(TextUtils.concat("Hello, ",
                GlobalData.getUser().getUsername(),
                "!!"));

        View tvCheckMyGymStaff = findViewById(R.id.tv_check_my_staff);
        tvCheckMyGymStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Member wants to check their 'My Staff' list
                startActivity(MyGymStaffActivity.makeIntent(MemberMainActivity.this));
            }
        });

        View tvBuildWorkout = findViewById(R.id.tv_build_workout);
        tvBuildWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Member wants to create a exercise plan
                startActivity(BuildWorkoutActivity.makeIntent(MemberMainActivity.this));
            }
        });

        View tvCheckMyHistory = findViewById(R.id.tv_check_my_history);
        tvCheckMyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Member wants to check the history of saved exercise plans
                startActivity(HistoryActivity.makeIntent(MemberMainActivity.this));
            }
        });
    }
}

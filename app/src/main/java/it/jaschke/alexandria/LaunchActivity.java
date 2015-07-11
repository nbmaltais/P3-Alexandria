package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by Nicolas on 2015-07-11.
 */
public class LaunchActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String start = sp.getString("pref_startFragment", "0");
            if(start.equals("0"))
            {
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        else
            {
                Intent intent = new Intent(this,AddBookActivity.class);


                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(intent)
                        .startActivities();

                //startActivity(intent);
            }
        finish();
    }
}

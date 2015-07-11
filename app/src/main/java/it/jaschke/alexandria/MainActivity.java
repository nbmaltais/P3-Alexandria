package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends AppCompatActivity
        implements  Callback , BookDetail.Host
{
    static final String TAG=MainActivity.class.getSimpleName();

    private static final String TAG_ADD_BOOK="ADDBOOK";

    private boolean mTwoPane=false;

    private BroadcastReceiver messageReceiver;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private boolean mStartAddBookActivity=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if(findViewById(R.id.right_container) != null){
            mTwoPane=true;
        }


        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,filter);

        if(savedInstanceState==null)
        {
            //TODo: this always fire the add book activity a we can't come back...
            // lauched, check preference
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String start = sp.getString("pref_startFragment", "0");
            /*if(!start.equals("0"))
            {
                Intent intent = new Intent(this,AddBookActivity.class);
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(intent)
                        .startActivities();
            }*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_about)
        {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        /*if(mStartAddBookActivity)
            Navigation.startAddBookActivity(this);*/
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {


        if(mTwoPane)
        {
            Bundle args = new Bundle();
            args.putString(BookDetail.EAN_KEY, ean);

            BookDetail fragment = new BookDetail();
            fragment.setArguments(args);

            int id = R.id.container;
            if (findViewById(R.id.right_container) != null)
            {
                id = R.id.right_container;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(id, fragment)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(this,BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_EAN,ean);
            startActivity(intent);
        }

    }

    @Override
    public void onBookDeleted()
    {
        if(mTwoPane)
        {
            /*getSupportFragmentManager().beginTransaction()
                    .remove(R.id.right_container)
                    .commit();*/
            // TODO remove fragment from right pane
        }
    }

    @Override
    public void setBookTitle(String title)
    {
        // DO nothing
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }




}
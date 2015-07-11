package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private boolean mTwoPane=false;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    //public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if(findViewById(R.id.right_container) != null){
            mTwoPane=true;
        }


        messageReciever = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

    }


    public void setTitle(int titleId) {
        title = getString(titleId);
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
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
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
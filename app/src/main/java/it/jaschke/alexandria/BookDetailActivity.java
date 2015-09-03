package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class BookDetailActivity extends AppCompatActivity implements BookDetailFragment.Host
{

    public static final String EXTRA_EAN = "EXTRA_EAN";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        if(savedInstanceState==null)
        {
            Intent intent = getIntent();
            if (intent != null)
            {
                String ean = intent.getStringExtra(EXTRA_EAN);
                Bundle args = new Bundle();
                args.putString(BookDetailFragment.EAN_KEY, ean);
                args.putBoolean(BookDetailFragment.ARG_SET_TITLE, true);
                args.putBoolean(BookDetailFragment.ARG_POSTPONED_TRANSITION, true);

                BookDetailFragment fragment = new BookDetailFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.book_detail_fragment, fragment)
                        .commit();

            }

            supportPostponeEnterTransition();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookDeleted()
    {
        finish();
    }

    @Override
    public void setBookTitle(String title)
    {
        setTitle(title);
    }
}

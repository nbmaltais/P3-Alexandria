package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class AddBookActivity extends AppCompatActivity implements ConfirmAddBook.Host, AddBook.Host
{
    static final String TAG = AddBookActivity.class.getSimpleName();
    static final String FAGMENTTAG="ConfirmAddBook";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(resultCode != RESULT_OK)
            return;

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {


            Fragment f = getSupportFragmentManager().findFragmentById(R.id.add_book_fragment);
            if(f==null)
                return; // The fragment is not here???
            AddBook ad = (AddBook)f;
            Log.d(TAG, "Received scan result: " + scanResult.toString());
            if( scanResult.getFormatName().equals("EAN_13") )
            {
                String ean = scanResult.getContents();
                ad.addBook(ean);
            }
            else
            {
                ad.onInvalidScanFormat();
            }
        }
        else
        {
            Log.d(TAG,"Received invalid scan result");
        }
    }

    @Override
    public void onSaveBook(String title, String ean)
    {
        Toast.makeText(this, getString(R.string.book_added_toast, title), Toast.LENGTH_LONG).show();
        dismissConfrimAddBookDialog();
    }

    private void dismissConfrimAddBookDialog()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FAGMENTTAG);
    }

    @Override
    public void onDeleteBook(String ean)
    {

    }

    @Override
    public void confirmAddBook(String ean)
    {
        ConfirmAddBook confirmAddBook = ConfirmAddBook.newInstance(ean);
        confirmAddBook.show(getSupportFragmentManager(),FAGMENTTAG);
    }
}

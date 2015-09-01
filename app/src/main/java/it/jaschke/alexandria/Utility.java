package it.jaschke.alexandria;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Nicolas on 2015-07-09.
 */
public class Utility
{
    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    static public void loadBookCoverIntoImageView(Context context, String imgUrl, ImageView view)
    {
        if(imgUrl!=null && !imgUrl.isEmpty())
            Picasso.with(context).load(imgUrl).into(view);
        else
        {

            //Picasso.with(context).load(R.drawable.book).into(view);
        }
    }
}

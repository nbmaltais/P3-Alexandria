package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Nicolas on 2015-07-11.
 */
public class Navigation
{
    public static void startAddBookActivity(Context context)
    {
        Intent intent = new Intent(context,AddBookActivity.class);
        context.startActivity(intent);
    }

    public static void startAboutActivity(Context context)
    {
        Intent intent = new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }
}

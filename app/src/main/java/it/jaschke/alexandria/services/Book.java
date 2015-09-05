package it.jaschke.alexandria.services;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas on 2015-09-04.
 */
public class Book implements Parcelable
{
    public final String ean;
    public String title = "";
    public String subtitle = "";
    public String desc="";
    public String imgUrl = null;
    public List<String> authors = new ArrayList<>();
    public List<String> categories = new ArrayList<>();

    public Book(String ean)
    {
        this.ean=ean;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.ean);
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeString(this.desc);
        dest.writeString(this.imgUrl);
        dest.writeStringList(this.authors);
        dest.writeStringList(this.categories);
    }

    protected Book(Parcel in)
    {
        this.ean = in.readString();
        this.title = in.readString();
        this.subtitle = in.readString();
        this.desc = in.readString();
        this.imgUrl = in.readString();
        this.authors = in.createStringArrayList();
        this.categories = in.createStringArrayList();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>()
    {
        public Book createFromParcel(Parcel source)
        {
            return new Book(source);
        }

        public Book[] newArray(int size)
        {
            return new Book[size];
        }
    };
}

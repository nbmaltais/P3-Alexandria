package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class ConfirmAddBook extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOGTAG = AddBook.class.getSimpleName();

    public static final String ARG_EAN = "ARG_EAN";

    private final int LOADER_ID = 1;
    private View mRootView;

    private View mSaveButton;
    private View mDeleteButton;
    private TextView mBookTitleView;
    private TextView mBookSubTitleView;
    private TextView mAuthorsView;
    private TextView mCategoriesView;
    private ImageView mBookCoverView;

    private String mBookEAN;
    private String mBookTitle;

    interface Host
    {
        void onSaveBook( String title, String ean );
        void onDeleteBook(String ean );
    }

    private Host mHost;

    public static ConfirmAddBook newInstance( String bookEAN )
    {
        ConfirmAddBook f = new ConfirmAddBook();
        Bundle args = new Bundle();
        args.putString(ARG_EAN, bookEAN);
        f.setArguments(args);

        return f;
    }

    public ConfirmAddBook(){
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mHost = (Host)activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_confirm_add_book, container, false);

        mBookTitleView = (TextView) mRootView.findViewById(R.id.bookTitle);
        mBookSubTitleView = (TextView) mRootView.findViewById(R.id.bookSubTitle);
        mAuthorsView = (TextView) mRootView.findViewById(R.id.authors);
        mCategoriesView = (TextView) mRootView.findViewById(R.id.categories);

        mBookCoverView = (ImageView) mRootView.findViewById(R.id.bookCover);

        mSaveButton = mRootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveBook();
            }
        });

        mDeleteButton = mRootView.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deleteBook();
            }
        });

        mBookEAN = getArguments().getString(ARG_EAN);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return mRootView;
    }

    void saveBook()
    {
        // Inform host activity that the book was saved
        mHost.onSaveBook(mBookTitle,mBookEAN);
    }

    void deleteBook()
    {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, mBookEAN);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);

        // Inform host activity that the book was deleted
        mHost.onDeleteBook(mBookEAN);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mBookEAN.length()==0){
            return null;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(mBookEAN)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        mBookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleView.setText(mBookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubTitleView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        mAuthorsView.setLines(authorsArr.length);
        mAuthorsView.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));

        Picasso.with(getActivity()).load(imgUrl).into(mBookCoverView);

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategoriesView.setText(categories);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }



}

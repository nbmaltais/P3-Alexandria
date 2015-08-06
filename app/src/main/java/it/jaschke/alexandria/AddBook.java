package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOGTAG = AddBook.class.getSimpleName();
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText mEanEdit;
    private final int LOADER_ID = 1;
    private View mRootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";


    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private String mBookEAN;
    private String mBookString;
    private View mBookDetailView;
    private View mScanButton;
    private View mSaveButton;
    private View mDeleteButton;
    private TextView mBookTitleView;
    private TextView mBookSubTitleView;
    private TextView mAuthorsView;
    private TextView mCategoriesView;
    private ImageView mBookCoverView;
    private Host mHost;

    public interface Host
    {
        void confirmAddBook(String ean);
    }

    boolean checkNetworkAndShowMessage()
    {
        Context context = getActivity();
        if(!Utility.isNetworkAvailable(context))
        {
            Toast.makeText(context,context.getString(R.string.error_no_network),Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void addBook( String ean )
    {
        if(!checkNetworkAndShowMessage())
            return;
        Context context = getActivity();
        mBookEAN=ean;
        mEanEdit.setText(ean);
        Log.d(LOGTAG, "Adding book with mEanEdit = " + ean);

        Intent bookIntent = new Intent(context, BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        context.startService(bookIntent);

        restartLoader();

        //mHost.confirmAddBook(mBookEAN);
    }

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mEanEdit !=null) {
            outState.putString(EAN_CONTENT, mEanEdit.getText().toString());
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        mEanEdit = (EditText) mRootView.findViewById(R.id.ean);

        mBookTitleView = (TextView) mRootView.findViewById(R.id.bookTitle);
        mBookSubTitleView = (TextView) mRootView.findViewById(R.id.bookSubTitle);
        mAuthorsView = (TextView) mRootView.findViewById(R.id.authors);
        mCategoriesView = (TextView) mRootView.findViewById(R.id.categories);

        mBookCoverView = (ImageView) mRootView.findViewById(R.id.bookCover);
        mBookDetailView = mRootView.findViewById(R.id.book_detail);

        mBookDetailView.setVisibility(View.INVISIBLE);

        mEanEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String ean = s.toString();
                // prevent infinite recursion
                if (ean.equals(mBookEAN))
                    return;

                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978"))
                {
                    ean = "978" + ean;
                }
                if (ean.length() < 13)
                {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                addBook(ean);

            }
        });

        mScanButton = mRootView.findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(!checkNetworkAndShowMessage())
                    return;
                // Start a scan using our Capture activity
                Activity activity = getActivity();
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setOrientationLocked(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt(activity.getString(R.string.scanner_prompt));
                integrator.setBeepEnabled(true);

                integrator.initiateScan();

            }
        });

        mSaveButton = mRootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mEanEdit.setText("");
                Toast.makeText(getActivity(), getActivity().getString(R.string.book_added_toast, mBookString), Toast.LENGTH_LONG).show();
            }
        });

        mDeleteButton = mRootView.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEanEdit.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                mEanEdit.setText("");
            }
        });

        if(savedInstanceState!=null){
            mEanEdit.setText(savedInstanceState.getString(EAN_CONTENT));
            //mEanEdit.setHint("");
        }

        checkNetworkAndShowMessage();

        return mRootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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

        mBookString = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleView.setText(mBookString);

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

        mBookDetailView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        mBookDetailView.setVisibility(View.INVISIBLE);
        mBookEAN=null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
        mHost = (Host)activity;
    }

    public void onInvalidScanFormat()
    {
        // notify the user that the scanned item is not an ISBN bar code
        Context ctx = getActivity();

        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.invalid_barcode_dialog_title))
                .setMessage(ctx.getString(R.string.error_invalid_barcode))
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
}

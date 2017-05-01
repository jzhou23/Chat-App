package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    private Uri uri;
    private Context context;
    private int loaderID;
    private IEntityCreator<T> creator;
    private IQueryListener<T> listener;

    private QueryBuilder(String tag, Context context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener) {
        this.uri = uri;
        this.context = context;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    // COMPLET complete the implementation of this
    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);


        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        int loaderID,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);

        LoaderManager lm = context.getLoaderManager();

        Bundle bundle = new Bundle();
        bundle.putStringArray("projection", projection);
        bundle.putString("selection", selection);
        bundle.putStringArray("selectionArgs", selectionArgs);

        lm.initLoader(loaderID, bundle, qb);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == loaderID) {
            if (args != null) {
                return new CursorLoader(context, uri, args.getStringArray("projection"),
                        args.getString("selection"), args.getStringArray("selectionArgs"), null);
            } else {
                return new CursorLoader(context, uri, null, null, null, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (loader.getId() == loaderID) {
            listener.handleResults(new TypedCursor<T>(cursor, creator));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == loaderID) {
            listener.closeResults();
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}

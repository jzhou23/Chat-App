package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor>{

    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;

    private SimpleQueryBuilder(IEntityCreator<T> helper, ISimpleQueryListener<T> listener) {
        this.helper = helper;
        this.listener = listener;
    }

    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }

    // COMPLETED Complete the implementation of this
    public static <T> void executeQuery(Context context,
                                        Uri uri,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener) {

        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);

        AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, null, null, null, null, qb);
    }

    public static <T> void executeQuery(Context context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener) {

        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);

        AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, projection, selection, selectionArgs, null, qb);
    }

    @Override
    public void kontinue(Cursor cursor) {
        // COMPLETED complete this
        List<T> instances = new ArrayList<T>();
        while(cursor.moveToNext()) {
            T instance = helper.create(cursor);
            instances.add(instance);
        }
        cursor.close();
        listener.handleResults(instances);
    }

}

package io.github.libxposed.api;

import android.database.Cursor;

import androidx.annotation.NonNull;

import java.io.Closeable;

import io.github.libxposed.annotation.SinceApi;

/**
 * Read-only SQL view over an APK/JAR/DEX.
 *
 * <p>Backed by the framework's dex query engine; {@link #query} returns an
 * {@link android.database.Cursor} consumed like
 * {@link android.database.sqlite.SQLiteDatabase#rawQuery}. Run as many queries as you like on
 * one instance.</p>
 *
 * <p>The database holds native resources (the mapped dex/cache), so it is {@link Closeable} —
 * {@link #close() close} it when done, ideally with try-with-resources. Closing the database
 * does <b>not</b> close cursors already returned by {@link #query}; close those separately.
 * After {@link #close()}, calling {@link #query} throws {@link IllegalStateException}.</p>
 */
@SinceApi(XposedInterface.API_103)
public interface DexDatabase extends Closeable {
    /**
     * Runs a read-only SQL query against the dex.
     *
     * @param sql The SQL query; only {@code SELECT}-style reads are supported
     * @return A {@link Cursor} over the result; close it when done
     * @throws IllegalArgumentException If the SQL is invalid or not a read-only query
     * @throws IllegalStateException    If the database has been closed
     */
    @NonNull
    Cursor query(@NonNull String sql);

    /**
     * Releases the native resources (mapped dex/cache) held by this database. Idempotent;
     * does not affect cursors already returned by {@link #query}.
     */
    @Override
    void close();
}

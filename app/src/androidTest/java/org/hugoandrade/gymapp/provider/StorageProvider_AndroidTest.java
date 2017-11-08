package org.hugoandrade.gymapp.provider;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.provider.parser.ContentValuesFormatter;
import org.hugoandrade.gymapp.provider.parser.CursorParser;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StorageProvider_AndroidTest extends ProviderTestCase2<StorageProvider> {

    // URI for Item(s)
    private static final Uri mEntryUri = User.Entry.CONTENT_URI;

    private CursorParser parser = new CursorParser();
    private ContentValuesFormatter formatter = new ContentValuesFormatter();

    // Default values for test User(s)
    private static final String TEST_PASSWORD_A = "passwordA";
    private static final String TEST_PASSWORD_B = "passwordB";
    private static final String TEST_USERNAME_A = "usernameA";
    private static final String TEST_USERNAME_B = "usernameB";

    // User(s) with default values.
    static private final User userA = new User(TEST_USERNAME_A, TEST_PASSWORD_A);
    static private final User userB = new User(TEST_USERNAME_B, TEST_PASSWORD_B);

    /**
     * Constructor.
     */
    public StorageProvider_AndroidTest() {
        super(StorageProvider.class, StorageProvider.AUTHORITY);
    }

    /**
     * Constructor.
     *
     * @param providerClass     The class name of the provider under test
     * @param providerAuthority The provider's authority string
     */
    @SuppressWarnings({"UnusedParameters", "unused"})
    public StorageProvider_AndroidTest(Class<StorageProvider> providerClass, String providerAuthority) {
        this();
    }

    /**
     * Setup that will run before every test method
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Method that will run after every test
     */
    @Override
    public void tearDown() {
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the MockContentResolver object, can be altered in future if need be.
     *
     * @return the MockContentResolver object
     */
    @Override
    public MockContentResolver getMockContentResolver() {
        return super.getMockContentResolver();
    }

    /**
     * A simple query with a junk uri parameter
     * a wrong uri results in {@link IllegalArgumentException}
     */
    public void testJunkUris() {
        // verify that that query doesn't return on junk URIs.
        Cursor cursor = null;
        try {
            cursor = getProvider().query(
                    StorageProvider.BASE_URI.buildUpon().appendPath("definitely_invalid").build(),
                    null, null, null, null);
            assertNotNull(cursor);
            // Should have thrown IllegalArgumentException, so now fail.
            fail("cursor.getCount: " + cursor.getCount());
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * A simple query without any parameters, before any inserts returns a
     * non-null cursor
     */
    public void testQueryEmpty() {
        // query the mock ContentResolver on the Item URI, return all rows.
        Cursor cursor = getProvider()
                .query(mEntryUri, null, null, null, null);
        // verify results
        assertNotNull(cursor);
        // results should be 0 because setUp doesn't insert anything into the provider to start
        assertThat(cursor.getCount(), is(0));
    }

    /**
     * A simple query without any parameters, after one insert returns a
     * non-null cursor with size 1 that returns an equal User compared to what was inserted.
     */
    public void testQueryAll() {
        // get ContentValues and insert it into Provider via ContentResolver
        getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));

        // query the mock ContentResolver on the Item URI, return all rows.
        Cursor cursor = getProvider()
                .query(mEntryUri, null, null, null, null);
        // verify cursor non-null
        assertNotNull(cursor);
        // verify results size = 1
        assertThat(cursor.getCount(), is(1));
        // get List<User> from the cursor.
        List<User> entries = parser.parseUsers(cursor);
        // make sure 1 and only 1 User was generated from the cursor
        assertThat(entries.size(), is(1));
        // get the inserted User
        User entry = entries.get(0);
        // check that returned User is non-null
        assertNotNull(entry);
        // verify that the returned User matches the inserted Entry.
        assertTrue(entry.equals(userA));
    }

    /**
     * Check that querying by ID is unsupported
     */
    public void testQueryRowIsUnsupported() {
        // get ContentValues and insert it into Provider via ContentResolver
        Uri resultUri = getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));

        // check that returned User is non-null
        assertNotNull(resultUri);
        // query the mock ContentResolver on the Item URI with ID.
        try {
            getProvider().query(resultUri, null, null, null, null);
            throw new RuntimeException("It is supported when it shouldn't");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    /**
     * Test Insert of single User into ContentProvider.
     */
    public void testInsertUser() {
        // use mock ContentResolver to insert mItem's ContentValues
        Uri resultingUri = getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));
        // Then you can test the correct execution of your insert:
        assertNotNull(resultingUri);
        long id = ContentUris.parseId(resultingUri);
        assertTrue(id > 0);

        Cursor cursor = getProvider().query(mEntryUri, null, null, null,null);

        List<User> entries = parser.parseUsers(cursor);
        assertTrue(entries.get(0).equals(userA));
    }

    /**
     * Test Insert of multiple Users into ContentProvider, and ContentProvider only stores the last.
     */
    public void testInsertMultipleUsersOnlyStoresLast() {
        // use mock ContentResolver to insert mItem's ContentValues
        Uri resultingUri = getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));
        // Then you can test the correct execution of your insert:
        assertNotNull(resultingUri);
        long id = ContentUris.parseId(resultingUri);
        assertTrue(id > 0);

        // use mock ContentResolver to insert mItem's ContentValues
        resultingUri = getProvider().insert(mEntryUri, formatter.getAsContentValues(userB));
        // Then you can test the correct execution of your insert:
        assertNotNull(resultingUri);
        id = ContentUris.parseId(resultingUri);
        assertTrue(id > 0);

        Cursor cursor = getProvider().query(mEntryUri, null, null, null,null);

        // verify results
        assertNotNull(cursor);
        // results should be 1
        assertThat(cursor.getCount(), is(1));

        List<User> entries = parser.parseUsers(cursor);

        // results should be different from first item
        assertFalse(entries.get(0).equals(userA));

        // results should be equal to last inserted item
        assertTrue(entries.get(0).equals(userB));
    }

    /**
     * Test Delete of all Users of ContentProvider.
     */
    public void testDeleteAll() {

        getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));
        int rowsDeleted = getProvider().delete(mEntryUri, null, null);
        assertThat(rowsDeleted, is(1));
        Cursor cursor = getProvider().query(mEntryUri, null, null, null, null);
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(0));
    }

    /**
     * Test Delete of single User is Unsupported.
     */
    public void testDeleteRowIsUnsupported() {

        getProvider().insert(mEntryUri, formatter.getAsContentValues(userB));
        Uri insertedUri = getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));
        assertNotNull(insertedUri);
        try {
            getProvider().delete(insertedUri, null, null);
            throw new RuntimeException("It is supported when it shouldn't");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    /**
     * Test Update of single User is Unsupported.
     */
    public void testUpdateIsUnsupported() {
        getProvider().insert(mEntryUri, formatter.getAsContentValues(userA));

        // test update all rows.
        try {
            getProvider().update(mEntryUri, formatter.getAsContentValues(userB), null, null);
            throw new RuntimeException("It is supported when it shouldn't");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }

        // test update one row.
        try {
            getProvider().update(mEntryUri,
                                 formatter.getAsContentValues(userB),
                                 "Username == ",
                                 new String[]{TEST_PASSWORD_A});
            throw new RuntimeException("It is supported when it shouldn't");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }
}

package org.hugoandrade.gymapp.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.provider.parser.ContentValuesFormatter;
import org.hugoandrade.gymapp.provider.parser.CursorParser;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * https://developer.android.com/training/testing/integration-testing/content-provider-testing.html
 * https://developer.android.com/training/testing/start/index.html#run-instrumented-tests
 */
public class StorageProvider_AndroidTest extends ProviderTestCase2<StorageProvider> {

    // The class reference of what ContentProvider we are testing
    static final private Class<StorageProvider> providerClassName =
            StorageProvider.class;

    // The Provider's Authority
    static final private String authority = StorageProvider.AUTHORITY;
    // URI for Item(s)
    private static final Uri mEntryUri = User.Entry.CONTENT_URI;

    // ContentProvider to test
    private StorageProvider mFeedContentProvider;
    // Mock ContentResolver
    private ContentResolver mMockResolver;

    // Default values for test User(s)
    static final public String TEST_ID = "id";
    static final public String TEST_USER_ID = "userid";
    static final public String TEST_USERNAME = "username";
    static final public String TEST_TOKEN = "token";

    // Entry with default values.
    static private final User testA = newA();
    static private final User testB = newB();

    // method to create a new Entry with default Values
    static private User newA() {
        return new User(TEST_ID, TEST_USERNAME, TEST_USER_ID, TEST_TOKEN);
    }

    static private User newB() {
        return new User(TEST_ID, TEST_USERNAME, TEST_USER_ID, TEST_TOKEN);
    }

    /**
     * Constructor.
     */
    public StorageProvider_AndroidTest() {
        super(providerClassName, authority);
    }

    /**
     * Constructor.
     *
     * @param providerClass     The class name of the provider under test
     * @param providerAuthority The provider's authority string
     */
    @SuppressWarnings("UnusedParameters")
    public StorageProvider_AndroidTest(Class<StorageProvider> providerClass, String providerAuthority) {
        super(providerClassName, authority);
    }

    /**
     * Setup that will run before every test method
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setContext(InstrumentationRegistry.getTargetContext());

        // get Mock Context and Mock ContentResolver.
        Context mMockContext = getMockContext();
        mMockResolver = mMockContext.getContentResolver();

        // assign the authority for the provider we will create next.
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = StorageProvider.AUTHORITY;
        // create provider
        mFeedContentProvider = new StorageProvider();
        // attach info to the provider
        mFeedContentProvider.attachInfo(mMockContext, providerInfo);

        // verify that mRssContentProvider exists.
        assertNotNull(mFeedContentProvider);

        // add our provider to the list of providers in the mock ContentResolver
        ((MockContentResolver) mMockResolver).addProvider(StorageProvider.AUTHORITY, mFeedContentProvider);
    }

    /**
     * Method that will run after every test
     */
    @Override
    public void tearDown() {
        mFeedContentProvider.shutdown();
    }

    /**
     * Get the MockContentResolver object, can be altered in future if need be.
     *
     * @return the MockContentResolver object
     */
    @Override
    @SuppressWarnings("EmptyMethod")
    public MockContentResolver getMockContentResolver() {
        return super.getMockContentResolver();
    }

    /**
     * Test Insert of single Entry into ContentProvider.
     */
    public void testInsertEntry() {
        // get ContentValues from mItem
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        ContentValues values = formatter.getAsContentValues(testA);
        // use mock ContentResolver to insert mItem's ContentValues
        Uri resultingUri = mMockResolver.insert(mEntryUri, values);
        // Then you can test the correct execution of your insert:
        assertNotNull(resultingUri);
        long id = ContentUris.parseId(resultingUri);
        assertTrue(id > 0);

        String[] ALL_COLUMN_NAMES = {
                "_" + User.Entry.Cols.ID,
                User.Entry.Cols.USERNAME,
                User.Entry.Cols.PASSWORD
        };
        Cursor cursor = mMockResolver.query(mEntryUri,
                ALL_COLUMN_NAMES,
                null,
                null,
                null
        );

        CursorParser parser = new CursorParser();
        List<User> entries = parser.parseUsers(cursor);
        assertTrue(entries.get(0).equals(testA));


    }

    /**
     * Very basic query test.
     * <p/>
     * Prerequisites:
     * <ul>
     * <li>A provider set up by the test framework
     * </ul>
     * <p/>
     * Expectations:
     * <ul>
     * <li> a simple query without any parameters, before any inserts returns a
     * non-null cursor
     * <li> a wrong uri results in {@link IllegalArgumentException}
     * </ul>
     */
    public void testQueryEmpty() {
        // query the mock ContentResolver on the Item URI, return all rows.
        Cursor cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        // verify results
        assertNotNull(cursor);
        // results should be 0 because setUp doesn't insert anything into the provider to start
        assertThat(cursor.getCount(), is(0));
    }

    /**
     * Very basic query test.
     * <p/>
     * Prerequisites:
     * <ul>
     * <li>A provider set up by the test framework
     * </ul>
     * <p/>
     * Expectations:
     * <ul>
     * <li> a simple query without any parameters, after one insert returns a
     * non-null cursor with size 1 that returns an equal Entry compared to what was inserted.
     * </ul>
     */
    public void testQueryNonEmpty() {
        // get ContentValues and insert it into Provider via ContentResolver
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        ContentValues values = formatter.getAsContentValues(testA);
        Uri resultUri = mMockResolver.insert(mEntryUri, values);


        /**
         * Query entire table.
         */
        // query the mock ContentResolver on the Item URI, return all rows.
        Cursor cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        // verify cursor non-null
        assertNotNull(cursor);
        // verify results size = 1
        assertThat(cursor.getCount(), is(1));
        // get List<Entry> from the cursor.
        CursorParser parser = new CursorParser();
        List<User> entries = parser.parseUsers(cursor);
        // make sure 1 and only 1 entry was generated from the cursor
        assertThat(entries.size(), is(1));
        // get the inserted Entry
        User entry = entries.get(0);
        // check that returned Entry is non-null
        assertNotNull(entry);
        // verify that the returned Entry matches (other than _ID) the inserted Entry.
        assertTrue(entry.equals(testA));


        /**
         * Check if query with uri with ID works
         */
        cursor = mMockResolver.query(resultUri, null, null, null, null);
        // verify cursor non-null
        assertNotNull(cursor);
        // verify results size = 1
        assertThat(cursor.getCount(), is(1));
        // get List<Entry> from the cursor.
        entries = parser.parseUsers(cursor);
        // make sure 1 and only 1 entry was generated from the cursor
        assertThat(entries.size(), is(1));
        // get the inserted Entry
        entry = entries.get(0);
        // check that returned Entry is non-null
        assertNotNull(entry);
        // verify that the returned Entry matches (other than _ID) the inserted Entry.
        assertTrue(entry.equals(testA));
    }


    /**
     * Test of Provider ignores junk URIs
     * <p/>
     * Prerequisites:
     * <ul>
     * <li>A provider set up by the test framework
     * </ul>
     * <p/>
     * Expectations:
     * <ul>
     * <li> a simple query with a junk uri parameter
     * <li> a wrong uri results in {@link IllegalArgumentException}
     * </ul>
     */
    public void testJunkUris() {
        // verify that that query doesn't return on junk URIs.
        Cursor cursor = null;
        try {
            cursor = mMockResolver.query(
                    StorageProvider.BASE_URI.buildUpon().appendPath("definitely_invalid").build(),
                    null, null, null, null);
            assertNotNull(cursor);
            // Should have thrown IllegalArgumentException, so now fail.
            fail("cursor.getCount: " + cursor.getCount());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @SuppressWarnings("UnusedAssignment") // Lint is confused w/factory methods.
    public void testDelete() {
        // get ContentValues and insert it into Provider via ContentResolver
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        ContentValues values = formatter.getAsContentValues(testA);
        Uri insertedUri;

        // System.out.println("uriresult = " + uriResult);
        // query the mock ContentResolver on the Item URI, return all rows.
        int rowsDeleted = mMockResolver.delete(mEntryUri, null, null);

        /**
         * Test delete all rows when only 1 row, with table uri
         */
        insertedUri = mMockResolver.insert(mEntryUri, values);
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        assertThat(rowsDeleted, is(1));
        Cursor cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(0));


        /**
         * Test delete 1 row when only 1 row, with direct Uri.
         */
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        insertedUri = mMockResolver.insert(mEntryUri, values);
        rowsDeleted = mMockResolver.delete(insertedUri, null, null);
        assertThat(rowsDeleted, is(1));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(0));

        /**
         * Test delete all rows when only 1 row, with table uri
         */
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        insertedUri = mMockResolver.insert(mEntryUri, values);
        insertedUri = mMockResolver.insert(mEntryUri, values);
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        assertThat(rowsDeleted, is(2));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(0));


        /**
         * Test delete all rows when only 1 row, with table uri
         */
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);

        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newA()));
        rowsDeleted = mMockResolver.delete(insertedUri, null, null);
        assertThat(rowsDeleted, is(1));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(1));
        CursorParser parser = new CursorParser();
        List<User> entries = parser.parseUsers(cursor);
        assertTrue(entries.get(0).equals(testB));


        /**
         * test delete single row when multiple rows, via uri w/number
         */
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        long firstID = ContentUris.parseId(insertedUri);
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        Uri insertedUriGoal = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newA()));
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        rowsDeleted = mMockResolver.delete(insertedUriGoal, null, null);
        assertThat(rowsDeleted, is(1));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(4));
        entries = parser.parseUsers(cursor);
        assertThat(entries.get(0).getID(), is(Long.toString(firstID)));
        assertThat(entries.get(1).getID(), is(Long.toString(firstID + 1)));
        assertThat(entries.get(2).getID(), is(Long.toString(firstID + 3)));
        assertThat(entries.get(3).getID(), is(Long.toString(firstID + 4)));


        /**
         * delete single row with where clause of _ID from many
         */
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        firstID = ContentUris.parseId(insertedUri);
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        insertedUriGoal = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newA()));
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        insertedUri = mMockResolver.insert(mEntryUri, formatter.getAsContentValues(newB()));
        rowsDeleted = mMockResolver.delete(mEntryUri, " _ID = ?", new String[]{"" + (firstID + 2)});
        assertThat(rowsDeleted, is(1));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        assertThat(cursor.getCount(), is(4));
        entries = parser.parseUsers(cursor);
        assertTrue(entries.get(0).equals(testB));
        assertThat(entries.get(0).getID(), is(Long.toString(firstID)));
        assertThat(entries.get(1).getID(), is(Long.toString(firstID + 1)));
        assertThat(entries.get(2).getID(), is(Long.toString(firstID + 3)));
        assertThat(entries.get(3).getID(), is(Long.toString(firstID + 4)));
    }


    @Test
    public void testUpdate() {
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        ContentValues values = formatter.getAsContentValues(testA);
        Uri insertedUri;
        Cursor cursor;
        int rowsDeleted;
        List<User> entries;
        int updatedCount;

        // System.out.println("uriresult = " + uriResult);
        // query the mock ContentResolver on the Item URI, return all rows.
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);

        // setup
        mMockResolver.insert(mEntryUri, values);
        mMockResolver.insert(mEntryUri, values);
        insertedUri = mMockResolver.insert(mEntryUri, values);
        cursor = mMockResolver.query(insertedUri, null, null, null, null);
        CursorParser parser = new CursorParser();
        entries = parser.parseUsers(cursor);
        assertTrue(entries.get(0).equals(testA));
        assertFalse(entries.get(0).equals(testB));

        // test update 1 row with uri row ID
//        mMockResolver.insert(mEntryUri, values);
//        mMockResolver.insert(mEntryUri, values);
//        updatedCount = mMockResolver.update(insertedUri, testB.getContentValues(), null, null);
//        assertThat(updatedCount, is(1));
//        cursor = mMockResolver.query(insertedUri, null, null, null, null);
//        entries = Entry.CONVERTER.getFromCursor(cursor);
//        assertFalse(entries.get(0).equals(testA));
//        assertTrue(entries.get(0).equals(testB));

        // test update every row.
        rowsDeleted = mMockResolver.delete(mEntryUri, null, null);
        mMockResolver.insert(mEntryUri, formatter.getAsContentValues(testA));
        mMockResolver.insert(mEntryUri, formatter.getAsContentValues(testA));
        mMockResolver.insert(mEntryUri, formatter.getAsContentValues(testA));
        mMockResolver.insert(mEntryUri, formatter.getAsContentValues(testA));
        mMockResolver.insert(mEntryUri, formatter.getAsContentValues(testA));

        // update every row to B
        updatedCount = mMockResolver.update(mEntryUri, formatter.getAsContentValues(testB), null, null);
        assertThat(updatedCount, is(5));
        cursor = mMockResolver.query(mEntryUri, null, null, null, null);
        entries = parser.parseUsers(cursor);
        // check they all equal B
        for (int i = 0; i < 5; i++) {
            assertTrue(entries.get(i).equals(testB));
        }


    }
}

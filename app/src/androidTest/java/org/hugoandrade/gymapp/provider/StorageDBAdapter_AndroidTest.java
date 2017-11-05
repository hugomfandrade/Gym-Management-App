package org.hugoandrade.gymapp.provider;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.provider.parser.ContentValuesFormatter;
import org.hugoandrade.gymapp.provider.parser.CursorParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class StorageDBAdapter_AndroidTest {

    private final User mEntryA = newA();

    static final public String TEST_ID = "id";
    static final public String TEST_USER_ID = "userid";
    static final public String TEST_USERNAME = "username";
    static final public String TEST_TOKEN = "token";

    private StorageDatabaseHelper mStorageDBAdapter;

    // method to create a new Entry with default Values
    static private User newA() {
        return new User(TEST_ID, TEST_USERNAME, TEST_USER_ID, TEST_TOKEN);
    }

    static public void checkUser(User testUser, String id) {
        assertThat(testUser.getID(), Matchers.is(id));
        assertThat(testUser.getUsername(), Matchers.is(TEST_USERNAME));
        assertThat(testUser.getUserID(), Matchers.is(TEST_USER_ID));
        assertThat(testUser.getToken(), Matchers.is(TEST_TOKEN));
    }


    @Before
    public void setUp() {

        // how to delete database at start. (seems to delete db right now either way...)
        //getTargetContext().deleteDatabase(RssSchema.DATABASE_NAME);

        mStorageDBAdapter = new StorageDatabaseHelper(InstrumentationRegistry.getTargetContext());
        mStorageDBAdapter.getWritableDatabase();

    }

    @After
    public void tearDown() {
        mStorageDBAdapter.close();/**/
    }

    @Test
    public void simpleQueryTest() {

        assertNotNull(mStorageDBAdapter);

        Cursor cursor = mStorageDBAdapter.getWritableDatabase().query(User.Entry.TABLE_NAME, null, null, null, null,
                null, null);

        assertNotNull(cursor);

        assertThat(cursor.getCount(), is(0));
    }

    @Test
    public void simpleInsertTest() {
        // just sanity check
        assertNotNull(mStorageDBAdapter);
        // set start point
        Cursor cursor = mStorageDBAdapter.getWritableDatabase().query(User.Entry.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor);
        int previousCount = cursor.getCount();
        //assertThat(cursor.getCount(), is(0));
        // insert mEntryA
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        mStorageDBAdapter.getWritableDatabase().insert(User.Entry.TABLE_NAME, null, formatter.getAsContentValues(mEntryA));
        Cursor cursor2 = mStorageDBAdapter.getWritableDatabase().query(User.Entry.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor2);
        assertThat(cursor2.getCount(), is(previousCount + 1));
    }

    @Test
    public void inDepthInsertTest() {
        ContentValuesFormatter formatter = new ContentValuesFormatter();
        CursorParser parser = new CursorParser();
        // just sanity check
        assertNotNull(mStorageDBAdapter);
        // set start point
        Cursor cursor = mStorageDBAdapter.getWritableDatabase().query(User.Entry.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor);
        int previousCount = cursor.getCount();
        //assertThat(cursor.getCount(), is(0));
        // insert mEntryA
        mStorageDBAdapter.getWritableDatabase().insert(User.Entry.TABLE_NAME, null, formatter.getAsContentValues(mEntryA));
        Cursor cursor2 = mStorageDBAdapter.getWritableDatabase().query(User.Entry.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor2);
        assertThat(cursor2.getCount(), is(previousCount + 1));

        List<User> testItems = parser.parseUsers(cursor2);

        assertNotNull(testItems);

        assertThat(testItems.size(), is(1));

        User newItem = testItems.get(0);

        assertTrue(newItem.getUsername().equals(mEntryA.getUsername()));

    }
}

package com.codekidlabs.storagechooserdemo;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;

import com.codekidlabs.storagechooser.models.Storages;

/**
 * Created by codekid on 28/07/17.
 */
@RunWith(AndroidJUnit4.class)
public class StorageChooserTest {

    private static final int CHECKBOX_DIR = 0;
    private static final int CHECKBOX_FILE = 1;
    private static final int CHECKBOX_MEMBAR = 2;

    @Rule
    public ActivityTestRule<MainActivity> mMainRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testFabClick() {
        // click on main fab
        withIdClick(R.id.fab);
        // test if storage chooser dialog header text is visible
        // IF NOT then storage chooser dialog is not functioning properly without
        // any config
        onView(withText("Choose Drive"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testConfigDirectoryChooser() {
        // before clicking thy holy fab
        // make sure directory checkbox is checked
        preliminaryAction(CHECKBOX_DIR);
        // Oh yes click that fab
        withIdClick(R.id.fab);
        // now dialog has been displayed
        // make sure root is dialog
        // Go to > internal storage
        checkSecondaryChooser();

    }

    @Test
    public void testConfigFilePicker() {
        // before clicking thy holy fab
        // make sure directory checkbox is checked
        preliminaryAction(CHECKBOX_FILE);
        // Oh yes click that fab
        withIdClick(R.id.fab);
        // now dialog has been displayed
        // make sure root is dialog
        // Go to > internal storage
        checkSecondaryChooser();

    }

    @Test
    public void testMemoryBar() {
        //start with preliminary action check memorybar box
        preliminaryAction(CHECKBOX_MEMBAR);
        // I like clicking FAB ..huihui
        withIdClick(R.id.fab);
        // is memory bar displayed ?
        withViewIsDisplayed(R.id.memory_bar);

    }

    /*
     * ======================================
      *  CONVINIENT METHODS
      *  ====================================
     *
     * */

    /**
     *  Called when certain checkbox wants to be checked in MainActivity
     * */
    public void preliminaryAction(int whichBox) {
        switch (whichBox) {
            // directory chooser box
            case CHECKBOX_DIR:
                //along with that allowCustomPath also
                withIdClick(R.id.checkbox_dir);
                break;
            case CHECKBOX_FILE:
                withIdClick(R.id.checkbox_file);
                break;
            case CHECKBOX_MEMBAR:
                withIdClick(R.id.checkbox_membar);
                break;

        }
    }

    /**
     * check for view with id is displayed or not
     * @param viewId is id of view
     * */
    private void withViewIsDisplayed(int viewId) {
        onView(withId(viewId))
                .check(matches(isDisplayed()));
    }

    private boolean isIdle() {
        return true;
    }

    /**
     * perform a click on view
     * @param id is id of view
     * */
    private void withIdClick(int id) {
        onView(withId(id))
                .perform(click());
    }

    /**
     * check if secondary chooser is displayed on click storage
     * */
    private void checkSecondaryChooser() {
        onData(instanceOf(Storages.class))
                .inAdapterView(withId(R.id.storage_list_view))
                .atPosition(0)
                .perform(click());
        if(isIdle()) {
            // check for addressbar text
            onView(withId(R.id.path_chosen))
                    .inRoot(isDialog())
                    .check(matches(withText(startsWith("/storage/"))));
        }
    }
}

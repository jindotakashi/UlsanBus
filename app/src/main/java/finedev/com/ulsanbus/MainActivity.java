package finedev.com.ulsanbus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

import finedev.com.ulsanbus.bus.BusDetailFragment;
import finedev.com.ulsanbus.bus.BusRootFragment;
import finedev.com.ulsanbus.bus.FindBusFragment;
import finedev.com.ulsanbus.favorite.FavoriteFragment;
import finedev.com.ulsanbus.more.MoreFragment;
import finedev.com.ulsanbus.network.NetworkHelper;
import finedev.com.ulsanbus.station.FindStationFragment;


public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener,
        FavoriteFragment.OnFragmentInteractionListener,
        FindBusFragment.OnBusItemSelectedListener,
        MoreFragment.OnFragmentInteractionListener,
        BusDetailFragment.OnBusDetailItemSelectedListener {

    private final Activity mThis = this;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private HashMap<String, Stack<Fragment>> mStacks;
    private int mCurrentTabPosition = 0;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation stacks for each tab
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put( getString(R.string.favorite), new Stack<Fragment>() );
        mStacks.put( getString(R.string.find_bus), new Stack<Fragment>() );
        mStacks.put( getString(R.string.find_station), new Stack<Fragment>() );
        mStacks.put( getString(R.string.more), new Stack<Fragment>() );

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setTag(mSectionsPagerAdapter.getPageTag(i))
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        updateBusAndStationDatabase();
    }

    private void updateBusAndStationDatabase() {

        final SharedPreferences pref = getSharedPreferences(AppConstant.DEFAULT_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        final String localDbVersionCode = pref.getString("db_version_code", null );

        NetworkHelper.getDatabaseInfo(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final String serverDbVersionCode = response.getString("db_version_code");
                    final String serverDbFileName = response.getString("db_name");
                    final String serverDbUrl = response.getString("db_url");

                    if (localDbVersionCode == null || !localDbVersionCode.equals(serverDbVersionCode)) {
                        NetworkHelper.downloadFile(serverDbUrl, new FileAsyncHttpResponseHandler(mThis) {

                            private ProgressDialog progressDialog;

                            @Override
                            public void onStart() {
                                super.onStart();
                                progressDialog = ProgressDialog.show(mThis, "DB Download", "새로운 버스정보를 가져오고있습니다. 잠시만 기다려주세요~", true);
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                Log.e(LOG_TAG, "database file download error! url is [" + serverDbUrl + "]");
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, File file) {
                                FileOutputStream fos = null;
                                try {
                                    File dbFile = new File(AppConstant.DB_BASE_URL + AppConstant.DB_FILE_NAME);
                                    if (!dbFile.exists()) {
                                        dbFile.getParentFile().mkdirs();
                                        dbFile.createNewFile();
                                    }
                                    fos = new FileOutputStream(dbFile);
                                    fos.write(FileUtils.readFileToByteArray(file));

                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("db_version_code", serverDbVersionCode);
                                    editor.putString("db_name", serverDbFileName);
                                    editor.putString("db_url", serverDbUrl);
                                    editor.commit();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (fos != null) try {
                                        fos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFinish() {
                                progressDialog.dismiss();
                                super.onFinish();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        hideSoftKeyboard();
        Log.d(LOG_TAG, "mCurrentTabPosition : " + mCurrentTabPosition);
        mCurrentTabPosition = tab.getPosition();
        mViewPager.setCurrentItem(mCurrentTabPosition);
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if ( view != null ) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onBusItemSelected(int busInfoId) {

        // Create fragment and give it an argument for the selected article
        BusDetailFragment newFragment = new BusDetailFragment();
        Bundle args = new Bundle();
//        args.put
//        args.putInt(BusDetailFragment.ARG_POSITION, position);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        mStacks.get(mSectionsPagerAdapter.getPageTag(mCurrentTabPosition)).push(newFragment);
        transaction.replace(getCurrentTabRootFrameId(), newFragment);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        Stack<Fragment> currentTabStack = mStacks.get(mSectionsPagerAdapter.getPageTag(mCurrentTabPosition));
        if ( currentTabStack.size() == 1 ) {
            finish();
            return;
        }

        currentTabStack.pop();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(getCurrentTabRootFrameId(), currentTabStack.lastElement());
        transaction.commit();


    }

    public int getCurrentTabRootFrameId() {
        if ( mSectionsPagerAdapter.getPageTag(mCurrentTabPosition) == getString(R.string.find_bus)) {
            return R.id.bus_root_frame;
        }
        return R.id.bus_root_frame;
    }

    public Stack<Fragment> getTabStack(String stackId) {
        return mStacks.get(getString(R.string.find_bus));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Stack<Fragment> currentTabStack = mStacks.get(getPageTag(position));

            Fragment fragment = null;

            if ( currentTabStack.size() == 0 ) {
                if ( getPageTitle(position).equals( getString(R.string.favorite) ) ) {
                    fragment = FavoriteFragment.newInstance(null, null);
                } else if ( getPageTitle(position).equals( getString(R.string.find_bus) ) ) {
                    fragment = BusRootFragment.newInstance(currentTabStack);
                } else if ( getPageTitle(position).equals( getString(R.string.find_station) ) ) {
                    fragment = FindStationFragment.newInstance(null, null);
                } else if ( getPageTitle(position).equals( getString(R.string.more) ) ) {
                    fragment = MoreFragment.newInstance(null, null);
                }
                currentTabStack.push(fragment);
            }
            return currentTabStack.lastElement();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.favorite);
                case 1:
                    return getString(R.string.find_bus);
                case 2:
                    return getString(R.string.find_station);
                case 3:
                    return getString(R.string.more);
            }
            return null;
        }

        public String getPageTag(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.favorite);
                case 1:
                    return getString(R.string.find_bus);
                case 2:
                    return getString(R.string.find_station);
                case 3:
                    return getString(R.string.more);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}

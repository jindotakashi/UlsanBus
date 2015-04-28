package finedev.com.ulsanbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

import java.util.HashMap;
import java.util.Stack;

import finedev.com.ulsanbus.bus.BusDetailFragment;
import finedev.com.ulsanbus.bus.FindBusFragment;
import finedev.com.ulsanbus.favorite.FavoriteFragment;
import finedev.com.ulsanbus.more.MoreFragment;
import finedev.com.ulsanbus.station.FindStationFragment;
import finedev.com.ulsanbus.station.StationDetailFragment;

public class AppMainTabActivity extends FragmentActivity implements
        FindBusFragment.OnBusItemSelectedListener,
        FindStationFragment.OnStationItemSelectedListener {
    /* Your Tab host */
    private TabHost mTabHost;

    /* A HashMap of stacks, where we use tab identifier as keys..*/
    private HashMap<String, Stack<Fragment>> mStacks;

    /*Save current tabs identifier in this..*/
    private String mCurrentTab;

    private int[] tabResourceId = { R.string.favorite, R.string.find_bus, R.string.find_station, R.string.more };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_tab_fragment_layout);

        /*
         *  Navigation stacks for each tab gets created..
         *  tab identifier is used as key to get respective stack for each tab
         */
        mStacks             =   new HashMap<String, Stack<Fragment>>();
        for( int resourceId : tabResourceId ) {
            mStacks.put( getString(resourceId), new Stack<Fragment>() );
        }

        mTabHost                =   (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();

        initializeTabs();
    }


//    private View createTabView(final int id) {
//        View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
//        ImageView imageView =   (ImageView) view.findViewById(R.id.tab_icon);
//        imageView.setImageDrawable(getResources().getDrawable(id));
//        return view;
//    }

    public void initializeTabs(){
//        /* Setup your tab icons and content views.. Nothing special in this..*/
//        TabHost.TabSpec spec    =   mTabHost.newTabSpec(getString(R.string.favorite));
//        mTabHost.setCurrentTab(-3);
//        spec.setContent(new TabHost.TabContentFactory() {
//            public View createTabContent(String tag) {
//                return findViewById(R.id.realtabcontent);
//            }
//        });
////        spec.setIndicator(createTabView(R.drawable.tab_home_state_btn));
//        mTabHost.addTab(spec);
//
//
//        spec                    =   mTabHost.newTabSpec(getString(R.string.find_bus));
//        spec.setContent(new TabHost.TabContentFactory() {
//            public View createTabContent(String tag) {
//                return findViewById(R.id.realtabcontent);
//            }
//        });
////        spec.setIndicator(createTabView(R.drawable.tab_status_state_btn));
//        mTabHost.addTab(spec);


        for( int resourceId : tabResourceId ) {
            TabHost.TabSpec spec = mTabHost.newTabSpec(getString(resourceId));
            spec.setContent(new TabHost.TabContentFactory() {
                public View createTabContent(String tag) {
                    return findViewById(R.id.realtabcontent);
                }
            });
            spec.setIndicator(getString(resourceId));
            mTabHost.addTab(spec);
        }

    }


    /*Comes here when user switch tab, or we do programmatically*/
    TabHost.OnTabChangeListener listener    =   new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {
        /*Set current tab..*/
            mCurrentTab                     =   tabId;

            if(mStacks.get(tabId).size() == 0){
          /*
           *    First time this tab is selected. So add first fragment of that tab.
           *    Dont need animation, so that argument is false.
           *    We are adding a new fragment which is not present in stack. So add to stack is true.
           */
                if (tabId.equals(getString(R.string.favorite))) {
                    pushFragments(tabId, new FavoriteFragment(), false, true);
                } else if (tabId.equals(getString(R.string.find_bus))) {
                    pushFragments(tabId, new FindBusFragment(), false, true);
                } else if (tabId.equals(getString(R.string.find_station))) {
                    pushFragments(tabId, new FindStationFragment(), false, true);
                } else if (tabId.equals(getString(R.string.more))) {
                    pushFragments(tabId, new MoreFragment(), false, true);
                }
            }else {
          /*
           *    We are switching tabs, and target tab is already has atleast one fragment.
           *    No need of animation, no need of stack pushing. Just show the target fragment
           */
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false,false);
            }
        }
    };


    /* Might be useful if we want to switch tab programmatically, from inside any of the fragment.*/
    public void setCurrentTab(int val){
        mTabHost.setCurrentTab(val);
    }


    /*
     *      To add fragment to a tab.
     *  tag             ->  Tab identifier
     *  fragment        ->  Fragment to show, in tab identified by tag
     *  shouldAnimate   ->  should animate transaction. false when we switch tabs, or adding first fragment to a tab
     *                      true when when we are pushing more fragment into navigation stack.
     *  shouldAdd       ->  Should add to fragment navigation stack (mStacks.get(tag)). false when we are switching tabs (except for the first time)
     *                      true in all other cases.
     */
    public void pushFragments(String tag, Fragment fragment,boolean shouldAnimate, boolean shouldAdd){
        if(shouldAdd)
            mStacks.get(tag).push(fragment);
        FragmentManager manager         =   getSupportFragmentManager();
        FragmentTransaction ft            =   manager.beginTransaction();
//        if(shouldAnimate)
//            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    public void popFragments(){
      /*
       *    Select the second last fragment in current tab's stack..
       *    which will be shown after the fragment transaction given below
       */
        Fragment fragment             =   mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

      /*pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();

      /* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager   manager         =   getSupportFragmentManager();
        FragmentTransaction ft            =   manager.beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        if(mStacks.get(mCurrentTab).size() == 1){
            // We are already showing first fragment of current tab, so when back pressed, we will finish this activity..
            finish();
            return;
        }

        /*  Each fragment represent a screen in application (at least in my requirement, just like an activity used to represent a screen). So if I want to do any particular action
         *  when back button is pressed, I can do that inside the fragment itself. For this I used AppBaseFragment, so that each fragment can override onBackPressed() or onActivityResult()
         *  kind of events, and activity can pass it to them. Make sure just do your non navigation (popping) logic in fragment, since popping of fragment is done here itself.
         */
//        ((AppBaseFragment)mStacks.get(mCurrentTab).lastElement()).onBackPressed();

        /* Goto previous fragment in navigation stack of this tab */
        popFragments();
    }


    /*
     *   Imagine if you wanted to get an image selected using ImagePicker intent to the fragment. Ofcourse I could have created a public function
     *  in that fragment, and called it from the activity. But couldn't resist myself.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mStacks.get(mCurrentTab).size() == 0){
            return;
        }

        /*Now current fragment on screen gets onActivityResult callback..*/
        mStacks.get(mCurrentTab).lastElement().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBusItemSelected(int busInfoId) {
        pushFragments(mCurrentTab, BusDetailFragment.newInstance(busInfoId), false, true);
    }

    @Override
    public void onStationItemClicked(int stationInfoId) {
        pushFragments(mCurrentTab, StationDetailFragment.newInstance(stationInfoId), false, true);
    }
}
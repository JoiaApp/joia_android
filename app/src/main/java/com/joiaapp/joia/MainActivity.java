package com.joiaapp.joia;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joiaapp.joia.alarm.AlarmScheduler;
import com.joiaapp.joia.group.GroupFragment;
import com.joiaapp.joia.read.ReadFragment;
import com.joiaapp.joia.service.ServiceFactory;
import com.joiaapp.joia.settings.SettingsFragment;
import com.joiaapp.joia.write.WriteFragment;

import static com.joiaapp.joia.InitialActivity.INITIAL_ACTIVITY_REQUEST_CODE;

/**
 * Created by arnell on 11/4/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

//TODO: update all the styling
//TODO: finish styling the mentions on the write form
//TODO: implement settings page
//TODO: clear views when signing out

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private WriteFragment writeFragment;
    private ReadFragment readFragment;
    private GroupFragment groupFragment;
    private SettingsFragment settingsFragment;

    private ImageButton btnNavBackButton;
    private Button btnNavNextButton;
    private TextView tvNavTitle;


    private static final int WRITE_PAGE = 0;
    private static final int READ_PAGE = 1;
    private static final int GROUP_PAGE = 2;
    private static final int SETTINGS_PAGE = 3;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServiceFactory.init(this);

        if (ServiceFactory.getUserService().getCurrentUser() == null) {
            startSignInProcess();
        }

        btnNavBackButton = (ImageButton) findViewById(R.id.btnNavBackButton);
        btnNavBackButton.setOnClickListener(this);
        btnNavNextButton = (Button) findViewById(R.id.btnNavNextButton);
        btnNavNextButton.setOnClickListener(this);
        tvNavTitle = (TextView) findViewById(R.id.tvNavTitle);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(WRITE_PAGE).setIcon(R.drawable.ic_pencil);
        tabLayout.getTabAt(READ_PAGE).setIcon(R.drawable.ic_journal);
        tabLayout.getTabAt(GROUP_PAGE).setIcon(R.drawable.ic_group);
        tabLayout.getTabAt(SETTINGS_PAGE).setIcon(R.drawable.ic_settings);
        setTabIconColors(0);

        AlarmScheduler.setAlarm(this);
    }

    public void startSignInProcess() {
        Intent intent = new Intent(this, InitialActivity.class);
        startActivityForResult(intent, INITIAL_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INITIAL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
        if (requestCode == INITIAL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            showWriteView();
            AlarmScheduler.setAlarm(this);
        }
    }

    public void updateNavigationBar(Integer newPosition) {
        int position = newPosition != null ? newPosition : tabLayout.getSelectedTabPosition();
        MainAppFragment fragment = (MainAppFragment) mSectionsPagerAdapter.getItem(position);
        setNavigationBar(fragment.getNavBarTitle(), fragment.isNavBarBackButtonVisible(), fragment.isNavBarNextButtonVisible());
    }

    private void setNavigationBar(String title, boolean backButtonVisible, boolean nextButtonVisible) {
        tvNavTitle.setText(title);
        if (backButtonVisible) {
            btnNavBackButton.setVisibility(View.VISIBLE);
        } else {
            btnNavBackButton.setVisibility(View.INVISIBLE);
        }
        if (nextButtonVisible) {
            btnNavNextButton.setVisibility(View.VISIBLE);
        } else {
            btnNavNextButton.setVisibility(View.INVISIBLE);
        }
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //System.out.println("onPageScrolled: " + position + "," + positionOffset + "," + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        setTabIconColors(position);
        updateNavigationBar(position);
        switch (position) {
            case READ_PAGE:
                onReadPageSelected();
                break;
            case GROUP_PAGE:
                onGroupPageSelected();
                break;
        }
    }

    private void setTabIconColors(int activePosition) {
        int activeColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activeColor = getResources().getColor(R.color.colorPrimary, null);
        } else {
            activeColor = getResources().getColor(R.color.colorPrimary);
        }
        tabLayout.getTabAt(activePosition).getIcon().setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            if (i != activePosition) {
                tabLayout.getTabAt(i).getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void onReadPageSelected() {
        readFragment.refreshView();
        writeFragment.onReadPageSelected();
    }

    private void onGroupPageSelected() {
        groupFragment.refreshView();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //System.out.println("onPageScrollStateChanged: " + state);
    }

    @Override
    public void onClick(View v) {
        switch (tabLayout.getSelectedTabPosition()) {
            case WRITE_PAGE:
                writeFragment.onClick(v);
                break;
            case READ_PAGE:
                readFragment.onClick(v);
                break;
        }
    }

    public void showJournalView() {
        tabLayout.getTabAt(READ_PAGE).select();
    }

    public void showWriteView() {
        tabLayout.getTabAt(WRITE_PAGE).select();
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            switch (position) {
                case WRITE_PAGE:
                    return writeFragment == null ? writeFragment = new WriteFragment() : writeFragment;
                case READ_PAGE:
                    return readFragment == null ? readFragment = new ReadFragment() : readFragment;
                case GROUP_PAGE:
                    return groupFragment == null ? groupFragment = new GroupFragment() : groupFragment;
                case SETTINGS_PAGE:
                    return settingsFragment == null ? settingsFragment = new SettingsFragment() : settingsFragment;
                default:
                    throw new RuntimeException("Invalid fragment position");
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Write";
//                case 1:
//                    return "Read";
//                case 2:
//                    return "Groups";
//                case 3:
//                    return "Settings";
//            }
//            return null;
//        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            // Generate title based on item position
//            // return tabTitles[position];
//            Drawable image = context.getResources().getDrawable(imageResId[position]);
//            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//            SpannableString sb = new SpannableString(" ");
//            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            return sb;
//        }
    }
}

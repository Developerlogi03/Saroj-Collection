package com.logimetrix.locationsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.logimetrix.locationsync.fragments.FragmentOne;
import com.logimetrix.locationsync.fragments.FragmentTwo;

public class LedgerActivity extends AppCompatActivity {
    String retailerId;

    private LedgerFragmentAdapter adapter;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);
        retailerId = getIntent().getStringExtra("routeId");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        createTabFragment();


    }

    private void createTabFragment(){
        adapter = new LedgerFragmentAdapter(getSupportFragmentManager(), tabLayout);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    public class LedgerFragmentAdapter extends FragmentStatePagerAdapter {
        TabLayout tabLayout;


        public LedgerFragmentAdapter(FragmentManager fm, TabLayout _tabLayout) {
            super(fm);
            this.tabLayout = _tabLayout;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0)
            {
                Bundle bundle = new Bundle();

                bundle.putString("retailerId",retailerId);
                fragment = new FragmentOne();
                fragment.setArguments(bundle);
            }
            else if (position == 1)
            {
                Bundle bundle = new Bundle();
                bundle.putString("retailerId",retailerId);
                fragment = new FragmentTwo();
                fragment.setArguments(bundle);

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
            {
                title = "Ledger";
            }
            else if (position == 1)
            {
                title = "Collection";
            }
            return title;
        }
    }

}
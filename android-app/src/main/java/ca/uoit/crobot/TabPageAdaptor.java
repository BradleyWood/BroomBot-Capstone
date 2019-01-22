package ca.uoit.crobot;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPageAdaptor extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> tabNames = new ArrayList<>();

    public TabPageAdaptor(final FragmentManager fm) {
        super(fm);
    }

    public void addTab(final String tabName, final Fragment fragment) {
        tabNames.add(tabName);
        fragments.add(fragment);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        return tabNames.get(i);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

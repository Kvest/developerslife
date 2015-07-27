package com.kvest.developerslife.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.kvest.developerslife.ui.fragment.PostsListFragment;
import com.kvest.developerslife.ui.fragment.RandomPostFragment;
import com.kvest.developerslife.utility.CategoryHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 02.01.14
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class DevlifePagesAdapter extends FragmentStatePagerAdapter {
    private static final int PAGES_COUNT = CategoryHelper.CATEGORIES_COUNT;

    private String[] categoryNames;
    private Fragment[] fragments = new Fragment[PAGES_COUNT];

    public DevlifePagesAdapter(FragmentManager fragmentManager, String[] categoryNames) {
        super(fragmentManager);
        this.categoryNames = categoryNames;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            switch (position) {
                case 0 :
                    fragments[position] = PostsListFragment.newInstance(CategoryHelper.LATEST_CATEGORY_ID);
                    break;
                case 1 :
                    fragments[position] = PostsListFragment.newInstance(CategoryHelper.TOP_CATEGORY_ID);
                    break;
                case 2 :
                    fragments[position] = PostsListFragment.newInstance(CategoryHelper.HOT_CATEGORY_ID);
                    break;
                case 3 :
                    fragments[position] = new RandomPostFragment();
                    break;
                default : throw new IllegalArgumentException("Can't find page " + position + " for DevlifePagesAdapter");
            }
        }

        return fragments[position];
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0 :
            case 1 :
            case 2 :
            case 3 :
                return categoryNames[position];
            default : throw new IllegalArgumentException("Can't find page " + position + " for DevlifePagesAdapter");
        }
    }

    public int getCategoryByPageNumber(int pageNumber) {
        switch (pageNumber) {
            case 0 : return CategoryHelper.LATEST_CATEGORY_ID;
            case 1 : return CategoryHelper.TOP_CATEGORY_ID;
            case 2 : return CategoryHelper.HOT_CATEGORY_ID;
            case 3 : return CategoryHelper.RANDOM_CATEGORY_ID;
            default : throw new IllegalArgumentException("Can't find page " + pageNumber + " for DevlifePagesAdapter");
        }
    }
}

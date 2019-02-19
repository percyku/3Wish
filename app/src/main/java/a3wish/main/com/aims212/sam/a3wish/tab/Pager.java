package a3wish.main.com.aims212.sam.a3wish.tab;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentPagerAdapter {

    private View mView;
    //integer to count number of tabs
    int tabCount;
    private ViewPager mContentVp;

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                AllAchieveFragment allAchieveFragment = new AllAchieveFragment();
                return allAchieveFragment;
            case 1:
                ImageAchieveFragment imageAchieveFragment = new ImageAchieveFragment();
                return imageAchieveFragment;
            case 2:
                VideoAchieveFragment videoAchieveFragment = new VideoAchieveFragment();
                return videoAchieveFragment;
            case 3:
                PanoramaAchieveFragment panoramaAchieveFragment = new PanoramaAchieveFragment();
                return panoramaAchieveFragment;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

}
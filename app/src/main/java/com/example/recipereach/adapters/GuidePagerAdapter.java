package com.example.recipereach.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.recipereach.fragments.guideFragments.FirstGuideFragment;
import com.example.recipereach.fragments.guideFragments.LikeFragment;
import com.example.recipereach.fragments.guideFragments.DislikeFragment;
import com.example.recipereach.fragments.guideFragments.OpenPalmFragment;
import com.example.recipereach.fragments.guideFragments.ZoomInFragment;
import com.example.recipereach.fragments.guideFragments.ZoomOutFragment;

/**
 * Adapter for managing the ViewPager2 in the guide section.
 * It handles fragment transitions for different guide steps.
 */
public class GuidePagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the GuidePagerAdapter.
     * @param fragmentActivity The activity that hosts the ViewPager2.
     */
    public GuidePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Returns the corresponding fragment for the given position.
     * @param position The index of the fragment.
     * @return The fragment corresponding to the position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FirstGuideFragment(); // Initial guide page
            case 1:
                return new LikeFragment(); // "Like" gesture guide
            case 2:
                return new DislikeFragment(); // "Dislike" gesture guide
            case 3:
                return new ZoomInFragment(); // "Zoom In" gesture guide
            case 4:
                return new ZoomOutFragment(); // "Zoom Out" gesture guide
            case 5:
                return new OpenPalmFragment(); // "Open Palm" gesture guide
            default:
                return new LikeFragment(); // Default fragment (shouldn't happen)
        }
    }

    /**
     * Returns the total number of guide pages.
     * @return Number of pages.
     */
    @Override
    public int getItemCount() {
        return 6; // Total number of guide steps
    }
}

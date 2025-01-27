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

public class GuidePagerAdapter extends FragmentStateAdapter {

    public GuidePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FirstGuideFragment();
            case 1:
                return new LikeFragment();
            case 2:
                return new DislikeFragment();
            case 3:
                return new ZoomInFragment();
            case 4:
                return new ZoomOutFragment();
            case 5:
                return new OpenPalmFragment();
            default:
                return new LikeFragment(); // ברירת מחדל
        }
    }

    @Override
    public int getItemCount() {
        return 6; // מספר הדפים
    }
}

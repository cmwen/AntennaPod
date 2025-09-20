package de.danoeh.antennapod.ui.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DiscoveryFragment extends Fragment {
    public static final String TAG = "DiscoveryFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_online_search, container, false);
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        ViewPager2 viewPager = root.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DiscoveryPagerAdapter(this));

        TabLayout tabLayout = root.findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(R.string.itunes_search_label);
                    } else {
                        tab.setText(R.string.podchaser_search_label);
                    }
                }
        ).attach();

        return root;
    }

    private static class DiscoveryPagerAdapter extends FragmentStateAdapter {
        public DiscoveryPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new ItunesSearchFragment();
            } else {
                return new PodchaserEpisodeSuggestFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

package de.danoeh.antennapod.ui.discovery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.preference.PreferenceManager;
import de.danoeh.antennapod.ui.discovery.R;
import de.danoeh.antennapod.net.podchaser.PodchaserApiClient;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PodchaserEpisodeSuggestFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PodchaserEpisodeAdapter adapter;
    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.podchaser_episode_suggest_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PodchaserEpisodeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        loadEpisodes();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void loadEpisodes() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        disposable = Observable
            .fromCallable(() -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String apiKey = prefs.getString("podchaser_api_key", "");
                String apiSecret = prefs.getString("podchaser_api_secret", "");
                String searchTerms = prefs.getString("podchaser_search_terms", "");

                PodchaserApiClient client = new PodchaserApiClient();
                String accessToken = client.requestAccessToken(apiKey, apiSecret);
                return client.searchEpisodes(accessToken, searchTerms);
            })
            .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            episodes -> {
                progressBar.setVisibility(View.GONE);
                if (episodes != null) {
                    adapter = new PodchaserEpisodeAdapter(episodes);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(getView(), "No episodes found", Snackbar.LENGTH_LONG).show();
                }
            },
            error -> {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getView(), "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        );
    }
}

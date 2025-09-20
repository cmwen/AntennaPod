package de.danoeh.antennapod.ui.discovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.danoeh.antennapod.ui.discovery.R;
import de.danoeh.antennapod.net.podchaser.model.Episode;

public class PodchaserEpisodeAdapter extends RecyclerView.Adapter<PodchaserEpisodeAdapter.ViewHolder> {

    private final List<Episode> episodes;

    public PodchaserEpisodeAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.podchaser_episode_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.titleTextView.setText(episode.title);
        holder.podcastTitleTextView.setText(episode.podcast.title);
        holder.descriptionTextView.setText(episode.description);

        Glide.with(holder.itemView.getContext())
                .load(episode.podcast.imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView podcastTitleTextView;
        TextView descriptionTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            podcastTitleTextView = itemView.findViewById(R.id.podcastTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}

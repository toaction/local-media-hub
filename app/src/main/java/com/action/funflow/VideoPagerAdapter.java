package com.action.funflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {

    private final Context context;
    private final List<VideoFile> videos;
    private final ArrayList<ExoPlayer> players;
    private int currentlyPlayingPosition = -1;

    public VideoPagerAdapter(Context context, List<VideoFile> videos) {
        this.context = context;
        this.videos = videos;
        this.players = new ArrayList<>();
        // Initialize players for all videos
        for (int i = 0; i < videos.size(); i++) {
            players.add(new ExoPlayer.Builder(context).build());
        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_player, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile video = videos.get(position);
        ExoPlayer player = players.get(position);

        holder.bind(video, player, position);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void playVideo(int position) {
        if (position < 0 || position >= players.size()) {
            return;
        }

        // Pause currently playing video
        if (currentlyPlayingPosition != -1 && currentlyPlayingPosition != position) {
            ExoPlayer currentPlayer = players.get(currentlyPlayingPosition);
            if (currentPlayer.getPlayWhenReady()) {
                currentPlayer.pause();
                currentPlayer.seekToDefaultPosition();
            }
        }

        // Play new video
        ExoPlayer newPlayer = players.get(position);
        if (!newPlayer.getPlayWhenReady()) {
            newPlayer.setPlayWhenReady(true);
        }
        currentlyPlayingPosition = position;
    }

    public void pauseCurrentVideo() {
        if (currentlyPlayingPosition != -1 && currentlyPlayingPosition < players.size()) {
            ExoPlayer currentPlayer = players.get(currentlyPlayingPosition);
            if (currentPlayer.getPlayWhenReady()) {
                currentPlayer.pause();
            }
        }
    }

    public void releasePlayers() {
        for (ExoPlayer player : players) {
            if (player != null) {
                player.release();
            }
        }
        players.clear();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private PlayerView playerView;
        private ImageButton playPauseButton;
        private View playTriangleBackground;
        private SeekBar progressSeekBar;
        private TextView currentPositionTextView;
        private TextView durationTextView;
        private TextView videoTitleTextView;
        private LinearLayout progressLayout;

        private ExoPlayer player;
        private VideoFile video;
        private int position;
        private final Runnable hideControlsRunnable;
        private boolean isUpdatingSeekBar = false;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            playPauseButton = itemView.findViewById(R.id.playPauseButton);
            playTriangleBackground = itemView.findViewById(R.id.playTriangleBackground);
            progressSeekBar = itemView.findViewById(R.id.progressSeekBar);
            currentPositionTextView = itemView.findViewById(R.id.currentPositionTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            videoTitleTextView = itemView.findViewById(R.id.videoTitleTextView);
            progressLayout = itemView.findViewById(R.id.progressLayout);

            hideControlsRunnable = () -> {
                // Progress bar is always visible now
            };

            setupClickListeners();
        }

        private void setupClickListeners() {
            // Click anywhere on video to toggle play/pause
            playerView.setOnClickListener(v -> {
                if (player != null) {
                    togglePlayPause();
                }
            });

            progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && player != null) {
                        long newPosition = (progress * player.getDuration()) / 100;
                        player.seekTo(newPosition);
                        currentPositionTextView.setText(formatDuration(newPosition));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isUpdatingSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isUpdatingSeekBar = false;
                }
            });
        }

        private void bind(VideoFile video, ExoPlayer player, int position) {
            this.video = video;
            this.player = player;
            this.position = position;

            // Set video info (remove file extension)
            String fileName = video.getFileName();
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileName = fileName.substring(0, lastDotIndex);
            }
            videoTitleTextView.setText(fileName);

            // Clear any previous listeners and callbacks
            playerView.removeCallbacks(hideControlsRunnable);

            // Attach player to view
            playerView.setPlayer(player);

            // Prepare player if not already prepared with this media
            MediaItem mediaItem = MediaItem.fromUri(video.getUri());
            if (player.getMediaItemCount() == 0 || !player.getCurrentMediaItem().localConfiguration.uri.toString().equals(video.getUri().toString())) {
                player.setMediaItem(mediaItem);
                player.prepare();
            }

            // Set duration
            durationTextView.setText(formatDuration(player.getDuration()));

            // Setup progress listener
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        durationTextView.setText(formatDuration(player.getDuration()));
                        progressSeekBar.setMax(100);
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        // Hide center play button when playing
                        playPauseButton.setVisibility(View.GONE);
                        playTriangleBackground.setVisibility(View.GONE);
                        startProgressUpdater();
                        // Progress bar is always visible
                    } else {
                        // Show center play button when paused
                        playPauseButton.setVisibility(View.VISIBLE);
                        playTriangleBackground.setVisibility(View.VISIBLE);
                        // Progress bar is always visible
                        playerView.removeCallbacks(hideControlsRunnable);
                    }
                }
            });
        }

        private void startProgressUpdater() {
            playerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (player != null && player.getPlayWhenReady() && !isUpdatingSeekBar) {
                        long position = player.getCurrentPosition();
                        long duration = player.getDuration();
                        if (duration > 0) {
                            int progress = (int) ((position * 100) / duration);
                            progressSeekBar.setProgress(progress);
                            currentPositionTextView.setText(formatDuration(position));
                        }
                        playerView.postDelayed(this, 1000);
                    }
                }
            }, 1000);
        }

        private void togglePlayPause() {
            if (player.getPlayWhenReady()) {
                // Currently playing, pause it
                player.pause();
                playPauseButton.setVisibility(View.VISIBLE);
                playTriangleBackground.setVisibility(View.VISIBLE);
                playerView.removeCallbacks(hideControlsRunnable);
            } else {
                // Currently paused, play it
                player.play();
                playPauseButton.setVisibility(View.GONE);
                playTriangleBackground.setVisibility(View.GONE);
            }
        }

        private String formatDuration(long duration) {
            long seconds = duration / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}

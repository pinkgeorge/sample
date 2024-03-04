package com.example.sample;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button[] buttons;
    private VideoView videoView;
    private LinearLayout buttonContainer;
    private TextView winsCounterTextView;
    private int correctButtonIndex;
    private int triesRemaining = 3;
    private int winsCounter = 0;
    private boolean gameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new Button[6];
        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);
        buttons[4] = findViewById(R.id.button5);
        buttons[5] = findViewById(R.id.button6);

        videoView = findViewById(R.id.videoView);
        buttonContainer = findViewById(R.id.buttonContainer);
        winsCounterTextView = findViewById(R.id.winsCounterTextView);

        initializeGame();
    }

    private void initializeGame() {
        for (int i = 0; i < buttons.length; i++) {
            final int buttonIndex = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gameStarted) {
                        handleButtonClick(buttonIndex);
                    }
                }
            });
        }

        startGame();
    }

    private void startGame() {
        gameStarted = true;
        correctButtonIndex = new Random().nextInt(6);
        triesRemaining = 3;
        enableAllButtons();
        playRandomVideo();
    }

    private void handleButtonClick(int buttonIndex) {
        // Disable all buttons temporarily
        disableAllButtons();

        if (buttonIndex == correctButtonIndex) {
            winsCounter++; // Increment wins counter
            updateWinsCounterText(); // Update wins counter text
            playCorrectVideo();
        } else {
            triesRemaining--;
            if (triesRemaining == 0) {
                endGame(false);
            } else {
                playRandomVideo();
            }
        }

        // Re-enable buttons
        enableAllButtons();
    }

    private void disableAllButtons() {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    private void enableAllButtons() {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }

    private void playRandomVideo() {
        int randomVideo = new Random().nextInt(6) + 1;
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/video" + randomVideo);
        videoView.setVideoURI(uri);
        videoView.setMediaController(null); // Disable media controller
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // No need to handle video completion in this case
                    }
                });
            }
        });
        // Start video playback
        videoView.start();
    }

    private void playCorrectVideo() {
        int correctVideoIndex = correctButtonIndex + 1; // Assuming video naming starts from 1
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/video" + correctVideoIndex);
        videoView.setVideoURI(uri);
        videoView.setMediaController(null); // Disable media controller
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        endGame(true);
                    }
                });
            }
        });
        // Start video playback
        videoView.start();
    }

    private void endGame(boolean win) {
        if (!win) {
            playCutscene("lose_cutscene"); // Play the losing cutscene
        }
        // Start the next activity after the cutscene finishes
        startNextActivity();
    }

    private void playCutscene(String cutsceneName) {
        // Get the resource ID of the cutscene video based on the name
        int cutsceneId = getResources().getIdentifier(cutsceneName, "raw", getPackageName());
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + cutsceneId);
        videoView.setVideoURI(uri);
        videoView.setMediaController(null); // Disable media controller
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // Start the next activity after the cutscene finishes
                        startNextActivity();
                    }
                });
            }
        });
        // Start video playback
        videoView.start();
    }

    private void startNextActivity() {
        // Start the next activity here
        Intent intent = new Intent(MainActivity.this, NextActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void updateWinsCounterText() {
        winsCounterTextView.setText("Wins: " + winsCounter);
    }
}

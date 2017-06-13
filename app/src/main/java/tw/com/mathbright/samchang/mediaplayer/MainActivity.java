package tw.com.mathbright.samchang.mediaplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import static android.R.attr.button;
import static android.R.attr.data;
import static android.R.attr.start;

public class MainActivity extends AppCompatActivity
        implements DialogInterface.OnDismissListener {

    private MediaPlayer mMediaPlayer;
    private final static int MAX_VOLUME = 100;
    private AudioManager mAudioManager;
    private SeekBar mVolumeControl;
    private int initVolume;
    private int maxVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button play = (Button) findViewById(R.id.play_button);
        final Button pause = (Button) findViewById(R.id.pause_button);
        final Button stop = (Button) findViewById(R.id.stop_button);
        final Button exit = (Button) findViewById(R.id.exit_button);
        final SeekBar seek = (SeekBar) findViewById(R.id.volume_bar);

        // after "new", mp is in "idle" state, it needs to be "initialized (setDataSource)" and  "prepared"
        // but if using "create" then no need to "prepare".
        mMediaPlayer = MediaPlayer.create(this, R.raw.faded);
        /*
        //final MediaPlayer mp = new MediaPlayer();
        String filename = "android.resource://" + this.getPackageName() + "/raw/faded";

        Log.v("*** MEDIA PLAYER :  ", filename);

        try {
            //Log.v("* Media Player :", getFilesDir().getAbsolutePath());
            mMediaPlayer.setDataSource(this, Uri.parse(filename));
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        // prepare volume setup
        // First set the Stream to your desired one & Initialize AudioManager (provides access to volume and ringer mode control)
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        // Get the InitialVolume (so that you set it back to this once you're done) and set it to MaxVolume
        initVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });
        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
            }
        });
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();

                try {
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.release();
            }
        });

        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                float volume = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
                mMediaPlayer.setVolume(volume, volume);
                mMediaPlayer.start();
            }
        });
        // Callback after the song is finished.
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Toast.makeText(MainActivity.this, "I'm done!", Toast.LENGTH_LONG).show();
                releaseMediaPlayer();
            }
        });
    }
    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initVolume, 0);

        //Stop Playing the Song
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
}

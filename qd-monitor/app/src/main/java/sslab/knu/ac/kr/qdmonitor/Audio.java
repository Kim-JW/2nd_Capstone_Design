package sslab.knu.ac.kr.qdmonitor;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Audio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this.getIntent());
        String Level = intent.getStringExtra("Level");
        MediaPlayer m = new MediaPlayer();

        switch (Level){
            case "1":
                m = MediaPlayer.create(this,R.raw.level1);
                m.start();
                break;
            case "2":
                m = MediaPlayer.create(this,R.raw.level2);
                m.start();
                break;
            case "3":
                m = MediaPlayer.create(this,R.raw.level3);
                m.start();
                break;
        }

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.stop();
                mp.release();
            }
        });
    }
}

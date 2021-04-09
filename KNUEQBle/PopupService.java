package sslab.knu.ac.kr.qdmonitor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupService extends Activity {
    TextView Levelview;
    ImageView ImageView;
    TextView Countermeasuresview;
    MediaPlayer m = new MediaPlayer();
    //String Level1CM = getString(R.string.Level1CM);
    //String Level2CM = getString(R.string.Level2CM);
    //String Level3CM = getString(R.string.Level3CM);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_service);
        Levelview = (TextView)findViewById(R.id.Level);
        ImageView = (ImageView)findViewById(R.id.table);
        Countermeasuresview = (TextView)findViewById(R.id.Countermeasures);
        Intent intent = getIntent();
        String Level = intent.getStringExtra("Level");
        Log.i(PopupService.class.getSimpleName(),"EQLevel = "+Level+"\n");
        switch (Level){
            case "1":
                Levelview.setText("낮음\n낮음");
                Levelview.setTextColor(Color.parseColor("#3cd405"));
                ImageView.setImageResource(R.drawable.level1);
                Countermeasuresview.setText("약한 지진입니다.\n당황하지 마시고 흔들림이 멈추면 주변에 파손된 물건이 없는지 확인하세요.");
                m = MediaPlayer.create(this,R.raw.lv1);
                m.start();
                break;
            case "2":
                Levelview.setText("주의\n주의");
                Levelview.setTextColor(Color.parseColor("#dcd646"));
                ImageView.setImageResource(R.drawable.level2);
                Countermeasuresview.setText("신속히 튼튼한 탁자 아래로 대피하여 신체를 보호하세요.\n흔들림이 멈추면 가스 및 전기를 차단하고 주변에 파손된 물건이 없는지 확인하세요.");
                m = MediaPlayer.create(this,R.raw.lv2);
                m.start();
                break;
            case "3":
                Levelview.setText("높음\n높음");
                Levelview.setTextColor(Color.parseColor("#d40f05"));
                ImageView.setImageResource(R.drawable.level3);
                Countermeasuresview.setText("신속히 튼튼한 탁자 아래로 대피하여 신체를 보호하세요.\n흔들림이 멈추는 즉시 두툼한 방석을 이용하여 머리를 보호한뒤 계단을 이용하여 탈출한 후 도보로 신속히 지진대피소, 공원으로 이동하세요.");
                m = MediaPlayer.create(this,R.raw.lv3);
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
        Intent intent1 = new Intent(getApplicationContext(), BleAdvertising.class);
        intent1.putExtra("Level", Level);
        startService(intent1);
    }

    public void mOnClose(View v){
        Intent intent1 = new Intent(getApplicationContext(), BleAdvertising.class);
        intent1.putExtra("Level", "99");
        startService(intent1);
        this.finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}

package com.mpc.reader;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContent = "The most comprehensive text-to-speech reading app online. Free for unlimited use. Generate speech and listen to texts, pdfs, ebooks & websites with the most natural sounding voices.";
        checkEnv();
    }

    private final int REQ_TTS_STATUS_CHECK = 1;

    private TextToSpeech mTts;
    private ImageButton mBtnFf;
    private ImageButton mBtnRew;
    private ImageButton mBtnPlay;
    private SeekBar mSeekBar;
    private Dialog mDlgTts;
    private String mContent;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                    initTts();
                    Log.d(TAG, "tts engine is instance");
                    break;
                case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
                    //文件已经损坏
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                    //缺少发音文件
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
                    //数据文件丢失

                    //从新更新TTS数据文件
                    Intent mUpdateData = new Intent();
                    mUpdateData.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(mUpdateData);

                    break;

                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
                    //检测失败应该重新检测
                    Log.d(TAG, "TTS engine checked fail");
                    break;
                default:
                    Log.d(TAG, "Got a failure. TTS apparently not available");
                    break;
            }

        }
    }

    private void checkEnv() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_TTS_STATUS_CHECK);
    }

    private void initTts() {
        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d(TAG, "onInit() status = " + status);

                if (status == TextToSpeech.SUCCESS) {
                    int result = mTts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.d(TAG, "language is not available");
                    } else {
                        showDlgTts();
                        initViews();
                        mTts.speak(mContent, TextToSpeech.QUEUE_ADD, null, mContent.hashCode()+"");
                    }

                }
            }
        });
    }

    private void showDlgTts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("阅读器")
                .setView(getLayoutInflater().inflate(R.layout.tts_controller, null, false))
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mTts.stop();
                        mTts.shutdown();
                        mTts = null;
                    }
                });
        mDlgTts = builder.create();
        mDlgTts.show();
    }

    private void initViews() {
        mBtnFf = mDlgTts.findViewById(R.id.btn_ff);
        mBtnPlay = mDlgTts.findViewById(R.id.btn_play);
        mBtnRew = mDlgTts.findViewById(R.id.btn_rew);
        mSeekBar = mDlgTts.findViewById(R.id.seekBar);

        mBtnFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBtnPlay.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnPlay.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                mTts.stop();
            }
        });

        mBtnRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}

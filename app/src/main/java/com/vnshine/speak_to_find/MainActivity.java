package com.vnshine.speak_to_find;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class MainActivity extends AppCompatActivity {
    ToggleButton buttonSwitch;
    TextView txtFrequency;
    CheckBox checkBoxSound;
    CheckBox checkBoxVibration;
    CheckBox checkBoxFlash;
    CheckBox checkBoxWork;
    SeekBar volume;
    TextView txtSelectRingtone;
    RelativeLayout rlScreen;
    RelativeLayout rlMain;

    SharedPreferences mPrefe;
    SharedPreferences.Editor myEditor;
    public static final String MPREF = "clapfinder";

    private AudioManager audioManager;
    private Vibrator v;
    private CameraManager mCameraManager;
    private String mCameraId;
    PowerManager pm;

    public String KEY_CHECKED_SOUND = "saveCheckedSound";
    public String KEY_CHECKED_VIBRATION = "saveCheckedVibration";
    public String KEY_CHECKED_FLASH = "saveCheckedFlash";
    public String KEY_CHECKED_WORK = "saveCheckedWork";

    int soundID[] = {
            R.raw.baby_smile,
            R.raw.iphone,
            R.raw.oggy_and_the_cockroaches,
            R.raw.tit_tit_remix,
            R.raw.weekend_has_come,
            R.raw.wolf_howl
    };
    String[] title = new String[]{
            "Baby smile",
            "Iphone",
            "Oggy and the cockroaches",
            "Tit Tit Tit",
            "Weekend has come",
            "Wolf howl"};

    MediaPlayer mp;

    ListView lvRingtone;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);


        rlMain = findViewById(R.id.layoutMain);
        rlScreen = findViewById(R.id.layoutScreen);
        buttonSwitch = findViewById(R.id.toggleButtonSwitch);
        txtFrequency = findViewById(R.id.textViewFrequency);
        checkBoxSound = findViewById(R.id.checkBoxSound);
        checkBoxVibration = findViewById(R.id.checkBoxVibration);
        checkBoxFlash = findViewById(R.id.checkBoxFlash);
        checkBoxWork = findViewById(R.id.checkBoxWork);
        volume = findViewById(R.id.seekBarVolume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        lvRingtone = findViewById(R.id.listViewRingtone);
        txtSelectRingtone = findViewById(R.id.textViewSelectRingtone);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        txtSelectRingtone.setText(title[0]);
        if (txtSelectRingtone.getText().equals(title[0])) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.baby_smile);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.line_ringtone, R.id.textViewRingtone, title);
        lvRingtone.setAdapter(adapter);

        lvRingtone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtSelectRingtone.setText(title[i]);
                lvRingtone.setVisibility(view.INVISIBLE);

                mp.reset();
                mp = MediaPlayer.create(MainActivity.this, soundID[i]);
            }
        });

        mPrefe = getBaseContext().getSharedPreferences(MPREF, MODE_PRIVATE);
        myEditor = mPrefe.edit();

        if (checkBoxSound.isChecked()) {
            checkBoxSound.setChecked(mPrefe.getBoolean(KEY_CHECKED_SOUND, true));
        } else {
            checkBoxSound.setChecked(mPrefe.getBoolean(KEY_CHECKED_SOUND, false));
        }

        if (checkBoxVibration.isChecked()) {
            checkBoxVibration.setChecked(mPrefe.getBoolean(KEY_CHECKED_VIBRATION, true));
        } else {
            checkBoxVibration.setChecked(mPrefe.getBoolean(KEY_CHECKED_VIBRATION, false));
        }

        if (checkBoxFlash.isChecked()) {
            checkBoxFlash.setChecked(mPrefe.getBoolean(KEY_CHECKED_FLASH, true));
        } else {
            checkBoxFlash.setChecked(mPrefe.getBoolean(KEY_CHECKED_FLASH, false));
        }

        if (checkBoxWork.isChecked()) {
            checkBoxWork.setChecked(mPrefe.getBoolean(KEY_CHECKED_WORK, true));
        } else {
            checkBoxWork.setChecked(mPrefe.getBoolean(KEY_CHECKED_WORK, false));
        }

        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    myEditor.putBoolean(KEY_CHECKED_SOUND, true);
                    myEditor.apply();

                } else {
                    myEditor.putBoolean(KEY_CHECKED_SOUND, false);
                    myEditor.apply();
                }
            }
        });

        checkBoxVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    myEditor.putBoolean(KEY_CHECKED_VIBRATION, true);
                    myEditor.apply();
                } else {
                    myEditor.putBoolean(KEY_CHECKED_VIBRATION, false);
                    myEditor.apply();
                }

            }
        });

        checkBoxFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    myEditor.putBoolean(KEY_CHECKED_FLASH, true);
                    myEditor.apply();
                } else {
                    myEditor.putBoolean(KEY_CHECKED_FLASH, false);
                    myEditor.apply();
                }
            }
        });

        checkBoxWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    myEditor.putBoolean(KEY_CHECKED_WORK, true);
                    myEditor.apply();
                } else {
                    myEditor.putBoolean(KEY_CHECKED_WORK, false);
                    myEditor.apply();
                }
            }
        });


        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (buttonSwitch.isChecked()) {
                    Toast.makeText(MainActivity.this, "On", Toast.LENGTH_LONG).show();

                    checkMicrophone();
                } else {
                    Toast.makeText(MainActivity.this, "Off", Toast.LENGTH_LONG).show();
                    turnOffFlash();
                    v.cancel();
                    stopPlaying();
                    if (txtSelectRingtone.getText().equals(title[0])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.baby_smile);
                    } else if (txtSelectRingtone.getText().equals(title[1])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.iphone);
                    } else if (txtSelectRingtone.getText().equals(title[2])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.oggy_and_the_cockroaches);
                    } else if (txtSelectRingtone.getText().equals(title[3])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.tit_tit_remix);
                    } else if (txtSelectRingtone.getText().equals(title[4])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.weekend_has_come);
                    } else if (txtSelectRingtone.getText().equals(title[5])) {
                        mp = MediaPlayer.create(MainActivity.this, R.raw.wolf_howl);
                    }
                }
            }
        });

        txtSelectRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvRingtone.setVisibility(view.VISIBLE);
            }
        });

        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvRingtone.setVisibility(view.INVISIBLE);
            }
        });

        volumeControl();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnInfo:
                final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle(getString(R.string.title_info_alertDialog));
                alert.setMessage(getString(R.string.message_info_alertDialog));
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.hide();
                    }
                });
                alert.show();
                break;

            default:
                break;
        }
        return true;
    }

    private void checkMicrophone() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(final PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        txtFrequency.setText("" + pitchInHz + " (Hz)");

                        if ((((2800.00000 <= pitchInHz) && (pitchInHz <= 3500.00000)) || ((350.00000 <= pitchInHz) && (pitchInHz <= 700.00000))) && buttonSwitch.isChecked() == true) {
                            if (checkBoxSound.isChecked() && buttonSwitch.isChecked()) {
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                mp.start();
                            }

                            if (checkBoxVibration.isChecked() && buttonSwitch.isChecked()) {
                                long[] pattern = {0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
                                v.vibrate(pattern, 2);
                            }

                            if (checkBoxFlash.isChecked() && buttonSwitch.isChecked()) {
                                try {
                                    mCameraId = mCameraManager.getCameraIdList()[0];
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                                turnOnFlash();

                                //TH device không hỗ trợ đèn flash
                                Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                                if (!isFlashAvailable) {
                                    AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                                    alert.setTitle(getString(R.string.title_flash_alertDialog));
                                    alert.setMessage(getString(R.string.message_flash_alertDialog));
                                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            System.exit(0);
                                        }
                                    });
                                    alert.show();
                                    return;
                                }
                            }

                            if (checkBoxWork.isChecked() && buttonSwitch.isChecked()) {
                                pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                if (pm.isScreenOn() == true) {
                                    turnOffFlash();
                                    v.cancel();
                                    stopPlaying();
                                    if (txtSelectRingtone.getText().equals(title[0])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.baby_smile);
                                    } else if (txtSelectRingtone.getText().equals(title[1])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.iphone);
                                    } else if (txtSelectRingtone.getText().equals(title[2])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.oggy_and_the_cockroaches);
                                    } else if (txtSelectRingtone.getText().equals(title[3])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.tit_tit_remix);
                                    } else if (txtSelectRingtone.getText().equals(title[4])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.weekend_has_come);
                                    } else if (txtSelectRingtone.getText().equals(title[5])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.wolf_howl);
                                    }
                                } else {
                                    mp.start();

                                    long[] pattern = {0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
                                    v.vibrate(pattern, 2);

                                    turnOnFlash();
                                }
                            }


                            rlMain.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    lvRingtone.setVisibility(view.INVISIBLE);
                                }
                            });

                            rlScreen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    buttonSwitch.setChecked(false);
                                    stopPlaying();

                                    if (txtSelectRingtone.getText().equals(title[0])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.baby_smile);
                                    } else if (txtSelectRingtone.getText().equals(title[1])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.iphone);
                                    } else if (txtSelectRingtone.getText().equals(title[2])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.oggy_and_the_cockroaches);
                                    } else if (txtSelectRingtone.getText().equals(title[3])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.tit_tit_remix);
                                    } else if (txtSelectRingtone.getText().equals(title[4])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.weekend_has_come);
                                    } else if (txtSelectRingtone.getText().equals(title[5])) {
                                        mp = MediaPlayer.create(MainActivity.this, R.raw.wolf_howl);
                                    }
                                    turnOffFlash();
                                    v.cancel();
                                }
                            });


                        }

                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher, "Audio Dispatcher").start();
    }


    public void stopPlaying() {
        mp.stop();
        mp.release();
        mp = null;
    }
//
//    @Override
//    protected void onResume() {
//        unlockScreen();
//        super.onResume();
//    }

//    private void unlockScreen() {
//        final Window win = getWindow();
//        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
//    }

    public void volumeControl() {
        volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        try {
            volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOnFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

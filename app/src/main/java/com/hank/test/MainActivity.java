package com.hank.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hank.oma.SmartCard;
import com.hank.oma.core.EnumReaderType;
import com.hank.oma.entity.CardResult;
import com.hank.oma.utils.LogUtil;

import java.security.MessageDigest;
import java.security.Signature;

import static com.hank.oma.utils.Hex.bytesToHexString;

public class MainActivity extends AppCompatActivity {

    private TextView openchannel;
    private TextView transceiveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SmartCard.getInstance().setmReaderType(EnumReaderType.READER_TYPE_SIM);
        openchannel = findViewById(R.id.openChannel);
        transceiveText = findViewById(R.id.transceiveText);
    }

    public void onBtnClick(View view) {
        //00A4040008A000000632010105
        //00A4040008A000000632010105
        //00A4040010D1560001010001600000000100000000
        //00A4040010D1560001010001600000000100000000
        SmartCard.getInstance().setmReaderType(EnumReaderType.READER_TYPE_SIM);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CardResult cardResult = SmartCard.getInstance().execute("00A4040010D1560001010001600000000100000000", "9000");
                if (cardResult.getStatus() == 0) {
                    LogUtil.i(cardResult.getRapdu());
                    LogUtil.i(cardResult.getSw());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openchannel.setText("Message:" + cardResult.getMessage() + "\nRapdu:" + cardResult.getRapdu() + "\nSw:" + cardResult.getSw());
                        }
                    });

                    transceive();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "开通道失败" + cardResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    LogUtil.i(cardResult.getMessage());
                    SmartCard.getInstance().closeChannel();
                    SmartCard.getInstance().closeService();
                }
            }
        }).start();
    }

    //00B0950058
    //00A4040008A000000632010105
    public void transceive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CardResult cardResult = SmartCard.getInstance().execute("80502400089FBD88CBBABC07DC00");
                if (cardResult.getStatus() == 0) {
                    LogUtil.i(cardResult.getRapdu());
                    LogUtil.i(cardResult.getSw());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            transceiveText.setText("Message:" + cardResult.getMessage() + "\nRapdu:" + cardResult.getRapdu() + "\nSw:" + cardResult.getSw());
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "执行失败" + cardResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    LogUtil.i(cardResult.getMessage());
                    SmartCard.getInstance().closeChannel();
                    SmartCard.getInstance().closeService();
                }
            }
        }).start();
    }
}

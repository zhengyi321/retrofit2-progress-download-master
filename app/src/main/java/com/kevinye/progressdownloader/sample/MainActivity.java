package com.kevinye.progressdownloader.sample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kevinye.progressdownloader.R;
import com.kevinye.progressdownloader.download.ProgressNet;
import com.kevinye.progressdownloader.download.ProgressResponseBody;
import com.kevinye.progressdownloader.sample.utils.ApkInstaller;
import com.kevinye.progressdownloader.download.FileUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.download_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_button:
                if (dialog == null) {
                    dialog = getProgressDialog();
                }
                dialog.show();
                ProgressNet.getApiService(new ProgressResponseBody.ProgressListener() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        dialog.setMax((int) (contentLength / 1024 / 1024));
                        dialog.setProgress((int) (bytesRead / 1024 / 1024));
                    }
                }).getWeChartAPK()
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ResponseBody, File>() {
                            @Override
                            public File call(ResponseBody responseBody) {
                                File apk = new File(Environment.getExternalStorageDirectory(), "WeChart");
                                FileUtil.save(responseBody.byteStream(), apk);
                                return apk;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File apk) {
                                dialog.dismiss();
                                getInstallDialog(apk).show();
                            }
                        });
                break;
        }
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressNumberFormat("%1d MB/%2d MB");
        dialog.setTitle(getString(R.string.download));
        dialog.setMessage(getString(R.string.download_in_progress));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        return dialog;
    }

    private AlertDialog getInstallDialog(final File apk) {
        return new AlertDialog.Builder(this).
                setTitle(getString(R.string.download_completed)).
                setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (apk == null || !apk.exists()) {
                            Toast.makeText(MainActivity.this, R.string.app_not_downloaded, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ApkInstaller.install(MainActivity.this, apk);
                    }
                }).
                setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }
}

package org.tianjyan.luban.plugin.logcat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import org.tianjyan.luban.infrastructure.abs.ui.AbsActivity;
import org.tianjyan.luban.plugin.logcat.R;
import org.tianjyan.luban.plugin.logcat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class CrashDetailActivity extends AbsActivity{
    @BindView(R2.id.crashDetail)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_detail);
        ButterKnife.bind(this);
        onNewIntent(getIntent());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        String packageName = bundle.getString("packageName");
        String content = bundle.getString("content");
        textView.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

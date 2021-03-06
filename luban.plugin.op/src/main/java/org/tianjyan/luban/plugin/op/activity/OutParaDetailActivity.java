package org.tianjyan.luban.plugin.op.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.tianjyan.luban.aidl.AidlEntry;
import org.tianjyan.luban.aidl.Config;
import org.tianjyan.luban.aidl.OutPara;
import org.tianjyan.luban.infrastructure.abs.plugin.IOutParaPlugin;
import org.tianjyan.luban.plugin.common.AliasName;
import org.tianjyan.luban.plugin.op.R;
import org.tianjyan.luban.plugin.op.R2;
import org.tianjyan.luban.plugin.op.adapter.OutParaDetailAdapter;
import org.tianjyan.luban.plugin.op.bridge.UIOutParaBridge;
import org.tianjyan.luban.plugin.op.event.OutParaHistoryUpdateEvent;
import org.tianjyan.luban.plugin.op.model.ParaHistory;

import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class OutParaDetailActivity extends AppCompatActivity {
    @Inject @Named(AliasName.OUT_PARA_BRIDGE)
    UIOutParaBridge outParaBridge;
    @Inject @Named(AliasName.OUT_PARA_PLUGIN)
    IOutParaPlugin outParaPlugin;
    @BindView(R2.id.para_rv) RecyclerView recyclerView;
    OutPara outPara;
    OutParaDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_para_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        String paraName = getIntent().getStringExtra("paraName");
        String pkgName = getIntent().getStringExtra("pkgName");
        outPara = outParaBridge.getOutPara(paraName, pkgName);
        Vector<ParaHistory> histories = outParaBridge.getHistories(outPara);
        adapter = new OutParaDetailAdapter(this, histories);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(paraName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!outParaPlugin.isGathering()) {
            if (outPara.getDisplayProperty() == AidlEntry.DISPLAY_FLOATING ||
                    outParaBridge.getFloatingItemCount() < Config.MAX_FLOATING_COUNT) {
                getMenuInflater().inflate(R.menu.para_detail, menu);
                int resId = outPara.getDisplayProperty() == AidlEntry.DISPLAY_FLOATING
                        ? R.string.menu_move_out_floating
                        : R.string.menu_move_to_floating;
                menu.findItem(R.id.move).setTitle(getString(resId));
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.move) {
            if (outPara.getDisplayProperty() == AidlEntry.DISPLAY_FLOATING) {
                outParaBridge.moveParaFromFloatingToNormal(outPara);
            } else {
                outParaBridge.moveParaFromNormalToFloating(outPara);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOutParaHistoryUpdated(OutParaHistoryUpdateEvent event) {
        if (event.getOutPara() == outPara) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

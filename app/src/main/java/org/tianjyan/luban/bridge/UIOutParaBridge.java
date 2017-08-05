package org.tianjyan.luban.bridge;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.tianjyan.luban.activity.OutParaDataAdapter;
import org.tianjyan.luban.aidl.AidlEntry;
import org.tianjyan.luban.aidl.OutPara;
import org.tianjyan.luban.event.OutParaHistoryUpdateEvent;
import org.tianjyan.luban.event.RegisterOutParaEvent;
import org.tianjyan.luban.event.SetOutParaEvent;
import org.tianjyan.luban.manager.ClientManager;
import org.tianjyan.luban.manager.IClient;
import org.tianjyan.luban.model.Const;
import org.tianjyan.luban.model.ParaHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class UIOutParaBridge {
    private boolean isRunning;
    private static UIOutParaBridge INSTANCE = new UIOutParaBridge();
    public static UIOutParaBridge getInstance() {
        return INSTANCE;
    }

    private OutParaDataAdapter outParaDataAdapter;
    private List<OutPara> outParas = Collections.synchronizedList(new ArrayList<>());
    private UIOutParaHistoryBridge historyBridge;

    public UIOutParaBridge() {
        EventBus.getDefault().register(this);
        historyBridge = UIOutParaHistoryBridge.getInstance();
        initParamList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterOutPara(RegisterOutParaEvent event) {
        OutPara outPara = event.getOutPara();
        switch (outPara.getDisplayProperty()) {
            case AidlEntry.DISPLAY_FLOATING:
                addParaToFloatingArea(outPara);
                break;
            case AidlEntry.DISPLAY_NORMAL:
                addParaToNormalArea(outPara);
                historyBridge.addOutPara(outPara);
                break;
            default:
                break;
        }
        outParaDataAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetOutPara(SetOutParaEvent event) {
        if (!isRunning) return;
        int position = outParas.indexOf(event.getOutPara());
        if (position > -1) {
            historyBridge.addHistory(event.getOutPara(), event.getValue());
            outParaDataAdapter.notifyItemChanged(position);
            EventBus.getDefault().post(
                    new OutParaHistoryUpdateEvent(event.getOutPara(), event.getValue()));
        }
    }

    public OutParaDataAdapter getOutParaDataAdapter(Context context) {
        if (outParaDataAdapter == null) {
            outParaDataAdapter = new OutParaDataAdapter(context, outParas);
        }
        return outParaDataAdapter;
    }

    public OutPara getOutPara(String paraName, String pkgName) {
        synchronized (outParas) {
            for (OutPara p : outParas) {
                if (p.getClient() != null
                        && p.getClient().equals(pkgName)
                        && p.getKey().equals(paraName))
                    return p;
            }
        }
        throw new IllegalArgumentException("Can't find the para.");
    }

    public Vector<ParaHistory> getHistories(OutPara outPara) {
        return historyBridge.getHistories(outPara);
    }

    public void clearHistories() {
        historyBridge.clearHistories();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private void initParamList() {
        List<OutPara> temps = getAll();

        outParas.clear();

        OutPara floatingPara = new OutPara();
        floatingPara.setKey(Const.Floating_Area_Title);
        outParas.add(floatingPara);
        addParas(AidlEntry.DISPLAY_FLOATING, temps);

        OutPara normalPara = new OutPara();
        normalPara.setKey(Const.Normal_Area_Title);
        outParas.add(normalPara);
        addParas(AidlEntry.DISPLAY_NORMAL, temps);
    }

    private void addParas(int type, List<OutPara> sources) {
        for (OutPara para: sources) {
            if (para.getDisplayProperty() == type) {
                outParas.add(para);
                historyBridge.addOutPara(para);
            }
        }
//        sources.stream().forEach(para -> {
//            if (para.getDisplayProperty() == type) {
//                outParas.add(para);
//            }
//        });
    }

    private void addParaToFloatingArea(OutPara outPara) {
        if (outParas.contains(outPara) ||
                outPara.getDisplayProperty() != AidlEntry.DISPLAY_FLOATING) {
            return;
        }

        int normalAreaPosition = getNormalDividePosition();

        if (normalAreaPosition < 4) {
            outParas.add(normalAreaPosition, outPara);
        } else {
            outPara.setDisplayProperty(AidlEntry.DISPLAY_NORMAL);
            addParaToNormalArea(outPara);
        }
    }

    private void addParaToNormalArea(OutPara outPara) {
        if (outParas.contains(outPara) ||
                outPara.getDisplayProperty() != AidlEntry.DISPLAY_NORMAL) {
            return;
        }

        outParas.add(outPara);
    }

    private int getNormalDividePosition() {
        return getDividePosition(Const.Normal_Area_Title);
    }

    private List<OutPara> getAll() {
        List<OutPara> tempOutParas = new ArrayList<>();
        for (IClient client : ClientManager.getInstance().getAllClient()) {
            tempOutParas.addAll(client.getOutPara());
        }
        return tempOutParas;
    }

    private int getDividePosition(String title) {
        int pos = 0;
        for (int i = 0; i < outParas.size(); i++) {
            if (outParas.get(i).getKey() == title) {
                pos = i;
                break;
            }
        }
        return pos;
    }
}
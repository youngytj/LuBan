package org.tianjyan.luban.client;

public class OutParaManager {
    public void register(String paraName) {
        LBInternal.getInstance().getOutParaManager().register(paraName);
    }

    public void setOutParasInFloatingArea(String... paraNames) {
        LBInternal.getInstance().getOutParaManager().setOutParasInFloatingArea(paraNames);
    }
}

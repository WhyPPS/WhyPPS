package club.hsspace.whypps.listener;

import club.hsspace.whypps.model.senior.*;

public enum DataLabel {
    DATA_S(DataS.class), DATA_R(DataR.class),
    BIN_S(BinS.class), BIN_R(BinR.class),
    HEART_S(HeartS.class), HEART_R(HeartR.class),
    LONG_S(LongS.class), LONG_R(LongR.class), LONG_M(LongM.class),

    RADIO(null),
    SWAP_S(null), SWAP_R(null);

    public final Class<? extends SeniorBaseModel> mapClass;

    DataLabel(Class<? extends SeniorBaseModel> dataModel) {
        this.mapClass = dataModel;
    }
}

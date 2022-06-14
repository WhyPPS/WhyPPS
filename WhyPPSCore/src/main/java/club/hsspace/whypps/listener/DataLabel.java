package club.hsspace.whypps.listener;

import club.hsspace.whypps.model.senior.*;

public enum DataLabel {
    DATA_S(DataS.class), DATA_R(DataR.class),
    BIN_S(BinS.class), BIN_R(BinR.class),
    HEART_S(HeartS.class), HEART_R(HeartR.class),
    LONG_S(LongS.class), LONG_R(LongR.class), LONG_M(LongM.class),

    RADIO(Radio.class), RADIO_S(RadioS.class), RADIO_R(RadioR.class),
    SWAP_S(SwapS.class), SWAP_R(SwapR.class);

    public final Class<? extends SeniorBaseModel> mapClass;

    DataLabel(Class<? extends SeniorBaseModel> dataModel) {
        this.mapClass = dataModel;
    }
}

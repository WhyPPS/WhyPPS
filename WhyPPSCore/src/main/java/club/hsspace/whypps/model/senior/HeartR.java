package club.hsspace.whypps.model.senior;

/**
 * @ClassName: HeartR
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class HeartR extends SeniorBaseModel{

    public int delay;

    private HeartR() {

    }

    public static HeartR of(String requestId, long time,int delay) {
        HeartR heartR = new HeartR();
        heartR.requestId = requestId;
        heartR.delay = delay;
        heartR.time = time;
        return heartR;
    }

}

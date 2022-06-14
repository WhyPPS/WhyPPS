package club.hsspace.whypps.model.senior;

/**
 * @ClassName: Radio
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class Radio extends SeniorBaseModel{

    public int times;

    public static Radio of(String requestId, int times) {
        Radio radio = new Radio();
        radio.time = System.currentTimeMillis();
        radio.requestId = requestId;
        radio.times = times;
        return radio;
    }

}

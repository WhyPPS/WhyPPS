package club.hsspace.whypps.model.senior;

import java.util.*;

/**
 * @ClassName: Code
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public record Code(int code, String msg) {

    private static final Map<Integer, Code> codeMap = new HashMap<>();

    public static final Code OK = of(10000, "请求成功");
    public static final Code NOT_FOUND = of(22020, "回应方无相应资源");
    public static final Code SERVER_ERROR = of(34010, "回应方处理时出现了意料之外的错误");
    public static final Code REQUEST_FAIL = of(20000, "请求失败");

    public static Code of(int code) {
        return of(code, "未定义错误信息码，如您遇到此错误，请联系系统管理员。");
    }

    public static Code of(int code, String msg) {
        Code codeI = codeMap.get(code);
        if (codeI != null)
            return codeI;
        codeI = new Code(code, msg);
        codeMap.put(code, codeI);
        return codeI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code)) return false;

        Code code1 = (Code) o;

        return code() == code1.code();
    }

    @Override
    public int hashCode() {
        return code();
    }
}

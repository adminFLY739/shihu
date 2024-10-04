package cn.lili.modules.member.entity.enums;

/**
 * @author: nxc
 * @since: 2023/7/4 09:27
 * @description: 用户状态
 */
public enum MemberStatusEnum {

    /**
     * 开启中
     */
    OPEN("用户开启"),
    /**
     * 店铺关闭
     */
    CLOSED("用户关闭"),

    /**
     * 取消申请
     */
    CANCEL("取消申请"),
    /**
     * 审核拒绝
     */
    REFUSED("审核拒绝"),
    /**
     * 申请中
     */
    APPLYING("申请中");

    private final String description;

    MemberStatusEnum(String des) {
        this.description = des;
    }

    public String description() {
        return this.description;
    }

    public String value() {
        return this.name();
    }
}

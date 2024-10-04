package cn.lili.modules.card.entity.enums;

/**
 * @author: nxc
 * @since: 2023/6/19 09:13
 * @description: 提货状态
 */
public enum deliveryStatus {

    NOTISSUED("未领取"),

    NOTUSE("未使用"),

    RECEIVED("已使用"),

    CLOSED("已关闭"),

    OVERDUE("已过期");


    private final String description;

    deliveryStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}

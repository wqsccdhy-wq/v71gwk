package com.seeyon.v3x.peoplerelate.enums;

/**
 * 关联人员类型
 */
public enum RelationType {

    leader(1), // 领导
    assistant(2), // 秘书
    junior(3), // 下级
    confrere(4), //同事
    otherEscapeLeader(5);//除了领导的所有人员

    private int key;

    private RelationType(int key) {
        this.key = key;
    }

    public int key() {
        return key;
    }

    /**
     * 根据key得到枚举类型
     * @param key
     * @return
     */
    public static RelationType valueOf(int key) {
        RelationType[] types = RelationType.values();

        if (types != null) {
            for (RelationType type : types) {
                if (type.key() == key) {
                    return type;
                }
            }
        }

        return null;
    }

}

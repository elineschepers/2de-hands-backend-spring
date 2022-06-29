package be.ucll.tweedehandsbackend.Enums;

public enum ECondition {
    USED, NEW, AS_GOOD_AS_NEW, PACKAGED;

    /**
     *Judging the validity of the Enum parameter
     */
    public static boolean isValidEnum(String condition) {
        for (ECondition conditionStatusEnum : ECondition.values()) {
            if (conditionStatusEnum.name().equals(condition)) {
                return true;
            }
        }
        return false;
    }
}

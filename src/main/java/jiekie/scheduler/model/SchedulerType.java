package jiekie.scheduler.model;

import java.util.Optional;

public enum SchedulerType {
    WORLD_RESET("월드초기화", "world_reset"),
    SERVER_AUTO_STOP("서버자동종료", "server_auto_stop"),
    CLEAN("청소", "clean"),
    CHURCH_BELL_SOUND("정각타종음", "church_bell_sound"),
    WEALTHY_STATUE_CREATE("부자동상생성", "wealthy_statue_create"),
    WILD_WARP_TICKET("야생이용시간", "wild_warp_ticket"),
    HELL_WARP_TICKET("지옥이용시간", "hell_warp_ticket");

    private final String displayName;
    private final String configKey;

    SchedulerType(String displayName, String configKey) {
        this.displayName = displayName;
        this.configKey = configKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public static Optional<SchedulerType> fromDisplayName(String name) {
        for (SchedulerType type : SchedulerType.values()) {
            if(type.displayName.equals(name)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}

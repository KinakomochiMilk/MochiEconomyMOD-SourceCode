package jp.kinakomochi.mochieconomy.item;

public enum MochiCoinType {

    NORMAL(
            "mochi_coin",
            1L,
            0
    ),

    COMPRESSED(
            "compressed_mochi_coin",
            64L,
            1
    ),

    SUPER_COMPRESSED(
            "super_compressed_mochi_coin",
            4096L,
            2
    );

    private final String localizationKey;
    private final long value;
    private final int visualLevel;

    MochiCoinType(
            String localizationKey,
            long value,
            int visualLevel
    ) {
        this.localizationKey = localizationKey;
        this.value = value;
        this.visualLevel = visualLevel;
    }

    public String getLocalizationKey() {
        return localizationKey;
    }

    public long getValue() {
        return value;
    }

    public int getVisualLevel() {
        return visualLevel;
    }

    public boolean canCompress() {
        return getNext() != null;
    }

    public boolean canDecompress() {
        return getPrevious() != null;
    }

    public MochiCoinType getNext() {
        switch (this) {
            case NORMAL:
                return COMPRESSED;

            case COMPRESSED:
                return SUPER_COMPRESSED;

            case SUPER_COMPRESSED:
            default:
                return null;
        }
    }

    public MochiCoinType getPrevious() {
        switch (this) {
            case NORMAL:
                return null;

            case COMPRESSED:
                return NORMAL;

            case SUPER_COMPRESSED:
                return COMPRESSED;

            default:
                return null;
        }
    }

    public long getCompressionAmount() {
        return 64L;
    }

    public static MochiCoinType fromValue(long value) {
        for (MochiCoinType type : values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }

    public static MochiCoinType fromName(String name) {
        if (name == null) {
            return null;
        }

        for (MochiCoinType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
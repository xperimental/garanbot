package net.sourcewalker.garanbot.api;

/**
 * Describes where an object was modified the last time.
 * 
 * @author Xperimental
 */
public enum ModificationOrigin {
    /**
     * Unknown location.
     */
    UNKNOWN(0),
    /**
     * Modified on local client.
     */
    CLIENT(1),
    /**
     * Modification date provided from server.
     */
    SERVER(2);

    private int value;

    ModificationOrigin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ModificationOrigin parseInt(int value) {
        for (ModificationOrigin iv : values()) {
            if (iv.getValue() == value) {
                return iv;
            }
        }
        throw new IllegalArgumentException("ItemVisibility value not found: "
                + value);
    }
}

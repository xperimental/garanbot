package net.sourcewalker.garanbot.api;

/**
 * Enum containing all the possible values for the item's visibility to friends.
 * 
 * @author Xperimental
 */
public enum ItemVisibility {
    PRIVATE(0), FRIENDS(1), FRIENDS_OF_FRIENDS(2), PUBLIC(3);

    private int value;

    ItemVisibility(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemVisibility parseInt(int value) {
        for (ItemVisibility iv : values()) {
            if (iv.getValue() == value) {
                return iv;
            }
        }
        throw new IllegalArgumentException("ItemVisibility value not found: "
                + value);
    }
}

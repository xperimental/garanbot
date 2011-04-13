package net.sourcewalker.garanbot.data;

/**
 * Represents the local state of an item.
 * 
 * @author Xperimental
 */
public class LocalState {

    public static final int UNCHANGED = 0;

    public static final int CHANGED_DETAILS = 1;

    public static final int CHANGED_PICTURE = 2;

    public static final int DELETED = 4;

    private int value;

    /**
     * Create a new instance which signals an unchanged state.
     */
    public LocalState() {
        this(UNCHANGED);
    }

    public LocalState(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public boolean isUnchanged() {
        return value == UNCHANGED;
    }

    public boolean changed() {
        return detailsChanged() || pictureChanged();
    }

    public boolean detailsChanged() {
        return (value & CHANGED_DETAILS) > 0;
    }

    public boolean pictureChanged() {
        return (value & CHANGED_PICTURE) > 0;
    }

    public boolean isDeleted() {
        return (value & DELETED) > 0;
    }

    public void setDeleted() {
        value |= LocalState.DELETED;
    }

    public void setDetailsChanged() {
        value |= LocalState.CHANGED_DETAILS;
    }

    public void setPictureChanged() {
        value |= LocalState.CHANGED_PICTURE;
    }

}

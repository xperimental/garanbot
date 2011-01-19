package net.sourcewalker.garanbot.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Contains the data of one registered item.
 * 
 * @author Xperimental
 */
public class Item {

    /**
     * Unique id of item. Assigned by Garanbo server and can not be changed.
     */
    private final int id;

    /**
     * Displayname of item.
     */
    private String name;

    /**
     * Manufacturer of item.
     */
    private String manufacturer;

    /**
     * Model name of item.
     */
    private String itemType;

    /**
     * Vendor where the item was purchased.
     */
    private String vendor;

    /**
     * Location where the item is currently.
     */
    private String location;

    /**
     * Notes about the item.
     */
    private String notes;

    /**
     * True, if the user has uploaded a picture for this item.
     */
    private boolean hasPicture;

    /**
     * Visibility of item to friends.
     */
    private ItemVisibility visibility;

    /**
     * Date the item was purchased.
     */
    private Date purchaseDate;

    /**
     * Date the item's warranty ends.
     */
    private Date endOfWarranty;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public ItemVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ItemVisibility visibility) {
        this.visibility = visibility;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getEndOfWarranty() {
        return endOfWarranty;
    }

    public void setEndOfWarranty(Date endOfWarranty) {
        this.endOfWarranty = endOfWarranty;
    }

    public Item(int id) {
        this.id = id;
    }

    /**
     * Tries to create a Item object from the provided JSON string.
     * 
     * @param content
     *            JSON content as String.
     * @return Item object
     * @throws ClientException
     *             If the provided JSON is not valid.
     */
    public static Item fromJSON(String content) throws ClientException {
        try {
            JSONObject object = (JSONObject) new JSONTokener(content)
                    .nextValue();
            Item result = new Item(object.getInt("id"));
            result.setName(object.getString("name"));
            result.setManufacturer(object.getString("manufacturer"));
            result.setItemType(object.getString("itemType"));
            result.setVendor(object.getString("vendor"));
            result.setLocation(object.getString("location"));
            result.setNotes(object.getString("notes"));
            result.setHasPicture(object.getBoolean("hasPicture"));
            result.setVisibility(ItemVisibility.parseInt(object
                    .getInt("visibility")));
            result.setPurchaseDate(parseDate(object.getString("purchaseDate")));
            result.setEndOfWarranty(parseDate(object.getString("endOfWarranty")));
            return result;
        } catch (JSONException e) {
            throw new ClientException("Error parsing Item: " + e.getMessage(),
                    e);
        }
    }

    private static Date parseDate(String string) throws ClientException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            return format.parse(string);
        } catch (ParseException e) {
            throw new ClientException("Error parsing date: " + e.getMessage(),
                    e);
        }
    }

}

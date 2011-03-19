package net.sourcewalker.garanbot.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Result returned by the server when querying for a product by barcode.
 * 
 * @author Xperimental
 */
public class BarcodeResult implements Parcelable {

    private String name;
    private String manufacturer;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public BarcodeResult(final JSONObject json) throws JSONException {
        name = json.getString("name");
        manufacturer = json.getString("manufactuer");
        type = json.getString("type");
    }

    public BarcodeResult(final Parcel source) {
        name = source.readString();
        manufacturer = source.readString();
        type = source.readString();
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<BarcodeResult> CREATOR = new Parcelable.Creator<BarcodeResult>() {

        public BarcodeResult createFromParcel(final Parcel in) {
            return new BarcodeResult(in);
        }

        public BarcodeResult[] newArray(final int size) {
            return new BarcodeResult[size];
        }

    };

}

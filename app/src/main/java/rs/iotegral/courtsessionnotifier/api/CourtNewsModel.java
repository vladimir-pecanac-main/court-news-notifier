package rs.iotegral.courtsessionnotifier.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lauda on 8/20/2018 23:21.
 */
public class CourtNewsModel {
    @SerializedName("d")
    @Expose
    private ApiData d;

    public ApiData getData() {
        return d;
    }

    public void setData(ApiData d) {
        this.d = d;
    }
}

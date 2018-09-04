package rs.iotegral.courtsessionnotifier.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lauda on 8/20/2018 23:23.
 */
public class ApiData {
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("__count")
    @Expose
    private String count;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

package rs.iotegral.courtsessionnotifier.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lauda on 8/20/2018 23:24.
 */
public class Result {
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("RowGuid")
    @Expose
    private String rowGuid;
    @SerializedName("ContentItemId")
    @Expose
    private Integer contentItemId;
    @SerializedName("LangTag")
    @Expose
    private String langTag;
    @SerializedName("VirtualPath")
    @Expose
    private String virtualPath;
    @SerializedName("VirtualDir")
    @Expose
    private String virtualDir;
    @SerializedName("IsDirectory")
    @Expose
    private Boolean isDirectory;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("ContentType")
    @Expose
    private String contentType;
    @SerializedName("LastUpdated")
    @Expose
    private String lastUpdated;
    @SerializedName("Published")
    @Expose
    private String published;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("RelationshipName")
    @Expose
    private Object relationshipName;
    @SerializedName("LangRelationshipId")
    @Expose
    private String langRelationshipId;
    @SerializedName("DocumentOrder")
    @Expose
    private String documentOrder;
    @SerializedName("Exists")
    @Expose
    private Boolean exists;
    @SerializedName("Roles")
    @Expose
    private String roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRowGuid() {
        return rowGuid;
    }

    public void setRowGuid(String rowGuid) {
        this.rowGuid = rowGuid;
    }

    public Integer getContentItemId() {
        return contentItemId;
    }

    public void setContentItemId(Integer contentItemId) {
        this.contentItemId = contentItemId;
    }

    public String getLangTag() {
        return langTag;
    }

    public void setLangTag(String langTag) {
        this.langTag = langTag;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public String getVirtualDir() {
        return virtualDir;
    }

    public void setVirtualDir(String virtualDir) {
        this.virtualDir = virtualDir;
    }

    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(Object relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getLangRelationshipId() {
        return langRelationshipId;
    }

    public void setLangRelationshipId(String langRelationshipId) {
        this.langRelationshipId = langRelationshipId;
    }

    public String getDocumentOrder() {
        return documentOrder;
    }

    public void setDocumentOrder(String documentOrder) {
        this.documentOrder = documentOrder;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}

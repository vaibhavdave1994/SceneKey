package com.scenekey.model;

import java.io.Serializable;

public class BucketDataModel implements Serializable {
    private String Key;
    private String LastModified;
    private String ETag;
    private String Size;
    private String StorageClass;
    private OwnerModel Owner;

    public BucketDataModel(String key, String lastModified, String ETag,String Size, String storageClass, OwnerModel owner) {
        Key = key;
        LastModified = lastModified;
        this.ETag = ETag;
        StorageClass = storageClass;
        this.Size = Size;
        Owner = owner;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getLastModified() {
        return LastModified;
    }

    public void setLastModified(String lastModified) {
        LastModified = lastModified;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public String getStorageClass() {
        return StorageClass;
    }

    public void setStorageClass(String storageClass) {
        StorageClass = storageClass;
    }

    public OwnerModel getOwner() {
        return Owner;
    }

    public void setOwner(OwnerModel owner) {
        Owner = owner;
    }
}

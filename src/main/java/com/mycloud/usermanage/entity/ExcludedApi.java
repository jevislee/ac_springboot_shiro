package com.mycloud.usermanage.entity;

import java.io.Serializable;
public class ExcludedApi implements Serializable {
    private Integer id;

    private String uri;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

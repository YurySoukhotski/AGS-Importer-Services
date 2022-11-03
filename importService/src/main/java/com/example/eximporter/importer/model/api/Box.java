/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Box
 */
public class Box {
    @JsonProperty("boxId")
    private Long boxId;

    @JsonProperty("pageId")
    private Long pageId;

    public Box(Long boxId, Long pageId) {
        this.boxId = boxId;
        this.pageId = pageId;
    }

    public Box() {
       super();
    }

    public Box boxId(Long boxId)
    {
        this.boxId=boxId;
        return this;
    }

    public Box pageId(Long pageId)
    {
        this.pageId=pageId;
        return this;
    }

    /**
     * Get boxId
     *
     * @return boxId
     **/

    public Long getBoxId() {
        return boxId;
    }

    public void setBoxId(Long boxId) {
        this.boxId = boxId;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
}


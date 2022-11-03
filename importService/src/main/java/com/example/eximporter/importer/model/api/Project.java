/**
 * Copyright (c) 2017 apollon GmbH+Co. KG All Rights Reserved.
 */
package com.example.eximporter.importer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
public class Project
{
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("parentId")
  private Long parentId = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("displayName")
  private String displayName = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("projects")

  private List<Link> projects = null;

  @JsonProperty("attributes")
  private AttributesValues attributes = null;

  @JsonProperty("tableAttributes")
  private TableAttributesValues tableAttributes = null;

  public Project id(Long id) {
    this.id = id;
    return this;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Project parentId(Long parentId) {
    this.parentId = parentId;
    return this;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Project type(String type) {
    this.type = type;
    return this;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Project projects(List<Link> projects) {
    this.projects = projects;
    return this;
  }

  public Project addProjectsItem(Link projectsItem) {
    if (this.projects == null) {
      this.projects = new ArrayList<>();
    }
    this.projects.add(projectsItem);
    return this;
  }

   /**
   * Links to children projects
   * @return projects
  **/
  public List<Link> getProjects() {
    return projects;
  }

  public void setProjects(List<Link> projects) {
    this.projects = projects;
  }

  public Project attributes(AttributesValues attributes) {
    this.attributes = attributes;
    return this;
  }

   /**
   * Get attributes
   * @return attributes
  **/
  public AttributesValues getAttributes() {
    return attributes;
  }

  public void setAttributes(AttributesValues attributes) {
    this.attributes = attributes;
  }

  public Project tableAttributes(TableAttributesValues tableAttributes) {
    this.tableAttributes = tableAttributes;
    return this;
  }

   /**
   * Get tableAttributes
   * @return tableAttributes
  **/
  public TableAttributesValues getTableAttributes() {
    return tableAttributes;
  }

  public void setTableAttributes(TableAttributesValues tableAttributes) {
    this.tableAttributes = tableAttributes;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }
}


package com.saudi.tourism.core.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.models.app.contact.Telephone;
import com.saudi.tourism.core.models.app.contact.Service;
import com.saudi.tourism.core.models.app.contact.Embassy;
import com.saudi.tourism.core.models.app.contact.ContactItem;

import com.saudi.tourism.core.models.app.page.Search;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Bean for the Contact API response.
 */
@Data
@Builder
public class ContactResponse implements Serializable {

  /**
   * List of Contacts Info.
   */
  private List<ContactItem> contacts;

  /**
   * List of services.
   */
  private List<Service> services;

  /**
   * List of Telephone Providers.
   */
  private Telephone telephone;

  /**
   * Embassy info.
   */
  private Embassy embassy;

  /**
   * Search Tags.
   */
  private Search search;

  /**
   * favId for app to map to web pages.
   */
  @JsonProperty("favId")
  @SerializedName("favId")
  private String webMappingPath;


}

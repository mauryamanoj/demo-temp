package com.saudi.tourism.core.models.app.contact;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.common.AppBaseModel;
import com.saudi.tourism.core.models.app.page.Search;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import java.util.List;

/**AppSeasonCampaignPageModel
 * App contact detail page model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = {Constants.APP_CONTACTS_PAGE_RESOURCE_TYPE})

@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
    value = Constants.STR_TRUE)})

public class AppContactPageModel extends AppBaseModel {

  /**
   * Contact info.
   */
  @Getter
  @Setter
  @ChildResource
  private Contact contact;

  /**
   * List of contact.
   */
  @Getter
  @Setter
  @ChildResource
  @JsonIgnore
  private transient List<ContactItem> contacts;

  /**
   * List of services.
   */
  @Getter
  @Setter
  @ChildResource
  private List<Service> services;

  /**
   * Telephone list provider.
   */
  @Getter
  @Setter
  @ChildResource
  private Telephone telephone;

  /**
   * Embassy info.
   */
  @Getter
  @Setter
  @ChildResource
  private Embassy embassy;

  /**
   * Search Tags.
   */
  @Getter
  @Setter
  @ChildResource
  private Search search;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    setWebMappingPath(LinkUtils.getFavoritePath(getWebMappingPath()));
  }

}

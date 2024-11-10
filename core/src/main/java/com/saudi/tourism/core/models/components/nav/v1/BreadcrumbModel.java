package com.saudi.tourism.core.models.components.nav.v1;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class BreadcrumbModel {

  /**
   * AEM core Breadcrumb representation.
   */
  @Self
  @Getter(AccessLevel.NONE)
  private transient Breadcrumb breadcrumb;

  /**
   * Run Mode Service.
   */
  @OSGiService
  private transient RunModeService runModeService;

  /**
   * Breadcrumb items.
   */
  @Expose
  private List<BreadcrumbItem> items;

  /** Initialize the properties. */
  @PostConstruct
  private void init() {
    if (breadcrumb != null && CollectionUtils.isNotEmpty(breadcrumb.getItems())) {
      items =
          breadcrumb.getItems().stream()
              .map(
                  b -> {
                    final Link link = new Link();
                    link.setUrl(LinkUtils.transformUrl(
                            b.getLink().getMappedURL(),
                            runModeService.isPublishRunMode()));
                    return BreadcrumbItem.builder().link(link).title(b.getTitle()).build();
                  })
              .collect(Collectors.toList());
    }
  }

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

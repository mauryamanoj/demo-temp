package com.saudi.tourism.core.models.components.alerts;

import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = { Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class AlertsModel {

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Resource Resolver.
   */
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * Enable manual authoring.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Expose
  private Boolean enableManualAuthoring;

  /**
   * alert color.
   */
  @Expose
  private String alertColor;

  /**
   * alert text.
   */
  @Expose
  private String alert;

  @PostConstruct
  void init() {

    if (Boolean.FALSE.equals(enableManualAuthoring)) {
      final var pageManager = resourceResolver.adaptTo(PageManager.class);
      final var currentPage = pageManager.getContainingPage(currentResource);
      if (currentPage == null) {
        return;
      }

      final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
      if (StringUtils.isEmpty(cfPath)) {
        return;
      }

      final var cfResource = resourceResolver.getResource(cfPath);
      if (cfResource == null) {
        return;
      }

      final var cfModel = cfResource.adaptTo(AlertCFModel.class);
      if (cfModel == null) {
        return;
      }

      alertColor = cfModel.getAlertColor();
      alert = cfModel.getAlert();
    }

    if (Boolean.TRUE.equals(enableManualAuthoring)) {
      if (Objects.isNull(currentResource)) {
        return;
      }
      ValueMap valueMap = currentResource.getValueMap();
      alertColor = valueMap.get("alertColor", String.class);
      alert = valueMap.get("alert", String.class);
    }

    if (StringUtils.isNotEmpty(alert)) {
      alert = LinkUtils.rewriteLinksInHtml(alert, resourceResolver, runModeService.isPublishRunMode());
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
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

package com.saudi.tourism.core.models.components.specialshowwidget.v1;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SpecialShowWidgetModel.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
@Slf4j
public class SpecialShowWidgetModel {
  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;
  /**
   * /**
   * CFModel.
   */
  @Expose
  private SpecialShowWidgetCFModel cfModel;


  /**
   * specialShowItemList.
   */
  @Expose
  private List<SpecialShowItem> specialShowItemList = new ArrayList<>();


  @PostConstruct
  void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    if (Objects.isNull(pageManager)) {
      return;
    }
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (Objects.isNull(currentPage)) {
      return;
    }

    final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
    if (StringUtils.isBlank(cfPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfPath);
    if (Objects.isNull(cfResource)) {
      return;
    }

    cfModel = cfResource.adaptTo(SpecialShowWidgetCFModel.class);
    if (Objects.isNull(cfModel)) {
      return;
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this.cfModel);
  }
}

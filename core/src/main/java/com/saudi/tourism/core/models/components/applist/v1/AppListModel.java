package com.saudi.tourism.core.models.components.applist.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * App List Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class AppListModel {
  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * Display Size.
   */
  @ValueMapValue(name = "display/cardSize")
  @Expose
  private String variation;

  /**
   * App Cards List.
   */
  @ChildResource(name = "appListCards")
  @Expose
  private List<AppCardsModel> cards;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    if (CollectionUtils.isNotEmpty(cards)) {
      cards.stream()
          .filter(Objects::nonNull)
          .forEach(
              c -> {
                DynamicMediaUtils.setAllImgBreakPointsInfo(
                    c.getImage(),
                    "crop-600x600",
                    "crop-600x600",
                    "1280",
                    "420",
                    currentResource.getResourceResolver(),
                    currentResource);
              });
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

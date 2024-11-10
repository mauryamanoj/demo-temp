package com.saudi.tourism.core.models.components.neighborhoodssection.v1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class NeighborhoodsSectionModel {
  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;
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
  private Resource resource;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;


  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Logo.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * View All CTA.
   */
  @ValueMapValue
  @Expose
  private String viewAll;

  /**
   * Cards CF.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> cardCFPaths;

  /**
   * List of cards.
   */
  @Expose
  private List<NeighborhoodsSectionCard> cards;

  @PostConstruct
  void init() {
    if (CollectionUtils.isNotEmpty(cardCFPaths)) {
      cards = cardCFPaths.stream()
        .filter(StringUtils::isNotEmpty)
        .map(this::processCardPath)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    }
  }

  /**
   * Process card path.
   *
   * @param path path
   * @return NeighborhoodsSectionCard
   */
  private NeighborhoodsSectionCard processCardPath(String path) {
    final var contentFragment = resourceResolver.getResource(path);
    if (contentFragment == null) {
      return null;
    }
    final var card = contentFragment.adaptTo(NeighborhoodsSectionCard.class);
    if (card != null) {
      processImage(card.getImage());
      updateCardCtaLink(card);
    }
    return card;
  }

  /**
   * Process image.
   *
   * @param image
   */
  private void processImage(Image image) {
    if (image != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          image,
          CropEnum.CROP_375x210.getValue(),
          CropEnum.CROP_375x210.getValue(),
          "1280",
          "420",
          resourceResolver,
          resource);
    }
  }

  /**
   * Update card cta link.
   *
   * @param card
   */
  private void updateCardCtaLink(NeighborhoodsSectionCard card) {
    if (StringUtils.isNotBlank(card.getCtaLink())) {
      String updatedUrl = LinkUtils.getAuthorPublishUrl(
          resourceResolver,
          card.getCtaLink(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
      card.setCtaLink(updatedUrl);
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

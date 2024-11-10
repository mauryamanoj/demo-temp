package com.saudi.tourism.core.models.components.keepExploring.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Model for Keep Exploring Section component.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class KeepExploringSectionModel {
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
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * type of card.
   */
  @Expose
  private String  type = "ExploringCards";

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * ctaLink.
   */
  @ValueMapValue
  private String ctaLink;

  /**
   * ctaLabel.
   */
  @ValueMapValue
  private String ctaLabel;


  /**
   * link.
   */
  @Expose
  private Link link;


  /**
   * Destinations.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> destinationCFPaths;


  /**
   * List of Destination cards.
   */
  @Expose
  private List<DestinationCard> cards;

  @PostConstruct
  void init() {

    ctaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, ctaLink, runModeService.isPublishRunMode());
    link = new Link(ctaLink, ctaLabel, false);

    if (CollectionUtils.isNotEmpty(destinationCFPaths)) {
      cards =
        destinationCFPaths.stream()
          .filter(StringUtils::isNotEmpty)
          .map(
            p -> {
              final var destinationCF = resourceResolver.getResource(p);
              if (destinationCF == null) {
                return null;
              }
              final var destinationCFModel = destinationCF.adaptTo(DestinationCFModel.class);
              if (destinationCFModel == null) {
                return null;
              }

              return DestinationCard.fromCFModel(destinationCFModel, resource, resourceResolver, settingsService);
            })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());

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

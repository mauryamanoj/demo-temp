package com.saudi.tourism.core.models.components.stories.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.CommonUtils;
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
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Model for Latest stories component.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class LatestStoriesModel {
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
   * Title.
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
   * Stories.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> storyCFPaths;

  /**
   * List of Story cards.
   */
  @Expose
  private List<LatestStoryCard> cards;

  /**
   * View All link.
   */
  @Expose
  private Link link;

  /**
   * Sort.
   */
  @ChildResource
  private LatestStoriesSortModel sort;

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  @PostConstruct
  void init() {

    String language = CommonUtils.getLanguageForPath(resource.getPath());
    ctaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, ctaLink, runModeService.isPublishRunMode());
    link = new Link(ctaLink, ctaLabel, false);

    if (CollectionUtils.isNotEmpty(storyCFPaths)) {
      cards =
        storyCFPaths.stream()
          .filter(StringUtils::isNotEmpty)
          .map(
            p -> {
              final var storyCF = resourceResolver.getResource(p);
              if (storyCF == null) {
                return null;
              }
              final var storyCFModel = storyCF.adaptTo(StoryCFModel.class);

              return LatestStoryCard.fromCFModel(storyCFModel, resource, language);
            })
          .filter(Objects::nonNull)
          .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (Objects.nonNull(sort) && "publishedDate".equals(sort.getSortBy())) {
              list.sort((o1, o2) -> o2.getPublishedDate().compareTo(o1.getPublishedDate()));
            }
            return list;
          }));
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

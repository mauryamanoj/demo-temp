package com.saudi.tourism.core.models.components.highlightscards.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.gson.IsoCalendarAdapter;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class HighlightsCardsModel {

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
   * type HighlightedCards.
   */
  @Expose
  private final String  type = "HighlightedCards";

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Sub Title.
   */
  @ValueMapValue
  @Expose
  private String subTitle;

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
   * List of Story cards.
   */
  @Expose
  private List<HighlightsCard> cards;

  @PostConstruct
  void init() {

    if (CollectionUtils.isNotEmpty(cardCFPaths)) {
      cards =
          cardCFPaths.stream()
              .filter(StringUtils::isNotEmpty)
              .map(
                  p -> {
                    final var contentF = resourceResolver.getResource(p);
                    if (contentF == null) {
                      return null;
                    }
                    final var highlightsCard = contentF.adaptTo(HighlightsCard.class);

                    Image image;
                    if (highlightsCard != null) {
                      image = highlightsCard.getImage();

                      if (Objects.nonNull(image)) {
                        DynamicMediaUtils.setAllImgBreakPointsInfo(
                            image,
                            "crop-460x620",
                            "crop-460x620",
                            "1280",
                            "420",
                            resource.getResourceResolver(),
                            resource);
                      }

                      highlightsCard.setCardCtaLink(
                          LinkUtils.getAuthorPublishUrl(
                          resourceResolver,
                          highlightsCard.getCardCtaLink(),
                          settingsService.getRunModes().contains(Externalizer.PUBLISH)));

                    }

                    return highlightsCard;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder
       .excludeFieldsWithoutExposeAnnotation()
       .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Calendar.class, new IsoCalendarAdapter());
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

}

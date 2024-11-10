package com.saudi.tourism.core.models.components.contentfragment.destination;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Model for Destination content fragment.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationCFModel {
  /** tagManager. */
  @Inject private TagManager tagManager;
  /** language. */
  private String language = StringUtils.EMPTY;
  /**
   * Current resource.
   */
  @Self
  @JsonIgnore
  private transient Resource resource;

  /**
   * Resource Resolver.
   */
  @Inject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @Inject
  @JsonIgnore
  private transient SlingSettingsService settingsService;

  /**
   * Destination title.
   */
  @Expose
  private String title;

  /**
   * tagline for destination.
   */
  @Expose
  private String tagline;

  /**
   * Destination subtitle.
   */
  @Expose
  private String subTitle;

  /**
   * Banner Image Path.
   */
  @JsonIgnore
  private String bannerImagePath;

  /**
   * S7 Banner Image Path.
   */
  @JsonIgnore
  private String s7BannerImagePath;

  /**
   * Banner Image.
   */
  @Expose
  private Image bannerImage;

  /**
   * PopUp Image Path.
   */
  @JsonIgnore
  private String popUpImagePath;

  /**
   * S7 PopUp Image Path.
   */
  @JsonIgnore
  private String s7PopUpImagePath;

  /**
   * PopUp Image transparent.
   */
  @JsonIgnore
  private Boolean isPopUpImageTransparent;

  /**
   * PopUp Image.
   */
  @Expose
  private Image popUpImage;

  /**
   * Latitude.
   */
  @Expose
  private String latitude;

  /**
   * Longitude.
   */
  @Expose
  private String longitude;

  /**
   * Id.
   */
  @Expose
  private String id;

  /**
   * About heading.
   */
  @Expose
  private String aboutHeading;

  /**
   * About description.
   */
  @Expose
  private String aboutDescription;

  /**
   * Categories of the destination.
   */
  @JsonIgnore
  private String[] categoriesCFPaths;

  /**
   * Categories.
   */
  @Expose
  private List<Category> categories;

  /**
   * categoriesTags.
   */
  private List<String> categoriesTags;


  /**
   * Destination page path (corresponding to the current CF).
   */
  @JsonIgnore
  private String destinationPagePath;

  /**
   * Destination page.
   */
  @Expose
  private Link pagePath;

  /**
   * Destination id.
   */
  @JsonIgnore
  private String destinationId;

  @PostConstruct
  void init() {
    if (resource != null) {
      language = CommonUtils.getLanguageForPath(resource.getPath());
      destinationId = StringUtils.substringAfterLast(resource.getPath(), Constants.FORWARD_SLASH_CHARACTER);
      var masterNode =
          resourceResolver.getResource(resource.getPath() + Constants.JCR_CONTENT_DATA_MASTER);

      if (masterNode != null && masterNode.getValueMap() != null) {
        title = masterNode.getValueMap().get("title", String.class);
        subTitle = masterNode.getValueMap().get("subTitle", String.class);
        latitude = masterNode.getValueMap().get("lat", String.class);
        longitude = masterNode.getValueMap().get("lng", String.class);
        id = masterNode.getValueMap().get("id", String.class);
        aboutHeading = masterNode.getValueMap().get("aboutTitle", String.class);
        aboutDescription = masterNode.getValueMap().get("aboutDescription", String.class);
        String[] categoriesCfm = masterNode.getValueMap().get("categories", String[].class);
        categoriesTags = Arrays.asList(Optional.ofNullable(categoriesCfm).orElse(new String[]{}));
        destinationPagePath = masterNode.getValueMap().get("pagePath", String.class);
        tagline = masterNode.getValueMap().get("tagline", String.class);

        if (CollectionUtils.isNotEmpty(categoriesTags)) {
          this.categories = categoriesTags.stream()
            .map(p -> TagUtils.getTagOrChildren(p, tagManager, resourceResolver, language))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        }
        String[] imagesArray = masterNode.getValueMap().get("images", String[].class);
        final var images =
            Optional.ofNullable(imagesArray).map(Arrays::asList).orElse(Collections.emptyList());

        var firstImage =
            images.stream()
                .filter(
                    jsonString -> {
                      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                      return jsonObject != null && "image".equals(jsonObject.get("type").getAsString());
                    })
                .findFirst()
                .orElse(null);


        bannerImagePath = StringUtils.EMPTY;
        s7BannerImagePath = StringUtils.EMPTY;

        if (StringUtils.isNotEmpty(firstImage)) {
          final var imageJson = JsonParser.parseString(firstImage).getAsJsonObject();
          if (imageJson.has("image")) {
            bannerImagePath = imageJson.get("image").getAsString();
          }
          if (imageJson.has("s7image")) {
            s7BannerImagePath = imageJson.get("s7image").getAsString();
          }

          if (StringUtils.isNotEmpty(bannerImagePath)) {
            bannerImage = new Image();
            bannerImage.setFileReference(bannerImagePath);
            bannerImage.setMobileImageReference(bannerImagePath);
            bannerImage.setS7fileReference(s7BannerImagePath);
            bannerImage.setS7mobileImageReference(s7BannerImagePath);
          }
        }

        popUpImagePath = masterNode.getValueMap().get("popUpImage", String.class);
        s7PopUpImagePath = masterNode.getValueMap().get("s7popUpImage", String.class);
        if (StringUtils.isNotEmpty(popUpImagePath)) {
          popUpImage = new Image();
          popUpImage.setFileReference(popUpImagePath);
          popUpImage.setMobileImageReference(popUpImagePath);
          popUpImage.setS7fileReference(s7PopUpImagePath);
          popUpImage.setS7mobileImageReference(s7PopUpImagePath);
        }

        if (StringUtils.isNotEmpty(destinationPagePath)) {
          destinationPagePath = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), destinationPagePath,
              settingsService.getRunModes().contains(Externalizer.PUBLISH));
          pagePath = new Link();
          pagePath.setUrl(destinationPagePath);
        }
      }
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

package com.saudi.tourism.core.models.components.contentfragment.story;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This is the Sling Model class for the Content Fragment component for Story.
 * The {@link Model} annotation allows us to register the class as a Sling Model.
 */
@Model(adaptables = {Resource.class,
    ContentFragment.class}, adapters = StoryCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class StoryCFModel {

  /** Resource Resolver. */
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * Run Mode Service.
   */
  @Inject
  private transient RunModeService runModeService;

  /**
   * Story ContentFragment Model.
   */
  @Self
  private ContentFragment contentFragment;

  /**
   * Current resource.
   */
  @Self
  private Resource resource;

  /**
   * Story page path (corresponding to the current CF).
   */
  private String storyPagePath;
  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @Inject
  private transient SlingSettingsService settingsService;

  /**
   * Story page link.
   */
  @Expose
  private Link link;

  /**
   * Tags.
   */
  @Expose
  private List<String> tags;

  /**
   * favId.
   */
  private String favId;

  /**
   * Created Date.
   */
  @ValueMapValue(name = "jcr:created")
  private transient Calendar createdDate;

  /**
   * Last Replicated Date.
   */
  @ValueMapValue(name = "jcr:content/cq:lastReplicated")
  private transient Calendar lastReplicatedDate;
  /**
   * Page Path.
   */
  @JsonIgnore
  private String internalPagePath;

  public String getTitle() {
    return getElementValue("title", String.class);
  }

  public String getSubtitle() {
    return getElementValue("subtitle", String.class);
  }

  public String getAboutDescription() {
    return getElementValue("aboutDescription", String.class);
  }

  public String getAboutTitle() {
    return getElementValue("aboutTitle", String.class);
  }

  public Boolean isHideFavorite() {
    return getElementValue("hideFavorite", Boolean.class);
  }

  public Double getLatitude() {
    return getElementValue("lat", Double.class);
  }

  public Double getLongitude() {
    return getElementValue("lng", Double.class);
  }

  public String[] getCategoriesTags() {
    return getElementValue("categories", String[].class);
  }

  public DestinationCFModel getDestination() {
    String destinationCFPath = getElementValue("destination", String.class);
    if (StringUtils.isEmpty(destinationCFPath)) {
      return null;
    }

    Resource destinationCF = resourceResolver.getResource(destinationCFPath);
    if (destinationCF != null) {
      return destinationCF.adaptTo(DestinationCFModel.class);
    } else {
      return null;
    }
  }

  public String[] getImages() {
    return getElementValue("images", String[].class);
  }


  /**
   * Get the list of image banners.
   * @return list of image banners.
   */
  public List<ImageBanner> getImageBanners() {
    String[] images = getElementValue("images", String[].class);

    if (images == null || images.length == 0) {
      return Collections.emptyList();
    }

    ObjectMapper mapper = new ObjectMapper();
    return Arrays.stream(images)
      .map(imageJson -> {
        try {
          ImageBanner imageBanner = mapper.readValue(imageJson, ImageBanner.class);
          if (StringUtils.isNotEmpty(imageBanner.getImage())) {
            Image image = new Image();
            image.setFileReference(imageBanner.getImage());
            image.setMobileImageReference(imageBanner.getImage());
            image.setS7fileReference(imageBanner.getS7image());
            image.setS7mobileImageReference(imageBanner.getS7image());
            imageBanner.setImageBanner(image);
          }
          return imageBanner;
        } catch (JsonProcessingException e) {
          LOGGER.warn("Error while parsing imageBanner", e);
          return null;
        }
      })
      .filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * Get the published date.
   * @return published date.
   */
  public Calendar getPublishedDate() {
    if (runModeService.isPublishRunMode()) {
      // on publish, use createdDate (lastReplicatedCal is not available).
      return createdDate;
    } else {
      // on author, use lastReplicated date if exists, else use createdDate.
      return Optional.ofNullable(lastReplicatedDate).orElse(createdDate);
    }
  }

  private <T> T getElementValue(String elementName, Class<T> type) {
    if (contentFragment.hasElement(elementName)) {
      FragmentData elementData = contentFragment.getElement(elementName).getValue();
      if (elementData != null) {
        return elementData.getValue(type);
      }
    }
    return null;
  }

  @PostConstruct
  void init() {
    internalPagePath = getElementValue("pagePath", String.class);
    storyPagePath = getElementValue("pagePath", String.class);
    if (StringUtils.isNotEmpty(storyPagePath)) {
      //should be done before rewriting pagePath
      favId = LinkUtils.getFavoritePath(storyPagePath);
      storyPagePath = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), storyPagePath,
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
      link = new Link();
      link.setUrl(storyPagePath);
      String language = CommonUtils.getLanguageForPath(resource.getPath());
      if (getCategoriesTags() != null) {
        tags = Arrays.stream(getCategoriesTags())
                     .map(tag -> CommonUtils.getTagNameFromCategory(tag, resourceResolver, language))
                     .collect(Collectors.toList());
      }
    }
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ImageBanner {

    /**
     * Type.
     */
    @Expose
    private String type;

    /**
     * image.
     */
    private String image;

    /**
     * s7image.
     */
    private String s7image;

    /**
     * video.
     */
    private String video;

    /**
     * s7video.
     */
    private String s7video;

    /**
     * thumbnail.
     */
    private String thumbnail;


    /**
     * image.
     */
    @Expose
    private Image imageBanner;

    /**
     * featureInSubStory.
     */
    @Expose
    private String featureInSubStory;

    public ImageBanner() {
    }

  }
}

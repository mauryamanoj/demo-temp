package com.saudi.tourism.core.models.components.contentfragment.article;

import java.util.Calendar;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * This is the Sling Model class for the Content Fragment component for Article. The {@link
 * Model} annotation allows us to register the class as a Sling Model.
 */
@Model(
    adaptables = {Resource.class},
    adapters = ArticleCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ArticleCFModel {

  /**
   * Current resource.
   */
  @Self
  @JsonIgnore
  private transient Resource currentResource;

  /** Resource Resolver. */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Article Id.
   */
  private String id;

  /**
   * Title.
   */
  private String title;

  /**
   * Splash Image.
   */
  private String splashImage;

  /**
   * S7 Splash Image.
   */
  private String s7splashImage;

  /**
   * Author.
   */
  private String author;

  /**
   * Publishing Date.
   */
  private transient Calendar publishingDate;

  /**
   * Time to Read Minutes.
   */
  private String timeToReadMinutes;

  /**
   * Time to Read Suffix.
   */
  private String timeToReadSuffix;

  /**
    * Tags.
   */
  private String[] tags;

  /**
   * Content.
   */
  private String content;

  /**
   * Category.
   */
  private String category;

  @PostConstruct
  public void init() {

    if (currentResource != null) {
      var masterNode = resourceResolver.getResource(currentResource.getPath() + Constants.JCR_CONTENT_DATA_MASTER);
      if (masterNode != null) {
        id = currentResource.getName();
        title = masterNode.getValueMap().get("title", String.class);
        splashImage = masterNode.getValueMap().get("splashImage", String.class);
        s7splashImage = masterNode.getValueMap().get("s7splashImage", String.class);
        author = masterNode.getValueMap().get("author", String.class);
        publishingDate = masterNode.getValueMap().get("publishingDate", Calendar.class);
        timeToReadMinutes = masterNode.getValueMap().get("timeToReadMinutes", String.class);
        timeToReadSuffix = masterNode.getValueMap().get("timeToReadSuffix", String.class);
        tags = masterNode.getValueMap().get("tags", String[].class);
        content = masterNode.getValueMap().get("content", String.class);
        category = masterNode.getValueMap().get("category", String.class);
      }
    }
  }
}

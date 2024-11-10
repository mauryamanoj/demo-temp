package com.saudi.tourism.core.models.exporter;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * The Saudi Content Page Sling Model Exporter.
 */
@Model(adaptables = Resource.class,
       resourceType = {"sauditourism/components/structure/page"},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson",
          extensions = "json",
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@Slf4j
public class PageFavoritesExporter {

  /**
   * The navImage.
   */
  @Setter
  @Getter
  @ValueMapValue(name = "featureImage")
  private String cardImage;
  /**
   * The title.
   */
  @Setter
  @Getter
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  private String title;
  /**
   * The altImage.
   */
  @Setter
  @Getter
  @ValueMapValue
  private String altImage;
  /**
   * The path.
   */
  @Getter
  private String path;
  /**
   * The link.
   */
  @Getter
  private String link;
  /**
   * The link.
   */
  @Getter
  private String favId;

  /**
   * The favCategory.
   */
  @Getter
  private String favCategory;
  /**
   * The Resource.
   */
  @Inject
  private Resource resource;
  /**
   * The saudiModeConfig.
   */
  @Inject
  private SaudiModeConfig saudiModeConfig;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    try {
      if (resource != null) {
        String pagePath = resource.getParent().getPath();
        path = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(),
            pagePath, saudiModeConfig.getPublish());
        link = path;
        favId = LinkUtils.getFavoritePath(pagePath);
        favCategory = SearchUtils.getFavCategory(resource);

      }

    } catch (Exception ex) {
      LOGGER.error(" Error in PageFavoritesExporter");
    }
  }
}

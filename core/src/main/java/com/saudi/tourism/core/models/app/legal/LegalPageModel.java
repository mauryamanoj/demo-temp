package com.saudi.tourism.core.models.app.legal;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.app.common.AppBaseModel;
import com.saudi.tourism.core.models.app.page.Search;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.TextModel;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.List;

/**
 * Legal data model.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.APP_LEGAL_PAGE_RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})
@ToString
public class LegalPageModel extends AppBaseModel {

  /**
   * Main title.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Named(JcrConstants.JCR_TITLE)
  private String title;


  /**
   * Main page description.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Named(JcrConstants.JCR_DESCRIPTION)
  private String description;


  /**
   * Preview image.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String image;

  /**
   * Preview image caption.
   */
  @Getter
  @Setter
  @ChildResource
  private ImageCaption imageCaption;

  /**
   * Text data.
   */
  @Getter
  @Setter
  @ChildResource
  private List<TextModel> texts;

  /**
   * Search Tags.
   */
  @Getter
  @Setter
  @ChildResource
  private Search search;
  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    setWebMappingPath(LinkUtils.getFavoritePath(getWebMappingPath()));

  }

}

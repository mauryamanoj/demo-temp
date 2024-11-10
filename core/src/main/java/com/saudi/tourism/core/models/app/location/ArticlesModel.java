package com.saudi.tourism.core.models.app.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ArticlesModel {

  /**
   * ID.
   */
  @Setter
  private String id;

  /**
   * Title.
   */
  @JsonIgnore
  @ValueMapValue
  private String titleEnglish;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Image reference.
   */
  @ValueMapValue
  private String image;

  /**
   * Articles Caption.
   */
  @ChildResource
  private ArticlesCaptionModel caption;

  /**
   * Thumbnail.
   */
  @ValueMapValue
  private String thumbnail;

  /**
   * Items for activities.
   */
  @ChildResource
  private ArticlesExtraInfoTipsModel extraInfo;

  /**
   * introduction.
   */
  @ValueMapValue
  private String introduction;

  /**
   * Articles Details.
   */
  @ChildResource
  private List<ArticlesDetailsModel> details;

  /**
   * Articles Tips.
   */
  @ChildResource
  private ArticlesExtraInfoTipsModel tips;

  /**
   * PostConstruct method.
   */
  @PostConstruct private void init() {
    if (Objects.nonNull(titleEnglish)) {
      setId(AppUtils.stringToID(titleEnglish, false));
    }
  }
}

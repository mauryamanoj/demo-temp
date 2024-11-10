package com.saudi.tourism.core.models.components.title.v1;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.swing.text.html.HTML;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.saudi.tourism.core.utils.SaudiConstants.DEFAULT_CSS_MARGIN_CLASS;


/**
 * This class contains the C02-Title Intro component details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TitleModel {

  /**
   * Component Main title.
   */
  @ValueMapValue
  @Expose
  private String mainTitle;

  /**
   * Component desc Below Title.
   */
  @ValueMapValue
  @Expose
  private String descBelowTitle;

  /**
   * Read more Button.
   */
  @ValueMapValue
  @Expose
  private String readMoreButton;

  /**
   * Read Less Button.
   */
  @ValueMapValue
  @Expose
  private String readLessButton;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String mainTitleWeight;

  /**
   * Component Intro title.
   */
  @ValueMapValue
  @Expose
  private String introTitle;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String introTitleWeight;

  /**
   * Component Intro subtitle.
   */
  @ValueMapValue
  @Expose
  private String introSubtitle;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String introSubtitleWeight;

  /**
   * Component Intro description.
   */
  @ValueMapValue
  @Expose
  private String introDescription;

  /**
   * categoryTags.
   */
  @ValueMapValue
  @Expose
  private String[] categoryTags;

  /**
   * css margin class.
   */
  @ValueMapValue
  @Expose
  private String cssMarginClass;

  /**
   * List of FAQs.
   */
  @ChildResource
  @Expose
  private List<Faq> faqs;

  /**
   * currentResource.
   */
  @Self
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * Flag indicating that this is an event page.
   */
  @Expose
  private boolean event;


  /**
   * Event Description.
   */
  @Expose
  private String eventDescription;

  /**
   * Initialize the properties.
   */
  @PostConstruct protected void init() {
    this.event =
      CommonUtils.getContainingPage(currentResource)
        .map(Page::getContentResource)
        .map(resource -> resource.isResourceType(Constants.EVENT_DETAIL_RES_TYPE))
        .orElse(false);
    ResourceResolver resolver = currentResource.getResourceResolver();
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    Locale locale = new Locale(lang);
    categoryTags = Optional.ofNullable(categoryTags)
      .map(Arrays::stream).orElse(Stream.empty())
      .map(tagManager::resolve).filter(Objects::nonNull).map(tag -> tag.getTitle(locale))
      .toArray(String[]::new);

    // To handle old authored components with no cssMarginClass
    if (StringUtils.isEmpty(cssMarginClass)) {
      cssMarginClass = DEFAULT_CSS_MARGIN_CLASS;
    }
    // Set h3 as default heading for for mainTitleWeight
    if (StringUtils.isEmpty(mainTitleWeight)) {
      mainTitleWeight = HTML.Tag.H3.toString();
    }

    // Set h4 as default heading for for introTitleWeight
    if (StringUtils.isEmpty(introTitleWeight)) {
      introTitleWeight = HTML.Tag.H4.toString();
    }

    // Set h3 as default heading for for introSubtitleWeight
    if (StringUtils.isEmpty(introSubtitleWeight)) {
      introSubtitleWeight = HTML.Tag.H3.toString();
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

package com.saudi.tourism.core.models.components.title.v2;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.components.title.v1.TitleModel;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jsoup.Jsoup;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class contains the C02-Title Intro component details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class EventTitleModel extends TitleModel {

  /**
   * currentResource.
   */
  @Self
  private Resource currentResource;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * Initialize the properties.
   */
  @PostConstruct
  @Override
  protected void init() {
    PageManager pageManager = currentResource.getResourceResolver().adaptTo(PageManager.class);
    Page page = pageManager.getContainingPage(currentResource);
    Resource jcrContent = page.getContentResource();
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));
    if (Objects.nonNull(jcrContent)) {
      ValueMap valueMap = jcrContent.getValueMap();
      setCategoryTags(valueMap.get("categoryTags", String[].class));
      String eventDescription = valueMap.get("copy", String.class);

      if (!StringUtils.startsWith(eventDescription, "<p>")) {
        setIntroDescription("<p>" + eventDescription + "</p>");
      } else {
        setIntroDescription(eventDescription);
      }

      if (StringUtils.isNotBlank(eventDescription)) {
        setEventDescription(StringEscapeUtils.escapeHtml(Jsoup.parse(eventDescription).text()));
      }

    }
    setMainTitle(CommonUtils.getI18nString(i18nBundle, "About this event"));
    super.init();
  }
}

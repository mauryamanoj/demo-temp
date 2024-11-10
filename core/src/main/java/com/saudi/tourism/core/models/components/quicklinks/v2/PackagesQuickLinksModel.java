package com.saudi.tourism.core.models.components.quicklinks.v2;


import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.quicklinks.v1.QuickLinksModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * C-01 Quick Links Model for packages.
 *
 * @author cbarrios
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PackagesQuickLinksModel extends QuickLinksModel {

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * init method for slingModel.
   */
  @PostConstruct
  @Override
  protected void init() {
    processPageTitles();
  }

  /**
   * Get the page titles.
   */
  @Override
  protected void processPageTitles() {
    if (Objects.nonNull(i18nProvider)) {
      String language = CommonUtils
          .getPageNameByIndex(getResource().getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
      if (Objects.isNull(getLinks())) {
        setComponentTitle(CommonUtils.getI18nString(i18nBundle, "Book this experience"));
        Link dmcLink = new Link();
        String vendor = CommonUtils
            .getPageNameByIndex(getResource().getPath(), Constants.AEM_PACKAGES_DMC_PATH_POSITION);
        dmcLink.setCopy(i18nBundle.getString(vendor));
        dmcLink.setUrlWithExtension(
            LinkUtils.getUrlWithExtension(i18nBundle.getString(vendor + "-link")));
        setLinks(Collections.singletonList(dmcLink));
      }
    }
  }

}

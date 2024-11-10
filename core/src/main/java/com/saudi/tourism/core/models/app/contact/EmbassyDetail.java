package com.saudi.tourism.core.models.app.contact;

import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Embassy detail model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EmbassyDetail implements Serializable {

  /**
   * Name.
   */
  @ValueMapValue
  @Getter
  private String name;

  /**
   * Phone.
   */
  @ValueMapValue
  @Getter
  private String phone;

  /**
   * Country code.
   */
  @ValueMapValue
  @Getter
  private String countryCode;

  /**
   * Continent.
   */
  @ValueMapValue
  @Getter
  private String continent;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {

    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);

    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    if (continent != null) {
      continent = i18nBundle.getString(continent);
    }
  }

}

package com.saudi.tourism.core.models.components.hotels;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Class HotelsSpecialOffers.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HotelsSpecialOffers implements Serializable {

  /**
   * The details.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String[] details;

  /**
   * The title.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String title;

  /**
   * The subtitle.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String subTitle;

  /**
   * The ctaText.
   */
  @Expose
  @ValueMapValue
  @Getter
  @Setter
  private String ctaText;


  /**
   * offerCtaKey for native App.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String offerCtaKey;


  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
            .orElse(StringUtils.EMPTY);
    try {
      String language = CommonUtils.getLanguageForPath(path);
      if (Objects.nonNull(i18nProvider) && !ArrayUtils.isEmpty(details)) {
        // update ctaText to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        setCtaText(getI18nString(i18nBundle, "viewoffers"));
      }
    } catch (Exception e) {
      LOGGER.error("Error in HotelSpecialOffers class ", e);
    }
  }

  /**
   * Gets i 18 n string.
   *
   * @param i18nBundle the 18 n bundle
   * @param key        the key
   * @return the i 18 n string
   */
  private String getI18nString(final ResourceBundle i18nBundle, final String key) {
    if (Objects.nonNull(key)) {
      return i18nBundle.getString(key);
    }
    return null;
  }

}

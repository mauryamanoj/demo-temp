package com.saudi.tourism.core.models.components.listcomponent.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class contains the table item info details.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ListTableItem implements Serializable {

  /**
   * Variable table item key.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Expose
  private String key;

  /**
   * Variable table item en key.
   */
  @Getter
  @Expose
  private String enKey;

  /**
   * Variable table item value.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String value;

  /**
   * Variable table item icon.
   */
  @Getter
  @Setter
  @Expose
  private String icon;

  /**
   * ResourceBundleProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  @JsonIgnore
  private transient ResourceBundleProvider i18nProvider;

  /**
   * resource.
   */
  @Self
  @JsonIgnore
  private transient Resource resource;

  /**
   * Post Construct method.
   */
  @PostConstruct
  private void init() {
    if ("King".equals(key)) {
      setIcon("flag");
    } else if ("Capital".equals(key)) {
      setIcon("pin");
    } else if ("Population".equals(key)) {
      setIcon("people3");
    } else if ("Currency".equals(key)) {
      setIcon("currency-circle");
    }
    if (StringUtils.isNotBlank(key) && null != resource) {
      this.enKey = key;
      String language = CommonUtils.getLanguageForPath(resource.getPath());
      if (Objects.nonNull(i18nProvider)) {
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        setKey(i18nBundle.getString(key));
      }
    }
  }
}

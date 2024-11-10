package com.saudi.tourism.core.models.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_READ_MORE;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_READ_LESS;
/**
 * Class for Welcome Intro component.
 */

@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class WelcomeIntroModel {

  /**
  * Variable for main title.
  */
  @Expose
  @ValueMapValue
  private String mainTitle;

  /**
   * Variable for sub title.
   */
  @Expose
  @ValueMapValue
  private String subTitle;

  /**
   * Variable for description.
   */
  @Expose
  @ValueMapValue
  private String description;

  /**
   * Variable for read more.
   */
  @Expose
  @Setter
  private String readMore;

  /**
   * Variable for read less.
   */
  @Expose
  @Setter
  private String readLess;

  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
      injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * init.
   */
  @PostConstruct
  public void init() {
    final String language = CommonUtils.getLanguageForPath(currentResource.getPath());
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    setReadMore(i18nBundle.getString(I18_KEY_READ_MORE));
    setReadLess(i18nBundle.getString(I18_KEY_READ_LESS));
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

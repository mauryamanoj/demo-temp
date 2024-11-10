package com.saudi.tourism.core.models.app.i18n;

import com.adobe.cq.export.json.ExporterConstants;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**AppTradPageModel
 * App Trad  page model.
 */
@Model(adaptables = {Resource.class },
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = {Constants.APP_TRAD_PAGE_RESOURCE_TYPE})

@Exporter(selector = "model",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS",
    value = Constants.STR_TRUE)})

public class AppTradPageModel implements Serializable {

  /**
   * List of contact.
   */
  @Getter
  @Setter
  @ChildResource
  @Named("rootapptrad")
  private transient List<AppTradLabel> listAppTradLabels;
  /**
   * Selector for IOS.
   */
  private static final String SELECTOR_IOS = "ios";

  /**
   * Selector for Android.
   */
  private static final String SELECTOR_ANDROID = "android";
  /**
   * Original pattern for variables.
   */
  private static final String ORIGNAL_PATTERN = "\\{\\{([0-9])\\}\\}";

  /**
   * IOS variable.
   */
  private static final String IOS_PATTERN = "%@";

  /**
   * ANDROID PATTERN.
   */
  private static final String ANDROID_PATTERN = "%$1\\$s";
  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

  }

  /**
   * Transform to Android/IOS format.
   *
   * @param rawValue original value
   * @param target target
   * @return transformedValue the transformed string
   */
  private String getTradValue(final String rawValue, String target) {

    String transformedValue = rawValue;

    if (!transformedValue.isEmpty()) {
      transformedValue = StringEscapeUtils.unescapeHtml(transformedValue);
      if (SELECTOR_ANDROID.equals(target)) {
        transformedValue = transformedValue.replaceAll(ORIGNAL_PATTERN, ANDROID_PATTERN);
      }
      if (SELECTOR_IOS.equals(target)) {
        transformedValue = transformedValue.replaceAll(ORIGNAL_PATTERN, IOS_PATTERN);
      }

    }
    return transformedValue;
  }


  /**
   * Map data.
   * @param target target
   * @return Map Map
   */
  public Map<String, String> getMap(String target) {

    return listAppTradLabels.stream().collect(
        Collectors.toMap(AppTradLabel::getKeyAppTrad,
            (apptradlabel) -> getTradValue(apptradlabel.getValueAppTrad(), target),
            (prev, next) -> next));
  }

}

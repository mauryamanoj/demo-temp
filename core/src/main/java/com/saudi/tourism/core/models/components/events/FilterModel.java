package com.saudi.tourism.core.models.components.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Class FilterModel.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
@Slf4j
public class FilterModel extends BaseFilterModel {

  /**
   * The placeHolderText.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "placeholder")
  @Expose
  private String placeHolderText;

  /**
   * The unit.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "unit")
  @Expose
  private String unit;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    try {
      setTypeGroup("checkbox-group");
      if ("price".equals(getType())) {
        setTypeGroup("range-slider");
      }

      if (Objects.isNull(getIcon())) {
        setIcon("location");
      }
      if (Objects.nonNull(getI18nProvider())) { // update city to i18n value
        String path = Optional.ofNullable(getCurrentResource().getParent()).map(Resource::getPath)
            .orElse(StringUtils.EMPTY);
        String language =
            CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        ResourceBundle i18nBundle = getI18nProvider().getResourceBundle(new Locale(language));
        unit = CommonUtils.getI18nString(i18nBundle, "SAR");
      }
    } catch (Exception e) {
      LOGGER.error("Error in FilterModel {}", e);
    }

  }

}

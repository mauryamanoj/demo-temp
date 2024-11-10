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

import static com.saudi.tourism.core.utils.Constants.DATE_FORMAT_DD_MM_YY;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_DETAIL_DATE_FROM;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_DETAIL_DATE_TO;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SHOW_ALL;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SHOW_LESS;

/**
 * The Class FilterModel.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
@Slf4j
public class EventsFilterModel extends BaseFilterModel {

  /**
   * The filterTitle.
   */
  @ValueMapValue
  @Getter
  @SerializedName(value = "datePickerTitle")
  @Expose
  private String placeHolderText;

  /**
   * The date placeholder.
   */
  @Getter
  @SerializedName(value = "placeholder")
  @Expose
  private DatePlaceholder datePlaceholder;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    try {
      setTypeGroup("checkbox-group");
      if ("date".equals(getType())) {
        datePlaceholder = new DatePlaceholder();
        datePlaceholder.setStartDate(DATE_FORMAT_DD_MM_YY);
        datePlaceholder.setEndDate(DATE_FORMAT_DD_MM_YY);
        datePlaceholder.setSelectDate(placeHolderText);
        datePlaceholder.setCustom("custom");
        setTypeGroup("checkbox-group-with-date");
        setIcon("calendar");
      } else if ("season".equals(getType())) {
        setIcon("calendar");
      } else if ("freePaid".equals(getType())) {
        setIcon("money");
        setTypeGroup("checkbox-toggle");
      } else if ("city".equals(getType())) {
        setIcon("location");
      } else if ("category".equals(getType())) {
        setIcon("interest");
      } else if ("target".equals(getType())) {
        setIcon("user");
      }

      if (Objects.isNull(getIcon())) {
        setIcon("location");
      }
      setShowAllButton(new ButtonsModel());
      if (Objects.nonNull(getI18nProvider())) {
        String path = Optional.ofNullable(getCurrentResource().getParent()).map(Resource::getPath)
            .orElse(StringUtils.EMPTY);
        String language =
            CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        ResourceBundle i18nBundle = getI18nProvider().getResourceBundle(new Locale(language));
        getShowAllButton().setShowAllLabel(CommonUtils.getI18nString(i18nBundle, I18_KEY_SHOW_ALL));
        getShowAllButton().setShowLessLabel(CommonUtils.getI18nString(i18nBundle, I18_KEY_SHOW_LESS));

        if (datePlaceholder != null) {
          datePlaceholder.setStartDate(CommonUtils.getI18nString(i18nBundle, I18_KEY_DETAIL_DATE_FROM));
          datePlaceholder.setEndDate(CommonUtils.getI18nString(i18nBundle, I18_KEY_DETAIL_DATE_TO));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in FilterModel", e);
    }
  }
}

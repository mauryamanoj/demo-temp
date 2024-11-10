package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Class PackageDetail.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PackageInfo implements Serializable {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String title;
  /**
   * The icon.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String icon;

  /**
   * List of links.
   */
  @ChildResource(name = "details")
  @Getter
  private List<PackageInfoItem> items;

  /**
   * List of details.
   */
  @Getter
  @Expose
  private List<String> details;
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
    Resource contentResource = currentResource.getParent();
    if (Objects.nonNull(i18nProvider)) { // update title to i18n value
      String path = Optional.ofNullable(contentResource).map(Resource::getPath)
          .orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

      title = CommonUtils.getI18nString(i18nBundle, title);

      details = new ArrayList<>();
      if (items != null) {
        for (PackageInfoItem item : items) {
          details.add(item.getText());
        }
      } else {
        fillDefaultDisclaimers(i18nBundle, contentResource);
      }
    }
  }

  /**
   * Fill Default disclaimer for prices.
   *
   * @param i18nBundle      I18nBundle.
   * @param contentResource content resource
   */
  private void fillDefaultDisclaimers(final ResourceBundle i18nBundle,
      final Resource contentResource) {
    if (Objects.nonNull(currentResource) && Objects.nonNull(contentResource) && currentResource
        .getName().equals("price-info")) {
      ValueMap properties = contentResource.getValueMap();
      String price = properties.get("price", String.class);
      if (StringUtils.isNotBlank(price)) {
        details.add(StringUtils
            .replaceEach(CommonUtils.getI18nString(i18nBundle, Constants.STARTING_FROM_I18KEY),
                new String[] {"{0}"}, new String[] {price}));
        details.add(CommonUtils.getI18nString(i18nBundle, "packageAvailabilityDisclaimer"));
        details.add(CommonUtils.getI18nString(i18nBundle, "termConditionsDisclaimer"));
      }
    }
  }
}

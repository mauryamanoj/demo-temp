package com.saudi.tourism.core.models.components.packages;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.hero.v1.CommonHeroModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.PN_BANNER_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_BOOK_NOW;

/**
 * H-01 Brand Page Hero Model.
 *
 * @author cbarrios
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PackagePageHeroModel extends CommonHeroModel {

  /**
   * The Current page.
   */
  @JsonIgnore
  @ScriptVariable
  private transient Page currentPage;

  /**
   * useSticky.
   */
  @Expose
  private boolean useSticky;

  /**
   * The Booking page url for APP.
   */
  @Expose
  private String bookNow;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    try {
      ValueMap pageProps = currentPage.getProperties();

      setTitle(getPropertyValue(pageProps, JcrConstants.JCR_TITLE));
      Image image = new Image();
      image.setAlt(getTitle());
      bookNow = getPropertyValue(pageProps, PN_BOOK_NOW);
      String banner = getPropertyValue(pageProps, PN_BANNER_IMAGE);
      if (Objects.nonNull(banner) && banner.contains("scth/ugc")) {
        banner = banner.replace("http://", "https://") + "?scl=1";
      }
      image.setFileReference(banner);
      image.setMobileImageReference(getPropertyValue(pageProps, "cardImage"));
      setImage(image);

      setUrlSlingExporter(LinkUtils.getFavoritePath(currentPage.getPath()));

      String vendorName = CommonUtils.getVendorName(currentPage);
      String packageName = CommonUtils.getPackageName(currentPage);

      // analytics data
      setAppEventData(CommonUtils.getAnalyticsEventData(Constants.DEFAULT_TOUR_PACKAGE_HERO_EVENT,
          LinkUtils.getAppFormatUrl(getItineraryPath()),
          StringUtils.defaultIfBlank(getItineraryCtaTitle(), getCreateItineraryLabel()), vendorName,
          packageName));
      // always sticky for package detail page
      this.useSticky = true;
    } catch (Exception e) {
      LOGGER.error(" Error in PackagePageHeroModel", e);
    } finally {
      // Cleanup
      this.currentPage = null;
    }
  }

  /**
   * Gets property value.
   *
   * @param pageProps the page props
   * @param key       the key
   * @return the property value
   */
  private String getPropertyValue(final ValueMap pageProps, final String key) {
    if (pageProps.containsKey(key)) {
      return pageProps.get(key).toString();
    }
    return null;
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

package com.saudi.tourism.core.models.components.hero.v1;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.events.StickyEventModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EventDetailHero {

  /**
   * The Current page.
   */
  @ScriptVariable
  private transient Page currentPage;

  /**
   * Hero Model.
   */
  @Self
  private CommonHeroModel heroModel;

  /**
   * Event Details.
   */
  @Getter(AccessLevel.NONE)
  private StickyEventModel eventDetail;

  /**
   * ResourceBundleProvider.
   */
  @OSGiService(
      filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
      injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

    final Resource contentResource = currentPage.getContentResource();
    if (Objects.nonNull(contentResource)) {
      eventDetail = contentResource.adaptTo(StickyEventModel.class);
    }


    if (heroModel != null && heroModel.getImage() == null) {
      heroModel.setImage(new Image());
    }

    // If images are not provided by the hero authoring
    // We will override the  hero images with the event images
    if (heroModel != null && heroModel.getImage() != null) {
      if (StringUtils.isEmpty(heroModel.getImage().getFileReference())
          && StringUtils.isNotEmpty(eventDetail.getFeatureEventImage())) {
        heroModel.getImage().setFileReference(eventDetail.getFeatureEventImage());
      }
      if (StringUtils.isEmpty(heroModel.getImage().getMobileImageReference())
          && StringUtils.isNotEmpty(eventDetail.getFeatureEventMobileImage())) {
        heroModel.getImage().setMobileImageReference(eventDetail.getFeatureEventMobileImage());
      }
    }

    if (heroModel != null && StringUtils.isEmpty(heroModel.getTitle())) {
      heroModel.setTitle(eventDetail.getTitle());
    }

    if (heroModel != null && StringUtils.isEmpty(heroModel.getSubtitle())) {
      heroModel.setSubtitle(eventDetail.getSubtitle());
    }

    final String language = CommonUtils.getLanguageForPath(currentPage.getPath());
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

    if (heroModel != null && StringUtils.isNotEmpty(eventDetail.getSeason())) {
      heroModel.setSeason(i18nBundle.getString(eventDetail.getSeason()));
    }

    if (heroModel != null && StringUtils.isEmpty(eventDetail.getSeason())) {
      heroModel.setSeason(eventDetail.getNoSeasonTitle());
    }
  }
}

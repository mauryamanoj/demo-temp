package com.saudi.tourism.core.models.components.contentfragment.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.login.UserApiModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;


@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class UserMenuCFModel {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * The Resource.
   */
  @Self
  private Resource resource;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * Register label.
   */
  @Expose
  @ValueMapValue
  private String registerLabel;

  /**
   * Ornament.
   */
  @Expose
  @ValueMapValue
  private String ornament;

  /**
   * Sign Out label.
   */
  @Expose
  @ValueMapValue
  private String signOutLabel;

  /**
   * Welcome label.
   */
  @Expose
  private String welcomeLabel;

  /**
   * View Profile label.
   */
  @Expose
  @ValueMapValue
  private String viewProfileLabel;

  /**
   * View Profile CTA.
   */
  @Expose
  @ValueMapValue
  private String viewProfileCTA;

  /**
   * Login Modal.
   */
  @Self
  @Expose
  private UserApiModel userApi;

  /**
   * User Sub Menu.
   */
  @ValueMapValue
  private List<String> userSubMenu;

  /** The links. */
  @Expose
  private List<UserLink> subMenu = new ArrayList<>();

  @PostConstruct
  public void init() {
    final var language = CommonUtils.getLanguageForPath(resource.getPath());
    if (Objects.nonNull(i18nProvider)) {
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));
      welcomeLabel = CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_WELCOME);
    }
    if (StringUtils.isNotEmpty(viewProfileCTA)) {
      viewProfileCTA = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), viewProfileCTA,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
    if (CollectionUtils.isNotEmpty(userSubMenu)) {
      subMenu = userSubMenu.stream()
        .map(userLink -> {
          ObjectMapper mapper = new ObjectMapper();
          try {
            UserLink userMenuLink = mapper.readValue(userLink, UserLink.class);
            // map the link url
            if (StringUtils.isNotEmpty(userMenuLink.getUrl())) {
              userMenuLink.setUrl(LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), userMenuLink.getUrl(),
                  settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            }
            return userMenuLink;
          } catch (JsonProcessingException e) {
            LOGGER.warn("Error while parsing UserLink", e);
            return null;
          }
        })
        .filter(Objects::nonNull).collect(Collectors.toList());
    }
  }

  @Getter
  @Setter
  public static class UserLink {

    /**
     * Icon Name.
     */
    @Expose
    private String iconName;

    /**
     * Url.
     */
    @Expose
    private String url;

    /**
     * Label.
     */
    @Expose
    private String label;

    public UserLink() {
    }
  }
}

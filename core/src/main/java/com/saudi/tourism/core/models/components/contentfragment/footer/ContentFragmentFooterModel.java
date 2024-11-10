package com.saudi.tourism.core.models.components.contentfragment.footer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManagerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

/**
 * This is the Sling Model class for the Content Fragment component.
 * The {@link PostConstruct} annotation allows us to perform some logic after the object has been instantiated.
 * The {@link Model} annotation allows us to register the class as a Sling Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class ContentFragmentFooterModel extends AbstractComponentImpl implements
    com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment {

  /**
   * theme.
   */
  public static final String THEME = "theme";
  /**
   * The content fragment path.
   */
  @ValueMapValue(name = com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment.PN_PATH,
      injectionStrategy = InjectionStrategy.OPTIONAL)
  @Nullable
  private String fragmentPath;

  /**
   * The page ManagerFactory.
   */
  @Inject
  private PageManagerFactory pageManagerFactory;
  /**
   * The resource resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * The resource .
   */
  @JsonIgnore
  @Self
  private Resource resource;

  /**
   * The slingHttpServletRequest .
   */
  @JsonIgnore
  @Self
  private SlingHttpServletRequest slingHttpServletRequest;


  /**
   * The content fragment.
   */
  private com.adobe.cq.dam.cfm.ContentFragment currentContentFragment;

  /**
   * The fragment resource.
   */
  private Resource fragmentResource;

  /**
   * The fragment content.
   */
  @Expose
  private FragmentFooter fragmentContent;

  /**
   * Fragment Footer Wrapper.
   */
  private FragmentFooter fragmentFooter = new FragmentFooter();
  /**
   * Map fragment content.
   */
  private Map<String, FragmentFooter> stringFragmentMap = new HashMap<>();

  @PostConstruct
  public void init() throws RepositoryException {
    if (StringUtils.isNotEmpty(this.fragmentPath)) {
      if (StringUtils.isNotBlank(fragmentPath)) {
        String path = slingHttpServletRequest.getPathInfo();
        String language = CommonUtils.getLanguageForPath(path);
        // Replace {language} with the value of the language.
        String replacedFragmentPath = fragmentPath.replace("{language}", language);
        // check if exist resource for replacedFragmentPath
        // case language is wrong, ex. https://www.visitsaudi.com/nopage
        if (resourceResolver.getResource(replacedFragmentPath) == null) {
          // if not exist, replace {language} with en (default language)
          replacedFragmentPath = fragmentPath.replace("{language}", "en");
        }
        fragmentResource = resourceResolver.getResource(replacedFragmentPath);
        if (Objects.isNull(fragmentResource)) {
          return;
        }
        currentContentFragment = fragmentResource.adaptTo(com.adobe.cq.dam.cfm.ContentFragment.class);
        if (currentContentFragment != null) {
          Resource currentResource = fragmentResource.getResourceResolver()
              .getResource(replacedFragmentPath + Constants.JCR_CONTENT_DATA_MASTER);
          if (Objects.isNull(currentResource)) {
            return;
          }
          FragmentFooter fragmentMaster = currentResource.adaptTo(FragmentFooter.class);
          if (fragmentMaster != null) {
            String theme = getTheme(path);
            fragmentFooter = buildSubFragmentMaster(theme, language, currentResource, fragmentMaster);
          }
        }
      }
    }
  }

  /**
   * Get the theme.
   *
   * @param path    the path
   * @return the theme
   */
  public String getTheme(String path) {
    String pagePath = getPathPageFromResourcePath(path);
    Resource pageResource = resourceResolver.getResource(pagePath);

    if (pageResource == null) {
      return "orange";
    }

    Page currentPage = pageResource.adaptTo(Page.class);
    if (currentPage == null) {
      return "orange";
    }

    return currentPage.getProperties().get(THEME, "orange");
  }

  private String getPathPageFromResourcePath(String path) {
    String pagePath = path;
    if (path.contains(JcrConstants.JCR_CONTENT)) {
      pagePath = path.split(JcrConstants.JCR_CONTENT)[0];
    } else if (path.contains("html")) {
      pagePath = StringUtils.removeEnd(path, "/");
      pagePath = pagePath.replaceAll("\\.html$", "");  // Remove .html extension if present
    }
    return pagePath;
  }

  private FragmentFooter buildSubFragmentMaster(String theme, String language, Resource currentResource,
                                                FragmentFooter fragmentMaster) {
    SubFragmentNewsletter fragmentNewsletter = currentResource.adaptTo(SubFragmentNewsletter.class);
    SubFragmentDownloads fragmentDownloads = currentResource.adaptTo(SubFragmentDownloads.class);
    SubFragmentGroup fragmentGroup = currentResource.adaptTo(SubFragmentGroup.class);
    SubFragmentBranding fragmentBranding = currentResource.adaptTo(SubFragmentBranding.class);
    fragmentBranding.setLanguage(language);
    fragmentBranding.setVisitSaudiLogo(fragmentBranding.buildVisitSaudiLogo());
    SubFragmentInternalLinks fragmentInternalLinks = currentResource.adaptTo(SubFragmentInternalLinks.class);
    SubFragmentExternalLinks fragmentExternalLinks = currentResource.adaptTo(SubFragmentExternalLinks.class);
    SubFragmentContact fragmentContact = currentResource.adaptTo(SubFragmentContact.class);
    SubFragmentContainer fragmentContainer = currentResource.adaptTo(SubFragmentContainer.class);
    fragmentMaster.setFragmentContact(fragmentContact);
    fragmentMaster.setFragmentNewsletter(fragmentNewsletter);
    fragmentMaster.setFragmentBranding(fragmentBranding);
    fragmentMaster.setFragmentGroup(fragmentGroup);
    fragmentMaster.setFragmentExternalLinks(fragmentExternalLinks);
    fragmentMaster.setFragmentDownloads(fragmentDownloads);
    fragmentMaster.setFragmentInternalLinks(fragmentInternalLinks);
    fragmentMaster.setFragmentContainer(fragmentContainer);
    return fragmentMaster;
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(fragmentFooter);
  }
}

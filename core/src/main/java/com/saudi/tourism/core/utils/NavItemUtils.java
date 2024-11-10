package com.saudi.tourism.core.utils;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.NavItem;
import com.saudi.tourism.core.models.components.nav.v2.PlanItem;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;


/**
 * Utility class for Navigation link related methods.
 */
public final class NavItemUtils {

  /**
   * Navigation type: articles.
   */
  public static final String NAV_TYPE_ARTICLES = "article";

  /**
   * Navigation type: events.
   */
  public static final String NAV_TYPE_EVENTS = "events";

  /**
   * Navigation type: events.
   */
  public static final String NAV_TYPE_PLAN = "plan";

  /**
   * Navigation type: weathercurrency (widgets).
   */
  public static final String NAV_TYPE_WIDGETS = "weathercurrency";


  /**
   * Private constructor to avoid instantiation.
   */
  private NavItemUtils() {

  }

  /**
   * This method is used to generated the child list for navigation header.
   *
   * @param resource       Resource
   * @param pageManager    allows easy retrieval of page information
   * @param childDepth     maximum depth of the children
   * @param allowEmptyPath If the url is allowed to be an empty or not
   *                       String
   * @param isPublishMode checking publish mode
   * @return List<NavItem>
   */

  public static List<NavItem> generateChildListItems(final Resource resource,
      final PageManager pageManager, final int childDepth, final Boolean allowEmptyPath, final Boolean isPublishMode) {
    Page containingPage = pageManager.getContainingPage(resource);
    return Optional.ofNullable(containingPage).map(Page::listChildren).map(
        pageIterator -> iterateChildren(containingPage.getDepth(), pageIterator, pageManager,
            childDepth, allowEmptyPath, isPublishMode)).orElse(new ArrayList<>());
  }

  /**
   * This method is used to create and return the NavItem for Provided Page.
   *
   * @param startLevel     depth of navigation root page
   * @param page           Page that contains the information for Nav Link
   * @param pageManager    This allows easy retrieval of page information
   * @param childDepth     maximum depth of the children
   * @param allowEmptyPath Is the url is allowed to be an empty
   * @param  isPublishMode is checking publish mode
   *                       String
   * @return NavItem
   */
  private static NavItem getNavLinkItem(final int startLevel, final Page page,
      final PageManager pageManager, final int childDepth, final Boolean allowEmptyPath, final Boolean isPublishMode) {
    String resourceUrl = null;
    NavItem.NavItemBuilder builder = NavItem.builder();
    ValueMap refPageProperties = page.getProperties();
    String redirectPage = refPageProperties.get(NameConstants.PN_REDIRECT_TARGET, "");
    Page resourcePage = pageManager.getContainingPage(redirectPage);

    if (resourcePage != null) {
      if (isPublishMode && resourcePage.getPath().startsWith(Constants.ROOT_CONTENT_PATH)) {
        resourceUrl = resourcePage.getPath().replace(Constants.ROOT_CONTENT_PATH, StringUtils.EMPTY);
      } else {
        resourceUrl = resourcePage.getPath();
      }
      builder.url(resourceUrl)
          .urlWithExtension(LinkUtils.getUrlWithExtension(resourcePage.getPath())).title(
          StringUtils.defaultIfEmpty(resourcePage.getNavigationTitle(), resourcePage.getTitle()))
          .description(resourcePage.getDescription())
          .featureImage(String.valueOf(resourcePage.getProperties().get(Constants.FEATURE_IMAGE)))
          .siteName(getSiteName(resourcePage.getPath()))
          .navType(String.valueOf(refPageProperties.get(Constants.NAVIGATION_TYPE)))
          .cities(getItemFromContent(page.getContentResource(), Constants.CITIES,
              NavItemUtils::getDictItem))
          .currencies(getItemFromContent(page.getContentResource(), Constants.CURRENCIES,
              NavItemUtils::getDictItem))
          .plans(getItemFromContent(page.getContentResource(), Constants.PLANS,
              NavItemUtils::getPlanItem))
          .articleItem(getArticleItem(pageManager,
              String.valueOf(refPageProperties.get(Constants.ARTICLE_PATH))))
          .text(String.valueOf(page.getProperties().get(Constants.TEXT)));
    } else if (!LinkUtils.isInternalLink(redirectPage)) {
      builder.url(redirectPage).urlWithExtension(redirectPage).title(page.getTitle());
    }
    builder.pageLevel(page.getDepth() - startLevel);

    if (page.getDepth() - startLevel < childDepth) {
      Iterator<Page> iterator = page.listChildren();
      builder.childList(
          iterateChildren(startLevel, iterator, pageManager, childDepth, allowEmptyPath, isPublishMode));
    }
    return builder.build();
  }

  /**
   * This method is used to get generic object from child of the resource.
   *
   * @param <T>            the type parameter
   * @param resource       resource object
   * @param childName      name of the child object
   * @param getItem        function to obtain generic object from child resource
   * @return DictItem
   */
  private static <T> List<T> getItemFromContent(Resource resource,
                                                String childName, Function<ValueMap, T> getItem) {
    List<T> list = new ArrayList<>();
    if (resource.getChild(childName) != null) {
      Iterator<Resource> children = resource.getChild(childName).listChildren();
      while (children.hasNext()) {
        Resource childResource = children.next();
        ValueMap properties = childResource.getValueMap();
        list.add(getItem.apply(properties));
      }
    }
    return list;
  }

  /**
   * This method is used to create the DictItem object from ValueMap.
   *
   * @param valueMap       ValueMap object
   * @return DictItem
   */
  private static DictItem getDictItem(ValueMap valueMap) {
    return null;
  }

  /**
   * This method is used to create the PlanItem object from ValueMap.
   *
   * @param valueMap       ValueMap object
   * @return PlanItem
   */
  private static PlanItem getPlanItem(ValueMap valueMap) {
    PlanItem planItem = new PlanItem();
    planItem.setPlanTitle(valueMap.get(Constants.PLAN_TITLE, String.class));
    planItem.setPlanDescription(valueMap.get(Constants.PLAN_DESCRIPTION, String.class));
    planItem.setPlanUrl(valueMap.get(Constants.PLAN_URL, String.class));
    planItem.setPlanIcon(valueMap.get(Constants.PLAN_ICON, String.class));
    return planItem;
  }

  /**
   * This method is used to get the ArticleItem object.
   *
   * @param pageManager PageManager object
   * @param path      Path of the article
   * @return ArticleItem
   */
  public static ArticleItem getArticleItem(PageManager pageManager, String path) {
    final Page articlePage = pageManager.getContainingPage(path);
    ArticleItem articleItem = new ArticleItem();

    if (articlePage == null) {
      // Return empty item if page was not found
      return articleItem;
    }

    final Resource pageResource = articlePage.adaptTo(Resource.class);
    assert pageResource != null;

    ValueMap valueMap = pageResource.getValueMap();

    final String imageReference = valueMap.get(Constants.FEATURE_IMAGE, String.class);
    if (StringUtils.isNotBlank(imageReference)) {
      articleItem.getImage().setFileReference(imageReference);

    } else if (articlePage.hasChild(Constants.PN_IMAGE)) {
      final Resource imageResource = pageResource.getChild(Constants.PN_IMAGE);
      if (imageResource != null) {
        articleItem.setImage(imageResource.adaptTo(Image.class));
      }
    }

    final Link link = new Link();
    link.setUrl(path);
    link.setUrlWithExtension(LinkUtils.getUrlWithExtension(path));
    link.setUrlSlingExporter(LinkUtils.getFavoritePath(path));
    articleItem.setLink(link);

    articleItem.setTitle(
        StringUtils.defaultIfEmpty(articlePage.getNavigationTitle(), articlePage.getTitle()));
    articleItem.setDescription(articlePage.getDescription());

    articleItem.setCity(valueMap.get(Constants.CITY, String.class));

    // for events
    if (valueMap.containsKey(Constants.EVENT_START)) {
      final Locale locale = articlePage.getLanguage(true);

      String dateFormat = Constants.FORMAT_EVENT_DATE;
      if (locale.toString().equals(Constants.ARABIC_LOCALE)) {
        dateFormat = Constants.FORMAT_EVENT_DATE_ARABIC;
      }
      articleItem.setStartDate(
          CommonUtils.getDateMonth(valueMap.get(Constants.EVENT_START, String.class), dateFormat));
      articleItem.setEndDate(
          CommonUtils.getDateMonth(valueMap.get(Constants.EVENT_END, String.class), dateFormat));
    }
    return articleItem;
  }

  /**
   * This method iterate child pages to populate Navigation header links based on the created
   * content structure for Header Navigation.
   *
   * @param startLevel     depth of root page for navigation
   * @param childIterator  Iterator for child Pages
   * @param pageManager    allows easy retrieval of page information
   * @param childDepth     maximum depth of the children
   * @param allowEmptyPath whether or not the url is allowed to be an empty
   *                       String
   * @param isPublishMode  publish mode
   * @return Hierarchical list of Navigation Links
   */
  private static List<NavItem> iterateChildren(final int startLevel,
      final Iterator<Page> childIterator, final PageManager pageManager, final int childDepth,
      final Boolean allowEmptyPath, final Boolean isPublishMode) {
    List<NavItem> items = new ArrayList<>();
    while (childIterator.hasNext()) {
      Page child = childIterator.next();
      items.add(getNavLinkItem(startLevel, child, pageManager, childDepth, allowEmptyPath, isPublishMode));
    }
    return items;
  }


  /**
   * This method is used to get the siteName for the site.
   *
   * @param pagePath String  page path
   * @return String
   */
  public static String getSiteName(String pagePath) {
    String siteName = pagePath;
    if (StringUtils.isNotEmpty(pagePath)) {
      String[] strArr = pagePath.split(Constants.FORWARD_SLASH_CHARACTER);
      if (StringUtils.isNotEmpty(strArr[Constants.THREE])) {
        siteName = strArr[Constants.THREE];
      }
    }
    return siteName;
  }
}

package com.sta.core.solr.services.impl;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Date;
import java.util.Collection;
import java.util.stream.Stream;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.models.mobile.components.MobileSearchResults;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.NativeAppHomepageService;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.PrimConstants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.SearchUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.sta.core.MmCoreException;
import com.sta.core.exceptions.TechnicalMmCoreException;
import com.sta.core.solr.model.SolrResponse;
import com.sta.core.solr.model.SolrResult;
import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.model.SolrRequest;
import com.sta.core.solr.model.SolrGroupSection;
import com.sta.core.solr.model.SolrSuggestionResponse;
import com.sta.core.solr.model.SolrWithGroupResponse;
import com.sta.core.solr.services.SolrSearchService;
import com.sta.core.solr.services.SolrServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import static org.eclipse.jetty.util.URIUtil.SLASH;

/**
 * The type Solr search service.
 */
@Slf4j
@Component(immediate = true,
    service = SolrSearchService.class)
public class SolrSearchServiceImpl implements SolrSearchService {

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * Sling settings service.
   */
  @Reference
  private SlingSettingsService settingsService;

  /**
   * Inject SearchResultsService.
   */
  @Reference
  private SearchResultsService searchResultsService;

  /**
   * The Solr configuration service.
   */
  @Reference
  private SolrServerConfiguration solrConfigurationService;

  /** NativeAppHomepageService Reference. */
  @Reference private NativeAppHomepageService nativeAppHomepageService;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Reference
  private AdapterManager adapterManager;

  /**
   * The map holds solr clients for different cores.
   */
  private Map<String, HttpSolrClient> httpSolrClientMap = new HashMap<>();

  /**
   * Current solr url value.
   */
  private String solrUrl = Constants.BLANK;

  /**
   * Current solr core value.
   */
  private String solrCore = Constants.BLANK;

  /**
   * Solr filter query param separator.
   */
  private static final String QUERY_PARAM_SEPARATOR = " " + "OR" + " ";

  /**
   * description.
   */
  private static final String FIELD_DESCRIPTION = "description";

  /**
   * field mobile index excluded.
   */
  private static final String FIELD_MOBILE_INDEX_EXCLUDED = "isExcludedMobileIndex";

  /**
   * Locale template fragment we expect to be included in configuration paths.
   */
  protected static final String LOCALE_TEMPLATE = "{" + Constants.LOCALE + "}";

  /**
   * Image element name.
   */
  public static final String IMAGE_ELEMENT_NAME = "image";



  /**
   * Cleans up the service upon deactivation.
   */
  @Deactivate
  protected void deactivate() {
    for (HttpSolrClient httpSolrClient : httpSolrClientMap.values()) {
      if (httpSolrClient != null) {
        try {
          httpSolrClient.close();
        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }
  }

  @Override
  public SolrArticle createPageMetadataObject(Resource page) {
    String pagePath = page.getPath();
    ResourceResolver resourceResolver = page.getResourceResolver();
    boolean isConfigsAdminPage = pagePath.endsWith("/Configs/admin");

    Resource pageContent = page.getChild(JcrConstants.JCR_CONTENT);
    if (pageContent == null) {
      return null;
    }

    ValueMap properties = pageContent.adaptTo(ValueMap.class);
    if (properties.get(Constants.HIDE_IN_NAV, Constants.BLANK).equals(Constants.STR_TRUE)) {
      return null;
    }

    ItemResponseModel mobileItem = null;
    String url;
    String language;
    String title;
    String description;
    String image = null;
    String jcrDescription = properties.get(JcrConstants.JCR_DESCRIPTION, Constants.BLANK);

    boolean isMobile = !StringUtils.isEmpty(solrConfigurationService.getMobilePath()) && pagePath
        .startsWith(solrConfigurationService.getMobilePath());

    if (isMobile) {
      url = MobileUtils.extractItemId(pagePath);
      Resource itemDetails = pageContent.getChild("item");
      if (Objects.isNull(itemDetails)) {
        return null;
      }
        mobileItem = itemDetails.adaptTo(ItemResponseModel.class);
      if (Objects.isNull(mobileItem)) {
        return null;
      }

      if (StringUtils.isBlank(mobileItem.getType())) {
        return null;
      }

      if (Objects.nonNull(mobileItem.getTitles())
          && StringUtils.isNotBlank(mobileItem.getTitles().getTitle())) {
        title = mobileItem.getTitles().getTitle();
      } else if (StringUtils.isNotBlank(mobileItem.getTitle())) {
        title = mobileItem.getTitle();
      } else {
        title = properties.get(JcrConstants.JCR_TITLE, Constants.BLANK);
      }

      if (Objects.nonNull(mobileItem.getAbout())
          && StringUtils.isNotBlank(mobileItem.getAbout().getDescription())) {
        description = mobileItem.getAbout().getDescription();
      } else {
        description = properties
          .get("shortDescription", properties.get(FIELD_DESCRIPTION, jcrDescription))
          .trim();
      }

      List<MediaGallery> mediaGallery = Optional.ofNullable(mobileItem.getMediaGallery())
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .filter(media -> "IMAGE".equals(media.getType()) && StringUtils.isNotBlank(media.getUrl()))
        .collect(Collectors.toList());

      if (CollectionUtils.isNotEmpty(mediaGallery)) {
        image = mediaGallery.stream()
          .map(img -> img.getUrl())
          .findFirst()
          .orElse(null);
      }

      language =
          CommonUtils.getPageNameByIndex(pagePath, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);
    } else {
      url = pagePath.replace(Constants.ROOT_CONTENT_PATH, Constants.BLANK);
      language =
          CommonUtils.getPageNameByIndex(pagePath, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      title = properties.get(JcrConstants.JCR_TITLE, Constants.BLANK);
      description = properties
              .get("shortDescription", properties.get(FIELD_DESCRIPTION, jcrDescription))
              .trim();
    }

    String resourceType =
        properties.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, Constants.BLANK);

    // Get referenced content fragment if exists.
    String referencedFragmentPath = properties.get("referencedFragmentPath", String.class);
    ContentFragment contentFragment = null;
    if (StringUtils.isNotEmpty(referencedFragmentPath)) {
      final var cfResource = resourceResolver.getResource(referencedFragmentPath);
      if (Objects.nonNull(cfResource)) {
        contentFragment = cfResource.adaptTo(ContentFragment.class);
      }
    }

    boolean isArticlePath = pagePath.contains("/Configs/articles");
    boolean isArticlePage = resourceType.equals(Constants.CARDS_RES_TYPE);
    // We only index cards page under /Configs/article and we ignore the rest
    if (isArticlePath && !isArticlePage) {
      return null;
    }


    // Allowed
    isMobile = isMobile || isArticlePath;

    if (isMobile) {
      if (inAppIgnoreList(resourceType)) {
        return null;
      }
    }
    boolean isEvent = resourceType.equals(Constants.EVENT_RES_TYPE);

    String tagAttr = NameConstants.PN_TAGS;
    if (isEvent) {
      tagAttr = "categoryTags";
    }
    List<String> tags = getTagsFromResource(language, pageContent.getResourceResolver(),
        properties.get(tagAttr, String[].class));
    List<String> cfTags = getTagsFromContentFragment(language, pageContent.getResourceResolver(), contentFragment);
    tags.addAll(cfTags);

    String featureImage = getFeatureImage(properties, contentFragment);

    SolrArticle solrArticle = new SolrArticle();
    solrArticle.setId(pagePath);
    solrArticle.setUrl(url);
    solrArticle.setTitle(title);
    solrArticle.setDescription(description);
    solrArticle.setLanguage(language);
    String pageType = AppUtils.getPageType(properties);
    // if pageType is empty get pageType from contentType tag on the page properties
    String contentTypeTag = properties.get(Constants.CONTENT_TYPE, String.class);
    if (StringUtils.isEmpty(pageType) && Objects.nonNull(contentTypeTag)) {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Tag tag = tagManager.resolve(Constants.TAGS_URL
        + contentTypeTag.replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER));
      pageType = tag.getName();
    }
    solrArticle.setType(pageType);

    boolean isMobileIndexExcluded = properties.get(FIELD_MOBILE_INDEX_EXCLUDED, false);
    var isInMobileAutoList =  inMobileAutoList(resourceType, isMobileIndexExcluded);
    solrArticle.setSource(getSource(pagePath, language, isMobile, pageType, isInMobileAutoList));
    if (solrArticle.getSource().isEmpty()) {
      return null;
    }
    solrArticle.setTags(tags);
    solrArticle.setCategories(getTagsFromResource(null, pageContent.getResourceResolver(),
        properties.get("searchCategories", String[].class)));
    addSearchCategoryByResourceType(resourceType, solrArticle);
    // Add page type to categories, so we can search by page type.
    addDefaultSearchCategory(resourceType, pageType, solrArticle);

    if (isMobile) {


      solrArticle.setType("item");
      if (StringUtils.isNotBlank(mobileItem.getType()) && AppUtils.isDeepLinkType(mobileItem.getType())) {
        // If the value matches one of the deeplink types, set the type accordingly
        solrArticle.setType(mobileItem.getType());
      }
      solrArticle.setImage(image);
      if (StringUtils.isNotBlank(mobileItem.getType()) && !AppUtils.isDeepLinkType(mobileItem.getType())) {
        solrArticle.setCategories(List.of(AppUtils.getPageTypeFromMobileItemType(mobileItem.getType())));
      } else{
        solrArticle.setCategories(Collections.emptyList());
      }
      if (isArticlePage) {
        if (StringUtils.isBlank(solrArticle.getImage())) {
          Resource imageResource = pageContent.getChild(Constants.PN_IMAGE);
          if (imageResource != null) {
            final Image imageArticle = imageResource.adaptTo(Image.class);
            solrArticle
                .setImage(imageArticle.getFileReference());
          }
        }

        List<String> cityArrayLabels =
            getTagsFromResource(language, pageContent.getResourceResolver(),
                properties.get("cardCity", String[].class));
        if (CollectionUtils.isNotEmpty(cityArrayLabels) && cityArrayLabels.size() == 1) {
          solrArticle.setRegion(cityArrayLabels.get(0));
        }

      }

    } else {
      solrArticle.setImage(featureImage);
      solrArticle.setRegion(getRegionFromContentFragment(resourceResolver, contentFragment));
      if (isEvent && Objects.nonNull(contentFragment)) {
        solrArticle.setImage(featureImage);
        solrArticle.setStartdate(getElementValue(contentFragment, "startDate", String.class, PrimConstants.BLANK));
        solrArticle.setEnddate(getElementValue(contentFragment, "endDate", String.class, PrimConstants.BLANK));
        solrArticle.setStarttime(getElementValue(contentFragment, "startTime", String.class, PrimConstants.BLANK));
        solrArticle.setEndtime(getElementValue(contentFragment, "endTime", String.class, PrimConstants.BLANK));
      }
    }

    solrArticle.setPublishdate(properties.get(NameConstants.PN_PAGE_LAST_MOD,
        properties.get(JcrConstants.JCR_CREATED, Constants.BLANK)));

    if (properties.get(Constants.SEARCH_HIGHLIGHT, Constants.BLANK).equals(Constants.STR_TRUE)) {
      solrArticle.setPriority(NumberConstants.CONST_HUNDRED - page.getPath()
          .split(Constants.FORWARD_SLASH_CHARACTER).length);
    }

    List<String> titles =
        getProperties(properties, solrConfigurationService.getTitleProperties(), tags);
    List<String> cfTitles =
      getCfProperties(contentFragment, solrConfigurationService.getCfProperties(), cfTags);
    titles.addAll(cfTitles);

    List<String> subtitles =
        getProperties(properties, solrConfigurationService.getSubtitleProperties(), tags);

    List<String> texts = addPageContentForIndex(pageContent);
    List<String> cfTexts = addContentFragmentForIndex(contentFragment);
    texts.addAll(cfTexts);

    if (isMobile) {
      if (CollectionUtils.isNotEmpty(mobileItem.getCategories())) {
        for (Category category: mobileItem.getCategories()) {
          titles.add(category.getTitle());
          texts.add(category.getTitle());
        }
      }

      titles.add(title);
      titles.add(description);
      titles = titles.stream().distinct().collect(Collectors.toList());

      if (StringUtils.isNotBlank(mobileItem.getTitle())) {
        texts.add(mobileItem.getTitle());
      }

      texts.add(title);
      texts.add(description);
      texts = texts.stream().distinct().collect(Collectors.toList());
    }

    if (!isConfigsAdminPage) {
      solrArticle.setTitles(titles);
      solrArticle.setSuggestProperties(new ArrayList<>());
      solrArticle.getSuggestProperties().add(title);
      solrArticle.setText(texts);
      solrArticle.setSubtitles(subtitles);
    } else {
      solrArticle.setSuggestProperties(getSuggestionKeys(pageContent));
      solrArticle.setSuggestWeight(NumberConstants.CONST_HUNDRED);
    }
    return solrArticle;
  }

  /**
   * This method is used to get destination from content fragment.
   *
   * @param resourceResolver
   * @param contentFragment
   * @return region
   */
  private String getRegionFromContentFragment(ResourceResolver resourceResolver, ContentFragment contentFragment) {
    if (Objects.isNull(contentFragment)) {
      return StringUtils.EMPTY;
    }
    String region = StringUtils.EMPTY;

    String templateTitle = contentFragment.getTemplate().getTitle();
    String locationKey = determineCFLocationKey(templateTitle);
    String locationCFPath = getElementValue(contentFragment, locationKey, String.class, PrimConstants.BLANK);
    if (StringUtils.isNotEmpty(locationCFPath)) {
      final var destinationCF = resourceResolver.getResource(locationCFPath);
      if (Objects.nonNull(destinationCF)) {
        return LinkUtils.getLastPathSegment(destinationCF.getPath()).toLowerCase();
      }
    }

    return region;
  }

  /**
   * determine CF location key.
   *
   * @param templateTitle
   * @return location key
   */
  private String determineCFLocationKey(String templateTitle) {
    switch (templateTitle) {
      case "story":
      case "tour":
        return "destination";
      default:
        return "locationValue";
    }
  }

  /**
   * This method is used to get Feature Image.
   *
   * @param properties properties.
   * @param contentFragment contentFragment.
   * @return feature image.
   */
  private String getFeatureImage(ValueMap properties, ContentFragment contentFragment) {
    String featureImage = properties.get(PrimConstants.FEATURE_IMAGE, PrimConstants.BLANK);
    if (Objects.nonNull(contentFragment)) {
      featureImage = getFeatureImageFromContentFragment(contentFragment);
    }
    return featureImage;
  }

  /**
   * Switch on contentFragment template title (event, attraction, activity ...) to get the right image.
   *
   * @param contentFragment contentFragment.
   * @return image path.
   */
  private String getFeatureImageFromContentFragment(ContentFragment contentFragment) {
    if (Objects.isNull(contentFragment)) {
      return StringUtils.EMPTY;
    }
    String templateTitle = contentFragment.getTemplate().getTitle();
    String image = StringUtils.EMPTY;

    switch (templateTitle) {
      case "activity":
      case "attraction":
      case "event":
      case "story":
      case "tour":
      case "Destination":
      case "poi":
        image = getImageBannerFromMultifiedImage(contentFragment);
        break;
      case "holiday":
      case "season":
        image = getElementValue(contentFragment, IMAGE_ELEMENT_NAME, String.class, StringUtils.EMPTY);
        break;
      default:
        break;
    }

    return image;
  }

  /**
   * This method is used to get image banner value from content fragment, case multified.
   *
   * @param contentFragment contentFragment.
   * @return image path.
   */
  private String getImageBannerFromMultifiedImage(ContentFragment contentFragment) {
    String[] images = getElementValue(contentFragment, "images", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
    if (ArrayUtils.isEmpty(images)) {
      return StringUtils.EMPTY;
    }
    return Arrays.stream(images)
      .filter(StringUtils::isNotEmpty)
      .map(imageJson -> {
        final var jsonObject = JsonParser.parseString(imageJson).getAsJsonObject();
        if (jsonObject != null && jsonObject.has("type") &&
          IMAGE_ELEMENT_NAME.equals(jsonObject.get("type").getAsString()) &&
          jsonObject.has(IMAGE_ELEMENT_NAME)) {
          return jsonObject.get(IMAGE_ELEMENT_NAME).getAsString();
        }
        return null;
      })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(StringUtils.EMPTY);
  }

  /**
   * This method is used to get translation.
   *
   * @param key      translation key
   * @param language language
   * @return translated String
   */
  private String getTranslation(String key, String language) {
    if (!StringUtils.isEmpty(key)) {
      ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
      return i18n.getString(key);
    }
    return Constants.BLANK;
  }

  /**
   * This method is used to add category by ResourceType.
   *
   * @param resourceType resourceType
   * @param solrArticle  object
   */
  private void addSearchCategoryByResourceType(String resourceType, SolrArticle solrArticle) {
    if (resourceType.equals(Constants.PACKAGE_DETAIL_RES_TYPE)) {
      solrArticle.getCategories().add("PlanYourTrip");
    }
  }

  /**
   * This method is used to get tags from node.
   *
   * @param pagePath    path
   * @param locale      language
   * @param isInMobileAutoList      isInMobileAutoList
   * @param isMobile       isMobile
   * @param appPageType page type for app
   * @return list of source
   */
  private List<String> getSource(String pagePath, String locale, boolean isMobile,
                                 String appPageType, boolean isInMobileAutoList) {
    List<String> result = new ArrayList<>();
    boolean appSource = false;
    if (isMobile) {
      appSource = true;
    } else {
      result.add(Constants.SOLR_WEB_SOURCE);
      appSource = isInMobileAutoList;
    }

    if (appSource && !appPageType.equals(StringUtils.EMPTY)) {
      result.add(Constants.SOLR_MOBILE_SOURCE);
    }

    return result;
  }

  /**
   * This method is used to get tags from node.
   *
   * @param pageContent resource
   * @return list of solrKeys
   */
  private List<String> getSuggestionKeys(Resource pageContent) {
    List<String> result = new ArrayList<>();
    Resource solrKeyResource = null;
    Resource suggestionsResource = pageContent.getChild(Constants.SOLR_SEARCH);
    if (suggestionsResource != null) {
      solrKeyResource = suggestionsResource.getChild(Constants.SOLR_KEY);
    }
    if (solrKeyResource != null) {
      for (Resource solrKey : solrKeyResource.getChildren()) {
        String suggestionKey =
            solrKey.getValueMap().get(Constants.SOLR_KEY, Constants.BLANK).trim();
        if (!suggestionKey.equals(Constants.BLANK)) {
          result.add(suggestionKey);
        }
      }
    }
    return result;
  }

  /**
   * This method is used to get tags from node.
   *
   * @param language         string
   * @param resourceResolver ResourceResolver
   * @param tags             list of tag ids
   * @return tags
   */
  private List<String> getTagsFromResource(String language, ResourceResolver resourceResolver,
                                           String[] tags) {
    List<String> tagList = new ArrayList<>();
    Locale locale = null;
    if (tags != null) {
      if (language != null) {
        locale = new Locale(language);
      }
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      for (int i = 0; i < tags.length; i++) {
        Tag tag = tagManager.resolve(Constants.TAGS_URL + tags[i]
            .replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER));
        if (tag != null) {
          if (locale != null) {
            tagList.add(tag.getTitle(locale));
          } else {
            tagList.add(tag.getName());
          }
        }
      }
    }
    return tagList;
  }

  /**
   * This method is used to get tags from Content Fragment.
   * @param language language
   * @param resourceResolver ResourceResolver
   * @param contentFragment contentFragment
   * @return
   */
  private List<String> getTagsFromContentFragment(String language, ResourceResolver resourceResolver, ContentFragment contentFragment) {
    if (Objects.isNull(contentFragment)) {
      return Collections.emptyList();
    }
    String[] cfTags = getElementValue(contentFragment, "tags", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
    return getTagsFromResource(language, resourceResolver, cfTags);
  }

  /**
   * This method is used to get properties from node.
   *
   * @param vmProperties node's ValueMap
   * @param confProps    properties from config
   * @param tags         formatted tags if needed
   * @return list of prop values
   */
  private List<String> getProperties(ValueMap vmProperties, String[] confProps, List<String> tags) {
    List<String> result = new ArrayList<>();
    for (String fieldName : confProps) {
      if (fieldName.equals(NameConstants.PN_TAGS)) {
        result.addAll(tags);
      } else {
        String value = vmProperties.get(fieldName, String.class);
        if (value != null && !value.trim().isEmpty() && !result.contains(value.trim())) {
          result.add(value.trim());
        }
      }
    }
    return result;
  }

  /**
   * Get Content fragment properties
   *
   * @param contentFragment    content fragment.
   * @param cfProperties    properties from config.
   * @param tagsFromCF     formatted tags if needed.
   *
   * @return cf data.
   */
  private List<String> getCfProperties(ContentFragment contentFragment, String[] cfProperties, List<String> tagsFromCF) {
    List<String> result = new ArrayList<>();
    if (Objects.isNull(contentFragment)) {
      return Collections.emptyList();
    }
    for (String fieldName : cfProperties) {
      if (!contentFragment.hasElement(fieldName)) {
        continue;
      }
      if (fieldName.equals("tags")) {
        result.addAll(tagsFromCF);
      } else {
        FragmentData fragmentData = contentFragment.getElement(fieldName).getValue();
        if (fragmentData != null) {
          result.add(fragmentData.getValue(String.class));
        }
      }
    }
    return result;
  }

  /**
   * This method is used to get text for index from child nodes.
   *
   * @param pageContent Resource
   * @return list of string
   */
  private List<String> addPageContentForIndex(Resource pageContent) {
    List<String> texts = new ArrayList<>();
    try {
      Node sourceNode = pageContent.adaptTo(Node.class);
      NodeIterator iter = sourceNode.getNodes();
      buildNodeContent(sourceNode, texts);
      while (iter.hasNext()) {
        Node source = iter.nextNode();
        buildNodeContent(source, texts);
        iterateNodes(source, texts);
      }
    } catch (Exception e) {
      LOGGER.error("error in getting property {}", e.getMessage());
    }
    return texts;
  }

  /**
   * This method is used to get text for index from child nodes.
   *
   * @param sourceNode       Node
   * @param texts            array to fill
   *
   * @throws RepositoryException exception
   */
  private void iterateNodes(Node sourceNode, List<String> texts) throws RepositoryException {
    NodeIterator iter = sourceNode.getNodes();
    while (iter.hasNext()) {
      Node source = iter.nextNode();
      if (!inIgnoreList(source)) {
        buildNodeContent(source, texts);
        iterateNodes(source, texts);
      }
    }
  }

  /**
   * This method is used to check if components content should be ignored.
   *
   * @param source Node
   * @return boolean
   */
  private boolean inIgnoreList(Node source) {
    try {
      if (solrConfigurationService.getIgnoredResourceTypes() != null
          && solrConfigurationService.getIgnoredResourceTypes().length > 0) {
        if (source.hasProperty(ResourceResolver.PROPERTY_RESOURCE_TYPE)) {
          String resourceType =
              source.getProperty(ResourceResolver.PROPERTY_RESOURCE_TYPE).getString();
          for (String type : solrConfigurationService.getIgnoredResourceTypes()) {
            if (type.equalsIgnoreCase(resourceType)) {
              return true;
            }
          }
        }
      }
    } catch (RepositoryException e) {
      LOGGER.error("error getting property {}", e.getMessage());
    }
    return false;
  }

  @Override
  public boolean indexPageToSolr(SolrArticle solrArticle) throws MmCoreException {
    if (solrArticle != null) {
      try {
        HttpSolrClient server = getHttpSolrClient(solrArticle.getLanguage());
        server.addBean(solrArticle);
        server.commit();
        return true;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new MmCoreException(e);
      }
    }

    return false;
  }

  @Override
  public SolrResponse<SolrResult> search(SolrRequest solrRequest) {

    if (StringUtils.isEmpty(solrRequest.getLocale()) || StringUtils
        .isEmpty(solrRequest.getQuery())) {
      LOGGER.error("No local query parameter specified");
      return null;
    }
    if (solrConfigurationService.isUseAEMSearch()) {
      return oldSearch(solrRequest.getLocale(), solrRequest.getQuery(),
          solrRequest.getOffset().toString());
    } else {
      return solrSearch(solrRequest);
    }
  }

  /**
   * This method is used to search pages via SOLR.
   *
   * @param solrRequest SolrRequest
   * @return SolrResponse list of SolrResult
   */
  private SolrResponse<SolrResult> solrSearch(SolrRequest solrRequest) {
    SolrResponse<SolrResult> solrResponse = new SolrResponse<>();
    SolrClient client = getHttpSolrClient(solrRequest.getLocale()
        /*+(solrRequest.getSource().equalsIgnoreCase("app")?"_app":"")*/);
    SolrQuery solrQuery = buildSolrQuery(solrRequest);
    try {
      List<DictItem> types = getPageTypes(solrRequest.getLocale());
      QueryResponse response = client.query(solrQuery);
      if (response.getResults().isEmpty()) {
        LOGGER.info("Empty response from solr for request " + solrRequest.getQuery() +
            "For local " + solrRequest.getLocale());
      }
      if (!StringUtils.isEmpty(solrRequest.getType())) {
        solrResponse.setTitle(
            types.stream().filter(d -> d.getCode().equalsIgnoreCase(solrRequest.getType()))
                .map(DictItem::getValue).findAny().orElse(Constants.BLANK));
      }
      solrResponse.setPagination(buildPagination(response, solrRequest.getLimit()));
      solrResponse.setData(getSolrResult(response.getResults()));
      solrResponse.setSuggestion(spellCheck(solrRequest.getQuery(), client));
      solrResponse.setTypes(types.stream().map(DictItem::getCode)
          .collect(Collectors.toList()));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      solrResponse.setPagination(new Pagination(0));
      solrResponse.setData(new ArrayList<>());
    }
    return solrResponse;
  }

  /**
   * This method is used to sort the section on basis of query title.
   *
   * @param solrRequest solrRequest.
   * @param section     section.
   * @return list of sorted section.
   */
  private List<SolrGroupSection<SolrResult>> getOrderBySection(SolrRequest solrRequest,
                                                               List<SolrGroupSection<SolrResult>> section) {
    if (!section.isEmpty() && null != section) {
      List<SolrGroupSection<SolrResult>> sectionSortedList = section.stream()
          .sorted((s1, s2) -> {
            if (s1.getData().stream().anyMatch(
                solrResult -> (solrResult.getTitle().equalsIgnoreCase(solrRequest.getQuery())))) {
              return -1;
            } else {
              return 1;
            }
          }).collect(Collectors.toList());
      return sectionSortedList;

    }
    return section;
  }

  /**
   * This method is used to build Pagination object.
   *
   * @param response QueryResponse
   * @param limit    page size
   * @return Pagination object
   */
  private Pagination buildPagination(QueryResponse response, int limit) {
    Pagination pagination = new Pagination();
    pagination.setTotal((int) response.getResults().getNumFound());
    pagination.setOffset((int) response.getResults().getStart());
    pagination.setLimit(limit);
    return pagination;
  }

  /**
   * This method is used to build SolrQuery.
   *
   * @param solrRequest SolrRequest
   * @return SolrQuery object
   */
  private SolrQuery buildSolrQuery(SolrRequest solrRequest) {
//    String query = fuzzySearchQuery(solrRequest.getQuery(), solrRequest.getLocale());
    String query = solrRequest.getQuery();
    String rows = solrRequest.getLimit().toString();
    SolrQuery solrQuery = new SolrQuery(query);
    solrQuery.setParam("rows", rows);
    solrQuery.setParam("start", solrRequest.getOffset().toString());
    solrQuery.addFilterQuery("source:" + solrRequest.getSource());

    if (!StringUtils.isEmpty(solrRequest.getType())) {
      if (!StringUtils.contains(solrRequest.getType(), ",")) {
        solrQuery.addFilterQuery("categories:" + solrRequest.getType());
      } else {
        String typeFilter = buildMultipleValuesFilter(solrRequest.getType()); // Multiple value filtering
        solrQuery.addFilterQuery("categories:" + typeFilter);
      }
    }

    if (!StringUtils.isEmpty(solrRequest.getRegion())) {
      if (!StringUtils.contains(solrRequest.getRegion(), ",")) {
        solrQuery.addFilterQuery("region:" + solrRequest.getRegion());
      } else {
        String typeFilter = buildMultipleValuesFilter(solrRequest.getRegion()); // Multiple value filtering
        solrQuery.addFilterQuery("region:" + typeFilter);
      }
    }

    solrQuery.setParam("defType", "edismax");

    if (solrRequest.getSort() != null && solrRequest.getSort()
        .equalsIgnoreCase(Constants.DATE_TYPE)) {
      solrQuery.setParam("sort", "publishdate desc, score desc");
    } else {
      solrQuery.setParam("sort", "priority desc, score desc");
    }

    String boostTitle = "titles^" + solrConfigurationService.getTitleScore();
    String boostSubtitle = "subtitles^" + solrConfigurationService.getSubtitleScore();
    String boostExact = solrConfigurationService.getExactMatchScore();

    solrQuery.setParam("qf", boostTitle, boostSubtitle, "text");
    solrQuery
        .setParam("pf", "titles^" + boostExact, "subtitles^" + boostExact, "text^5");
    return solrQuery;
  }

  /**
   * This method is used to build a multiple value filter
   *
   * @param typeValuesString String
   * @return the fq solr param
   */
  private String buildMultipleValuesFilter(String typeValuesString) {
    String prefixedType = typeValuesString.replace(",", QUERY_PARAM_SEPARATOR);
    return "(" + prefixedType + ")";
  }

  /**
   * This method is used to build query for fuzzy search.
   *
   * @param query  query
   * @param locale language
   * @return query for fuzzy search
   */
  private String fuzzySearchQuery(String query, String locale) {
    if (!locale.equals("ja") && !locale.equals("zh")) {
      query = Stream.of(query.split("[\\p{IsPunctuation}\\p{IsWhite_Space}]+")).map(q -> {
        if (q.length() > NumberConstants.THREE) {
          if (locale.equals(Constants.ARABIC_LOCALE)) {
            // Append left if the locale is RTL
            q = "~" + q;
          } else {
            q += "~";
          }
        }
        return q;
      }).collect(Collectors.joining(Constants.SPACE));
    }
    return query;
  }

  /**
   * This method is used to get list of types.
   *
   * @param lang search string
   * @return list of types
   */
  private List<DictItem> getPageTypes(String lang) {
    Locale locale = new Locale(lang);
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      Stream<Resource> catResources;
      List<String> categories = SearchUtils.getSearchCategories(resolver, lang);
      if (categories != null && !categories.isEmpty()) {
        catResources = categories.stream().map(tagId -> Constants.TAGS_URL + tagId
                .replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER))
            .map(resolver::resolve);
      } else {
        Resource searchCats =
            resolver.resolve(Constants.TAGS_URL + "/sauditourism/searchCategories");
        Iterator<Resource> resources = searchCats.listChildren();
        catResources = CommonUtils.iteratorToStream(resources);
      }
      return catResources.map(resource -> tagManager.resolve(resource.getPath()))
          .filter(Objects::nonNull)
          .filter(tag -> !tag.getName().equalsIgnoreCase(Constants.SOLR_TYPE_HIGHLIGHT))
          .map(tag -> new DictItem(tag.getName(), tag.getTitle(locale)))
          .collect(Collectors.toList());
    }
  }

  /**
   * This method is used to build SolrQuery.
   *
   * @param query  search string
   * @param client SolrClient
   * @return String suggestion
   */
  private String spellCheck(String query, SolrClient client) {
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setParam("spellcheck", "true");
    solrQuery.setParam("spellcheck.q", query);
    solrQuery.setParam("qt", "/spell");

    try {
      QueryResponse queryResponse = client.query(solrQuery);
      SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
      if (!spellCheckResponse.isCorrectlySpelled()) {
        return calculateSpellCheckResult(spellCheckResponse);
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

    return StringUtils.EMPTY;
  }

  /**
   * Calculate Spell Check Result.
   *
   * @param spellCheckResponse spellCheckResponse
   * @return Spell Check Result
   */
  private String calculateSpellCheckResult(SpellCheckResponse spellCheckResponse) {
    StringBuilder result = new StringBuilder();
    boolean isChanged = false;
    if (!spellCheckResponse.getSuggestions().isEmpty()) {
      for (SpellCheckResponse.Suggestion suggestion : spellCheckResponse.getSuggestions()) {
        if (result.length() > 0) {
          result.append(" ");
        }
        if (suggestion.getOriginalFrequency() > 0) {
          result.append(suggestion.getToken());
        } else {
          result.append(getFreqSuggestion(suggestion));
          isChanged = true;
        }
      }
      if (isChanged) {
        return StringUtils.capitalize(result.toString());
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * This method is used to get most frequently used suggestion.
   *
   * @param suggestion spelling suggestion
   * @return String suggestion
   */
  private String getFreqSuggestion(SpellCheckResponse.Suggestion suggestion) {
    List<String> alternatives = suggestion.getAlternatives();
    List<Integer> alternativeFrequencies = suggestion.getAlternativeFrequencies();

    if (alternatives != null && alternativeFrequencies != null
        && alternatives.size() == alternativeFrequencies.size()) {
      int maxFreq = 0;
      int index = -1;
      for (int i = 0; i < alternativeFrequencies.size(); i++) {
        if (alternativeFrequencies.get(i) > maxFreq) {
          maxFreq = alternativeFrequencies.get(i);
          index = i;
        }
      }
      if (index > -1) {
        return alternatives.get(index);
      }
    }
    return suggestion.getToken();
  }

  /**
   * This method is used to search aem pages via searchResultsService.
   *
   * @param lang  language
   * @param query search string
   * @param start page
   * @return SolrResponse list of SolrResult
   */
  private SolrResponse<SolrResult> oldSearch(String lang, String query, String start) {
    SolrResponse<SolrResult> solrResponse = new SolrResponse<>();

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {

      String contentRoot = StringUtils.join(Constants.ROOT_CONTENT_PATH, SLASH, lang);
      PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
      List<SearchResultModel> listOfSearchResults = searchResultsService
          .webSearch(resourceResolver, pageManager.getPage(contentRoot), query,
              Long.valueOf(start), Boolean.TRUE);

      if (listOfSearchResults.size() == NumberConstants.CONST_ZERO) {
        listOfSearchResults = searchResultsService
            .webSearch(resourceResolver, pageManager.getPage(contentRoot), query,
                Long.valueOf(start), Boolean.FALSE);
      }
      Pagination pagination = new Pagination();
      pagination.setOffset(Integer.valueOf(start));
      pagination.setLimit(NumberConstants.CONST_TEN);
      solrResponse.setPagination(pagination);

      List<SolrResult> solrResults = new ArrayList<>();
      listOfSearchResults.stream().forEach(searchResultModel -> {
        SolrResult solrResult = new SolrResult();
        solrResult.setUrl(searchResultModel.getPagePath());
        solrResult.setTitle(searchResultModel.getPageTitle());
        solrResult.setDescription(searchResultModel.getPageDescription());
        solrResult.setFeatureImage(searchResultModel.getFeatureImage());
        solrResults.add(solrResult);

        pagination.setTotal(searchResultModel.getTotalResults().intValue());
      });
      solrResponse.setData(solrResults);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return solrResponse;
  }

  /**
   * This method is used to build list of SolrResult objects.
   *
   * @param solrDocumentList solrDocument list
   * @return SolrResult list
   */
  private List<SolrResult> getSolrResult(SolrDocumentList solrDocumentList) {
    return solrDocumentList.stream()
        .map(this::fillSolrResult)
        .collect(Collectors.toList());
  }

  /**
   * This method creates SolrResult from SolrDocument object.
   *
   * @param solrDocument solrDocument
   * @return SolrResult object
   */
  private SolrResult fillSolrResult(SolrDocument solrDocument) {
    SolrResult solrResult = new SolrResult();
    solrResult.setUrl((String) solrDocument.getFieldValue(Constants.URL));
    solrResult.setTitle((String) solrDocument.getFieldValue(Constants.PN_TITLE));
    solrResult.setDescription((String) solrDocument.getFieldValue(FIELD_DESCRIPTION));
    solrResult.setFeatureImage((String) solrDocument.getFieldValue(Constants.PN_IMAGE));
    solrResult.setType((String) solrDocument.getFieldValue(Constants.PREDICATE_TYPE));
    solrResult.setPath((String) solrDocument.getFieldValue(Constants.PN_ID));
    String language = (String) solrDocument.getFieldValue("language");
    solrResult.setLanguage(language);
    String region = (String) solrDocument.getFieldValue("region");
    if (StringUtils.isNotEmpty(region)) {
      solrResult.setRegion(getTranslation(region, language));
    }

    List<String> source =(List<String>) solrDocument.getFieldValue("source");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

    Date startDate = (Date) solrDocument.getFieldValue("startdate");
    if (startDate != null) {
      solrResult.setStartdate(sdf.format(startDate));
    }

    Date endDate = (Date) solrDocument.getFieldValue("enddate");
    if (endDate != null) {
      solrResult.setEnddate(sdf.format(endDate));
    }

    solrResult.setStarttime((String) solrDocument.getFieldValue("starttime"));
    solrResult.setEndtime((String) solrDocument.getFieldValue("endtime"));

    Collection<Object> tags = solrDocument.getFieldValues("tags");
    if (tags != null) {
      List<String> tagList = tags.stream().filter(Objects::nonNull).map(Object::toString)
          .collect(Collectors.toList());
      solrResult.setTags(tagList);
    }
    Collection<Object> categories = solrDocument.getFieldValues("categories");
    if (categories != null) {
      List<String> categoryList = categories.stream().filter(Objects::nonNull)
          .map(Object::toString).collect(Collectors.toList());


      ResourceResolver resolver = userService.getResourceResolver();
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      Locale locale = new Locale(solrResult.getUrl().substring(1, 3));

      if (null == locale) {
        solrResult.setCategories(categoryList);
        return solrResult;
      }

      List<String> catList = new ArrayList<>();
      for (String tag : categoryList) {
        catList.add(CommonUtils.getTagName("sauditourism:searchCategories/" + tag, tagManager, locale));
      }

      if (CollectionUtils.isNotEmpty(catList)) {
        solrResult.setContentTypeTitle(catList.get(0));
      }


      solrResult.setCategories(catList);
    }
    return solrResult;
  }

  @Override
  public SolrSuggestionResponse suggest(SolrRequest solrRequest) {
    if (StringUtils.isEmpty(solrRequest.getLocale()) || StringUtils
        .isEmpty(solrRequest.getSuggestion())) {
      return null;
    }

    SolrClient client = getHttpSolrClient(solrRequest.getLocale());

    SolrSuggestionResponse solrResponse = new SolrSuggestionResponse();
    solrResponse.setData(suggestRequest(solrRequest, client));
    List<SolrResult> results = titleSearch(solrRequest);
    if (solrRequest.getSource().equals(Constants.SOLR_WEB_SOURCE)) {
      solrResponse.setResults(results);
    } else if (solrRequest.getSource().equals(Constants.SOLR_MOBILE_SOURCE)) {
      solrResponse.setResults(SolrSearchService.toAppListSearchResultModel(results));
    }
    return solrResponse;
  }

  /**
   * This method is used to search pages via SOLR.
   *
   * @param solrRequest SolrRequest
   * @return list of SolrResult
   */
  private List<SolrResult> titleSearch(SolrRequest solrRequest) {
    SolrClient client = getHttpSolrClient(solrRequest.getLocale());

    String query = solrRequest.getSuggestion().trim().replace(Constants.SPACE, " AND ");
    query += "*";

    SolrQuery solrQuery = new SolrQuery(query);
    solrQuery.setParam("df", Constants.PN_TITLE);
    solrQuery.setParam("rows", "2");
    solrQuery.addFilterQuery("source:" + solrRequest.getSource());
    solrQuery.addFilterQuery("categories: Highlight");
    try {
      QueryResponse response = client.query(solrQuery);
      return getSolrResult(response.getResults());
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  /**
   * Send request to SOLR server.
   *
   * @param solrRequest solrRequest
   * @param client      SolrClient
   * @return list of results
   */
  private List<String> suggestRequest(SolrRequest solrRequest, SolrClient client) {
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setRequestHandler("/suggest");
    solrQuery.setParam("suggest.q", solrRequest.getSuggestion());
    solrQuery.setParam("suggest.cfq", solrRequest.getSource());
    solrQuery.setParam("suggest.count",
        String.valueOf(solrRequest.getLimit() + NumberConstants.CONST_TEN));
    try {
      QueryResponse response = client.query(solrQuery);
      List<String> suggestions =
          response.getSuggesterResponse().getSuggestedTerms().get(Constants.SOLR_FREETEXTSUGGESTER);

      return removeDuplicates(suggestions, solrRequest.getLimit());
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  /**
   * This method is used to get HttpSolrClient.
   *
   * @param lang String
   * @return HttpSolrClient from map
   */
  @Override
  public HttpSolrClient getHttpSolrClient(String lang) {

    if (!solrUrl.equals(solrConfigurationService.getSolrUrl()) || !solrCore
        .equals(solrConfigurationService.getSolrCore())) {
      httpSolrClientMap.clear();
    }

    if (httpSolrClientMap.containsKey(lang)) {
      return httpSolrClientMap.get(lang);
    }

    solrUrl = solrConfigurationService.getSolrUrl();
    solrCore = solrConfigurationService.getSolrCore();
    HttpSolrClient solrClient = createSolrClient(lang);
    httpSolrClientMap.put(lang, solrClient);
    LOGGER.info("Created solr client with url " + solrClient.getBaseURL());
    return solrClient;
  }

  @Override
  public boolean deletePageFromSolr(String path) throws MmCoreException {
    if (path != null) {
      try {
        String language;
        if (!StringUtils.isEmpty(solrConfigurationService.getMobilePath()) && path
            .startsWith(solrConfigurationService.getMobilePath())) {
          language =
              CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);
        } else {
          language =
              CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        }
        HttpSolrClient server = getHttpSolrClient(language);
        server.deleteById(path);
        server.commit();
        return true;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new MmCoreException(e);
      }
    }

    return false;
  }

  @Override
  public boolean deleteAllFromSolr(String path) throws MmCoreException {
    if (path != null) {
      try {
        String language =
            CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        HttpSolrClient server = getHttpSolrClient(language);
        server.deleteByQuery("*:*");
        server.commit();
        return true;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new MmCoreException(e);
      }
    }

    return false;
  }

  /**
   * This method removes duplicates from list.
   *
   * @param source list of strings
   * @param limit  max count of suggestions
   * @return list without duplicates
   */
  private List<String> removeDuplicates(List<String> source, int limit) {
    List<String> result = new ArrayList<>();

    source.forEach(text -> {
      text = text.trim();
      if (result.size() < limit && !isDuplicate(result, text)) {
        result.add(text);
      }
    });
    return result;
  }

  /**
   * This method returns true if the list contains the value.
   *
   * @param values   list of strings
   * @param newValue value for search
   * @return boolean
   */
  private boolean isDuplicate(List<String> values, String newValue) {
    for (String value : values) {
      if (value.equalsIgnoreCase(newValue)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method creates HttpSolrClient.
   *
   * @param lang String
   * @return HttpSolrClient
   */
  private HttpSolrClient createSolrClient(String lang) {
    String url = solrUrl + solrCore + "_" + lang;
    return new HttpSolrClient(url);
  }

  /**
   * This method is used get check if the server is alive.
   *
   * @param lang language
   * @return boolean value
   */
  @Override
  public boolean solrTest(String lang) {
    HttpSolrClient httpSolrClient = getHttpSolrClient(lang);
    boolean available = false;
    try {
      available = httpSolrClient.ping().getStatus() == 0;
    } catch (SolrServerException | IOException e) {
      LOGGER.error("Could not connect to Solr storage");
    }
    return available;
  }

  /**
   * This method is used get IndexProperties from node and fill json array.
   *
   * @param source           Node
   * @param texts            array to fill
   */
  private void buildNodeContent(Node source, List<String> texts) {
    try {
      if (source.hasProperty("enableManualAuthoring") && !source.getProperty("enableManualAuthoring").getBoolean()) {
        return;
      }
      for (String fieldName : solrConfigurationService.getTextProperties()) {
        if (source.hasProperty(fieldName)) {
          javax.jcr.Property prop = source.getProperty(fieldName);
          if (!prop.isMultiple()) {
            texts.add(prop.getString());
          }
        }
      }
    } catch (RepositoryException e) {
      LOGGER.error("error in updating property {}", e.getMessage());
    }
  }

  /**
   * This method is used get IndexProperties from content fragment node and fill json array.
   *
   * @param contentFragment content fragment.
   * @return list of string
   */
  private List<String> addContentFragmentForIndex(ContentFragment contentFragment) {
    List<String> texts = new ArrayList<>();
    if (Objects.isNull(contentFragment)) {
      return Collections.emptyList();
    }
    for (String fieldName : solrConfigurationService.getCfTextProperties()) {
      if (!contentFragment.hasElement(fieldName)) {
        continue;
      }
      String elementValue = getElementValue(contentFragment, fieldName, String.class, PrimConstants.BLANK);
      if (StringUtils.isNotBlank(elementValue)) {
        texts.add(elementValue);
      }
    }
    return texts;
  }

  /**
   * This method is used to add a default category by ResourceType.
   *
   * @param resourceType resourceType
   * @param pageType    pageType
   * @param solrArticle  object
   */
  private void addDefaultSearchCategory(String resourceType, String pageType, SolrArticle solrArticle) {

    if (Objects.isNull(solrArticle.getCategories())) {
      return;
    }
    String defaultCategory = AppUtils.getDefaultSearchCategory(resourceType);
    if (StringUtils.isNotBlank(defaultCategory)) {
      solrArticle.getCategories().add(defaultCategory);
    } else if (Objects.nonNull(pageType)) {
      solrArticle.getCategories().add(pageType);
    }
  }

  /**
   * This method is used to build Pagination for group.
   *
   * @param group Group
   * @param limit page size
   * @return Pagination object
   */
  private Pagination buildPagination(Group group, int limit) {
    Pagination pagination = new Pagination();
    pagination.setTotal((int) group.getResult().getNumFound());
    pagination.setOffset((int) group.getResult().getStart());
    pagination.setLimit(limit);
    return pagination;
  }

  @Override
  public SolrWithGroupResponse<SolrResult> solrSearchWithGroup(SolrRequest solrRequest)
      throws TechnicalMmCoreException {
    SolrWithGroupResponse<SolrResult> solrWithGroupResponse = new SolrWithGroupResponse<>();
    List<SolrGroupSection<SolrResult>> sections = new ArrayList<>();
    SolrClient client = getHttpSolrClient(solrRequest.getLocale());
    List<DictItem> types =
        SearchUtils.getSearchCategoriesFromTags(userService.getResourceResolver(), solrRequest.getLocale());

    if (StringUtils.isBlank((solrRequest.getType()))) {
      solrRequest.setType(types.stream().map(DictItem::getCode).collect(Collectors.joining(",")));
    }

    boolean includeArticles = false;
    if (null != solrRequest.getIncludeArticles()) {
      includeArticles = Boolean.parseBoolean(solrRequest.getIncludeArticles());
    }

    SolrQuery solrQuery = buildSolrQueryWithGrouping(solrRequest, includeArticles);
    try {

      QueryResponse response = client.query(solrQuery);
      GroupResponse gr = response.getGroupResponse();
      for (GroupCommand gc : gr.getValues()) {
        for (Group g : gc.getValues()) {
          SolrGroupSection section = new SolrGroupSection<>();
          section.setData(getSolrResult(g.getResult()));
          section.setPagination(buildPagination(g, solrRequest.getLimit()));
          String typeSection = StringUtils.removeStart(g.getGroupValue(), "categories:");
          section.setType(typeSection);
          section.setTitle(
              types.stream()
                  .filter(d -> d.getCode().equalsIgnoreCase(typeSection))
                  .map(DictItem::getValue).findAny().orElse(Constants.BLANK));
          if (!g.getResult().isEmpty()) {
            sections.add(section);
          }
        }
      }
    } catch (Exception e) {
      throw new TechnicalMmCoreException(e);
    }
    solrWithGroupResponse.setSuggestion(spellCheck(solrRequest.getQuery(), client));
    solrWithGroupResponse.setSections(getOrderBySection(solrRequest, sections));

    return solrWithGroupResponse;
  }

  /**
   * This method is used to build SolrQuery with grouping.
   *
   * @param solrRequest     SolrRequest
   * @param includeArticles excludeArticle
   * @return SolrQuery object
   */
  private SolrQuery buildSolrQueryWithGrouping(SolrRequest solrRequest, boolean includeArticles) {
    String query = solrRequest.getQuery();
    String rows = solrRequest.getLimit().toString();
    SolrQuery solrQuery = new SolrQuery(query);
    solrQuery.setParam("rows", rows);
    solrQuery.setParam("start", solrRequest.getOffset().toString());
    solrQuery.addFilterQuery("source:" + solrRequest.getSource());

    if (!includeArticles) {
      solrQuery.addFilterQuery("-type:article");
    }


    solrQuery.setParam("defType", "edismax");

    if (solrRequest.getSort() != null && solrRequest.getSort()
        .equalsIgnoreCase(Constants.DATE_TYPE)) {
      solrQuery.setParam("sort", "publishdate desc, score desc");
    } else {
      solrQuery.setParam("sort", "priority desc, score desc");
    }

    String boostTitle = "titles^" + solrConfigurationService.getTitleScore();
    String boostSubtitle = "subtitles^" + solrConfigurationService.getSubtitleScore();
    String boostExact = solrConfigurationService.getExactMatchScore();

    solrQuery.setParam("qf", boostTitle, boostSubtitle, "text");
    solrQuery
        .setParam("pf", "titles^" + boostExact, "subtitles^" + boostExact, "text^5");

    solrQuery.setParam("group", true);
    solrQuery.setParam("group.limit", rows);
    solrQuery.setParam("group.offset", solrRequest.getOffset().toString());

    if (!StringUtils.isEmpty(solrRequest.getType())) {
      List<String> typeList = Arrays.asList(solrRequest.getType().split(","));
      String[] prefixedTypeList = typeList.stream().map(s -> "categories:" + s).toArray(String[]::new);
      solrQuery.setParam("group.query", prefixedTypeList);
    }

    return solrQuery;
  }

  /**
   * This method is used to check if App pages should be indexed content should be ignored.
   *
   * @param resourceType resourceType
   * @return boolean
   */
  private boolean inAppIgnoreList(final String resourceType) {
    if (ArrayUtils.isEmpty(solrConfigurationService.getAppIgnoredResourceTypes())) {
      return false;
    }
    return Arrays.stream(solrConfigurationService.getAppIgnoredResourceTypes()).anyMatch(resourceType::equals);
  }

  /**
   * This method is used to check if Web pages should be indexed for mobile
   *
   * @param resourceType resourceType
   * @param isExcludedMobileIndex isExcludedMobileIndex
   * @return boolean
   */
  private boolean inMobileAutoList(final String resourceType, final boolean isExcludedMobileIndex) {
    if (isExcludedMobileIndex) {
      return false;
    }
    if (ArrayUtils.isEmpty(solrConfigurationService.getMobileIncludedResourceTypes())) {
      return false;
    }
    return Arrays.stream(solrConfigurationService.getMobileIncludedResourceTypes()).anyMatch(resourceType::equals);
  }


  /**
   * This method is used to get element value from content fragment.
   *
   * @param cf
   * @param elementName
   * @param type
   * @param defaultValue
   * @return
   * @param <T>
   */
  private <T> T getElementValue(ContentFragment cf, String elementName, Class<T> type, @NotNull T defaultValue) {
    if (cf == null) {
      return defaultValue;
    }

    if (cf.hasElement(elementName)) {
      FragmentData elementData = cf.getElement(elementName).getValue();
      if (elementData != null) {
        return elementData.getValue(type);
      }
    }

    return defaultValue;
  }

  /**
   * This method converts SolrResult to MobileSolrSearchResults.
   *
   * @param solrResponse with SolrResult list
   * @return SolrResponse with MobileSearchResult list
   */
  @Override
  public SolrResponse<MobileSearchResults> getSearchAndSuggestions(SolrResponse<SolrResult> solrResponse, String locale) {
    SolrResponse<MobileSearchResults> mobileResponse = new SolrResponse<>();
    List<String> trending = nativeAppHomepageService.getTrending(locale);
    List<MobileSearchResults> suggestions = nativeAppHomepageService.getSuggestions(locale);
    mobileResponse.setSuggestions(suggestions);
    mobileResponse.setTrending(trending);
    mobileResponse.setTotalCount(solrResponse.getPagination().getTotal());
    mobileResponse.setAutocompleteResults(Collections.emptyList());
    mobileResponse.setQueryResults(new ArrayList<>());

    for (SolrResult solrResult : solrResponse.getData()) {
      MobileSearchResults.MobileSearchResultsBuilder builder = MobileSearchResults.builder();

      MobileSearchResults.MediaGallery.MediaGalleryBuilder mediaGalleryBuilder =
        MobileSearchResults.MediaGallery.builder();

      mediaGalleryBuilder.type(Constants.PN_IMAGE.toUpperCase());
      String url = LinkUtils.getAuthorPublishAssetUrl(userService.getResourceResolver(), solrResult.getFeatureImage(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
      mediaGalleryBuilder.url(url);

      var id = solrResult.getUrl();
      var type = solrResult.getType();

      // Handle Auto Item for Mobile
      if (!"item".equalsIgnoreCase(type) && !AppUtils.isDeepLinkType(type)) {
        id = solrResult.getPath();
        type = "auto";
      }

      builder.id(id);
      builder.title(solrResult.getTitle());
      builder.mediaGallery(mediaGalleryBuilder.build());
      builder.type(type);
      builder.contentTypeTitle(solrResult.getContentTypeTitle());
      mobileResponse.getQueryResults().add(builder.build());
    }

    return mobileResponse;
  }

  /**
   * This method converts SolrSuggestionResponse to MobileSolrResults.
   *
   * @param suggestionsResponse with SolrSuggestions list
   * @return SolrResponse with MobileSearchResult list
   */
  @Override
  public SolrResponse<MobileSearchResults> getSearchAndSuggestions(SolrSuggestionResponse suggestionsResponse, String locale) {
    SolrResponse<MobileSearchResults> mobileResponse = new SolrResponse<>();
    List<String> trending = nativeAppHomepageService.getTrending(locale);
    List<MobileSearchResults> suggestions = nativeAppHomepageService.getSuggestions(locale);
    mobileResponse.setSuggestions(suggestions);
    mobileResponse.setTrending(trending);
    mobileResponse.setAutocompleteResults(suggestionsResponse.getData());
    mobileResponse.setQueryResults(new ArrayList<>());
    mobileResponse.setTotalCount(mobileResponse.getQueryResults().size());

    return mobileResponse;
  }
}

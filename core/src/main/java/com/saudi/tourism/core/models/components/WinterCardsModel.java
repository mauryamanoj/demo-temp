
package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.beans.RoadTripData;
import com.saudi.tourism.core.beans.WinterCardsData;
import com.saudi.tourism.core.beans.WinterCardsExperienceIDResponse;
import com.saudi.tourism.core.beans.WinterCardsExperienceResponse;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WinterCardsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for WinterCardsModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class WinterCardsModel {
  /**
   * header.
   */
  @ChildResource
  @Expose
  private ComponentHeading header;
  /**
   * Injecting component type value from the component.
   */
  @Expose
  @ValueMapValue
  private String componentType;

  /**
   * Injecting card style of the component.
   */
  @Expose
  @ValueMapValue
  private String cardStyle;

  /**
   * Injecting card buy button text.
   */
  @Expose
  @ValueMapValue
  private String buy;
  /**
   * Road Trip End Point.
   */
  @Expose
  private String roadTripEndpoint;
  /**
   * Injecting card view all button text.
   */
  @Expose
  @ValueMapValue
  private String viewAllText;

  /**
   * Injecting card view all button link.
   */
  @Expose
  @ValueMapValue
  private String viewAllLink;

  /**
   * Duration Unit.
   */
  @Expose
  @ValueMapValue
  private String durationUnit;
  /**
   * from label.
   */
  @ValueMapValue
  @Expose
  private String from;
  /**
   * currency.
   */
  @ValueMapValue
  @Expose
  private String sar;
  /**
   * off label.
   */
  @ValueMapValue
  @Expose
  private String off;
  /**
   * person label.
   */
  @ValueMapValue
  @Expose
  private String person;
  /**
   * not available label.
   */
  @ValueMapValue
  @Expose
  private String notAvailable;
  /**
   * free label.
   */
  @ValueMapValue
  @Expose
  private String free;
  /**
   * book now label.
   */
  @ValueMapValue
  @Expose
  private String bookNow;

  /**
   * city.
   */
  @ValueMapValue
  @Expose
  private String winterPackagesCardsCity;

  /**
   * By Text.
   */
  @Expose
  @ValueMapValue
  private String byText;

  /**
   * Limit.
   */
  @Expose
  @ValueMapValue
  private Integer limit;

  /**
   * Using WinterCardsService.
   */
  @OSGiService
  private transient WinterCardsService service;

  /**
   * WinterCardsExperienceResponse object.
   */
  @Expose
  private WinterCardsExperienceResponse obj;

  /**
   * WinterCardsExperienceIDResponse object.
   */
  @Expose
  private WinterCardsExperienceIDResponse expId;
  /**
   * Saudi Tourism Configurations.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SaudiTourismConfigs saudiTourismConfig;
  /**
   * Variable for language .
   */
  @Expose
  private String lang;

  /**
   * List of String.
   */
  @Expose
  private final List<WinterCardsData> list = new ArrayList<WinterCardsData>();

  /**
   * List of String.
   */
  @Expose
  private List<RoadTripData> res = new ArrayList<RoadTripData>();

//  /**
//   * List of categories.
//   */
//  private List<String> diningList = new ArrayList<String>();

  /**
   * Constant component type winter2021 .
   */
  private static final String COMPONENT_TYPE = "winter2021";

  /**
   * Constant card style experience .
   */
  private static final String CARD_STYLE_EXPERIENCE = "experience";

  /**
   * Constant card style dining .
   */
  private static final String CARD_STYLE_DINING = "dining";

  /**
   * Constant card style road trip .
   */
  private static final String CARD_STYLE_ROAD_TRIP = "roadTrip";

  /**
   * Constant for page template .
   */
  private static final String TEMPLATE = "/conf/sauditourism/settings/wcm/templates/content-page";

  /**
   * Constant for parent page .
   */
  private static final String PARENT_PAGE = "/content/sauditourism/";

  /**
   * Constant for component node .
   */
  private static final String COMPONENT_NODE = "h02_secondary_hero";

  /**
   * Constant for component resource type.
   */
  private static final String COMPONENT_RESOURCE_TYPE =
      "sauditourism/components/content/h02-secondary-hero/v1/h02-secondary-hero";

  /**
   * Constant for middle layer nodes .
   */
  private static final String MIDDLE_NODE = "/jcr:content/root/responsivegrid";

  /**
   * The Resource resolver.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * Added a child resource.
   */
  @Expose
  @ChildResource
  private List<Resource> diningCards;

  /**
   * The Resource resolver.
   */
  @Reference
  private ResourceResolverFactory resolverFactory;

  /**
   * HY Experience domain.
   */
  @Expose
  private String hYExperienceDomain;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * Post construct init method.
   * @throws IOException IOException.
   */
  @PostConstruct
  protected void init() throws IOException {
    ResourceResolver resourceResolver = request.getResourceResolver();
    HashMap<String, Object> map = new HashMap<String, Object>();
    lang = CommonUtils.getLanguageForPath(request.getResource().getPath());
    map.put("lang", lang);
    Gson gson = new Gson();
    if (componentType != null && componentType.equals(COMPONENT_TYPE)) {
      if (cardStyle != null && cardStyle.equals(CARD_STYLE_EXPERIENCE)) {
        list.clear();
        hYExperienceDomain = saudiTourismConfig.getHYExperienceDomain();
        String response =  service.getExperience(map);
        PageManager p = resourceResolver.adaptTo(PageManager.class);
        obj =  gson.fromJson(response, WinterCardsExperienceResponse.class);
        obj.getData().forEach(item -> {
          WinterCardsData winterData = new WinterCardsData();
          winterData.setName(item.getName());
          winterData.setId(item.getId());
          winterData.setPrice(item.getListPrice());
          winterData.setDiscountPrice(item.getDiscountedPrice());
          winterData.setCity(item.getCity());
          winterData.setImages(item.getBackgroundImages());
          winterData.setPriceType(item.getPriceType());
          winterData.setBackgroundImage(item.getBackgroundImage());
          winterData.setContactName(item.getContactName());
          winterData.setDuration(item.getDuration());
          winterData.setPrimaryDestination(item.getPrimaryDestination());
          winterData.setDurationUnit(item.getDurationUnit());
          winterData.setCategorySlug(item.getCategorySlug());
          winterData.setBookOnline(item.getBookOnline());
          if (item.getId() != null) {
            try {
              HashMap<String, Object> mapObj = new HashMap<String, Object>();
              mapObj.put("lang", lang);
              String apiResponse = service.getExperienceDetails(mapObj, item.getId());
              boolean status = apiResponse.contains("status");
              if (status) {
                expId = gson.fromJson(apiResponse, WinterCardsExperienceIDResponse.class);
                String description = expId.getData().getDescription();
                winterData.setDescription(description);
              }
              String pageName = item.getName().replaceAll("\\s", "");
              Page existingPage = p.getPage(PARENT_PAGE + lang + "/experience-package/" + pageName);
              if (existingPage == null) {
                String detailPagePath = PARENT_PAGE + lang + "/experience-package/" + pageName;
                winterData.setDetailPagePath(detailPagePath);
                Page page = p.create(PARENT_PAGE + lang + "/experience-package", pageName,
                    TEMPLATE, item.getName(), true);
                String pagePath = page.getPath() + MIDDLE_NODE;
                Resource resourcePath = resourceResolver.getResource(pagePath);
                Node pageNode = resourcePath.adaptTo(Node.class);
                Node node = pageNode.addNode(COMPONENT_NODE);
                node.setProperty("hideFav", true);
                node.setProperty("title", item.getName());
                node.setProperty("sling:resourceType",
                    COMPONENT_RESOURCE_TYPE);
                node.setProperty("titleWeight", "h1");
                Node imageNode = node.addNode("image");
                imageNode.setProperty("fileReference", item.getBackgroundImage());
                imageNode.getSession().save();
                node.getSession().save();
              } else {
                String detailPagePath = PARENT_PAGE + lang + "/experience-package/" + pageName;
                winterData.setDetailPagePath(detailPagePath);
              }
            } catch (IOException | WCMException | RepositoryException e) {
              e.printStackTrace();
            }
          }
          list.add(winterData);

        });
      } else if (cardStyle != null && cardStyle.equals(CARD_STYLE_ROAD_TRIP)) {
        roadTripEndpoint = saudiTourismConfig.getRoadTripEndpoint();
        res.clear();
        res = service.getRoadTripScenariosFromApi(Integer.parseInt(String.valueOf(limit)), lang,
          winterPackagesCardsCity);
      } else if (cardStyle != null && cardStyle.equals(CARD_STYLE_DINING)) {
        StringBuilder sb = new StringBuilder();
        diningCards.forEach(card -> {
          String path = card.getPath();
          try {
            Resource resource =  resourceResolver.getResource(path);
            ValueMap value = resource.adaptTo(ValueMap.class);
            String category = value.get("subCategory", String.class);
            //diningList.add(category);
            sb.append(category);
            sb.append(", ");

          } catch (Exception e) {
            e.printStackTrace();
          }
        });
        map.put("sub_category", sb);
        list.clear();
        String response =  service.getExperience(map);
        obj =  gson.fromJson(response, WinterCardsExperienceResponse.class);
        obj.getData().forEach(item -> {
          WinterCardsData winterData = new WinterCardsData();
          winterData.setName(item.getName());
          winterData.setId(item.getId());
          winterData.setCity(item.getCity());
          winterData.setImages(item.getBackgroundImages());
          winterData.setBackgroundImage(item.getBackgroundImage());
          winterData.setPrimaryDestination(item.getPrimaryDestination());
          list.add(winterData);
        });
      }
    }
    viewAllLink = LinkUtils.getAuthorPublishUrl(resourceResolver,
                  viewAllLink, settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

package com.saudi.tourism.core.models.components.pricewidget.v1;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;

/**
 * PriceWidgetModel.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class PriceWidgetModel {

  /**
   * Resource Resolver.
   */
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @Inject
  private SlingSettingsService settingsService;

  /**
   * Ticket Type.
   */
  @Expose
  private String ticketType;

  /**
   * Price.
   */
  @Expose
  private String price;

  /**
   * Price Suffix.
   */
  @Expose
  private String priceSuffix;

  /**
   * Text.
   */
  @Expose
  private String text;

  /**
   * Button Label.
   */
  @Expose
  private String buttonLabel;

  /**
   * Url.
   */
  @Expose
  private String url;

  @PostConstruct
  void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return;
    }

    final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfPath);
    if (cfResource == null) {
      return;
    }

    final var cfModel = cfResource.adaptTo(PriceWidgetCFModel.class);
    if (cfModel == null) {
      return;
    }

    ticketType = cfModel.getTicketType();
    price = cfModel.getTicketPrice();
    priceSuffix = cfModel.getTicketPriceSuffix();
    text = cfModel.getTicketDetails();
    buttonLabel = cfModel.getTicketCTALabel();
    var ticketCTALink = cfModel.getTicketCTALink();
    if (StringUtils.isNotEmpty(ticketCTALink)) {
      url = LinkUtils.getAuthorPublishUrl(currentResource.getResourceResolver(), ticketCTALink,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

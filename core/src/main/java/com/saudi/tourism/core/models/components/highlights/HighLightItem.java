package com.saudi.tourism.core.models.components.highlights;


import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.models.components.travellerquotes.v1.QuotesSlide;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;


/**
 * HighLight Item.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class HighLightItem {

  /**
   * The Sling settings service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Short Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Short Title.
   */
  @Expose
  private String shortTitle;

  /**
   * Description.
   */
  @ValueMapValue
  private String highlightShortDesc;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * QuotesSlide.
   */
  @Expose
  private QuotesSlide travellerQuote;

  /**
   * Full Title.
   */
  @ValueMapValue
  private String descriptionTitle;

  /**
   * Full Title.
   */
  @Expose
  private String fullTitle;

  /**
   * Description tagline.
   */
  @ValueMapValue
  private String descriptionTagline;

  /**
   * Full Description.
   */
  @Expose
  private String fullDescription;

  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Poster image.
   */
  @ChildResource
  @Expose
  private Image posterImage;

  /**
   * Video.
   */
  @ChildResource
  private Video video;

  /**
   * Video.
   */
  @Expose
  private Video videoLink;

  /**
   * Having a video.
   */
  @ValueMapValue
  @Expose
  private boolean hasVideo;

  /**
   * Description Tagline Type.
   */
  @ValueMapValue
  @Expose
  private boolean descriptionTaglineType;

  /**
   * Event Schedule link text.
   */
  @ValueMapValue
  private String eventScheduleLinkText;

  /**
   * Event Schedule link.
   */
  @ValueMapValue
  private String eventScheduleLink;

  /**
   * Event Schedule link target.
   */
  @ValueMapValue
  private Boolean eventScheduleLinkTarget;

  /**
   * Event Schedule link icon.
   */
  @ValueMapValue
  private String eventScheduleIcon;

  /**
   * Variable to hold Event Button object.
   */
  @Expose
  private Button eventButton;

  /**
   * Buy Tickets Link Text.
   */
  @ValueMapValue
  private String buyTicketsLinkText;

  /**
   * Buy Tickets Link.
   */
  @ValueMapValue
  private String buyTicketsLink;

  /**
   * Buy tickets link target.
   */
  @ValueMapValue
  private Boolean buyTicketsLinkTarget;

  /**
   * Buy tickets Icon.
   */
  @ValueMapValue
  private String buyTicketsIcon;


  /**
   * Variable to hold Ticket Button object.
   */
  @Expose
  private Button ticketButton;


  /**
   * View Map link text.
   */
  @ValueMapValue
  private String viewMapLinkText;

  /**
   * View Map link.
   */
  @ValueMapValue
  private String viewMapLink;

  /**
   * View Map link target.
   */
  @ValueMapValue
  private Boolean viewMapLinkTarget;

  /**
   * map Icon.
   */
  @ValueMapValue
  private String viewMapIcon;

  /**
   * Variable to hold Map Button object.
   */
  @Expose
  private Button mapButton;


  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resolver;

  /**
   * init.
   */
  @PostConstruct
  public void init() {
    setShortTitle(getTitle());
    setFullDescription(getDescription());
    setDescription(getHighlightShortDesc());
    setFullTitle(getDescriptionTitle());
    if (StringUtils.isNotBlank(getDescriptionTagline())) {
      travellerQuote = new QuotesSlide();
      travellerQuote.setQuote(getDescriptionTagline());
    }
    setVideoLink(video);
    eventButton = new Button();
    eventButton.setText(eventScheduleLinkText);
    eventButton.setUrl(LinkUtils
        .getAuthorPublishUrl(resolver, eventScheduleLink, settingsService.getRunModes().contains(
          Externalizer.PUBLISH)));
    eventButton.setExternal(eventScheduleLinkTarget);
    eventButton.setIcon(eventScheduleIcon);
    ticketButton = new Button();
    ticketButton.setText(buyTicketsLinkText);
    ticketButton.setUrl(LinkUtils
        .getAuthorPublishUrl(resolver, buyTicketsLink, settingsService.getRunModes().contains(
        Externalizer.PUBLISH)));
    ticketButton.setExternal(buyTicketsLinkTarget);
    ticketButton.setIcon(buyTicketsIcon);
    mapButton = new Button();
    mapButton.setText(viewMapLinkText);
    mapButton.setUrl(LinkUtils
        .getAuthorPublishUrl(resolver, viewMapLink, settingsService.getRunModes().contains(
        Externalizer.PUBLISH)));
    mapButton.setExternal(viewMapLinkTarget);
    mapButton.setIcon(viewMapIcon);
  }

}

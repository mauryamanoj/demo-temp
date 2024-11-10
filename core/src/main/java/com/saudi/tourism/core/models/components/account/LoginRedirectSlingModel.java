package com.saudi.tourism.core.models.components.account;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class, SlingHttpServletResponse.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class LoginRedirectSlingModel {

  /**
   * The Request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * The Response.
   */
  @SlingObject
  private SlingHttpServletResponse response;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {
    try {

      response.sendRedirect("https://dev-zjgb0z4j.eu.auth0"
          + ".com/login?state=g6Fo2&client=nHmI&protocol=oauth2&redirect_uri=" + request
          .getRequestURL() + "&response_type" + "=code&ui_locales=en-NL");

    } catch (Exception ex) {
      LOGGER.error(" Error in EventSliderModel");
    }
  }

}

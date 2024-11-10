package com.saudi.tourism.core.services;

import org.apache.sling.api.SlingHttpServletResponse;

/**
 * All methods for EmailNotificationService.
 */
public interface EmailNotificationService {
  /**
   *  Returns the Email Template notifications.
   *  @param service sling http request
   *  @param response sling http response
   *  @param locale String locale
   *  @return Sms Notification template text
   */
  String getEmailTemplate(UserService service, SlingHttpServletResponse response, String locale);

}

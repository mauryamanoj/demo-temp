package com.saudi.tourism.core.services;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * All methods for SMSNotificationService.
 */
public interface SMSNotificationService {
  /**
   *  Returns the SMS Template notifications.
   *  @param userService sling http request
   *  @param response sling http response
   *  @param locale String locale
   *  @return Sms Notification template text
   */
  String getSmsTemplate(UserService userService, SlingHttpServletResponse response, String locale);

}

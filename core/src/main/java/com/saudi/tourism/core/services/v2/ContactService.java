package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.beans.ContactResponse;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * ContactService.
 */
public interface ContactService {

  /**
   * Method getContactInfosWithInfoItems .
   *
   * @param request    request
   * @param localePath localePath
   * @return json
   */
  ContactResponse getContactInfos(SlingHttpServletRequest request, String localePath);
}

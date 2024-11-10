package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.beans.ContactResponse;
import com.saudi.tourism.core.models.app.contact.AppContactPageModel;
import com.saudi.tourism.core.services.v2.ContactService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * Contact Service.
 */
@Component(service = ContactService.class)
public class ContactServiceImpl implements ContactService {
  @Override
  public ContactResponse getContactInfos(final SlingHttpServletRequest request, final String localePath) {


    AppContactPageModel appContactPageModel = getContactInformation(request, localePath);
    if (appContactPageModel != null) {
      return ContactResponse.builder()
          .contacts(appContactPageModel.getContacts())
          .services(appContactPageModel.getServices())
          .telephone(appContactPageModel.getTelephone())
          .embassy(appContactPageModel.getEmbassy())
          .search(appContactPageModel.getSearch())
          .webMappingPath(appContactPageModel.getWebMappingPath())
          .build();
    }

    return null;
  }

  /**
   * Get the contact information for a locale.
   *
   * @param request    request
   * @param localePath localePath code
   * @return model
   */
  private AppContactPageModel getContactInformation(final SlingHttpServletRequest request, final String localePath) {

    Resource resource =
        request.getResourceResolver().getResource(localePath + Constants.APP_CONTACT_PAGE + JcrConstants.JCR_CONTENT);

    AppContactPageModel model = null;
    if (resource != null) {
      model = resource.adaptTo(AppContactPageModel.class);
    }
    return model;
  }
}

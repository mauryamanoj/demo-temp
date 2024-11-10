package com.saudi.tourism.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.saudi.tourism.core.beans.bestexperience.BestExperience;
import com.saudi.tourism.core.beans.bestexperience.Experience;
import com.saudi.tourism.core.beans.bestexperience.ExperienceData;
import com.saudi.tourism.core.beans.bestexperience.ExperienceDetails;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.saudi.tourism.core.servlets.BestExperiencesServlet.SERVLET_PATH;

/**
 * Best Experiences Servlet.
 */
@Component(service = Servlet.class,
        property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Events Servlet",
                ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                        + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                        + SERVLET_PATH
        })
@Slf4j
public class BestExperiencesServlet extends SlingAllMethodsServlet {

    /* Sample API call.
    http://localhost:4502/bin/api/v1/bestexperience?locale=en&startDate=2022-03-01
    &endDate=2022-03-31&city=jeddah&experiencePagePath=/en/summer-vibes/package-details */

  /**
   * SERVLET API PATH.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/bestexperience";

  /**
   * Constant Success.
   */
  private static final String SUCCESS = "Success";

  /**
   * Constant EXPERIENCE PAGE PATH.
   */
  private static final String EXPERIENCE_PAGE_PATH = "experiencePagePath";

  /**
   * SimpleDate Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX.
   */
  private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

  /**
   * SimpleDate Format yyyy-MM-dd HH:mm.
   */
  private final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  /**
   * SimpleDateFormat yyyy-MM-dd.
   */
  private final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Event Service.
   */
  @Reference
  private transient EventService eventService;

  /**
   * Experience Service.
   */
  @Reference
  private transient ExperienceService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
          throws ServletException, IOException {

    EventsRequestParams eventsRequestParams =
            new Convert<>(request, EventsRequestParams.class).getRequestData();
    List<BestExperience> experiences = new ArrayList<>();

    try {
      if (!StringUtils.isBlank(eventsRequestParams.getLocale())) {
        setEvents(eventsRequestParams, experiences);
        setPackages(request, experiences);

        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), experiences);
      } else {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
                new ResponseMessage(MessageType.ERROR.getType(),
                        Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE));
      }

    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.EVENT_DETAIL_RES_TYPE,
              e.getLocalizedMessage(), e);
    }
  }

  /**
   * This method with Set Events for Best Experiences.
   *
   * @param eventsRequestParams EventsRequestParams.
   * @param experiences         BestExperience.
   * @throws RepositoryException RepositoryException.
   */
  private void setEvents(EventsRequestParams eventsRequestParams,
                         List<BestExperience> experiences) throws RepositoryException {
    EventListModel model = eventService.getFilteredEvents(eventsRequestParams);
    List<EventDetail> eventsData = model.getData();

    if (!eventsData.isEmpty()) {
      for (EventDetail eventDetail : eventsData) {
        BestExperience event = new BestExperience();
        event.setTitle(eventDetail.getTitle());
        event.setDescription(eventDetail.getShortDescription());
        event.setCalendarStartDate(convertDateStringFormat(sdf1, eventDetail.getCalendarStartDate()));
        event.setCalendarEndDate(convertDateStringFormat(sdf1, eventDetail.getCalendarEndDate()));
        event.setCity(eventDetail.getCity());

        String s7featureEventImage = eventDetail.getS7featureEventImage();
        if (StringUtils.isNotBlank(s7featureEventImage)) {
          event.setCardImage(s7featureEventImage);
        } else {
          event.setCardImage(eventDetail.getFeatureEventImage());
        }

        Link eventLink = eventDetail.getEventLink();

        if (eventLink != null) {
          event.setLink(eventLink.getUrl());
          event.setLinkText(eventLink.getCopy());
        } else {
          event.setLink(eventDetail.getLink());
          event.setLinkText("Learn More");
        }

        experiences.add(event);
      }
    }
  }

  /**
   * This method with Set Packages from Hala yalla for Best Experiences.
   *
   * @param request     SlingHttpServletRequest.
   * @param experiences BestExperience.
   * @throws IOException IOException.
   */
  private void setPackages(SlingHttpServletRequest request, List<BestExperience> experiences) throws IOException {
    JsonElement packageJsonElement = (JsonElement) service.getAllExperiences(RestHelper.getParameters(request));

    if (packageJsonElement != null) {
      Gson gson = new Gson();
      Experience experience = gson.fromJson(packageJsonElement.toString(), Experience.class);

      List<ExperienceData> data = experience.getData();
      if (StringUtils.equals(SUCCESS, experience.getStatus()) && data != null && !data.isEmpty()) {
        for (ExperienceData experienceData : data) {
          BestExperience experiencePackage = new BestExperience();
          experiencePackage.setTitle(experienceData.getName());
          experiencePackage.setCardImage(experienceData.getBackgroundImage());
          experiencePackage.setCity(experienceData.getCity());
          String id = experienceData.getId();

          String experiencePageDetailsPath = request.getParameter(EXPERIENCE_PAGE_PATH);

          if (StringUtils.isNotBlank(experiencePageDetailsPath)) {
            experiencePackage.setLink(experiencePageDetailsPath + "?experienceId=" + id);
          } else {
            experiencePackage.setLink("/en/summer-vibes/package-details?experienceId=" + id);
          }

          experiencePackage.setLinkText("From " + experienceData.getListPrice() + " SAR");
          setPackageDetails(request, gson, experiencePackage, id);

          experiences.add(experiencePackage);
        }
      }
    }
  }

  /**
   * This method will set Package Details.
   *
   * @param request           SlingHttpServletRequest.
   * @param gson              Gson.
   * @param experiencePackage BestExperience.
   * @param id                String.
   * @throws IOException IOException.
   */
  private void setPackageDetails(SlingHttpServletRequest request, Gson gson,
                                 BestExperience experiencePackage, String id) throws IOException {
    JsonElement packageDetailsJsonElement = (JsonElement) service
            .getExperienceDetails(RestHelper.getParameters(request), id);

    if (packageDetailsJsonElement != null) {
      ExperienceDetails experienceDetails = gson.fromJson(packageDetailsJsonElement.toString(),
              ExperienceDetails.class);
      ExperienceData data = experienceDetails.getData();

      if (StringUtils.equals(SUCCESS, experienceDetails.getStatus())
              && StringUtils.isNotBlank(data.getDescription())) {
        experiencePackage.setDescription(data.getDescription());
      }

      experiencePackage.setCalendarStartDate(convertDateStringFormat(sdf2, data.getStartDate()));
      experiencePackage.setCalendarEndDate(convertDateStringFormat(sdf2, data.getEndDate()));
    }

  }

  /**
   * This Method will convert DateString from one format to YYYY-DD-MM format.
   *
   * @param simpleDateFormat SimpleDateFormat.
   * @param dateString       Date String.
   * @return dateString.
   */
  private String convertDateStringFormat(SimpleDateFormat simpleDateFormat, String dateString) {
    try {
      Date date = simpleDateFormat.parse(dateString);
      return sdf3.format(date);
    } catch (ParseException e) {
      LOGGER.error("Error while parsing date String {} ", dateString, e);
      return StringUtils.EMPTY;
    }
  }

}

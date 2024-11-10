package com.sta.core.vendors.service;

import com.saudi.tourism.core.models.common.ResponseMessage;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;

/**
 * .
 */
public interface EventEntryProxyService {

  /**
   * Forward get event entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io e
   */
  ResponseMessage getEvent(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward add event entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  ResponseMessage createUpdateEvent(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward delete event entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  ResponseMessage deleteEvent(SlingHttpServletRequest request) throws IOException;
  /**
   * Forward update event entry request to author instance and expose it's result.
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  ResponseMessage getEvents(SlingHttpServletRequest request) throws IOException;

  /**
   * Service configuration.
   */
  @ObjectClassDefinition(name = "Saudi EventEntryProxyService Configuration") @interface Configuration {

    /**
     * URI for get event entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Author instance user",
                         type = AttributeType.STRING)
    String encodedAuth();

    /**
     * URI for get event entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Get event entry API uri",
                         defaultValue = "/bin/api/v1/entry/event?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String getEventUri();

    /**
     * URI for post event entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Post event entry API uri",
                         defaultValue = "/bin/api/v1/entry/event?dmc=%s",
                         type = AttributeType.STRING)
    String postEventUri();

    /**
     * URI for post event entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Put event entry API uri",
                         defaultValue = "/bin/api/v1/entry/event?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String putEventUri();

    /**
     * URI for delete event entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Put event entry API uri",
                         defaultValue = "/bin/api/v1/entry/event?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String deleteEventUri();

    /**
     * URI for get event entries list request.
     * @return URI
     */
    @AttributeDefinition(name = "Get event entry list API uri",
                         defaultValue = "/bin/api/v1/entry/event/list?dmc=%s",
                         type = AttributeType.STRING)
    String getEventsUri();
  }

}

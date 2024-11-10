package com.sta.core.vendors;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.saudi.tourism.core.services.UserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Package entry utility methods.
 */
public final class PackageEntryUtils {

  /**
   * Missing parameter error message.
   */
  private static final String MISSING_PARAM_MESSAGE = "Missing mandatory %s parameter";

  /**
   * Get mandatory request parameter or throw exception.
   * @param request servlet request
   * @param name parameter name
   * @return parameter value or exception
   */
  public static String getMandatoryParameter(SlingHttpServletRequest request, String name) {
    return Optional.ofNullable(request.getParameter(name))
        .orElseThrow(() -> new IllegalArgumentException(String.format(MISSING_PARAM_MESSAGE,
            name)));
  }

  /**
   * Get package entry user's resource resolver.
   * @param userService user service
   * @return resource resolver
   */
  public static ResourceResolver getResourceResolver(UserService userService) {
    return userService.getWritableResourceResolver();
  }

  public static void buildLatLong(final Object googleMapLink,
      final Map<String, Object> nodeProperties) {
    if (Objects.nonNull(googleMapLink) && StringUtils.isNotEmpty(googleMapLink.toString())) {
      Pattern pattern = Pattern.compile("@(.*),(.*),");
      Matcher matcher = pattern.matcher(googleMapLink.toString());

      while (matcher.find()) {
        nodeProperties.put("latitude", matcher.group(1));
        nodeProperties.put("longitude", matcher.group(2));
      }
    } else {
      nodeProperties.put("googleMapLink", StringUtils.EMPTY);
      nodeProperties.put("latitude", StringUtils.EMPTY);
      nodeProperties.put("longitude", StringUtils.EMPTY);
    }
  }
  /**
   * Hidden constructor.
   */
  private PackageEntryUtils() { }
}

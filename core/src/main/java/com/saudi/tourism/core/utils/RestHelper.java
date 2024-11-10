package com.saudi.tourism.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.saudi.tourism.core.models.common.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This is to define all singleton objects.
 */
@Slf4j
public final class RestHelper {

  /**
   * The constant OBJECT_MAPPER.
   */
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();

    OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    // TODO Check this
    OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    //Date format
    final var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // StdDateFormat is ISO8601 since jackson 2.9
    OBJECT_MAPPER.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    // Do not output nulls
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

  }

  /**
   * Prevents developer from initiating instance.
   */
  private RestHelper() {

  }

  /**
   * Gets the object mapper.
   * @return the OBJECT_MAPPER
   */
  public static ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }

  /**
   * Gets values of query string if had.
   * @param request a {@link SlingHttpServletRequest}.
   * @return a map which contains list of parameter key and value.
   */
  public static Map<String, Object> getParameters(final SlingHttpServletRequest request) {
    Map<String, Object> parametersMap = new HashMap<>();
    Map<String, String[]> map = request.getParameterMap();
    for (Map.Entry<String, String[]> parameter : map.entrySet()) {
      String[] paramValue = parameter.getValue();
      final boolean currentParamIsLocale = Constants.PN_LOCALE.equals(parameter.getKey());
      final boolean currentParamIsLimit = Constants.LIMIT.equals(parameter.getKey());

      if (paramValue.length > 1 && !currentParamIsLocale && !currentParamIsLimit) {
        parametersMap.put(parameter.getKey(), Arrays.asList(paramValue));
      } else if (paramValue.length == 1 || currentParamIsLocale || currentParamIsLimit) {
        parametersMap.put(parameter.getKey(), paramValue[0]);
      } else {
        parametersMap.put(parameter.getKey(), StringUtils.EMPTY);
      }
    }

    // In case of JSON object sent from front end form submit
    // (locale param still can be passed in url)
    final String locale = (String) parametersMap.get(Constants.PN_LOCALE);
    final boolean hasLocaleInUrl = StringUtils.isNotBlank(locale);
    if (map.isEmpty() || (map.size() == Constants.ONE && hasLocaleInUrl)) {
      StringBuilder paramsStringBuilder = new StringBuilder();
      String paramLine;
      try {
        BufferedReader reader = request.getReader();
        while ((paramLine = reader.readLine()) != null) {
          paramsStringBuilder.append(paramLine);
        }
        // convert json string to key-value map
        final String paramsString = paramsStringBuilder.toString();
        if (StringUtils.isNotBlank(paramsString)) {
          parametersMap = OBJECT_MAPPER.readValue(paramsString,
              new TypeReference<Map<String, Object>>() {
              });
          if (!parametersMap.containsKey(Constants.PN_LOCALE) && hasLocaleInUrl) {
            parametersMap.put(Constants.PN_LOCALE, locale);
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in reading JSON params");
      }

    }
    return parametersMap;
  }

  /**
   * Read the body and returnt the String version of the body.
   * @param bodyStream the body stream
   * @return string
   * @throws IOException the io exception
   */
  private static String readBody(final InputStream bodyStream) throws IOException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(bodyStream));
    StringBuilder body = new StringBuilder();
    String line;

    while ((line = rd.readLine()) != null) {
      body.append(line);
    }
    return body.toString();
  }

  /**
   * Execute method get response message.
   * @param url       the url
   * @param authToken the authentication data
   * @param bearer    the bearer
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodGet(final String url, final String authToken,
      final boolean bearer) throws IOException {
    HttpGet request = new HttpGet(url);
    return executeRequest(request, authToken, bearer);
  }

  /**
   * Execute method get response message.
   * @param url         the url
   * @param authToken   the authentication data
   * @param queryString params for the API
   * @return the response message
   * @throws IOException        the io exception
   * @throws URISyntaxException
   */
  public static ResponseMessage executeMethodGetWithQueryParameters(final String url,
      final String authToken, final Map<String, Object> queryString) throws IOException {
    HttpGet httpGet = new HttpGet(url);
    if (!queryString.isEmpty()) {
      URIBuilder builder = new URIBuilder(httpGet.getURI());
      for (Map.Entry<String, Object> entry : queryString.entrySet()) {
        builder.setParameter(entry.getKey(), entry.getValue().toString());
      }
      URI uri;
      try {
        uri = builder.build();
      } catch (URISyntaxException e) {
        throw new IOException(e);
      }
      httpGet = new HttpGet(uri);
    }
    return executeRequestCustomHeaders(httpGet, authToken);
  }

  /**
   * Execute method get response message.
   * @param url         the url
   * @param queryString contains the query parameters
   * @param headers     contains the http headers
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodGetWithQueryParametersandHeaders(final String url,
      final Map<String, Object> queryString, final Map<String, Object> headers) throws IOException {
    HttpGet request = new HttpGet(url);
    if (!queryString.isEmpty()) {
      URIBuilder builder = new URIBuilder(request.getURI());
      for (Map.Entry<String, Object> entry : queryString.entrySet()) {
        builder.setParameter(entry.getKey(), entry.getValue().toString());
      }
      URI uri;
      try {
        uri = builder.build();
      } catch (URISyntaxException e) {
        throw new IOException(e);
      }
      request = new HttpGet(uri);
    }
    return executeRequestCustomHeaders(request, headers);
  }

  /**
   * Execute method patch response message.
   * @param url       the url
   * @param body      the body
   * @param authToken the authentication data
   * @param bearer    the bearer
   * @param headerMap the header map
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodPatch(final String url, final String body,
      final String authToken, final boolean bearer, final Map<String, String> headerMap)
      throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpPatch request = new HttpPatch(url);

    if (authToken != null && authToken.length() > 0) {
      request.addHeader("Authorization", initBasicAuthentication(authToken, bearer));
    }

    if (body.length() > 0) {
      HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
      request.setEntity(entity);
    }
    if (headerMap != null) { // in case of any header need to be set
      for (Map.Entry<String, String> entry : headerMap.entrySet()) {
        request.setHeader(entry.getKey(), entry.getValue());
      }
    }
    HttpResponse response = client.execute(request);

    return new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
  }

  /**
   * Execute method PUT.
   * @param url       the url
   * @param entity    http put body (StringEntity for json, UrlEncodedFormEntity
   *                  for default x-www-form-urlencoded form body etc.)
   * @param authToken the authentication data (Base64)
   * @param bearer    true if authentication method is bearer
   * @return the response message
   * @throws IOException if can't connect to remote service
   */
  public static ResponseMessage executeMethodPut(final String url, final HttpEntity entity,
      final String authToken, final boolean bearer) throws IOException {
    final HttpEntityEnclosingRequestBase request = new HttpPut(url);
    if (entity != null) {
      request.setEntity(entity);
    }

    return executeRequest(request, authToken, bearer);
  }

  /**
   * Execute method POST.
   * @param url       the url
   * @param entity    http post body (StringEntity for json, UrlEncodedFormEntity
   *                  for default x-www-form-urlencoded form body etc.)
   * @param authToken the authentication data (Base64)
   * @param bearer    true if authentication method is bearer
   * @return the response message
   * @throws IOException the io exception if can't connect to remote service
   */
  public static ResponseMessage executeMethodPost(final String url, final HttpEntity entity,
      final String authToken, final boolean bearer) throws IOException {
    final HttpEntityEnclosingRequestBase request = new HttpPost(url);
    if (entity != null) {
      request.setEntity(entity);
    }

    return executeRequest(request, authToken, bearer);
  }

  /**
   * Execute method post response message.
   * @param url       the url
   * @param body      the body
   * @param authToken the authentication data
   * @param headerMap the header map
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodPost(final String url, final String body,
      final String authToken, final Map<String, String> headerMap) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpPost request = new HttpPost(url);

    if (authToken != null && authToken.length() > 0) {
      request.addHeader("Authorization", initBasicAuthentication(authToken, false));
    }
    if (headerMap != null) { // in case of any header need to be set
      for (Map.Entry<String, String> entry : headerMap.entrySet()) {
        request.setHeader(entry.getKey(), entry.getValue());
      }
    }

    if (body.length() > 0) {
      HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
      request.setEntity(entity);
    }

    HttpResponse response = client.execute(request);

    return new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
  }

  /**
   * Execute method delete response message.
   * @param url       service url
   * @param authToken authorization token
   * @return response message
   * @throws IOException io exception
   */
  public static ResponseMessage executeMethodDelete(final String url, final String authToken)
      throws IOException {
    return executeRequest(new HttpDelete(url), authToken, false);
  }

  /**
   * Execute http request.
   * @param request   http request to execute
   * @param authToken authentication data (Base64)
   * @param bearer    true if authentication method is bearer
   * @return the response message
   * @throws IOException if can't connect to remote service
   */
  @NotNull
  private static ResponseMessage executeRequest(final HttpRequestBase request,
      final String authToken, final boolean bearer) throws IOException {
    if (StringUtils.isNotBlank(authToken)) {
      request.addHeader(HttpHeaders.AUTHORIZATION, initBasicAuthentication(authToken, bearer));
    }

    final HttpClient client = HttpClientBuilder.create().build();
    final HttpResponse response = client.execute(request);

    return new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
  }

  /**
   * Execute with custom headers.
   * @param request   http request to execute
   * @param authToken authentication data
   * @return the response message
   * @throws IOException if can't connect to remote service
   */
  @NotNull
  private static ResponseMessage executeRequestCustomHeaders(final HttpRequestBase request,
      final String authToken) throws IOException {
    if (StringUtils.isNotBlank(authToken)) {
      request.addHeader("token", authToken);
    }

    final HttpClient client = HttpClientBuilder.create().build();
    final HttpResponse response = client.execute(request);

    return new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
  }

  /**
   * Add with multiple headers.
   * @param request   http request to execute
   * @param headers http request header
   * @return the response message
   * @throws IOException if can't connect to remote service
   */
  @NotNull
  private static ResponseMessage executeRequestCustomHeaders(final HttpRequestBase request,
      final Map<String, Object> headers) throws IOException {
    if (!headers.isEmpty()) {
      for (Map.Entry<String, Object> header : headers.entrySet()) {
        request.addHeader(header.getKey(), header.getValue().toString());
      }
    }
    final HttpClient client = HttpClientBuilder.create().build();
    final HttpResponse response = client.execute(request);

    return new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
  }

  /**
   * Init authentication string.
   * @param authToken the authentication data
   * @param bearer    the Bearer
   * @return the string
   */
  private static String initBasicAuthentication(final String authToken, final boolean bearer) {
    if (bearer) {
      return "Bearer " + authToken;
    }
    return "Basic " + authToken;
  }
}

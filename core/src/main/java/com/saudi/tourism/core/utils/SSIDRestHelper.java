package com.saudi.tourism.core.utils;

import com.saudi.tourism.core.models.common.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Map;

/**
 * SSID Rest Helper.
 */
@Slf4j
public final class SSIDRestHelper {

  /**
   * Execute method get response message.
   * @param url       the url
   * @param authToken the authentication data
   * @param bearer    the bearer
   * @param isProd    is prod runmode
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodGet(final String url, final String authToken,
                                                 final boolean bearer,
                                                 final boolean isProd) throws IOException {
    HttpGet request = new HttpGet(url);
    return executeRequest(request, authToken, bearer, isProd);
  }

  /**
   * Execute http request.
   * @param request   http request to execute
   * @param authToken authentication data (Base64)
   * @param bearer    true if authentication method is bearer
   * @param isProd is Prod run mode.
   * @return the response message
   * @throws IOException if can't connect to remote service
   */
  @NotNull
  private static ResponseMessage executeRequest(final HttpRequestBase request,
        final String authToken, final boolean bearer,
        final boolean isProd) throws IOException {
    if (StringUtils.isNotBlank(authToken)) {
      request.addHeader(HttpHeaders.AUTHORIZATION, initBasicAuthentication(authToken, bearer));
    }
    try {
      char[] password = "changeit".toCharArray();
      SSLContext sslContext = SSLContextBuilder.create()
          .loadKeyMaterial(getKeyStore("/etc/pki/tls/vscert/visitsaudi.p12", password), password)
          .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
      HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
      final HttpResponse response = client.execute(request);
      final int statusCode = response.getStatusLine().getStatusCode();
      if ((statusCode == HttpStatus.SC_OK) || (statusCode == HttpStatus.SC_ACCEPTED)) {
        return new ResponseMessage(statusCode,
          readBody(response.getEntity().getContent()));
      } else {
        LOGGER.error("SSID GET/POST call with request URI: {} , failed with status code: {}",
            request.getURI(), statusCode);
        if (request.getURI().toString().endsWith("/login-url")) {
          throw new Exception();
        } else {
          return new ResponseMessage(statusCode,
            readBody(response.getEntity().getContent()));
        }
      }
    } catch (Exception e) {
      LOGGER.error("could not set the SSL context for SSID", e);
      throw new IOException("could not set the SSL context for SSID");
    }
  }

  /**
   * Get Keystore.
   * @param filePath path.
   * @param password pass.
   * @return keystore.
   * @throws Exception exception.
   */
  private static KeyStore getKeyStore(String filePath, char[] password) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    InputStream in = new FileInputStream(filePath);
    try {
      keyStore.load(in, password);
    } finally {
      if (in != null) {
        in.close();
      }
    }
    return keyStore;
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

  /**
   * Execute method POST.
   * @param url       the url
   * @param entity    http post body (StringEntity for json, UrlEncodedFormEntity
   *                  for default x-www-form-urlencoded form body etc.)
   * @param authToken the authentication data (Base64)
   * @param bearer    true if authentication method is bearer
   * @param isProd    true if runmode is prod.
   * @return the response message
   * @throws IOException the io exception if can't connect to remote service
   */
  public static ResponseMessage executeMethodPost(final String url, final HttpEntity entity,
                                                  final String authToken, final boolean bearer,
                                                  final boolean isProd) throws IOException {
    final HttpEntityEnclosingRequestBase request = new HttpPost(url);
    if (entity != null) {
      request.setEntity(entity);
    }
    return executeRequest(request, authToken, bearer, isProd);
  }

  /**
   * Execute method patch response message.
   * @param url       the url
   * @param body      the body
   * @param authToken the authentication data
   * @param bearer    the bearer
   * @param headerMap the header map
   * @param isProd is prod run mode.
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodPatch(final String url, final String body,
        final String authToken, final boolean bearer, final Map<String, String> headerMap,
                                                   final boolean isProd)
        throws IOException {
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
    try {
      char[] password = "changeit".toCharArray();
      SSLContext sslContext = SSLContextBuilder.create()
          .loadKeyMaterial(getKeyStore("/etc/pki/tls/vscert/visitsaudi.p12", password), password)
          .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
      HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
      final HttpResponse response = client.execute(request);
      final int statusCode = response.getStatusLine().getStatusCode();
      if ((statusCode == HttpStatus.SC_OK) || (statusCode == HttpStatus.SC_ACCEPTED)) {
        return new ResponseMessage(statusCode,
          readBody(response.getEntity().getContent()));
      } else {
        LOGGER.error("SSID PATCH call failed with request URI: {} , failed with status code: {}",
            request.getURI(), statusCode);
        return new ResponseMessage(statusCode,
            readBody(response.getEntity().getContent()));
      }
    } catch (Exception e) {
      LOGGER.error("could not set the SSL context for SSID PATCH request", e);
      throw new IOException("could not set the SSL context for SSID PATCH request");
    }
  }

  /**
   * Execute method patch response message.
   * @param url       the url
   * @param body      the body
   * @param authToken the authentication data
   * @param bearer    the bearer
   * @return the response message
   * @throws IOException the io exception
   */
  public static ResponseMessage executeMethodPatch(final String url, final HttpEntity body,
                                                   final String authToken, final boolean bearer)
      throws IOException {
    HttpPatch request = new HttpPatch(url);

    if (authToken != null && authToken.length() > 0) {
      request.addHeader("Authorization", initBasicAuthentication(authToken, bearer));
    }

    request.setEntity(body);

    try {
      char[] password = "changeit".toCharArray();
      SSLContext sslContext = SSLContextBuilder.create()
          .loadKeyMaterial(getKeyStore("/etc/pki/tls/vscert/visitsaudi.p12", password), password)
          .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
      HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();

      LOGGER.info("Request URI: {}", request.getURI());
      LOGGER.info("Request Body: {}", body);
      LOGGER.info("Request Body Content: {}", body.getContent());
      final HttpResponse response = client.execute(request);
      final int statusCode = response.getStatusLine().getStatusCode();
      LOGGER.info("Response Status Code: {}", statusCode);
      if ((statusCode == HttpStatus.SC_OK) || (statusCode == HttpStatus.SC_ACCEPTED)) {
        return new ResponseMessage(statusCode,
          readBody(response.getEntity().getContent()));
      } else {
        LOGGER.error("SSID PATCH call failed with request URI: {} , failed with status code: {}",
            request.getURI(), statusCode);
        return new ResponseMessage(statusCode,
          readBody(response.getEntity().getContent()));
      }
    } catch (Exception e) {
      LOGGER.error("could not set the SSL context for SSID PATCH request");
      throw new IOException("could not set the SSL context for SSID PATCH request");
    }
  }

  /**
   * Prevents developer from initiating instance.
   */
  private SSIDRestHelper() {
  }


}

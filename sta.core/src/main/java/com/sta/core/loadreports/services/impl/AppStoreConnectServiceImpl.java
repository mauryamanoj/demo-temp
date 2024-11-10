package com.sta.core.loadreports.services.impl;

import com.sta.core.MmCoreException;
import com.sta.core.loadreports.LoadReportsUtils;
import com.sta.core.loadreports.services.AppStoreConnectService;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the AppStoreConnectService, is used to load reports from App Store Connect.
 */
@Component(service = AppStoreConnectService.class,
           immediate = true,
           property = {
               LoadReportsUtils.SERVICE_DESCRIPTION + LoadReportsUtils.EQUAL
                   + AppStoreConnectServiceImpl.APP_STORE_CONNECT_DESCRIPTION})
@Slf4j
public class AppStoreConnectServiceImpl implements AppStoreConnectService {

  /**
   * This Service description for OSGi.
   */
  static final String APP_STORE_CONNECT_DESCRIPTION = "App Store Connect Service";
  /**
   * App Store base url.
   */
  static final String APP_STORE_BASE_URL = "https://api.appstoreconnect.apple.com";
  /**
   * App Store audience.
   */
  static final String APP_STORE_AUDIENCE = "appstoreconnect-v1";

  /**
   * JWT header name of 'key id'.
   */
  private static final String JWT_HEADER_KEY_ID_PROP = "kid";

  /**
   * JWT token livetime.
   */
  private static final int JWT_LIVE_TIME = 20 * 10 * 1000;

  @Override
  public byte[] loadReport(String path, Map<String, String> reportParams, String keyID,
      String issuer, PrivateKey privateKey) throws URISyntaxException, IOException,
          MmCoreException {
    URI uri = buildURI(path, reportParams);
    String token = createJWToken(keyID, issuer, privateKey);
    return executeRequest(uri, token);
  }

  /**
   * Execute request to get report.
   *
   * @param uri   - url
   * @param token - token
   * @return byte[] report in binary view
   * @throws IOException - exception
   */
  private byte[] executeRequest(URI uri, String token) throws IOException, MmCoreException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(uri.toString());
      request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
      request.setHeader(HttpHeaders.ACCEPT, "application/a-gzip; stream; */*;");
      request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          HttpEntity entity = response.getEntity();
          GZIPInputStream zis = new GZIPInputStream(entity.getContent());
          return IOUtils.toByteArray(zis);
        } else {
          LOGGER.error("Response status: " + response.getStatusLine());
        }
      }
    }
    throw new MmCoreException("Illegal response data - empty");
  }

  /**
   * Create JSON Web Token.
   *
   * @param keyID      - App store connect key id
   * @param issuer     - App store connect issuer
   * @param privateKey -  - App store connect private key
   * @return String JSON Web Token
   */
  private String createJWToken(String keyID, String issuer, PrivateKey privateKey) {
    Map<String, Object> headers = new HashMap<>();
    headers.put(Header.TYPE, Header.JWT_TYPE);
    headers.put(JWT_HEADER_KEY_ID_PROP, keyID);

    Date exp = new Date(System.currentTimeMillis() + JWT_LIVE_TIME);
    return Jwts.builder().setAudience(APP_STORE_AUDIENCE).setIssuer(issuer).setHeader(headers)
        .setExpiration(exp).signWith(privateKey, SignatureAlgorithm.ES256).compact();
  }

  /**
   * Build request uri to App store connect.
   *
   * @param path         - path to obtain a report
   * @param reportParams - request's params
   * @return URI
   * @throws URISyntaxException - exception
   */
  private URI buildURI(String path, Map<String, String> reportParams) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(APP_STORE_BASE_URL);
    uriBuilder.setPath(path);
    if (Objects.isNull(reportParams)) {
      return uriBuilder.build();
    }
    reportParams.forEach(uriBuilder::addParameter);
    return uriBuilder.build();
  }
}

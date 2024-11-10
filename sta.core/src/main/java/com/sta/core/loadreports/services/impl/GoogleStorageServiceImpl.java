package com.sta.core.loadreports.services.impl;

import com.sta.core.MmCoreException;
import com.sta.core.loadreports.LoadReportsUtils;
import com.sta.core.loadreports.services.GoogleStorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import static org.eclipse.jetty.util.URIUtil.SLASH;

/**
 * Implementation of the Google Storage client Service, is used to download reports from there.
 */
@Component(service = GoogleStorageService.class,
           immediate = true,
           property = {LoadReportsUtils.SERVICE_DESCRIPTION + LoadReportsUtils.EQUAL
               + GoogleStorageServiceImpl.GOOGLE_STORAGE_DESCRIPTION})
@Slf4j
public class GoogleStorageServiceImpl implements GoogleStorageService {

  /**
   * This Service description for OSGi.
   */
  static final String GOOGLE_STORAGE_DESCRIPTION = "Google Storage client Service";

  /**
   * Global configuration of Google Cloud Storage OAuth 2.0 scope.
   */
  private static final String STORAGE_SCOPE =
      "https://www.googleapis.com/auth/devstorage.read_write";

  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  /**
   * Google storage api base url.
   */
  private static final String STORAGE_GOOGLEAPIS_BASE_URL = "https://storage.googleapis.com/";

  @Override public byte[] download(String bucketName, String serviceAccount, PrivateKey privateKey,
      String filepath) throws GeneralSecurityException, IOException, MmCoreException {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    @SuppressWarnings("deprecation")
    GoogleCredential credential =
        new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(serviceAccount)
            .setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
            .setServiceAccountPrivateKey(privateKey).build();

    if (!StringUtils.startsWith(filepath, SLASH)) {
      filepath = SLASH + filepath;
    }
    String uri = STORAGE_GOOGLEAPIS_BASE_URL + bucketName + filepath;
    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
    GenericUrl url = new GenericUrl(uri);
    HttpRequest request = requestFactory.buildGetRequest(url);
    HttpResponse response = request.execute();
    try {
      if (HttpStatus.SC_OK == response.getStatusCode()
          && response.getContentType().contains(LoadReportsUtils.CONTENT_TYPE_CSV)) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
          response.download(bout);
          return bout.toByteArray();
        }
      }
    } finally {
      try {
        response.disconnect();
      } catch (Exception e) {
        LOGGER.error("Error during close the response", e.getMessage());
      }
    }
    throw new MmCoreException("Illegal response data - empty");
  }
}

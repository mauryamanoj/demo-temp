package com.sta.core.scene7.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.api.client.util.Base64;
import com.sta.core.scene7.DMAssetUploadConfig;
import com.sta.core.scene7.DMAssetUploadService;
import com.saudi.tourism.core.models.common.ResponseMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * DAM Asset upload Service implementation.
 */
@Slf4j
@Component(service = DMAssetUploadService.class,
           immediate = true)
public class DMAssetUploadServiceImpl implements DMAssetUploadService {

  /**
   * Open tag 'items'.
   */
  private static final String TAG_OPEN_ITEMS = "            <items>\n";
  /**
   * Close tag 'items'.
   */
  private static final String TAG_CLOSE_ITEMS = "            </items>\n";
  /**
   * Tag 'items' with value 'Banner'.
   */
  private static final String TAG_TYPE_BANNER = "                <type>Banner</type>\n";
  /**
   * The Saudi Tourism config service.
   */
  @Reference
  private DMAssetUploadConfig dmConfig;

  @Override public ResponseMessage uploadAsset(String base64Data, final String path,
      final String vendor) {
    try {

      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      CloseableHttpClient httpClient = getHttpClient();


      Charset charsetUtf8 = StandardCharsets.UTF_8;
      ContentType contentTypeUtf8 =
          ContentType.create(ContentType.MULTIPART_FORM_DATA.getMimeType(), charsetUtf8);

      long imageName = System.currentTimeMillis();


      String imageDataBytes = base64Data.substring(base64Data.indexOf(',') + 1);
      InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(imageDataBytes.getBytes()));
      InputStreamBody fileBody =
          new InputStreamBody(stream, ContentType.APPLICATION_OCTET_STREAM,
              vendor + "-" + imageName +
              ".jpg");
      LOGGER.debug(fileBody.toString());
      builder.addPart("file", fileBody);
      String authBody =
          "<authHeader xmlns=\"http://www.scene7.com/IpsApi/xsd/2019-09-10-beta\">" + "<user>"
              + dmConfig.getUser() + "</user>" + "    <password>" + dmConfig.getPassword()
              + "</password>" + "    <locale>en-US</locale>"
              + "    <appName>MyUploadServletTest</appName>" + "    <appVersion>1.0</appVersion>"
              + "    <faultHttpStatusCode>200</faultHttpStatusCode>" + "</authHeader>";
      builder.addPart("auth", new StringBody(authBody, contentTypeUtf8));

      String body = "<uploadPostParam xmlns=\"http://www.scene7.com/IpsApi/xsd/2019-09-10-beta\">\n"
          + "    <companyHandle>c|26268</companyHandle>\n" + "    <jobName>uploadFileServlet-"
          + vendor + "-" + imageName + "</jobName>\n"
          + "    <destFolder>scth/saudi-tourism/"+path+"/</destFolder>\n" + "    <fileName>"
          + vendor + "-" + imageName + ".jpg</fileName>\n" + "    <uploadParams>\n"
          + "        <overwrite>true</overwrite>\n"
          + "        <readyForPublish>true</readyForPublish>\n"
          + "        <preservePublishState>true</preservePublishState>\n"
          + "        <synchronizationComplete>true</synchronizationComplete>\n"
          + "        <createMask>false</createMask>\n" + "        <autoSmartCropOptionsArray>\n"
          + TAG_OPEN_ITEMS + "                <name>crop-1920x1080</name>\n"
          + "                <width>1920</width>\n" + "                <height>1080</height>\n"
          + TAG_TYPE_BANNER + TAG_CLOSE_ITEMS
          + TAG_OPEN_ITEMS + "                <name>crop-360x480</name>\n"
          + "                <width>360</width>\n" + "                <height>480</height>\n"
          + TAG_TYPE_BANNER + TAG_CLOSE_ITEMS
          + TAG_OPEN_ITEMS + "                <name>crop-315x236</name>\n"
          + "                <width>315</width>\n" + "                <height>236</height>\n"
          + TAG_TYPE_BANNER + TAG_CLOSE_ITEMS
          + TAG_OPEN_ITEMS + "                <name>crop-260x195</name>\n"
          + "                <width>260</width>\n" + "                <height>195</height>\n"
          + TAG_TYPE_BANNER + TAG_CLOSE_ITEMS
          + TAG_OPEN_ITEMS + "                <name>crop-667x375</name>\n"
          + "                <width>667</width>\n" + "                <height>375</height>\n"
          + TAG_TYPE_BANNER + TAG_CLOSE_ITEMS
          + "        </autoSmartCropOptionsArray>\n" + "        <emailSetting>None</emailSetting>\n"
          + "        <unsharpMaskOptions>\n" + "            <amount>1.75</amount>\n"
          + "            <radius>0.2</radius>\n" + "            <threshold>2</threshold>\n"
          + "            <monochrome>0</monochrome>\n" + "        </unsharpMaskOptions>\n"
          + "        <excludeMasterVideoFromAVS>true</excludeMasterVideoFromAVS>\n"
          + "    </uploadParams>\n" + "</uploadPostParam>";
      LOGGER.debug(body);
      builder.addPart("uploadParams", new StringBody(body, contentTypeUtf8));
      HttpPost request = new HttpPost(dmConfig.getUgcAssetUploadUrl());
      HttpEntity requestEntity = builder.build();
      request.setEntity(requestEntity);

      HttpResponse response = httpClient.execute(request);

      String responseBody = EntityUtils.toString(response.getEntity());
      LOGGER.debug(responseBody);

      if (responseBody.contains("<jobHandle>")) {
        ResponseMessage responseMessage =  new ResponseMessage();
        responseMessage.setMessage("https://s7g10.scene7.com/is/image/scth/" + vendor + "-" + imageName);
        return responseMessage;
      }


    } catch (Exception e) {
      LOGGER.error("Exception at DMAssetUploadServiceImpl : uploadAsset() " + e.getMessage());
    }
    return null;
  }

  private static CloseableHttpClient getHttpClient() {
    SocketConfig sc = SocketConfig.custom().setSoTimeout(180000).build();

    CloseableHttpClient httpClient;
    PoolingHttpClientConnectionManager clientConnectionManager =
        new PoolingHttpClientConnectionManager();
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setDefaultSocketConfig(sc);
    httpClientBuilder.setConnectionManager(clientConnectionManager);
    httpClientBuilder.setConnectionManagerShared(true);
    httpClient = httpClientBuilder.build();

    return httpClient;
  }


}

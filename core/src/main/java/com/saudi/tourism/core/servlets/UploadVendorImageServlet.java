package com.saudi.tourism.core.servlets;

import com.day.cq.dam.api.AssetManager;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpStatus;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.servlets.PackageFormServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Upload.
 */
@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
               SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/uploadimage"})
@Slf4j
public class UploadVendorImageServlet extends BaseAllMethodsServlet {

  /**
   * ResourceResolverFactory.
   */
  @Reference
  private transient ResourceResolverFactory resolverFactory;

  @Override
  protected void doPost(final SlingHttpServletRequest request,
                        final SlingHttpServletResponse response)
          throws ServletException, IOException {
    try {
      final boolean isMultipart = ServletFileUpload.isMultipartContent(request);

      if (isMultipart) {
        final Map<String, RequestParameter[]> params = request.getRequestParameterMap();

        String list = params.entrySet().stream().map(Map.Entry::getValue).map(str -> {
          try (InputStream inputStream = str[0].getInputStream()) {
            String fileName = str[0].getFileName();

            //Save the uploaded file into the Adobe CQ DAM
            return writeToDam(inputStream, fileName);
          } catch (IOException e) {
            LOGGER.error("Unable to retrieve InputStream", e);
            return null;
          }
        }).collect(Collectors.joining("; "));
        CommonUtils.writeJSON(response, HttpStatus.SC_CREATED,
                "The Sling Servlet placed the uploaded file here: " + list);
      }
    } catch (Exception e) {
      LOGGER.error("Unable to ");
    }
  }

  /**
   * write do DAM.
   * @param inputStream InputStream
   * @param fileName fileName
   * @return file path
   */
  private String writeToDam(InputStream inputStream, String fileName) {
    Map<String, Object> param = new HashMap<>();
    param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");

    try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {

      //Use AssetManager to place the file into the AEM DAM
      AssetManager assetMgr = resolver.adaptTo(AssetManager.class);
      String newFile = PathUtils.concat(Constants.DAM_SAUDITOURISM_PATH, Constants.PACKAGES,
              fileName);
      assetMgr.createAsset(newFile, inputStream, "image/jpeg", true);
      // Return the path to the file was stored
      return newFile;
    } catch (Exception e) {
      LOGGER.error("Cannot write file", e);
    }
    return null;
  }
}

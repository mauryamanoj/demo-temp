package com.saudi.tourism.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.saudi.tourism.core.utils.Constants.PN_BANNER_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;

/**
 * Dynamic Image Reference Servlet.
 * http://localhost:4502/bin/script/api/v1/dynamic-media-content-migration?rootPath=&flag=&highRes=
 * flag = check/update/assetReview/assetUpdate
 * highRes= 1920x1080
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Dynamic Media Migration Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
            + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/script/api/v1/dynamic-media-content-migration"})
@Slf4j
public class MediaContentMigrationServlet extends SlingSafeMethodsServlet {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private transient SaudiTourismConfigs saudiTourismConfig;

  /**
   * Property Update Action.
   */
  private static final String UPDATE = "update";

  /**
   * Property CHECK Action.
   */
  private static final String CHECK = "check";

  /**
   * Property ASSET_REVIEW for high resolution Action.
   */
  private static final String ASSET_REVIEW = "assetReview";

  /**
   * Property ASSET_UPDATE for high resolution Action.
   */
  private static final String ASSET_UPDATE = "assetUpdate";

  /**
   * DEFAULT_HIGHEST_RESOLUTION.
   */
  private static final String DEFAULT_HIGHEST_RESOLUTION = "1920x1080";

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = -1L;

  /**
   * Action Flag : check/update/forceUpdate.
   */
  private String actionFlag = CHECK;

  /**
   * Highest Resolution.
   */
  private String highestRes;
  /**
   * Counter to keep track of properties to be updated.
   */
  private int count;

  /**
   * The SlingRepository repository.
   */
  @Reference
  private transient SlingRepository repository;

  /**
   * bindRepository.
   *
   * @param repository SlingRepository
   */
  @Generated
  public void bindRepository(SlingRepository repository) {
    this.repository = repository;
  }

  @Override protected void doGet(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response) {
    final ResourceResolver resourceResolver = request.getResourceResolver();
    final Session session = resourceResolver.adaptTo(Session.class);
    try  {
      count = 0;
      LOGGER.debug("Invoking Update Media Content Migration Servlet");
      String rootPath = request.getParameter("rootPath");
      highestRes = Constants.FORWARD_SLASH_CHARACTER + StringUtils
          .defaultIfBlank(request.getParameter("highRes"), DEFAULT_HIGHEST_RESOLUTION)
          + Constants.FORWARD_SLASH_CHARACTER;
      actionFlag = request.getParameter("flag");
      PrintWriter out = response.getWriter();
      List<String> propertiesNames = getImagePropertyNameList();
      if (StringUtils.isNotBlank(rootPath)) {
        Resource rootPageResource = resourceResolver.getResource(rootPath);
        if (Objects.nonNull(rootPageResource)) {
          handleMediaProperties(rootPageResource, propertiesNames, out, resourceResolver);
          Iterator<Resource> resourceIterator = rootPageResource.listChildren();
          while (resourceIterator.hasNext()) {
            Resource nodeResource = resourceIterator.next();
            session.refresh(true);
            handleMediaProperties(nodeResource, propertiesNames, out, resourceResolver);
            visitRecursively(nodeResource, propertiesNames, out, resourceResolver, session);
          }
        }
      }
      printResultSummary(out);
      if (Objects.nonNull(session)) {
        session.save();
      }
      resourceResolver.commit();
      out.flush();
    } catch (Exception e) {
      LOGGER.error("Exception Occurred {}", e.getLocalizedMessage());
    } finally {
      if (Objects.nonNull(session)) {
        session.logout();
      }
    }
  }

  /**
   * Handle Media Property.
   *
   * @param resource  resource
   * @param propNames propertyName list
   * @param out       print writer
   * @param resourceResolver resource resolver
   * @throws RepositoryException repository exception
   */
  private void handleMediaProperties(final Resource resource, final List<String> propNames,
      final PrintWriter out, ResourceResolver resourceResolver) throws RepositoryException {
    ValueMap properties = resource.getValueMap();
    Node node = resource.adaptTo(Node.class);
    for (String propertyName : propNames) {
      String s7PropertyName = getScene7PropertyName(propertyName);
      if (properties.containsKey(propertyName) && Objects.nonNull(node)) {
        String imagePath = (String) properties.get(propertyName);
        String s7ImagePath = StringUtils
            .defaultIfBlank(properties.get(s7PropertyName, String.class), StringUtils.EMPTY);
        if (CHECK.equals(actionFlag) && StringUtils.isNotBlank(s7ImagePath) && !s7ImagePath
            .contains(Constants.S7_IMAGE_CONTENT)) {
          count++;
        } else {
          s7ImagePath =
              performAction(resourceResolver, node, imagePath, propertyName, s7PropertyName);
        }
        printData(resource.getPath(), propertyName, imagePath, s7PropertyName, s7ImagePath, out);
      }
    }
  }

  /**
   * Perform Action.
   *
   * @param resourceResolver Resource resolver
   * @param node             node
   * @param imagePath        image path dam
   * @param propertyName     image property name
   * @param s7PropertyName   s7 property name
   * @return imagePath
   * @throws RepositoryException Repository exception
   */
  private String performAction(final ResourceResolver resourceResolver, final Node node,
      final String imagePath, final String propertyName,
      final String s7PropertyName) throws RepositoryException {
    String s7ImagePath = StringUtils.EMPTY;
    if ((UPDATE).equals(actionFlag) && StringUtils.isNotBlank(imagePath)) {
      s7ImagePath =
          getScene7ImagePath(imagePath, resourceResolver, saudiTourismConfig.getScene7Domain());
      node.setProperty(s7PropertyName, s7ImagePath);
      count++;
    } else if ((ASSET_REVIEW.equals(actionFlag) || ASSET_UPDATE.equals(actionFlag)) && StringUtils
        .isNotBlank(imagePath)) {
      s7ImagePath = findHighResImage(resourceResolver, imagePath);
      if (ASSET_UPDATE.equals(actionFlag) && Objects.nonNull(s7ImagePath)) {
        // update high resolution image name
        node.setProperty(propertyName, s7ImagePath);
      }
    }
    return s7ImagePath;
  }

  /**
   * Find High Resolution image.
   *
   * @param resourceResolver resource resolver
   * @param imagePath        image path
   * @return high res image path
   */
  private String findHighResImage(final ResourceResolver resourceResolver, final String imagePath) {
    Matcher matcher = Pattern.compile("/\\d{3,4}x\\d{3,4}/").matcher(imagePath);
    if (matcher.find() && !matcher.group().equals(highestRes)) {
      count++;
      String path =  matcher.replaceAll(highestRes);
      Resource resource = resourceResolver.getResource(path);
      if (Objects.nonNull(resource) && DamUtil.isAsset(resource)) {
        return path;
      }
      return StringUtils.EMPTY;
    }
    return null;
  }

  /**
   * Get Scene7 Image Path.
   *
   * @param imagePath        image path
   * @param resourceResolver resource resolver
   * @param domain           Scene7 domain
   * @return scene7Path of image
   */
  private String getScene7ImagePath(final String imagePath, ResourceResolver resourceResolver,
      final String domain) {
    Resource imageResource = resourceResolver.getResource(imagePath);
    if (DamUtil.isAsset(imageResource)) {
      Asset asset = DamUtil.getAssets(imageResource).next();
      return DynamicMediaUtils.getScene7Path(asset, domain);
    }
    return imagePath;
  }

  /**
   * Get Scene7 Property Name. if property name is image, return s7image.
   *
   * @param propertyName property Name.
   * @return Scene 7 property name
   */
  private String getScene7PropertyName(final String propertyName) {
    return "s7" + propertyName;
  }

  /**
   * Visit Recursively th resources to find images in properties.
   *
   * @param nodeResource nodeResource
   * @param propNames    propertyName list
   * @param out          printWriter
   * @param resourceResolver resource resolver
   * @param session      session
   * @throws Exception exception
   */
  private void visitRecursively(final Resource nodeResource, final List<String> propNames,
      final PrintWriter out, final ResourceResolver resourceResolver, final Session session) throws Exception {
    Iterator<Resource> childResources = nodeResource.listChildren();
    while (childResources.hasNext()) {
      Resource child = childResources.next();
      session.refresh(true);
      handleMediaProperties(child, propNames, out, resourceResolver);
      visitRecursively(child, propNames, out, resourceResolver, session);
    }
  }

  /**
   * Print data for review.
   *
   * @param path         node path
   * @param propertyName image property name
   * @param value        property value
   * @param s7PropName   s7property name
   * @param s7ImageValue s7 image path
   * @param out          print writer
   */
  private void printData(final String path, final String propertyName, final String value,
      final String s7PropName, final String s7ImageValue, PrintWriter out) {
    if ((ASSET_REVIEW.equals(actionFlag) || ASSET_UPDATE.equals(actionFlag)) && Objects
        .nonNull(s7ImageValue)) {
      out.println("-------------------------------------------------------------------------");
      out.println("Node Path : " + path);
      out.println("PropName : " + propertyName);
      out.println("PropValue : " + value);
      if (StringUtils.isNotBlank(s7ImageValue)) {
        out.println("High Res Image available at : " + s7ImageValue);
      } else {
        out.println("High Res Image NOT FOUND!");
      }
    }
    if (actionFlag.equals(UPDATE) || actionFlag.equals(CHECK)) {
      out.println("-------------------------------------------------------------------------");
      out.println("Node Path : " + path);
      out.println("PropName : " + propertyName);
      out.println("PropValue : " + value);
      out.println("S7PropName : " + s7PropName);
      out.println("S7PropValue : " + s7ImageValue);
    }
  }

  /**
   * Print result summary.
   *
   * @param out print writer.
   */
  private void printResultSummary(final PrintWriter out) {
    if (count == 0) {
      out.println("All properties are up to date!");
    } else if ((UPDATE).equals(actionFlag)) {
      out.println("Total : " + count + " properties updated!");
    } else {
      out.println("Total : " + count + " properties required to be updated!");
    }
  }

  /**
   * Get Image Property Name List.
   *
   * @return propertyName list.
   */
  private List<String> getImagePropertyNameList() {
    List<String> pnList = new ArrayList<>();
    pnList.add(PN_IMAGE);
    pnList.add("mobileImage");
    pnList.add("fileReference");
    pnList.add("mobileImageReference");
    pnList.add("cardImage");
    pnList.add("cardImageMobile");
    pnList.add("navImage");
    pnList.add("featureImage");
    pnList.add("mobileNavImage");
    pnList.add("featuredImage");
    pnList.add("previewImage");
    pnList.add("tabImage");
    pnList.add("featureEventMobileImage");
    pnList.add(PN_FEATURE_EVENT_IMAGE);
    pnList.add("sliderImage");
    pnList.add(PN_BANNER_IMAGE);
    return pnList;
  }
}

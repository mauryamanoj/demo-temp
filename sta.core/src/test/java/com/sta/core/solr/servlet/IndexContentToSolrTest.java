package com.sta.core.solr.servlet;

import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.service.Utils;
import com.sta.core.solr.services.SolrSearchService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class IndexContentToSolrTest {

  private static final String CONTENT_PATH_EN = "/content/sauditourism/en/solr";

  @Mock
  private SolrSearchService solrSearchService;

  @Mock
  private ResourceResolverFactory resourceResolverFactory;

  private IndexContentToSolr indexContentToSolr;

  @BeforeEach
  private void setUp(final AemContext context) throws LoginException {
    context.load().json("/solr/content.json", CONTENT_PATH_EN);

    indexContentToSolr = new IndexContentToSolr();
    Utils.setInternalState(indexContentToSolr, "solrSearchService", solrSearchService);
    Utils.setInternalState(indexContentToSolr, "resourceResolverFactory", resourceResolverFactory);

    lenient().when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(context.resourceResolver());
    lenient().when(solrSearchService.createPageMetadataObject(any())).thenReturn(new SolrArticle());
  }

  @Test
  public void index(final AemContext context) throws IOException {

    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("action", "index");
    context.request().setParameterMap(requestParameters);

    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix(CONTENT_PATH_EN);

    indexContentToSolr.doGet(request, context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test
  public void delete(final AemContext context) throws IOException {
    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("action", "delete");
    context.request().setParameterMap(requestParameters);

    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix(CONTENT_PATH_EN);

    indexContentToSolr.doGet(request, context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test
  public void emptyParamError(final AemContext context) throws IOException {

    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix(CONTENT_PATH_EN);

    indexContentToSolr.doGet(request, context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test
  public void wrongAction(final AemContext context) throws IOException {

    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("action", "test");
    context.request().setParameterMap(requestParameters);

    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix(CONTENT_PATH_EN);

    indexContentToSolr.doGet(request, context.response());
    Assertions.assertNotNull(context.response());
  }
}

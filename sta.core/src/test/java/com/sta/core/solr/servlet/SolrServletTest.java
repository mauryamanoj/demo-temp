package com.sta.core.solr.servlet;

import com.sta.core.solr.service.Utils;
import com.sta.core.solr.services.SolrSearchService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SolrServletTest {
  @Mock
  private SolrSearchService solrSearchService;

  private SolrServlet solrServlet;

  @BeforeEach
  private void setUp(final AemContext context) {
    solrServlet = new SolrServlet();
    Utils.setInternalState(solrServlet, "solrSearchService", solrSearchService);
  }

  @Test
  void mainSearch(final AemContext context) throws IOException {

    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("query", "riydh");
    requestParameters.put("limit", "10");
    requestParameters.put("locale", "en");
    context.request().setParameterMap(requestParameters);

    solrServlet.doGet(context.request(), context.response());
    Assertions.assertEquals(200, context.response().getStatus());
  }

  @Test
  void testError(final AemContext context) throws IOException {
    solrServlet.doGet(context.request(), context.response());
    Assertions.assertEquals(200, context.response().getStatus());
  }

  @Test
  void suggest(final AemContext context) throws IOException {

    Map<String, Object> requestParameters = new HashMap<>();
    requestParameters.put("suggestion", "riy");
    requestParameters.put("limit", "10");
    requestParameters.put("locale", "en");
    context.request().setParameterMap(requestParameters);

    solrServlet.doGet(context.request(), context.response());
    Assertions.assertEquals(200, context.response().getStatus());
  }
}

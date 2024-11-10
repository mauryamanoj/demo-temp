package com.saudi.tourism.core.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.utils.deploy.scripts.AppLocationPageMultifieldMigrationScript;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import org.apache.commons.compress.utils.Lists;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class AppLocationPageMultifieldMigrationScriptTest {
  private QueryBuilder queryBuilder;
  private AppLocationPageMultifieldMigrationScript script;
  private ResourceResolver rs;

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/servlets/app/en.json", "/content/sauditourism/app/en");
    script = new AppLocationPageMultifieldMigrationScript();

    queryBuilder = mock(QueryBuilder.class);

    rs = spy(context.resourceResolver());

    doReturn(queryBuilder).when(rs).adaptTo(QueryBuilder.class);
  }

  @Disabled
  @Test
  public void testExecute(AemContext context) throws Exception {

    Node contentRoot = context.resourceResolver().getResource("/content/sauditourism/app/en").adaptTo(Node.class);
    NodeIterator locations = contentRoot.getNodes("location-page*");

    final Query query = mock(Query.class);
    SearchResult result = mock(SearchResult.class);
    when(result.getNodes()).thenReturn(locations);
    when(query.getResult()).thenReturn(result);

    when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).then(invocation -> {
      PredicateGroup pg = invocation.getArgument(0);
      assertEquals("/content/sauditourism/app", pg.get(0).getParameters().get("path"));
      return query;
    });

    script.execute(rs);

    Node contentNode = contentRoot.getNode("location-page").getNode(JcrConstants.JCR_CONTENT);
    Node locations_1 = (Node) contentNode.getNode("locations").getNodes().next();
    assertEquals("12345.321", locations_1.getProperty(Constants.LATITUDE).getString());
  }

  @Test
  public void testEmptyQueryBuilder(AemContext context) {
    doReturn(null).when(rs).adaptTo(QueryBuilder.class);
    boolean exception = false;
    try {
      script.execute(rs);
    } catch (Exception e) {
      exception = true;
    }
    Assertions.assertFalse(exception);
  }

  @Test
  public void testEmptyResult(AemContext context) {

    final Query query = mock(Query.class);
    SearchResult result = mock(SearchResult.class);
    when(result.getNodes()).thenReturn(Lists.<Node>newArrayList().iterator());
    when(query.getResult()).thenReturn(result);

    when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).then(invocation -> {
      return query;
    });

    boolean exception = false;
    try {
      script.execute(rs);
    } catch (Exception e) {
      exception = true;
    }
    Assertions.assertFalse(exception);
  }

  @Test
  public void testException(AemContext context) {

    doThrow(new RuntimeException()).when(rs).adaptTo(QueryBuilder.class);

    assertDoesNotThrow(() -> script.execute(rs));
  }
}

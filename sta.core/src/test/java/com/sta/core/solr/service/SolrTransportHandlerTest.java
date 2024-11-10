package com.sta.core.solr.service;

import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationContent;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.TransportContext;
import com.saudi.tourism.core.services.UserService;
import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.replication.SolrTransportHandler;
import com.sta.core.solr.services.SolrSearchService;
import com.sta.core.solr.services.SolrServerConfiguration;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SolrTransportHandlerTest {

  private static final String CONTENT_PATH_EN = "/content/sauditourism/en/solr";
  @Mock
  TransportContext ctx;
  @Mock
  ReplicationTransaction tx;
  @Mock
  ReplicationLog replicationLog;
  @Mock
  ReplicationAction replicationAction;
  @Mock
  ReplicationContent replicationContent;
  private SolrTransportHandler solrTransportHandler;
  @Mock
  private SolrServerConfiguration solrConfigurationService;
  @Mock
  private SolrSearchService solrSearchService;
  @Mock
  private UserService userService;

  @Mock
  private AgentConfig agentConfig;

  @BeforeEach
  private void setUp(final AemContext context)
      throws Exception {
    context.load().json("/solr/content.json", CONTENT_PATH_EN);

    solrTransportHandler = new SolrTransportHandler();
    Mockito.lenient().when(tx.getAction()).thenReturn(replicationAction);
    Mockito.lenient().when(tx.getContent()).thenReturn(replicationContent);

    InputStream is = new ByteArrayInputStream(CONTENT_PATH_EN.getBytes());

    Mockito.lenient().when(replicationContent.getInputStream()).thenReturn(is);

    Mockito.lenient().when(tx.getLog()).thenReturn(replicationLog);

    lenient().when(userService.getResourceResolver()).thenReturn(context.resourceResolver());
    lenient().when(solrSearchService.solrTest("en")).thenReturn(true);
    lenient().when(solrSearchService.createPageMetadataObject(any())).thenReturn(new SolrArticle());
    lenient().when(solrSearchService.deletePageFromSolr(any())).thenThrow(
        new RuntimeException("test"));

    Utils.setInternalState(solrTransportHandler, "solrSearchService", solrSearchService);
    Utils.setInternalState(solrTransportHandler, "userService", userService);
  }

  @Test
  public void doActivate() throws ReplicationException {
    Mockito.when(replicationAction.getType()).thenReturn(ReplicationActionType.ACTIVATE);
    ReplicationResult replicationResult = solrTransportHandler.deliver(ctx, tx);
    Assertions.assertTrue(replicationResult.isSuccess());
  }

  @Test
  public void doActivateError() throws ReplicationException {
    Mockito.when(replicationAction.getType()).thenReturn(ReplicationActionType.DEACTIVATE);
    ReplicationResult replicationResult = solrTransportHandler.deliver(ctx, tx);
    Assertions.assertTrue(replicationResult.isSuccess());
  }

  @Test
  public void doTest() throws ReplicationException {
    Mockito.when(replicationAction.getType()).thenReturn(ReplicationActionType.TEST);
    Assertions.assertTrue(solrTransportHandler.deliver(ctx, tx).isSuccess());
  }

  @Test
  public void canHandle() throws ReplicationException {

    Assertions.assertFalse(solrTransportHandler.canHandle(agentConfig));
  }
}

package com.sta.core.solr.replication;

import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.TransportHandler;
import com.day.cq.replication.TransportContext;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.Agent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.services.SolrSearchService;
import com.sta.core.solr.utils.SolrUtils;
import com.saudi.tourism.core.services.UserService;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Transport handler to send test and purge requests to CDN and handle
 * responses. The handler sets up basic authentication with the user/pass from
 * the replication agent's transport config and sends a GET request as a test
 * and POST as purge request. A valid test response is 200 while a valid purge
 * response vary for different CDNs.
 * <p>
 * The transport handler is triggered by setting your replication agent's
 * transport URL's protocol to "cdn://".
 * <p>
 * The transport handler will get the URL from OSGI configuration
 * {@link }
 * <p>
 * The transport handler builds the POST request body in accordance with
 * CDN's REST APIs using the replication agent properties.
 */
@Component(immediate = true,
           service = TransportHandler.class)
@Slf4j
public class SolrTransportHandler implements TransportHandler {

  /**
   * Agent transportUri.
   */
  private static final String SOLR_PROTOCOL = "solr://";

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * The solr search service.
   */
  @Reference
  private SolrSearchService solrSearchService;

  /**
   * This is agent manager reference object.
   */
  @Reference
  AgentManager agentManager;

  /**
   * {@inheritDoc}
   */
  @Override public boolean canHandle(AgentConfig config) {
    final String transportURI = config.getTransportURI();
    if (transportURI != null) {
      return transportURI.toLowerCase().startsWith(SOLR_PROTOCOL);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx)
      throws ReplicationException {
    final ReplicationActionType replicationType = tx.getAction().getType();

    if (replicationType == ReplicationActionType.TEST) {
      return doTest(tx);
    } else if (replicationType == ReplicationActionType.ACTIVATE
        || replicationType == ReplicationActionType.DEACTIVATE
        || replicationType == ReplicationActionType.DELETE) {
      return doActivate(tx);
    } else {
      return ReplicationResult.OK;
    }
  }

  /**
   * Send test request to CDN via a GET request.
   * <p>
   * It will respond with a 200 HTTP status code if the request was
   * successfully submitted. The response will have information about the
   * queue length, but we're simply interested in the fact that the request
   * was authenticated.
   *
   * @param tx       Replication Transaction
   * @return ReplicationResult OK if 200 response
   * @throws ReplicationException replication exception
   */
  private ReplicationResult doTest(ReplicationTransaction tx) {

    final ReplicationLog log = tx.getLog();
    log.info("doTest");

    boolean testSuccess = solrSearchService.solrTest("en");
    if (testSuccess) {
      log.info("Success Test");
      log.info("---------------------------------------");
      return ReplicationResult.OK;
    }

    return new ReplicationResult(false, 0, "Replication test failed");
  }

  /**
   * Send purge request to CDN via a POST request.
   * <p>
   * Check status code if the purge request was successfully submitted.
   *
   * @param tx       Replication Transaction
   * @return ReplicationResult OK if 201 response from CDN
   * @throws ReplicationException replication exception
   */
  private ReplicationResult doActivate(ReplicationTransaction tx) {
    final ReplicationLog log = tx.getLog();
    log.info("doActivate");
    ResourceResolver resourceResolver = null;
    try {
      final String content =
          IOUtils.toString(tx.getContent().getInputStream(), StandardCharsets.UTF_8);
      if (SolrUtils.allowedSolrPath(content)) {

        ReplicationActionType replicationType = tx.getAction().getType();
        resourceResolver = userService.getResourceResolver();
        Resource resource = resourceResolver.resolve(content);
        //Clearing the solr replication queue if it is blocked.
        if (null != agentManager) {
          Map<String, Agent> agents = agentManager.getAgents();
          if (agents.containsKey("solr")) {
            Agent solrReplicationAgent = agentManager.getAgents().get("solr");
            if (null != solrReplicationAgent) {
              log.debug("Inside if block");
              long solrQueueStatus = solrReplicationAgent.getQueue().getStatus().getProcessingSince();
              log.debug("Replication time is" + solrQueueStatus);
              //Clearing the queue if it is blocked.
              if (solrQueueStatus == 0) {
                solrReplicationAgent.getQueue().clear();
                resourceResolver.commit();
              }
            }
          }
        }
        if (SolrUtils.allowedSolrResource(resource)) {
          if (replicationType == ReplicationActionType.ACTIVATE) {
            SolrArticle solrArticle = solrSearchService.createPageMetadataObject(resource);
            if (solrArticle != null) {
              solrSearchService.indexPageToSolr(solrArticle);
            }
          } else if (replicationType == ReplicationActionType.DEACTIVATE
              || replicationType == ReplicationActionType.DELETE) {
            solrSearchService.deletePageFromSolr(resource.getPath());
          }
        }
      }
    } catch (Exception e) {
      log.error("Error {}", e);
      return new ReplicationResult(true, 0, "Replication failed");
    } finally {
      if (null != resourceResolver && resourceResolver.isLive()) {
        log.debug("Inside finally block");
        resourceResolver.close();
      }
    }

    log.info("Success");
    log.info("---------------------------------------");
    return ReplicationResult.OK;
  }

}

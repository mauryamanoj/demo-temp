package com.saudi.tourism.core.services.impl.v2;

import com.saudi.tourism.core.models.app.page.PageInfo;
import com.saudi.tourism.core.services.v2.LoyaltyPagesService;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * LoyaltyPages Implementation of Service.
 */
@Component(service = LoyaltyPagesService.class, immediate = true)
@Slf4j
public class LoyaltyPagesServiceImpl implements LoyaltyPagesService {


  @Override
  public List<PageInfo> searchRecursivelyPropPres(Node node, List<PageInfo> searchResults) {
    if (searchResults == null) {
      searchResults = new ArrayList<>();
    }
    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty(NN_TEMPLATE) && currentSubNode.getProperty(NN_TEMPLATE)
            .getString().equals(Constants.APP_LOYALTY_CONTENT_TEMPLATE)) {

          PageInfo currentPage = new PageInfo();
          currentPage.setPage(currentSubNode.getPath()
              .replace(Constants.FORWARD_SLASH_CHARACTER + JCR_CONTENT, Constants.BLANK));

          if (currentSubNode.hasProperty(Constants.APP_PUBLISH_DATE)) {
            currentPage.setPublishDate(
                currentSubNode.getProperty(Constants.APP_PUBLISH_DATE).getValue().toString());
          }

          searchResults.add(currentPage);
        }
        searchRecursivelyPropPres(currentSubNode, searchResults);
      }
      return searchResults;
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the content for the app. " + rpe.getMessage());
    }
    return searchResults;
  }
}

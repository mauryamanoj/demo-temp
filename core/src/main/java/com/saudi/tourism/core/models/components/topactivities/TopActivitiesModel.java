package com.saudi.tourism.core.models.components.topactivities;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.models.components.cardsgrid.CardsGridModel;
import com.saudi.tourism.core.models.components.listcomponent.v1.CardListModel;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Model for the Top Activities component.
 * Uses {@link CardsGridModel} to fetch
 * activities list from the Cards Grid component.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.TOP_ACTIVITIES_RESOURCE_TYPE)
@Slf4j
@Getter
@NoArgsConstructor
public class TopActivitiesModel extends CardListModel {

  /**
   * Count of items displayed by default.
   */
  static final long DEFAULT_ITEM_COUNT = 4L;

  /**
   * Item count to display.
   */
  @ValueMapValue
  private Long itemCount = DEFAULT_ITEM_COUNT;

  /**
   * Current resource resolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Model initialization method.
   */
  @PostConstruct private void init() {
    getCardsFromCardsGrid(getResourceResolver());
  }

  /**
   * Extracts all cards from Cards Grid component. Cards are put into existing
   * {@link CardListModel#getCards} field.
   *
   * @param resourceResolver current resource resolver
   */
  void getCardsFromCardsGrid(final ResourceResolver resourceResolver) {
    String path = null;

    final ComponentHeading header = getHeader();

    if (null != header && null != header.getLink()) {
      path = header.getLink().getUrl();
    }

    // Check if path is set
    if (StringUtils.isBlank(path)) {
      LOGGER.warn("No path property for Top Activities component. Data will not be displayed.");
      return;
    }

    final Resource cardsGridResource = getCardsGridResource(resourceResolver, path);
    if (null == cardsGridResource) {
      LOGGER.error("Cards Grid component is not found by path {}", path);
      return;
    }

    final CardsGridModel cardsGridModel = cardsGridResource.adaptTo(CardsGridModel.class);
    if (null == cardsGridModel) {
      LOGGER.error("The specified resource is not CardsGridModel {}", cardsGridResource.getPath());
      return;
    }

    // Copy first `itemCount` cards from CardsGridModel to our one.
    final List<CardModel> collectedItems = cardsGridModel.getCardModels().stream().limit(itemCount)
        .collect(Collectors.toCollection(this::getCards));
    if (CollectionUtils.isEmpty(collectedItems)) {
      LOGGER.warn("There are no cards in specified Card Grid {}", path);
    }

    // Get page url
    final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    assert pageManager != null;
    final Page page = pageManager.getContainingPage(path);
    if (Objects.nonNull(header.getLink())) {
      if (cardsGridModel.getCardModels().size() > itemCount) {
        header.getLink().setUrlWithExtension(LinkUtils.getUrlWithExtension(page.getPath()));
      } else {
        header.getLink().setUrl(null);
        header.getLink().setUrlWithExtension(null);
      }
    }
  }

  /**
   * This is searching for Cards Grid resource on specified path. If the path is specified for
   * the Cards Grid resource, the resource is returned, if path is set for a page or a container
   * on page, it searches all its children.
   *
   * @param resourceResolver the current resource resolver
   * @param path             path for the search
   * @return Cards Grid resource
   */
  @Nullable Resource getCardsGridResource(final ResourceResolver resourceResolver,
      final String path) {
    Resource resource = resourceResolver.resolve(path);

    if (ResourceUtil.isNonExistingResource(resource)) {
      LOGGER.error("Resource is not found by path {}", path);
      return null;
    }

    if (!resource.isResourceType(Constants.CARDS_GRID_RESOURCE_TYPE)) {
      // The specified resource is not Card Grid, but a page or a page content resource
      // or any component below - look for a card grid in child nodes.

      if (resource.isResourceType(NameConstants.NT_PAGE)) {
        // Resource is a page
        final Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
        if (null != contentResource) {
          resource = com.saudi.tourism.core.utils.ResourceUtil
              .findChildResourceByType(contentResource, Constants.CARDS_GRID_RESOURCE_TYPE);
        }
      } else if (path.contains(JcrConstants.JCR_CONTENT)) {
        // Resource is a content resource or a component below
        resource = com.saudi.tourism.core.utils.ResourceUtil
            .findChildResourceByType(resource, Constants.CARDS_GRID_RESOURCE_TYPE);
      }
    }

    return resource;
  }

}

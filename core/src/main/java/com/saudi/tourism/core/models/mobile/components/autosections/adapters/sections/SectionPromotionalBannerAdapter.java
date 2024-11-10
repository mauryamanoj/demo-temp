package com.saudi.tourism.core.models.mobile.components.autosections.adapters.sections;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.CommonCFModel;
import com.saudi.tourism.core.models.components.promotional.PromotionalBannerModel;
import com.saudi.tourism.core.models.components.promotional.PromotionalSectionBannerModel;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.CategoryFilterItem;
import com.saudi.tourism.core.models.mobile.components.atoms.Filter;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.MobileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component(service = SectionResponseModelAdapter.class, immediate = true)
public class SectionPromotionalBannerAdapter implements SectionResponseModelAdapter {

  /** The constant PROMO_BANNER_RES_TYPE. */
  private static final String PROMO_BANNER_RES_TYPE = "sauditourism/components/content/promotional-banner";

  /** Sling settings service. */
  @Reference private SlingSettingsService settingsService;

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase(PROMO_BANNER_RES_TYPE, ((Resource) adaptable).getResourceType());
  }

  @Override
  public SectionResponseModel adaptTo(Adaptable adaptable) {
    Resource currentResource = ((Resource) adaptable);
    if (currentResource == null) {
      return null;
    }

    var resolver = currentResource.getResourceResolver();
    var pageManager = resolver.adaptTo(PageManager.class);
    if (pageManager == null) {
      return null;
    }

    var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return null;
    }

    var promoBannerModel = currentResource.adaptTo(PromotionalBannerModel.class);
    if (promoBannerModel == null) {
      return null;
    }

    CommonCFModel cfModel = MobileUtils.loadCommonCF(currentPage, resolver, settingsService);

    SectionResponseModel section = new SectionResponseModel();
    ItemResponseModel item = new ItemResponseModel();
    Titles titles = new Titles();
    MediaGallery image = new MediaGallery();
    MediaGallery video = new MediaGallery();
    CustomAction customAction = new CustomAction();
    if (CollectionUtils.isNotEmpty(promoBannerModel.getCards())) {
      Optional<PromotionalSectionBannerModel> firstCard =
          promoBannerModel.getCards().stream().filter(Objects::nonNull).findFirst();
      firstCard.ifPresent(
          card -> {
            titles.setTitle(card.getTitle());
            image.setType("IMAGE");
            image.setUrl(
                LinkUtils.getAuthorPublishAssetUrl(
                    resolver,
                    card.getImage().getFileReference(),
                    settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            video.setType("VIDEO");
            video.setUrl(
                LinkUtils.getAuthorPublishAssetUrl(
                    resolver,
                    card.getVideo().getVideoFileReference(),
                    settingsService.getRunModes().contains(Externalizer.PUBLISH)));
            // Fill cta.
            Filter filter = new Filter();
            Category category = new Category();
            if (cfModel != null && cfModel.getDestination() != null) {
              category.setId(cfModel.getDestination().getId());
              category.setTitle(cfModel.getDestination().getTitle());
            }
            CategoryFilterItem filterItem =
                CategoryFilterItem.builder().id("destinations").items(List.of(category)).build();
            filter.setCategories(List.of(filterItem));
            Cta cta = new Cta("DEEPLINK_SCREEN", "discover", filter);
            // Fill custom action.
            customAction.setTitle(card.getLink().getCopy());
            customAction.setShow(true);
            customAction.setEnable(true);
            customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
            customAction.setCta(cta);
            item.setMediaGallery(List.of(image, video));
            item.setTitles(titles);
            item.setCustomAction(customAction);
          });
    }

    if (Objects.isNull(item)) {
      return null;
    }

    var itemComponentStyle = new SectionResponseModel.ItemComponentStyle();
    itemComponentStyle.setComponentUIType("OFFER");
    itemComponentStyle.setComponentScrollDirection("HORIZONTAL");
    section.setId("auto/banner");
    section.setFilterType("");
    section.setFavoriteContentType("THINGS_TO_DO");
    section.setItemComponentStyle(itemComponentStyle);
    section.setItems(List.of(item));
    section.setTotalCount(section.getItems().size());

    return section;
  }
}

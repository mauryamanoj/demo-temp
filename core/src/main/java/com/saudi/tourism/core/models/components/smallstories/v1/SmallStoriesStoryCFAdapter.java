package com.saudi.tourism.core.models.components.smallstories.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.Objects;

/** Adapts Stories CF to SmallStoriesModel. */
@Slf4j
@Component(service = SmallStoriesCFAdapter.class, immediate = true)
public class SmallStoriesStoryCFAdapter implements SmallStoriesCFAdapter, ContentFragmentAwareModel {



  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("story", cf.getTemplate().getTitle());
  }

  @Override
  public CardModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }

    var title = getElementValue(cf, "title", String.class);
    var destinationPath = getElementValue(cf, "destination", String.class);
    var hideFavorite = getElementValue(cf, "hideFavorite", Boolean.class);
    var tags = getElementValue(cf, "categories", String[].class);
    var cardCtaLink = getElementValue(cf, "pagePath", String.class);

    String  favId = null;
    if (StringUtils.isNotEmpty(cardCtaLink)) {
      //should be done before rewriting pagePath
      favId = LinkUtils.getFavoritePath(cardCtaLink);
    }
    String[] images = getElementValue(cf, "images", String[].class);
    Image image = null;
    if (ArrayUtils.isNotEmpty(images)) {
      image = Arrays.stream(images)
        .filter(Objects::nonNull)
        .filter(StringUtils::isNotEmpty)
        .map(s -> {
          final var jsonObject = JsonParser.parseString(s).getAsJsonObject();
          if (Objects.isNull(jsonObject)) {
            return null;
          }
          String bannerImagePath = null;
          String s7bannerImagePath = null;
          String altBannerImage = null;

          if (!jsonObject.has("type")
              || !"image".equals(jsonObject.get("type").getAsString())) {
            return null;
          }
          if (jsonObject.has("image")) {
            bannerImagePath = jsonObject.get("image").getAsString();
          }

          if (jsonObject.has("s7image")) {
            s7bannerImagePath = jsonObject.get("s7image").getAsString();
          }

          if (jsonObject.has("alt")) {
            altBannerImage = jsonObject.get("alt").getAsString();
          }

          if (StringUtils.isEmpty(bannerImagePath)) {
            return null;
          }

          final var bannerImage = new Image();
          bannerImage.setFileReference(bannerImagePath);
          bannerImage.setMobileImageReference(bannerImagePath);
          bannerImage.setS7fileReference(s7bannerImagePath);
          bannerImage.setS7mobileImageReference(s7bannerImagePath);
          bannerImage.setAlt(altBannerImage);

          return bannerImage;
        })
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
    }
    String firstTag = StringUtils.EMPTY;
    if (ArrayUtils.isNotEmpty(tags)) {
      firstTag = tags[0];
    }
    return CardModel.builder()
      .title(title)
      .destinationPath(destinationPath)
      .hideFavorite(hideFavorite)
      .favId(favId)
      .tag(firstTag)
      .image(image)
      .cardCtaLink(cardCtaLink)
      .build();

  }

}

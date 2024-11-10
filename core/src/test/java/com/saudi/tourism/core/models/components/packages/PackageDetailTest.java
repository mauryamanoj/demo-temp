package com.saudi.tourism.core.models.components.packages;

import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import lombok.Getter;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static com.saudi.tourism.core.utils.Constants.FORWARD_SLASH_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(AemContextExtension.class)
@Getter
class PackageDetailTest {

  private static final String RELATIVE_PATH = "en/packages/full-day-in-jeddah";
  private static final String CONTENT_PATH =
      PathUtils.concat(Constants.ROOT_CONTENT_PATH, RELATIVE_PATH);

  @Mock private SaudiModeConfig saudiModeConfig;
  @Mock private ResourceBundleProvider i18nProvider;

  @Mock private PackagesSignupFormConfig signupFormConfig;
  private LeadFormSuccessError leadFormSuccessError = new LeadFormSuccessError();

  @BeforeEach public void setUp(AemContext context) {
    initMocks(this);
    context.load().json("/pages/full-day-in-jeddah.json", CONTENT_PATH);
    context.load().json("/pages/admin-config.json",
        "/content/sauditourism/en/Configs/admin");

    leadFormSuccessError.setError(new TitleSubtitle());
    leadFormSuccessError.setSuccess(new TitleSubtitle());
    when(saudiModeConfig.getPublish()).thenReturn(null);
    when(signupFormConfig.getLead()).thenReturn(leadFormSuccessError);
    when(i18nProvider.getResourceBundle(any())).thenReturn(Utils.i18nBundle());
    context.registerService(SaudiModeConfig.class, saudiModeConfig);
    context.registerService(ResourceBundleProvider.class, i18nProvider, ImmutableMap.of(
        "component.name", "org.apache.sling.i18n.impl.JcrResourceBundleProvider"));
    context.registerAdapter(Resource.class, PackagesSignupFormConfig.class, signupFormConfig);
  }


  @Test void testModel(AemContext context) {
    context.currentResource(CONTENT_PATH + "/jcr:content");
    PackageDetail packageDetail = context.currentResource().adaptTo(PackageDetail.class);
    assertEquals(FORWARD_SLASH_CHARACTER + RELATIVE_PATH, packageDetail.getPath());
    assertEquals(packageDetail.getArea().get(0), new AppFilterItem("jeddah",
        "Jeddah"));
    assertIterableEquals(Arrays.asList(new AppFilterItem("jeddah", "Jeddah")),
        packageDetail.getArea());
    assertIterableEquals(Arrays.asList("jeddah"), packageDetail.getAreas());
    assertNull(packageDetail.getBudgetTag());
    assertEquals(new AppFilterItem(null, null), packageDetail.getBudget());
    assertIterableEquals(Arrays.asList("hike", "snorkeling"),
        packageDetail.getCategories());
    assertIterableEquals(Arrays.asList(
        new AppFilterItem("hike", "Hike"),
        new AppFilterItem("snorkeling", "Snorkeling")),
        packageDetail.getCategory());
    assertEquals(new AppFilterItem("1-day", "1-day"), packageDetail.getDuration());
    assertEquals("1-day", packageDetail.getDurationAuth());
    assertArrayEquals(new String[]{"sauditourism:city/Jeddah"}, packageDetail.getPackageAreaTags());
    assertArrayEquals(new String[] {"sauditourism:packageCategories/Hike",
    "sauditourism:packageCategories/Snorkeling"}, packageDetail.getPackageCategoryTags());
    assertArrayEquals(new String[]{"sauditourism:packageTargets/Couples",
        "sauditourism:packageTargets/Small Families"
    }, packageDetail.getPackageTargetTags());
    assertIterableEquals(
        Arrays.asList(
            new AppFilterItem("couples", "Couples"),
            new AppFilterItem("small-families", "Small families")
        ), packageDetail.getTarget());
    assertIterableEquals(Arrays.asList(
        "couples",
        "small-families"
    ), packageDetail.getTargets());
    assertEquals("Full day in Jeddah", packageDetail.getAltImage());
    assertEquals("/content/dam/saudi-tourism/media/tour-packages/jeddah/"
        + "1920x1080/161652_7360 x 4140.jpg", packageDetail.getBannerImage());
    assertNull(packageDetail.getBannerImageCaption());
    assertEquals("/content/dam/saudi-tourism/media/tour-packages/jeddah/"
        + "760x570/161652_5914 x 4436.jpg", packageDetail.getCardImage());
    assertEquals("Jeddah is one of the most beautiful cities in the Kingdom of Saudi "
        + "Arabia and its history and ancient heritage is deeply connected to the history and "
        + "heritage of the Kingdom, which can be seen everywhere. Visit its historical old houses "
        + "and the Jeddah Corniche on the Red Sea where you can enjoy a casual atmosphere and its "
        + "many attractions. The trip includes a yacht cruise to enjoy marine activities.",
        packageDetail.getCopy());
    assertNull(packageDetail.getCardImageCaption());
    assertEquals("/packages/full-day-in-jeddah",
        packageDetail.getDetailAppUrl());
    assertEquals("/content/dam/saudi-tourism/media/tour-packages/jeddah/"
        + "260x195/161652_5914 x 4436.jpg", packageDetail.getFeatureImage());
    assertEquals("packages/full-day-in-jeddah", packageDetail.getId());
    assertNull(packageDetail.getRelatedPackagesPaths());
    assertNull(packageDetail.getS7bannerImage());
    assertEquals("Explore the history of Jeddah its ancient heritage through a unique full-day "
        + "tour. ", packageDetail.getShortDescription());
    assertEquals("/content/dam/saudi-tourism/media/tour-packages/jeddah/760x570/"
        + "161652_5914 x 4436.jpg", packageDetail.getSliderImage());
    assertNull(packageDetail.getSliderImageCaption());
    assertEquals("Explore the history of Jeddah its ancient heritage through a unique full-day "
        + "tour. ", packageDetail.getSubtitle());
    assertEquals("Full day in Jeddah", packageDetail.getTitle());
    List<PackageInfo> additionalInformation = packageDetail.getAdditionalInformation();
    assertEquals(4, additionalInformation.size());
    assertEquals("Important Information", additionalInformation.get(0).getTitle());
    assertEquals("check", additionalInformation.get(0).getIcon());
    assertEquals(7, additionalInformation.get(0).getItems().size());
    assertEquals("This package is for groups with a minimum of 4 adults",
        additionalInformation.get(0).getItems().get(0).getText());
    assertEquals("Children under the age of 10 will receive a 20% discount",
        additionalInformation.get(0).getItems().get(1).getText());
    assertEquals("Payment must be made 15 days before the start of the trip",
        additionalInformation.get(0).getItems().get(2).getText());
    assertEquals("This package is changeable upon request",
        additionalInformation.get(0).getItems().get(3).getText());
    assertEquals("The maximum passenger capacity per car is 5 people",
        additionalInformation.get(0).getItems().get(4).getText());
    assertEquals("The maximum capacity per bus is 30 people.",
        additionalInformation.get(0).getItems().get(5).getText());
    assertEquals("Appropriate shoes and clothing must be worn according to the nature of "
            + "the place being visited.", additionalInformation.get(0).getItems().get(6).getText());
    assertNull(packageDetail.getPackageDaysModels().getTitle());
    assertEquals(1, packageDetail.getPackageDaysModels().getDays().size());
    assertEquals(1, packageDetail.getDays().size());
    PackageDayModel day = packageDetail.getDays().get(0);
    assertEquals("Itinerary", day.getTitle());
    assertEquals("/content/dam/saudi-tourism/media/red-sea-route-/dmc-content/plt/660x370/Jeddah "
        + "Old City (2).jpg", day.getImage());
    assertEquals("https://s7g10.scene7.com/is/image/scth/Jeddah Old City (2)-3",
        day.getS7image());
    assertEquals(9, day.getItems().size());
    assertEquals("Meet at the pick-up point", day.getItems().get(0).getText());
    PackageInfo important = packageDetail.getImportantInformation();
    assertEquals("Important Information", important.getTitle());
    assertEquals("check", important.getIcon());
    assertEquals(7, important.getItems().size());
    assertEquals("This package is for groups with a minimum of 4 adults",
        important.getItems().get(0).getText());
    assertEquals(7, important.getDetails().size());
    assertEquals("This package is for groups with a minimum of 4 adults",
        important.getDetails().get(0));
    assertEquals(new TitleSubtitle(), packageDetail.getLead().getError());
    assertEquals(new TitleSubtitle(), packageDetail.getLead().getSuccess());
    assertEquals("Package excludes", packageDetail.getPackageExclude().getTitle());
    assertEquals("ticket-no", packageDetail.getPackageExclude().getIcon());
    assertEquals(4, packageDetail.getPackageExclude().getItems().size());
    assertEquals("Flight bookings", packageDetail.getPackageExclude().getItems().get(0).getText());
    assertEquals(4, packageDetail.getPackageExclude().getDetails().size());
    assertEquals("Flight bookings", packageDetail.getPackageExclude().getDetails().get(0));
    assertEquals("Package includes", packageDetail.getPackageInclude().getTitle());
    assertEquals("ticket", packageDetail.getPackageInclude().getIcon());
    assertEquals(5, packageDetail.getPackageInclude().getItems().size());
    assertEquals("Professional Local Tour Guide",
        packageDetail.getPackageInclude().getItems().get(0).getText());
    assertEquals(5, packageDetail.getPackageInclude().getDetails().size());
    assertEquals("Professional Local Tour Guide",
        packageDetail.getPackageInclude().getDetails().get(0));
    assertEquals("Price Info title, edit me", packageDetail.getPriceInfo().getTitle());
    assertEquals("price", packageDetail.getPriceInfo().getIcon());
    assertEquals("349", packageDetail.getPrice());
    assertEquals("startingFromCapitalLabel", packageDetail.getPriceDisplay());
    assertEquals("startingFrom2", packageDetail.getCtaText());
    assertNull(packageDetail.getPriceInfo().getItems());
    assertEquals("palms-land-tours", packageDetail.getDmc());
    assertEquals("Jeddah", packageDetail.getAreasEn());
  }

}
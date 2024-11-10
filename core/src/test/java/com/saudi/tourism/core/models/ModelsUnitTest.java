package com.saudi.tourism.core.models;

import com.saudi.tourism.core.beans.ClientData;
import com.saudi.tourism.core.beans.MailchimpParams;
import com.saudi.tourism.core.beans.PackageFormParams;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.cache.MemHolder;
import com.saudi.tourism.core.login.OauthTokenModel;
import com.saudi.tourism.core.models.app.common.AppBaseModel;
import com.saudi.tourism.core.models.app.contact.AppContactPageModel;
import com.saudi.tourism.core.models.app.contact.Contact;
import com.saudi.tourism.core.models.app.contact.Embassy;
import com.saudi.tourism.core.models.app.contact.Service;
import com.saudi.tourism.core.models.app.content.PathModel;
import com.saudi.tourism.core.models.app.content.RelatedModel;
import com.saudi.tourism.core.models.app.content.TabModel;
import com.saudi.tourism.core.models.app.eventpackage.PackageDayDetail;
import com.saudi.tourism.core.models.app.eventpackage.PackagesInfo;
import com.saudi.tourism.core.models.app.page.AppCarousel;
import com.saudi.tourism.core.models.app.page.AppHomepage;
import com.saudi.tourism.core.models.app.page.AppPage;
import com.saudi.tourism.core.models.app.page.AppPageInfo;
import com.saudi.tourism.core.models.app.page.ComponentInfo;
import com.saudi.tourism.core.models.app.page.EVisaBannerConfig;
import com.saudi.tourism.core.models.app.page.LocationPageInfo;
import com.saudi.tourism.core.models.app.page.PageInfo;
import com.saudi.tourism.core.models.common.AnalyticsLinkModel;
import com.saudi.tourism.core.models.common.CTA;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.ObjectResponse;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.common.SliderDataLayer;
import com.saudi.tourism.core.models.common.SocialChannelModel;
import com.saudi.tourism.core.models.common.TextModel;
import com.saudi.tourism.core.models.components.AdminPageAlert;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.NavItem;
import com.saudi.tourism.core.models.components.NavMenuBase;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.models.components.PlaceholderKey;
import com.saudi.tourism.core.models.components.PlaceholderTranslation;
import com.saudi.tourism.core.models.components.TextCardsModel;
import com.saudi.tourism.core.models.components.WelcomeIntroModel;
import com.saudi.tourism.core.models.components.WidgetModel;
import com.saudi.tourism.core.models.components.account.AccountPropertiesModel;
import com.saudi.tourism.core.models.components.account.InputOptionsModel;
import com.saudi.tourism.core.models.components.account.ToastMessagesModel;
import com.saudi.tourism.core.models.components.account.UserApiModel;
import com.saudi.tourism.core.models.components.articlecard.v1.ArticleCardModel;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.models.components.cardsgrid.GridCard;
import com.saudi.tourism.core.models.components.downloadbanner.Apps;
import com.saudi.tourism.core.models.components.events.BaseFilterModel;
import com.saudi.tourism.core.models.components.events.BaseRequestParams;
import com.saudi.tourism.core.models.components.events.ButtonsModel;
import com.saudi.tourism.core.models.components.events.DatePlaceholder;
import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.models.components.events.EventFilterModel;
import com.saudi.tourism.core.models.components.events.EventImage;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventPathModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.events.InfoItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.evisa.v1.EvisaModel;
import com.saudi.tourism.core.models.components.footnote.v1.FootnoteModel;
import com.saudi.tourism.core.models.components.fullbleedslider.v1.FullBleedSliderModel;
import com.saudi.tourism.core.models.components.fullbleedvideo.v1.FullBleedVideoModel;
import com.saudi.tourism.core.models.components.hero.v1.BrandPageHeroModel;
import com.saudi.tourism.core.models.components.hero.v1.CommonHeroModel;
import com.saudi.tourism.core.models.components.hotels.HotelListSlingModel;
import com.saudi.tourism.core.models.components.hotels.HotelsFilterModel;
import com.saudi.tourism.core.models.components.hotels.HotelsListModel;
import com.saudi.tourism.core.models.components.hotels.HotelsRequestParams;
import com.saudi.tourism.core.models.components.listcomponent.v1.CardListModel;
import com.saudi.tourism.core.models.components.listcomponent.v1.ListTableItem;
import com.saudi.tourism.core.models.components.map.v1.InterestPoint;
import com.saudi.tourism.core.models.components.map.v1.MapModel;
import com.saudi.tourism.core.models.components.nav.v2.MenuItems;
import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import com.saudi.tourism.core.models.components.nav.v2.NewHeaderSlingModel;
import com.saudi.tourism.core.models.components.nav.v2.PlanItem;
import com.saudi.tourism.core.models.components.packages.ExitConfirmationModel;
import com.saudi.tourism.core.models.components.packages.InputModel;
import com.saudi.tourism.core.models.components.packages.InputsModel;
import com.saudi.tourism.core.models.components.packages.LeadFormSuccessError;
import com.saudi.tourism.core.models.components.packages.NoResultSlingModel;
import com.saudi.tourism.core.models.components.packages.PackageDayModel;
import com.saudi.tourism.core.models.components.packages.PackageDaysModel;
import com.saudi.tourism.core.models.components.packages.PackageInfoItem;
import com.saudi.tourism.core.models.components.packages.PackageListSlingModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.models.components.packages.PhoneInputModel;
import com.saudi.tourism.core.models.components.packages.TitleSubtitle;
import com.saudi.tourism.core.models.components.packages.VendorLink;
import com.saudi.tourism.core.models.components.packages.VendorList;
import com.saudi.tourism.core.models.components.search.SearchConfigModel;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.models.components.search.SearchPill;
import com.saudi.tourism.core.models.components.search.SearchPillsModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.models.components.search.SearchTrendingsModel;
import com.saudi.tourism.core.models.components.simpleTitleIntro.SimpleTitleIntroModel;
import com.saudi.tourism.core.models.components.title.v1.Faq;
import com.saudi.tourism.core.models.components.topactivities.TopActivitiesModel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.DefaultPackageFilter;
import pl.pojo.tester.api.PackageFilter;
import pl.pojo.tester.api.assertion.Method;

import java.util.ArrayList;
import java.util.List;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsForAll;

/**
 * @noinspection rawtypes
 */
@Disabled
class ModelsUnitTest {

  @Test void testPojoModelClasses() {
    // Test classes for getters + setters + contructor
    assertPojoMethodsForAll(getModelClassesForCompleteTest())
        .testing(Method.GETTER, Method.SETTER, Method.CONSTRUCTOR).areWellImplemented();

    // Test all classes in specified packages for getters + constructors
    final List<PackageFilter> packageFilterList = getPackagesForTest();
    packageFilterList.forEach(packageFilter -> assertPojoMethodsForAll(packageFilter)
        .testing(Method.GETTER, Method.CONSTRUCTOR).areWellImplemented());

    // Test getters + constructor
    assertPojoMethodsForAll(getModelClassForBasicTest()).testing(Method.GETTER, Method.CONSTRUCTOR)
        .areWellImplemented();

    // Test setters + constructor
    assertPojoMethodsForAll(getModelClassForBasicSetTest())
        .testing(Method.SETTER, Method.CONSTRUCTOR).areWellImplemented();
  }

  /**
   * Get packages for testing all classes inside for basic tests (getter, constructor).
   *
   * @return packages list.
   */
  private List<PackageFilter> getPackagesForTest() {
    List<PackageFilter> packageList = new ArrayList<>();
    packageList.add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.components"));
    packageList
        .add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.components.events"));
    packageList.add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.app.cruise"));
    packageList.add(
        DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.app.eventpackage"));
    packageList.add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.app.i18n"));
    packageList.add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.models.app.legal"));
    packageList.add(DefaultPackageFilter.forPackage("com.saudi.tourism.core.login.models"));
    return packageList;
  }

  /**
   * Get Model classes for testing fields.
   * Only class with all-fields getters, setters and with a constructor are here.
   * <p>
   * Note: Put here all that annotated with {@link lombok.Data}!
   *
   * @return class list.
   */
  private Class[] getModelClassesForCompleteTest() {
    return new Class[]{// @formatter:off
        AdminPageAlert.class,
        AdminPageOption.class,
        ArticleItem.class,
        CardModel.class,
        ClientData.class,
        EventFilterModel.class,
        EventImage.class,
        EventsRequestParams.class,
        Image.class,
        InfoItem.class,
        Link.class,
        MenuItems.class,
        NavItem.class,
        PackageDayDetail.class,
        PackageFormParams.class,
        PackagesInfo.class,
        Pagination.class,
        SearchResultModel.class, SearchListResultModel.class, Pagination.class, AppBaseModel.class,
        PathModel.class, RelatedModel.class, TabModel.class, AppCarousel.class, AppHomepage.class,
        AppPage.class, AppPageInfo.class, ComponentInfo.class, EVisaBannerConfig.class,
        LocationPageInfo.class, PageInfo.class, AnalyticsLinkModel.class, CategoryTag.class,
        ComponentHeading.class, CTA.class, ObjectResponse.class, ResponseMessage.class,
        SliderDataLayer.class, SearchPill.class, SearchPillsModel.class, SearchConfigModel.class,
        ExitConfirmationModel.class, InputModel.class, InputsModel.class,
        LeadFormSuccessError.class, NoResultSlingModel.class, PackagesRequestParams.class,
        PhoneInputModel.class, TitleSubtitle.class, VendorLink.class, VendorList.class,
        PlanItem.class, MenuItems.class, HotelsFilterModel.class, InputOptionsModel.class,
        HotelsListModel.class, HotelsRequestParams.class, BaseRequestParams.class,
        ButtonsModel.class, DatePlaceholder.class, EventAppFilterModel.class, Pagination.class,
        InfoItem.class, ClientData.class, MailchimpParams.class, PackageFormParams.class,
        OauthTokenModel.class

    }; //@formatter:on
  }

  /**
   * Get Model classes for test fields (Only constructor, getters).
   * Be sure these classes are not in {@link #getPackagesForTest()} (not in the packages
   * {@link com.saudi.tourism.core.models.components}
   * or {@link com.saudi.tourism.core.models.components.events}) because these ones are already
   * tested in per-package basic tests, not to be tested twice.
   *
   * @return class list.
   */
  private Class[] getModelClassForBasicTest() {
    return new Class[] {// @formatter:off
        ArticleCardModel.class,
        BrandPageHeroModel.class,
        CardListModel.class,
        CommonHeroModel.class,
        EvisaModel.class,
        FootnoteModel.class,
        FullBleedSliderModel.class,
        FullBleedVideoModel.class,
        MailchimpParams.class,
        MapModel.class,
        NavigationHeader.class,
        NewHeaderSlingModel.class, SearchParams.class, RegionCity.class, RegionCityExtended.class,
        Slide.class, TopActivitiesModel.class, AppContactPageModel.class, Contact.class,
        Embassy.class, Service.class, ImageCaption.class, SocialChannelModel.class,
        TextModel.class, PackagePageSettings.class, NavMenuBase.class, PlaceholderKey.class,
        PlaceholderTranslation.class, TextCardsModel.class, WelcomeIntroModel.class,
        WidgetModel.class, Faq.class, SimpleTitleIntroModel.class, SearchTrendingsModel.class,
        PackageDayModel.class, PackageDaysModel.class, PackageInfoItem.class,
        PackageListSlingModel.class, PackagesListModel.class, InterestPoint.class, MapModel.class,
        ListTableItem.class, HotelListSlingModel.class, GridCard.class, Apps.class,
        BaseFilterModel.class, EventListModel.class, EventPathModel.class, SearchParams.class,
        MemHolder.class
    };// @formatter:on
  }

  /**
   * Get Model classes for test fields (Only constructor, setters).
   *
   * @return class list.
   */
  private Class[] getModelClassForBasicSetTest() {
    return new Class[] {// @formatter:off
        AccountPropertiesModel.class, ToastMessagesModel.class, UserApiModel.class
    };// @formatter:on
  }
}

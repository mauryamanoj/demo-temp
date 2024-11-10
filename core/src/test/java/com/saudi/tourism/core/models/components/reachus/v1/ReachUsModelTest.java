package com.saudi.tourism.core.models.components.reachus.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Link;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ReachUsModelTest {

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(
        ReachUsModel.class,
        PhoneCardModel.class,
        PhoneCardModel.ContactListModel.class,
        QuickLinkCardModel.class,
        SocialMediaCardModel.class,
        Link.class);
  }

  @Test
  public void testReachUsPhoneCardWithCountries(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/reachus/phone-card-countries.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us");

    // Act
    final var model =
        context
            .currentResource(
                "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us")
            .adaptTo(ReachUsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ReachUsModel.class);

    //Assert
    assertEquals("PHONE_CARD", data.getType());
    assertEquals("Contact Us", data.getTitle());
    assertEquals("WITH_COUNTRIES", data.getVariation());

    final var phoneCard = data.getPhoneCard();
    assertNotNull(phoneCard);
    assertEquals("Call Us", phoneCard.getCallUsCta());
    assertEquals("Live", phoneCard.getLiveLabel());
    assertEquals("Off line", phoneCard.getOfflineLabel());

    final var countries = data.getPhoneCard().getContactItems();
    assertNotNull(countries);
    assertEquals(2, countries.size());
    assertEquals("/content/dam/sauditourism/favicon.png", countries.get(0).getCountryFlag());
    assertEquals("Arabic Saudi", countries.get(0).getContactName());
    assertEquals("+9655432165343", countries.get(0).getPhoneNumber());
  }

  @Test
  public void testReachUsPhoneCardWithNumbers(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/reachus/phone-card-numbers.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us")
        .adaptTo(ReachUsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ReachUsModel.class);

    //Assert
    assertEquals("PHONE_CARD", data.getType());
    assertEquals("Emergency numbers in Saudi", data.getTitle());
    assertEquals("WITH_NUMBERS", data.getVariation());

    final var phoneCard = data.getPhoneCard();
    assertNotNull(phoneCard);
    assertEquals("Call Us", phoneCard.getCallUsCta());
    assertEquals("Live", phoneCard.getLiveLabel());
    assertEquals("Off line", phoneCard.getOfflineLabel());

    final var numbers = data.getPhoneCard().getContactItems();
    assertNotNull(numbers);
    assertEquals(1, numbers.size());
    assertEquals("Saudi Ambulance", numbers.get(0).getContactName());
    assertEquals("+12334558976", numbers.get(0).getPhoneNumber());
  }

  @Test
  public void testReachUsSmallPhoneCard(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/reachus/phone-card-small.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us")
        .adaptTo(ReachUsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ReachUsModel.class);

    //Assert
    assertEquals("PHONE_CARD", data.getType());
    assertEquals("Police number in Riyadh, Makkah,  and Eastern provinces", data.getTitle());
    assertEquals("SMALL", data.getVariation());

    final var phoneCard = data.getPhoneCard();
    assertNotNull(phoneCard);
    assertEquals("Call Us", phoneCard.getCallUsCta());
    assertEquals("Live", phoneCard.getLiveLabel());
    assertEquals("Off line", phoneCard.getOfflineLabel());
    assertEquals("+11111", phoneCard.getPhoneNumber());

  }
  @Test
  public void testReachUsQuickLinkCardType(final AemContext context) {
    //Arrange
    context
      .load()
      .json(
        "/components/reachus/quicklink-card.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us")
        .adaptTo(ReachUsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ReachUsModel.class);

    //Assert
    assertEquals("QUICK_LINK_CARD", data.getType());
    assertEquals("Chat With Us", data.getTitle());

    final var quickLinkCard = data.getQuickLinkCard();
    assertNotNull(quickLinkCard);
    assertEquals("Start a Whatsapp chat", quickLinkCard.getSubTitle());
    assertEquals("https://web.whatsapp.com/", quickLinkCard.getLink().getUrl());
  }

  @Test
  public void testReachUsSocialMediaCardType(final AemContext context) {
    //Arrange
    context
      .load()
      .json(
        "/components/reachus/socialmedia-card.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/reach_us")
        .adaptTo(ReachUsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ReachUsModel.class);

    //Assert
    assertEquals("SOCIAL_MEDIA_CARD", data.getType());
    assertEquals("On Social Media", data.getTitle());

    final var socialMediaCards = data.getSocialMediaCards();
    assertNotNull(socialMediaCards);
    assertEquals(4, socialMediaCards.size());
    assertEquals("categories_xs_nature", socialMediaCards.get(2).getIcon());
    assertEquals(true, socialMediaCards.get(2).getLink().isTargetInNewWindow());
    assertEquals("x.com/visit-saudi", socialMediaCards.get(2).getLink().getUrl());
    assertEquals("categories_xs_entertainment", socialMediaCards.get(3).getIcon());
    assertEquals("linkedin.com/visit-saudi", socialMediaCards.get(3).getLink().getUrl());
    assertEquals("Visit Saudi", socialMediaCards.get(3).getLink().getText());
    assertEquals(true, socialMediaCards.get(2).getLink().isTargetInNewWindow());
  }

}
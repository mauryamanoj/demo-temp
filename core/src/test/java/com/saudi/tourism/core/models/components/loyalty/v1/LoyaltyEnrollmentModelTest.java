package com.saudi.tourism.core.models.components.loyalty.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class LoyaltyEnrollmentModelTest {

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/loyalty-enrollment/content.json",
        "/content/sauditourism/en/jcr:content/root/responsivegrid/loyalty_enrollment");
  }

  @Test
  public void testLoyaltyEnrollmentModel(AemContext context) {
    //Act
    final var model =
        context.currentResource("/content/sauditourism/en/jcr:content/root/responsivegrid/loyalty_enrollment")
            .adaptTo(LoyaltyEnrollmentModel.class);
    final String json = model.getJson();
    final var data = gson.fromJson(json, LoyaltyEnrollmentModel.class);

    //Assert
    assertEquals("action", data.getQueryParameter().getName());
    assertEquals("loyalty-enrollment", data.getQueryParameter().getValue());

    assertEquals("Hey!", data.getLoginModal().getTitle());
    assertEquals("Log In or Sign Up to enroll to our loyalty program", data.getLoginModal().getParagraph());
    assertEquals("Log In or Sign Up", data.getLoginModal().getButton().getCopy());
    assertEquals("/bin/api/v1/ssid/login-url", data.getLoginModal().getButton().getLink());

    assertEquals(
        "It looks like you're already a member of our Loyalty Program. No need to sign up again – you're all set to keep enjoying your benefits and rewards. Thanks for being a loyal member!",
        data.getEnrollmentModal().getAlreadyEnrolledMessage());
    assertEquals(
        "Welcome to our Loyalty Program! Your onboarding was successful. Get ready to enjoy exclusive rewards and special offers. We're excited to have you with us!",
        data.getEnrollmentModal().getSuccessfullyEnrolledMessage());
    assertEquals(
        "We're sorry, but we couldn't complete your onboarding to our Loyalty Program. Please try again or reach out to our support team for help. We're eager to assist you!",
        data.getEnrollmentModal().getUnsuccessfullyEnrolledMessage());
    assertEquals("OK", data.getEnrollmentModal().getCopy());
    assertEquals("/bin/api/v2/user/loyalty-enrollment", data.getEnrollmentModal().getApiEndpoint());
  }

}
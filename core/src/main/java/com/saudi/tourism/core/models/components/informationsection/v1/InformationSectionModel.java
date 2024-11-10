package com.saudi.tourism.core.models.components.informationsection.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.RunModeService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class InformationSectionModel {
  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * Resource Resolver.
   */
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * Enable manual authoring.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Expose
  private Boolean enableManualAuthoring;

  /**
   * Title.
   */
  @Expose
  private String title;

  /**
   * CTA.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * Questions and Answers.
   */
  @Expose
  private List<FAQ> data = new ArrayList<>();

  /**
   * Hide View More.
   */
  @Expose
  private Boolean hideReadMore;

  /**
   * View More Label.
   */
  @Expose
  private String readMoreLabel;

  /**
   * View Less Label.
   */
  @Expose
  private String readLessLabel;

  @PostConstruct
  void init() {

    if (Boolean.FALSE.equals(enableManualAuthoring)) {
      final var pageManager = resourceResolver.adaptTo(PageManager.class);
      final var currentPage = pageManager.getContainingPage(currentResource);
      if (currentPage == null) {
        return;
      }

      final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
      if (StringUtils.isEmpty(cfPath)) {
        return;
      }

      final var cfResource = resourceResolver.getResource(cfPath);
      if (cfResource == null) {
        return;
      }

      final var cfModel = cfResource.adaptTo(InformationSectionCFModel.class);
      if (cfModel == null) {
        return;
      }

      title = cfModel.getQaTitle();
      FAQDeserializer deserializer = new FAQDeserializer(FAQ.class, resourceResolver, runModeService);
      ObjectMapper mapper = new ObjectMapper();
      for (String questionAnswer : cfModel.getQuestionsAnswers()) {
        try {
          SimpleModule module = new SimpleModule();
          module.addDeserializer(FAQ.class, deserializer);
          mapper.registerModule(module);

          FAQ faq = mapper.readValue(questionAnswer, FAQ.class);
          data.add(faq);
        } catch (JsonProcessingException e) {
          LOGGER.warn("Error while parsing QuestionsAnswers item", e);
        }
      }
      hideReadMore = cfModel.isQaHideReadMore();
      readMoreLabel = cfModel.getQaReadMoreLabel();
      readLessLabel = cfModel.getQaReadLessLabel();
    }

    if (Boolean.TRUE.equals(enableManualAuthoring)) {
      if (Objects.isNull(currentResource)) {
        return;
      }
      ValueMap valueMap = currentResource.getValueMap();
      title = valueMap.get("title", String.class);
      if (currentResource.hasChildren()) {
        Resource questionsAnswersResource = currentResource.getChild("questionsAnswers");
        if (Objects.nonNull(questionsAnswersResource)) {
          questionsAnswersResource.getChildren().forEach(child ->
              data.add(child.adaptTo(FAQ.class))
          );
        }
        hideReadMore = valueMap.get("hideReadMore", Boolean.class);
        readMoreLabel = valueMap.get("readMoreLabel", String.class);
        readLessLabel = valueMap.get("readLessLabel", String.class);
      }
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}

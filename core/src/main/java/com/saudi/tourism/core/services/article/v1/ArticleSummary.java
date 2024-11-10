package com.saudi.tourism.core.services.article.v1;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

/** Article Model. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
public class ArticleSummary {

  /**
   * Article Id.
   */
  private String id;

  /**
   * Title.
   */
  private String title;

  /**
   * Splash Image.
   */
  private String splashImage;

  /**
   * Author.
   */
  private String author;

  /**
   * Time to Read.
   */
  private String timeToRead;

  /**
   * Time to Read Minutes.
   */
  @JsonIgnore
  private String timeToReadMinutes;

  /**
   * Time to Read Suffix.
   */
  @JsonIgnore
  private String timeToReadSuffix;

  /**
   * Tags.
   */
  @JsonIgnore
  private transient List<String> articleTags;

  /**
   * Articles Tags.
   */
  private  List<ArticleTag> tags;

  /**
   * Category.
   */
  @JsonIgnore
  private String category;


  public static final ArticleSummary fromCFModel(final ArticleCFModel model) {
    if (model == null) {
      return null;
    }
    String timeToReadMinutes = Optional.ofNullable(model.getTimeToReadMinutes()).orElse("");
    String timeToReadSuffix = Optional.ofNullable(model.getTimeToReadSuffix()).orElse("");
    String timeToRead = String.format("%s %s", timeToReadMinutes, timeToReadSuffix).trim();
    return ArticleSummary.builder()
      .id(model.getId())
      .title(model.getTitle())
      .splashImage(model.getSplashImage())
      .author(model.getAuthor())
      .timeToRead(timeToRead)
      .articleTags(
        Optional.ofNullable(model.getTags())
          .map(Arrays::stream)
          .orElseGet(Stream::empty)
          .collect(Collectors.toList()))
      .category(model.getCategory())
      .build();
  }
}

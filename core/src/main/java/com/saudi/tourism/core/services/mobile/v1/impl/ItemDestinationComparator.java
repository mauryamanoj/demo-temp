package com.saudi.tourism.core.services.mobile.v1.impl;

import java.util.Comparator;
import java.util.List;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;

/**
 * A comparator for sorting {@link ItemResponseModel} objects based on whether their destination ID
 * matches any ID in a provided list of destination IDs.
 * <p>
 * This comparator is designed to prioritize items whose destination IDs are found within
 * the specified list of destination IDs, facilitating sorting where matched items come before unmatched ones.
 * </p>
 */
public class ItemDestinationComparator implements Comparator<ItemResponseModel> {

  /**
   * The list of destination IDs to compare against.
   */
  private final List<String> destinations;

  /**
   * Constructs a new {@code ItemDestinationComparator} with a given list of destination IDs.
   *
   * @param destinations The list of destination IDs to use for comparison.
   */
  public ItemDestinationComparator(List<String> destinations) {
    this.destinations = destinations;
  }

  /**
   * Compares two {@link ItemResponseModel} objects to determine their ordering with respect to the list
   * of destination IDs provided at construction.
   * <p>
   * The comparison prioritizes items whose destination IDs match any of the destination IDs in the list
   * over those that do not. If both items match or both do not match, they are considered equal in terms
   * of this comparison.
   * </p>
   *
   * @param item1 the first item to be compared.
   * @param item2 the second item to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than,
   * equal to, or greater than the second.
   */
  @Override
  public int compare(ItemResponseModel item1, ItemResponseModel item2) {
    boolean item1Matches = false;
    boolean item2Matches = false;

    if (item1.getLocation() != null && item1.getLocation().getDestination() != null) {
      String id1 = item1.getLocation().getDestination().getId();
      item1Matches = destinations.contains(id1);
    }

    if (item2.getLocation() != null && item2.getLocation().getDestination() != null) {
      String id2 = item2.getLocation().getDestination().getId();
      item2Matches = destinations.contains(id2);
    }

    if (item1Matches && !item2Matches) {
      return -1; // item1 comes before item2
    } else if (!item1Matches && item2Matches) {
      return 1; // item2 comes before item1
    } else {
      return 0; // equal priority
    }
  }
}

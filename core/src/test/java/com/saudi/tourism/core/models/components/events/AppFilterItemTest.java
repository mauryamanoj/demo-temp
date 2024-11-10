package com.saudi.tourism.core.models.components.events;

import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * AppFilterItem Unit Test.
 *
 * @noinspection SimplifiableJUnitAssertion, EqualsBetweenInconvertibleTypes,
 * MismatchedQueryAndUpdateOfCollection, StringOperationCanBeSimplified,
 * EqualsWithItself, ConstantConditions
 */
class AppFilterItemTest {

  private static final String ID = "id";

  private static final String ID1 = "id1";

  private static final String VAL = "val";

  private static final String VAL1 = "val1";

  @Test
  void valueMapConstructorTest() {
    final ValueMap valueMap = mock(ValueMap.class);
    doReturn("Some Value/for id").when(valueMap).get(eq("value"));
    doReturn("some text").when(valueMap).get(eq("text"));

    final AppFilterItem item = new AppFilterItem(valueMap);
    // AppUtils can't be mocked so it's checked multiple times
    assertEquals("some-value/for-id", item.getId());
    assertEquals("some text", item.getValue());
  }

  @Test
  public void Should_Equal_Itself() {
    final AppFilterItem item = new AppFilterItem(ID, VAL);

    assertTrue(item.equals(item));
  }

  @Test
  public void Should_Equal_Other_Object_With_Same_Values() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID, VAL);

    assertTrue(item1.equals(item2));
  }

  @Test
  public void Should_Not_Equal_Null() {
    final AppFilterItem item = new AppFilterItem(ID, VAL);

    assertFalse(item.equals(null));
  }

  @Test
  public void Should_Not_Equal_Other_Object_With_Different_Ids() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID1, VAL);

    assertFalse(item1.equals(item2));
  }

  @Test
  public void Should_Equal_Other_Object_With_Same_Id() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID, VAL1);

    assertTrue(item1.equals(item2));
  }

  @Test
  public void Should_Not_Equal_Object_Of_Different_Type() {
    final AppFilterItem item = new AppFilterItem(ID, VAL);

    assertFalse(item.equals(new String()));
  }

  @Test
  public void Should_Generate_Same_Hash_Code_Every_Time() {
    final AppFilterItem item = new AppFilterItem(ID, VAL);

    final int result1 = item.hashCode();
    final int result2 = item.hashCode();

    assertEquals(result1, result2);
  }

  @Test
  public void Should_Generate_Same_Hash_Code_For_Equal_Objects() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID, VAL);

    final int result1 = item1.hashCode();
    final int result2 = item2.hashCode();

    assertEquals(result1, result2);
  }

  @Test
  public void Should_Generate_Same_Hash_Code_For_Objects_With_Same_ID() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID, VAL1);

    final int result1 = item1.hashCode();
    final int result2 = item2.hashCode();

    assertEquals(result1, result2);
  }

  @Test
  public void Should_Generate_Different_Hash_Code_For_Different_Objects() {
    final AppFilterItem item1 = new AppFilterItem(ID, VAL);
    final AppFilterItem item2 = new AppFilterItem(ID1, VAL);

    final int result1 = item1.hashCode();
    final int result2 = item2.hashCode();

    assertNotEquals(result1, result2);
  }

}

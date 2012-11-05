package org.zanata.search;

import org.hamcrest.Matchers;
import org.hibernate.Query;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.model.HLocale;
import org.zanata.webtrans.shared.model.DocumentId;

import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Test(groups = "unit-tests")
@Slf4j
public class FilterConstraintToQueryTest
{
   public static final String SOURCE_CONTENT_CASE_INSENSITIVE = "(lower(tf.content0) like :searchString or lower(tf.content1) like :searchString or lower(tf.content2) like :searchString or lower(tf.content3) like :searchString or lower(tf.content4) like :searchString or lower(tf.content5) like :searchString)";
   public static final String TARGET_CONTENT_CASE_INSENSITIVE = "(lower(content0) like :searchString or lower(content1) like :searchString or lower(content2) like :searchString or lower(content3) like :searchString or lower(content4) like :searchString or lower(content5) like :searchString)";
   public static final String SOURCE_CONTENT_CASE_SENSITIVE = "((tf.content0) like :searchString or (tf.content1) like :searchString or (tf.content2) like :searchString or (tf.content3) like :searchString or (tf.content4) like :searchString or (tf.content5) like :searchString)";
   public static final String TARGET_CONTENT_CASE_SENSITIVE = "((content0) like :searchString or (content1) like :searchString or (content2) like :searchString or (content3) like :searchString or (content4) like :searchString or (content5) like :searchString)";
   public static final String QUERY_BEFORE_WHERE = "SELECT distinct tf FROM HTextFlow tf LEFT JOIN tf.targets tfts WITH index(tfts)=:locale ";
   @Mock
   private Query query;
   @Mock
   private HLocale hLocale;
   private DocumentId documentId = new DocumentId(1);

   @BeforeMethod
   public void beforeMethod()
   {
      MockitoAnnotations.initMocks(this);
      when(hLocale.getId()).thenReturn(2L);
   }

   @Test
   public void testBuildSearchConditionWithNothingToSearch()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.keepAll());

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.nullValue());
   }

   @Test
   public void testBuildSearchConditionInSource()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("FiLe").ignoreTarget().filterSource());

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.equalToIgnoringCase("(" + SOURCE_CONTENT_CASE_INSENSITIVE + ")"));
   }

   @Test
   public void testBuildSearchConditionInTarget()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("FiLe").ignoreSource().filterTarget());

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.equalToIgnoringCase("( EXISTS ( FROM HTextFlowTarget WHERE (textFlow=tf and locale=:locale and " + TARGET_CONTENT_CASE_INSENSITIVE + ")))"));

   }

   @Test
   public void testBuildSearchConditionInBoth()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("FiLe"));

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.equalToIgnoringCase("(" + SOURCE_CONTENT_CASE_INSENSITIVE + " OR  EXISTS ( FROM HTextFlowTarget WHERE (textFlow=tf and locale=:locale and " + TARGET_CONTENT_CASE_INSENSITIVE + ")))"));
   }

   @Test
   public void testCaseSensitiveSearch()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("FiLe").caseSensitive(true));

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.equalToIgnoringCase("(" + SOURCE_CONTENT_CASE_SENSITIVE + " OR  EXISTS ( FROM HTextFlowTarget WHERE (textFlow=tf and locale=:locale and " + TARGET_CONTENT_CASE_SENSITIVE + ")))"));
   }

   @Test
   public void testBuildStateConditionWithAllState()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.keepAll());

      String result = constraintToQuery.buildSearchCondition();

      assertThat(result, Matchers.nullValue());
   }

   @Test
   public void testBuildStateConditionWithoutUntranslatedState()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.keepAll().excludeNew());

      String result = constraintToQuery.buildStateCondition();

      assertThat(result, Matchers.equalToIgnoringCase(" EXISTS ( FROM HTextFlowTarget WHERE (textFlow=tf and locale=:locale AND state in (:contentStateList)))"));
   }

   @Test
   public void testBuildStateConditionWithUntranslatedStateButNoSearch()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.keepAll().excludeApproved());

      String result = constraintToQuery.buildStateCondition();

      // @formatter:off
      assertThat(result, Matchers.equalToIgnoringCase(
            "( EXISTS " +
                  "( FROM HTextFlowTarget WHERE " +
                     "(textFlow=tf and locale=:locale AND state in (:contentStateList))" +
                  ") OR :locale not in indices(tf.targets)" +
            ")"));
      // @formatter:on
   }

   @Test
   public void testBuildStateConditionWithUntranslatedStateAndSearch()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("blah").excludeApproved());

      String result = constraintToQuery.buildStateCondition();

      // @formatter:off
      assertThat(result, Matchers.equalToIgnoringCase(
            "( EXISTS " +
                  "( FROM HTextFlowTarget WHERE " +
                     "(textFlow=tf and locale=:locale AND state in (:contentStateList))" +
                  ") OR " +
                  "(:locale not in indices(tf.targets) AND " + SOURCE_CONTENT_CASE_INSENSITIVE + ")" +
            ")"));
      // @formatter:on
   }

   @Test
   public void testToHQLWithNoCondition()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.keepAll());

      String result = constraintToQuery.toHQL();

      assertThat(result, Matchers.equalToIgnoringCase(QUERY_BEFORE_WHERE + "WHERE (tf.obsolete=0 AND tf.document.id=:docId) ORDER BY tf.pos"));
   }

   @Test
   public void testToHQLWithSearchAndStateCondition()
   {
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(FilterConstraints.filterBy("FiLe").excludeApproved());

      String result = constraintToQuery.toHQL();
      log.info("hql: {}", result);

      // @formatter:off
      assertThat(result, Matchers.equalToIgnoringCase(
            "SELECT distinct tf FROM HTextFlow tf LEFT JOIN tf.targets tfts WITH index(tfts)=:locale WHERE " +
                  "(tf.obsolete=0 AND tf.document.id=:docId AND " +
                     "(" +
                        SOURCE_CONTENT_CASE_INSENSITIVE + " OR  EXISTS " +
                        "( FROM HTextFlowTarget WHERE (textFlow=tf and locale=:locale and " + TARGET_CONTENT_CASE_INSENSITIVE + ")" +
                     ")" +
                  ") AND " +
                  "( EXISTS " +
                     "( FROM HTextFlowTarget WHERE " +
                        "(textFlow=tf and locale=:locale AND state in (:contentStateList))" +
                     ") OR " +
                     "(:locale not in indices(tf.targets) AND " + SOURCE_CONTENT_CASE_INSENSITIVE + ")" +
                  ")" +
                  ") ORDER BY tf.pos"));
      // @formatter:on
   }

   @Test
   public void testSetParametersForQuery()
   {
      FilterConstraints constraints = FilterConstraints.filterBy("file").excludeApproved();
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(constraints);

      constraintToQuery.setQueryParameters(query, documentId, hLocale);

      verify(query).setParameter(FilterConstraintToQuery.DOC_ID_NAMED_PARAM, documentId.getId());
      verify(query).setParameter(FilterConstraintToQuery.LOCALE_NAMED_PARAM, hLocale.getId());
      verify(query).setParameter(FilterConstraintToQuery.SEARCH_NAMED_PARAM, "%file%");
      verify(query).setParameterList(FilterConstraintToQuery.STATE_LIST_NAMED_PARAM, constraints.getContentStateAsList());
      verifyNoMoreInteractions(query);
   }

   @Test
   public void testSetParametersForQueryWithNoSearch()
   {
      FilterConstraints constraints = FilterConstraints.keepAll().excludeApproved();
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(constraints);

      constraintToQuery.setQueryParameters(query, documentId, hLocale);

      verify(query).setParameter(FilterConstraintToQuery.DOC_ID_NAMED_PARAM, documentId.getId());
      verify(query).setParameter(FilterConstraintToQuery.LOCALE_NAMED_PARAM, hLocale.getId());
      verify(query).setParameterList(FilterConstraintToQuery.STATE_LIST_NAMED_PARAM, constraints.getContentStateAsList());
      verifyNoMoreInteractions(query);
   }

   @Test
   public void testSetParametersForQueryWithNoStateFilter()
   {
      FilterConstraints constraints = FilterConstraints.filterBy("FiLe");
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(constraints);

      constraintToQuery.setQueryParameters(query, documentId, hLocale);

      verify(query).setParameter(FilterConstraintToQuery.DOC_ID_NAMED_PARAM, documentId.getId());
      verify(query).setParameter(FilterConstraintToQuery.LOCALE_NAMED_PARAM, hLocale.getId());
      verify(query).setParameter(FilterConstraintToQuery.SEARCH_NAMED_PARAM, "%file%");
      verifyNoMoreInteractions(query);
   }

   @Test
   public void testSetParametersForQueryWithSearchCaseSensitive()
   {
      FilterConstraints constraints = FilterConstraints.filterBy("FiLe").caseSensitive(true);
      FilterConstraintToQuery constraintToQuery = FilterConstraintToQuery.from(constraints);

      constraintToQuery.setQueryParameters(query, documentId, hLocale);

      verify(query).setParameter(FilterConstraintToQuery.DOC_ID_NAMED_PARAM, documentId.getId());
      verify(query).setParameter(FilterConstraintToQuery.LOCALE_NAMED_PARAM, hLocale.getId());
      verify(query).setParameter(FilterConstraintToQuery.SEARCH_NAMED_PARAM, "%FiLe%");
      verifyNoMoreInteractions(query);
   }
}
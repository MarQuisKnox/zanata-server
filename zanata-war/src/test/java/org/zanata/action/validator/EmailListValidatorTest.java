package org.zanata.action.validator;

import javax.validation.ConstraintValidatorContext;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

@Test(groups = "unit-tests")
public class EmailListValidatorTest {
    private EmailListValidator validator;
    private ConstraintValidatorContext context = null;

    @BeforeMethod
    public void beforeMethod() {
        validator = new EmailListValidator();
        validator.initialize(null);
    }

    @Test
    public void nullOrEmptyStringIsValid() throws Exception {
        assertThat(validator.isValid(null, context)).isTrue();
        assertThat(validator.isValid("", context)).isTrue();
    }

    @Test
    public void isValidIfListsAreEmailsSeparatedByComma() {
        assertThat(validator.isValid("a@b.co,a@c.co", context)).isTrue();
        assertThat(validator.isValid("a@b.co ,a@c.co", context)).isTrue();
        assertThat(validator.isValid("a@b.co, a@c.co", context)).isTrue();
        assertThat(validator.isValid("a@b.co , a@c.co", context)).isTrue();
        assertThat(validator.isValid(" a@b.co , a@c.co", context)).isTrue();
        assertThat(validator.isValid(" a@b.co , a@c.co ", context)).isTrue();
    }

    @Test
    public void isInvalidIfListsContainNotValidEmail() {
        assertThat(validator.isValid("a@b.co,a b@1", context)).isFalse();
        assertThat(validator.isValid("a@b.co ,d", context)).isFalse();
    }
}

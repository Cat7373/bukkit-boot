package org.cat73.bukkitboot.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @see org.cat73.bukkitboot.util.Strings
 */
class StringsTest {
    @Test
    void isBlankTest() {
        Assertions.assertTrue(Strings.isBlank(null));
        Assertions.assertTrue(Strings.isBlank(""));
        Assertions.assertTrue(Strings.isBlank("  \t  "));
        Assertions.assertTrue(Strings.isBlank("  \t\n  "));
        Assertions.assertFalse(Strings.isBlank("  2\t\n  "));
    }

    @Test
    void notBlankTest() {
        Assertions.assertFalse(Strings.notBlank(null));
        Assertions.assertFalse(Strings.notBlank(""));
        Assertions.assertFalse(Strings.notBlank("  \t  "));
        Assertions.assertFalse(Strings.notBlank("  \t\n  "));
        Assertions.assertTrue(Strings.notBlank("  2\t\n  "));
    }

    @Test
    void isEmptyTest() {
        Assertions.assertTrue(Strings.isEmpty(null));
        Assertions.assertTrue(Strings.isEmpty(""));
        Assertions.assertFalse(Strings.isEmpty("  \t  "));
        Assertions.assertFalse(Strings.isEmpty("  \t\n  "));
        Assertions.assertFalse(Strings.isEmpty("  2\t\n  "));
    }

    @Test
    void notEmptyTest() {
        Assertions.assertFalse(Strings.notEmpty(null));
        Assertions.assertFalse(Strings.notEmpty(""));
        Assertions.assertTrue(Strings.notEmpty("  \t  "));
        Assertions.assertTrue(Strings.notEmpty("  \t\n  "));
        Assertions.assertTrue(Strings.notEmpty("  2\t\n  "));
    }
}

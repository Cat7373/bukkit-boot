package org.cat73.bukkitboot.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @see org.cat73.bukkitboot.util.Lang
 */
class LangTest {
    @Test
    void wrapThrowTest() {
        RuntimeException e = Lang.wrapThrow(new IOException("xxx"));

        Assertions.assertNotNull(e);
        Assertions.assertTrue(e.getCause() instanceof IOException);
    }

    @Test
    void makeThrowTest() {
        RuntimeException e = Lang.makeThrow("%s: %d", "foo", 123);

        Assertions.assertNotNull(e);
        Assertions.assertEquals(RuntimeException.class, e.getClass());
        Assertions.assertEquals("foo: 123", e.getMessage());
        Assertions.assertNull(e.getCause());
    }

    @Test
    void noImplementTest() {
        RuntimeException e = Lang.noImplement();

        Assertions.assertNotNull(e);
        Assertions.assertEquals(RuntimeException.class, e.getClass());
        Assertions.assertEquals("Not implement yet!", e.getMessage());
        Assertions.assertNull(e.getCause());
    }

    @Test
    void impossibleTest() {
        RuntimeException e = Lang.impossible();

        Assertions.assertNotNull(e);
        Assertions.assertEquals(RuntimeException.class, e.getClass());
        Assertions.assertEquals("r u kidding me?! It is impossible!", e.getMessage());
        Assertions.assertNull(e.getCause());
    }

    @Test
    void throwAnyTest() {
        Assertions.assertThrows(IOException.class, () -> Lang.throwAny(new IOException()));
    }

    @Test
    void wrapCodeTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapCode(() -> {
            throw new IOException();
        }));

        Assertions.assertEquals("test", Lang.wrapCode(() -> "test"));
    }

    @Test
    void wrapConsumerTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapConsumer(i -> { throw new IOException(); }).accept(1));
    }

    @Test
    void wrapBiConsumerTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapBiConsumer((a, b) -> { throw new IOException(); }).accept(1, 2));
    }

    @Test
    void wrapFunctionTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapFunction(i -> { throw new IOException(); }).apply(1));
    }

    @Test
    void wrapBiFunctionTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapBiFunction((a, b) -> { throw new IOException(); }).apply(1, 2));
    }

    @Test
    void wrapSupplierTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapSupplier(() -> { throw new IOException(); }).get());
    }

    @Test
    void wrapRunnableTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapRunnable(() -> { throw new IOException(); }).run());
    }

    @Test
    void wrapPredicateTest() {
        Assertions.assertThrows(RuntimeException.class, () -> Lang.wrapPredicate(t -> { throw new IOException(); }).test(1));
    }
}

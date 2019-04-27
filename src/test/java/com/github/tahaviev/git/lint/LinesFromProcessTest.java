/*
 * MIT License
 *
 * Copyright (c) 2019 tahaviev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.tahaviev.git.lint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link LinesFromProcess} test.
 */
public final class LinesFromProcessTest {

    /**
     * Can read lines.
     */
    @Test
    public void readLines() {
        final Collection<String> lines = Arrays.asList(
            "first", "second"
        );
        MatcherAssert.assertThat(
            new LinesFromProcess(
                () -> new FakeProcess(String.join("\n", lines))
            )
                .get(),
            Matchers.equalTo(lines)
        );
    }

    /**
     * Can throw I/O exception.
     */
    @Test
    public void throwException() {
        Assertions.assertThrows(
            IOException.class,
            () -> new LinesFromProcess(
                new Supplier<Process>() {

                    @Override
                    @SneakyThrows
                    public Process get() {
                        throw new IOException("test");
                    }
                }
            ).get()
        );
    }

}

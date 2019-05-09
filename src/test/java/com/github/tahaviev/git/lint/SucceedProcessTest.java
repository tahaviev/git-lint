/*-
 * #%L
 * Git Lint Maven Plugin
 * %%
 * Copyright (C) 2019 tahaviev
 * %%
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package com.github.tahaviev.git.lint;

import java.io.IOException;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SucceedProcess} test.
 */
public final class SucceedProcessTest {

    /**
     * Can be successfully completed.
     */
    @SneakyThrows
    @Test
    public void succeedOnSuccessProcess() {
        MatcherAssert.assertThat(
            new SucceedProcess(
                () -> new FakeSucceedProcess("success")
            )
                .get()
                .waitFor(),
            Matchers.equalTo(0)
        );
    }

    /**
     * Can throw exception on fail process.
     */
    @Test
    public void throwException() {
        Assertions.assertThrows(
            IOException.class,
            () -> new SucceedProcess(
                () -> new FakeFailedProcess("error")
            )
                .get()
                .waitFor()
        );
    }

}

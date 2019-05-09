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

import java.io.File;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * {@link CommitMessagesProcess} integration test.
 */
public final class CommitMessagesProcessIT {

    /**
     * {@link GitCommitsExtension} extension.
     */
    @RegisterExtension
    public static final Extension EXTENSION = new GitCommitsExtension(
        new String[]{"1", "2"}
    );

    /**
     * Can extract commit messages.
     *
     * @param directory git repository directory
     */
    @Test
    public void extractCommitMessages(
        @GitCommitsExtension.Directory final File directory
    ) {
        MatcherAssert.assertThat(
            new LinesFromProcess(
                new CommitMessagesProcess(directory, "master")
            )
                .get(),
            Matchers.equalTo(Arrays.asList("2", "1"))
        );
    }

}

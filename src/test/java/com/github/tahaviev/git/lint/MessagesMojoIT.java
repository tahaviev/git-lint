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
import java.io.IOException;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoFailureException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link MessagesMojo} integration test.
 */
public final class MessagesMojoIT {

    /**
     * {@link GitCommitsExtension} extension.
     */
    @RegisterExtension
    public static final Extension EXTENSION = new GitCommitsExtension(
        new String[]{"#123", "#test"}
    );

    /**
     * {@link MessagesMojo} instance to test.
     */
    private MessagesMojo mojo;

    /**
     * Creates {@link MessagesMojo} instance.
     *
     * @param directory git repository directory
     */
    @BeforeEach
    public void setUp(@GitCommitsExtension.Directory final File directory) {
        this.mojo = new MessagesMojo();
        this.mojo.setDirectory(directory);
        this.mojo.setParent("master");
    }

    /**
     * Can accept good commits.
     */
    @Test
    public void acceptGoodCommits() {
        this.mojo.setPattern("#.+");
        Assertions.assertDoesNotThrow(this.mojo::execute);
    }

    /**
     * Can reject commits with wrong messages.
     */
    @Test
    public void rejectWrongCommits() {
        this.mojo.setPattern("#(\\d)+");
        try {
            this.mojo.execute();
            Assertions.fail("no exception was thrown");
        } catch (final MojoFailureException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.allOf(
                    Matchers.containsString("#test"),
                    Matchers.not(
                        Matchers.containsString("#123")
                    )
                )
            );
        }
    }

    /**
     * Can throw exception on invalid input.
     *
     * @param temp temporary directory
     */
    @Test
    public void throwExceptionOnInvalidInput(@TempDir final Path temp) {
        this.mojo.setDirectory(temp.resolve("nonexistent").toFile());
        Assertions.assertThrows(
            IOException.class,
            this.mojo::execute
        );
    }

}

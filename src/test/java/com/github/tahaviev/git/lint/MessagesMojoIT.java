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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.maven.plugin.MojoFailureException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link MessagesMojo} integration test.
 */
public final class MessagesMojoIT {

    /**
     * Git directory.
     */
    private static File directory;

    /**
     * {@link MessagesMojo} instance to test.
     */
    private MessagesMojo mojo;

    /**
     * Creates {@link MessagesMojoIT#directory}.
     */
    @BeforeAll
    @SneakyThrows
    public static void setUpDirectory() {
        MessagesMojoIT.directory = Files.createTempDirectory("junit").toFile();
    }

    /**
     * Creates git commits.
     */
    @BeforeAll
    @SneakyThrows
    public static void setUpCommits() {
        final Runtime runtime = Runtime.getRuntime();
        runtime
            .exec(new String[]{"git", "init"}, null, MessagesMojoIT.directory)
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "config", "user.email", "\"user.email\""},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "config", "user.name", "\"user.name\""},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "checkout", "-b", "master"},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        final Path file = Files.createFile(
            MessagesMojoIT.directory.toPath().resolve("test.txt")
        );
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "commit", "-m", "file"},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "checkout", "-b", "branch"},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        Files.write(file, "first".getBytes());
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "commit", "-m", "#123"},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        Files.write(file, "second".getBytes());
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "commit", "-m", "#test"},
                null,
                MessagesMojoIT.directory
            )
            .waitFor();
    }

    /**
     * Deletes {@link MessagesMojoIT#directory}.
     */
    @AfterAll
    @SneakyThrows
    public static void tearDownDirectory() {
        final boolean fail;
        try (
            Stream<Path> files = Files.walk(MessagesMojoIT.directory.toPath())
        ) {
            fail = !files
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .allMatch(File::delete);
        }
        if (fail) {
            throw new IOException(
                String.format("can't delete %s", MessagesMojoIT.directory)
            );
        }
    }

    /**
     * Creates {@link MessagesMojo} instance.
     */
    @BeforeEach
    public void setUpMojo() {
        this.mojo = new MessagesMojo();
        this.mojo.setDirectory(MessagesMojoIT.directory.toString());
        this.mojo.setRemote("master");
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
        this.mojo.setDirectory(temp.resolve("nonexistent").toString());
        Assertions.assertThrows(
            IOException.class,
            this.mojo::execute
        );
    }

}

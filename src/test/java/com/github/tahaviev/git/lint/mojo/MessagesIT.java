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
package com.github.tahaviev.git.lint.mojo;

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
 * {@link Messages} integration test.
 */
public final class MessagesIT {

    /**
     * Git directory.
     */
    private static File DIRECTORY;

    /**
     * {@link Messages} instance to test.
     */
    private Messages messages;

    /**
     * Creates {@link MessagesIT#DIRECTORY}.
     */
    @BeforeAll
    @SneakyThrows
    public static void setUpDirectory() {
        MessagesIT.DIRECTORY = Files.createTempDirectory("junit").toFile();
    }

    /**
     * Creates git commits.
     */
    @BeforeAll
    @SneakyThrows
    public static void setUpCommits() {
        final Runtime runtime = Runtime.getRuntime();
        runtime
            .exec(new String[]{"git", "init"}, null, MessagesIT.DIRECTORY)
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "config", "user.email", "\"user.email\""},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "config", "user.name", "\"user.name\""},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "checkout", "-b", "master"},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        final Path file = Files.createFile(
            MessagesIT.DIRECTORY.toPath().resolve("test.txt")
        );
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(new String[]{"git", "commit", "-m", "file"}, null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(
                new String[]{"git", "checkout", "-b", "branch"},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        Files.write(file, "first".getBytes());
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(new String[]{"git", "commit", "-m", "#123"}, null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        Files.write(file, "second".getBytes());
        runtime
            .exec(
                new String[]{"git", "add", file.getFileName().toString()},
                null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
        runtime
            .exec(new String[]{"git", "commit", "-m", "#test"}, null,
                MessagesIT.DIRECTORY
            )
            .waitFor();
    }

    /**
     * Deletes {@link MessagesIT#DIRECTORY}.
     */
    @AfterAll
    @SneakyThrows
    public static void tearDownDirectory() {
        final boolean fail;
        try (Stream<Path> files = Files.walk(MessagesIT.DIRECTORY.toPath())) {
            fail = !files
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .allMatch(File::delete);
        }
        if (fail) {
            throw new IOException(
                String.format("can't delete %s", MessagesIT.DIRECTORY)
            );
        }
    }

    /**
     * Creates {@link Messages} instance.
     */
    @BeforeEach
    public void setUpMessages() {
        this.messages = new Messages();
        this.messages.setDirectory(MessagesIT.DIRECTORY.toString());
        this.messages.setRemote("master");
    }

    /**
     * Can accept good commits.
     */
    @Test
    public void acceptGoodCommits() {
        this.messages.setPattern("#.+");
        Assertions.assertDoesNotThrow(this.messages::execute);
    }

    /**
     * Can reject commits with wrong messages.
     */
    @Test
    public void rejectWrongCommits() {
        this.messages.setPattern("#(\\d)+");
        try {
            this.messages.execute();
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
        this.messages.setDirectory(temp.resolve("nonexistent").toString());
        Assertions.assertThrows(
            IOException.class,
            this.messages::execute
        );
    }

}

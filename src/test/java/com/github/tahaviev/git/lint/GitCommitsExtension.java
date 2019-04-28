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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Instantiates git repository in temporary directory with provided commit
 * messages.
 */
@RequiredArgsConstructor
public final class GitCommitsExtension
    implements AfterAllCallback, BeforeAllCallback, ParameterResolver {

    /**
     * Marker for git repository directory injection.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Directory {

    }

    /**
     * Commit messages.
     */
    private final String[] messages;

    @Override
    public void afterAll(final ExtensionContext context) throws IOException {
        final File directory = File.class.cast(
            context
                .getStore(ExtensionContext.Namespace.create(this))
                .get("directory")
        );
        final boolean fail;
        try (
            Stream<Path> files = Files.walk(directory.toPath())
        ) {
            fail = !files
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .allMatch(File::delete);
        }
        if (fail) {
            throw new IOException(
                String.format("can't delete %s", directory)
            );
        }
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws IOException {
        final File directory = Files.createTempDirectory("junit").toFile();
        context
            .getStore(ExtensionContext.Namespace.create(this))
            .put("directory", directory);
        new Executions(
            new String[][]{
                {"git", "init"},
                {"git", "config", "user.email", "\"user.email\""},
                {"git", "config", "user.name", "\"user.name\""}
            },
            directory
        )
            .run();
        final Path file = Files.createFile(
            directory.toPath().resolve("test.txt")
        );
        new Executions(
            new String[][]{
                {"git", "add", file.getFileName().toString()},
                {"git", "commit", "-m", "file"},
                {"git", "checkout", "-b", "branch"}
            },
            directory
        )
            .run();
        for (int index = 0; index < this.messages.length; index++) {
            Files.write(file, String.valueOf(index).getBytes());
            new Executions(
                new String[][]{
                    {"git", "add", file.getFileName().toString()},
                    {"git", "commit", "-m", this.messages[index]}
                },
                directory
            )
                .run();
        }
    }

    @Override
    public Object resolveParameter(
        final ParameterContext parameter, final ExtensionContext extension
    ) {
        return File.class.cast(
            extension
                .getStore(ExtensionContext.Namespace.create(this))
                .get("directory")
        );
    }

    @Override
    public boolean supportsParameter(
        final ParameterContext parameter, final ExtensionContext extension
    ) {
        return parameter.isAnnotated(GitCommitsExtension.Directory.class)
            && parameter.getParameter().getType().equals(File.class);
    }

}

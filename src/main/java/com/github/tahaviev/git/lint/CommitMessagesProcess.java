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
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Represents commit messages process.
 */
@RequiredArgsConstructor
public final class CommitMessagesProcess implements Supplier<Process> {

    /**
     * Git repository directory.
     */
    private final File directory;

    /**
     * Parent branch where commit messages start.
     */
    private final String parent;

    @Override
    @SneakyThrows
    public Process get() {
        return Runtime.getRuntime().exec(
            new String[]{
                "git",
                "log",
                "--format=%s",
                "--no-merges",
                String.format("%s..HEAD", this.parent)
            },
            null,
            this.directory
        );
    }

}

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
 *
 */
package com.github.tahaviev.git.lint.mojo;

import com.github.tahaviev.git.lint.LinesFromProcess;
import com.github.tahaviev.git.lint.Mismatches;
import com.github.tahaviev.git.lint.SucceedProcess;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Commit messages lint mojo.
 */
@Mojo(
    name = "messages",
    defaultPhase = LifecyclePhase.VERIFY,
    requiresProject = false
)
public final class Messages extends AbstractMojo {

    /**
     * Git repository directory.
     */
    @Parameter(defaultValue = "${project.basedir}/.git")
    private String directory;

    /**
     * Commit message pattern.
     */
    @Parameter(required = true)
    private String pattern;

    /**
     * Remote branch name.
     */
    @Parameter(defaultValue = "origin/master")
    private String remote;

    @Override
    public void execute() throws MojoFailureException {
        final Collection<String> mismatches = new Mismatches(
            new LinesFromProcess(
                new SucceedProcess(
                    new Messages.Processes(this.directory, this.remote)
                )
            ),
            this.pattern
        )
            .get();
        if (!mismatches.isEmpty()) {
            throw new MojoFailureException(
                String.format(
                    "Commit messages are not matches with %s%n%s",
                    this.pattern,
                    String.join("\n", mismatches)
                )
            );
        }
    }

    /**
     * Represents commit messages process.
     */
    @RequiredArgsConstructor
    private static final class Processes implements Supplier<Process> {

        /**
         * Git repository directory.
         */
        private final String directory;

        /**
         * Remote branch name.
         */
        private final String remote;

        @Override
        @SneakyThrows
        public Process get() {
            return Runtime.getRuntime().exec(
                new String[]{
                    "git",
                    "--git-dir",
                    this.directory,
                    "log",
                    "--abbrev-commit",
                    "--first-parent",
                    "--format=\"%s\"",
                    "--no-merges",
                    String.format("%s..HEAD", this.remote)
                }
            );
        }

    }

}
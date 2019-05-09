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
import java.util.Collection;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Fail the build if there were any commit messages violations.
 */
@Mojo(
    defaultPhase = LifecyclePhase.VERIFY,
    inheritByDefault = false,
    name = "messages",
    requiresProject = false
)
@Setter
public final class MessagesMojo extends AbstractMojo {

    /**
     * Git repository directory.
     */
    @Parameter(defaultValue = "${project.basedir}")
    private File directory;

    /**
     * Parent branch where commit messages validation start.
     */
    @Parameter(defaultValue = "origin/master")
    private String parent;

    /**
     * Commit message pattern.
     */
    @Parameter(required = true)
    private String pattern;

    @Override
    public void execute() throws MojoFailureException {
        final Collection<String> mismatches = new Mismatches(
            new LinesFromProcess(
                new SucceedProcess(
                    new CommitMessagesProcess(this.directory, this.parent)
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

}

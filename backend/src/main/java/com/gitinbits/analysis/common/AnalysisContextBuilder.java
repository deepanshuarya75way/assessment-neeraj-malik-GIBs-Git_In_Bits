package com.gitinbits.analysis.common;

import com.gitinbits.dto.context.GitHubContext;

/**
 * Generic contract for all context builders across Git in Bits analysis layers.
 *
 * @param <C> The analysis context type assembled by this builder.
 */
public interface AnalysisContextBuilder<C> {
    /**
     * Assembles a complete analysis context for the specified target.
     *
     * @param context The GitHub authentication/source context.
     * @param target  The identifier of the target resource (e.g., repository name, branch name, team name).
     * @return The fully populated analysis context object.
     */
    C build(GitHubContext context, String target);
}

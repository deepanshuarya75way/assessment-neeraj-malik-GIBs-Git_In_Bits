package com.gitinbits.analysis.common;

/**
 * Reusable generic interface for all analysis modules in Git in Bits.
 * Every future analyzer inside Git in Bits must implement this interface.
 *
 * @param <C> The analysis context type containing input metadata.
 * @param <R> The analysis result type produced by the analyzer.
 */
public interface Analyzer<C, R> {
    R analyze(C context);
}

import { useQuery } from '@tanstack/react-query';
import { apiClient } from './client';
import type { RepositoryAnalysisReport, RepositoryStabilityResult } from '../types';

/**
 * Hook to fetch complete repository analysis (including health and future analysis modules)
 * independently and in parallel without blocking main repository metadata.
 */
export const useRepositoryAnalysis = (repoName: string) => {
  return useQuery({
    queryKey: ['repositoryAnalysis', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<RepositoryAnalysisReport>(`/api/repos/${repoName}/analysis`);
      return data;
    },
    enabled: !!repoName,
    staleTime: 5 * 60 * 1000,
    retry: 1,
  });
};

/**
 * Hook to fetch live repository stability analysis.
 */
export const useRepositoryStability = (repoName: string) => {
  return useQuery({
    queryKey: ['repositoryStability', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<RepositoryStabilityResult>(`/api/repos/${repoName}/analysis/stability`);
      return data;
    },
    enabled: !!repoName,
    staleTime: 5 * 60 * 1000,
    retry: 1,
  });
};

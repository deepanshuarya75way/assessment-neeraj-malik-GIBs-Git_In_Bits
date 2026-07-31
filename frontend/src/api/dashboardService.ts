import { useQuery, useMutation } from '@tanstack/react-query';
import { apiClient } from './client';

export interface DeveloperActivitySummary {
  id: string;
  commitCount: number;
  latestCommitDate: string;
  latestCommitMessage?: string;
}

export interface OrganizationSummary {
  owner: string;
  timeframe: string;
  summaryText: string;
  generatedAt: string;
}

export const useOrganizationSummary = (owner: string, timeframe: string = '1_day') => {
  return useQuery({
    queryKey: ['orgSummary', owner, timeframe],
    queryFn: async () => {
      try {
        const { data } = await apiClient.get<OrganizationSummary>(`/api/dashboard/summary`, {
          params: { owner, timeframe }
        });
        return data;
      } catch (error) {
        // Return null if not found (e.g. hasn't been generated yet)
        return null;
      }
    },
    enabled: !!owner,
  });
};

export const useGenerateOrgSummary = () => {
  return useMutation({
    mutationFn: async ({ owner, timeframe }: { owner: string; timeframe: string }) => {
      const { data } = await apiClient.post(`/api/dashboard/summary/generate?owner=${owner}&timeframe=${timeframe}`);
      return data;
    }
  });
};

export const useTopDevelopers = (owner: string) => {
  return useQuery({
    queryKey: ['topDevelopers', owner],
    queryFn: async () => {
      const { data } = await apiClient.get<DeveloperActivitySummary[]>(`/api/dashboard/developers`, {
        params: { owner }
      });
      return data;
    },
    enabled: !!owner,
  });
};

export const useDeveloperAiReport = (owner: string, authorName: string, timeframe: string) => {
  return useQuery({
    queryKey: ['devAiReport', owner, authorName, timeframe],
    queryFn: async () => {
      const { data } = await apiClient.get<{report: string}>(`/api/dashboard/developers/${encodeURIComponent(authorName)}/ai-report`, {
        params: { owner, timeframe }
      });
      return data;
    },
    enabled: !!owner && !!authorName,
    staleTime: 1000 * 60 * 60, // Cache for 1 hour
  });
};

export interface DeveloperEvidence {
  authorName: string;
  commitCount: number;
  totalAdditions: number;
  totalDeletions: number;
  prsOpened: number;
  prsMerged: number;
  avgMergeTime: string;
  activePrs: { title: string; openTime: string }[];
  reviewsConducted: number;
  issuesResolved: number;
  workflowFailures: number;
  workflowSuccesses: number;
}

export const useDeveloperEvidence = (owner: string, authorName: string, timeframe: string) => {
  return useQuery({
    queryKey: ['devEvidence', owner, authorName, timeframe],
    queryFn: async () => {
      const { data } = await apiClient.get<DeveloperEvidence>(`/api/dashboard/developers/${encodeURIComponent(authorName)}/evidence`, {
        params: { owner, timeframe }
      });
      return data;
    },
    enabled: !!owner && !!authorName,
  });
};

export interface OrganizationEvidence {
  activeWorkstreams: string[];
  recentlyCompleted: string[];
  needsAttention: string[];
  totalPrsMerged: number;
  totalIssuesClosed: number;
  totalWorkflowFailures: number;
}

export const useOrganizationEvidence = (owner: string, timeframe: string = '1_day') => {
  return useQuery({
    queryKey: ['orgEvidence', owner, timeframe],
    queryFn: async () => {
      const { data } = await apiClient.get<OrganizationEvidence>(`/api/dashboard/organization/evidence`, {
        params: { owner, timeframe }
      });
      return data;
    },
    enabled: !!owner,
  });
};

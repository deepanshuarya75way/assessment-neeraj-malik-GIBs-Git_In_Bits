import { useQuery } from '@tanstack/react-query';
import { apiClient } from './client';
import type { 
  OrgSummary, Repo, Branch, Commit, PullRequest, 
  Review, ReviewComment, Issue, Contributor, Release, Team 
} from '../types';

export const useUserOrgs = () => {
  return useQuery({
    queryKey: ['orgs'],
    queryFn: async () => {
      const { data } = await apiClient.get<OrgSummary[]>('/api/auth/orgs');
      return data;
    },
  });
};

export const useRepos = () => {
  return useQuery({
    queryKey: ['repos'],
    queryFn: async () => {
      const { data } = await apiClient.get<Repo[]>('/api/repos');
      return data;
    },
  });
};

export const useRepoDetails = (repoName: string) => {
  return useQuery({
    queryKey: ['repo', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Repo>(`/api/repos/${repoName}`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useBranches = (repoName: string) => {
  return useQuery({
    queryKey: ['branches', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Branch[]>(`/api/repos/${repoName}/branches`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useCommits = (repoName: string) => {
  return useQuery({
    queryKey: ['commits', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Commit[]>(`/api/repos/${repoName}/commits`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const usePullRequests = (repoName: string) => {
  return useQuery({
    queryKey: ['pulls', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<PullRequest[]>(`/api/repos/${repoName}/pulls`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useReviews = (repoName: string, prNumber: number) => {
  return useQuery({
    queryKey: ['reviews', repoName, prNumber],
    queryFn: async () => {
      const { data } = await apiClient.get<Review[]>(`/api/repos/${repoName}/pulls/${prNumber}/reviews`);
      return data;
    },
    enabled: !!repoName && !!prNumber,
  });
};

export const useReviewComments = (repoName: string, prNumber: number) => {
  return useQuery({
    queryKey: ['comments', repoName, prNumber],
    queryFn: async () => {
      const { data } = await apiClient.get<ReviewComment[]>(`/api/repos/${repoName}/pulls/${prNumber}/comments`);
      return data;
    },
    enabled: !!repoName && !!prNumber,
  });
};

export const useIssues = (repoName: string) => {
  return useQuery({
    queryKey: ['issues', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Issue[]>(`/api/repos/${repoName}/issues`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useContributors = (repoName: string) => {
  return useQuery({
    queryKey: ['contributors', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Contributor[]>(`/api/repos/${repoName}/contributors`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useReleases = (repoName: string) => {
  return useQuery({
    queryKey: ['releases', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<Release[]>(`/api/repos/${repoName}/releases`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useTeams = () => {
  return useQuery({
    queryKey: ['teams'],
    queryFn: async () => {
      const { data } = await apiClient.get<Team[]>('/api/teams');
      return data;
    },
  });
};

export const useCommitComments = (repoName: string) => {
  return useQuery({
    queryKey: ['commitComments', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/commits/comments`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useIssueComments = (repoName: string) => {
  return useQuery({
    queryKey: ['issueComments', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/issues/comments`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useLabels = (repoName: string) => {
  return useQuery({
    queryKey: ['labels', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/labels`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useMilestones = (repoName: string) => {
  return useQuery({
    queryKey: ['milestones', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/milestones`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useWorkflows = (repoName: string) => {
  return useQuery({
    queryKey: ['workflows', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/actions/workflows`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useWorkflowRuns = (repoName: string) => {
  return useQuery({
    queryKey: ['workflowRuns', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/actions/runs`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useDeployments = (repoName: string) => {
  return useQuery({
    queryKey: ['deployments', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/deployments`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useCollaborators = (repoName: string) => {
  return useQuery({
    queryKey: ['collaborators', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/collaborators`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useRulesets = (repoName: string) => {
  return useQuery({
    queryKey: ['rulesets', repoName],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>(`/api/repos/${repoName}/rulesets`);
      return data;
    },
    enabled: !!repoName,
  });
};

export const useCapabilities = () => {
  return useQuery({
    queryKey: ['capabilities'],
    queryFn: async () => {
      const { data } = await apiClient.get<any>('/api/capabilities');
      return data;
    },
  });
};

export const useSystemCoverage = () => {
  return useQuery({
    queryKey: ['systemCoverage'],
    queryFn: async () => {
      const { data } = await apiClient.get<any>('/api/system/coverage');
      return data;
    },
  });
};

export const useMetadataDictionary = () => {
  return useQuery({
    queryKey: ['metadataDictionary'],
    queryFn: async () => {
      const { data } = await apiClient.get<any[]>('/api/metadata-dictionary');
      return data;
    },
  });
};

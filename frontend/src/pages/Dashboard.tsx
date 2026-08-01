import { useState } from 'react';
import { EntityHeader } from '../components/common/EntityHeader';
import { Card } from '../components/ui/Card';
import { useDataSource } from '../context/DataSourceContext';
import { CheckCircle2, Sparkles, RefreshCw, Briefcase, CheckSquare, AlertTriangle } from 'lucide-react';

import { useOrganizationSummary, useGenerateOrgSummary, useOrganizationEvidence, useTopDevelopers } from '../api/dashboardService';
import { Spinner } from '../components/ui/Spinner';
import { AIReportMarkdown } from '../components/ai/AIReportMarkdown';
import { KpiSummaryRow } from '../components/dashboard/KpiSummaryRow';


export function Dashboard() {
  const { sourceValue } = useDataSource();
  const [timeframe, setTimeframe] = useState('30_days');
  
  const { data: orgSummary, isLoading: summaryLoading, refetch: refetchSummary } = useOrganizationSummary(sourceValue || '', timeframe);
  const { data: orgEvidence, isLoading: evidenceLoading } = useOrganizationEvidence(sourceValue || '', timeframe);
  const { data: developers } = useTopDevelopers(sourceValue || '');
  const { mutate: generateSummary, isPending: generating } = useGenerateOrgSummary();

  const handleGenerate = () => {
    generateSummary({ owner: sourceValue || '', timeframe }, {
      onSuccess: () => refetchSummary()
    });
  };


  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-slate-700/50 pb-6">
        <div>
          <div className="flex items-center space-x-2 mb-2">
            <Briefcase className="w-4 h-4 text-slate-400" />
            <span className="text-xs font-bold text-slate-400 tracking-widest uppercase">Organization Overview</span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold text-white tracking-tight">
            {sourceValue}
          </h1>
        </div>
        <div className="flex space-x-2">
          <select 
            className="bg-slate-800 border border-slate-700 text-sm rounded px-3 py-1.5 text-slate-300 focus:outline-none focus:border-blue-500 shadow-inner"
            value={timeframe}
            onChange={(e) => setTimeframe(e.target.value)}
          >
            <option value="1_day">Yesterday</option>
            <option value="3_days">Last 3 Days</option>
            <option value="7_days">Last 7 Days</option>
            <option value="10_days">Last 10 Days</option>
            <option value="30_days">Last 30 Days</option>
          </select>
        </div>
      </div>

      {/* KPI Hero Section */}
      <section>
        <KpiSummaryRow 
          totalCommits={orgEvidence?.totalCommits ?? 0}
          totalPrsMerged={orgEvidence?.totalPrsMerged ?? 0}
          totalIssuesClosed={orgEvidence?.totalIssuesClosed ?? 0}
          totalWorkflowFailures={orgEvidence?.totalWorkflowFailures ?? 0}
          activePrCount={orgEvidence?.activePrCount ?? 0}
          activeDeveloperCount={developers?.length ?? 0}
          isLoading={evidenceLoading}
        />
      </section>


      {/* Level 1: AI Organization Dashboard */}
      <section>
        <Card className="bg-gradient-to-br from-[#0F172A] via-[#1E293B] to-[#0F172A] border-blue-900/30 overflow-hidden relative shadow-xl">
          <div className="absolute top-0 right-0 p-8 opacity-5">
            <Sparkles className="w-48 h-48" />
          </div>
          <div className="p-8 relative z-10">
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4">
              <div className="flex items-center space-x-2">
                <Sparkles className="w-5 h-5 text-blue-400" />
                <h2 className="text-lg font-bold text-white tracking-tight">AI Executive Briefing</h2>
              </div>
              <button 
                onClick={handleGenerate}
                disabled={generating}
                className="bg-blue-600/20 hover:bg-blue-600/40 text-blue-400 border border-blue-500/30 px-3 py-1.5 rounded text-sm flex items-center transition-colors disabled:opacity-50"
              >
                {generating ? <Spinner className="w-4 h-4 mr-2" /> : <RefreshCw className="w-4 h-4 mr-2" />}
                Generate Briefing
              </button>
            </div>

            <div className="bg-[#1E293B]/60 p-6 rounded-xl border border-slate-700/50 min-h-[120px] flex items-start mb-6 shadow-inner backdrop-blur-sm transition-all duration-500">
              {summaryLoading ? (
                <div className="w-full text-center text-blue-400 flex flex-col items-center justify-center py-8 animate-pulse">
                  <Spinner className="w-8 h-8 mb-4" /> 
                  <span className="text-sm font-medium tracking-wide">Analyzing organization data...</span>
                </div>
              ) : orgSummary ? (
                <div className="prose prose-invert prose-blue max-w-none w-full text-slate-300 text-sm leading-relaxed font-light prose-p:mb-3 prose-h4:text-base prose-h4:font-semibold prose-h4:text-blue-300 prose-h4:mt-4 prose-h4:mb-2 prose-strong:font-bold prose-strong:text-white prose-li:my-0.5 prose-li:marker:text-blue-500 animate-fade-in">
                  <AIReportMarkdown content={orgSummary.summaryText} />
                </div>
              ) : (
                <p className="text-slate-400 italic text-center w-full">
                  No briefing generated yet. Click "Generate New" to run the nightly AI summary task immediately.
                </p>
              )}
            </div>

            {/* Deterministic Organization Evidence */}
            {evidenceLoading ? (
               <div className="flex justify-center py-4"><Spinner className="w-6 h-6 text-blue-500" /></div>
            ) : orgEvidence ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                
                {/* Active Work */}
                <div className="bg-slate-900/40 border border-slate-700 rounded-lg p-5">
                  <h3 className="text-sm font-semibold text-blue-400 uppercase tracking-wider mb-4 flex items-center">
                    <Briefcase className="w-4 h-4 mr-2" /> Active Work
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.activeWorkstreams.length > 0 ? orgEvidence.activeWorkstreams.map((ws, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <span className="w-1.5 h-1.5 rounded-full bg-blue-500 mt-1.5 mr-2 flex-shrink-0" />
                        <span className="line-clamp-2">{ws}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">No active workstreams detected.</p>
                    )}
                  </ul>
                </div>

                {/* Recently Completed */}
                <div className="bg-slate-900/40 border border-slate-700 rounded-lg p-5">
                  <h3 className="text-sm font-semibold text-emerald-400 uppercase tracking-wider mb-4 flex items-center">
                    <CheckSquare className="w-4 h-4 mr-2" /> Recently Completed
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.recentlyCompleted.length > 0 ? orgEvidence.recentlyCompleted.map((rc, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <CheckCircle2 className="w-4 h-4 text-emerald-500 mr-2 flex-shrink-0 mt-0.5" />
                        <span className="line-clamp-2">{rc}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">No PRs merged in this period.</p>
                    )}
                  </ul>
                  <div className="mt-4 pt-4 border-t border-slate-800 flex justify-between text-xs text-slate-400">
                    <span>Total PRs Merged: <strong className="text-slate-200">{orgEvidence.totalPrsMerged}</strong></span>
                    <span>Issues Closed: <strong className="text-slate-200">{orgEvidence.totalIssuesClosed}</strong></span>
                  </div>
                </div>

                {/* Needs Attention */}
                <div className="bg-slate-900/40 border border-rose-900/50 rounded-lg p-5 relative overflow-hidden">
                  <div className="absolute top-0 right-0 w-16 h-16 bg-rose-500/5 rounded-bl-full pointer-events-none" />
                  <h3 className="text-sm font-semibold text-rose-400 uppercase tracking-wider mb-4 flex items-center">
                    <AlertTriangle className="w-4 h-4 mr-2" /> Needs Attention
                  </h3>
                  <ul className="space-y-3">
                    {orgEvidence.needsAttention.length > 0 ? orgEvidence.needsAttention.map((na, i) => (
                      <li key={i} className="text-sm text-slate-300 flex items-start">
                        <span className="text-rose-500 mr-2 font-bold flex-shrink-0">⚠</span>
                        <span className="line-clamp-2">{na}</span>
                      </li>
                    )) : (
                      <p className="text-slate-500 text-sm italic">Looking good! No major issues detected.</p>
                    )}
                  </ul>
                  <div className="mt-4 pt-4 border-t border-slate-800 text-xs text-slate-400">
                    Total CI/CD Failures: <strong className="text-rose-400">{orgEvidence.totalWorkflowFailures}</strong>
                  </div>
                </div>

              </div>
            ) : null}

          </div>
        </Card>
      </section>



    </div>
  );
}

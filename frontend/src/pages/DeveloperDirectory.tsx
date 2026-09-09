import { useState } from 'react';
import { useDataSource } from '../context/DataSourceContext';
import { useTopDevelopers, useDeveloperAiReport, useDeveloperEvidence } from '../api/dashboardService';
import { EntityHeader } from '../components/common/EntityHeader';
import { Spinner } from '../components/ui/Spinner';
import { Card } from '../components/ui/Card';
import { Sparkles, Activity, CheckCircle2, GitMerge, AlertTriangle, Users, GitCommit, Clock } from 'lucide-react';
import { AIReportMarkdown } from '../components/ai/AIReportMarkdown';
export function DeveloperDirectory() {
  const { sourceValue } = useDataSource();
  const { data: developers, isLoading: devsLoading } = useTopDevelopers(sourceValue || '');
  
  const [selectedDev, setSelectedDev] = useState<string | null>(null);
  const [isPrDropdownOpen, setIsPrDropdownOpen] = useState(false);
  const [aiTimeframe, setAiTimeframe] = useState<string>('30_days');

  const { data: evidence, isLoading: evidenceLoading } = useDeveloperEvidence(
    sourceValue || '', 
    selectedDev || '',
    'lifetime'
  );

  const { data: aiReport, isLoading: reportLoading } = useDeveloperAiReport(
    sourceValue || '', 
    selectedDev || '',
    aiTimeframe
  );

  return (
    <div className="space-y-6">
      <EntityHeader
        title="Developer Directory"
        description="Organization-wide developer activity and AI efficiency insights."
      />

      <div className="flex flex-col lg:flex-row gap-6 h-[calc(100vh-200px)]">
        {/* Left Sidebar: Developer List */}
        <div className="w-full lg:w-1/3 flex flex-col gap-3 overflow-y-auto pr-2 custom-scrollbar">
          <h2 className="text-sm font-semibold text-text-secondary uppercase tracking-wider mb-2 flex items-center">
            <Users className="w-4 h-4 mr-2" />
            Top Contributors
          </h2>
          
          {devsLoading ? (
            <div className="flex justify-center py-10"><Spinner className="w-8 h-8" /></div>
          ) : developers?.map(dev => (
            <div 
              key={dev.id}
              onClick={() => setSelectedDev(dev.id)}
              className={`p-4 rounded-lg cursor-pointer transition-all border ${
                selectedDev === dev.id 
                  ? 'bg-blue-900/30 border-blue-500' 
                  : 'bg-surface border-border hover:border-slate-500 hover:bg-surface/80'
              }`}
            >
              <div className="flex items-center space-x-4">
                <img 
                  src={`https://github.com/${dev.id}.png`} 
                  alt={dev.id} 
                  className="w-12 h-12 rounded-full border border-slate-600 bg-slate-800"
                  onError={(e) => {
                    // Fallback if avatar fails
                    (e.target as HTMLImageElement).src = `https://ui-avatars.com/api/?name=${dev.id}&background=0D8ABC&color=fff`;
                  }}
                />
                <div className="flex-1 min-w-0">
                  <h3 className="text-md font-medium text-text-primary truncate">{dev.id}</h3>
                  <div className="flex items-center text-xs text-text-muted mt-1 space-x-3">
                    <span className="flex items-center">
                      <GitCommit className="w-3 h-3 mr-1" />
                      {dev.commitCount} commits
                    </span>
                    <span className="flex items-center">
                      <Clock className="w-3 h-3 mr-1" />
                      {new Date(dev.latestCommitDate).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
          {developers?.length === 0 && (
            <div className="text-slate-400 text-sm text-center py-4">No developer activity found.</div>
          )}
        </div>

        {/* Right Area: AI Efficiency Profile */}
        <div className="w-full lg:w-2/3 flex flex-col">
          {selectedDev ? (
            <Card className="flex-1 bg-surface border-border overflow-hidden flex flex-col">
                  {(() => {
                    const devInfo = developers?.find(d => d.id === selectedDev);
                    return (
                      <div className="p-6 border-b border-border bg-slate-800/30">
                        <div className="flex items-center space-x-4">
                          <img 
                            src={`https://github.com/${selectedDev}.png`} 
                            alt={selectedDev} 
                            className="w-16 h-16 rounded-full border-2 border-blue-500 bg-slate-800"
                            onError={(e) => {
                              (e.target as HTMLImageElement).src = `https://ui-avatars.com/api/?name=${selectedDev}&background=0D8ABC&color=fff`;
                            }}
                          />
                          <div>
                            <h2 className="text-2xl font-bold text-text-primary">{selectedDev}</h2>
                            <p className="text-slate-400">Developer Profile & Efficiency Analysis</p>
                          </div>
                        </div>
                      </div>
                    );
                  })()}

              <div className="flex-1 overflow-y-auto p-6 space-y-6">
                
                {/* Evidence Stats Dashboard */}
                {evidenceLoading ? (
                   <div className="flex justify-center py-4"><Spinner className="w-6 h-6 text-blue-500" /></div>
                ) : evidence ? (
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                    <div className="bg-slate-800/50 p-4 rounded-lg border border-slate-700 flex items-center justify-between">
                      <div>
                        <p className="text-xs text-slate-400 font-semibold uppercase tracking-wider mb-1">Pull Requests</p>
                        <p className="text-2xl font-bold text-blue-400">{evidence.prsMerged} <span className="text-sm font-normal text-slate-500">/ {evidence.prsOpened}</span></p>
                      </div>
                      <GitMerge className="w-8 h-8 text-blue-500/50" />
                    </div>
                    <div className="bg-slate-800/50 p-4 rounded-lg border border-slate-700 flex items-center justify-between">
                      <div>
                        <p className="text-xs text-slate-400 font-semibold uppercase tracking-wider mb-1">Merge Time</p>
                        <p className="text-lg font-bold text-emerald-400">{evidence.avgMergeTime}</p>
                      </div>
                      <Clock className="w-8 h-8 text-emerald-500/50" />
                    </div>
                    <div className="bg-slate-800/50 p-4 rounded-lg border border-slate-700 flex items-center justify-between">
                      <div>
                        <p className="text-xs text-slate-400 font-semibold uppercase tracking-wider mb-1">CI/CD Health</p>
                        <p className="text-lg font-bold text-indigo-400">{evidence.workflowSuccesses} <span className="text-sm font-normal text-slate-500">pass</span> / {evidence.workflowFailures} <span className="text-sm font-normal text-slate-500">fail</span></p>
                      </div>
                      <Activity className="w-8 h-8 text-indigo-500/50" />
                    </div>
                    <div className="relative">
                      <div 
                        className={`bg-slate-800/50 p-4 rounded-lg border flex items-center justify-between transition-colors ${evidence.activePrs.length > 1 ? 'cursor-pointer hover:bg-slate-800 border-slate-600' : 'border-slate-700'}`}
                        onClick={() => {
                          if (evidence.activePrs.length > 1) {
                            setIsPrDropdownOpen(!isPrDropdownOpen);
                          }
                        }}
                      >
                        <div>
                          <p className="text-xs text-slate-400 font-semibold uppercase tracking-wider mb-1">Opened PRs</p>
                          <p className="text-lg font-bold text-amber-400">{evidence.activePrs.length} <span className="text-sm font-normal text-slate-500">active</span></p>
                          {evidence.activePrs.length === 1 && (
                            <p className="text-xs text-slate-500 mt-1 truncate max-w-[100px]" title={evidence.activePrs[0].title}>
                              {evidence.activePrs[0].openTime} - {evidence.activePrs[0].title}
                            </p>
                          )}
                        </div>
                        <AlertTriangle className="w-8 h-8 text-amber-500/50" />
                      </div>
                      
                      {isPrDropdownOpen && evidence.activePrs.length > 1 && (
                        <div className="absolute top-full left-0 mt-2 w-64 bg-slate-800/95 backdrop-blur border border-slate-600 rounded-lg shadow-xl z-50 overflow-hidden">
                          {evidence.activePrs.map((pr, idx) => (
                            <div key={idx} className="p-3 border-b border-slate-700/50 last:border-0 hover:bg-slate-700/50 transition-colors">
                              <p className="text-xs text-amber-400 font-bold mb-1">{pr.openTime} open</p>
                              <p className="text-sm text-slate-300 truncate" title={pr.title}>{pr.title}</p>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                ) : null}

                {/* AI Coaching Brief */}
                <div className="bg-blue-900/10 rounded-xl border border-blue-900/50 p-6 flex flex-col h-full">
                  <div className="flex-1">
                    <div className="flex items-center justify-between mb-4 relative z-10">
                      <div className="flex items-center space-x-2">
                        <Sparkles className="w-5 h-5 text-blue-400" />
                        <h3 className="text-sm font-bold text-slate-300 uppercase tracking-widest">AI Coaching & Efficiency Brief</h3>
                      </div>
                      <select
                        value={aiTimeframe}
                        onChange={(e) => setAiTimeframe(e.target.value)}
                        className="bg-slate-800 border border-slate-700 text-slate-200 text-xs rounded-md px-2 py-1 outline-none focus:border-blue-500 cursor-pointer"
                      >
                        <option value="1_day" className="bg-slate-800 text-slate-200">Yesterday</option>
                        <option value="3_days" className="bg-slate-800 text-slate-200">Last 3 Days</option>
                        <option value="7_days" className="bg-slate-800 text-slate-200">Last 7 Days</option>
                        <option value="10_days" className="bg-slate-800 text-slate-200">Last 10 Days</option>
                        <option value="30_days" className="bg-slate-800 text-slate-200">Last 30 Days</option>
                        <option value="lifetime" className="bg-slate-800 text-slate-200">Lifetime</option>
                      </select>
                    </div>
                    
                    {reportLoading ? (
                      <div className="flex flex-col items-center justify-center py-8 space-y-4">
                        <Spinner className="w-8 h-8 text-blue-500" />
                        <p className="text-slate-400 animate-pulse">Analyzing cross-repo evidence...</p>
                      </div>
                    ) : aiReport ? (
                      <div className="prose prose-invert prose-blue max-w-none text-slate-300 text-sm leading-relaxed font-light prose-p:mb-3 prose-h4:text-base prose-h4:font-semibold prose-h4:text-blue-300 prose-h4:mt-4 prose-h4:mb-2 prose-strong:font-bold prose-strong:text-white prose-li:my-0.5 prose-li:marker:text-blue-500">
                        <AIReportMarkdown content={aiReport.report} />
                      </div>
                    ) : (
                      <div className="flex items-center space-x-2 text-rose-400">
                        <AlertTriangle className="w-5 h-5" />
                        <p>Failed to generate AI report.</p>
                      </div>
                    )}
                  </div>
                  
                  {/* Lifetime Activity Summary (Historical Stats) */}
                  {(() => {
                    const devInfo = developers?.find(d => d.id === selectedDev);
                    if (!devInfo) return null;
                    return (
                      <div className="mt-6 pt-4 border-t border-blue-900/30 flex flex-col gap-3 text-xs text-slate-500">
                        <div className="flex items-center justify-between">
                          <div className="flex items-center space-x-2">
                            <GitCommit className="w-4 h-4 text-slate-600" />
                            <span>Lifetime record: <strong className="text-slate-400">{devInfo.commitCount}</strong> total commits</span>
                          </div>
                          <div className="flex items-center space-x-2">
                            <Clock className="w-4 h-4 text-slate-600" />
                            <span>Last recorded activity: <strong className="text-slate-400">{new Date(devInfo.latestCommitDate).toLocaleDateString()}</strong></span>
                          </div>
                        </div>
                        {devInfo.latestCommitMessage && (
                          <div className="bg-slate-800/40 rounded p-2 italic text-slate-400 truncate">
                            "{devInfo.latestCommitMessage}"
                          </div>
                        )}
                      </div>
                    );
                  })()}
                </div>
              </div>
            </Card>
          ) : (
            <div className="flex-1 border-2 border-dashed border-border rounded-lg flex flex-col items-center justify-center text-slate-500 p-8 text-center">
              <Users className="w-16 h-16 mb-4 opacity-50" />
              <h3 className="text-xl font-medium text-slate-300 mb-2">Select a Developer</h3>
              <p>Click on any developer in the list to generate a deep-dive AI efficiency report across all their repositories.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

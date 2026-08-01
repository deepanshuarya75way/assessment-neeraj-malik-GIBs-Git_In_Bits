import React from 'react';
import ReactMarkdown from 'react-markdown';
import { AlertTriangle, BarChart3, CheckCircle2, Lightbulb, Activity } from 'lucide-react';

interface AIReportMarkdownProps {
  content: string;
  remarkPlugins?: any[];
}

export function AIReportMarkdown({ content, remarkPlugins }: AIReportMarkdownProps) {
  const renderHeading = (children: any, props: any, Level: 'h2' | 'h3' | 'h4') => {
    let text = '';
    if (Array.isArray(children)) {
      text = children.join('');
    } else {
      text = String(children);
    }
    
    let Icon: React.ElementType | null = null;
    let chipClass = '';
    
    if (text.includes('🚨')) {
      Icon = Activity;
      chipClass = 'bg-blue-500/10 text-blue-400';
      text = text.replace('🚨', '').trim();
    } else if (text.includes('🟠')) {
      Icon = AlertTriangle;
      chipClass = 'bg-amber-500/10 text-amber-400';
      text = text.replace('🟠', '').trim();
    } else if (text.includes('🟢')) {
      Icon = CheckCircle2;
      chipClass = 'bg-emerald-500/10 text-emerald-400';
      text = text.replace('🟢', '').trim();
    } else if (text.includes('📊')) {
      Icon = BarChart3;
      chipClass = 'bg-blue-500/10 text-blue-400';
      text = text.replace('📊', '').trim();
    } else if (text.includes('💡')) {
      Icon = Lightbulb;
      chipClass = 'bg-indigo-500/10 text-indigo-400';
      text = text.replace('💡', '').trim();
    }

    if (Icon) {
      return (
        <div className="flex items-center gap-3 mt-8 mb-4 border-b border-slate-700/50 pb-3 not-prose">
          <div className={`flex items-center justify-center p-2 rounded-lg ${chipClass}`}>
            <Icon className="w-5 h-5" />
          </div>
          <Level className="text-xl font-bold text-white m-0 p-0 border-0">{text}</Level>
        </div>
      );
    }

    return <Level {...props}>{children}</Level>;
  };

  return (
    <ReactMarkdown
      remarkPlugins={remarkPlugins}
      components={{
        h2: ({ node, children, ...props }) => renderHeading(children, props, 'h2'),
        h3: ({ node, children, ...props }) => renderHeading(children, props, 'h3'),
        h4: ({ node, children, ...props }) => renderHeading(children, props, 'h4')
      }}
    >
      {content}
    </ReactMarkdown>
  );
}

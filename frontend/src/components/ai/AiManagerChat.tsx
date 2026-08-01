import React, { useState, useRef, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { AIReportMarkdown } from './AIReportMarkdown';
import remarkGfm from 'remark-gfm';
import { X, Send, Loader2 } from 'lucide-react';

interface Message {
  role: 'user' | 'ai';
  content: string;
}

export function AiManagerChat() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId] = useState(() => crypto.randomUUID());
  
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input.trim();
    setInput('');
    setMessages((prev) => [...prev, { role: 'user', content: userMessage }]);
    setIsLoading(true);

    try {
      const response = await apiClient.post('/api/ai/chat', {
        conversationId,
        message: userMessage,
      });
      setMessages((prev) => [
        ...prev,
        { role: 'ai', content: response.data.response },
      ]);
    } catch (error) {
      console.error('AI Chat Error:', error);
      setMessages((prev) => [
        ...prev,
        { role: 'ai', content: '**Error:** Failed to connect to AI Manager. Is the backend running?' },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      {/* Trigger Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`fixed bottom-6 right-6 p-4 rounded-full shadow-[0_0_30px_rgba(37,99,235,0.4)] z-50 flex items-center justify-center group transition-all duration-300 ${
          isOpen ? 'bg-slate-800 text-gray-400 hover:text-white scale-90 opacity-0 pointer-events-none' : 'bg-gradient-to-br from-blue-600 to-indigo-600 text-white hover:shadow-[0_0_40px_rgba(37,99,235,0.6)] hover:scale-105'
        }`}
        title="Talk to your AI Manager"
      >
        <img src="/nerd.png" alt="Nerd AI" className="w-8 h-8 group-hover:scale-110 transition-transform drop-shadow-lg" />
      </button>

      {/* Chat Window */}
      {isOpen && (
        <div 
          className="fixed bottom-6 right-6 w-[28rem] h-[38rem] bg-[#0b1121]/90 backdrop-blur-xl border border-white/10 rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] flex flex-col z-50 overflow-hidden text-gray-200 origin-bottom-right transition-all duration-300"
          style={{ animation: 'var(--animate-fade-in, none) 0.3s ease-out' }}
        >
          {/* Header */}
          <div className="flex items-center justify-between p-5 bg-gradient-to-r from-blue-900/40 to-indigo-900/40 border-b border-white/5 relative overflow-hidden">
            <div className="absolute inset-0 bg-white/5 opacity-10 mix-blend-overlay"></div>
            <div className="flex items-center gap-3 relative z-10">
              <div className="w-10 h-10 rounded-full bg-blue-500/20 flex items-center justify-center border border-blue-400/30 shadow-inner">
                <img src="/nerd.png" alt="Nerd" className="w-6 h-6" />
              </div>
              <div>
                <h3 className="font-semibold text-lg text-white leading-tight tracking-wide">AI Engineering Manager</h3>
                <p className="text-xs text-blue-300/80 font-medium tracking-wider uppercase mt-0.5">Online & Ready</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="text-gray-400 hover:text-white hover:bg-white/10 p-2 rounded-full transition-colors relative z-10"
            >
              <X size={20} />
            </button>
          </div>

          {/* Messages Area */}
          <div className="flex-1 overflow-y-auto p-5 space-y-6 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">
            {messages.length === 0 && (
              <div className="flex flex-col items-center justify-center h-full text-center px-4" style={{ animation: 'var(--animate-fade-in, none) 0.7s ease-out' }}>
                <div className="w-20 h-20 bg-blue-500/10 rounded-full flex items-center justify-center mb-6 shadow-[0_0_30px_rgba(59,130,246,0.15)] relative">
                  <div className="absolute inset-0 bg-blue-400/20 rounded-full animate-ping opacity-20"></div>
                  <img src="/nerd.png" alt="Nerd AI" className="w-12 h-12 drop-shadow-xl" />
                </div>
                <h4 className="text-xl font-medium text-white mb-2">How can I help you?</h4>
                <p className="text-sm text-gray-400 max-w-[200px] leading-relaxed">
                  Ask me about repository health, engineering risks, or team trends.
                </p>
              </div>
            )}
            
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`flex gap-3 ${
                  msg.role === 'user' ? 'justify-end' : 'justify-start'
                }`}
                style={{ animation: 'var(--animate-fade-in, none) 0.3s ease-out' }}
              >
                {msg.role === 'ai' && (
                  <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500/20 to-indigo-500/20 border border-blue-400/20 flex items-center justify-center shrink-0 mt-1 shadow-sm">
                    <img src="/nerd.png" alt="Nerd" className="w-4 h-4" />
                  </div>
                )}
                <div
                  className={`px-4 py-3 rounded-2xl max-w-[85%] text-sm shadow-md flex-col overflow-hidden ${
                    msg.role === 'user'
                      ? 'bg-gradient-to-br from-blue-600 to-indigo-600 text-white rounded-br-sm'
                      : 'bg-[#151b2b] text-gray-300 rounded-bl-sm border border-white/5'
                  }`}
                >
                  {msg.role === 'user' ? (
                    msg.content
                  ) : (
                    <div className="prose prose-invert prose-sm max-w-none prose-p:leading-relaxed prose-pre:bg-[#0a0f18] prose-pre:border prose-pre:border-white/10 prose-pre:shadow-inner prose-a:text-blue-400 break-words overflow-x-auto whitespace-pre-wrap">
                      <AIReportMarkdown content={msg.content} remarkPlugins={[remarkGfm]} />
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex gap-3 justify-start" style={{ animation: 'var(--animate-fade-in, none) 0.3s ease-out' }}>
                <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500/20 to-indigo-500/20 border border-blue-400/20 flex items-center justify-center shrink-0 mt-1 shadow-sm">
                  <img src="/nerd.png" alt="Nerd" className="w-4 h-4" />
                </div>
                <div className="px-4 py-3.5 rounded-2xl bg-[#151b2b] rounded-bl-sm border border-white/5 flex items-center gap-3 shadow-md">
                  <div className="flex gap-1">
                    <div className="w-1.5 h-1.5 bg-blue-500 rounded-full animate-bounce [animation-delay:-0.3s]"></div>
                    <div className="w-1.5 h-1.5 bg-blue-400 rounded-full animate-bounce [animation-delay:-0.15s]"></div>
                    <div className="w-1.5 h-1.5 bg-blue-300 rounded-full animate-bounce"></div>
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} className="h-px" />
          </div>

          {/* Input Area */}
          <div className="p-4 bg-[#0d1323] border-t border-white/5">
            <form onSubmit={handleSend} className="relative flex items-center">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Message AI Manager..."
                className="w-full bg-[#151b2b] border border-white/10 rounded-full pl-5 pr-12 py-3.5 text-sm focus:outline-none focus:border-blue-500/50 focus:ring-1 focus:ring-blue-500/50 transition-all text-white placeholder-gray-500 shadow-inner"
                disabled={isLoading}
              />
              <button
                type="submit"
                disabled={!input.trim() || isLoading}
                className="absolute right-2 p-2 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-full text-white hover:scale-105 hover:shadow-[0_0_15px_rgba(37,99,235,0.5)] disabled:opacity-50 disabled:hover:scale-100 disabled:cursor-not-allowed transition-all"
              >
                <Send size={16} className="ml-0.5" />
              </button>
            </form>
            <div className="text-center mt-2">
              <span className="text-[10px] text-gray-500 uppercase tracking-widest font-semibold">AI Engineering Insights</span>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

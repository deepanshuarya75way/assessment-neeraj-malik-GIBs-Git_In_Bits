import React, { useState, useRef, useEffect } from 'react';
import { apiClient } from '../../api/client';
import ReactMarkdown from 'react-markdown';
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
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="fixed bottom-6 right-6 p-4 bg-blue-600 text-white rounded-full shadow-[0_0_20px_rgba(37,99,235,0.4)] hover:bg-blue-500 transition-all z-50 flex items-center justify-center group"
          title="Talk to your AI Manager"
        >
          <img src="/nerd.png" alt="Nerd AI" className="w-8 h-8 group-hover:scale-110 transition-transform drop-shadow-md" />
        </button>
      )}

      {isOpen && (
        <div className="fixed bottom-6 right-6 w-96 h-[32rem] bg-[#1a1f35] border border-gray-700 rounded-2xl shadow-2xl flex flex-col z-50 overflow-hidden text-gray-200">
          {/* Header */}
          <div className="flex items-center justify-between p-4 border-b border-gray-700 bg-[#141829]">
            <div className="flex items-center gap-2">
              <img src="/nerd.png" alt="Nerd" className="w-6 h-6" />
              <h3 className="font-semibold text-lg">AI Engineering Manager</h3>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="text-gray-400 hover:text-white transition-colors"
            >
              <X size={20} />
            </button>
          </div>

          {/* Messages */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4 scrollbar-thin scrollbar-thumb-gray-600 scrollbar-track-transparent">
            {messages.length === 0 && (
              <div className="text-center text-gray-500 mt-10">
                <img src="/nerd.png" alt="Nerd AI" className="w-16 h-16 mx-auto mb-4 opacity-75 drop-shadow-lg" />
                <p>Hello! I am your AI Engineering Manager.</p>
                <p className="text-sm mt-2">Ask me about repository health, risks, trends, or what your team should prioritize.</p>
              </div>
            )}
            
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`flex gap-3 ${
                  msg.role === 'user' ? 'justify-end' : 'justify-start'
                }`}
              >
                {msg.role === 'ai' && (
                  <div className="w-8 h-8 rounded-full bg-blue-900/50 flex items-center justify-center shrink-0">
                    <img src="/nerd.png" alt="Nerd" className="w-5 h-5" />
                  </div>
                )}
                <div
                  className={`px-4 py-2 rounded-2xl max-w-[85%] text-sm shadow-sm ${
                    msg.role === 'user'
                      ? 'bg-blue-600 text-white rounded-br-none'
                      : 'bg-gray-800 text-gray-200 rounded-bl-none border border-gray-700/50'
                  }`}
                >
                  {msg.role === 'user' ? (
                    msg.content
                  ) : (
                    <div className="prose prose-invert prose-sm max-w-none prose-p:leading-relaxed prose-pre:bg-gray-900 prose-pre:border prose-pre:border-gray-700">
                      <ReactMarkdown remarkPlugins={[remarkGfm]}>
                        {msg.content}
                      </ReactMarkdown>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex gap-3 justify-start">
                <div className="w-8 h-8 rounded-full bg-blue-900/50 flex items-center justify-center shrink-0">
                  <img src="/nerd.png" alt="Nerd" className="w-5 h-5" />
                </div>
                <div className="px-4 py-3 rounded-2xl bg-gray-800 rounded-bl-none flex items-center gap-2 border border-gray-700/50">
                  <Loader2 size={16} className="animate-spin text-blue-400" />
                  <span className="text-sm text-gray-400">Analyzing metrics...</span>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input */}
          <div className="p-3 bg-[#141829] border-t border-gray-700">
            <form onSubmit={handleSend} className="flex gap-2">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask about your repositories..."
                className="flex-1 bg-gray-900 border border-gray-700 rounded-full px-4 py-2 text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all text-white placeholder-gray-500"
                disabled={isLoading}
              />
              <button
                type="submit"
                disabled={!input.trim() || isLoading}
                className="p-2.5 bg-blue-600 rounded-full text-white hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-md"
              >
                <Send size={18} />
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
}

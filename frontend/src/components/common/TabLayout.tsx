import React, { useState } from 'react';
import { Tabs } from '../ui/Tabs';

interface Tab {
  id: string;
  label: string;
  icon?: React.ReactNode;
  content: React.ReactNode;
}

interface TabLayoutProps {
  tabs: Tab[];
  defaultTab?: string;
}

export function TabLayout({ tabs, defaultTab }: TabLayoutProps) {
  const [activeTab, setActiveTab] = useState(defaultTab || tabs[0]?.id);

  const currentTab = tabs.find((t) => t.id === activeTab) || tabs[0];

  return (
    <div className="flex flex-col space-y-6">
      <Tabs
        tabs={tabs.map(({ id, label, icon }) => ({ id, label, icon }))}
        activeTab={activeTab}
        onChange={setActiveTab}
      />
      <div className="min-h-[400px]">
        {currentTab?.content}
      </div>
    </div>
  );
}

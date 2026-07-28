import { FileJson } from 'lucide-react';
import { useState } from 'react';
import { Button } from '../ui/Button';
import { Modal } from '../ui/Modal';

interface RawJsonViewerProps {
  data: any;
  title?: string;
}

export function RawJsonViewer({ data, title = "Raw JSON Data" }: RawJsonViewerProps) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <Button variant="outline" size="sm" onClick={() => setIsOpen(true)}>
        <FileJson className="mr-2 h-4 w-4" />
        View Raw JSON
      </Button>

      <Modal isOpen={isOpen} onClose={() => setIsOpen(false)} title={title}>
        <div className="bg-[#1e1e1e] p-4 rounded-md overflow-x-auto">
          <pre className="text-[#d4d4d4] text-sm font-mono whitespace-pre-wrap break-words">
            {JSON.stringify(data, null, 2)}
          </pre>
        </div>
      </Modal>
    </>
  );
}

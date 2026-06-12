import type { ReactNode } from 'react';
import { Button } from '@/components/common/Button';

interface ModalProps {
  title: string;
  open: boolean;
  onClose: () => void;
  children: ReactNode;
  footer?: ReactNode;
}

export function Modal({ title, open, onClose, children, footer }: ModalProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="modal-overlay" onClick={onClose} role="presentation">
      <div
        className="modal-panel"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
      >
        <h2 id="modal-title">{title}</h2>
        {children}
        {footer ?? (
          <div className="btn-row">
            <Button type="button" variant="ghost" onClick={onClose}>
              Отмена
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

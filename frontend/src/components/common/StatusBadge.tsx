import { statusClass } from '@/constants/enums';

interface StatusBadgeProps {
  label: string;
  status: string;
}

export function StatusBadge({ label, status }: StatusBadgeProps) {
  return <span className={`status-badge ${statusClass(status)}`}>{label}</span>;
}

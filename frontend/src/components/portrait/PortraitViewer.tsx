import { portraitImageUrl } from '@/api/client';

interface PortraitViewerProps {
  portraitId: string;
  alt?: string;
}

export function PortraitViewer({ portraitId, alt = 'Портрет персонажа' }: PortraitViewerProps) {
  return (
    <div className="portrait-frame">
      <img src={portraitImageUrl(portraitId)} alt={alt} loading="lazy" />
    </div>
  );
}

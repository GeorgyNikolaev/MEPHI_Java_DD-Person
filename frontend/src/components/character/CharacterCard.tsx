import { Link } from 'react-router-dom';
import type { CharacterSummary } from '@/types/api';
import { PortraitViewer } from '@/components/portrait/PortraitViewer';
import { formatDate } from '@/api/client';

interface CharacterCardProps {
  character: CharacterSummary;
}

export function CharacterCard({ character }: CharacterCardProps) {
  return (
    <Link to={`/characters/${character.id}`} className="character-card" style={{ textDecoration: 'none' }}>
      {character.lastPortrait ? (
        <div style={{ marginBottom: '0.75rem' }}>
          <PortraitViewer portraitId={character.lastPortrait.id} alt={character.name} />
        </div>
      ) : (
        <div className="portrait-placeholder" style={{ marginBottom: '0.75rem', minHeight: '120px' }}>
          Портрет ещё не создан
        </div>
      )}
      <h3>{character.name}</h3>
      <div className="character-meta">
        {character.roleArchetype.labelRu} · {character.universeStyle.labelRu}
      </div>
      <div className="character-meta">Обновлён: {formatDate(character.updatedAt)}</div>
    </Link>
  );
}

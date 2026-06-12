import { useCallback, useEffect, useState } from 'react';

export function usePolling<T>(
  fetcher: () => Promise<T>,
  shouldPoll: (data: T) => boolean,
  intervalMs = 2500,
) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      const result = await fetcher();
      setData(result);
      setError(null);
      return result;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка загрузки');
      return null;
    } finally {
      setLoading(false);
    }
  }, [fetcher]);

  useEffect(() => {
    let active = true;
    let timer: ReturnType<typeof setTimeout> | undefined;

    const tick = async () => {
      const result = await refresh();
      if (!active || !result || !shouldPoll(result)) {
        return;
      }
      timer = setTimeout(tick, intervalMs);
    };

    setLoading(true);
    void tick();

    return () => {
      active = false;
      if (timer) clearTimeout(timer);
    };
  }, [refresh, shouldPoll, intervalMs]);

  return { data, loading, error, refresh };
}

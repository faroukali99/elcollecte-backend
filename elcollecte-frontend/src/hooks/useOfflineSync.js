import { useState, useEffect, useCallback } from 'react';
import collectesApi from '../api/collectesApi';

const QUEUE_KEY = 'elcollecte_offline_queue';

/**
 * Hook de gestion de la file hors-ligne.
 * Quand la connexion revient, synchronise automatiquement les collectes
 * enregistrées localement.
 *
 * Usage :
 *   const { queue, addToQueue, syncNow, isSyncing } = useOfflineSync();
 */
export const useOfflineSync = () => {
  const [queue,     setQueue]     = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(QUEUE_KEY) || '[]');
    } catch { return []; }
  });
  const [isSyncing, setIsSyncing] = useState(false);
  const [lastSync,  setLastSync]  = useState(null);
  const [isOnline,  setIsOnline]  = useState(navigator.onLine);

  // ── Écouter les changements de connectivité ───────────────────────────────
  useEffect(() => {
    const onOnline  = () => { setIsOnline(true);  syncNow(); };
    const onOffline = () =>   setIsOnline(false);

    window.addEventListener('online',  onOnline);
    window.addEventListener('offline', onOffline);
    return () => {
      window.removeEventListener('online',  onOnline);
      window.removeEventListener('offline', onOffline);
    };
  }, [queue]); // re-bind quand la queue change

  // ── Ajouter une collecte à la file hors-ligne ─────────────────────────────
  const addToQueue = useCallback((collecte) => {
    const item = {
      localId:      `local_${Date.now()}_${Math.random().toString(36).slice(2)}`,
      formulaireId: collecte.formulaireId,
      projetId:     collecte.projetId,
      donnees:      collecte.donnees,
      latitude:     collecte.latitude  ?? null,
      longitude:    collecte.longitude ?? null,
      collectedAt:  new Date().toISOString(),
    };
    setQueue(prev => {
      const next = [...prev, item];
      localStorage.setItem(QUEUE_KEY, JSON.stringify(next));
      return next;
    });
    return item.localId;
  }, []);

  // ── Vider la file ─────────────────────────────────────────────────────────
  const clearQueue = useCallback(() => {
    setQueue([]);
    localStorage.removeItem(QUEUE_KEY);
  }, []);

  // ── Synchroniser avec le serveur ──────────────────────────────────────────
  const syncNow = useCallback(async () => {
    const current = JSON.parse(localStorage.getItem(QUEUE_KEY) || '[]');
    if (current.length === 0 || isSyncing || !navigator.onLine) return;

    setIsSyncing(true);
    try {
      const response = await collectesApi.syncBatch(current);

      // Retirer uniquement les collectes synchronisées avec succès
      const failedIds = new Set(
          response.results
              .filter(r => !r.success)
              .map(r => r.localId)
      );
      const remaining = current.filter(item => failedIds.has(item.localId));

      setQueue(remaining);
      localStorage.setItem(QUEUE_KEY, JSON.stringify(remaining));
      setLastSync(new Date());

      if (response.synced > 0) {
        console.info(`[OfflineSync] ${response.synced} collecte(s) synchronisée(s)`);
      }
      if (response.failed > 0) {
        console.warn(`[OfflineSync] ${response.failed} collecte(s) en échec`);
      }

      return response;
    } catch (err) {
      console.error('[OfflineSync] Erreur lors de la synchronisation', err);
    } finally {
      setIsSyncing(false);
    }
  }, [isSyncing]);

  return {
    queue,
    queueSize: queue.length,
    addToQueue,
    clearQueue,
    syncNow,
    isSyncing,
    lastSync,
    isOnline,
  };
};

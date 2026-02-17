import { useState, useEffect } from 'react';

export const useOfflineQueue = () => {
  const [queue, setQueue] = useState([]);

  useEffect(() => {
    const storedQueue = localStorage.getItem('offlineQueue');
    if (storedQueue) {
      setQueue(JSON.parse(storedQueue));
    }
  }, []);

  const addToQueue = (item) => {
    const newQueue = [...queue, item];
    setQueue(newQueue);
    localStorage.setItem('offlineQueue', JSON.stringify(newQueue));
  };

  const clearQueue = () => {
    setQueue([]);
    localStorage.removeItem('offlineQueue');
  };

  return { queue, addToQueue, clearQueue };
};
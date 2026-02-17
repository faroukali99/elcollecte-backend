import React from 'react';
import MediaUpload from '../components/MediaUpload';

const Collecte = () => {
  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Nouvelle Collecte</h1>
      <form className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Description</label>
          <textarea className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2" />
        </div>
        <MediaUpload />
        <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded">Soumettre</button>
      </form>
    </div>
  );
};

export default Collecte;
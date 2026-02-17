import React from 'react';

const MediaUpload = () => {
  return (
    <div className="border-2 border-dashed border-gray-300 rounded-md p-6 text-center">
      <p className="text-gray-500">Glissez et déposez des fichiers ici, ou cliquez pour sélectionner</p>
      <input type="file" className="hidden" />
    </div>
  );
};

export default MediaUpload;
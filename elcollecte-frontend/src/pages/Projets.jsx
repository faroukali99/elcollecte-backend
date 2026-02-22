import React, { useState, useEffect } from 'react';
import client from '../api/client';
import { Plus, Search, Edit, Trash2, Users, Calendar, MapPin, CheckCircle2, Clock, XCircle } from 'lucide-react';

const STATUS_CONFIG = {
  BROUILLON: { label: 'Brouillon', color: '#6b7280', bg: '#f3f4f6', icon: '📝' },
  ACTIF: { label: 'Actif', color: '#059669', bg: '#d1fae5', icon: '✅' },
  SUSPENDU: { label: 'Suspendu', color: '#d97706', bg: '#fef3c7', icon: '⏸️' },
  TERMINE: { label: 'Terminé', color: '#374151', bg: '#f9fafb', icon: '🏁' },
};

const Projets = () => {
  const [projets, setProjets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    zoneGeo: ''
  });

  const fetchProjets = async () => {
    setLoading(true);
    try {
      const { data } = await client.get('/projets?page=0&size=50');
      setProjets(data.content ?? data ?? []);
    } catch (err) {
      console.error('[Projets]', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjets();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await client.post('/projets', {
        ...formData,
        statut: 'BROUILLON'
      });
      setShowCreateForm(false);
      setFormData({
        titre: '',
        description: '',
        dateDebut: '',
        dateFin: '',
        zoneGeo: ''
      });
      fetchProjets();
    } catch (err) {
      console.error('[Projets] Création échouée:', err);
    }
  };

  const filteredProjets = projets.filter(p =>
    p.titre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <div style={{ fontSize: '1.1rem', color: 'var(--c-ink-2)' }}>Chargement des projets...</div>
      </div>
    );
  }

  return (
    <div style={{ padding: '28px 0' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <h1 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--c-ink)', letterSpacing: '-0.02em' }}>
            Projets
          </h1>
          <p style={{ margin: '4px 0 0', fontSize: '0.855rem', color: 'var(--c-ink-2)' }}>
            Gérez vos projets de collecte de données
          </p>
        </div>
        <button
          onClick={() => setShowCreateForm(true)}
          style={{
            display: 'flex', alignItems: 'center', gap: 8,
            padding: '10px 16px', border: 'none', borderRadius: 10,
            background: 'var(--c-accent)', color: '#fff',
            fontSize: '0.855rem', fontWeight: 600, cursor: 'pointer'
          }}
        >
          <Plus size={16} /> Nouveau projet
        </button>
      </div>

      <div style={{ marginBottom: 20 }}>
        <div style={{ position: 'relative', maxWidth: 400 }}>
          <Search size={18} style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--c-ink-3)' }} />
          <input
            type="text"
            placeholder="Rechercher un projet..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            style={{
              width: '100%', padding: '10px 14px 10px 44px',
              border: '1px solid var(--c-border)', borderRadius: 10,
              fontSize: '0.855rem', background: '#fff'
            }}
          />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', gap: 20 }}>
        {filteredProjets.length === 0 ? (
          <div style={{ gridColumn: '1/-1', textAlign: 'center', padding: '60px 20px' }}>
            <div style={{ fontSize: '3rem', marginBottom: 16, opacity: 0.3 }}>📁</div>
            <p style={{ margin: 0, fontSize: '1.1rem', fontWeight: 600, color: 'var(--c-ink-2)' }}>
              {searchTerm ? 'Aucun projet trouvé' : 'Aucun projet pour le moment'}
            </p>
          </div>
        ) : (
          filteredProjets.map(projet => {
            const status = STATUS_CONFIG[projet.statut] || STATUS_CONFIG.BROUILLON;
            return (
              <div key={projet.id} className="card" style={{ padding: 20, border: '1px solid var(--c-border)', borderRadius: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                  <h3 style={{ margin: 0, fontSize: '1.1rem', fontWeight: 700, color: 'var(--c-ink)' }}>
                    {projet.titre}
                  </h3>
                  <span style={{
                    display: 'inline-flex', alignItems: 'center', gap: 6,
                    padding: '4px 10px', borderRadius: 99,
                    fontSize: '0.72rem', fontWeight: 700,
                    background: status.bg, color: status.color
                  }}>
                    {status.icon} {status.label}
                  </span>
                </div>

                <p style={{ margin: '0 0 16px', fontSize: '0.855rem', color: 'var(--c-ink-2)', lineHeight: 1.5 }}>
                  {projet.description || 'Aucune description'}
                </p>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 16 }}>
                  {projet.zoneGeo && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                      <MapPin size={14} /> {projet.zoneGeo}
                    </div>
                  )}
                  {projet.dateDebut && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                      <Calendar size={14} /> Début: {new Date(projet.dateDebut).toLocaleDateString('fr-FR')}
                    </div>
                  )}
                  {projet.dateFin && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--c-ink-2)' }}>
                      <Calendar size={14} /> Fin: {new Date(projet.dateFin).toLocaleDateString('fr-FR')}
                    </div>
                  )}
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--c-ink-3)' }}>
                    <Users size={14} />
                    <span>{projet.nbEnqueteurs || 0} enquêteur{projet.nbEnqueteurs > 1 ? 's' : ''}</span>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Modal de création */}
      {showCreateForm && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 100,
          background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
          <div style={{
            background: '#fff', borderRadius: 16, width: '100%', maxWidth: 500,
            padding: 24, margin: 20
          }}>
            <h2 style={{ margin: '0 0 20px', fontSize: '1.2rem', fontWeight: 700, color: 'var(--c-ink)' }}>
              Nouveau projet
            </h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  Titre *
                </label>
                <input
                  type="text"
                  required
                  value={formData.titre}
                  onChange={e => setFormData(f => ({ ...f, titre: e.target.value }))}
                  style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)', borderRadius: 8 }}
                  placeholder="Nom du projet"
                />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={e => setFormData(f => ({ ...f, description: e.target.value }))}
                  style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)', borderRadius: 8, minHeight: 80 }}
                  placeholder="Description du projet"
                />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                    Date de début
                  </label>
                  <input
                    type="date"
                    value={formData.dateDebut}
                    onChange={e => setFormData(f => ({ ...f, dateDebut: e.target.value }))}
                    style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)', borderRadius: 8 }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                    Date de fin
                  </label>
                  <input
                    type="date"
                    value={formData.dateFin}
                    onChange={e => setFormData(f => ({ ...f, dateFin: e.target.value }))}
                    style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)', borderRadius: 8 }}
                  />
                </div>
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 6 }}>
                  Zone géographique
                </label>
                <input
                  type="text"
                  value={formData.zoneGeo}
                  onChange={e => setFormData(f => ({ ...f, zoneGeo: e.target.value }))}
                  style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)', borderRadius: 8 }}
                  placeholder="Région, ville, quartier..."
                />
              </div>
              <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => setShowCreateForm(false)}
                  style={{
                    padding: '10px 16px', border: '1px solid var(--c-border)', borderRadius: 8,
                    background: 'transparent', cursor: 'pointer', fontSize: '0.855rem', fontWeight: 600
                  }}
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  style={{
                    padding: '10px 16px', border: 'none', borderRadius: 8,
                    background: 'var(--c-accent)', color: '#fff', cursor: 'pointer',
                    fontSize: '0.855rem', fontWeight: 600
                  }}
                >
                  Créer le projet
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Projets;

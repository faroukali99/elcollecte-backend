import React, { useState, useEffect } from 'react';
import client from '../api/client';
import {
  Plus, Search, Edit2, Trash2, Check, X, AlertCircle,
  Users, Calendar, MapPin, Eye, EyeOff, Filter, Download
} from 'lucide-react';

const STATUS_CONFIG = {
  BROUILLON: { label: 'Brouillon', color: '#6b7280', bg: '#f3f4f6', icon: '📝', action: 'Soumettre' },
  ACTIF: { label: 'Actif', color: '#059669', bg: '#d1fae5', icon: '✅', action: 'Activer' },
  SUSPENDU: { label: 'Suspendu', color: '#d97706', bg: '#fef3c7', icon: '⏸️', action: 'Réactiver' },
  TERMINE: { label: 'Terminé', color: '#374151', bg: '#f9fafb', icon: '🏁', action: 'Archiver' },
  REJETE: { label: 'Rejeté', color: '#dc2626', bg: '#fee2e2', icon: '❌', action: 'Réactiver' },
  VALIDE: { label: 'Validé', color: '#0891b2', bg: '#cffafe', icon: '✔️', action: 'Valider' },
};

const GestionProjets = () => {
  const [projets, setProjets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Modal states
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [selectedProjet, setSelectedProjet] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [showEditModal, setShowEditModal] = useState(false);
  const [editData, setEditData] = useState(null);

  // Fetch projects
  const fetchProjets = async () => {
    setLoading(true);
    try {
      const { data } = await client.get('/projets?page=0&size=100');
      setProjets(data.content ?? data ?? []);
    } catch (err) {
      console.error('Erreur lors du chargement des projets:', err);
      setError('Impossible de charger les projets');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjets();
  }, []);

  // Filter projects
  const filteredProjets = projets.filter(p => {
    const matchSearch = p.titre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.description?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchFilter = filterStatus === 'ALL' || p.statut === filterStatus;
    return matchSearch && matchFilter;
  });

  // Handle project status change
  const handleStatusChange = async (action) => {
    setError('');
    setSuccess('');

    try {
      const payload = {
        statut: action === 'valider' ? 'VALIDE' :
                action === 'rejeter' ? 'REJETE' :
                action === 'activer' ? 'ACTIF' :
                action === 'suspendre' ? 'SUSPENDU' : 'BROUILLON'
      };

      if (action === 'rejeter' && !rejectionReason.trim()) {
        setError('Veuillez fournir un motif de rejet');
        return;
      }

      if (action === 'rejeter') {
        payload.motifRejet = rejectionReason;
      }

      await client.put(`/projets/${selectedProjet.id}`, payload);
      setSuccess(`Projet ${payload.statut.toLowerCase()}`);
      setShowConfirmModal(false);
      setConfirmAction(null);
      setRejectionReason('');
      fetchProjets();
    } catch (err) {
      const msg = err.response?.data?.message || 'Erreur lors de la mise à jour';
      setError(msg);
    }
  };

  // Handle project deletion
  const handleDelete = async () => {
    setError('');
    try {
      await client.delete(`/projets/${selectedProjet.id}`);
      setSuccess('Projet supprimé avec succès');
      setShowDetailsModal(false);
      fetchProjets();
    } catch (err) {
      const msg = err.response?.data?.message || 'Erreur lors de la suppression';
      setError(msg);
    }
  };

  // Handle project update
  const handleUpdateProjet = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await client.put(`/projets/${editData.id}`, editData);
      setSuccess('Projet mis à jour avec succès');
      setShowEditModal(false);
      fetchProjets();
    } catch (err) {
      const msg = err.response?.data?.message || 'Erreur lors de la mise à jour';
      setError(msg);
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <div style={{ fontSize: '1.1rem', color: 'var(--c-ink-2)' }}>Chargement des projets...</div>
      </div>
    );
  }

  return (
    <div style={{ padding: '28px 0' }}>
      {/* Header */}
      <div style={{ marginBottom: 32 }}>
        <h1 style={{ margin: 0, fontSize: '1.8rem', fontWeight: 800, color: 'var(--c-ink)' }}>
          🎯 Gestion des Projets
        </h1>
        <p style={{ margin: '8px 0 0', fontSize: '0.95rem', color: 'var(--c-ink-2)' }}>
          Validez, rejetez et gérez vos projets de collecte de données
        </p>
      </div>

      {/* Messages */}
      {error && (
        <div style={{
          padding: '12px 16px', borderRadius: 10, marginBottom: 20,
          background: '#fee2e2', color: '#991b1b', fontSize: '0.9rem',
          border: '1px solid #fecaca', display: 'flex', alignItems: 'center', gap: 8
        }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {success && (
        <div style={{
          padding: '12px 16px', borderRadius: 10, marginBottom: 20,
          background: '#dcfce7', color: '#166534', fontSize: '0.9rem',
          border: '1px solid #bbf7d0'
        }}>
          ✅ {success}
        </div>
      )}

      {/* Search & Filter */}
      <div style={{ display: 'flex', gap: 16, marginBottom: 24, flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: 250 }}>
          <Search size={18} style={{
            position: 'absolute', left: 14, top: '50%',
            transform: 'translateY(-50%)', color: 'var(--c-ink-3)'
          }} />
          <input
            type="text"
            placeholder="Rechercher un projet..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            style={{
              width: '100%', padding: '11px 14px 11px 44px',
              border: '1px solid var(--c-border)', borderRadius: 10,
              fontSize: '0.9rem', background: '#fff'
            }}
          />
        </div>

        <select
          value={filterStatus}
          onChange={e => setFilterStatus(e.target.value)}
          style={{
            padding: '11px 14px', border: '1px solid var(--c-border)',
            borderRadius: 10, fontSize: '0.9rem', background: '#fff',
            cursor: 'pointer', minWidth: 180
          }}
        >
          <option value="ALL">Tous les statuts</option>
          <option value="BROUILLON">Brouillon</option>
          <option value="ACTIF">Actif</option>
          <option value="SUSPENDU">Suspendu</option>
          <option value="VALIDE">Validé</option>
          <option value="REJETE">Rejeté</option>
          <option value="TERMINE">Terminé</option>
        </select>
      </div>

      {/* Stats */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
        gap: 12, marginBottom: 24
      }}>
        {[
          { label: 'Total', count: projets.length, color: '#3b82f6' },
          { label: 'Actifs', count: projets.filter(p => p.statut === 'ACTIF').length, color: '#059669' },
          { label: 'En attente', count: projets.filter(p => p.statut === 'BROUILLON').length, color: '#6b7280' },
          { label: 'Validés', count: projets.filter(p => p.statut === 'VALIDE').length, color: '#0891b2' },
          { label: 'Rejetés', count: projets.filter(p => p.statut === 'REJETE').length, color: '#dc2626' },
        ].map((stat, idx) => (
          <div key={idx} style={{
            padding: 14, borderRadius: 10, background: '#f9fafb',
            border: '1px solid var(--c-border)'
          }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', marginBottom: 4 }}>
              {stat.label}
            </div>
            <div style={{ fontSize: '1.6rem', fontWeight: 700, color: stat.color }}>
              {stat.count}
            </div>
          </div>
        ))}
      </div>

      {/* Projects Table */}
      <div style={{
        border: '1px solid var(--c-border)', borderRadius: 12,
        overflow: 'hidden', background: '#fff'
      }}>
        {filteredProjets.length === 0 ? (
          <div style={{ padding: '60px 20px', textAlign: 'center' }}>
            <div style={{ fontSize: '3rem', marginBottom: 16, opacity: 0.3 }}>📭</div>
            <p style={{ margin: 0, fontSize: '1.1rem', fontWeight: 600, color: 'var(--c-ink-2)' }}>
              {searchTerm || filterStatus !== 'ALL' ? 'Aucun projet trouvé' : 'Aucun projet pour le moment'}
            </p>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f9fafb', borderBottom: '1px solid var(--c-border)' }}>
                  <th style={{ padding: '14px 16px', textAlign: 'left', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
                    Projet
                  </th>
                  <th style={{ padding: '14px 16px', textAlign: 'left', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
                    Statut
                  </th>
                  <th style={{ padding: '14px 16px', textAlign: 'left', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
                    Dates
                  </th>
                  <th style={{ padding: '14px 16px', textAlign: 'left', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
                    Enquêteurs
                  </th>
                  <th style={{ padding: '14px 16px', textAlign: 'center', fontSize: '0.8rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody>
                {filteredProjets.map((projet, idx) => {
                  const status = STATUS_CONFIG[projet.statut] || STATUS_CONFIG.BROUILLON;
                  const zoneLabel = projet.zoneGeo?.nom ?? (typeof projet.zoneGeo === 'string' ? projet.zoneGeo : null);

                  return (
                    <tr
                      key={projet.id}
                      style={{
                        borderBottom: '1px solid var(--c-border)',
                        '&:hover': { background: '#f9fafb' }
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.background = '#f9fafb'}
                      onMouseLeave={(e) => e.currentTarget.style.background = '#fff'}
                    >
                      {/* Projet Info */}
                      <td style={{ padding: '14px 16px' }}>
                        <div>
                          <div style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--c-ink)', marginBottom: 4 }}>
                            {projet.titre}
                          </div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)' }}>
                            {zoneLabel && `📍 ${zoneLabel}`}
                          </div>
                        </div>
                      </td>

                      {/* Status */}
                      <td style={{ padding: '14px 16px' }}>
                        <span style={{
                          display: 'inline-flex', alignItems: 'center', gap: 6,
                          padding: '5px 10px', borderRadius: 6,
                          fontSize: '0.8rem', fontWeight: 600,
                          background: status.bg, color: status.color
                        }}>
                          {status.icon} {status.label}
                        </span>
                      </td>

                      {/* Dates */}
                      <td style={{ padding: '14px 16px', fontSize: '0.8rem', color: 'var(--c-ink-2)' }}>
                        {projet.dateDebut && (
                          <div>{new Date(projet.dateDebut).toLocaleDateString('fr-FR')}</div>
                        )}
                        {projet.dateFin && (
                          <div>à {new Date(projet.dateFin).toLocaleDateString('fr-FR')}</div>
                        )}
                      </td>

                      {/* Enquêteurs */}
                      <td style={{ padding: '14px 16px', textAlign: 'left' }}>
                        <span style={{
                          display: 'inline-flex', alignItems: 'center', gap: 4,
                          fontSize: '0.8rem', color: 'var(--c-ink-2)'
                        }}>
                          👤 {projet.nbEnqueteurs || 0}
                        </span>
                      </td>

                      {/* Actions */}
                      <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                        <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }}>
                          <button
                            onClick={() => {
                              setSelectedProjet(projet);
                              setShowDetailsModal(true);
                            }}
                            style={{
                              padding: '6px 10px', border: '1px solid var(--c-border)',
                              borderRadius: 6, background: '#fff', cursor: 'pointer',
                              display: 'flex', alignItems: 'center', gap: 4
                            }}
                            title="Voir les détails"
                          >
                            <Eye size={14} />
                          </button>

                          <button
                            onClick={() => {
                              setEditData({ ...projet });
                              setShowEditModal(true);
                            }}
                            style={{
                              padding: '6px 10px', border: '1px solid var(--c-border)',
                              borderRadius: 6, background: '#fff', cursor: 'pointer',
                              display: 'flex', alignItems: 'center', gap: 4
                            }}
                            title="Modifier"
                          >
                            <Edit2 size={14} />
                          </button>

                          {projet.statut === 'BROUILLON' && (
                            <button
                              onClick={() => {
                                setSelectedProjet(projet);
                                setConfirmAction('valider');
                                setShowConfirmModal(true);
                              }}
                              style={{
                                padding: '6px 10px', border: '1px solid #059669',
                                borderRadius: 6, background: '#ecfdf5', color: '#059669',
                                cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600,
                                display: 'flex', alignItems: 'center', gap: 4
                              }}
                              title="Valider"
                            >
                              <Check size={14} /> Valider
                            </button>
                          )}

                          {(projet.statut === 'BROUILLON' || projet.statut === 'ACTIF') && (
                            <button
                              onClick={() => {
                                setSelectedProjet(projet);
                                setConfirmAction('rejeter');
                                setShowConfirmModal(true);
                              }}
                              style={{
                                padding: '6px 10px', border: '1px solid #dc2626',
                                borderRadius: 6, background: '#fef2f2', color: '#dc2626',
                                cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600,
                                display: 'flex', alignItems: 'center', gap: 4
                              }}
                              title="Rejeter"
                            >
                              <X size={14} /> Rejeter
                            </button>
                          )}

                          {projet.statut === 'ACTIF' && (
                            <button
                              onClick={() => {
                                setSelectedProjet(projet);
                                setConfirmAction('suspendre');
                                setShowConfirmModal(true);
                              }}
                              style={{
                                padding: '6px 10px', border: '1px solid #d97706',
                                borderRadius: 6, background: '#fffbeb', color: '#d97706',
                                cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600,
                                display: 'flex', alignItems: 'center', gap: 4
                              }}
                              title="Suspendre"
                            >
                              ⏸️ Suspendre
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Details Modal */}
      {showDetailsModal && selectedProjet && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 200,
          background: 'rgba(0,0,0,0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: 20
        }}>
          <div style={{
            background: '#fff', borderRadius: 16, maxWidth: 600,
            width: '100%', padding: 28
          }}>
            <h2 style={{ margin: '0 0 20px', fontSize: '1.3rem', fontWeight: 700 }}>
              {selectedProjet.titre}
            </h2>

            <div style={{ marginBottom: 20 }}>
              <div style={{ marginBottom: 12 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                  Statut
                </label>
                <div style={{ marginTop: 4 }}>
                  <span style={{
                    display: 'inline-flex', alignItems: 'center', gap: 6,
                    padding: '6px 12px', borderRadius: 8,
                    fontSize: '0.9rem', fontWeight: 600,
                    background: STATUS_CONFIG[selectedProjet.statut].bg,
                    color: STATUS_CONFIG[selectedProjet.statut].color
                  }}>
                    {STATUS_CONFIG[selectedProjet.statut].icon} {STATUS_CONFIG[selectedProjet.statut].label}
                  </span>
                </div>
              </div>

              <div style={{ marginBottom: 12 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                  Description
                </label>
                <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--c-ink)' }}>
                  {selectedProjet.description || 'Aucune description'}
                </p>
              </div>

              {selectedProjet.zoneGeo && (
                <div style={{ marginBottom: 12 }}>
                  <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                    Zone géographique
                  </label>
                  <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--c-ink)' }}>
                    {selectedProjet.zoneGeo?.nom || selectedProjet.zoneGeo}
                  </p>
                </div>
              )}

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 12 }}>
                {selectedProjet.dateDebut && (
                  <div>
                    <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                      Date de début
                    </label>
                    <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--c-ink)' }}>
                      {new Date(selectedProjet.dateDebut).toLocaleDateString('fr-FR')}
                    </p>
                  </div>
                )}

                {selectedProjet.dateFin && (
                  <div>
                    <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                      Date de fin
                    </label>
                    <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--c-ink)' }}>
                      {new Date(selectedProjet.dateFin).toLocaleDateString('fr-FR')}
                    </p>
                  </div>
                )}
              </div>

              <div>
                <label style={{ fontSize: '0.8rem', color: 'var(--c-ink-2)', fontWeight: 600 }}>
                  Nombre d'enquêteurs
                </label>
                <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--c-ink)' }}>
                  {selectedProjet.nbEnqueteurs || 0}
                </p>
              </div>

              {selectedProjet.statut === 'REJETE' && selectedProjet.motifRejet && (
                <div style={{
                  padding: 12, background: '#fee2e2', border: '1px solid #fca5a5',
                  borderRadius: 8, marginTop: 12
                }}>
                  <label style={{ fontSize: '0.8rem', color: '#991b1b', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 6 }}>
                    ❌ Motif de rejet
                  </label>
                  <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: '#7f1d1d' }}>
                    {selectedProjet.motifRejet}
                  </p>
                </div>
              )}
            </div>

            <div style={{
              display: 'flex', gap: 12, justifyContent: 'flex-end',
              borderTop: '1px solid var(--c-border)', paddingTop: 16
            }}>
              <button
                onClick={() => setShowDetailsModal(false)}
                style={{
                  padding: '10px 16px', border: '1px solid var(--c-border)',
                  borderRadius: 8, background: '#fff', cursor: 'pointer',
                  fontSize: '0.9rem', fontWeight: 600
                }}
              >
                Fermer
              </button>
              <button
                onClick={handleDelete}
                style={{
                  padding: '10px 16px', border: '1px solid #dc2626',
                  borderRadius: 8, background: '#fef2f2', color: '#dc2626',
                  cursor: 'pointer', fontSize: '0.9rem', fontWeight: 600
                }}
              >
                Supprimer
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && editData && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 200,
          background: 'rgba(0,0,0,0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: 20
        }}>
          <div style={{
            background: '#fff', borderRadius: 16, maxWidth: 500,
            width: '100%', padding: 24
          }}>
            <h2 style={{ margin: '0 0 20px', fontSize: '1.3rem', fontWeight: 700 }}>
              Modifier le projet
            </h2>

            <form onSubmit={handleUpdateProjet} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: 6 }}>
                  Titre
                </label>
                <input
                  type="text"
                  value={editData.titre || ''}
                  onChange={e => setEditData({ ...editData, titre: e.target.value })}
                  style={{
                    width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)',
                    borderRadius: 8, fontSize: '0.9rem'
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: 6 }}>
                  Description
                </label>
                <textarea
                  value={editData.description || ''}
                  onChange={e => setEditData({ ...editData, description: e.target.value })}
                  style={{
                    width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)',
                    borderRadius: 8, fontSize: '0.9rem', minHeight: 80
                  }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: 6 }}>
                    Date de début
                  </label>
                  <input
                    type="date"
                    value={editData.dateDebut || ''}
                    onChange={e => setEditData({ ...editData, dateDebut: e.target.value })}
                    style={{
                      width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)',
                      borderRadius: 8
                    }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: 6 }}>
                    Date de fin
                  </label>
                  <input
                    type="date"
                    value={editData.dateFin || ''}
                    onChange={e => setEditData({ ...editData, dateFin: e.target.value })}
                    style={{
                      width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)',
                      borderRadius: 8
                    }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  style={{
                    padding: '10px 16px', border: '1px solid var(--c-border)',
                    borderRadius: 8, background: '#fff', cursor: 'pointer',
                    fontSize: '0.9rem', fontWeight: 600
                  }}
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  style={{
                    padding: '10px 16px', border: 'none', borderRadius: 8,
                    background: 'var(--c-accent)', color: '#fff', cursor: 'pointer',
                    fontSize: '0.9rem', fontWeight: 600
                  }}
                >
                  Enregistrer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Confirm Modal */}
      {showConfirmModal && selectedProjet && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 200,
          background: 'rgba(0,0,0,0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: 20
        }}>
          <div style={{
            background: '#fff', borderRadius: 16, maxWidth: 450,
            width: '100%', padding: 24
          }}>
            <h2 style={{ margin: '0 0 16px', fontSize: '1.2rem', fontWeight: 700 }}>
              {confirmAction === 'valider' && '✅ Valider le projet?'}
              {confirmAction === 'rejeter' && '❌ Rejeter le projet?'}
              {confirmAction === 'suspendre' && '⏸️ Suspendre le projet?'}
            </h2>

            <p style={{ margin: '0 0 20px', fontSize: '0.95rem', color: 'var(--c-ink-2)' }}>
              {confirmAction === 'valider' && 'Le projet será activé et les enquêteurs pourront commencer la collecte.'}
              {confirmAction === 'rejeter' && 'Le projet será rejeté. Veuillez fournir un motif.'}
              {confirmAction === 'suspendre' && 'Le projet será suspendu temporairement.'}
            </p>

            {confirmAction === 'rejeter' && (
              <textarea
                value={rejectionReason}
                onChange={e => setRejectionReason(e.target.value)}
                placeholder="Motif du rejet..."
                style={{
                  width: '100%', padding: '10px 12px', border: '1px solid var(--c-border)',
                  borderRadius: 8, fontSize: '0.9rem', minHeight: 80, marginBottom: 16
                }}
              />
            )}

            {error && (
              <div style={{
                padding: '10px 12px', borderRadius: 8, marginBottom: 16,
                background: '#fee2e2', color: '#991b1b', fontSize: '0.85rem'
              }}>
                {error}
              </div>
            )}

            <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
              <button
                onClick={() => {
                  setShowConfirmModal(false);
                  setConfirmAction(null);
                  setRejectionReason('');
                }}
                style={{
                  padding: '10px 16px', border: '1px solid var(--c-border)',
                  borderRadius: 8, background: '#fff', cursor: 'pointer',
                  fontSize: '0.9rem', fontWeight: 600
                }}
              >
                Annuler
              </button>
              <button
                onClick={() => handleStatusChange(confirmAction)}
                style={{
                  padding: '10px 16px', border: 'none', borderRadius: 8,
                  background: confirmAction === 'rejeter' ? '#dc2626' : 'var(--c-accent)',
                  color: '#fff', cursor: 'pointer', fontSize: '0.9rem', fontWeight: 600
                }}
              >
                {confirmAction === 'valider' && 'Valider'}
                {confirmAction === 'rejeter' && 'Rejeter'}
                {confirmAction === 'suspendre' && 'Suspendre'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GestionProjets;


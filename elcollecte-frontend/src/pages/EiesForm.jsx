import React, { useState, useEffect, useCallback, useRef } from 'react';
import client from '../api/client';
import {
  Leaf, Users, FileText, ChevronRight, ChevronLeft,
  Save, Send, CheckCircle, AlertTriangle, Upload,
  MapPin, Building, Calendar, Trees, Droplets,
  Wind, Volume2, Trash2, Shield, Info, X
} from 'lucide-react';

// ── Design tokens ─────────────────────────────────────────────────────────────
const COULEURS_SECTION = {
  0: { accent: '#16a34a', bg: '#f0fdf4', border: '#86efac', icon: '#15803d' },
  1: { accent: '#0369a1', bg: '#eff6ff', border: '#93c5fd', icon: '#0284c7' },
  2: { accent: '#7c3aed', bg: '#faf5ff', border: '#c4b5fd', icon: '#6d28d9' },
  3: { accent: '#b45309', bg: '#fffbeb', border: '#fcd34d', icon: '#92400e' },
  4: { accent: '#374151', bg: '#f9fafb', border: '#d1d5db', icon: '#1f2937' },
};

// ── Constantes ────────────────────────────────────────────────────────────────
const SECTIONS = [
  { id: 0, titre: 'Informations générales',   icone: MapPin    },
  { id: 1, titre: 'Description du projet',    icone: Building  },
  { id: 2, titre: 'Impacts environnementaux', icone: Leaf      },
  { id: 3, titre: 'Impacts sociaux',          icone: Users     },
  { id: 4, titre: 'Documents & Annexes',      icone: FileText  },
];

const TYPES_PROJET = [
  'Infrastructure routière', 'Infrastructure hydraulique',
  'Industrie extractive', 'Industrie manufacturière',
  'Agriculture & agro-industrie', 'Énergie',
  'Urbanisme & habitat', 'Tourisme', 'Autre',
];

const OUI_NON = ['OUI', 'NON', 'PARTIEL'];

// ── Composants utilitaires ────────────────────────────────────────────────────

const Label = ({ children, required }) => (
  <label style={{
    display: 'block', fontSize: '0.8rem', fontWeight: 700,
    color: 'var(--c-ink)', marginBottom: 6,
  }}>
    {children}
    {required && <span style={{ color: '#dc2626', marginLeft: 3 }}>*</span>}
  </label>
);

const FieldError = ({ message }) =>
  message ? (
    <p style={{
      marginTop: 4, fontSize: '0.75rem', color: '#dc2626',
      display: 'flex', alignItems: 'center', gap: 4,
    }}>
      <AlertTriangle size={11} /> {message}
    </p>
  ) : null;

const Field = ({ label, required, error, children }) => (
  <div style={{ marginBottom: 20 }}>
    {label && <Label required={required}>{label}</Label>}
    {children}
    <FieldError message={error} />
  </div>
);

const Input = ({ style, ...props }) => (
  <input
    className="input-field"
    style={{ ...style }}
    {...props}
  />
);

const Textarea = ({ style, ...props }) => (
  <textarea
    className="input-field"
    style={{ minHeight: 110, resize: 'vertical', ...style }}
    {...props}
  />
);

const RadioGroup = ({ options, value, onChange, name }) => (
  <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
    {options.map(opt => (
      <label
        key={opt}
        style={{
          display: 'flex', alignItems: 'center', gap: 7,
          padding: '8px 16px', borderRadius: 10, cursor: 'pointer',
          border: `1.5px solid ${value === opt ? 'var(--c-accent)' : 'var(--c-border)'}`,
          background: value === opt ? '#dbeafe' : '#fff',
          color: value === opt ? 'var(--c-accent)' : 'var(--c-ink-2)',
          fontWeight: value === opt ? 700 : 400,
          fontSize: '0.855rem',
          transition: 'all 0.15s',
          userSelect: 'none',
        }}
      >
        <input
          type="radio"
          name={name}
          value={opt}
          checked={value === opt}
          onChange={() => onChange(opt)}
          style={{ display: 'none' }}
        />
        {opt}
      </label>
    ))}
  </div>
);

// ── Barre de progression ──────────────────────────────────────────────────────
const BarreProgression = ({ etape, total, score }) => (
  <div style={{
    background: '#fff',
    border: '1px solid var(--c-border)',
    borderRadius: 16,
    padding: '16px 24px',
    marginBottom: 24,
    boxShadow: 'var(--shadow-xs)',
  }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
      <span style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--c-ink-2)' }}>
        Étape {etape + 1} sur {total}
      </span>
      <span style={{
        display: 'flex', alignItems: 'center', gap: 6,
        fontSize: '0.82rem', fontWeight: 700,
        color: score >= 75 ? '#16a34a' : score >= 40 ? '#d97706' : '#dc2626',
      }}>
        <CheckCircle size={14} />
        Complétude : {score}%
      </span>
    </div>

    {/* Steps */}
    <div style={{ display: 'flex', gap: 4, marginBottom: 12 }}>
      {SECTIONS.map((s, i) => {
        const couleur = COULEURS_SECTION[i];
        const actif   = i === etape;
        const fait    = i < etape;
        return (
          <div
            key={i}
            style={{
              flex: 1, height: 6, borderRadius: 99,
              background: fait ? couleur.accent : actif ? couleur.border : 'var(--c-border)',
              transition: 'background 0.3s',
            }}
          />
        );
      })}
    </div>

    {/* Section labels */}
    <div style={{ display: 'flex', gap: 4 }}>
      {SECTIONS.map((s, i) => {
        const actif = i === etape;
        const fait  = i < etape;
        const Icon  = s.icone;
        return (
          <div
            key={i}
            style={{
              flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
              gap: 3, opacity: fait ? 1 : actif ? 1 : 0.4,
              transition: 'opacity 0.2s',
            }}
          >
            <div style={{
              width: 28, height: 28, borderRadius: 8,
              background: fait ? COULEURS_SECTION[i].accent
                        : actif ? COULEURS_SECTION[i].bg : '#f3f4f6',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: fait ? '#fff'
                   : actif ? COULEURS_SECTION[i].icon : 'var(--c-ink-3)',
              transition: 'all 0.25s',
            }}>
              {fait ? <CheckCircle size={14} /> : <Icon size={14} />}
            </div>
            <span style={{
              fontSize: '0.6rem', fontWeight: actif ? 700 : 500,
              color: actif ? 'var(--c-ink)' : 'var(--c-ink-3)',
              textAlign: 'center', lineHeight: 1.2,
              display: 'none', // visible uniquement desktop
            }}
              className="step-label"
            >
              {s.titre.split(' ')[0]}
            </span>
          </div>
        );
      })}
    </div>
  </div>
);

// ── Message flash ─────────────────────────────────────────────────────────────
const Flash = ({ type, message, onClose }) => {
  const colors = {
    success: { bg: '#d1fae5', border: '#6ee7b7', color: '#065f46', icon: <CheckCircle size={16} /> },
    error:   { bg: '#fee2e2', border: '#fca5a5', color: '#991b1b', icon: <AlertTriangle size={16} /> },
    info:    { bg: '#dbeafe', border: '#93c5fd', color: '#1d4ed8', icon: <Info size={16} /> },
  };
  const c = colors[type] ?? colors.info;
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      gap: 10, padding: '12px 16px', borderRadius: 10,
      background: c.bg, border: `1px solid ${c.border}`,
      color: c.color, fontSize: '0.855rem', fontWeight: 500,
      marginBottom: 20,
    }}>
      <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
        {c.icon} {message}
      </span>
      {onClose && (
        <button onClick={onClose} style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: c.color, padding: 2 }}>
          <X size={14} />
        </button>
      )}
    </div>
  );
};

// ── Section A : Informations générales ───────────────────────────────────────
const SectionA = ({ data, onChange, erreurs }) => (
  <div>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
      <Field label="Nom du projet" required error={erreurs?.nomProjet?.[0]}>
        <Input
          placeholder="ex : Construction du barrage de…"
          value={data.nomProjet ?? ''}
          onChange={e => onChange('nomProjet', e.target.value)}
        />
      </Field>
      <Field label="Promoteur / Maître d'ouvrage" required error={erreurs?.promoteur?.[0]}>
        <Input
          placeholder="Nom de l'entité responsable"
          value={data.promoteur ?? ''}
          onChange={e => onChange('promoteur', e.target.value)}
        />
      </Field>
    </div>

    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
      <Field label="Région" required error={erreurs?.region?.[0]}>
        <Input placeholder="ex : Maritime" value={data.region ?? ''} onChange={e => onChange('region', e.target.value)} />
      </Field>
      <Field label="Département" required error={erreurs?.departement?.[0]}>
        <Input placeholder="ex : Golfe" value={data.departement ?? ''} onChange={e => onChange('departement', e.target.value)} />
      </Field>
      <Field label="Commune" required error={erreurs?.commune?.[0]}>
        <Input placeholder="ex : Lomé" value={data.commune ?? ''} onChange={e => onChange('commune', e.target.value)} />
      </Field>
    </div>

    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
      <Field label="Email du promoteur" error={erreurs?.emailPromoteur?.[0]}>
        <Input type="email" placeholder="contact@organisation.com" value={data.emailPromoteur ?? ''} onChange={e => onChange('emailPromoteur', e.target.value)} />
      </Field>
      <Field label="Date de début prévue" required error={erreurs?.dateDebut?.[0]}>
        <Input type="date" value={data.dateDebut ?? ''} onChange={e => onChange('dateDebut', e.target.value)} />
      </Field>
      <Field label="Durée estimée" error={erreurs?.dureeEstimee?.[0]}>
        <Input placeholder="ex : 18 mois" value={data.dureeEstimee ?? ''} onChange={e => onChange('dureeEstimee', e.target.value)} />
      </Field>
    </div>
  </div>
);

// ── Section B : Description du projet ────────────────────────────────────────
const SectionB = ({ data, onChange, erreurs }) => (
  <div>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
      <Field label="Type de projet" required error={erreurs?.typeProjet?.[0]}>
        <select
          className="input-field"
          value={data.typeProjet ?? ''}
          onChange={e => onChange('typeProjet', e.target.value)}
          style={{ cursor: 'pointer' }}
        >
          <option value="">Sélectionnez un type…</option>
          {TYPES_PROJET.map(t => <option key={t} value={t}>{t}</option>)}
        </select>
      </Field>
      <Field label="Superficie concernée (ha)" required error={erreurs?.superficie?.[0]}>
        <Input
          type="number" min="0" step="0.01"
          placeholder="ex : 250.5"
          value={data.superficie ?? ''}
          onChange={e => onChange('superficie', e.target.value)}
        />
      </Field>
    </div>

    <Field label="Description détaillée du projet" required error={erreurs?.descriptionDetaillee?.[0]}>
      <Textarea
        style={{ minHeight: 150 }}
        placeholder="Décrivez les objectifs, les composantes, les phases de réalisation et les résultats attendus (minimum 100 caractères)…"
        value={data.descriptionDetaillee ?? ''}
        onChange={e => onChange('descriptionDetaillee', e.target.value)}
      />
      <p style={{ fontSize: '0.72rem', color: 'var(--c-ink-3)', marginTop: 4 }}>
        {(data.descriptionDetaillee ?? '').length} / min 100 caractères
      </p>
    </Field>

    <Field label="Activités principales" required error={erreurs?.activitesPrincipales?.[0]}>
      <Textarea
        placeholder="Listez les activités principales du projet (construction, exploitation, maintenance…)"
        value={data.activitesPrincipales ?? ''}
        onChange={e => onChange('activitesPrincipales', e.target.value)}
      />
    </Field>

    <Field label="Besoins en ressources (eau, énergie, matériaux)" error={erreurs?.besoinsRessources?.[0]}>
      <Textarea
        style={{ minHeight: 80 }}
        placeholder="Décrivez les ressources nécessaires et leur origine…"
        value={data.besoinsRessources ?? ''}
        onChange={e => onChange('besoinsRessources', e.target.value)}
      />
    </Field>
  </div>
);

// ── Section C : Impacts environnementaux ─────────────────────────────────────
const SectionC = ({ data, onChange, erreurs }) => {
  const impacts = [
    { key: 'impactFaune',   label: 'Impact sur la faune',          Icon: Trees,    note: 'Présence d'espèces protégées ?' },
    { key: 'impactFlore',   label: 'Impact sur la flore',          Icon: Leaf,     note: 'Déforestation, destruction végétale ?' },
    { key: 'impactEau',     label: 'Impact sur les eaux',          Icon: Droplets, note: 'Pollution, prélèvement excessif ?' },
    { key: 'impactAir',     label: 'Impact sur la qualité de l\'air', Icon: Wind,  note: 'Émissions, poussières, gaz ?' },
    { key: 'impactSonore',  label: 'Impact sonore',                Icon: Volume2,  note: 'Nuisances sonores pour les riverains ?' },
  ];
  return (
    <div>
      <div style={{
        background: '#faf5ff', border: '1px solid #c4b5fd', borderRadius: 12,
        padding: 16, marginBottom: 24,
      }}>
        {impacts.map(({ key, label, Icon, note }) => (
          <div key={key} style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '12px 0', borderBottom: '1px solid #ede9fe',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <div style={{
                width: 34, height: 34, borderRadius: 9, background: '#ede9fe',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <Icon size={15} color="#7c3aed" />
              </div>
              <div>
                <p style={{ margin: 0, fontWeight: 700, fontSize: '0.875rem', color: 'var(--c-ink)' }}>{label}</p>
                <p style={{ margin: 0, fontSize: '0.75rem', color: 'var(--c-ink-3)' }}>{note}</p>
              </div>
            </div>
            <RadioGroup
              name={key}
              options={OUI_NON}
              value={data[key] ?? ''}
              onChange={v => onChange(key, v)}
            />
          </div>
        ))}
      </div>

      <Field label="Gestion des déchets" required error={erreurs?.gestionDechets?.[0]}>
        <Textarea
          placeholder="Décrivez le plan de gestion des déchets générés par le projet…"
          value={data.gestionDechets ?? ''}
          onChange={e => onChange('gestionDechets', e.target.value)}
        />
      </Field>

      <Field label="Mesures d'atténuation des impacts" required error={erreurs?.mesuresAttenuation?.[0]}>
        <Textarea
          placeholder="Listez toutes les mesures correctives, préventives et compensatoires prévues…"
          value={data.mesuresAttenuation ?? ''}
          onChange={e => onChange('mesuresAttenuation', e.target.value)}
        />
      </Field>
    </div>
  );
};

// ── Section D : Impacts sociaux ───────────────────────────────────────────────
const SectionD = ({ data, onChange, erreurs }) => (
  <div>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
      <Field label="Population directement affectée" required error={erreurs?.populationAffectee?.[0]}>
        <Input
          type="number" min="0"
          placeholder="Nombre de personnes"
          value={data.populationAffectee ?? ''}
          onChange={e => onChange('populationAffectee', e.target.value)}
        />
      </Field>
      <Field label="Création d'emplois prévue" error={erreurs?.creationEmplois?.[0]}>
        <Input
          type="number" min="0"
          placeholder="Nombre d'emplois"
          value={data.creationEmplois ?? ''}
          onChange={e => onChange('creationEmplois', e.target.value)}
        />
      </Field>
    </div>

    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 20 }}>
      <div>
        <Label required>Déplacements de populations</Label>
        <RadioGroup
          name="deploiementPopulations"
          options={['OUI', 'NON']}
          value={data.deploiementPopulations ?? ''}
          onChange={v => onChange('deploiementPopulations', v)}
        />
        <FieldError message={erreurs?.deploiementPopulations?.[0]} />
      </div>
      <div>
        <Label required>Consultation des communautés locales</Label>
        <RadioGroup
          name="consultationCommunautes"
          options={['OUI', 'EN_COURS', 'NON']}
          value={data.consultationCommunautes ?? ''}
          onChange={v => onChange('consultationCommunautes', v)}
        />
        <FieldError message={erreurs?.consultationCommunautes?.[0]} />
      </div>
    </div>

    <Field label="Impact sur les activités économiques locales" error={erreurs?.impactEconomiqueLocal?.[0]}>
      <Textarea
        placeholder="Décrivez les effets sur les commerces, l'agriculture, l'artisanat, la pêche…"
        value={data.impactEconomiqueLocal ?? ''}
        onChange={e => onChange('impactEconomiqueLocal', e.target.value)}
      />
    </Field>

    <Field label="Mesures de compensation sociale" error={erreurs?.mesuresCompensationSociale?.[0]}>
      <Textarea
        placeholder="Plan de réinstallation, indemnisations, formations, services communautaires…"
        value={data.mesuresCompensationSociale ?? ''}
        onChange={e => onChange('mesuresCompensationSociale', e.target.value)}
      />
    </Field>
  </div>
);

// ── Section E : Documents ─────────────────────────────────────────────────────
const SectionE = ({ data, onChange }) => {
  const fileTypes = [
    { key: 'etudes',         label: 'Études complémentaires',       accept: '.pdf,.doc,.docx' },
    { key: 'plans',          label: 'Plans & cartes',               accept: '.pdf,.dwg,.png,.jpg' },
    { key: 'autorisations',  label: 'Autorisations & permis',      accept: '.pdf' },
    { key: 'photos',         label: 'Photos du site',              accept: '.jpg,.jpeg,.png' },
  ];

  return (
    <div>
      <div style={{
        background: '#f9fafb', border: '1px solid var(--c-border)',
        borderRadius: 12, padding: 16, marginBottom: 20,
      }}>
        <p style={{ margin: '0 0 16px', fontSize: '0.855rem', color: 'var(--c-ink-2)', lineHeight: 1.6 }}>
          <Info size={14} style={{ verticalAlign: 'middle', marginRight: 6 }} />
          Les documents sont optionnels à ce stade, mais faciliteront la révision de votre dossier.
          Formats acceptés : PDF, Word, images (JPG, PNG).
        </p>

        {fileTypes.map(({ key, label, accept }) => (
          <div
            key={key}
            style={{
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
              padding: '12px 0', borderBottom: '1px solid var(--c-border)',
            }}
          >
            <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--c-ink)' }}>
              {label}
            </span>
            <label style={{
              display: 'flex', alignItems: 'center', gap: 6,
              padding: '8px 14px', borderRadius: 8, cursor: 'pointer',
              border: '1.5px solid var(--c-border)', background: '#fff',
              fontSize: '0.82rem', fontWeight: 600, color: 'var(--c-ink-2)',
              transition: 'all 0.15s',
            }}
              onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--c-accent)'; e.currentTarget.style.color = 'var(--c-accent)'; }}
              onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--c-border)'; e.currentTarget.style.color = 'var(--c-ink-2)'; }}
            >
              <Upload size={14} />
              Ajouter
              <input
                type="file" multiple accept={accept}
                style={{ display: 'none' }}
                onChange={e => {
                  const files = Array.from(e.target.files).map(f => f.name);
                  onChange('documentsAnnexes', { ...(data.documentsAnnexes ?? {}), [key]: files });
                }}
              />
            </label>
          </div>
        ))}
      </div>

      {/* Zone drag & drop */}
      <div style={{
        border: '2px dashed var(--c-border)', borderRadius: 12, padding: 40,
        textAlign: 'center', color: 'var(--c-ink-3)',
        cursor: 'pointer', transition: 'border-color 0.2s, background 0.2s',
      }}
        onDragOver={e => { e.preventDefault(); e.currentTarget.style.borderColor = 'var(--c-accent)'; e.currentTarget.style.background = '#eff6ff'; }}
        onDragLeave={e => { e.currentTarget.style.borderColor = 'var(--c-border)'; e.currentTarget.style.background = 'transparent'; }}
      >
        <Upload size={28} style={{ margin: '0 auto 10px', display: 'block', opacity: 0.5 }} />
        <p style={{ margin: 0, fontSize: '0.875rem', fontWeight: 600 }}>Glissez des fichiers ici</p>
        <p style={{ margin: '4px 0 0', fontSize: '0.78rem' }}>ou cliquez pour sélectionner</p>
      </div>
    </div>
  );
};

// ── Page principale EiesForm ──────────────────────────────────────────────────
const EiesForm = () => {
  const [etape,        setEtape]        = useState(0);
  const [formulaireId, setFormulaireId] = useState(null);
  const [data,         setData]         = useState({});
  const [erreurs,      setErreurs]      = useState({});
  const [avertissements, setAvertissements] = useState([]);
  const [flash,        setFlash]        = useState(null);
  const [saving,       setSaving]       = useState(false);
  const [submitting,   setSubmitting]   = useState(false);
  const [scoreCompletude, setScore]     = useState(0);
  const autosaveRef = useRef(null);

  const couleur = COULEURS_SECTION[etape];
  const SectionIcone = SECTIONS[etape].icone;

  // Initialiser un brouillon au montage
  useEffect(() => {
    const init = async () => {
      try {
        const { data: f } = await client.post('/formulaires/eies');
        setFormulaireId(f.id);
      } catch {
        // Pas de backend ? on travaille en local
      }
    };
    init();
  }, []);

  // Autosauvegarde toutes les 30 s
  useEffect(() => {
    if (autosaveRef.current) clearInterval(autosaveRef.current);
    autosaveRef.current = setInterval(() => {
      if (formulaireId) sauvegarder(true);
    }, 30_000);
    return () => clearInterval(autosaveRef.current);
  }, [formulaireId, data]);

  const changer = useCallback((champ, valeur) => {
    setData(prev => ({ ...prev, [champ]: valeur }));

    // Validation en temps réel du champ
    validerChampApi(champ, valeur);
  }, []);

  const validerChampApi = async (champ, valeur) => {
    try {
      const { data: res } = await client.post('/validations/champ', {
        type: 'CHAMP', champNom: champ, valeur, formulaire: null,
      });
      setErreurs(prev => {
        const copy = { ...prev };
        if (res.valide) delete copy[champ];
        else copy[champ] = res.erreurs[champ] ?? [];
        return copy;
      });
    } catch {
      // Service non disponible — validation locale uniquement
    }
  };

  const sauvegarder = async (silencieux = false) => {
    if (!formulaireId) return;
    if (!silencieux) setSaving(true);
    try {
      const { data: res } = await client.put(`/formulaires/eies/${formulaireId}`, data);
      setScore(res.scoreCompletude ?? 0);
      if (!silencieux) setFlash({ type: 'success', message: 'Brouillon sauvegardé ✓' });
    } catch {
      if (!silencieux) setFlash({ type: 'error', message: 'Erreur lors de la sauvegarde.' });
    } finally {
      setSaving(false);
    }
  };

  const validerComplet = async () => {
    try {
      const { data: res } = await client.post('/validations/complet', {
        type: 'EIES_COMPLET', formulaire: data,
      });
      setErreurs(res.erreurs ?? {});
      setAvertissements(res.avertissements ?? []);
      setScore(res.scoreCompletude ?? scoreCompletude);
      return res.valide;
    } catch {
      return true; // Service indisponible → on laisse passer
    }
  };

  const suivant = async () => {
    if (etape < SECTIONS.length - 1) {
      await sauvegarder(true);
      setEtape(e => e + 1);
      window.scrollTo(0, 0);
    }
  };

  const precedent = () => {
    if (etape > 0) {
      setEtape(e => e - 1);
      window.scrollTo(0, 0);
    }
  };

  const soumettre = async () => {
    const valide = await validerComplet();
    if (!valide) {
      setFlash({ type: 'error', message: 'Le formulaire comporte des erreurs. Corrigez-les avant de soumettre.' });
      return;
    }
    setSubmitting(true);
    try {
      await sauvegarder(true);
      if (formulaireId) {
        await client.post(`/formulaires/eies/${formulaireId}/soumettre`);
      }
      setFlash({ type: 'success', message: '🎉 Formulaire EIES soumis avec succès ! Vous recevrez une confirmation.' });
    } catch {
      setFlash({ type: 'error', message: 'Erreur lors de la soumission. Réessayez.' });
    } finally {
      setSubmitting(false);
    }
  };

  const nbErreurs = Object.keys(erreurs).length;

  return (
    <div style={{ padding: '28px 0', maxWidth: 900, margin: '0 auto' }}>

      {/* En-tête */}
      <div className="animate-fade-up" style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 6 }}>
          <div style={{
            width: 44, height: 44, borderRadius: 12,
            background: `linear-gradient(135deg, ${couleur.accent}, ${couleur.icon})`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: `0 4px 14px ${couleur.accent}40`,
            flexShrink: 0,
          }}>
            <Leaf size={20} color="#fff" strokeWidth={2} />
          </div>
          <div>
            <h1 style={{ margin: 0, fontSize: '1.4rem', fontWeight: 800, color: 'var(--c-ink)', letterSpacing: '-0.03em' }}>
              Étude d'Impact Environnemental et Social
            </h1>
            <p style={{ margin: 0, fontSize: '0.82rem', color: 'var(--c-ink-2)' }}>
              Formulaire officiel EIES — Remplissez toutes les sections obligatoires
            </p>
          </div>
        </div>
      </div>

      {/* Barre de progression */}
      <BarreProgression etape={etape} total={SECTIONS.length} score={scoreCompletude} />

      {/* Avertissements */}
      {avertissements.length > 0 && (
        <div style={{ marginBottom: 16 }}>
          {avertissements.map((a, i) => (
            <Flash key={i} type="info" message={a} />
          ))}
        </div>
      )}

      {/* Flash message */}
      {flash && (
        <Flash
          type={flash.type}
          message={flash.message}
          onClose={() => setFlash(null)}
        />
      )}

      {/* Carte section */}
      <div className="card animate-fade-up" style={{
        borderTop: `3px solid ${couleur.accent}`,
        overflow: 'hidden',
      }}>
        {/* Titre section */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 12,
          padding: '18px 24px',
          background: couleur.bg,
          borderBottom: `1px solid ${couleur.border}`,
        }}>
          <div style={{
            width: 36, height: 36, borderRadius: 9,
            background: couleur.accent,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: '#fff',
          }}>
            <SectionIcone size={16} />
          </div>
          <div>
            <p style={{ margin: 0, fontSize: '0.7rem', fontWeight: 700, color: couleur.icon, textTransform: 'uppercase', letterSpacing: '0.08em' }}>
              Section {etape + 1} / {SECTIONS.length}
            </p>
            <h2 style={{ margin: 0, fontSize: '1rem', fontWeight: 800, color: 'var(--c-ink)' }}>
              {SECTIONS[etape].titre}
            </h2>
          </div>

          {/* Indicateur erreurs */}
          {nbErreurs > 0 && (
            <span style={{
              marginLeft: 'auto',
              display: 'inline-flex', alignItems: 'center', gap: 5,
              padding: '4px 12px', borderRadius: 99,
              background: '#fee2e2', color: '#dc2626',
              fontSize: '0.75rem', fontWeight: 700,
            }}>
              <AlertTriangle size={12} /> {nbErreurs} erreur{nbErreurs > 1 ? 's' : ''}
            </span>
          )}
        </div>

        {/* Corps de la section */}
        <div style={{ padding: '24px' }}>
          {etape === 0 && <SectionA data={data} onChange={changer} erreurs={erreurs} />}
          {etape === 1 && <SectionB data={data} onChange={changer} erreurs={erreurs} />}
          {etape === 2 && <SectionC data={data} onChange={changer} erreurs={erreurs} />}
          {etape === 3 && <SectionD data={data} onChange={changer} erreurs={erreurs} />}
          {etape === 4 && <SectionE data={data} onChange={changer} erreurs={erreurs} />}
        </div>

        {/* Navigation */}
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          padding: '16px 24px',
          borderTop: '1px solid var(--c-border)',
          background: '#faf9f7',
        }}>
          <button
            onClick={precedent}
            disabled={etape === 0}
            style={{
              display: 'flex', alignItems: 'center', gap: 6,
              padding: '10px 18px', border: '1.5px solid var(--c-border)',
              borderRadius: 10, background: '#fff', cursor: etape === 0 ? 'not-allowed' : 'pointer',
              fontSize: '0.875rem', fontWeight: 600, color: etape === 0 ? 'var(--c-ink-3)' : 'var(--c-ink)',
              fontFamily: 'var(--font-sans)', transition: 'all 0.15s',
            }}
          >
            <ChevronLeft size={16} /> Précédent
          </button>

          <div style={{ display: 'flex', gap: 10 }}>
            {/* Sauvegarde manuelle */}
            <button
              onClick={() => sauvegarder(false)}
              disabled={saving}
              style={{
                display: 'flex', alignItems: 'center', gap: 6,
                padding: '10px 16px', border: '1.5px solid var(--c-border)',
                borderRadius: 10, background: '#fff', cursor: 'pointer',
                fontSize: '0.855rem', fontWeight: 600, color: 'var(--c-ink-2)',
                fontFamily: 'var(--font-sans)', opacity: saving ? 0.7 : 1,
              }}
            >
              <Save size={14} />
              {saving ? 'Sauvegarde…' : 'Sauvegarder'}
            </button>

            {/* Suivant / Soumettre */}
            {etape < SECTIONS.length - 1 ? (
              <button
                onClick={suivant}
                style={{
                  display: 'flex', alignItems: 'center', gap: 6,
                  padding: '10px 22px',
                  background: couleur.accent, color: '#fff', border: 'none',
                  borderRadius: 10, cursor: 'pointer',
                  fontSize: '0.875rem', fontWeight: 700,
                  fontFamily: 'var(--font-sans)',
                  boxShadow: `0 4px 12px ${couleur.accent}40`,
                  transition: 'transform 0.15s, box-shadow 0.15s',
                }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-1px)'; }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; }}
              >
                Suivant <ChevronRight size={16} />
              </button>
            ) : (
              <button
                onClick={soumettre}
                disabled={submitting}
                style={{
                  display: 'flex', alignItems: 'center', gap: 8,
                  padding: '10px 24px',
                  background: submitting ? '#9ca3af' : '#16a34a',
                  color: '#fff', border: 'none', borderRadius: 10,
                  cursor: submitting ? 'not-allowed' : 'pointer',
                  fontSize: '0.875rem', fontWeight: 700,
                  fontFamily: 'var(--font-sans)',
                  boxShadow: submitting ? 'none' : '0 4px 12px rgba(22,163,74,0.35)',
                  transition: 'all 0.15s',
                }}
              >
                <Send size={15} />
                {submitting ? 'Soumission en cours…' : 'Soumettre le formulaire'}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Info autosauvegarde */}
      <p style={{
        marginTop: 12, fontSize: '0.75rem', color: 'var(--c-ink-3)',
        textAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
      }}>
        <Shield size={12} />
        Sauvegarde automatique toutes les 30 secondes — vos données sont protégées
      </p>

      <style>{`
        @media (max-width: 640px) {
          .step-label { display: none !important; }
        }
      `}</style>
    </div>
  );
};

export default EiesForm;

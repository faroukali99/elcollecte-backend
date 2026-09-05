/**
 * Helpers d'autorisation frontend.
 *
 * Le frontend ne doit JAMAIS être considéré comme la couche de sécurité
 * principale (voir SecurityConfig backend) : ces helpers servent uniquement
 * à adapter l'AFFICHAGE (menus, boutons, routes visibles) au profil de
 * l'utilisateur. Le backend revérifie systématiquement via @PreAuthorize.
 *
 * `user` est l'objet stocké dans le state Redux `auth.user`, tel que
 * renvoyé par AuthResponse.UserInfo :
 *   { id, nom, prenom, email, role, profil, profilLibelle, permissions, organisationId }
 *
 * `role` (legacy) et `permissions` (dynamique) coexistent pendant la
 * migration : hasPermission() vérifie les permissions dynamiques, mais on
 * garde isAdmin() comme filet de sécurité pour ne rien casser tant que
 * tous les profils n'ont pas leurs permissions bien calibrées.
 */

export function hasPermission(user, code) {
  if (!user) return false;
  if (isAdmin(user)) return true; // ADMIN legacy = accès complet, toujours vrai
  return Array.isArray(user.permissions) && user.permissions.includes(code);
}

export function hasAnyPermission(user, codes = []) {
  return codes.some((code) => hasPermission(user, code));
}

export function isAdmin(user) {
  return user?.role === 'ADMIN';
}

export function profilLabel(user) {
  return user?.profilLibelle || user?.profil || user?.role || 'Utilisateur';
}

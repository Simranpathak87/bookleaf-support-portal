function token() { return localStorage.getItem('token'); }
function logout() { localStorage.clear(); location.href = '/index.html'; }
function esc(s) {
  return String(s ?? '').replace(/[&<>"']/g,
    c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}
const money = v => v == null ? 'Not yet available' : '₹' + Number(v).toLocaleString('en-IN');
const val = v => v == null || v === '' ? 'Not yet available' : v;
const dt = v => v ? new Date(v).toLocaleString() : '—';

async function api(path, method = 'GET', body) {
  const headers = { 'Content-Type': 'application/json' };
  if (token()) headers['Authorization'] = 'Bearer ' + token();
  const res = await fetch(path, { method, headers, body: body ? JSON.stringify(body) : undefined });
  if (res.status === 401 && !path.startsWith('/api/auth')) { logout(); throw new Error('Session expired'); }
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const e = new Error(data.error || 'Request failed');
    e.status = res.status; e.data = data;
    throw e;
  }
  return data;
}

function requireRole(role) {
  if (!token() || localStorage.getItem('role') !== role) location.href = '/index.html';
  document.addEventListener('DOMContentLoaded', () => {
    const n = document.getElementById('who');
    if (n) n.textContent = localStorage.getItem('name');
  });
}
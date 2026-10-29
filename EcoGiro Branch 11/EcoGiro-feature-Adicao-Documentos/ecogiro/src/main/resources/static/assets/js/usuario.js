const $ = (s) => document.querySelector(s);
const fmtDate = (v) => v ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(v)) : '—';
const esc = (v = '') => String(v).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
const statusPt = { PENDING:'Pendente', APPROVED:'Aprovado', REJECTED:'Rejeitado', CANCELLED:'Cancelado', ACTIVE:'Ativo', INACTIVE:'Inativo', AVAILABLE:'Disponível', RENTED:'Alugado', MAINTENANCE:'Manutenção' };

async function api(url) {
  const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
  if (!res.ok) throw new Error('Não foi possível carregar os dados da sua conta.');
  return res.json();
}

function badge(status) { return `<span class="badge badge-${esc(status)}">${esc(statusPt[status] || status)}</span>`; }

async function loadDashboard() {
  try {
    const [me, requests, subscriptions, recommendations] = await Promise.all([
      api('/api/user/me'), api('/api/user/requests'), api('/api/user/subscriptions'), api('/api/user/recommendations')
    ]);
    $('#user-first-name').textContent = me.fullName.split(' ')[0];
    $('#profile-list').innerHTML = `
      <div class="profile-item"><small>Nome completo</small><strong>${esc(me.fullName)}</strong></div>
      <div class="profile-item"><small>E-mail</small><strong>${esc(me.email)}</strong></div>
      <div class="profile-item"><small>CPF</small><strong>${esc(me.cpf)}</strong></div>
      <div class="profile-item"><small>Idade</small><strong>${esc(me.age)} anos</strong></div>`;

    const active = subscriptions.find(s => s.status === 'ACTIVE');
    $('#stat-plan').textContent = active ? active.planName : 'Sem plano';
    $('#stat-requests').textContent = requests.length;
    $('#stat-pending').textContent = requests.filter(r => r.status === 'PENDING').length;
    $('#stat-recommendations').textContent = recommendations.length;

    $('#active-subscription').className = active ? 'list-item' : 'empty';
    $('#active-subscription').innerHTML = active ? `
      <div class="list-item-top"><div><h4>${esc(active.planName)}</h4><p>Válido de ${esc(active.startDate)} até ${esc(active.endDate)}</p></div>${badge(active.status)}</div>`
      : 'Você ainda não possui um plano ativo. <a href="planos-aluguel.html"><strong>Escolher um plano</strong></a>';

    $('#request-list').innerHTML = requests.length ? requests.map(r => `
      <article class="list-item"><div class="list-item-top"><div><h4>${esc(r.planName)}</h4><p>Solicitado em ${fmtDate(r.requestedAt)}</p></div>${badge(r.status)}</div>
      ${r.userMessage ? `<p><strong>Sua observação:</strong> ${esc(r.userMessage)}</p>` : ''}
      ${r.adminResponse ? `<p><strong>Resposta EcoGiro:</strong> ${esc(r.adminResponse)}</p>` : ''}</article>`).join('') : '<div class="empty">Nenhuma solicitação feita até agora.</div>';

    $('#recommendation-list').innerHTML = recommendations.length ? recommendations.map(r => `
      <article class="list-item"><div class="list-item-top"><div><h4>${esc(r.principal)}</h4><p>Alternativa: ${esc(r.alternativa)}</p></div><span class="muted">${fmtDate(r.createdAt)}</span></div></article>`).join('')
      : '<div class="empty">Nenhuma recomendação vinculada à sua conta. Faça o quiz depois de entrar.</div>';
  } catch (err) { showToast(err.message, true); }
}

function showToast(message, error = false) {
  const el = $('#toast'); el.textContent = message; el.className = 'toast show' + (error ? ' error' : '');
  setTimeout(() => el.className = 'toast', 3500);
}
loadDashboard();

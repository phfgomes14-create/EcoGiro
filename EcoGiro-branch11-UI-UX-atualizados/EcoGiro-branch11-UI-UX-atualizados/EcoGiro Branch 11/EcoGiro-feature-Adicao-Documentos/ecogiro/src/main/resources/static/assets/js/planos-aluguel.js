const $ = (s) => document.querySelector(s);
const esc = (v = '') => String(v).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
const statusPt = { PENDING:'Pendente', APPROVED:'Aprovado', REJECTED:'Rejeitado', CANCELLED:'Cancelado', ACTIVE:'Ativo', INACTIVE:'Inativo', AVAILABLE:'Disponível', RENTED:'Alugado', MAINTENANCE:'Manutenção' };
const money = (v) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
const fmtDate = (v) => v ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(v)) : '—';
let selectedPlan = null;

async function api(url, options = {}) {
  const res = await fetch(url, { headers: { 'Content-Type':'application/json', 'Accept':'application/json', ...(options.headers || {}) }, ...options });
  const data = res.status === 204 ? null : await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data?.error || 'Não foi possível concluir a operação.');
  return data;
}
function badge(status) { return `<span class="badge badge-${esc(status)}">${esc(statusPt[status] || status)}</span>`; }
function showToast(message, error = false) { const el=$('#toast'); el.textContent=message; el.className='toast show'+(error?' error':''); setTimeout(()=>el.className='toast',3500); }

async function load() {
  try {
    const [plans, requests, subscriptions] = await Promise.all([api('/api/user/plans'), api('/api/user/requests'), api('/api/user/subscriptions')]);
    const active = subscriptions.find(s => s.status === 'ACTIVE');
    $('#plan-notice').innerHTML = active ? `<div class="notice"><strong>Plano ativo:</strong> ${esc(active.planName)} — válido até ${esc(active.endDate)}.</div>` : '';
    $('#plans-grid').innerHTML = plans.length ? plans.map(p => {
      const pending = requests.some(r => r.planId === p.id && r.status === 'PENDING');
      return `<article class="plan-card"><span class="plan-tag">${esc(p.vehicleCategory.replaceAll('_',' '))}</span><h3>${esc(p.name)}</h3><div class="price">${money(p.price)} <small>/ ${p.durationDays} dias</small></div><p>${esc(p.description)}</p><footer><span class="muted">${p.durationDays} dias</span><button class="btn ${pending?'btn-secondary':'btn-primary'} request-plan" data-plan="${p.id}" data-name="${esc(p.name)}" ${pending?'disabled':''}>${pending?'Solicitação pendente':'Solicitar plano'}</button></footer></article>`;
    }).join('') : '<div class="empty">Nenhum plano ativo disponível.</div>';

    $('#requests-list').innerHTML = requests.length ? requests.map(r => `<article class="list-item"><div class="list-item-top"><div><h4>${esc(r.planName)}</h4><p>${fmtDate(r.requestedAt)}</p></div>${badge(r.status)}</div>${r.adminResponse?`<p><strong>Resposta:</strong> ${esc(r.adminResponse)}</p>`:''}${r.status==='PENDING'?`<p><button class="btn btn-danger btn-sm cancel-request" data-id="${r.id}">Cancelar solicitação</button></p>`:''}</article>`).join('') : '<div class="empty">Você ainda não solicitou nenhum plano.</div>';
  } catch (err) { showToast(err.message, true); }
}

document.addEventListener('click', async (e) => {
  const btn = e.target.closest('.request-plan');
  if (btn) { selectedPlan = Number(btn.dataset.plan); $('#request-modal-title').textContent = `Solicitar ${btn.dataset.name}`; $('#request-message').value=''; $('#request-modal').classList.add('open'); }
  const cancel = e.target.closest('.cancel-request');
  if (cancel) { try { await api(`/api/user/requests/${cancel.dataset.id}/cancel`, {method:'POST'}); showToast('Solicitação cancelada.'); load(); } catch(err){ showToast(err.message,true); } }
});
$('#request-cancel').onclick = () => $('#request-modal').classList.remove('open');
$('#request-modal').addEventListener('click', e => { if (e.target.id === 'request-modal') e.currentTarget.classList.remove('open'); });
$('#request-confirm').onclick = async () => {
  if (!selectedPlan) return;
  try { await api('/api/user/requests', { method:'POST', body: JSON.stringify({ planId:selectedPlan, message:$('#request-message').value }) }); $('#request-modal').classList.remove('open'); showToast('Solicitação enviada para a administração.'); await load(); }
  catch(err){ showToast(err.message,true); }
};
load();

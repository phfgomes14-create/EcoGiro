(() => {
  'use strict';
  const $ = id => document.getElementById(id);
  let token, role, stores = [];
  const el = (tag, value) => { const node = document.createElement(tag); if (value !== undefined) node.textContent = value; return node; };
  const option = (value, text) => { const node = el('option', text); node.value = value; return node; };
  async function api(path, body) {
    const response = await fetch(path, { credentials: 'same-origin', redirect: 'error', headers: body === undefined ? {} : {'Content-Type':'application/json', [token.headerName]:token.token}, ...(body === undefined ? {} : {method:'POST',body:JSON.stringify(body)}) });
    if (!response.ok) {
      let detail; try { detail = await response.json(); } catch { /* A resposta pode não ser JSON. */ }
      const message = typeof detail?.error === 'string' ? detail.error : null;
      throw new Error(message || (response.status >= 500
        ? 'Falha no servidor. O cadastro pode ter sido salvo; atualize a página antes de tentar novamente.'
        : `Operação recusada (${response.status}). Confira permissões, capacidade, estado do veículo e dados informados.`));
    }
    const text = await response.text(); return text ? JSON.parse(text) : null;
  }
  const run = async (action, showSuccess=true) => { try { await action(); if(showSuccess) $('message').textContent = 'Operação concluída.'; } catch (error) { $('message').textContent = error.message; } };
  const button = (label, action) => { const b = el('button', label); b.type = 'button'; b.onclick = () => run(async () => { b.disabled=true; try { await action(); } finally { b.disabled=false; } }); return b; };
  function cell(row, node) { const td=el('td'); td.append(typeof node === 'string' ? document.createTextNode(node) : node); row.append(td); }
  async function loadFleet() {
    stores = await api('/api/admin/operations/stores');
    const fleet = await api('/api/admin/operations/vehicles');
    $('stores').replaceChildren(); $('vehicles').replaceChildren();
    for (const s of stores) {
      const row=el('tr'); cell(row, `${s.name} / ${s.sectorName}${s.active ? '' : ' (inativa)'}`);
      const limits=el('div'); limits.append(el('span', `${s.occupancy} / `)); const capacity=el('input'); capacity.type='number'; capacity.min='1'; capacity.max='10000'; capacity.value=s.capacity; capacity.setAttribute('aria-label', `Capacidade de ${s.name}`); limits.append(capacity); cell(row,limits);
      const blocked=el('input'); blocked.type='checkbox'; blocked.checked=s.blocked; blocked.setAttribute('aria-label', `Bloquear ${s.name}`); cell(row,blocked);
      cell(row,button('Salvar limites',async()=>{ const n=Number(capacity.value); if(!Number.isInteger(n)||n<1||n>10000) throw new Error('Capacidade deve ser um inteiro entre 1 e 10000.'); await api(`/api/admin/operations/stores/${s.id}/limits`,{capacity:n,blocked:blocked.checked}); await loadFleet(); })); $('stores').append(row);
    }
    for (const v of fleet) {
      const row=el('tr'); cell(row,`${v.code} — ${v.model}`); const state=el('select'); state.setAttribute('aria-label',`Estado de ${v.code}`);
      for (const name of ['AVAILABLE','MAINTENANCE','INACTIVE',...(v.status==='RENTED'?['RENTED']:[])]) state.append(option(name, {AVAILABLE:'Disponível',MAINTENANCE:'Manutenção',INACTIVE:'Inativo',RENTED:'Alugado'}[name])); state.value=v.status; state.disabled=v.status==='RENTED'; cell(row,state);
      cell(row,stores.find(s=>s.id===v.storeId)?.name || 'Sem loja atribuída'); const target=el('select'); target.setAttribute('aria-label',`Destino de ${v.code}`); target.append(option('','Selecione uma loja'));
      for(const s of stores.filter(s=>s.active&&!s.blocked&&(s.occupancy<s.capacity||s.id===v.storeId))) target.append(option(s.id,`${s.name} (${s.occupancy}/${s.capacity})`)); cell(row,target);
      const actions=el('div'); const transfer=button('Transferir',async()=>{if(!target.value) throw new Error('Selecione uma loja de destino.');await api(`/api/admin/operations/vehicles/${v.id}/move`,{storeId:Number(target.value)});await loadFleet();}); transfer.disabled=v.status==='RENTED'; const save=button('Salvar estado',async()=>{await api(`/api/admin/operations/vehicles/${v.id}/status`,{status:state.value});await loadFleet();}); save.disabled=v.status==='RENTED'; actions.append(transfer,save);cell(row,actions);$('vehicles').append(row);
    }
    $('message').textContent = stores.length ? 'Dados atualizados. As operações respeitam seus setores autorizados.' : 'Nenhuma loja acessível. O administrador geral precisa cadastrar setores/lojas e aprovar a atribuição dos setores.';
  }
  async function loadGeneral() {
    const sectors=await api('/api/admin/sectors');
    for(const id of ['sector-select','proposal-sector']) { $(id).replaceChildren(option('','Selecione um setor')); for(const s of sectors.filter(s=>s.active)) $(id).append(option(s.id,`${s.name} (#${s.id})`)); }
    const pending=await api('/api/admin/redistribution-proposals/pending'); $('pending').replaceChildren();
    for(const p of pending) {const row=el('p',`#${p.id}: ${p.sectorName} → administrador #${p.toAdministratorId}. ${p.reason} `);for(const action of ['approve','reject']) row.append(button(action==='approve'?'Aprovar':'Rejeitar',async()=>{await api(`/api/admin/redistribution-proposals/${p.id}/${action}`,{});await loadGeneral();}));$('pending').append(row);}
    if(!pending.length) $('pending').append(el('p','Nenhuma proposta pendente.'));
  }
  const form = (id,path,numbers,refresh) => {
    let submitting=false;
    $(id).onsubmit=event=>{
      event.preventDefault(); if(submitting) return;
      const target=event.target, submit=target.querySelector('button');
      submitting=true; submit.disabled=true;
      run(async()=>{
        try {
          const data=Object.fromEntries(new FormData(target));for(const key of numbers)data[key]=Number(data[key]);
          await api(path,data); target.reset();
          try { await refresh(); } catch { throw new Error('Cadastro salvo, mas a atualização da tela falhou. Recarregue a página; não envie novamente.'); }
        } finally { submitting=false;submit.disabled=false; }
      });
    };
  };
  run(async()=>{
    role=(await api('/api/session/profile')).role;
    if(!['ADMIN','ADMIN_GERAL','ADMIN_SETORIAL'].includes(role)) {location.replace('/login.html');return;}
    if(role==='ADMIN_SETORIAL') $('panel').href='/admin-setorial.html';
    token=await api('/api/admin/operations/csrf'); await loadFleet();
    if(role==='ADMIN_GERAL') {
      $('general').hidden=false;await loadGeneral();
      form('sector-form','/api/admin/operations/sectors',[],loadGeneral);
      form('store-form','/api/admin/operations/stores',['sectorId','latitude','longitude','capacity'],loadFleet);
      form('proposal-form','/api/admin/redistribution-proposals',['sectorId','toAdministratorId'],loadGeneral);
      $('preview').onclick=()=>run(async()=>{const preview=await api('/api/admin/sector-distribution/preview');$('suggestions').replaceChildren(el('p',preview.calculationBasis));for(const s of preview.suggestions){const row=el('p',`${s.sectorName} → administrador #${s.toAdministratorId}. ${s.reason} `);row.append(button('Criar proposta',async()=>{await api('/api/admin/redistribution-proposals',{sectorId:s.sectorId,toAdministratorId:s.toAdministratorId,reason:s.reason});await loadGeneral();}));$('suggestions').append(row);}});
    }
  }, false);
})();
const $ = (s) => document.querySelector(s);
const esc = (v = '') => String(v).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
let vehicles = [], currentFilter = 'ALL', markers = [];
const map = L.map('vehicle-map', { scrollWheelZoom: true }).setView([-15.7939, -47.8828], 11);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);
const typeLabel = { MOTO:'Moto', BIKE:'Bicicleta', E_BIKE:'E-bike', SCOOTER:'Patinete' };
const statusLabel = { AVAILABLE:'Disponível', RENTED:'Alugado', MAINTENANCE:'Manutenção', INACTIVE:'Inativo' };

async function loadVehicles(){
  try { const res=await fetch('/api/map/vehicles'); if(!res.ok) throw new Error('Não foi possível carregar a frota.'); vehicles=await res.json(); render(); }
  catch(err){ showToast(err.message,true); }
}
function filtered(){ return vehicles.filter(v => currentFilter==='ALL' || v.type===currentFilter || v.status===currentFilter); }
function render(){
  markers.forEach(m => map.removeLayer(m)); markers=[]; const items=filtered();
  const bounds=[];
  items.forEach(v => {
    const available=v.status==='AVAILABLE';
    const marker=L.circleMarker([v.latitude,v.longitude], { radius:10, weight:3, fillOpacity:.88, color: available?'#16863d':'#68776d', fillColor: available?'#26a653':'#9aa69e' }).addTo(map);
    marker.bindPopup(`<strong>${esc(v.model)}</strong><br>${esc(v.code)} · ${esc(typeLabel[v.type]||v.type)}<br>${esc(v.locationName)}<br><strong>${esc(statusLabel[v.status]||v.status)}</strong>`);
    markers.push(marker); bounds.push([v.latitude,v.longitude]);
  });
  if(bounds.length) map.fitBounds(bounds,{padding:[35,35],maxZoom:13});
  $('#map-count').textContent=`${items.length} veículo(s) exibido(s)`;
  $('#map-list').innerHTML=items.length?items.map(v=>`<article class="list-item"><div class="list-item-top"><div><h4>${esc(v.model)}</h4><p>${esc(v.code)} · ${esc(typeLabel[v.type]||v.type)}</p></div><span class="badge badge-${esc(v.status)}">${esc(statusLabel[v.status]||v.status)}</span></div><p><strong>${esc(v.locationName)}</strong><br>${v.latitude.toFixed(5)}, ${v.longitude.toFixed(5)}</p></article>`).join(''):'<div class="empty">Nenhum veículo neste filtro.</div>';
}
$('#map-filter').addEventListener('click', e=>{ const btn=e.target.closest('button[data-type]'); if(!btn)return; $('#map-filter').querySelectorAll('button').forEach(b=>b.classList.remove('active')); btn.classList.add('active'); currentFilter=btn.dataset.type; render(); });
function showToast(message,error=false){const el=$('#toast');el.textContent=message;el.className='toast show'+(error?' error':'');setTimeout(()=>el.className='toast',3500)}
loadVehicles();

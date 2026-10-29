const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');const vm=require('node:vm');
const base='EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/static/';
async function setup(rows, stores=[]) {
 const elements=new Map();const el=s=>{if(!elements.has(s))elements.set(s,{value:'ALL',textContent:'',innerHTML:'',className:'',listeners:{},addEventListener(n,fn){this.listeners[n]=fn;}});return elements.get(s);};
 const markers=[],calls=[];const map={removeLayer(){},fitBounds(){},setView(){return this;}};
 const L={map:()=>map,tileLayer:()=>({addTo(){}}),divIcon:x=>x,marker:(coords,options)=>{const m={coords,options,on(n,fn){this[n]=fn;return this;},addTo(){markers.push(this);return this;},bindPopup(){return this;}};return m;}};
 const context={document:{querySelector:el},L,setTimeout(){},fetch:async(url,opts)=>{calls.push({url,opts});let data=url.endsWith('/profile')?{role:'ADMIN_SETORIAL'}:url.endsWith('/csrf')?{headerName:'X-XSRF-TOKEN',token:'fixture-token'}:url.endsWith('/inventory')?rows:url.endsWith('/stores')?stores:{};return {ok:true,json:async()=>data};}};
 vm.runInNewContext(fs.readFileSync(base+'assets/js/estoque.js','utf8'),context);
 await new Promise(resolve=>setImmediate(resolve));return {el,markers,calls};
}
const vehicle=(extra={})=>({id:1,code:'ECO1',model:'Bike',type:'BIKE',status:'AVAILABLE',locationName:'Centro',latitude:-15,longitude:-47,storeId:1,...extra});
test('mapa agrupa veículos por localização e ignora coordenadas inválidas',async()=>{
 const h=await setup([vehicle(),vehicle({id:2,latitude:-17,longitude:-49}),vehicle({id:3,latitude:null}),vehicle({id:4,latitude:95})]);
 assert.equal(h.markers.length,1);assert.deepEqual(Array.from(h.markers[0].coords),[-16,-48]);
});
test('clique no marcador seleciona localização sem apagar filtros',async()=>{
 const h=await setup([vehicle(),vehicle({id:2,locationName:'Outro'})]);h.el('#type-filter').value='BIKE';h.markers[0].click();
 assert.equal(h.el('#sector-select').value,'Centro');assert.equal(h.el('#type-filter').value,'BIKE');assert.match(h.el('#inventory-caption').textContent,/Centro/);
 assert.equal(h.el('#admin-home').href,'admin-setorial.html');
});
test('rótulos escapam HTML e destinos lotados ou bloqueados não aparecem',async()=>{
 const h=await setup([vehicle({locationName:'<img src=x>'})],[{id:2,name:'Livre',active:true,blocked:false,occupancy:0,capacity:2},{id:3,name:'Lotada',active:true,occupancy:2,capacity:2},{id:4,name:'Bloqueada',active:true,blocked:true,occupancy:0,capacity:2}]);
 assert.ok(h.markers[0].options.icon.html.includes('&lt;img'));assert.ok(!h.markers[0].options.icon.html.includes('<img'));
 assert.match(h.el('#inventory-list').innerHTML,/Livre/);assert.doesNotMatch(h.el('#inventory-list').innerHTML,/Lotada|Bloqueada/);
});
test('salvar utiliza POST com CSRF e endpoint territorial',async()=>{
 const h=await setup([vehicle()]);h.el('[data-status-id="1"]').value='MAINTENANCE';h.el('[data-store-id="1"]').value='';
 const button={dataset:{id:'1'}};await h.el('#inventory-list').listeners.click({target:{closest:()=>button}});
 const write=h.calls.find(c=>c.opts.method==='POST');assert.equal(write.url,'/api/admin/operations/vehicles/1/status');assert.equal(write.opts.headers['X-XSRF-TOKEN'],'fixture-token');
 assert.deepEqual(JSON.parse(write.opts.body),{status:'MAINTENANCE'});assert.ok(!h.calls.some(c=>c.url==='/api/admin/vehicles'));
});
test('proxy encaminha estoque e alias ao backend com resposta protegida',async()=>{
 const mod=await import('data:text/javascript;base64,'+Buffer.from(fs.readFileSync(base+'_worker.js')).toString('base64'));
 const original=global.fetch;global.fetch=async req=>{assert.equal(new URL(req.url).pathname,'/estoque.html');return new Response('denied',{status:403});};
 try{for(const path of ['/estoque','/estoque.html']){const r=await mod.default.fetch(new Request('https://ecogiro.pages.dev'+path),{});assert.equal(r.status,403);assert.equal(r.headers.get('Cache-Control'),'no-store');}}finally{global.fetch=original;}
});

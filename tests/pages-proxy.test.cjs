const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const code=fs.readFileSync('EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/static/_worker.js','utf8');
const worker=import('data:text/javascript;base64,'+Buffer.from(code).toString('base64'));
test('login administrativo preserva POST, corpo, cookie e redirecionamento',async()=>{
 const original=global.fetch;
 global.fetch=async request=>{
  assert.equal(request.url,'https://ecogiro.onrender.com/admin/login');
  assert.equal(request.method,'POST');assert.equal(request.headers.get('Cookie'),'JSESSIONID=test');
  assert.equal(await request.text(),'username=test&password=fixture');
  return new Response(null,{status:302,headers:{Location:'https://ecogiro.onrender.com/admin.html','Set-Cookie':'JSESSIONID=new; Path=/; HttpOnly'}});
 };
 try {
 const r=await (await worker).default.fetch(new Request('https://ecogiro.pages.dev/admin/login',{method:'POST',body:'username=test&password=fixture',headers:{Cookie:'JSESSIONID=test'}}),{});
 assert.equal(r.status,302);assert.equal(r.headers.get('Location'),'https://ecogiro.pages.dev/admin.html');assert.ok(r.headers.get('Set-Cookie').includes('JSESSIONID=new'));
 }finally{global.fetch=original;}
});
test('assets permanecem no Pages',async()=>{const r=await(await worker).default.fetch(new Request('https://ecogiro.pages.dev/assets/css/style.css'),{ASSETS:{fetch:()=>new Response('css')}});assert.equal(await r.text(),'css');});
test('página administrativa exige resposta do backend',async()=>{const original=global.fetch;global.fetch=async req=>{assert.equal(new URL(req.url).pathname,'/mapa-admin.html');return new Response('denied',{status:403});};try{const r=await(await worker).default.fetch(new Request('https://ecogiro.pages.dev/mapa-admin.html'),{});assert.equal(r.status,403);}finally{global.fetch=original;}});

test('logout preserva cookie e invalidação retornada pelo backend',async()=>{const original=global.fetch;global.fetch=async req=>{assert.equal(req.method,'POST');assert.equal(req.headers.get('Cookie'),'JSESSIONID=test');assert.equal(new URL(req.url).pathname,'/logout');return new Response(null,{status:302,headers:{Location:'/login.html?logout=1','Set-Cookie':'JSESSIONID=; Max-Age=0; Path=/'}});};try{const r=await(await worker).default.fetch(new Request('https://ecogiro.pages.dev/logout',{method:'POST',headers:{Cookie:'JSESSIONID=test'}}),{});assert.equal(r.headers.get('Location'),'https://ecogiro.pages.dev/login.html?logout=1');assert.match(r.headers.get('Set-Cookie'),/Max-Age=0/);assert.equal(r.headers.get('Cache-Control'),'no-store');}finally{global.fetch=original;}});
test('alias da operação territorial passa pelo backend',async()=>{const original=global.fetch;global.fetch=async req=>{assert.equal(new URL(req.url).pathname,'/operacao.html');return new Response('login',{status:302,headers:{Location:'/login.html'}});};try{const r=await(await worker).default.fetch(new Request('https://ecogiro.pages.dev/operacao'),{});assert.equal(r.status,302);}finally{global.fetch=original;}});

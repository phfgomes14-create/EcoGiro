const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const vm=require('node:vm');
const code=fs.readFileSync('EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/static/assets/js/operacao.js','utf8');
const flush=()=>new Promise(resolve=>setImmediate(resolve));
async function setup(post, refreshFails=false) {
 const nodes=new Map();const node=()=>({textContent:'',disabled:false,children:[],append(...children){this.children.push(...children);},replaceChildren(...children){this.children=children;},setAttribute(){}});
 const get=id=>{if(!nodes.has(id))nodes.set(id,node());return nodes.get(id);};
 let writes=0;
 const fetch=async (path,options={})=>{
   if(options.method==='POST'){writes++;return post();}
   if(path==='/api/session/profile')return Response.json({role:'ADMIN_GERAL'});
   if(path==='/api/admin/operations/csrf')return Response.json({headerName:'X-XSRF-TOKEN',token:'fixture'});
   if(refreshFails&&writes&&path==='/api/admin/sectors')return new Response('',{status:500});
   return Response.json([]);
 };
 const context={document:{getElementById:get,createElement:()=>node(),createTextNode:text=>({textContent:text})},fetch,location:{replace(){throw new Error('Unexpected redirect');}},FormData:class {constructor(){return [['code','DF-CENTRO'],['name','Centro']];}}};
 vm.runInNewContext(code,context);await flush();assert.equal(typeof get('sector-form').onsubmit,'function');
 const submit=node();const target={querySelector:()=>submit,reset(){}};
 return {get,submit,writes:()=>writes,event:{preventDefault(){},target}};
}
test('cliques repetidos geram um envio e conflito exibe mensagem segura',async()=>{
 let release;const state=await setup(()=>new Promise(resolve=>release=resolve));
 state.get('sector-form').onsubmit(state.event);state.get('sector-form').onsubmit(state.event);
 assert.equal(state.writes(),1);assert.equal(state.submit.disabled,true);
 const message='Já existe um setor com esse código. <script>fixture</script>';
 release(Response.json({error:message},{status:409}));await flush();
 assert.equal(state.get('message').textContent,message);assert.equal(state.submit.disabled,false);
});
test('falha de atualização após salvar orienta recarregar sem reenviar',async()=>{
 const state=await setup(()=>Promise.resolve(new Response(null,{status:200})),true);
 state.get('sector-form').onsubmit(state.event);await flush();
 assert.equal(state.writes(),1);assert.match(state.get('message').textContent,/Cadastro salvo/);assert.match(state.get('message').textContent,/não envie novamente/);assert.equal(state.submit.disabled,false);
});

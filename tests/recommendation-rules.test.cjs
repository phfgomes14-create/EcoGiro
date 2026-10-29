const {test}=require('node:test');
const assert=require('node:assert/strict');
const {rank}=require('../EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/static/assets/js/recommendation-rules.js');
const answers=profiles=>profiles.map(profile=>({profile}));
for(const [name,input,expected] of [
 ['entregas e preferências moto',['moto','moto','moto','moto','moto'],['moto','bicicleta','ebike','patinete']],
 ['perfil bicicleta',['bicicleta','bicicleta','bicicleta','bicicleta','bicicleta'],['bicicleta','moto','ebike','patinete']],
 ['perfil elétrico',['ebike','ebike','ebike','ebike','ebike'],['ebike','moto','bicicleta','patinete']],
 ['perfil patinete',['patinete','patinete','patinete','patinete','patinete'],['patinete','moto','bicicleta','ebike']],
 ['empate moto e bike',['moto','bicicleta','ebike','moto','bicicleta'],['moto','bicicleta','ebike','patinete']],
 ['empate elétricos',['ebike','patinete','ebike','patinete','bicicleta'],['ebike','patinete','bicicleta','moto']]
])test(name,()=>assert.deepEqual(rank(answers(input)),expected));
test('1024 combinações mantêm ranking original, determinismo e entrada intacta',()=>{
 const order=['moto','bicicleta','ebike','patinete'];
 for(let n=0;n<1024;n++){
  let value=n;const input=Array.from({length:5},()=>{const profile=order[value%4];value=Math.floor(value/4);return {profile};});
  const original=JSON.stringify(input),scores=Object.fromEntries(order.map(p=>[p,0]));input.forEach(a=>scores[a.profile]++);
  const expected=[...order].sort((a,b)=>scores[b]-scores[a]);
  assert.deepEqual(rank(input),expected);assert.deepEqual(rank(input),rank(input));assert.equal(JSON.stringify(input),original);
 }
});
test('entrada incompleta ou inválida é recusada',()=>{
 assert.throws(()=>rank([]));assert.throws(()=>rank(answers(['moto','moto','moto','moto','__proto__'])));
});

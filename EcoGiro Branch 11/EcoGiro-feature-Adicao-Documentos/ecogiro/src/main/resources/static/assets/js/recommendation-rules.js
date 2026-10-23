(function(root,factory){
  if(typeof module==='object' && module.exports) module.exports=factory();
  else root.EcoGiroRecommendationRules=factory();
})(typeof globalThis!=='undefined'?globalThis:this,function(){
  'use strict';
  const order=['moto','bicicleta','ebike','patinete'];
  function rank(answers){
    if(!Array.isArray(answers)||answers.length!==5) throw new Error('Responda às cinco perguntas.');
    const scores=Object.fromEntries(order.map(profile=>[profile,0]));
    for(const answer of answers){
      if(!answer||!order.includes(answer.profile))throw new Error('Perfil de resposta inválido.');
      scores[answer.profile]++;
    }
    return [...order].sort((a,b)=>scores[b]-scores[a]||order.indexOf(a)-order.indexOf(b));
  }
  return {rank};
});

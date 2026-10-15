const { test } = require('node:test');
const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const vm = require('node:vm');
const root = 'EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/static/';
const source = readFileSync(root + 'assets/js/mapa.js', 'utf8').split('// O perfil vem da sessão, nunca de tentativas de acesso a APIs administrativas.')[1].replace('configureMapNavigation();', '');
async function navigate(role, page, response = {}) {
  const link = { href: 'admin.html', textContent: 'Administração' };
  const state = { link, loads: 0, redirect: null, error: null };
  const context = vm.createContext({
    document: { body: { dataset: { mapProfile: page } }, getElementById: () => link },
    window: { location: { replace: value => state.redirect = value } },
    fetch: async url => { assert.equal(url, '/api/session/profile'); return { ok: true, status: 200, json: async () => ({ role }), ...response }; },
    loadVehicles: async () => state.loads++, showToast: message => state.error = message
  });
  vm.runInContext(source, context);
  await context.configureMapNavigation();
  return state;
}
for (const role of ['ADMIN', 'ADMIN_GERAL', 'ADMIN_SETORIAL']) {
  test(role + ' mantém mapa administrativo e painel correto', async () => {
    const result = await navigate(role, 'ADMIN');
    assert.equal(result.redirect, null); assert.equal(result.loads, 1);
    assert.equal(result.link.href, role === 'ADMIN_SETORIAL' ? 'admin-setorial.html' : 'admin.html');
  });
  test(role + ' em URL antiga vai ao mapa administrativo', async () => {
    const result = await navigate(role, 'USER');
    assert.equal(result.redirect, '/mapa-admin.html'); assert.equal(result.loads, 0);
  });
}
test('USER mantém mapa de usuário', async () => { assert.equal((await navigate('USER', 'USER')).loads, 1); });
test('USER não carrega mapa administrativo', async () => { const r = await navigate('USER', 'ADMIN'); assert.equal(r.redirect, '/mapa.html'); assert.equal(r.loads, 0); });
test('falha de API não transforma administrador em usuário', async () => { const r = await navigate(null, 'ADMIN', { ok: false, status: 500 }); assert.equal(r.redirect, null); assert.equal(r.loads, 0); assert.ok(r.error); });
test('sessão expirada exige login administrativo', async () => { assert.equal((await navigate(null, 'ADMIN', { ok: false, status: 401 })).redirect, '/admin-login.html'); });
test('HTML administrativo não contém links de usuário', () => { const html = readFileSync(root + 'mapa-admin.html', 'utf8'); assert.ok(!html.includes('href="usuario.html"')); assert.ok(!html.includes('href="planos-aluguel.html"')); });

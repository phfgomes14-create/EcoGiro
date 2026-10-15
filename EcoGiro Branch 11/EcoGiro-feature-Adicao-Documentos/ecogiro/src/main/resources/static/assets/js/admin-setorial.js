const statusElement = document.getElementById('sector-status');
const container = document.getElementById('sectors');
(async function loadAssignedSectors() {
  try {
    const response = await fetch('/api/admin/sectors', { credentials: 'same-origin', headers: {Accept:'application/json'} });
    if (!response.ok) throw new Error(response.status === 403 ? 'Sua conta não possui acesso aos setores.' : 'Não foi possível carregar os setores.');
    const sectors = await response.json();
    statusElement.textContent = sectors.length ? sectors.length + ' setor(es) atribuído(s)' : 'Nenhum setor atribuído. Aguarde a distribuição aprovada pelo administrador geral.';
    for (const sector of sectors) {
      const card = document.createElement('article');
      card.className = 'sector-card';
      const name = document.createElement('h2');
      name.textContent = sector.name;
      const code = document.createElement('p');
      code.textContent = 'Código: ' + sector.code;
      card.append(name, code);
      container.append(card);
    }
  } catch(error) { statusElement.textContent = error.message; }
})();

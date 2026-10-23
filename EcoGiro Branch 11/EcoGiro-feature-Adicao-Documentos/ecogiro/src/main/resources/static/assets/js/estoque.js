(function () {
  const $ = (selector) => document.querySelector(selector);
  const esc = (value) =>
    String(value ?? "").replace(/[&<>'"]/g, (character) => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      "'": "&#39;",
      '"': "&quot;",
    })[character]);
  const statusText = {
    AVAILABLE: "Disponível",
    RENTED: "Alugado",
    MAINTENANCE: "Manutenção",
    INACTIVE: "Inativo",
  };
  const typeText = {
    MOTO: "Moto",
    BIKE: "Bicicleta",
    E_BIKE: "E-bike",
    SCOOTER: "Patinete",
  };
  let all = [];
  let map;
  let stores = [];
  let csrf;
  let marks = [];

  function toast(message, isError) {
    const element = $("#toast");
    element.textContent = message;
    element.className = "toast show" + (isError ? " error" : "");
    setTimeout(() => (element.className = "toast"), 3500);
  }

  async function api(url, options) {
    const response = await fetch(url, {
      credentials: "same-origin",
      redirect: "error",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        ...(csrf ? { [csrf.headerName]: csrf.token } : {}),
      },
      ...options,
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw Error(
        data.message || data.error || "Não foi possível concluir a operação.",
      );
    }
    return data;
  }

  function sectors() {
    const select = $("#sector-select");
    const previous = $("#sector-select").value || "ALL";
    const names = [
      ...new Set(all.map((vehicle) => vehicle.locationName).filter(Boolean)),
    ].sort((a, b) => a.localeCompare(b, "pt-BR"));
    select.innerHTML =
      '<option value="ALL">Todos os setores</option>' +
      names
        .map((name) => '<option value="' + esc(name) + '">' + esc(name) + "</option>")
        .join("");
    select.value = names.includes(previous) ? previous : "ALL";
  }

  function items() {
    return all.filter(
      (vehicle) =>
        ($("#sector-select").value === "ALL" ||
          vehicle.locationName === $("#sector-select").value) &&
        ($("#type-filter").value === "ALL" ||
          vehicle.type === $("#type-filter").value) &&
        ($("#status-filter").value === "ALL" ||
          vehicle.status === $("#status-filter").value),
    );
  }

  function sectorPoints(rows) {
    const points = new Map();
    rows.forEach((vehicle) => {
      if (
        !vehicle.locationName ||
        !Number.isFinite(vehicle.latitude) ||
        !Number.isFinite(vehicle.longitude) ||
        Math.abs(vehicle.latitude) > 90 || Math.abs(vehicle.longitude) > 180
      ) {
        return;
      }
      const point = points.get(vehicle.locationName) || {
        latitude: 0,
        longitude: 0,
        count: 0,
      };
      point.latitude += vehicle.latitude;
      point.longitude += vehicle.longitude;
      point.count += 1;
      points.set(vehicle.locationName, point);
    });
    return [...points.entries()].map(([name, point]) => ({
      name,
      latitude: point.latitude / point.count,
      longitude: point.longitude / point.count,
      count: point.count,
    }));
  }

  function render() {
    const rows = items();
    const sector = $("#sector-select").value;
    $("#inventory-caption").textContent =
      (sector === "ALL" ? "Todos os setores" : sector) +
      " · " +
      rows.length +
      " veículo(s) exibido(s)";
    const sectorVehicles =
      sector === "ALL"
        ? all
        : all.filter((vehicle) => vehicle.locationName === sector);
    $("#sector-total").textContent = sectorVehicles.length;
    $("#sector-available").textContent = sectorVehicles.filter(
      (vehicle) => vehicle.status === "AVAILABLE",
    ).length;
    $("#sector-rented").textContent = sectorVehicles.filter(
      (vehicle) => vehicle.status === "RENTED",
    ).length;
    $("#sector-maintenance").textContent = sectorVehicles.filter(
      (vehicle) => vehicle.status === "MAINTENANCE",
    ).length;
    $("#inventory-list").innerHTML = rows.length
      ? rows
          .map(
            (vehicle) =>
              '<article class="inventory-item"><div class="inventory-item-top"><div><h3>' +
              esc(vehicle.model) +
              "</h3><p>" +
              esc(vehicle.code) +
              " · " +
              esc(typeText[vehicle.type] || vehicle.type) +
              " · " +
              esc(vehicle.locationName) +
              '</p></div><span class="badge badge-' +
              esc(vehicle.status) +
              '">' +
              esc(statusText[vehicle.status] || vehicle.status) +
              '</span></div><div class="inventory-item-controls"><select aria-label="Status de ' +
              esc(vehicle.code) +
              '" data-status-id="' +
              vehicle.id +
              '">' +
              Object.entries(statusText).filter(([key]) => key !== "RENTED" || vehicle.status === "RENTED")
                .map(([key, text]) => '<option value="' + key + '" ' + (vehicle.status === key ? "selected" : "") + '>' + text + '</option>').join("") +
              '</select><select class="sector-move" aria-label="Loja de destino de ' + esc(vehicle.code) + '" data-store-id="' + vehicle.id + '" ' + (vehicle.status === "RENTED" ? "disabled" : "") + '>' +
              '<option value="">Manter loja atual</option>' + stores.filter(store => store.id !== vehicle.storeId && store.active && !store.blocked && store.occupancy < store.capacity)
                .map(store => '<option value="' + store.id + '">' + esc(store.name) + ' (' + store.occupancy + '/' + store.capacity + ')</option>').join("") +
              '</select><button class="btn btn-primary save-status" data-id="' + vehicle.id + '" ' + (vehicle.status === "RENTED" ? "disabled" : "") + '>Salvar</button></div><span class="sector-hint">Transferências respeitam lojas, setores, bloqueios e capacidade. Veículos alugados não podem ser alterados aqui.</span></article>',
          )
          .join("")
      : '<div class="empty">Nenhum veículo corresponde aos filtros selecionados.</div>';

    if (!map) return;
    marks.forEach((marker) => map.removeLayer(marker));
    marks = [];
    const points = sectorPoints(rows);
    points.forEach((point) => {
      const marker = L.marker([point.latitude, point.longitude], {
        icon: L.divIcon({
          className: "inventory-sector-icon",
          html:
            '<span class="inventory-sector-pin"><span class="inventory-sector-dot"></span><span class="inventory-sector-name">' +
            esc(point.name) +
            "</span></span>",
          iconSize: [1, 1],
          iconAnchor: [0, 0],
        }),
      }).addTo(map);
      marker.bindPopup(
        "<strong>" +
          esc(point.name) +
          "</strong><br>" +
          point.count +
          " veículo(s)<br>Clique no ponto para ver este setor.",
      );
      marker.on("click", () => {
        $("#sector-select").value = point.name;

        render();
      });
      marks.push(marker);
    });
    if (points.length) {
      map.fitBounds(
        points.map((point) => [point.latitude, point.longitude]),
        { padding: [36, 36], maxZoom: 13 },
      );
    }
  }

  async function load() {
    try {
      const data = await Promise.all([api("/api/admin/operations/inventory"), api("/api/admin/operations/stores")]);
      all = data[0];
      stores = data[1];
      sectors();
      render();
    } catch (error) {
      toast(error.message, true);
    }
  }

  $("#sector-select").addEventListener("change", () => {

    render();
  });
  $("#type-filter").addEventListener("change", render);
  $("#status-filter").addEventListener("change", render);
  $("#refresh-inventory").addEventListener("click", load);
  $("#inventory-list").addEventListener("click", async (event) => {
    const button = event.target.closest(".save-status");
    if (!button) return;
    const vehicle = all.find((item) => item.id === Number(button.dataset.id));
    const status = $(
      '[data-status-id="' + button.dataset.id + '"]',
    ).value;
    const storeId = $('[data-store-id="' + button.dataset.id + '"]').value;
    if (!vehicle) return;

    button.disabled = true;
    try {
      // Cada operação é transacional no servidor. Atualiza o status antes de uma transferência opcional.
      if (status !== vehicle.status) await api('/api/admin/operations/vehicles/' + vehicle.id + '/status', {
        method: 'POST', body: JSON.stringify({ status })
      });
      if (storeId) await api('/api/admin/operations/vehicles/' + vehicle.id + '/move', {
        method: 'POST', body: JSON.stringify({ storeId: Number(storeId) })
      });
      toast("Operações de estoque concluídas.");
      await load();
    } catch (error) {
      toast(error.message + " As operações são independentes; confira os dados atualizados.", true);
      await load();
    } finally {
      button.disabled = false;
    }
  });

  async function init() {
    try {
      const profile = await api('/api/session/profile');
      $('#admin-home').href = profile.role === 'ADMIN_SETORIAL' ? 'admin-setorial.html' : 'admin.html';
      csrf = await api('/api/admin/operations/csrf');
      if (typeof L !== 'undefined') {
        map = L.map("inventory-map").setView([-15.7939, -47.8828], 11);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { maxZoom: 19, attribution: "&copy; OpenStreetMap" }).addTo(map);
      } else {
        $('#inventory-map').textContent = 'O mapa não carregou. Os filtros e a lista continuam disponíveis.';
      }
      await load();
    } catch (error) {
      $('#inventory-list').textContent = error.message;
      toast(error.message, true);
    }
  }
  init();
})();

import { FormEvent, useEffect, useMemo, useState } from "react";

type Fridge = {
  id: number;
  name: string;
  temperatureC: number;
  mode: string;
  note: string;
  country: string;
  city: string;
  latitude: number;
  longitude: number;
  firmwareCode: string;
};
type FirmwareState = {
  devices: Record<
    string,
    { temperatureC: number; mode: string; status: string }
  >;
};
type Route = "dashboard" | "devices" | "register" | "firmware";
const auth = (user: string) => ({
  Authorization: `Basic ${btoa(`${user}:${user === "teacher" ? "laboratorio-teacher" : "laboratorio-student"}`)}`,
});
const emptyForm = {
  name: "",
  temperatureC: "4",
  mode: "ECO",
  note: "",
  country: "Brasil",
  city: "São Paulo",
  latitude: "-23.550520",
  longitude: "-46.633308",
  firmwareCode:
    "// FridgeLab firmware example\nvoid setup() { pinMode(LED_BUILTIN, OUTPUT); }\nvoid loop() { readTemperature(); maintainTarget(); delay(1000); }",
};
export default function App() {
  const [route, setRoute] = useState<Route>(
      (location.hash.slice(1) as Route) || "dashboard",
    ),
    [user, setUser] = useState("teacher"),
    [devices, setDevices] = useState<Fridge[]>([]),
    [firmware, setFirmware] = useState<FirmwareState>({ devices: {} }),
    [selected, setSelected] = useState<Fridge | null>(null),
    [search, setSearch] = useState(""),
    [message, setMessage] = useState(""),
    [form, setForm] = useState<any>(emptyForm);
  const headers = auth(user);
  const navigate = (next: Route) => {
    location.hash = next;
    setRoute(next);
    setMessage("");
  };
  const load = async () => {
    const [a, b] = await Promise.all([
      fetch(`/api/devices?name=${encodeURIComponent(search)}`, { headers }),
      fetch("/api/firmware/state", { headers }),
    ]);
    if (a.ok) setDevices(await a.json());
    if (b.ok) setFirmware(await b.json());
  };
  useEffect(() => {
    const fn = () => setRoute((location.hash.slice(1) as Route) || "dashboard");
    addEventListener("hashchange", fn);
    void load();
    return () => removeEventListener("hashchange", fn);
  }, [user]);
  const online = useMemo(
    () =>
      devices.filter(
        (d) => firmware.devices[String(d.id)]?.status === "applied",
      ).length,
    [devices, firmware],
  );
  const edit = (d: Fridge) => {
    setSelected(d);
    setForm({
      name: d.name,
      temperatureC: String(d.temperatureC),
      mode: d.mode,
      note: d.note,
      country: d.country,
      city: d.city,
      latitude: String(d.latitude),
      longitude: String(d.longitude),
      firmwareCode: d.firmwareCode,
    });
  };
  const save = async (e: FormEvent) => {
    e.preventDefault();
    setMessage("Salvando...");
    const body = {
      ...form,
      temperatureC: Number(form.temperatureC),
      latitude: Number(form.latitude),
      longitude: Number(form.longitude),
    };
    const r = await fetch(
      selected ? `/api/devices/${selected.id}` : "/api/devices",
      {
        method: selected ? "PUT" : "POST",
        headers: { ...headers, "Content-Type": "application/json" },
        body: JSON.stringify(body),
      },
    );
    const data = await r.json().catch(() => ({}));
    setMessage(
      r.ok ? "Registro salvo com sucesso." : data.error || "Falha ao salvar.",
    );
    if (r.ok) {
      await load();
      if (!selected) setForm(emptyForm);
    }
  };
  const deleteDevice = async (d: Fridge) => {
    if (!confirm(`Excluir ${d.name}?`)) return;
    const r = await fetch(`/api/devices/${d.id}`, {
      method: "DELETE",
      headers,
    });
    if (r.ok) {
      setMessage("Dispositivo excluído.");
      setSelected(null);
      await load();
    }
  };
  const configure = async (d: Fridge) => {
    const r = await fetch(`/api/devices/${d.id}/configuration`, {
      method: "PUT",
      headers: { ...headers, "Content-Type": "application/json" },
      body: JSON.stringify({
        temperatureC: d.temperatureC,
        mode: d.mode,
        note: d.note,
      }),
    });
    setMessage(
      r.ok ? "Configuração enviada ao firmware." : "Falha no firmware.",
    );
    await load();
  };
  const updateCode = async () => {
    if (!selected) return;
    const r = await fetch(`/api/devices/${selected.id}/firmware`, {
      method: "PUT",
      headers: { ...headers, "Content-Type": "application/json" },
      body: JSON.stringify({ code: form.firmwareCode }),
    });
    setMessage(r.ok ? "Código salvo." : "Falha ao salvar código.");
    await load();
  };
  const title =
    route === "dashboard"
      ? "Mapa operacional"
      : route === "devices"
        ? "Dispositivos"
        : route === "register"
          ? "Cadastrar geladeira"
          : "Laboratório de firmware";
  return (
    <div className="app">
      <aside className="sidebar">
        <div className="logo">
          FRIDGE<span>LAB</span>
        </div>
        <p className="caption">SIMULADOR IoT DIDÁTICO</p>
        <nav>
          {(
            [
              ["dashboard", "Visão geral"],
              ["devices", "Dispositivos"],
              ["register", "Cadastrar"],
              ["firmware", "Firmware"],
            ] as [Route, string][]
          ).map(([key, label]) => (
            <button
              className={route === key ? "active" : ""}
              onClick={() => navigate(key)}
              key={key}
            >
              <span>
                {key === "dashboard"
                  ? "◈"
                  : key === "devices"
                    ? "▦"
                    : key === "register"
                      ? "+"
                      : "⌁"}
              </span>
              {label}
            </button>
          ))}
        </nav>
        <div className="userbox">
          <small>USUÁRIO DO LAB</small>
          <select value={user} onChange={(e) => setUser(e.target.value)}>
            <option value="teacher">teacher</option>
            <option value="student">student</option>
          </select>
          <p>Dados fictícios. Execução somente local.</p>
        </div>
      </aside>
      <main>
        <header className="topbar">
          <div>
            <span className="kicker">FRIDGE LAB / {route.toUpperCase()}</span>
            <h1>{title}</h1>
            <p>Monitoramento seguro de uma frota de geladeiras simuladas.</p>
          </div>
          <div className="health">
            <i /> SISTEMA OPERACIONAL
          </div>
        </header>
        {route === "dashboard" && (
          <Dashboard
            devices={devices}
            online={online}
            firmware={firmware}
            onSelect={(d) => {
              edit(d);
              navigate("firmware");
            }}
          />
        )}
        {route === "devices" && (
          <Devices
            devices={devices}
            search={search}
            setSearch={setSearch}
            load={load}
            onEdit={edit}
            onDelete={deleteDevice}
            onConfigure={configure}
          />
        )}{" "}
        {route === "register" && (
          <Editor
            title="Nova geladeira"
            form={form}
            setForm={setForm}
            onSubmit={save}
            onCancel={() => navigate("devices")}
          />
        )}{" "}
        {route === "firmware" && (
          <Firmware
            devices={devices}
            selected={selected}
            onSelect={edit}
            form={form}
            setForm={setForm}
            firmware={firmware}
            onSaveCode={updateCode}
            onConfigure={configure}
          />
        )}{" "}
        {message && <div className="toast">{message}</div>}
      </main>
    </div>
  );
}
function Dashboard({
  devices,
  online,
  firmware,
  onSelect,
}: {
  devices: Fridge[];
  online: number;
  firmware: FirmwareState;
  onSelect: (d: Fridge) => void;
}) {
  return (
    <>
      <div className="metrics">
        <div>
          <small>DISPOSITIVOS VISÍVEIS</small>
          <strong>{devices.length}</strong>
          <em>neste usuário</em>
        </div>
        <div>
          <small>ATIVOS NO FIRMWARE</small>
          <strong>{online}</strong>
          <em>estado aplicado</em>
        </div>
        <div>
          <small>PAÍSES MONITORADOS</small>
          <strong>{new Set(devices.map((d) => d.country)).size}</strong>
          <em>rotas simuladas</em>
        </div>
        <div>
          <small>ALERTA</small>
          <strong>
            {
              devices.filter((d) => d.temperatureC < -15 || d.temperatureC > 12)
                .length
            }
          </strong>
          <em>fora da faixa ideal</em>
        </div>
      </div>
      <section className="panel map-panel">
        <div className="section-head">
          <div>
            <span className="kicker">GEOLOCALIZAÇÃO SIMULADA</span>
            <h2>Mapa da frota</h2>
          </div>
          <span className="badge">{devices.length} pontos</span>
        </div>
        <div className="map">
          <div className="map-lines" />
          {devices.map((d) => (
            <button
              title={`${d.name} · ${d.city}`}
              className="pin"
              style={{
                left: `${Math.min(92, Math.max(7, (d.longitude + 125) / 2.5))}%`,
                top: `${Math.min(88, Math.max(10, 55 - d.latitude / 2))}%`,
              }}
              onClick={() => onSelect(d)}
              key={d.id}
            >
              <i />
              <span>{d.city}</span>
            </button>
          ))}
        </div>
      </section>
      <div className="grid two">
        <section className="panel">
          <div className="section-head">
            <h2>Últimos dispositivos</h2>
            <button className="link" onClick={() => navigateHash("devices")}>
              Ver lista →
            </button>
          </div>
          {devices.slice(0, 5).map((d) => (
            <div className="row-item" key={d.id}>
              <span className="avatar">{d.name.slice(0, 1)}</span>
              <div>
                <b>{d.name}</b>
                <small>
                  {d.city}, {d.country}
                </small>
              </div>
              <strong>{d.temperatureC}°C</strong>
            </div>
          ))}
        </section>
        <section className="panel">
          <div className="section-head">
            <h2>Firmware</h2>
            <span className="live">● LIVE</span>
          </div>
          {devices.slice(0, 4).map((d) => (
            <div className="row-item" key={d.id}>
              <div>
                <b>{d.name}</b>
                <small>
                  {firmware.devices[String(d.id)]?.status || "pendente"}
                </small>
              </div>
              <span className="mode">{d.mode}</span>
            </div>
          ))}
        </section>
      </div>
    </>
  );
}
const navigateHash = (r: Route) => {
  location.hash = r;
};
function Devices({
  devices,
  search,
  setSearch,
  load,
  onEdit,
  onDelete,
  onConfigure,
}: {
  devices: Fridge[];
  search: string;
  setSearch: (s: string) => void;
  load: () => void;
  onEdit: (d: Fridge) => void;
  onDelete: (d: Fridge) => void;
  onConfigure: (d: Fridge) => void;
}) {
  return (
    <section className="panel screen">
      <div className="section-head">
        <div>
          <span className="kicker">INVENTÁRIO</span>
          <h2>Todos os dispositivos</h2>
        </div>
        <form
          className="search"
          onSubmit={(e) => {
            e.preventDefault();
            void load();
          }}
        >
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar por nome..."
          />
          <button>Buscar</button>
        </form>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Dispositivo</th>
              <th>Localização</th>
              <th>Temperatura</th>
              <th>Modo</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {devices.map((d) => (
              <tr key={d.id}>
                <td>
                  <b>{d.name}</b>
                  <small>#{d.id}</small>
                </td>
                <td>
                  {d.city}
                  <small>
                    {d.country} · {d.latitude.toFixed(3)},{" "}
                    {d.longitude.toFixed(3)}
                  </small>
                </td>
                <td>
                  <strong>{d.temperatureC} °C</strong>
                </td>
                <td>
                  <span className="mode">{d.mode}</span>
                </td>
                <td className="actions">
                  <button onClick={() => onEdit(d)}>Editar</button>
                  <button onClick={() => onConfigure(d)}>Aplicar</button>
                  <button className="danger" onClick={() => onDelete(d)}>
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
function Editor({
  title,
  form,
  setForm,
  onSubmit,
  onCancel,
}: {
  title: string;
  form: any;
  setForm: (f: any) => void;
  onSubmit: (e: FormEvent) => void;
  onCancel: () => void;
}) {
  const change = (k: string, v: string) => setForm({ ...form, [k]: v });
  return (
    <section className="panel screen">
      <span className="kicker">CADASTRO</span>
      <h2>{title}</h2>
      <form className="editor" onSubmit={onSubmit}>
        <label>
          Nome
          <input
            required
            value={form.name}
            onChange={(e) => change("name", e.target.value)}
          />
        </label>
        <label>
          País
          <input
            required
            value={form.country}
            onChange={(e) => change("country", e.target.value)}
          />
        </label>
        <label>
          Cidade
          <input
            required
            value={form.city}
            onChange={(e) => change("city", e.target.value)}
          />
        </label>
        <label>
          Temperatura alvo
          <input
            type="number"
            min="-20"
            max="15"
            value={form.temperatureC}
            onChange={(e) => change("temperatureC", e.target.value)}
          />
        </label>
        <label>
          Modo
          <select
            value={form.mode}
            onChange={(e) => change("mode", e.target.value)}
          >
            <option>ECO</option>
            <option>NORMAL</option>
            <option>TURBO</option>
          </select>
        </label>
        <label>
          Latitude
          <input
            type="number"
            step="0.000001"
            value={form.latitude}
            onChange={(e) => change("latitude", e.target.value)}
          />
        </label>
        <label>
          Longitude
          <input
            type="number"
            step="0.000001"
            value={form.longitude}
            onChange={(e) => change("longitude", e.target.value)}
          />
        </label>
        <label className="wide">
          Nota
          <textarea
            value={form.note}
            onChange={(e) => change("note", e.target.value)}
          />
        </label>
        <label className="wide">
          Código do firmware (C++)
          <textarea
            className="code"
            value={form.firmwareCode}
            onChange={(e) => change("firmwareCode", e.target.value)}
          />
        </label>
        <div className="form-actions">
          <button className="secondary" type="button" onClick={onCancel}>
            Cancelar
          </button>
          <button type="submit">Salvar dispositivo</button>
        </div>
      </form>
    </section>
  );
}
function Firmware({
  devices,
  selected,
  onSelect,
  form,
  setForm,
  firmware,
  onSaveCode,
  onConfigure,
}: {
  devices: Fridge[];
  selected: Fridge | null;
  onSelect: (d: Fridge) => void;
  form: any;
  setForm: (f: any) => void;
  firmware: FirmwareState;
  onSaveCode: () => void;
  onConfigure: (d: Fridge) => void;
}) {
  return (
    <section className="firmware-screen">
      <div className="panel selector">
        <span className="kicker">FIRMWARE LAB</span>
        <h2>Selecione uma geladeira</h2>
        <div className="firmware-list">
          {devices.map((d) => (
            <button
              className={selected?.id === d.id ? "selected" : ""}
              onClick={() => onSelect(d)}
              key={d.id}
            >
              <span className="avatar">{d.id}</span>
              <div>
                <b>{d.name}</b>
                <small>
                  {d.city} · {d.mode} ·{" "}
                  {firmware.devices[String(d.id)]?.status || "pendente"}
                </small>
              </div>
              <span>›</span>
            </button>
          ))}
        </div>
      </div>
      <div className="panel code-panel">
        {selected ? (
          <>
            <div className="section-head">
              <div>
                <span className="kicker">COMPILADOR SIMULADO</span>
                <h2>{selected.name}</h2>
              </div>
              <span className="badge">C++ / ESP32</span>
            </div>
            <p className="helper">
              Edite o sketch didático e salve no dispositivo simulado. A
              aplicação da temperatura usa o endpoint protegido do firmware.
            </p>
            <textarea
              className="code big"
              value={form.firmwareCode}
              onChange={(e) =>
                setForm({ ...form, firmwareCode: e.target.value })
              }
            />
            <div className="code-actions">
              <button onClick={onSaveCode}>Salvar código</button>
              <button
                className="secondary"
                onClick={() => onConfigure(selected)}
              >
                Enviar configuração atual
              </button>
            </div>
            <div className="terminal">
              <span>● firmware simulator</span>
              <code>{`device_${selected.id} > ${firmware.devices[String(selected.id)]?.status || "waiting"}\ntelemetry.temperature = ${firmware.devices[String(selected.id)]?.temperatureC ?? selected.temperatureC}C\nmode = ${firmware.devices[String(selected.id)]?.mode ?? selected.mode}`}</code>
            </div>
          </>
        ) : (
          <div className="empty">
            <b>Nenhuma geladeira selecionada</b>
            <span>
              Escolha um item à esquerda para abrir o laboratório de firmware.
            </span>
          </div>
        )}
      </div>
    </section>
  );
}

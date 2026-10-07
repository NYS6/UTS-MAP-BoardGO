import { createFileRoute } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { Search, SlidersHorizontal, X } from "lucide-react";
import { Chip, Empty, Screen, SessionRow, TopBar, btn, btnGhost } from "@/components/bg";
import { games, getGame, getVenue, pastSessionIds, venues } from "@/lib/data";
import { defaultFilters, useStore, type Filters } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/discover")({
  head: () => meta("Discover", "Search and filter board game sessions across BSD, Gading Serpong and beyond."),
  component: Discover,
});

const areas = ["All", ...new Set(venues.map((v) => v.area))];

function Discover() {
  const { sessions, filters, setFilters } = useStore();
  const [q, setQ] = useState("");
  const [sheet, setSheet] = useState(false);
  const list = useMemo(() => sessions.filter((s) => {
    if (pastSessionIds.includes(s.id)) return false;
    const g = getGame(s.gameId), v = getVenue(s.venueId);
    if (q && !`${g.name} ${v.name} ${v.area}`.toLowerCase().includes(q.toLowerCase())) return false;
    if (filters.area !== "All" && v.area !== filters.area) return false;
    if (filters.games.length && !filters.games.includes(s.gameId)) return false;
    if (filters.level !== "Any" && s.level !== filters.level) return false;
    if (filters.day !== "Any" && s.day !== filters.day) return false;
    return true;
  }), [sessions, q, filters]);
  const active = (filters.area !== "All" ? 1 : 0) + filters.games.length + (filters.level !== "Any" ? 1 : 0) + (filters.day !== "Any" ? 1 : 0);

  return (
    <Screen>
      <TopBar title="Discover" />
      <div className="flex gap-2 px-5">
        <label className="flex flex-1 items-center gap-2 rounded-full bg-surface px-4 ring-1 ring-border focus-within:ring-2 focus-within:ring-primary">
          <Search className="size-4 text-muted-foreground" />
          <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search games or venues" className="w-full bg-transparent py-3 text-sm outline-none" />
          {q && <button onClick={() => setQ("")} aria-label="Clear"><X className="size-4" /></button>}
        </label>
        <button onClick={() => setSheet(true)} aria-label="Filters" className="relative grid size-12 shrink-0 place-items-center rounded-full bg-primary text-primary-foreground">
          <SlidersHorizontal className="size-4" />
          {active > 0 && <span className="absolute -top-1 -right-1 grid size-5 place-items-center rounded-full bg-meeple text-[10px] font-bold text-foreground">{active}</span>}
        </button>
      </div>
      <div className="no-scrollbar flex gap-2 overflow-x-auto px-5 pt-3 pb-2">
        {areas.map((a) => <Chip key={a} active={filters.area === a} onClick={() => setFilters({ ...filters, area: a })}>{a}</Chip>)}
      </div>
      <p className="px-5 pb-2 font-mono text-[11px] text-muted-foreground">{list.length} tables found</p>
      {list.length ? list.map((s, i) => <SessionRow key={s.id} s={s} delay={i * 40} />) : (
        <Empty title="No tables" body="Try a different search or loosen your filters." action={<button className={btnGhost} onClick={() => { setQ(""); setFilters(defaultFilters); }}>Reset filters</button>} />
      )}
      {sheet && <FilterSheet initial={filters} onClose={() => setSheet(false)} onApply={(f) => { setFilters(f); setSheet(false); }} />}
    </Screen>
  );
}

function FilterSheet({ initial, onClose, onApply }: { initial: Filters; onClose: () => void; onApply: (f: Filters) => void }) {
  const [f, setF] = useState(initial);
  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-foreground/40 backdrop-blur-sm" onClick={onClose}>
      <div className="sheet-up w-full max-w-[430px] rounded-t-[28px] bg-surface p-5 pb-8" onClick={(e) => e.stopPropagation()}>
        <div className="mx-auto mb-4 h-1.5 w-12 rounded-full bg-muted" />
        <h2 className="font-display text-2xl uppercase">Filters</h2>
        <Group label="Games">{games.map((g) => <Chip key={g.id} active={f.games.includes(g.id)} onClick={() => setF({ ...f, games: f.games.includes(g.id) ? f.games.filter((x) => x !== g.id) : [...f.games, g.id] })}>{g.name}</Chip>)}</Group>
        <Group label="Area">{areas.map((a) => <Chip key={a} active={f.area === a} onClick={() => setF({ ...f, area: a })}>{a}</Chip>)}</Group>
        <Group label="Level">{["Any", "Beginner", "Casual", "Competitive"].map((l) => <Chip key={l} active={f.level === l} onClick={() => setF({ ...f, level: l })}>{l}</Chip>)}</Group>
        <Group label="When">{["Any", "Tonight", "Tomorrow", "Sat", "Sun"].map((d) => <Chip key={d} active={f.day === d} onClick={() => setF({ ...f, day: d })}>{d}</Chip>)}</Group>
        <div className="mt-6 grid grid-cols-2 gap-3">
          <button className={btnGhost} onClick={() => setF(defaultFilters)}>Reset</button>
          <button className={btn} onClick={() => onApply(f)}>Show results</button>
        </div>
      </div>
    </div>
  );
}

function Group({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="mt-4">
      <p className="mb-2 font-mono text-[11px] uppercase text-muted-foreground">{label}</p>
      <div className="flex flex-wrap gap-2">{children}</div>
    </div>
  );
}

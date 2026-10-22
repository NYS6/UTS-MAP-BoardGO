import { createFileRoute, Link } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { Crosshair, List, Minus, Plus } from "lucide-react";
import { toast } from "sonner";
import { Chip, Empty, Screen, SessionRow, TopBar, btnGhost } from "@/components/bg";
import { MAP_KM, getGame, getVenue, pastSessionIds, sessionsWithin, venuePoint, venues } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/nearby")({
  head: () => meta("Nearby map", "See board game tables hosted near you on a map and filter them by distance."),
  component: Nearby,
});

const radii = [1, 2, 3, 5, 10];

function Nearby() {
  const { sessions, filters, setFilters } = useStore();
  const [zoom, setZoom] = useState(1);
  const [picked, setPicked] = useState<string | null>(null);
  const km = filters.maxKm;
  const live = sessions.filter((s) => !pastSessionIds.includes(s.id));
  const list = useMemo(() => sessionsWithin(live, km), [live, km]);
  const venueIds = new Set(list.map((s) => s.venueId));
  const pickedSessions = picked ? list.filter((s) => s.venueId === picked) : [];
  const radiusPct = (km / MAP_KM) * 100;

  return (
    <Screen>
      <TopBar back title="Nearby tables" right={<Link to="/discover" aria-label="List view" className="grid size-10 place-items-center rounded-full bg-surface ring-1 ring-border"><List className="size-4" /></Link>} />

      <div className="relative mx-5 aspect-square overflow-hidden rounded-[24px] ring-1 ring-border">
        <div className="absolute inset-0 transition-transform duration-300" style={{ transform: `scale(${zoom})` }}>
          <MapArt />
          <div className="absolute rounded-full border-2 border-primary/60 bg-primary/10 transition-all duration-300"
            style={{ left: `${50 - radiusPct}%`, top: `${50 - radiusPct}%`, width: `${radiusPct * 2}%`, height: `${radiusPct * 2}%` }} />
          {venues.map((v) => {
            const p = venuePoint(v);
            const on = venueIds.has(v.id);
            const count = list.filter((s) => s.venueId === v.id).length;
            return (
              <button key={v.id} onClick={() => on && setPicked(v.id)} aria-label={v.name}
                className={cn("absolute -translate-x-1/2 -translate-y-full transition-opacity", on ? "opacity-100" : "opacity-35")}
                style={{ left: `${p.x}%`, top: `${p.y}%`, transform: `translate(-50%,-100%) scale(${1 / zoom})`, transformOrigin: "bottom center" }}>
                <span className={cn("grid size-8 place-items-center rounded-full rounded-br-none rotate-45 shadow-lg ring-2 ring-surface", picked === v.id ? "bg-foreground" : on ? "bg-primary" : "bg-muted-foreground")}>
                  <span className="-rotate-45 text-[11px] font-bold text-primary-foreground">{on ? count : "·"}</span>
                </span>
              </button>
            );
          })}
          <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2" style={{ transform: `translate(-50%,-50%) scale(${1 / zoom})` }}>
            <span className="absolute inset-0 animate-ping rounded-full bg-sky" />
            <span className="relative block size-4 rounded-full bg-sky ring-4 ring-surface" />
          </span>
        </div>

        <div className="absolute right-3 top-3 flex flex-col overflow-hidden rounded-xl bg-surface shadow ring-1 ring-border">
          <button aria-label="Zoom in" onClick={() => setZoom((z) => Math.min(2.5, z + 0.5))} className="grid size-9 place-items-center"><Plus className="size-4" /></button>
          <span className="h-px bg-border" />
          <button aria-label="Zoom out" onClick={() => setZoom((z) => Math.max(1, z - 0.5))} className="grid size-9 place-items-center"><Minus className="size-4" /></button>
        </div>
        <button aria-label="Recenter" onClick={() => { setZoom(1); setPicked(null); toast.success("Centered on your location"); }} className="absolute bottom-3 right-3 grid size-9 place-items-center rounded-xl bg-surface shadow ring-1 ring-border"><Crosshair className="size-4" /></button>
        <span className="absolute bottom-3 left-3 rounded-full bg-surface/90 px-3 py-1 font-mono text-[10px] ring-1 ring-border">Within {km} km · {list.length} tables</span>
      </div>

      <div className="px-5 pt-4">
        <p className="mb-2 font-mono text-[11px] uppercase text-muted-foreground">Distance from you</p>
        <div className="no-scrollbar flex gap-2 overflow-x-auto">
          {radii.map((r) => <Chip key={r} active={km === r} onClick={() => { setFilters({ ...filters, maxKm: r }); setPicked(null); }}>{r} km</Chip>)}
        </div>
      </div>

      {picked ? (
        <div className="sheet-up mx-5 mt-4 rounded-2xl bg-surface p-4 ring-1 ring-border">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-display text-lg uppercase leading-none">{getVenue(picked).name}</p>
              <p className="mt-1 text-[12px] text-muted-foreground">{getVenue(picked).area} · {getVenue(picked).distance} km away</p>
            </div>
            <button className="font-mono text-[11px] text-primary" onClick={() => setPicked(null)}>show all</button>
          </div>
          <div className="-mx-5 mt-3">{pickedSessions.map((s) => <SessionRow key={s.id} s={s} />)}</div>
        </div>
      ) : (
        <div className="pt-4">
          {list.length ? list.map((s, i) => (
            <div key={s.id} className="relative">
              <SessionRow s={s} delay={i * 30} />
              <span className="pointer-events-none absolute right-16 top-3 rounded-full bg-secondary px-2 py-0.5 font-mono text-[10px]">{getVenue(s.venueId).distance} km</span>
            </div>
          )) : (
            <Empty title="Nothing this close" body={`No tables within ${km} km. Try a wider distance.`} action={<button className={btnGhost} onClick={() => setFilters({ ...filters, maxKm: 10 })}>Show 10 km</button>} />
          )}
        </div>
      )}
      <p className="sr-only">{list.map((s) => getGame(s.gameId).name).join(", ")}</p>
    </Screen>
  );
}

/** Stylised street map drawn locally (mock — no map service needed). */
function MapArt() {
  return (
    <svg viewBox="0 0 100 100" className="absolute inset-0 size-full" preserveAspectRatio="none" aria-hidden>
      <rect width="100" height="100" fill="var(--color-secondary)" />
      <path d="M0 78 C20 70 30 88 55 80 S85 70 100 76 L100 100 L0 100Z" fill="var(--color-sky)" opacity="0.35" />
      <rect x="8" y="10" width="18" height="14" rx="3" fill="var(--color-success)" opacity="0.25" />
      <rect x="68" y="30" width="20" height="12" rx="3" fill="var(--color-success)" opacity="0.25" />
      <circle cx="30" cy="58" r="7" fill="var(--color-success)" opacity="0.2" />
      <g stroke="var(--color-surface)" strokeLinecap="round" fill="none">
        <path d="M0 50 H100" strokeWidth="3" />
        <path d="M50 0 V100" strokeWidth="3" />
        <path d="M0 15 L100 65" strokeWidth="2.2" />
        <path d="M10 100 L90 0" strokeWidth="2.2" />
        {[20, 35, 65, 80].map((v) => <path key={"h" + v} d={`M0 ${v} H100`} strokeWidth="1" />)}
        {[15, 30, 70, 85].map((v) => <path key={"v" + v} d={`M${v} 0 V100`} strokeWidth="1" />)}
      </g>
      <path d="M0 50 H100 M50 0 V100" stroke="var(--color-meeple)" strokeWidth="0.6" opacity="0.7" />
    </svg>
  );
}

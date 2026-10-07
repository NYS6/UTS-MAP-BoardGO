import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { Check } from "lucide-react";
import { Chip, Screen, TopBar, btn, btnGhost, input } from "@/components/bg";
import { games, getGame, getVenue, venues, type Session } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/create")({
  head: () => meta("Host a game", "Create a board game session and invite players nearby."),
  component: Create,
});

const steps = ["Game", "Date", "Venue", "Players", "Details"];

function Create() {
  const { create } = useStore();
  const [step, setStep] = useState(0);
  const [done, setDone] = useState<string | null>(null);
  const [f, setF] = useState({ gameId: "", day: "Tonight", time: "19:30", venueId: "", max: 4, level: "Casual" as Session["level"], note: "", fee: "Free" });
  const ok = [f.gameId, f.day && f.time, f.venueId, f.max >= 2, true][step];

  if (done) {
    const g = getGame(f.gameId), v = getVenue(f.venueId);
    return (
      <Screen>
        <div className="flex flex-col items-center px-6 pt-16 text-center">
          <div className="rise grid size-20 place-items-center rounded-3xl bg-success text-primary-foreground shadow-lg"><Check className="size-10" /></div>
          <h1 className="rise mt-6 font-display text-[40px] uppercase leading-[0.9]">Table<br />is live!</h1>
          <p className="mt-3 text-sm text-muted-foreground">{g.name} · {f.day} {f.time} · {v.name}</p>
          <img src={g.image} alt={g.name} loading="lazy" className="mt-6 h-40 w-full rounded-3xl object-cover" />
          <div className="mt-6 flex w-full flex-col gap-3">
            <Link to="/session/$id" params={{ id: done }} className={btn}>View session</Link>
            <Link to="/my-games" className={btnGhost}>Go to My Games</Link>
          </div>
        </div>
      </Screen>
    );
  }

  return (
    <Screen>
      <TopBar title="Host a game" />
      <div className="flex gap-1.5 px-5">
        {steps.map((s, i) => <div key={s} className={cn("h-1.5 flex-1 rounded-full", i <= step ? "bg-primary" : "bg-muted")} />)}
      </div>
      <p className="px-5 pt-3 font-mono text-[11px] uppercase text-muted-foreground">Step {step + 1}/5 · {steps[step]}</p>

      <div key={step} className="rise px-5 pt-4">
        {step === 0 && (
          <div className="grid grid-cols-2 gap-3">
            {games.map((g) => (
              <button key={g.id} onClick={() => setF({ ...f, gameId: g.id })} className={cn("overflow-hidden rounded-2xl bg-surface text-left ring-2 transition", f.gameId === g.id ? "ring-primary" : "ring-transparent")}>
                <img src={g.image} alt={g.name} loading="lazy" className="h-24 w-full object-cover" />
                <div className="p-2.5"><p className="truncate font-display uppercase leading-none">{g.name}</p><p className="mt-1 font-mono text-[10px] text-muted-foreground">{g.players} · {g.duration}</p></div>
              </button>
            ))}
          </div>
        )}
        {step === 1 && (
          <div className="space-y-5">
            <div className="flex flex-wrap gap-2">{["Tonight", "Tomorrow", "Sat", "Sun"].map((d) => <Chip key={d} active={f.day === d} onClick={() => setF({ ...f, day: d })}>{d}</Chip>)}</div>
            <div className="flex flex-wrap gap-2">{["13:00", "16:00", "18:00", "19:30", "21:00"].map((t) => <Chip key={t} active={f.time === t} onClick={() => setF({ ...f, time: t })}>{t}</Chip>)}</div>
          </div>
        )}
        {step === 2 && (
          <div className="space-y-2">
            {venues.map((v) => (
              <button key={v.id} onClick={() => setF({ ...f, venueId: v.id })} className={cn("flex w-full items-center justify-between rounded-2xl bg-surface p-4 text-left ring-2", f.venueId === v.id ? "ring-primary" : "ring-transparent")}>
                <div><p className="font-semibold">{v.name}</p><p className="text-xs text-muted-foreground">{v.area}</p></div>
                <span className="font-mono text-[11px] text-muted-foreground">{v.distance} km</span>
              </button>
            ))}
          </div>
        )}
        {step === 3 && (
          <div className="space-y-6">
            <div className="flex items-center justify-between rounded-2xl bg-surface p-4 ring-1 ring-border">
              <span className="font-semibold">Max players</span>
              <div className="flex items-center gap-4">
                <button className="size-10 rounded-full bg-muted font-display text-xl" onClick={() => setF({ ...f, max: Math.max(2, f.max - 1) })}>−</button>
                <span className="w-6 text-center font-display text-2xl">{f.max}</span>
                <button className="size-10 rounded-full bg-primary font-display text-xl text-primary-foreground" onClick={() => setF({ ...f, max: Math.min(8, f.max + 1) })}>+</button>
              </div>
            </div>
            <div className="flex flex-wrap gap-2">{(["Beginner", "Casual", "Competitive"] as const).map((l) => <Chip key={l} active={f.level === l} onClick={() => setF({ ...f, level: l })}>{l}</Chip>)}</div>
          </div>
        )}
        {step === 4 && (
          <div className="space-y-4">
            <textarea className={input + " min-h-28"} placeholder="House rules, expansions, what to bring…" value={f.note} onChange={(e) => setF({ ...f, note: e.target.value })} />
            <div className="flex flex-wrap gap-2">{["Free", "Rp15k", "Rp25k", "Rp30k"].map((x) => <Chip key={x} active={f.fee === x} onClick={() => setF({ ...f, fee: x })}>{x}</Chip>)}</div>
            <div className="rounded-2xl bg-surface p-4 text-sm ring-1 ring-border">
              <p className="font-display text-lg uppercase">{getGame(f.gameId).name}</p>
              <p className="text-muted-foreground">{f.day} {f.time} · {getVenue(f.venueId).name} · {f.max} players · {f.level}</p>
            </div>
          </div>
        )}
      </div>

      <div className="mt-6 grid grid-cols-[auto_1fr] gap-3 px-5">
        <button className={btnGhost} disabled={step === 0} onClick={() => setStep(step - 1)}>Back</button>
        {step < 4 ? (
          <button className={btn} disabled={!ok} onClick={() => setStep(step + 1)}>Continue</button>
        ) : (
          <button className={btn} onClick={() => setDone(create({ ...f, note: f.note || "Come play!" }))}>Publish table</button>
        )}
      </div>
    </Screen>
  );
}

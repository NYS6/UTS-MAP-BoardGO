import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { Clock, MapPin, Share2, Users, Gauge } from "lucide-react";
import { toast } from "sonner";
import { Avatar, Empty, Screen, TopBar, btn, btnGhost } from "@/components/bg";
import { getGame, getPlayer, getVenue, pastSessionIds } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/session/$id")({
  head: () => meta("Session", "Board game session details, players and venue."),
  component: SessionDetail,
});

function SessionDetail() {
  const { id } = Route.useParams();
  const { sessions, join, leave } = useStore();
  const nav = useNavigate();
  const [confirm, setConfirm] = useState(false);
  const s = sessions.find((x) => x.id === id);
  if (!s) return <Screen><TopBar back title="Session" /><Empty title="Table not found" body="This session may have ended." action={<Link to="/discover" className={btn}>Find another</Link>} /></Screen>;
  const g = getGame(s.gameId), v = getVenue(s.venueId), host = getPlayer(s.hostId);
  const mine = s.playerIds.includes("me");
  const full = s.playerIds.length >= s.max;
  const past = pastSessionIds.includes(s.id);

  return (
    <Screen nav={false}>
      <div className="relative h-72">
        <img src={g.image} alt={g.name} width={1024} height={640} className="h-full w-full object-cover" />
        <div className="absolute inset-0 bg-gradient-to-t from-background via-background/10 to-transparent" />
        <div className="absolute inset-x-0 top-0">
          <TopBar back light right={<button onClick={() => toast("Link copied")} aria-label="Share" className="grid size-10 place-items-center rounded-full bg-glass/25 text-primary-foreground ring-1 ring-glass/40 backdrop-blur-md"><Share2 className="size-4" /></button>} />
        </div>
      </div>
      <div className="relative -mt-16 px-5">
        <span className="rounded-full bg-meeple px-2.5 py-1 text-[10px] font-bold uppercase text-foreground">{s.level}</span>
        <h1 className="mt-2 font-display text-[44px] uppercase leading-[0.9] tracking-tight">{g.name}</h1>
        <p className="mt-2 text-sm text-muted-foreground">{g.description}</p>

        <div className="mt-5 grid grid-cols-2 gap-2.5">
          <Fact icon={Clock} label={s.day} value={s.time} />
          <Fact icon={MapPin} label={v.area} value={`${v.distance} km`} />
          <Fact icon={Users} label="Players" value={`${s.playerIds.length}/${s.max}`} />
          <Fact icon={Gauge} label={g.duration} value={g.difficulty} />
        </div>

        <div className="mt-4 rounded-2xl bg-surface p-4 ring-1 ring-border">
          <p className="font-display text-lg uppercase">{v.name}</p>
          <p className="text-sm text-muted-foreground">{v.area} · Fee {s.fee}</p>
          <p className="mt-3 text-sm">"{s.note}"</p>
        </div>

        <p className="mt-5 mb-2 font-display text-[12px] uppercase tracking-[0.18em]">At the table</p>
        <div className="space-y-2">
          {s.playerIds.map((pid) => {
            const p = getPlayer(pid);
            return (
              <Link key={pid} to={pid === "me" ? "/profile" : "/profile/$userId"} params={{ userId: pid }} className="flex items-center gap-3 rounded-2xl bg-surface p-3 ring-1 ring-border">
                <Avatar id={pid} />
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-semibold">{pid === "me" ? "You" : p.name}</p>
                  <p className="font-mono text-[10px] text-muted-foreground">★ {p.rating} · {p.games} games</p>
                </div>
                {pid === host.id && <span className="rounded-full bg-primary/10 px-2 py-0.5 font-mono text-[10px] text-primary">HOST</span>}
              </Link>
            );
          })}
          {Array.from({ length: s.max - s.playerIds.length }).map((_, i) => (
            <div key={i} className="rounded-2xl border border-dashed border-border p-3 text-center font-mono text-[11px] text-muted-foreground">open seat</div>
          ))}
        </div>
      </div>

      <div className="sticky bottom-0 z-20 mt-6 flex gap-3 bg-gradient-to-t from-background via-background to-transparent px-5 pt-6 pb-5">
        {past ? (
          <Link to="/rate/$sessionId" params={{ sessionId: s.id }} className={btn + " flex-1"}>Rate players</Link>
        ) : mine ? (
          <>
            <button className={btnGhost} onClick={() => { leave(s.id); toast("You left the table"); }}>Leave</button>
            <Link to="/chat/$sessionId" params={{ sessionId: s.id }} className={btn + " flex-1"}>Open chat</Link>
          </>
        ) : (
          <button disabled={full} className={btn + " flex-1"} onClick={() => setConfirm(true)}>{full ? "Table full" : "Join table"}</button>
        )}
      </div>

      {confirm && (
        <div className="fixed inset-0 z-50 flex items-end justify-center bg-foreground/40 backdrop-blur-sm" onClick={() => setConfirm(false)}>
          <div className="sheet-up w-full max-w-[430px] rounded-t-[28px] bg-surface p-6" onClick={(e) => e.stopPropagation()}>
            <div className="mx-auto mb-4 h-1.5 w-12 rounded-full bg-muted" />
            <h2 className="font-display text-2xl uppercase">Join {g.name}?</h2>
            <p className="mt-1 text-sm text-muted-foreground">{s.day} at {s.time} · {v.name}. Fee {s.fee}.</p>
            <div className="mt-6 grid grid-cols-2 gap-3">
              <button className={btnGhost} onClick={() => setConfirm(false)}>Not now</button>
              <button className={btn} onClick={() => { join(s.id); setConfirm(false); toast.success("You're in! 🎲"); nav({ to: "/chat/$sessionId", params: { sessionId: s.id } }); }}>Confirm</button>
            </div>
          </div>
        </div>
      )}
    </Screen>
  );
}

function Fact({ icon: Icon, label, value }: { icon: typeof Clock; label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-surface p-3 ring-1 ring-border">
      <Icon className="size-4 text-primary" />
      <p className="mt-2 font-display text-lg leading-none">{value}</p>
      <p className="mt-1 font-mono text-[10px] uppercase text-muted-foreground">{label}</p>
    </div>
  );
}

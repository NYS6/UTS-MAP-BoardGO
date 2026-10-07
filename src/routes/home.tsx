import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { Bell, MapPin } from "lucide-react";
import { toast } from "sonner";
import { AvatarStack, Chip, Screen, SectionLabel, SessionRow, SkyBackdrop } from "@/components/bg";
import { getGame, getVenue, pastSessionIds, venues } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/home")({
  head: () => meta("Home", "Live board game sessions near you, right now."),
  component: HomePage,
});

const chips = ["All", "catan", "wingspan", "azul", "mars"];

function HomePage() {
  const { sessions, notifs, join, profile } = useStore();
  const [chip, setChip] = useState("All");
  const [loc, setLoc] = useState<"idle" | "locating" | "on">("on");
  const open = sessions.filter((s) => !pastSessionIds.includes(s.id) && (chip === "All" || s.gameId === chip));
  const featured = open.find((s) => !s.playerIds.includes("me") && s.playerIds.length < s.max) ?? open[0];
  const rest = open.filter((s) => s !== featured);
  const unread = notifs.filter((n) => !n.read).length;

  const locate = () => {
    setLoc("locating");
    setTimeout(() => { setLoc("on"); toast.success("Location updated · BSD"); }, 1200);
  };

  return (
    <Screen>
      <SkyBackdrop tall />
      <header className="relative z-10 flex items-center justify-between px-5 pt-6 pb-2">
        <div className="flex items-center gap-2.5">
          <div className="sheen grid size-9 place-items-center overflow-hidden rounded-xl bg-primary font-display text-[17px] text-primary-foreground shadow-lg shadow-primary/30">B</div>
          <span className="font-display text-[22px] tracking-tight text-primary-foreground">BOARDGO</span>
        </div>
        <Link to="/notifications" aria-label="Notifications" className="relative grid size-10 place-items-center rounded-full bg-glass/25 text-primary-foreground ring-1 ring-glass/40 backdrop-blur-md">
          <Bell className="size-4" />
          {unread > 0 && <span className="absolute -top-0.5 -right-0.5 size-2.5 rounded-full bg-meeple ring-2 ring-sky" />}
        </Link>
      </header>

      <button onClick={locate} className="rise relative z-10 flex w-full items-center gap-2 px-5 pt-3 pb-1 text-left" style={{ animationDelay: "60ms" }}>
        <span className="font-display text-[11px] tracking-[0.2em] text-primary">◉ {loc === "locating" ? "LOCATING…" : "NEARBY"}</span>
        <span className="flex items-center gap-1 font-mono text-[11px] text-primary-foreground/80"><MapPin className="size-3" />{profile.area} · {venues.length} venues</span>
        <span className="ml-auto font-mono text-[11px] text-primary-foreground/70">{open.length} sessions</span>
      </button>

      <h1 className="rise relative z-10 px-5 pt-1 font-display text-[46px] leading-[0.9] tracking-tight text-primary-foreground" style={{ animationDelay: "110ms" }}>
        FIND A TABLE<br />IN <span className="text-meeple">30 SEC</span>
      </h1>
      <p className="rise relative z-10 max-w-[28ch] px-5 pt-2 text-[13px] leading-snug text-primary-foreground/85" style={{ animationDelay: "160ms" }}>
        Live sessions around you — grab a meeple, roll in, play tonight.
      </p>

      <div className="no-scrollbar rise relative z-10 flex gap-2 overflow-x-auto px-5 pt-4" style={{ animationDelay: "200ms" }}>
        {chips.map((c) => <Chip key={c} glass active={chip === c} onClick={() => setChip(c)}>{c === "All" ? "All" : getGame(c).name}</Chip>)}
      </div>

      {featured ? <Featured id={featured.id} onJoin={() => { join(featured.id); toast.success(`Joined ${getGame(featured.gameId).name}!`); }} /> : null}

      <SectionLabel right={<Link to="/discover" className="font-mono text-[11px] text-primary">see all</Link>}>Up next</SectionLabel>
      <div className="relative z-10">{rest.slice(0, 4).map((s, i) => <SessionRow key={s.id} s={s} delay={300 + i * 40} />)}</div>
    </Screen>
  );
}

function Featured({ id, onJoin }: { id: string; onJoin: () => void }) {
  const s = useStore().sessions.find((x) => x.id === id)!;
  const g = getGame(s.gameId), v = getVenue(s.venueId);
  const left = s.max - s.playerIds.length;
  const mine = s.playerIds.includes("me");
  return (
    <div className="rise relative z-10 mx-5 mt-4 overflow-hidden rounded-[20px] bg-surface/85 ring-1 ring-border backdrop-blur-xl" style={{ animationDelay: "240ms" }}>
      <Link to="/session/$id" params={{ id }} className="sheen block h-[140px] overflow-hidden">
        <img src={g.image} alt={g.name} width={1024} height={640} className="h-full w-full object-cover" />
        <span className="absolute left-3 top-3 rounded-full bg-primary px-2.5 py-1 text-[10px] font-bold uppercase tracking-wide text-primary-foreground">
          {left <= 2 ? `Filling fast · ${left} seat${left === 1 ? "" : "s"}` : `${left} seats open`}
        </span>
      </Link>
      <div className="p-3.5">
        <div className="flex items-start justify-between gap-3">
          <Link to="/session/$id" params={{ id }} className="min-w-0">
            <h2 className="font-display text-[26px] uppercase leading-none tracking-tight">{g.name}</h2>
            <p className="mt-1.5 truncate text-[12px] text-muted-foreground">{v.name} · {v.area} · {v.distance} km</p>
          </Link>
          <div className="shrink-0 text-right">
            <span className="font-mono text-[11px] uppercase text-muted-foreground">{s.day}</span>
            <span className="block font-display text-[18px] leading-none">{s.time}</span>
          </div>
        </div>
        <div className="mt-3 flex items-center gap-2">
          <AvatarStack ids={s.playerIds} />
          <span className="font-mono text-[11px] text-muted-foreground">{s.playerIds.length}/{s.max} joined</span>
          {mine ? (
            <Link to="/chat/$sessionId" params={{ sessionId: id }} className="ml-auto rounded-full bg-success px-4 py-2 text-[12px] font-semibold text-primary-foreground">Open chat</Link>
          ) : (
            <button onClick={onJoin} className="ml-auto rounded-full bg-primary px-4 py-2 text-[12px] font-semibold text-primary-foreground shadow-lg shadow-primary/25 transition-transform active:scale-95">Join table</button>
          )}
        </div>
      </div>
    </div>
  );
}

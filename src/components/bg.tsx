import { Link, useRouterState } from "@tanstack/react-router";
import { Home, Compass, Plus, LayoutGrid, User, ChevronLeft } from "lucide-react";
import type { ReactNode } from "react";
import { getGame, getPlayer, getVenue, type Session } from "@/lib/data";
import { cn } from "@/lib/utils";

const colorBg = { p1: "bg-p1", p2: "bg-p2", p3: "bg-p3", p4: "bg-p4" } as const;

export function Avatar({ id, size = "md" }: { id: string; size?: "sm" | "md" | "lg" | "xl" }) {
  const p = getPlayer(id);
  const s = { sm: "size-7 text-[10px]", md: "size-10 text-sm", lg: "size-14 text-lg", xl: "size-24 text-3xl" }[size];
  return (
    <div className={cn("shrink-0 rounded-full ring-2 ring-surface grid place-items-center font-bold text-primary-foreground", colorBg[p.color], s)}>
      {p.name[0]}
    </div>
  );
}

export function AvatarStack({ ids }: { ids: string[] }) {
  return <div className="flex -space-x-2">{ids.slice(0, 4).map((id) => <Avatar key={id} id={id} size="sm" />)}</div>;
}

/** Sky header backdrop with glass panels — used on hero screens */
export function SkyBackdrop({ tall = false }: { tall?: boolean }) {
  return (
    <div className="pointer-events-none absolute inset-x-0 top-0 overflow-hidden" style={{ height: tall ? 560 : 300 }}>
      <div className="absolute inset-0 bg-sky-gradient" />
      <div className="drift absolute -left-14 top-12 h-64 w-64 rounded-full bg-glass/30 blur-3xl" />
      <div className="absolute -right-12 top-6 h-48 w-48 rounded-full bg-primary/30 blur-3xl" />
      <div className="absolute -right-24 -top-16 h-72 w-72 rotate-[28deg] border border-glass/30 bg-glass/15 backdrop-blur-2xl" />
      <div className="absolute left-[-30%] top-[150px] h-40 w-[160%] rotate-[-14deg] border-y border-glass/25 bg-glass/10 backdrop-blur-xl" />
    </div>
  );
}

export function TopBar({ title, back, right, light }: { title?: string; back?: boolean; right?: ReactNode; light?: boolean }) {
  return (
    <header className="relative z-10 grid grid-cols-[auto_minmax(0,1fr)_auto] items-center gap-3 px-5 pt-6 pb-3">
      {back ? (
        <button onClick={() => history.back()} aria-label="Back" className={cn("grid size-10 place-items-center rounded-full ring-1", light ? "bg-glass/25 ring-glass/40 text-primary-foreground backdrop-blur-md" : "bg-surface ring-border")}>
          <ChevronLeft className="size-5" />
        </button>
      ) : <span />}
      <h1 className={cn("truncate font-display text-[22px] uppercase leading-none tracking-tight", light ? "text-primary-foreground" : "text-foreground")}>{title}</h1>
      <div>{right}</div>
    </header>
  );
}

const nav = [
  { to: "/home", label: "HOME", icon: Home },
  { to: "/discover", label: "FIND", icon: Compass },
  { to: "/create", label: "", icon: Plus },
  { to: "/my-games", label: "MINE", icon: LayoutGrid },
  { to: "/profile", label: "ME", icon: User },
] as const;

export function BottomNav() {
  const path = useRouterState({ select: (s) => s.location.pathname });
  return (
    <nav className="fixed inset-x-0 bottom-3 z-30 mx-auto flex w-[calc(100%-1.5rem)] max-w-[406px] items-center justify-between rounded-[22px] bg-surface/85 px-2 py-2 ring-1 ring-border backdrop-blur-xl">
      {nav.map(({ to, label, icon: Icon }) => {
        const active = path === to || (to === "/profile" && path === "/profile/");
        if (to === "/create")
          return (
            <Link key={to} to={to} aria-label="Create" className="-mt-6 grid size-12 place-items-center rounded-2xl bg-primary text-primary-foreground shadow-lg shadow-primary/30 transition-transform active:scale-95">
              <Icon className="size-6" strokeWidth={2.5} />
            </Link>
          );
        return (
          <Link key={to} to={to} className={cn("flex min-w-14 flex-col items-center gap-1 rounded-xl px-3 py-1.5", active ? "bg-primary/10 text-primary" : "text-muted-foreground")}>
            <Icon className="size-4" />
            <span className="font-mono text-[9px]">{label}</span>
          </Link>
        );
      })}
    </nav>
  );
}

export function Screen({ children, nav = true, className }: { children: ReactNode; nav?: boolean; className?: string }) {
  return (
    <div className="flex min-h-screen w-full justify-center bg-secondary">
      <div className={cn("relative flex min-h-screen w-full max-w-[430px] flex-col overflow-x-clip bg-background", className)}>
        <div className={cn("relative flex-1", nav ? "pb-28" : "pb-4")}>{children}</div>
        {nav && <BottomNav />}
      </div>
    </div>
  );
}

export function SectionLabel({ children, right }: { children: ReactNode; right?: ReactNode }) {
  return (
    <div className="flex items-center gap-2 px-5 pt-5 pb-2">
      <span className="font-display text-[12px] uppercase leading-none tracking-[0.18em]">{children}</span>
      <span className="h-px flex-1 bg-border" />
      {right}
    </div>
  );
}

export function Chip({ active, children, onClick, glass }: { active?: boolean; children: ReactNode; onClick?: () => void; glass?: boolean }) {
  return (
    <button onClick={onClick} className={cn(
      "shrink-0 rounded-full px-4 py-2 text-[12px] font-medium ring-1 transition-colors",
      active ? "bg-primary text-primary-foreground ring-transparent font-semibold"
        : glass ? "bg-glass/25 text-primary-foreground ring-glass/40 backdrop-blur-md" : "bg-surface text-foreground ring-border",
    )}>{children}</button>
  );
}

export function SessionRow({ s, delay = 0 }: { s: Session; delay?: number }) {
  const g = getGame(s.gameId), v = getVenue(s.venueId);
  const left = s.max - s.playerIds.length;
  return (
    <Link to="/session/$id" params={{ id: s.id }} className="rise mx-5 mb-2.5 flex items-center gap-3 rounded-2xl bg-surface p-3 ring-1 ring-border transition-transform active:scale-[0.98]" style={{ animationDelay: `${delay}ms` }}>
      <img src={g.image} alt={g.name} loading="lazy" width={112} height={112} className="size-14 shrink-0 rounded-xl object-cover" />
      <div className="min-w-0 flex-1">
        <h3 className="truncate font-display text-[18px] uppercase leading-none tracking-tight">{g.name}</h3>
        <p className="mt-1 truncate text-[11px] text-muted-foreground">{v.name} · {v.area}</p>
        <p className="mt-0.5 font-mono text-[10px] text-muted-foreground">{left > 0 ? `${left} seat${left > 1 ? "s" : ""}` : "Full"} · {s.day} {s.time}</p>
      </div>
      <span className={cn("grid size-9 shrink-0 place-items-center rounded-full font-display text-[18px] leading-none", left > 0 ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground")}>{left > 0 ? "+" : "✓"}</span>
    </Link>
  );
}

export function Empty({ title, body, action }: { title: string; body: string; action?: ReactNode }) {
  return (
    <div className="mx-5 mt-6 rounded-2xl border border-dashed border-border bg-surface/60 p-8 text-center">
      <div className="mx-auto mb-3 grid size-12 place-items-center rounded-xl bg-meeple/20 font-display text-xl text-meeple">?</div>
      <p className="font-display text-lg uppercase">{title}</p>
      <p className="mt-1 text-sm text-muted-foreground">{body}</p>
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}

export const btn = "inline-flex items-center justify-center gap-2 rounded-full bg-primary px-5 py-3 text-sm font-semibold text-primary-foreground shadow-lg shadow-primary/25 transition-transform active:scale-95 disabled:opacity-50";
export const btnGhost = "inline-flex items-center justify-center gap-2 rounded-full bg-surface px-5 py-3 text-sm font-semibold text-foreground ring-1 ring-border transition-transform active:scale-95";
export const input = "w-full rounded-2xl bg-surface px-4 py-3.5 text-sm ring-1 ring-border outline-none focus:ring-2 focus:ring-primary";

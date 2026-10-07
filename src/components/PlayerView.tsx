import type { ReactNode } from "react";
import { Avatar, SkyBackdrop } from "@/components/bg";
import { getGame } from "@/lib/data";

export function PlayerView({ id, name, area, bio, level, rating, games, favorites, actions }: {
  id: string; name: string; area: string; bio: string; level: string; rating: number; games: number; favorites: string[]; actions: ReactNode;
}) {
  return (
    <>
      <SkyBackdrop />
      <div className="relative z-10 flex flex-col items-center px-5 pt-4 text-center">
        <Avatar id={id} size="xl" />
        <h1 className="mt-4 font-display text-[34px] uppercase leading-none text-primary-foreground">{name}</h1>
        <p className="mt-1 font-mono text-[11px] text-primary-foreground/80">{area} · {level}</p>
      </div>
      <div className="relative z-10 mx-5 mt-6 grid grid-cols-3 gap-2 rounded-3xl bg-surface/90 p-4 text-center ring-1 ring-border backdrop-blur-xl">
        <Stat v={`★ ${rating}`} l="rating" /><Stat v={String(games)} l="games" /><Stat v={String(favorites.length)} l="favorites" />
      </div>
      <p className="mx-5 mt-4 text-center text-sm text-muted-foreground">{bio}</p>
      <div className="mx-5 mt-4 flex gap-2">{actions}</div>
      <p className="px-5 pt-6 pb-2 font-display text-[12px] uppercase tracking-[0.18em]">Favorite games</p>
      <div className="no-scrollbar flex gap-3 overflow-x-auto px-5">
        {favorites.map((f) => { const g = getGame(f); return (
          <div key={f} className="w-32 shrink-0 overflow-hidden rounded-2xl bg-surface ring-1 ring-border">
            <img src={g.image} alt={g.name} loading="lazy" className="h-20 w-full object-cover" />
            <p className="truncate p-2 font-display text-sm uppercase">{g.name}</p>
          </div>
        ); })}
      </div>
    </>
  );
}

function Stat({ v, l }: { v: string; l: string }) {
  return <div><p className="font-display text-xl">{v}</p><p className="font-mono text-[10px] uppercase text-muted-foreground">{l}</p></div>;
}

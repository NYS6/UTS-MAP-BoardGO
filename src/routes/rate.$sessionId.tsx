import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { Star } from "lucide-react";
import { toast } from "sonner";
import { Avatar, Chip, Empty, Screen, TopBar, btn } from "@/components/bg";
import { getGame, getPlayer } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/rate/$sessionId")({
  head: () => meta("Rate players", "Rate the players from your last board game session."),
  component: Rate,
});

const tags = ["Friendly", "On time", "Good teacher", "Fair play", "Fun"];

function Rate() {
  const { sessionId } = Route.useParams();
  const { sessions, rate, rated } = useStore();
  const nav = useNavigate();
  const s = sessions.find((x) => x.id === sessionId);
  const [scores, setScores] = useState<Record<string, number>>({});
  const [picked, setPicked] = useState<Record<string, string[]>>({});
  if (!s) return <Screen><TopBar back title="Rate" /><Empty title="Session not found" body="Nothing to rate here." /></Screen>;
  if (rated.includes(s.id)) return <Screen><TopBar back title="Rate" /><Empty title="Already rated" body="Thanks for keeping BoardGo friendly!" action={<Link to="/discover" className={btn}>Play again</Link>} /></Screen>;
  const others = s.playerIds.filter((p) => p !== "me");
  const allDone = others.every((p) => scores[p]);

  return (
    <Screen nav={false}>
      <TopBar back title="Rate table" />
      <p className="px-5 pb-4 text-sm text-muted-foreground">How was {getGame(s.gameId).name}? Your rating stays anonymous.</p>
      <div className="space-y-3 px-5">
        {others.map((pid) => (
          <div key={pid} className="rounded-2xl bg-surface p-4 ring-1 ring-border">
            <div className="flex items-center gap-3"><Avatar id={pid} /><p className="font-semibold">{getPlayer(pid).name}</p></div>
            <div className="mt-3 flex gap-1">
              {[1, 2, 3, 4, 5].map((n) => (
                <button key={n} type="button" className="grid size-11 place-items-center" aria-label={`${n} stars`} onClick={() => setScores({ ...scores, [pid]: n })}>
                  <Star className={cn("size-8 transition", (scores[pid] ?? 0) >= n ? "fill-meeple text-meeple" : "text-muted")} />
                </button>
              ))}
            </div>
            <div className="mt-3 flex flex-wrap gap-2">
              {tags.map((t) => { const on = !!picked[pid]?.includes(t); return <Chip key={t} active={on} onClick={() => setPicked({ ...picked, [pid]: on ? picked[pid]!.filter((x) => x !== t) : [...(picked[pid] ?? []), t] })}>{t}</Chip>; })}
            </div>
          </div>
        ))}
      </div>
      <div className="px-5 pt-6">
        <button disabled={!allDone} className={btn + " w-full"} onClick={() => { rate(s.id); toast.success("Ratings sent — play again soon!"); nav({ to: "/my-games" }); }}>Submit ratings</button>
        {!allDone && <p className="mt-2 text-center text-xs text-muted-foreground">Rate all players to submit ({others.filter((p) => scores[p]).length} of {others.length} rated)</p>}
      </div>
    </Screen>
  );
}

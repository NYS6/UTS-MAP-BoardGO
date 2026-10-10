import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { MoreHorizontal } from "lucide-react";
import { toast } from "sonner";
import { Screen, TopBar, btn, btnGhost } from "@/components/bg";
import { PlayerView } from "@/components/PlayerView";
import { getPlayer } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/profile/$userId")({
  head: () => meta("Player profile", "See a BoardGo player's ratings and favorite games."),
  component: Other,
});

const reasons = ["No-show", "Rude behavior", "Spam", "Something else"];

function Other() {
  const { userId } = Route.useParams();
  const { block, blocked } = useStore();
  const nav = useNavigate();
  const [sheet, setSheet] = useState<"menu" | "report" | null>(null);
  const p = getPlayer(userId);
  const isBlocked = blocked.includes(p.id);

  return (
    <Screen>
      <TopBar back light right={<button onClick={() => setSheet("menu")} aria-label="More" className="grid size-10 place-items-center rounded-full bg-glass/25 text-primary-foreground ring-1 ring-glass/40 backdrop-blur-md"><MoreHorizontal className="size-4" /></button>} />
      <PlayerView {...p}
        actions={isBlocked ? <p className="w-full text-center font-mono text-[11px] text-destructive">You blocked this player</p>
          : <><button className={btn + " flex-1"} onClick={() => toast.success(`Invite sent to ${p.name.split(" ")[0]}`)}>Invite to a game</button><button className={btnGhost} onClick={() => nav({ to: "/discover" })}>Tables</button></>} />

      {sheet && (
        <div className="fixed inset-0 z-50 flex items-end justify-center bg-foreground/40 backdrop-blur-sm" onClick={() => setSheet(null)}>
          <div className="sheet-up w-full max-w-[430px] space-y-2 rounded-t-[28px] bg-surface p-5 pb-8" onClick={(e) => e.stopPropagation()}>
            <div className="mx-auto mb-3 h-1.5 w-12 rounded-full bg-muted" />
            {sheet === "menu" ? (
              <>
                <button className={btnGhost + " w-full"} onClick={() => setSheet("report")}>Report player</button>
                <button className={btnGhost + " w-full text-destructive"} onClick={() => { block(p.id); setSheet(null); toast(`${p.name} blocked`); }}>Block player</button>
                <button className="w-full py-3 text-sm text-muted-foreground" onClick={() => setSheet(null)}>Cancel</button>
              </>
            ) : (
              <>
                <h2 className="pb-2 font-display text-2xl uppercase">Why report?</h2>
                {reasons.map((r) => <button key={r} className={btnGhost + " w-full justify-start"} onClick={() => { setSheet(null); toast.success("Thanks — our team will review this."); }}>{r}</button>)}
              </>
            )}
          </div>
        </div>
      )}
    </Screen>
  );
}

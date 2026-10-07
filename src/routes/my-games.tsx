import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { Chip, Empty, Screen, SessionRow, TopBar, btn } from "@/components/bg";
import { pastSessionIds } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/my-games")({
  head: () => meta("My Games", "Your upcoming, hosted and past board game sessions."),
  component: MyGames,
});

function MyGames() {
  const { sessions, rated } = useStore();
  const [tab, setTab] = useState<"Upcoming" | "Hosting" | "Past">("Upcoming");
  const mine = sessions.filter((s) => s.playerIds.includes("me"));
  const list = tab === "Past" ? mine.filter((s) => pastSessionIds.includes(s.id))
    : mine.filter((s) => !pastSessionIds.includes(s.id) && (tab === "Upcoming" || s.hostId === "me"));

  return (
    <Screen>
      <TopBar title="My Games" />
      <div className="flex gap-2 px-5 pb-4">{(["Upcoming", "Hosting", "Past"] as const).map((t) => <Chip key={t} active={tab === t} onClick={() => setTab(t)}>{t}</Chip>)}</div>
      {list.length === 0 ? (
        <Empty title={tab === "Hosting" ? "Not hosting yet" : "Nothing here"} body={tab === "Hosting" ? "Pick a game and open your own table." : "Find a table and join a game."}
          action={<Link to={tab === "Hosting" ? "/create" : "/discover"} className={btn}>{tab === "Hosting" ? "Host a game" : "Discover"}</Link>} />
      ) : list.map((s) => (
        <div key={s.id}>
          <SessionRow s={s} />
          {tab === "Past" && (
            <div className="-mt-1 mb-3 px-5">
              {rated.includes(s.id) ? <p className="font-mono text-[11px] text-success">✓ rated — thanks!</p>
                : <Link to="/rate/$sessionId" params={{ sessionId: s.id }} className="font-mono text-[11px] text-primary">★ rate your table →</Link>}
            </div>
          )}
          {tab !== "Past" && (
            <div className="-mt-1 mb-3 px-5"><Link to="/chat/$sessionId" params={{ sessionId: s.id }} className="font-mono text-[11px] text-primary">open chat →</Link></div>
          )}
        </div>
      ))}
    </Screen>
  );
}

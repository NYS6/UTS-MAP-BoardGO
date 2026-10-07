import { createFileRoute, Link } from "@tanstack/react-router";
import { Empty, Screen, TopBar } from "@/components/bg";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/notifications")({
  head: () => meta("Notifications", "Updates on your tables, chats and nearby games."),
  component: Notifications,
});

function Notifications() {
  const { notifs, markAllRead } = useStore();
  return (
    <Screen>
      <TopBar back title="Alerts" right={<button onClick={markAllRead} className="font-mono text-[11px] text-primary">read all</button>} />
      {notifs.length === 0 ? <Empty title="All quiet" body="We'll ping you when tables open up." /> : (
        <div className="space-y-2 px-5">
          {notifs.map((n, i) => (
            <Link key={n.id} to={(n.link ?? "/home") as "/home"} className={cn("rise flex gap-3 rounded-2xl p-4 ring-1", n.read ? "bg-surface/60 ring-border" : "bg-surface ring-primary/30")} style={{ animationDelay: `${i * 40}ms` }}>
              <span className={cn("mt-1.5 size-2 shrink-0 rounded-full", n.read ? "bg-muted" : "bg-primary")} />
              <div className="min-w-0 flex-1">
                <p className="text-sm font-semibold">{n.title}</p>
                <p className="truncate text-sm text-muted-foreground">{n.body}</p>
              </div>
              <span className="font-mono text-[10px] text-muted-foreground">{n.time}</span>
            </Link>
          ))}
        </div>
      )}
    </Screen>
  );
}

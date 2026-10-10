import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useRef, useState } from "react";
import { Send } from "lucide-react";
import { Avatar, Screen, TopBar } from "@/components/bg";
import { getGame, getPlayer } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/chat/$sessionId")({
  head: () => meta("Table chat", "Chat with the players at your board game table."),
  component: Chat,
});

const replies = ["Sounds good! 👍", "Can't wait 🎲", "I'll bring snacks", "See you there!"];

function Chat() {
  const { sessionId } = Route.useParams();
  const { sessions, chats, send } = useStore();
  const [text, setText] = useState("");
  const [typing, setTyping] = useState(false);
  const end = useRef<HTMLDivElement>(null);
  const s = sessions.find((x) => x.id === sessionId);
  const msgs = chats[sessionId] ?? [];
  useEffect(() => { end.current?.scrollIntoView({ behavior: "smooth" }); }, [msgs.length, typing]);

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!text.trim()) return;
    send(sessionId, text.trim()); setText("");
    const other = s?.playerIds.find((p) => p !== "me");
    if (other) {
      setTyping(true);
      setTimeout(() => {
        setTyping(false);
        // simulated reply from another player
        send(sessionId, `__from:${other}__${replies[Math.floor(Math.random() * replies.length)]}`);
      }, 1400);
    }
  };

  if (!s) return <Screen><TopBar back title="Chat" /><p className="px-5 text-muted-foreground">Chat not found.</p></Screen>;
  const g = getGame(s.gameId);

  return (
    <Screen nav={false}>
      <div className="flex min-h-screen flex-col">
        <TopBar back title={g.name} right={<Link to="/session/$id" params={{ id: s.id }} className="font-mono text-[11px] text-primary">info</Link>} />
        <p className="px-5 pb-3 font-mono text-[11px] text-muted-foreground">{s.playerIds.length} players · {s.day} {s.time}</p>
        <div className="flex-1 space-y-3 px-5 pb-4">
          {msgs.length === 0 && <p className="pt-10 text-center text-sm text-muted-foreground">Say hi to your table 👋</p>}
          {msgs.map((m) => {
            let from = m.from, body = m.text;
            const mm = body.match(/^__from:(\w+)__(.*)$/);
            if (mm) { from = mm[1]!; body = mm[2]!; }
            if (from === "system") return <p key={m.id} className="text-center font-mono text-[11px] text-muted-foreground">{body}</p>;
            const me = from === "me";
            return (
              <div key={m.id} className={cn("rise flex items-end gap-2", me && "flex-row-reverse")}>
                {!me && <Avatar id={from} size="sm" />}
                <div className={cn("max-w-[75%] rounded-2xl px-3.5 py-2.5 text-sm", me ? "rounded-br-md bg-primary text-primary-foreground" : "rounded-bl-md bg-surface ring-1 ring-border")}>
                  {!me && <p className="mb-0.5 text-[11px] font-semibold text-muted-foreground">{getPlayer(from).name.split(" ")[0]!}</p>}
                  {body}
                  <p className={cn("mt-1 text-right font-mono text-[9px]", me ? "text-primary-foreground/70" : "text-muted-foreground")}>{m.time}</p>
                </div>
              </div>
            );
          })}
          {typing && <p className="font-mono text-[11px] text-muted-foreground">someone is typing…</p>}
          <div ref={end} />
        </div>
        <form onSubmit={submit} className="sticky bottom-0 flex gap-2 bg-background/90 px-4 py-3 backdrop-blur-xl">
          <input value={text} onChange={(e) => setText(e.target.value)} placeholder="Message the table" className="flex-1 rounded-full bg-surface px-4 py-3 text-sm ring-1 ring-border outline-none focus:ring-2 focus:ring-primary" />
          <button aria-label="Send" className="grid size-12 place-items-center rounded-full bg-primary text-primary-foreground"><Send className="size-4" /></button>
        </form>
      </div>
    </Screen>
  );
}

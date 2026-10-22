import { createContext, useContext, useState, type ReactNode } from "react";
import { initialSessions, players, type Session } from "./data";

export type Msg = { id: string; from: string; text: string; time: string };
export type Notif = { id: string; title: string; body: string; time: string; read: boolean; link?: string };
export type Filters = { area: string; games: string[]; level: string; day: string };

type Profile = { name: string; bio: string; area: string; favorites: string[]; level: string };

type Store = {
  sessions: Session[];
  profile: Profile;
  setProfile: (p: Profile) => void;
  join: (id: string) => void;
  leave: (id: string) => void;
  create: (s: Omit<Session, "id" | "hostId" | "playerIds">) => string;
  chats: Record<string, Msg[]>;
  send: (sid: string, text: string, from?: string) => void;
  notifs: Notif[];
  markAllRead: () => void;
  filters: Filters;
  setFilters: (f: Filters) => void;
  rated: string[];
  rate: (sid: string) => void;
  blocked: string[];
  block: (uid: string) => void;
};

const Ctx = createContext<Store | null>(null);
export const defaultFilters: Filters = { area: "All", games: [], level: "Any", day: "Any" };

const now = () => new Date().toTimeString().slice(0, 5);

export function StoreProvider({ children }: { children: ReactNode }) {
  const me = players[0]!;
  const [sessions, setSessions] = useState<Session[]>(initialSessions);
  const [profile, setProfile] = useState<Profile>({ name: me.name, bio: me.bio, area: me.area, favorites: me.favorites, level: me.level });
  const [filters, setFilters] = useState<Filters>(defaultFilters);
  const [rated, setRated] = useState<string[]>([]);
  const [blocked, setBlocked] = useState<string[]>([]);
  const [chats, setChats] = useState<Record<string, Msg[]>>({
    s4: [
      { id: "1", from: "u3", text: "Hey! Prelude cards are packed 🔥", time: "10:02" },
      { id: "2", from: "me", text: "Nice, I'll bring chips", time: "10:05" },
      { id: "3", from: "u3", text: "See you Saturday at Meeple House", time: "10:06" },
    ],
  });
  const [notifs, setNotifs] = useState<Notif[]>([
    { id: "n1", title: "Kevin replied in chat", body: "See you Saturday at Meeple House", time: "10m", read: false, link: "/chat/s4" },
    { id: "n2", title: "New Catan table nearby", body: "Coffee & Dice · Tonight 19:30 · 1 seat left", time: "1h", read: false, link: "/session/s1" },
    { id: "n3", title: "Rate your last game", body: "How was Catan at The Board Room?", time: "2d", read: true, link: "/rate/s7" },
  ]);

  const push = (n: Omit<Notif, "id" | "time" | "read">) =>
    setNotifs((x) => [{ ...n, id: String(Date.now()), time: "now", read: false }, ...x]);

  const value: Store = {
    sessions, profile, setProfile, filters, setFilters, chats, notifs, rated, blocked,
    join: (id) => {
      setSessions((s) => s.map((x) => (x.id === id && !x.playerIds.includes("me") ? { ...x, playerIds: [...x.playerIds, "me"] } : x)));
      setChats((c) => ({ ...c, [id]: [...(c[id] ?? []), { id: String(Date.now()), from: "system", text: "You joined the table 🎲", time: now() }] }));
      push({ title: "You're in!", body: "Say hi to your table in chat.", link: `/chat/${id}` });
    },
    leave: (id) => setSessions((s) => s.map((x) => (x.id === id ? { ...x, playerIds: x.playerIds.filter((p) => p !== "me") } : x))),
    create: (s) => {
      const id = "c" + Date.now();
      setSessions((x) => [{ ...s, id, hostId: "me", playerIds: ["me"] }, ...x]);
      push({ title: "Session published", body: "Players nearby can now join your table.", link: `/session/${id}` });
      return id;
    },
    send: (sid, text, from = "me") => setChats((c) => ({ ...c, [sid]: [...(c[sid] ?? []), { id: String(Date.now()) + from, from, text, time: now() }] })),
    markAllRead: () => setNotifs((x) => x.map((n) => ({ ...n, read: true }))),
    rate: (sid) => setRated((r) => [...r, sid]),
    block: (uid) => setBlocked((b) => [...b, uid]),
  };
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useStore() {
  const c = useContext(Ctx);
  if (!c) throw new Error("StoreProvider missing");
  return c;
}

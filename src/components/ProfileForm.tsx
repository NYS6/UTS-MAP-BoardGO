import { useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { toast } from "sonner";
import { Avatar, Chip, btn, input } from "@/components/bg";
import { games, venues } from "@/lib/data";
import { useStore } from "@/lib/store";

const areas = [...new Set(venues.map((v) => v.area))];
const levels = ["Beginner", "Casual", "Competitive"];

export function ProfileForm({ after, cta }: { after: "/home" | "/profile"; cta: string }) {
  const { profile, setProfile } = useStore();
  const nav = useNavigate();
  const [p, setP] = useState(profile);
  const toggle = (id: string) => setP({ ...p, favorites: p.favorites.includes(id) ? p.favorites.filter((f) => f !== id) : [...p.favorites, id] });

  return (
    <div className="space-y-6 px-5">
      <div className="flex items-center gap-4">
        <Avatar id="me" size="lg" />
        <button onClick={() => toast("Photo upload is coming soon")} className="font-mono text-[11px] text-primary">change photo</button>
      </div>
      <div className="space-y-3">
        <input className={input} value={p.name} onChange={(e) => setP({ ...p, name: e.target.value })} placeholder="Display name" />
        <textarea className={input + " min-h-24"} value={p.bio} onChange={(e) => setP({ ...p, bio: e.target.value })} placeholder="Short bio" />
      </div>
      <Group label="Home area">{areas.map((a) => <Chip key={a} active={p.area === a} onClick={() => setP({ ...p, area: a })}>{a}</Chip>)}</Group>
      <Group label="Experience">{levels.map((l) => <Chip key={l} active={p.level === l} onClick={() => setP({ ...p, level: l })}>{l}</Chip>)}</Group>
      <Group label="Favorite games">{games.map((g) => <Chip key={g.id} active={p.favorites.includes(g.id)} onClick={() => toggle(g.id)}>{g.name}</Chip>)}</Group>
      <button className={btn + " w-full"} onClick={() => { setProfile(p); toast.success("Profile saved"); nav({ to: after }); }}>{cta}</button>
    </div>
  );
}

function Group({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <p className="mb-2 font-display text-[12px] uppercase tracking-[0.18em]">{label}</p>
      <div className="flex flex-wrap gap-2">{children}</div>
    </div>
  );
}

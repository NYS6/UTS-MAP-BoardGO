import { Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { Screen, SkyBackdrop, TopBar, btn, input } from "@/components/bg";

export function AuthForm({ mode }: { mode: "login" | "register" }) {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [pw, setPw] = useState("");
  const [name, setName] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);
  const reg = mode === "register";

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    if (reg && !name.trim()) return setErr("Tell us your name");
    if (!/\S+@\S+\.\S+/.test(email)) return setErr("Enter a valid email");
    if (pw.length < 6) return setErr("Password must be at least 6 characters");
    setErr(""); setLoading(true);
    setTimeout(() => nav({ to: reg ? "/profile-setup" : "/home" }), 700);
  };

  return (
    <Screen nav={false}>
      <SkyBackdrop />
      <TopBar back light />
      <div className="relative z-10 px-6 pt-4">
        <h1 className="rise font-display text-[44px] uppercase leading-[0.9] text-primary-foreground">{reg ? <>Grab a<br />seat</> : <>Welcome<br />back</>}</h1>
        <form onSubmit={submit} className="rise mt-10 space-y-3 rounded-3xl bg-surface/90 p-5 ring-1 ring-border backdrop-blur-xl" style={{ animationDelay: "100ms" }}>
          {reg && <input className={input} placeholder="Full name" value={name} onChange={(e) => setName(e.target.value)} />}
          <input className={input} placeholder="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <input className={input} placeholder="Password" type="password" value={pw} onChange={(e) => setPw(e.target.value)} />
          {err && <p className="text-sm text-destructive">{err}</p>}
          <button className={btn + " w-full"} disabled={loading}>{loading ? "One sec…" : reg ? "Create account" : "Log in"}</button>
          <button type="button" onClick={() => nav({ to: reg ? "/profile-setup" : "/home" })} className="w-full rounded-full bg-background py-3 text-sm font-semibold ring-1 ring-border">Continue with Google</button>
        </form>
        <p className="mt-6 text-center text-sm text-muted-foreground">
          {reg ? "Have an account? " : "New here? "}
          <Link to={reg ? "/login" : "/register"} className="font-semibold text-primary">{reg ? "Log in" : "Sign up"}</Link>
        </p>
      </div>
    </Screen>
  );
}

import { createFileRoute, Link } from "@tanstack/react-router";
import { Screen, SkyBackdrop, btn } from "@/components/bg";
import { meta } from "@/lib/meta";
import catan from "@/assets/catan.jpg";

export const Route = createFileRoute("/")({
  head: () => meta("Welcome", "BoardGo — find people, find games, play together. Board game sessions near you."),
  component: Welcome,
});

function Welcome() {
  return (
    <Screen nav={false}>
      <SkyBackdrop tall />
      <div className="relative z-10 flex min-h-screen flex-col px-6 pt-10 pb-8">
        <div className="flex items-center gap-2.5">
          <div className="sheen grid size-10 place-items-center overflow-hidden rounded-xl bg-primary font-display text-lg text-primary-foreground shadow-lg shadow-primary/30">B</div>
          <span className="font-display text-2xl tracking-tight text-primary-foreground">BOARDGO</span>
        </div>
        <h1 className="rise mt-12 font-display text-[56px] uppercase leading-[0.9] tracking-tight text-primary-foreground" style={{ animationDelay: "80ms" }}>
          Find people.<br />Find games.<br /><span className="text-meeple">Play together.</span>
        </h1>
        <div className="rise mt-8 overflow-hidden rounded-3xl ring-1 ring-glass/40" style={{ animationDelay: "160ms" }}>
          <img src={catan} alt="Catan board with meeples" width={1024} height={640} className="h-48 w-full object-cover" />
        </div>
        <div className="mt-auto flex flex-col gap-3 pt-8">
          <Link to="/register" className={btn}>Create account</Link>
          <Link to="/login" className="inline-flex items-center justify-center rounded-full bg-surface px-5 py-3 text-sm font-semibold ring-1 ring-border">I already have an account</Link>
          <Link to="/home" className="text-center font-mono text-[11px] text-muted-foreground">skip — just browse →</Link>
        </div>
      </div>
    </Screen>
  );
}

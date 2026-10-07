import { createFileRoute, Link } from "@tanstack/react-router";
import { Screen, TopBar, btn, btnGhost } from "@/components/bg";
import { PlayerView } from "@/components/PlayerView";
import { players } from "@/lib/data";
import { useStore } from "@/lib/store";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/profile/")({
  head: () => meta("My profile", "Your BoardGo player profile, ratings and favorite games."),
  component: Me,
});

function Me() {
  const { profile } = useStore();
  const me = players[0];
  return (
    <Screen>
      <TopBar light title="" />
      <PlayerView id="me" {...profile} rating={me.rating} games={me.games}
        actions={<><Link to="/profile/edit" className={btn + " flex-1"}>Edit profile</Link><Link to="/" className={btnGhost}>Log out</Link></>} />
    </Screen>
  );
}

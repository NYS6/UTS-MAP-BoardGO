import { createFileRoute } from "@tanstack/react-router";
import { Screen, TopBar } from "@/components/bg";
import { ProfileForm } from "@/components/ProfileForm";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/profile-setup")({
  head: () => meta("Set up your profile", "Tell BoardGo what you like to play and where."),
  component: () => (
    <Screen nav={false}>
      <TopBar back title="Your profile" />
      <p className="px-5 pb-5 text-sm text-muted-foreground">Help us match you with the right tables.</p>
      <ProfileForm after="/home" cta="Let's play" />
    </Screen>
  ),
});

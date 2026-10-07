import { createFileRoute } from "@tanstack/react-router";
import { Screen, TopBar } from "@/components/bg";
import { ProfileForm } from "@/components/ProfileForm";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/profile/edit")({
  head: () => meta("Edit profile", "Update your BoardGo player profile."),
  component: () => (
    <Screen>
      <TopBar back title="Edit profile" />
      <ProfileForm after="/profile" cta="Save changes" />
    </Screen>
  ),
});

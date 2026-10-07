import { createFileRoute } from "@tanstack/react-router";
import { AuthForm } from "@/components/AuthForm";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/login")({
  head: () => meta("Log in", "Log in to BoardGo and get back to the table."),
  component: () => <AuthForm mode="login" />,
});

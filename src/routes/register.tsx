import { createFileRoute } from "@tanstack/react-router";
import { AuthForm } from "@/components/AuthForm";
import { meta } from "@/lib/meta";

export const Route = createFileRoute("/register")({
  head: () => meta("Sign up", "Create your BoardGo account and find players near you."),
  component: () => <AuthForm mode="register" />,
});

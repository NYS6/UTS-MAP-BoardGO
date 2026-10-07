export const meta = (title: string, description: string) => ({
  meta: [
    { title: `${title} · BoardGo` },
    { name: "description", content: description },
    { property: "og:title", content: `${title} · BoardGo` },
    { property: "og:description", content: description },
  ],
});

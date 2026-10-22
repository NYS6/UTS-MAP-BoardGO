import catan from "@/assets/catan.jpg";
import wingspan from "@/assets/wingspan.jpg";
import azul from "@/assets/azul.jpg";
import splendor from "@/assets/splendor.jpg";
import ticket from "@/assets/ticket.jpg";
import mars from "@/assets/mars.jpg";
import mahjong from "@/assets/mahjong.jpg";
import chess from "@/assets/chess.jpg";
import checkers from "@/assets/checkers.jpg";
import monopoly from "@/assets/monopoly.jpg";
import scrabble from "@/assets/scrabble.jpg";
import werewolf from "@/assets/werewolf.jpg";

export const categories = ["Strategy", "Family", "Classic", "Tile & Card", "Party", "Word"] as const;
export type Category = (typeof categories)[number];

export type Game = {
  id: string; name: string; image: string; description: string;
  players: string; duration: string; difficulty: "Easy" | "Medium" | "Hard"; category: Category;
};
/** distance in km from the player; bearing in degrees (0 = north) used to place pins on the map */
export type Venue = { id: string; name: string; area: string; distance: number; bearing: number };
export type Player = {
  id: string; name: string; color: "p1" | "p2" | "p3" | "p4"; area: string; bio: string;
  rating: number; games: number; favorites: string[]; level: string;
};
export type Session = {
  id: string; gameId: string; venueId: string; hostId: string; day: string; time: string;
  max: number; playerIds: string[]; level: "Beginner" | "Casual" | "Competitive";
  note: string; fee: string;
};

export const games: Game[] = [
  { id: "catan", name: "Catan", image: catan, description: "Trade, build and settle the island of Catan. Gather resources and outmaneuver rivals.", players: "3–4", duration: "75 min", difficulty: "Medium", category: "Strategy" },
  { id: "wingspan", name: "Wingspan", image: wingspan, description: "Attract birds to your wildlife preserves in this engine-building card game.", players: "1–5", duration: "60 min", difficulty: "Medium", category: "Strategy" },
  { id: "mars", name: "Terraforming Mars", image: mars, description: "Lead a corporation to make Mars habitable. Heavy, rewarding strategy.", players: "1–5", duration: "120 min", difficulty: "Hard", category: "Strategy" },
  { id: "azul", name: "Azul", image: azul, description: "Draft gorgeous tiles to decorate the walls of the Royal Palace of Evora.", players: "2–4", duration: "40 min", difficulty: "Easy", category: "Tile & Card" },
  { id: "splendor", name: "Splendor", image: splendor, description: "Collect gems and build a Renaissance merchant empire.", players: "2–4", duration: "30 min", difficulty: "Easy", category: "Tile & Card" },
  { id: "mahjong", name: "Mahjong", image: mahjong, description: "The classic four-player tile game of skill, luck and sharp memory.", players: "4", duration: "90 min", difficulty: "Medium", category: "Tile & Card" },
  { id: "ticket", name: "Ticket to Ride", image: ticket, description: "Claim railway routes across the map and connect distant cities.", players: "2–5", duration: "60 min", difficulty: "Easy", category: "Family" },
  { id: "monopoly", name: "Monopoly", image: monopoly, description: "Buy, trade and build your way to a property empire. Friendships may vary.", players: "2–6", duration: "120 min", difficulty: "Easy", category: "Family" },
  { id: "chess", name: "Chess", image: chess, description: "The timeless duel of strategy. Casual or rated, every level welcome.", players: "2", duration: "30 min", difficulty: "Hard", category: "Classic" },
  { id: "checkers", name: "Checkers", image: checkers, description: "Jump, capture and crown your pieces in this quick classic.", players: "2", duration: "20 min", difficulty: "Easy", category: "Classic" },
  { id: "scrabble", name: "Scrabble", image: scrabble, description: "Build words on the grid and hunt for triple-word scores.", players: "2–4", duration: "60 min", difficulty: "Medium", category: "Word" },
  { id: "werewolf", name: "Werewolf", image: werewolf, description: "A big-group party game of bluffing, deduction and dramatic accusations.", players: "6–15", duration: "45 min", difficulty: "Easy", category: "Party" },
];

export const gamesByCategory = categories
  .map((c) => ({ category: c, games: games.filter((g) => g.category === c) }))
  .filter((x) => x.games.length);

export const venues: Venue[] = [
  { id: "v1", name: "Coffee & Dice", area: "BSD", distance: 0.6, bearing: 40 },
  { id: "v2", name: "The Board Room", area: "Gading Serpong", distance: 1.4, bearing: 250 },
  { id: "v3", name: "Rooftop Café", area: "BSD", distance: 0.9, bearing: 150 },
  { id: "v4", name: "Meeple House", area: "Alam Sutera", distance: 3.2, bearing: 10 },
  { id: "v5", name: "Roll & Brew", area: "Karawaci", distance: 5.1, bearing: 290 },
  { id: "v6", name: "Tabletop Hub", area: "Tangerang Selatan", distance: 2.4, bearing: 110 },
  { id: "v7", name: "Kopi Catur", area: "BSD", distance: 1.8, bearing: 195 },
  { id: "v8", name: "Pavilion Games", area: "Bintaro", distance: 4.3, bearing: 75 },
];

export const players: Player[] = [
  { id: "me", name: "Dimas Pratama", color: "p3", area: "BSD", bio: "Weekend strategist. Will trade sheep for wood.", rating: 4.8, games: 42, favorites: ["catan", "mars"], level: "Competitive" },
  { id: "u1", name: "Anisa Rahma", color: "p1", area: "Gading Serpong", bio: "Bird nerd. Wingspan every Friday.", rating: 4.9, games: 88, favorites: ["wingspan", "azul"], level: "Casual" },
  { id: "u2", name: "Raka Wijaya", color: "p2", area: "BSD", bio: "Teaching new players is my favorite thing.", rating: 4.7, games: 61, favorites: ["catan", "ticket"], level: "Casual" },
  { id: "u3", name: "Kevin Tan", color: "p3", area: "Alam Sutera", bio: "Heavy euros, long nights.", rating: 4.6, games: 120, favorites: ["mars", "splendor"], level: "Competitive" },
  { id: "u4", name: "Sari Putri", color: "p4", area: "Karawaci", bio: "Just here for the snacks and Azul.", rating: 4.9, games: 23, favorites: ["azul", "splendor"], level: "Beginner" },
  { id: "u5", name: "Bima Santoso", color: "p1", area: "Tangerang Selatan", bio: "Ticket to Ride world traveler.", rating: 4.5, games: 37, favorites: ["ticket"], level: "Casual" },
];

export const initialSessions: Session[] = [
  { id: "s1", gameId: "catan", venueId: "v1", hostId: "u2", day: "Tonight", time: "19:30", max: 4, playerIds: ["u2", "u1", "u3"], level: "Casual", note: "Base game + Seafarers if we're quick. Beginners welcome!", fee: "Free" },
  { id: "s2", gameId: "wingspan", venueId: "v2", hostId: "u1", day: "Tonight", time: "20:00", max: 5, playerIds: ["u1", "u4", "u5"], level: "Casual", note: "European expansion included.", fee: "Rp25k" },
  { id: "s3", gameId: "azul", venueId: "v3", hostId: "u4", day: "Tomorrow", time: "18:00", max: 4, playerIds: ["u4", "u2", "u5"], level: "Beginner", note: "Chill game, rules taught.", fee: "Free" },
  { id: "s4", gameId: "mars", venueId: "v4", hostId: "u3", day: "Sat", time: "13:00", max: 5, playerIds: ["u3", "me"], level: "Competitive", note: "Full corporate era with Prelude. Bring snacks.", fee: "Rp30k" },
  { id: "s5", gameId: "ticket", venueId: "v6", hostId: "u5", day: "Sat", time: "16:00", max: 5, playerIds: ["u5"], level: "Casual", note: "Europe map.", fee: "Free" },
  { id: "s6", gameId: "splendor", venueId: "v5", hostId: "u3", day: "Sun", time: "15:00", max: 4, playerIds: ["u3", "u4"], level: "Casual", note: "Quick games, multiple rounds.", fee: "Rp15k" },
  { id: "s8", gameId: "mahjong", venueId: "v1", hostId: "u4", day: "Tomorrow", time: "19:00", max: 4, playerIds: ["u4", "u1"], level: "Casual", note: "Hong Kong rules, we'll teach scoring.", fee: "Free" },
  { id: "s9", gameId: "chess", venueId: "v7", hostId: "u3", day: "Tonight", time: "18:30", max: 2, playerIds: ["u3"], level: "Competitive", note: "Rapid 15+10. Bring your best opening.", fee: "Free" },
  { id: "s10", gameId: "checkers", venueId: "v7", hostId: "u2", day: "Sun", time: "10:00", max: 2, playerIds: ["u2"], level: "Beginner", note: "Relaxed Sunday morning game.", fee: "Free" },
  { id: "s11", gameId: "monopoly", venueId: "v8", hostId: "u5", day: "Sat", time: "14:00", max: 6, playerIds: ["u5", "u1", "u4"], level: "Casual", note: "House rules: Free Parking jackpot.", fee: "Rp20k" },
  { id: "s12", gameId: "werewolf", venueId: "v6", hostId: "u1", day: "Sat", time: "19:00", max: 10, playerIds: ["u1", "u2", "u4", "u5"], level: "Beginner", note: "Big group night, moderator provided.", fee: "Rp10k" },
  { id: "s13", gameId: "scrabble", venueId: "v3", hostId: "u4", day: "Sun", time: "16:00", max: 4, playerIds: ["u4"], level: "Casual", note: "English dictionary, timer optional.", fee: "Free" },
  { id: "s7", gameId: "catan", venueId: "v2", hostId: "me", day: "Last Fri", time: "19:00", max: 4, playerIds: ["me", "u1", "u2", "u4"], level: "Casual", note: "Done! Great games.", fee: "Free" },
];

export const pastSessionIds = ["s7"];

export const getGame = (id: string) => games.find((g) => g.id === id)!;
export const getVenue = (id: string) => venues.find((v) => v.id === id)!;
export const getPlayer = (id: string) => players.find((p) => p.id === id) ?? players[0]!;

/** Map projection: the player sits at (50,50); MAP_KM is the width of the visible map in km. */
export const MAP_KM = 12;
export function venuePoint(v: Venue) {
  const r = (v.distance / MAP_KM) * 100;
  const a = (v.bearing * Math.PI) / 180;
  return { x: 50 + r * Math.sin(a), y: 50 - r * Math.cos(a) };
}
export function sessionsWithin<T extends { venueId: string }>(list: T[], maxKm: number) {
  return list
    .filter((s) => getVenue(s.venueId).distance <= maxKm)
    .sort((a, b) => getVenue(a.venueId).distance - getVenue(b.venueId).distance);
}

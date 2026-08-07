import { Picture } from "./image.interface";

export interface EventCard {
  id: number;
  title: string;
  picture: Picture;
  eventDate: string;
  updatedAt: string;
}

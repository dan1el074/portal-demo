import { Picture } from "./image.interface";
import { UserSummary } from "./user.interface";

export interface PostCard {
  id: number;
  author: UserSummary,
  createdAt: string,
  content: string,
  pictures: Array<Picture>,
}

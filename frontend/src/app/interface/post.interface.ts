import { Picture } from "./image.interface";
import { UserSummary } from "./user.interface";

export interface PostCard {
  id: number;
  author: UserSummary,
  createdAt: string,
  content: string,
  isWarning: boolean,
  pictures: Array<Picture>,
}

export interface NewPost {
  text: string;
  images: File[];
  isWarning: boolean;
}

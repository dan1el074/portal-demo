import { ImageCard } from "./image.interface";

export interface PostCard {
  id: number;
  author: string,
  authorPicture: string,
  position: string,
  instant: string,
  content: string,
  pictures: Array<ImageCard>,
}

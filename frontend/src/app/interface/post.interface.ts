import { ImageCard } from "./image.interface";

export interface PostCard {
  id: number;
  author: string,
  authorPictureId: number,
  position: string,
  instant: string,
  content: string,
  pictures: Array<ImageCard>,
}

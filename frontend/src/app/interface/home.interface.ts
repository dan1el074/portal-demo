import { Birthday } from "./birthday.interface";
import { EventCard } from "./event.interface";
import { FileCard } from "./file.interface";
import { PostCard } from "./post.interface";

export interface HomeInfo {
  upcomingEvents: number;
  openOrders: number
  openMemorandos: number;
  files: Array<FileCard>;
  event: EventCard;
  monthBirthdays: Array<Birthday>;
  todayBirthdays: Array<Birthday>;
  feed: Array<PostCard>;
}

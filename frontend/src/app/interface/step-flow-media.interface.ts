import { StepFlowImage } from './image.interface';
import { StepFlowVideo } from './step-flow.interface';

export type StepFlowMediaFilter = 'all' | 'video' | 'image';

export type StepFlowMedia =
  | (StepFlowImage & { type: 'image' })
  | (StepFlowVideo & { type: 'video' });

export function getSortedStepFlowMedia(
  pictures: Array<StepFlowImage>,
  videos: Array<StepFlowVideo>,
  filter: StepFlowMediaFilter
): Array<StepFlowMedia> {
  return [
    ...pictures.map(image => ({ ...image, type: 'image' as const })),
    ...videos.map(video => ({ ...video, type: 'video' as const })),
  ]
    .filter(item => filter === 'all' || item.type === filter)
    .sort((a, b) => toTimestamp(b.createdAt) - toTimestamp(a.createdAt));
}

function toTimestamp(value: string): number {
  const timestamp = new Date(value).getTime();
  return Number.isNaN(timestamp) ? 0 : timestamp;
}

import { Component, EventEmitter, Input, Output, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { AvatarComponent, CardBodyComponent, CardComponent } from '@coreui/angular';
import { Me } from '../../../../app/interface/user.interface';
import { PostCard } from '../../../../app/interface/post.interface';
import { environment } from '../../../../environments/environment';
import { NewPostModalComponent } from '../../../modal/post/new-post-modal/new-post-modal.component';

@Component({
  selector: 'app-new-post',
  imports: [CardComponent, CardBodyComponent, AvatarComponent, NewPostModalComponent],
  templateUrl: './new-post.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './new-post.component.scss',
})
export class NewPostComponent {
  @Input() user!: Me;
  @Output() publishTask = new EventEmitter<FormData>();
  @Output() editPostTask = new EventEmitter<{ id: number; data: FormData }>();
  @Output() eventTask = new EventEmitter<void>();
  @ViewChild(NewPostModalComponent) modal!: NewPostModalComponent;
  protected apiUrl = environment.apiUrl;
  protected showModal = false;
  protected showPictureDialog = false;
  protected editingPost: PostCard | null = null;

  protected openModal(withPicture = false): void {
    this.editingPost = null;
    this.showPictureDialog = withPicture;
    this.showModal = true;
  }

  protected closeModal(): void { this.showModal = false; }
  public stopLoad(): void { this.modal.stopLoad(); }
  public finishSubmit(): void { this.modal.stopLoad(); this.showModal = false; }

  protected publishPost(data: FormData): void {
    if (this.editingPost) this.editPostTask.emit({ id: this.editingPost.id, data });
    else this.publishTask.emit(data);
  }

  public editPost(post: PostCard): void {
    this.editingPost = post;
    this.showPictureDialog = false;
    this.showModal = true;
  }
}

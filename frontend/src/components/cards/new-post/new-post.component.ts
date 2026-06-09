import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AvatarComponent, CardBodyComponent, CardComponent } from '@coreui/angular';
import { Me } from '../../../app/interface/user.interface';
import { environment } from '../../../environments/environment';
import { NewPostModalComponent } from '../../modal/post/new-post-modal/new-post-modal.component';
import { NewPost } from '../../../app/interface/post.interface';

@Component({
  selector: 'app-new-post',
  imports: [
    CardComponent,
    CardBodyComponent,
    AvatarComponent,
    NewPostModalComponent
  ],
  templateUrl: './new-post.component.html',
  styleUrl: './new-post.component.scss',
})
export class NewPostComponent {
  @Input() user!: Me;
  @Output() publishTask = new EventEmitter<FormData>();
  protected apiUrl = environment.apiUrl;
  protected showModal = false;
  protected warning = false;
  protected showPictureDialog = false;

  protected openModal(): void {
    this.clearArgs();
    this.showModal = true;
  }

  protected openModalWithWarning(): void {
    this.clearArgs();
    this.warning = true;
    this.showModal = true;
  }

  protected openModalWithPicture(): void {
    this.clearArgs();
    this.showPictureDialog = true;
    this.showModal = true;
  }

  protected closeModal(): void {
    this.showModal = false;
  }

  private clearArgs(): void {
    this.warning = false;
    this.showPictureDialog = false;
  }

  protected publishPost(post: FormData): void {
    this.publishTask.emit(post);
  }
}

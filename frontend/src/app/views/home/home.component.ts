import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ContainerComponent } from '@coreui/angular';
import { HomeService } from './../../services/home.service';
import { UserService } from './../../services/user.service';
import { PostService } from './../../services/post.service';
import { BirthdaysComponent } from './../../../components/cards/birthdays/birthdays.component';
import { PostComponent } from './../../../components/cards/post/post.component';
import { EventComponent } from './../../../components/cards/event/event.component';
import { FilesComponent } from './../../../components/cards/files/files.component';
import { HelloComponent } from '../../../components/cards/hello/hello.component';
import { NewPostComponent } from './../../../components/cards/post/new-post/new-post.component';
import { DeletePostModalComponent } from '../../../components/modal/post/delete-post-modal/delete-post-modal.component';
import { HomeInfo } from '../../interface/home.interface';
import { Me } from '../../interface/user.interface';
import { PostCard } from '../../interface/post.interface';
import { EventService } from '../../services/event.service';
import { EventCard } from '../../interface/event.interface';
import { NewEventModalComponent } from '../../../components/modal/event/new-event-modal/new-event-modal.component';

@Component({
  selector: 'app-home',
  imports: [
    ContainerComponent,
    FilesComponent,
    EventComponent,
    PostComponent,
    HelloComponent,
    BirthdaysComponent,
    NewPostComponent,
    DeletePostModalComponent
    ,NewEventModalComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild(NewPostComponent) newPost!: NewPostComponent;
  @ViewChild(NewEventModalComponent) eventModal!: NewEventModalComponent;
  protected user!: Me;
  protected fatalError = false;
  protected homeInfo!: HomeInfo;
  protected canPost = false;
  protected showDeleteModel = false;
  protected idPostToDelete = 0;
  protected showEventModal = false;
  protected editingEvent: EventCard | null = null;
  protected showDeleteEventModal = false;
  protected idEventToDelete = 0;
  // carregar mais posts
  private _sentinel!: ElementRef;
  protected loadingMore = false;
  protected noMorePosts = false;
  private observer!: IntersectionObserver;
  @ViewChild('sentinel')
  set sentinel(el: ElementRef) {
    if (el && !this.observer) {
      this._sentinel = el;
      this.setupIntersectionObserver();
    }
  }

  constructor(
    private homeService: HomeService,
    private toasterService: ToastrService,
    private userService: UserService,
    private postService: PostService,
    private eventService: EventService,
    private spinner: NgxSpinnerService,
    private cdf: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.updateData();

    this.userService.user$.subscribe(user => {
      if (!user) return;

      if (user.roles.findIndex(role => role.authority == 'ROLE_POST') >= 0) {
        this.canPost = true;
      }

      this.user = user;
    });

    setTimeout(() => {
      this.spinner.hide("loginSpinner")
    }, 500);
  }

  public ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  private setupIntersectionObserver(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting && !this.loadingMore && !this.noMorePosts) {
          this.loadMorePosts();
        }
      },
      { root: null, rootMargin: '0px', threshold: 0.1 }
    );

    this.observer.observe(this._sentinel.nativeElement);
  }

  private loadMorePosts(): void {
    console.log("carregando publicações...");
    if (!this.homeInfo?.feed?.length) return;

    const lastId = this.homeInfo.feed[this.homeInfo.feed.length - 1].id;
    this.loadingMore = true;

    this.postService.getFeedFromId(lastId).subscribe({
      next: (newPosts: Array<PostCard>) => {
        if (!newPosts.length) {
          this.noMorePosts = true;
        } else {
          this.homeInfo!.feed = [...this.homeInfo!.feed, ...newPosts];
        }

        this.loadingMore = false;
        this.cdf.detectChanges();
      },
      error: () => {
        this.loadingMore = false;
      }
    });
  }

  private updateData(): void {
    this.homeService.getHomeInfo().subscribe({
      next: (data) => {
        data.events = data.events?.length ? data.events : data.event ? [data.event] : [];
        this.homeInfo = data;
        this.cdf.detectChanges();
      },
      error: () => {
        this.toasterService.error("Erro ao obter informações!");
        this.fatalError = true;
      }
    });
  }

  protected toggleEditModal(id: number, status: boolean): void {
    if (!status) return;
    const post = this.homeInfo.feed.find(item => item.id === id);
    if (post) this.newPost.editPost(post);
  }

  protected editEvent(event: EventCard): void {
    this.editingEvent = event;
    this.showEventModal = true;
  }

  protected openEventModal(): void { this.editingEvent = null; this.showEventModal = true; }
  protected closeEventModal(): void { this.showEventModal = false; this.editingEvent = null; }
  protected toggleDeleteEventModal(id: number, visible: boolean): void { this.idEventToDelete = id; this.showDeleteEventModal = visible; }

  protected toggleDeleteModal(id: number, status: boolean): void {
    this.showDeleteModel = status;
    if (status) this.idPostToDelete = id;
  }

  protected insertPost(post: FormData): void {
    this.postService.insert(post).subscribe({
      next: () => {
        this.newPost.finishSubmit()
        this.toasterService.success("Publicação enviada com sucesso!")
        this.updateData();
      },
      error: () => {
        this.newPost.stopLoad()
        this.toasterService.error("Erro ao enviar publicação!")
      }
    });
  }

  protected editPost(submission: { id: number; data: FormData }): void {
    this.postService.update(submission.id, submission.data).subscribe({
      next: () => {
        this.newPost.finishSubmit();
        this.toasterService.success("Post editado com sucesso!");
        this.updateData();
      },
      error: () => {
        this.newPost.stopLoad();
        this.toasterService.error("Erro ao editar post!");
      }
    });
  }

  protected insertEvent(data: FormData): void {
    this.eventService.insert(data).subscribe({
      next: () => {
        this.eventModal.finishSubmit();
        this.toasterService.success("Evento criado com sucesso!");
        this.updateData();
      },
      error: () => {
        this.eventModal.stopLoad();
        this.toasterService.error("Erro ao criar evento!");
      }
    });
  }

  protected updateEvent(submission: { id: number; data: FormData }): void {
    this.eventService.update(submission.id, submission.data).subscribe({
      next: () => {
        this.eventModal.finishSubmit();
        this.toasterService.success("Evento editado com sucesso!");
        this.updateData();
      },
      error: () => {
        this.eventModal.stopLoad();
        this.toasterService.error("Erro ao editar evento!");
      }
    });
  }

  protected saveEvent(submission: { id?: number; data: FormData }): void {
    if (submission.id) this.updateEvent({ id: submission.id, data: submission.data });
    else this.insertEvent(submission.data);
  }

  protected deleteEvent(id: number): void {
    this.eventService.delete(id).subscribe({
      next: () => { this.showDeleteEventModal = false; this.toasterService.success('Evento apagado com sucesso!'); this.updateData(); },
      error: () => this.toasterService.error('Erro ao apagar evento!')
    });
  }

  protected deletePost(id: number): void {
    this.postService.delete(id).subscribe({
      next: () => {
        this.showDeleteModel = false;
        this.toasterService.success("Post deletado com sucesso!");
        this.updateData();
      },
      error: () => this.toasterService.error("Erro ao deletar post!")
    });
  }
}

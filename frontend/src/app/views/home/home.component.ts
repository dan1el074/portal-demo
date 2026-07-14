import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ButtonDirective, ContainerComponent } from '@coreui/angular';
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
import { NewPost, PostCard } from '../../interface/post.interface';

@Component({
  selector: 'app-home',
  imports: [
    ContainerComponent,
    ButtonDirective,
    FilesComponent,
    EventComponent,
    PostComponent,
    HelloComponent,
    BirthdaysComponent,
    NewPostComponent,
    DeletePostModalComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild(NewPostComponent) newPost!: NewPostComponent;
  protected user!: Me;
  protected fatalError = false;
  protected homeInfo!: HomeInfo;
  protected isAdmin = false;
  protected canPost = false;
  protected showDeleteModel = false;
  protected idPostToDelete = 0;
  protected showEditModel = false;
  protected idPostToEdit = 0;

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
    private spinner: NgxSpinnerService,
    private cdf: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.updateData();

    this.userService.user$.subscribe(user => {
      if (!user) return;

      if (user.roles.findIndex(role => role.authority == 'ROLE_ADMIN') >= 0) {
        this.isAdmin = true;
      }

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
        this.homeInfo = data;
        this.cdf.detectChanges();
      },
      error: () => {
        this.toasterService.error("Erro ao obter informações!");
        this.fatalError = true;
      }
    });
  }

  protected clearCache(): void {
    this.homeService.clearAllCache().subscribe({
      next: () => this.toasterService.success("Cache limpo com sucesso!"),
      error: () => this.toasterService.error("Erro ao limpar cache!")
    });
  }

  protected toggleEditModal(id: number, status: boolean): void {
    this.toasterService.info("Isso será implementado nas próximas atualizações.");

    // this.showEditModel = status;
    // if (status) this.idPostToEdit = id;
  }

  protected toggleDeleteModal(id: number, status: boolean): void {
    this.showDeleteModel = status;
    if (status) this.idPostToDelete = id;
  }

  protected insertPost(post: FormData): void {
    this.postService.insert(post).subscribe({
      next: () => {
        this.newPost.stopLoad()
        this.toasterService.success("Publicação enviada com sucesso!")
        this.updateData();
      },
      error: () => {
        this.newPost.stopLoad()
        this.toasterService.error("Erro ao enviar publicação!")
      }
    });
  }

  protected editPost(id: number, post: NewPost): void {
    this.postService.update(id, post).subscribe({
      next: () => {
        this.showEditModel = false;
        this.toasterService.success("Post deletado com sucesso!");
        this.updateData();
      },
      error: () => this.toasterService.error("Erro ao deletar post!")
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

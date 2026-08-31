import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DocumentViewComponent } from './document-view.component';

describe('DocumentViewComponent', () => {
  let component: DocumentViewComponent;
  let fixture: ComponentFixture<DocumentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentViewComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentViewComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows a canceled warning instead of the unpublished signatures message', () => {
    component.ngOnInit = () => {};
    (component as any).loading = false;
    (component as any).item.status = 'CANCELED';

    fixture.detectChanges();

    const warning = fixture.nativeElement.querySelector('.signature-state.canceled') as HTMLElement;
    expect(warning).toBeTruthy();
    expect(warning.textContent).toContain(
      'Nenhuma assinatura poderá ser adicionada porque o memorando foi cancelado.'
    );
    expect(warning.textContent).not.toContain('As assinaturas serão liberadas após a publicação.');
  });

  it('shows the cancel action for an approved memorando to administrators', () => {
    component.ngOnInit = () => {};
    (component as any).loading = false;
    (component as any).item.status = 'APPROVED';
    (component as any).isAdmin = true;

    fixture.detectChanges();
    expect(cancelButton()).toBeTruthy();
  });

  function cancelButton(): HTMLButtonElement | undefined {
    return Array.from(
      fixture.nativeElement.querySelectorAll('.document-actions button') as NodeListOf<HTMLButtonElement>
    ).find(button => button.textContent?.trim() === 'Cancelar');
  }
});

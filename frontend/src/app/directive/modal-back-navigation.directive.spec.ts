import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ModalComponent } from '@coreui/angular';
import { BackNavigationService } from '../services/back-navigation.service';
import { ModalBackNavigationDirective } from './modal-back-navigation.directive';

@Component({
  imports: [ModalComponent, ModalBackNavigationDirective],
  template: '<c-modal modalBackNavigation [visible]="visible" (visibleChange)="visible = $event"></c-modal>',
})
class TestHostComponent {
  visible = false;
}

describe('ModalBackNavigationDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let modal: ModalComponent;
  let directive: ModalBackNavigationDirective;
  let backNavigation: jasmine.SpyObj<BackNavigationService>;

  beforeEach(async () => {
    backNavigation = jasmine.createSpyObj<BackNavigationService>(
      'BackNavigationService',
      ['register', 'unregister']
    );

    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
      providers: [{ provide: BackNavigationService, useValue: backNavigation }],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    modal = fixture.debugElement.query(By.directive(ModalComponent)).componentInstance;
    directive = fixture.debugElement
      .query(By.directive(ModalBackNavigationDirective))
      .injector.get(ModalBackNavigationDirective);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('registers navigation when the modal opens', async () => {
    modal.visible.set(true);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(directive).toBeTruthy();
    expect(modal.visible()).toBeTrue();
    expect(backNavigation.register).toHaveBeenCalledTimes(1);

    modal.visible.set(false);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('closes the modal without unregistering again when browser back is used', async () => {
    modal.visible.set(true);
    fixture.detectChanges();
    await fixture.whenStable();
    const onBack = backNavigation.register.calls.mostRecent().args[0];

    onBack();
    fixture.detectChanges();
    await fixture.whenStable();

    expect(modal.visible()).toBeFalse();
    expect(backNavigation.unregister).not.toHaveBeenCalled();
  });

  it('unregisters navigation when the modal closes normally', async () => {
    modal.visible.set(true);
    fixture.detectChanges();
    await fixture.whenStable();

    modal.visible.set(false);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(backNavigation.unregister).toHaveBeenCalledTimes(1);
  });
});

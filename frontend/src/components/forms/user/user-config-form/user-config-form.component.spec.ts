import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserConfigFormComponent } from './user-config-form.component';

describe('UserConfigFormComponent', () => {
  let component: UserConfigFormComponent;
  let fixture: ComponentFixture<UserConfigFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserConfigFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserConfigFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

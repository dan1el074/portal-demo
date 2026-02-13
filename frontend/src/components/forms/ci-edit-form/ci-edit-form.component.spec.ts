import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CiEditFormComponent } from './ci-edit-form.component';

describe('CiEditFormComponent', () => {
  let component: CiEditFormComponent;
  let fixture: ComponentFixture<CiEditFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CiEditFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CiEditFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

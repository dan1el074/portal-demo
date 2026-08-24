import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RawMaterialsTableComponent } from './raw-materials-table.component';

describe('RawMaterialsTableComponent', () => {
  let component: RawMaterialsTableComponent;
  let fixture: ComponentFixture<RawMaterialsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RawMaterialsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RawMaterialsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hide code and category columns in operator mode', () => {
    component.mode = 'operator';
    component.ngOnChanges();

    const columns = (component as any).columns;
    expect(columns.map((column: any) => column.key)).toEqual([
      'name',
      'currentStorage',
      'updateAt',
    ]);
  });

  it('should keep code and category columns outside operator mode', () => {
    component.mode = 'consultation';
    component.ngOnChanges();

    const columns = (component as any).columns;
    expect(columns.map((column: any) => column.key)).toContain('code');
    expect(columns.map((column: any) => column.key)).toContain('type');
  });
});

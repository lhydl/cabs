import { ComponentFixture, TestBed } from '@angular/core/testing';
import BookConsultationComponent from './bookConsultation.component';

describe('BookConsulationComponent', () => {
  let component: BookConsultationComponent;
  let fixture: ComponentFixture<BookConsultationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookConsultationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BookConsultationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

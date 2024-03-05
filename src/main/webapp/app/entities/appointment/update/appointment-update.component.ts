import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAppointment } from '../appointment.model';
import { AppointmentService } from '../service/appointment.service';
import { AppointmentFormService, AppointmentFormGroup } from './appointment-form.service';

@Component({
  standalone: true,
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving = false;
  appointment: IAppointment | null = null;

  editForm: AppointmentFormGroup = this.appointmentFormService.createAppointmentFormGroup();

  constructor(
    protected appointmentService: AppointmentService,
    protected appointmentFormService: AppointmentFormService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointment }) => {
      this.appointment = appointment;
      if (appointment) {
        this.updateForm(appointment);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appointment = this.appointmentFormService.getAppointment(this.editForm);
    if (appointment.id !== null) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      this.subscribeToSaveResponse(this.appointmentService.create(appointment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    // this.previousState();
    this.router.navigate(['/appointment']);
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(appointment: IAppointment): void {
    this.appointment = appointment;
    this.appointmentFormService.resetForm(this.editForm, appointment);
  }
}

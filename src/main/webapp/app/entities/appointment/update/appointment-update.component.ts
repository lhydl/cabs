import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormBuilder, FormsModule, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';

import { IAppointment } from '../appointment.model';
import { AppointmentService } from '../service/appointment.service';
import { AppointmentFormService, AppointmentFormGroup } from './appointment-form.service';
import { faAnglesDown, faMillSign } from '@fortawesome/free-solid-svg-icons';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { UserManagementService } from 'app/admin/user-management/service/user-management.service';
import { User } from 'app/admin/user-management/user-management.model';
import { DATE_FORMAT, TIME_FORMAT } from 'app/config/input.constants';
import dayjs, { Dayjs } from 'dayjs/esm';
import { addMinutes, format } from 'date-fns';

@Component({
  standalone: true,
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html',
  styleUrls: ['./appointment-update.component.scss'],
  imports: [SharedModule, FormsModule, ReactiveFormsModule, HasAnyAuthorityDirective],
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving = false;
  appointment: IAppointment | null = null;
  isNewPatient: boolean = false;
  account: Account | null = null;
  isEdit: boolean = false;
  isAdmin: boolean = this.accountService.hasAnyAuthority('ROLE_ADMIN');
  userList: User[] | null = null;
  apptTypeList: string[] = ['Consultation', 'Urgent Care', 'Dental', 'Pharmacy'];
  timeslots: string[] = [];

  editForm: AppointmentFormGroup = this.appointmentFormService.createAppointmentFormGroup({ id: null }, this.isNewPatient, this.isAdmin);

  private readonly destroy$ = new Subject<void>();

  constructor(
    protected appointmentService: AppointmentService,
    protected appointmentFormService: AppointmentFormService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected fb: FormBuilder,
    private accountService: AccountService,
    private userService: UserManagementService,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    this.activatedRoute.data.subscribe(({ appointment }) => {
      this.appointment = appointment;
      if (appointment) {
        this.isEdit = true;
        this.updateForm(appointment);
      }
    });
    if (this.isAdmin) {
      this.getUserList();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public toggleIsNewPatient(): void {
    this.initializeForm();
  }

  public getUserList(): void {
    console.log('button clicked');
    this.userService.getUserList().subscribe((res: any) => {
      if (res) {
        console.log('res:::' + res);
        this.userList = res;
      } else {
        this.userList = [];
      }
    });
  }

  initializeForm(): void {
    this.editForm = this.appointmentFormService.createAppointmentFormGroup({ id: null }, this.isNewPatient, this.isAdmin);
  }

  previousState(): void {
    window.history.back();
  }

  generateTimeSlots(event?: Event, patchedDate?: string): void {
    let selectedDate = null;
    if (event === undefined) {
      selectedDate = patchedDate; // For edit appt
    } else {
      const input = event.target as HTMLInputElement;
      selectedDate = input.value; // For create new appt
    }
    // TODO-> write api: pass selectedDate to backend to get existing appointment time, then block that time
    const startTime = new Date('2000-01-01T08:00:00'); // 08:00 AM
    const endTime = new Date('2000-01-01T20:00:00'); // 08:00 PM
    let currentTime = startTime;
    while (currentTime < endTime) {
      const timeLabel = `${format(currentTime, 'HH:mm')}`;
      // TODO-> if timeLabel === existing appointment time, skip push
      this.timeslots.push(timeLabel);
      currentTime = addMinutes(currentTime, 30); // One time slot every 30 mins
    }
  }

  save(): void {
    this.isSaving = true;
    const appointment = this.appointmentFormService.getAppointment(this.editForm);
    if (appointment.apptDate && appointment.apptTime) {
      const datetimeString = `${appointment.apptDate} ${appointment.apptTime}`;
      appointment.apptDatetime = dayjs(datetimeString);
    }
    if (appointment.id !== null) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      if (appointment.patientId === null) {
        appointment.patientId = this.account?.id;
      }
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
    const patchedDate = appointment.apptDatetime;
    const dateLabel = patchedDate?.format('YYYY-MM-DD');
    this.generateTimeSlots(undefined, dateLabel?.toString());
    this.appointmentFormService.resetForm(this.editForm, appointment);
  }
}

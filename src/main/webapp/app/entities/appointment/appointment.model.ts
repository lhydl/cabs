import dayjs from 'dayjs/esm';

export interface IAppointment {
  id: number;
  apptType?: string | null;
  apptDatetime?: dayjs.Dayjs | null;
  remarks?: string | null;
  patientId?: number | null;
  doctorId?: number | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
}

export type NewAppointment = Omit<IAppointment, 'id'> & { id: null };

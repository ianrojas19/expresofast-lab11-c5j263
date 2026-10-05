import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Envio, EnvioRegistroPayload, CheckTrackingResponse } from '../models/envio.model';

@Injectable({
  providedIn: 'root'
})
export class EnvioService {
  private http = inject(HttpClient);
  private apiUrl = environment.API_URL + 'envios';

  obtenerEnvios(): Observable<Envio[]> {
    return this.http.get<Envio[]>(this.apiUrl);
  }

  obtenerPorRastreo(codigo: string): Observable<Envio> {
    return this.http.get<Envio>(`${this.apiUrl}/rastreo/${codigo}`);
  }

  crearEnvio(payload: EnvioRegistroPayload): Observable<Envio> {
    return this.http.post<Envio>(this.apiUrl, payload);
  }

  actualizarEstado(id: number, nuevoEstado: string): Observable<Envio> {
    return this.http.patch<Envio>(`${this.apiUrl}/${id}/estado`, nuevoEstado);
  }

  checkTracking(trackingNumber: string): Observable<CheckTrackingResponse> {
    return this.http.get<CheckTrackingResponse>(
      `${this.apiUrl}/check-tracking/${encodeURIComponent(trackingNumber)}`
    );
  }
}
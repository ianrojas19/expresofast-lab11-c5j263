export interface Paquete {
  id?: number;
  descripcion: string;
  pesoKg: number;
}

export interface Envio {
  id: number;
  codigoRastreo: string;
  destinatario: string;
  direccionDestino: string;
  montoFlete: number;
  estado: string;
  fechaCreacion: string;
  fechaDespacho?: string;
  fechaEntregaEstimada?: string;
  paquetes?: Paquete[];
}

export interface EnvioRegistroPayload {
  numeroTracking: string;
  destinatario: string;
  direccionDestino: string;
  montoFlete: number;
  fechaDespacho: string;
  fechaEntregaEstimada: string;
  paquetes: Paquete[];
}

export interface CheckTrackingResponse {
  existe: boolean;
}
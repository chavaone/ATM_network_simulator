/***************************************************************************
 *	ATM Network Simulator ACS. FIC. UDC. 2012/2013									*
 *	Copyright (C) 2013 by Pablo Castro and Marcos Chavarria 						*
 * <pablo.castro1@udc.es>, <marcos.chavarria@udc.es> 								*
 * 																								*
 * This program is free software; you can redistribute it and/or modify 	*
 * it under the terms of the GNU General Public License as published by 	*
 * the Free Software Foundation; either version 2 of the License, or 		*
 * (at your option) any later version. 												*
 ***************************************************************************/
package practicaacs.banco.estados;

import practicaacs.fap.Mensaje;
import practicaacs.banco.Banco;

public abstract class EstadoSesion {

	public abstract void analizarMensaje(Mensaje m, Banco b);

	public boolean sesionAberta(){
		return true;
	}

	public boolean traficoActivo(){
		return true;
	}

	public boolean recuperacion() {
		return false;
	}

	/**
	 * Método utilizado para la transformacion desde los estados devueltos por la BD 
	 * a EstadoSesion correspondiente.
	 * @param valor EL valor entero a convertir.
	 * @return El EstadoSesion correspondiente al entero.
	 */
	static public EstadoSesion getEstadoSesion_fromInt(int valor){
	
		switch(valor){
			case 1:{
				return SesAberta.instance();			
			}
			case 2:{
				return SesNonAberta.instance();
			}
			case 3:{
				return SesDetida.instance();
			}
			case 4:{
				return SesRecuperacion.instance();
			}
		}
		return SesNonAberta.instance();
	}
	
	/**
	 * Método que transforma el valor EstadoSesion indicado por parámetro en el valor
	 * del estado correspondiente para que la BD lo interprete (en formato de int)
	 * @param valor EL valor en EstadoSesion.
	 * @return El entero correspondiente al EstadoSesion introducido por parámetro.
	 */
	static public int getInt_fromEstadoSesion(EstadoSesion valor){
	
		if(valor.equals(SesAberta.instance()))
			return 1;
		if(valor.equals(SesNonAberta.instance()))
			return 2;
		if(valor.equals(SesDetida.instance()))
			return 3;
		if(valor.equals(SesRecuperacion.instance()))
			return 4;
		return 0;
	}
		
}






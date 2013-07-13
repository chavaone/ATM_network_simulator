package practicaacs.consorcio.bd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import practicaacs.banco.estados.EstadoSesion;
import practicaacs.banco.estados.SesAberta;
import practicaacs.banco.estados.SesNonAberta;
import practicaacs.consorcio.aux.Movimiento;
import practicaacs.consorcio.aux.TipoOrigDest;
import practicaacs.fap.*;


/**
 * Libreria de acceso a la base de datos
 */
public class Database_lib {
	
	static private Database_lib instancia;

	private Connection con;
	private Statement statement = null;
	
	/**
	 * Constructor de la clase que realiza el acceso a la BASE DE DATOS
	 */
	private Database_lib() {
		
    	//Obtenemos los datos del fichero properties
		Properties prop = new Properties();
		InputStream is;
		//LA SITUACION DEL FICHERO DE CONFIGURACION
		//String file = "/home/ch01/UNI/ACS/RepositorioPractica/res/consorcioBD.properties";
		String file = "/home/castrinho8/Escritorio/UNI/ACS/res/consorcioBD.properties";

		try {
			is = new FileInputStream(file);
		    prop.load(is);
		} catch (FileNotFoundException e) {
			System.err.println("No se encontró el archivo de configuración " + file + ".");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error cargando el fichero de configuración " + file + ".");
			System.exit(-1);
		}
		
		String bdname = prop.getProperty("consorcio.bd.name");
		String bdadd = prop.getProperty("consorcio.bd.add");
		String bduser = prop.getProperty("consorcio.bd.user");
		String bdpass = prop.getProperty("consorcio.bd.pass");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Error creando el driver.");
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			con = DriverManager.getConnection("jdbc:mysql://" + bdadd + "/" + bdname + "?user=" + bduser + "&password=" + bdpass);
			statement = con.createStatement();
		} catch (SQLException e) {
			System.err.println("Error al conectar con la BD. " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}	
	}
	
	
	/**
	 * Devuelve la instancia del singleton
	 * @return El singleton de acceso a la base de datos
	 */
	static public Database_lib getInstance(){
		if(instancia == null)
			instancia = new Database_lib();
		
		return instancia;
	}
	
	
	/*---------------------------------------------------
	 -------------- COMPROBACION DE BANCOS --------------
	 --------------- CUENTAS Y TARJETAS -----------------
	 ----------------------------------------------------*/	
	
	
	/**
	 * Método que comprueba si existe la pareja Tarjeta,Cuenta en la BD. En caso de que no exista la inserta.
	 * @param tarjeta El string correspondiente a la tarjeta.
	 * @param cuenta El int correspondiente a la cuenta.
	 */
	private void comprueba_cuenta(String tarjeta,int cuenta){
		
		ResultSet resultSet;
		//Obtiene la cuenta de la BD
		try {
			resultSet = this.statement.executeQuery("SELECT codCuenta FROM Cuenta" +  
				" WHERE codTarjeta = '" + tarjeta + "' AND codCuenta = " + cuenta);
			
			if(resultSet.next())
				return;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//En caso de que no haya, se introduce en la BD
		try {
			this.statement.executeUpdate("INSERT INTO Cuenta(codTarjeta,codCuenta,cusaldo)" +
					" VALUES('" + tarjeta + "'," + cuenta + ",0)");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Método que comprueba si existe la tarjeta en la BD. En caso de que no exista la inserta.
	 * @param tarjeta El string correspondiente a la tarjeta.
	 */
	private void comprueba_tarjeta(String tarjeta){
		
		ResultSet resultSet;
		//Obtiene la tarjeta de la BD
		try {
			resultSet = this.statement.executeQuery("SELECT codTarjeta FROM Tarjeta" +  
				" WHERE codTarjeta = '" + tarjeta + "'");
			
			if(resultSet.next())
				return;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//En caso de que no haya, se introduce en la BD
		try {
			this.statement.executeUpdate("INSERT INTO Tarjeta(codTarjeta,tagastoOffline)" +
					" VALUES('" + tarjeta + "',0)");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Comprueba si un banco existe y en caso contrario lo inserta.
	 * @param id_banco El banco a comprobar
	 * @return El int con el codigo que identifica al banco en la BD
	 * @throws ConsorcioBDException Lanza una excepcion cuando no existe el banco y no puede ser añadido
	 */
	private int comprueba_banco(String id_banco) throws ConsorcioBDException{

		//Comprueba si existe, si existe devuelve el codigo de la BD que lo identifica
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco WHERE codBanco ='" + id_banco + "'");

			if(resultSet.next())
				return resultSet.getInt(1);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Inserta el banco y devuelve el codigo de la BD que lo identifica
		try {
			this.statement.executeUpdate("INSERT INTO Banco(codBanco,codEBanco,bamaxCanales)" +
					" VALUES('"+id_banco+"',2,0)");
			
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco WHERE codBanco ='" + id_banco + "'");

			if(resultSet.next())
				return resultSet.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		throw new ConsorcioBDException("No existe el banco y no puede ser añadido.");
	}
	
	/**
	 * Método que comprueba si existe la tarjeta indicada y devuelve un booleano que lo indica.
	 * @param tarjeta La tarjeta a comprobar
	 * @return True si existe y False en caso contrario
	 */
	private boolean existeTarjeta(String tarjeta){
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codTarjeta FROM Tarjeta WHERE codTarjeta ='" + tarjeta + "'");

			return resultSet.next();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	
	/**
	 * Método que comprueba si existe la cuenta para la tarjeta indicadas y devuelve un booleano que lo indica.
	 * @param tarjeta La tarjeta a la que pertenece la cuenta
	 * @param cuenta La cuenta a comprobar
	 * @return True si existe y False en caso contrario
	 */
	private boolean existeCuenta(String tarjeta, int cuenta){
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codCuenta FROM Cuenta " +
					"WHERE codTarjeta='"+tarjeta+"' AND codCuenta =" + cuenta);

			return resultSet.next();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	
	/*---------------------------------------------------
	 --------------------- CONSULTAS --------------------
	 ----------------------------------------------------*/	
	
	/**
	 * Método que comprueba si se dan las condiciones necesarias para realizar la operación y 
	 * devuelve el CodigosRespuesta correspondiente, CORRECTO si se dan y el codigo correspondiente 
	 * en caso contrario.
	 * @param tarjeta La tarjeta que realiza la operación.
	 * @param cuenta_origen Si es un traspaso, la cuenta de la que se obtiene el dinero.
	 * @param cuenta_destino La cuenta sobre la que se realiza la operacion
	 * @param tipo El tipo de operación que se realiza.
	 * @param importe La cantidad de dinero que se maneja en la operación.
	 * @return El CodigosRespuesta correspondiente en función de si ha habido errores o no.
	 */
	public CodigosRespuesta comprobar_condiciones(String tarjeta, int cuenta_origen, int cuenta_destino,
			CodigosMensajes tipo, int importe, boolean codonline){
		
		if (!this.existeTarjeta(tarjeta))
			return CodigosRespuesta.TARJETANVALIDA;
		
		if (!(tipo.equals(CodigosMensajes.SOLTRASPASO)) && (!this.existeCuenta(tarjeta,cuenta_destino)))
			return CodigosRespuesta.CUENTANVALIDA;
		
		if ((tipo.equals(CodigosMensajes.SOLTRASPASO)) && (!this.existeCuenta(tarjeta,cuenta_origen)))
			return CodigosRespuesta.TRANSCUENTAORINVALIDA;

		if ((tipo.equals(CodigosMensajes.SOLTRASPASO)) && (!this.existeCuenta(tarjeta,cuenta_destino)))
			return CodigosRespuesta.TRANSCUENTADESNVALIDA;
		
		/**if (((tipo.equals(CodigosMensajes.SOLSALDO)) || (tipo.equals(CodigosMensajes.SOLMOVIMIENTOS)))
				&& (!codonline))
			return CodigosRespuesta.CONSDEN;
		*/
		if ((this.consultarGastoOffline(tarjeta)+importe) > 1000)
			return CodigosRespuesta.IMPORTEEXCLIMITE;

		if ((tipo.equals(CodigosMensajes.SOLTRASPASO)) && (importe > 9999))
			return CodigosRespuesta.IMPORTEEXCLIMITE;

		if ((tipo.equals(CodigosMensajes.SOLTRASPASO)) && (cuenta_origen == cuenta_destino))
			return CodigosRespuesta.TRANSCUENTASIGUALES;
		
		if ((tipo.equals(CodigosMensajes.SOLTRASPASO)) && (this.consultar_saldo(tarjeta,cuenta_origen) < importe))
			return CodigosRespuesta.TRANSSINFONDOS;
		
		return CodigosRespuesta.CONSACEPTADA;
/*		CAPTARJ(11,"Consulta Denegada con Captura de Tarjeta."), 
	*/	
	}
	
	
	/**
	 * Actualiza el gasto offline para la tarjeta indicada.
	 * @param tarjeta La tarjeta para actualizar.
	 * @param importe El importe a sumar.
	 */
	private void actualiza_GastoOffline(String tarjeta,int importe){
		
		//Comprobamos si existen tarjeta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("actualiza_GastoOffline: La tarjeta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Realizar la actualizacion
		try {
			this.statement.executeUpdate("UPDATE Tarjeta SET tagastoOffline=tagastoOffline+" + importe +  
				" WHERE codTarjeta = '" + tarjeta + "'");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	
	/**
	 * Recalcula el saldo actual de la cuenta indicada.
	 * @param cuenta La cuenta a actualizar el saldo.
	 * @param importe El importe a modificar.
	 * @param signo El signo que indica si se debe sumar o restar.
	 */
	private void recalcular_saldoActual(int cuenta,String tarjeta, int importe, char signo){
	
		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("recalcular_saldoActual: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta
			if(!this.existeCuenta(tarjeta, cuenta))
				throw new ConsorcioBDException("recalcular_saldoActual: La cuenta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Realizamos la actualizacion del saldo
		try {
			this.statement.executeUpdate("UPDATE Cuenta SET cusaldo = cusaldo"+ signo + importe +
				" WHERE codCuenta = " + cuenta + " AND codTarjeta = '" + tarjeta + "'");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		} 

	}
	
	
	/**
	 * Obtiene el saldo a partir del atributo que hay en cuenta.
	 * ERROR: Devuelve -1 si hay error
	 * @param cuenta La cuenta a consultar.
	 * @return Devuelve el saldo actual de la cuenta y null en caso de error
	 */
	public int consultar_saldo(String tarjeta,int cuenta){
		
		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("consultar_saldo: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta
			if(!this.existeCuenta(tarjeta, cuenta))
				throw new ConsorcioBDException("consultar_saldo: La cuenta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Realizamos la consulta
		ResultSet resultSet;
		try{
			resultSet = this.statement.executeQuery("SELECT cusaldo FROM Cuenta " +
					"WHERE codCuenta=" + cuenta + " AND codTarjeta = '" + tarjeta + "'");
			if(resultSet.next())
				return resultSet.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return -1;
	}

	
	/**
	 * Método que realiza una consulta de movimientos para la cuenta indicada.
	 * @param tarjeta La tarjeta a consultar.
	 * @param cuenta La cuenta a consultar.
	 * @return Un arrayList con los movimientos existentes.
	 * @throws ConsorcioBDException Lanza la excepción cuando no existe la tarjeta o la cuenta y no se puede añadir.
	 */
	public ArrayList<Movimiento> consultar_movimientos(String tarjeta,int cuenta) throws ConsorcioBDException{
		
		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("consultar_movimientos: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta
			if(!this.existeCuenta(tarjeta, cuenta))
				throw new ConsorcioBDException("consultar_movimientos: La cuenta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Realizamos la consulta
		ResultSet resultSet;
		try{
			//Obtenemos todos los movimientos de la cuenta
			resultSet = this.statement.executeQuery("SELECT codMovimiento,moimporte,mofecha,codTMovimiento" +
					" FROM Movimiento " +
					"WHERE ((codCuentaOrig = " + cuenta + " AND codTarjeta = '" + tarjeta +
					"') || (codCuentaDest = " + cuenta + " AND codTarjeta = '" + tarjeta + "'))");
			
			ArrayList<Movimiento> res = new ArrayList<Movimiento>();
			Date fecha = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			//Añadimos movimiento a movimiento al arrayList
			while(resultSet.next()){
				
				//Obtiene el tipo
				CodigosMovimiento tipo = null;
				try {
					int type = resultSet.getInt(4);
					//Comprueba si el valor de la BD !=NULL
					if(type!=0)
						tipo = CodigosMovimiento.getTipoMovimiento(type);
					else
						tipo = CodigosMovimiento.OTRO;
						
				} catch (CodigoNoValidoException e) {
					System.out.println("Codigo de movimiento no valido");
					e.printStackTrace();
				}
				
				//Obtiene la fecha
				try {
					Date data = resultSet.getDate(3);
					//Comprueba si el valor de la BD !=NULL
					if(data!=null){
						String sqlDate = sdf.format(data);
				    	fecha = sdf.parse(sqlDate);
					}else
						fecha = null;
				} catch (ParseException ex) {
				    ex.printStackTrace();
				}
				
				//Añade el nuevo movimiento al arrayList
				res.add(new Movimiento(resultSet.getInt(1),CodigosMovimiento.getSigno(tipo),resultSet.getInt(2),fecha,tipo));
			}
			
			return res;
		}catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		throw new ConsorcioBDException("consultar_movimientos: No existe la cuenta o la tarjeta y no puede ser añadida.");
	}

	
	/**
	 * Método que realiza un reintegro en la BD.
	 * Añade el movimiento a la tabla MOVIMIENTO, actualiza el saldo actual de la CUENTA y 
	 * si es offline
	 * @param tarjeta La tarjeta en la que realizar la operacion.
	 * @param cuenta La cuenta en la que realizar la operacion.
	 * @param importe El importe del que realizar el reintegro.
	 * @param codonline Valor booleano que indica true si es online y false si es offline
	 * @return El saldo actual de la cuenta despues de realizar la operacion.
	 */
	public int realizar_reintegro(String tarjeta,int cuenta,int importe,boolean codonline){

		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("realizar_reintegro: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta
			if(!this.existeCuenta(tarjeta, cuenta))
				throw new ConsorcioBDException("realizar_reintegro: La cuenta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Actualizamos el gasto offline si es Offline
		if(!codonline)
			this.actualiza_GastoOffline(tarjeta, importe);
		
		//Obtenemos el banco a partir de la tarjeta
		String id_banco = "";
		try{
			id_banco = tarjeta.substring(0,tarjeta.length()-3);
		}catch(IndexOutOfBoundsException i){
			i.printStackTrace();
			System.exit(-1);
		}
		
		//Insertamos en la tabla MOVIMIENTO
		this.insertar_movimiento(tarjeta,-1,cuenta,CodigosMovimiento.REINTEGRO,importe,codonline,id_banco);
		
		//Recalculamos el saldo actual de la CUENTA
		this.recalcular_saldoActual(cuenta,tarjeta, importe,'-');
		
		//Devolvemos el saldo actual de la cuenta
		int res = this.consultar_saldo(tarjeta,cuenta);
		
		return res;
	}
	
	
	/**
	 * Método que realiza un traspaso entre dos cuentas en la BD
	 * @param tarjeta La tarjeta correspondiente a la cuenta que realiza el movimiento.
	 * @param cuenta_origen La cuenta que realiza el traspaso.
	 * @param cuenta_destino La cuenta que recibe el traspaso.
	 * @param importe El importe a traspasar.
	 * @return El nuevo saldo de la cuenta destino.
	 */
	public int realizar_traspaso(String tarjeta,int cuenta_origen,int cuenta_destino,boolean codonline,int importe){

		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("realizar_traspaso: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta origen
			if(!this.existeCuenta(tarjeta, cuenta_origen))
				throw new ConsorcioBDException("realizar_traspaso: La cuenta origen indicada no existe.");
			//Comprueba si existe la cuenta destino
			if(!this.existeCuenta(tarjeta, cuenta_destino))
				throw new ConsorcioBDException("realizar_traspaso: La cuenta destino indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Actualizamos el gasto offline si es Offline
		if(!codonline)
			this.actualiza_GastoOffline(tarjeta, importe);
		
		//Obtenemos el banco a partir de la tarjeta
		String id_banco = "";
		try{
			id_banco = tarjeta.substring(0,tarjeta.length()-3);
		}catch(IndexOutOfBoundsException i){
			i.printStackTrace();
			System.exit(-1);
			return -1;
		}
		
		//Insertamos en la tabla MOVIMIENTO
		this.insertar_movimiento(tarjeta,cuenta_origen,cuenta_destino,CodigosMovimiento.TRANSEMITIDO,importe,codonline,id_banco);

		//Recalculamos el saldo actual de las CUENTAS
		this.recalcular_saldoActual(cuenta_origen,tarjeta, importe,'-');
		this.recalcular_saldoActual(cuenta_destino,tarjeta, importe,'+');

		//Devolvemos el saldo actual de la cuenta
		int res = this.consultar_saldo(tarjeta,cuenta_destino);
		
		return res;
	}
	
	

	/**
	 * Método que realiza un abono en la cuenta indicada. 
	 * @param tarjeta La tarjeta que realiza el abono.
	 * @param cuenta La cuenta en la que se abona el importe.
	 * @param importe La cantidad a abonar.
	 * @return El nuevo saldo actual de la cuenta despues de realizar el abono.
	 */
	public int realizar_abono(String tarjeta, int cuenta,boolean codonline,int importe){
		
		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("realizar_abono: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta
			if(!this.existeCuenta(tarjeta, cuenta))
				throw new ConsorcioBDException("realizar_abono: La cuenta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Actualizamos el gasto offline si es Offline
		if(!codonline)
			this.actualiza_GastoOffline(tarjeta, importe);
		
		//Obtenemos el banco a partir de la tarjeta
		String id_banco = "";
		try{
			id_banco = tarjeta.substring(0,tarjeta.length()-3);
		}catch(IndexOutOfBoundsException i){
			i.printStackTrace();
			System.exit(-1);
			return -1;
		}
		
		//Insertamos en la tabla MOVIMIENTO
		this.insertar_movimiento(tarjeta,-1,cuenta,CodigosMovimiento.ABONO,importe,codonline,id_banco);

		//Recalculamos el saldo actual de la CUENTA
		this.recalcular_saldoActual(cuenta,tarjeta, importe,'+');
		
		//Devolvemos el saldo actual de la cuenta
		int res = this.consultar_saldo(tarjeta,cuenta);
		
		return res;
	}
	
	
	/*---------------------------------------------------
	 ---------------- MOVIMIENTOS -----------------------
	 ----------------------------------------------------*/

	/**
	 * Método general que obtiene la suma de todos los importes para el tipo de movimiento introducido.
	 * @param codigo_mov El codigo correspondiente al tipo de movimiento.
	 * @param id_banco El banco del cual obtener la suma de los movimientos.
	 * @return Un int con el sumatorio de los importes de todos los movimientos.
	 */
	private int get_sumaTipoMovimiento(CodigosMovimiento cod_m, String id_banco){
		
		//Obtiene el numero para el codigo de movimiento indicado
		int codigo_mov = cod_m.getNum();
		
		int id_banco_bd = -1;
		//Obtiene codigo que identifica al banco en la BD a partir del string del banco
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		//Calcula la suma de los movimientos para el tipo de movimiento indicado
		ResultSet resultSet;
		try{
			resultSet = this.statement.executeQuery("SELECT SUM(moimporte) FROM Movimiento " +
				"WHERE codTMovimiento = " + codigo_mov + " AND codBanco = "+ id_banco_bd);
		
			if(resultSet.next())
				return resultSet.getInt(1);
			
		}catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return 0;
	}
	
	
	/**
	 * Recorre la tabla de movimientos sumando los reintegros para el banco 
	 * indicado por parámetro
	 * @param id_banco El banco en donde consultar los reintegros.
	 * @return El resultado de la suma de todos los reintegros
	 */
	public int getNumReintegros(String id_banco) {
		return get_sumaTipoMovimiento(CodigosMovimiento.REINTEGRO,id_banco);
	}

	
	/**
	 * Recorre la tabla de movimientos sumando los traspasos para el banco 
	 * indicado por parámetro
	 * @param id_banco El banco en donde consultar los traspasos.
	 * @return El resultado de la suma de todos los traspasos
	 */
	public int getNumTraspasos(String id_banco) {
		return get_sumaTipoMovimiento(CodigosMovimiento.TRANSEMITIDO,id_banco);// o 12?
	}

	
	/**
	 * Recorre la tabla de movimientos sumando los abonos para el banco 
	 * indicado por parámetro
	 * @param id_banco El banco en donde consultar los abonos.
	 * @return El resultado de la suma de todos los abonos
	 */
	public int getNumAbonos(String id_banco) {
		return get_sumaTipoMovimiento(CodigosMovimiento.ABONO,id_banco);
	}
	
	
	/**
	 * Método que realiza una consulta del gasto offline para la tarjeta indicada.
	 * @param tarjeta La tarjeta a consultar
	 * @return Un entero con el gasto oofline.
	 */
	private int consultarGastoOffline(String tarjeta){

		//Comprobamos si existen tarjeta/cuenta
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("consultarGastoOffline: La tarjeta indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		
		//Consulta el gastoOffline de la tarjeta
		ResultSet resultSet;
		try{
			resultSet = this.statement.executeQuery("SELECT tagastoOffline FROM Tarjeta WHERE codTarjeta = '" + tarjeta + "'");
			
			if(resultSet.next())
				return resultSet.getInt(1);
			
		}catch (SQLException e) {
				e.printStackTrace();
				System.exit(-1);
		}
		return 0;
	}
	
	
	/**
	 * Método que inserta un movimiento en la tabla MOVIMIENTO.
	 * Admite nulos en cualquier campo excepto en el codigo online.
	 * @param cuenta_orig La cuenta origen.
	 * @param cuenta_dest La cuenta destino. 
	 * @param cod_tmovimiento El tipo del movimiento.
	 * @param importe El importe del movimiento.
	 * @param codonline El booleando que indica si es online o offline.
	 * @param banco El número del banco.
	 */
	private void insertar_movimiento(String tarjeta,int cuenta_orig,int cuenta_dest,CodigosMovimiento codigo_m,
		int importe,boolean codonline,String id_banco){
		
		//Comprobamos si existen tarjeta/cuentas
		try{
			//Comprueba si existe la tarjeta
			if(!this.existeTarjeta(tarjeta))
				throw new ConsorcioBDException("insertar_movimiento: La tarjeta indicada no existe.");
			//Comprueba si existe la cuenta origen y sino la añade
			if(cuenta_orig>0)
				if(!this.existeCuenta(tarjeta, cuenta_orig))
					throw new ConsorcioBDException("insertar_movimiento: La cuenta origen indicada no existe.");
			//Comprueba si existe la cuenta destino y sino la añade
			if(cuenta_dest>0)
				if(!this.existeCuenta(tarjeta, cuenta_dest))
					throw new ConsorcioBDException("insertar_movimiento: La cuenta destino indicada no existe.");
		}catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Obtiene codigo que identifica al banco en la BD a partir del string del banco
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		//Obtenemos el numero del codigo de movimiento
		int cod_tmovimiento = codigo_m.getNum();
		
		//Obtiene el tiempo actual y lo añade con el formato indicado
	  	Calendar time = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//Obtiene los strings con la fecha y las cuentas
		String cuentas = ((cuenta_orig<0)? "NULL":cuenta_orig) + "," + ((cuenta_dest<0)? "NULL":cuenta_dest);
		String fecha = sdf.format(time.getTime());
		
		System.out.println("INSERT INTO Movimiento" +
				"(codTarjeta,codCuentaOrig,codCuentaDest,codTMovimiento,mofecha,moimporte,moonline,codBanco)" +
				" VALUES ('" + tarjeta + "'," + cuentas + "," + cod_tmovimiento + "," + fecha + "," +
				importe + "," + (codonline) + "," + id_banco_bd + ")");

		//Inserta el movimiento en la BD
		try {
			this.statement.executeUpdate("INSERT INTO Movimiento" +
				"(codTarjeta,codCuentaOrig,codCuentaDest,codTMovimiento,mofecha,moimporte,moonline,codBanco)" +
				" VALUES ('" + tarjeta + "'," + cuentas + "," + cod_tmovimiento + ", STR_TO_DATE('" + fecha + "','%d/%m/%Y')," +
				importe + "," + (codonline) + "," + id_banco_bd + ")");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/*---------------------------------------------------
	 ----------------- SESIONES/BANCOS -------------------
	 ----------------------------------------------------*/
	
	/**
	 * Método que abre una sesion con el banco indicado.
	 * Comprueba si ya esta, en ese caso setea el estado a ACTIVA, sino añade una linea a la tabla
	 * de BANCO.
	 * @param id_banco El identificador a añadir.
	 * @param ip La ip en la que se encuentra el servidor del banco. Ej:'127.0.0.1'
	 * @param puerto El puerto en el que se encuentra el servidor del banco.
	 * @param num_canales El número de canales máximo que el banco puede usar.
	 */
	public void abrir_sesion(String id_banco, String ip, int puerto, int num_canales){
		
		ResultSet resultSet;
		try{
			//Obtiene el codigo que identifica al banco en la BD
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco " +
					"WHERE codBanco = '" + id_banco + "'");
			
			if(resultSet.next()){
				//Si ya está el banco, settear la sesion a abierta y los datos
				try{
					this.setEstado_conexion_banco(id_banco,SesAberta.instance());
				}catch(ConsorcioBDException c){
					c.printStackTrace();
					System.exit(-1);
				}
				this.setPuertoBanco(id_banco, puerto);
				this.setIpBanco(id_banco, ip);
				this.setNumCanalesBanco(id_banco, num_canales);
			}else{
				//Añadir BANCO a la BD
				insertar_banco(id_banco,SesAberta.instance(),puerto,ip,num_canales);
			}
			
			//Borrar todos los canales del banco
			borrar_canales(id_banco);

			int id_canal = 0;
			//Añadir todos los nuevos CANALES del BANCO
			for(id_canal=0;id_canal<num_canales;id_canal++)
				anhadir_canal(id_banco,id_canal);
			
		}catch (SQLException e) {
				e.printStackTrace();
				System.exit(-1);
		}
	}
	
	
	/**
	 * Método que cierra una sesión con el banco indicado.
	 * Comprueba si está la sesión, si está setea el estado a CERRADA, sino no hace nada.
	 * @param id_banco El identificador del banco a cerrar.
	 */
	public void cerrar_sesion(String id_banco){

		ResultSet resultSet;
		try{
			//Obtiene el codigo que identifica al banco en la BD
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco " +
					"WHERE codbanco = '" + id_banco +"'");
			
			if(resultSet.next()){
				try{
					//Si hay un banco, settear la sesion a cerrada y borrar canales
					this.setEstado_conexion_banco(id_banco,SesNonAberta.instance());
				}catch(ConsorcioBDException c){
					c.printStackTrace();
					System.exit(-1);
				}
				borrar_canales(id_banco);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Método que inserta un BANCO
	 * @param id_banco El id del banco
	 * @param estado El estado en el que se encuentra.
	 * @param puerto El puerto en el que escucha el banco.
	 * @param ip La ip en la que se encuentra el banco.
	 * @param num_canales El numero maximo de canales.
	 */
	private void insertar_banco(String id_banco, EstadoSesion state ,int puerto,String ip,int num_canales){
		
		//Obtiene el int que identifica al estado introducido
		int estado = EstadoSesion.getInt_fromEstadoSesion(state);
		
		System.out.println("INSERTAR BANCO: "+id_banco+"-"+estado+"-"+puerto+"-"+ip+"-"+num_canales);
		try {
			this.statement.executeUpdate("INSERT INTO Banco(codBanco,codEBanco,bapuerto,baip,bamaxCanales)" +
			" VALUES('" + id_banco + "'," + estado + "," + puerto + ",'" + ip + "'," + num_canales+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Comprueba en BANCO si el banco introducido por parámetro tiene sesión.
	 * @param id_banco El banco a buscar.
	 * @return True si la sesion es ACTIVA y False en caso contrario.
	 */
	public boolean hasSesion(String id_banco){
		
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco" +
					" WHERE codBanco = '" + id_banco + "' AND codEBanco = 1");
			
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	
	/**
	 * Comprueba si la SESION admite envios o no.
	 * @param id_banco El banco a buscar.
	 * @return True si los admite y False en caso contrario (si el estado es trafico detenido o cerrada)
	 */
	public boolean consultar_protocolo(String id_banco){

		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco" +
					" WHERE codBanco = '" + id_banco + "' AND ((codEBanco = 1 ) || (codEBanco = 4))");
			
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	
	//--GETTERS BANCOS--

	/**
	 * Método que obtiene el ID real que identifica al banco pasado por 
	 * parámetro en la BD.
	 * @param id_banco El id del banco que funciona como el nombre
	 * @return El int que indentifica al banco con el string pasado por parámetro, dentro de la BD.
	 * @throws ConsorcioBDException Si no existe el banco se lanza la excepción.
	 */
	private int getIdBancoBD(String id_banco) throws ConsorcioBDException{
		
		//Comprueba si existe, si existe devuelve el codigo de la BD que lo identifica
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codigo FROM Banco WHERE codBanco ='" + id_banco + "'");
		
			if(resultSet.next())
				return resultSet.getInt(1);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		System.out.println("BANCO:"+id_banco+"-");
		throw new ConsorcioBDException("No existe el banco con el que se trata de realizar la operacion.");
	}
	
	
	/**
	 * Getter en BANCO del estado de la conexion con el banco.
	 * @param id_banco El banco a buscar.
	 * @return El estado de la conexion correspondiente.
	 */
	public EstadoSesion getEstado_conexion_banco(String id_banco){
		
		//Comprobamos si esta el banco
		try {
			this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Obtenemos el estado del banco
		ResultSet resultSet;
		int codEBanco = 0;
		try {
			resultSet = this.statement.executeQuery("SELECT codEBanco FROM Banco" +
					" WHERE codBanco = '" + id_banco + "'");
			
			if(resultSet.next())
				codEBanco = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		//Devolvemos la sesion, si no hay banco devuelve sesion cerrada
		return EstadoSesion.getEstadoSesion_fromInt(codEBanco);
	}

	
	/**
	 * Getter en BANCO del numero de canales
	 * @param id_banco El banco a buscar.
	 * @return El número máximo de canales del banco.
	 */
	public int getNum_canales(String id_banco){
		
		//Comprobamos si esta el banco
		try {
			this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Consultamos los canales del banco indicado
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT bamaxCanales FROM Banco" +
					" WHERE codBanco = '" + id_banco + "'");
			
			if(resultSet.next())
				return resultSet.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return 0;
	}
	
	
	/**
	 * Getter en BANCO del puerto del banco
	 * Devuelve 0 en caso de que no exista
	 * @param id_banco El banco a buscar.
	 * @return El puerto correspondiente. 
	 */
	public int getPortBanco(String id_banco){

		//Comprobamos si esta el banco
		try {
			this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Consultamos el puerto del banco
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT bapuerto FROM Banco" +
					" WHERE codBanco = '" + id_banco +"'");
			
			if(resultSet.next())
				return resultSet.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return 0;
	}
	
	
	/**
	 * Getter en BANCO de la ip del banco.
	 * Devuelve NULL en caso de que no exista
	 * @param id_banco El banco a buscar.
	 * @return La ip correspondiente. 
	 * @throws UnknownHostException 
	 */
	public InetAddress getIpBanco(String id_banco){
		
		//Comprobamos si esta el banco
		try {
			this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Consultamos la ip del banco
		ResultSet resultSet;
		String temp = "";
		try {
			resultSet = this.statement.executeQuery("SELECT baip FROM Banco" +
					" WHERE codBanco = '" + id_banco + "'");
			
			if(resultSet.next())
				temp = resultSet.getString(1);
				
			return InetAddress.getByName(temp);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (UnknownHostException ex){
			return null;
		}
		return null;
	}
	
	
	/**
	 * Getter del ultimo canal utilizado por el BANCO
	 * @param id_banco El banco del que obtener el ultimo canal utilizado
	 * @return El int que identifica al ultimo canal utilizado.
	 */
	public int getLastChannelUsed(String id_banco){
		
		//Comprobamos si esta el banco
		try {
			this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		int canal = 0;
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT balastChannelUsed FROM Banco" +
					" WHERE codBanco = '" + id_banco + "'");
			
			if(resultSet.next()){
				canal = resultSet.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return canal;
	}
	
	
	/**
	 * Método que obtiene una lista con los identificadores de todos los BANCO que tienen sesión 
	 * con el codigo de sesion que se pasa por parámetro.
	 * @param codigo_estado El codigo de sesión por el que filtrar los bancos
	 * @return Un array list de String con los id de todos los BANCOS que tienen la sesión del tipo indicado.
	 */
	public ArrayList<String> getSesiones(int codigo_estado) {
		
		//Realiza la búsqueda
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codBanco FROM Banco " +
					"WHERE codEBanco = "+codigo_estado);
			
			ArrayList<String> res = new ArrayList<String>();
			
			while(resultSet.next()){
				res.add(resultSet.getString(1));
			}
			
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	
	
	//--SETTERS BANCO--
	
	
	/**
	 * Setter en BANCO del estado de la conexion
	 * @param id_banco El banco al que cambiar el estado.
	 * @param estado El nuevo estado.
	 * @throws ConsorcioBDException Se produce cuando se trata de settear a un estado que no esta insertado en la BD.
	 */
	public void setEstado_conexion_banco(String id_banco,EstadoSesion estado) throws ConsorcioBDException{
		
		//Obtiene el valor a introducir en la BD del estado correspondiente.
		int state = EstadoSesion.getInt_fromEstadoSesion(estado);
		
		//Consultamos si el estado está introducido en la BD
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT codEBanco FROM EstadoBanco" +
					" WHERE codEBanco = " + state);
			
			if(!resultSet.next()){
				throw new ConsorcioBDException("setEstado_conexion_banco: La sesión a settear no se encuentra disponible en la BD.");
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			this.statement.executeUpdate("UPDATE Banco SET codEbanco = " + state + 
					" WHERE codBanco = '" + id_banco + "'");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Setter del puerto en BANCO
	 * @param id_banco El banco al que cambiar el puerto.
	 * @param puerto El nuevo puerto.
	 */
	private void setPuertoBanco(String id_banco, int puerto){
		
		try {
			this.statement.executeUpdate("UPDATE Banco SET bapuerto = " + puerto + 
					" WHERE codBanco = '" + id_banco + "'");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Setter de la ip en el BANCO
	 * @param id_banco El banco al que cambiar el puerto.
	 * @param ip La nueva ip
	 */
	private void setIpBanco(String id_banco,String ip){
		
		try {
			this.statement.executeUpdate("UPDATE Banco SET baip = '" + ip + 
					"' WHERE codBanco = '" + id_banco + "'");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Setter del maximo de canales en el BANCO
	 * @param id_banco El banco al que cambiar el puerto.
	 * @param num_canales El nuevo número máximo de canales
	 */
	private void setNumCanalesBanco(String id_banco, int num_canales){
		
		try {
			this.statement.executeUpdate("UPDATE Banco SET bamaxCanales = " + num_canales + 
					" WHERE codBanco = '" + id_banco + "'");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Setter del ultimo canal usado del BANCO.
	 * @param id_banco El id del banco 
	 * @param last_channel El valor del ultimo canal utilizado que se va a actualizar en la BD
	 */
	private void setLastChannelUsed(String id_banco, int last_channel){
		try {
			this.statement.executeUpdate("UPDATE Banco SET balastChannelUsed = " + last_channel + 
					" WHERE codBanco = '" + id_banco + "'");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/*---------------------------------------------------
	 -------------------- CANALES -----------------------
	 ----------------------------------------------------*/


	/**
	 * Obtiene en CANAL el siguiente canal disponible para realizar un envio.
	 * El algorítmo de selección consiste en (ULTIMO_CANAL_USADO+1) MOD TOTALCANALES 
	 * ERROR: Devuelve -1 en caso de que no haya canales que puedan ser usados
	 * @param id_banco El banco de donde obtener un nuevo canal.
	 * @return Un entero con el canal correspondiente.
	 */
	public int seleccionarCanal(String id_banco){

		int id_banco_bd = 0;
		//Comprueba si está el banco
		try {
			id_banco_bd = this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		//Obtiene el ultimo canal usado en el banco
		int last_channel = this.getLastChannelUsed(id_banco);
		int max_channel = this.getNum_canales(id_banco);
		int selected_channel = last_channel+1%max_channel;
		
		ResultSet resultSet;
		try {
			//Consultamos los canales libres en la BD
			resultSet = this.statement.executeQuery("SELECT DISTINCT c.codCanal FROM Canal c JOIN UltimoEnvio ue " +
					"ON c.codBanco = " + id_banco_bd + " WHERE c.cabloqueado = 0 AND ue.uecontestado = 1");
			
			//Obtenemos todos los canales que estan libres para ser usados
			ArrayList<Integer> canales_posibles = new ArrayList<Integer>();
			while(resultSet.next()){
				canales_posibles.add(resultSet.getInt(1));
			}
			
			//Si no hay canales que puedan ser usados
			if(canales_posibles.isEmpty())
				return -1;
				
			//Buscar el canal seleccionado en el array, si no está avanzar el numero y comprobar de nuevo
			while(!canales_posibles.contains(selected_channel)){
				selected_channel=(selected_channel+1)%max_channel;
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Coloca el canal seleccionado como ultimo canal usado en la tabla del Banco
		this.setLastChannelUsed(id_banco, selected_channel);
		
		return selected_channel;
	}
	
	
	/**
	 * Obtiene en CANAL el siguiente numero de mensaje para el banco y canal indicado 
	 * y le suma 1.
	 * ERROR: Devuelve -1 en caso de que no haya un next_numMensaje para el banco y canal indicados
	 * @param id_banco El banco en el que se encuentra el canal.
	 * @param id_canal El canal de donde seleccionar el numero de mensaje.
	 * @return Un entero con el número de mensaje a utilizar.
	 */
	public int selecciona_num_mensaje(String id_banco,int id_canal){

		int id_banco_bd = 0;
		//Comprueba si está el banco y obtiene su id
		try {
			id_banco_bd = this.getIdBancoBD(id_banco);
		} catch (ConsorcioBDException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		int num_mensaje = 0;
		ResultSet resultSet;
		try {
			//Obtiene el siguiente Numero de Mensaje del canal indicado
			resultSet = this.statement.executeQuery("SELECT canext_numMensaje FROM Canal " +
					"WHERE codBanco = " + id_banco_bd + " AND codCanal = " + id_canal);
			
			//Si no hay un elemento se devuelve -1
			if(resultSet.next())
				num_mensaje = resultSet.getInt(1);
			else
				return -1;
			
			//Se actualiza el numero de mensaje sumandole 1
			this.statement.executeUpdate("UPDATE Canal SET canext_numMensaje = canext_numMensaje+1" +
					" WHERE codBanco = " + id_banco_bd + " AND codCanal = " + id_canal);

		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return num_mensaje;
	}
	
	
	/**
	 * Comprueba si el canal está ocupado o el envio está sin contestar y devuelve un booleano.
	 * @param id_banco El banco a comprobar.
	 * @param canal El canal concreto a comprobar.
	 * @return True si el canal esta ocupado o False en caso contrario.
	 */
	public boolean isCanal_ocupado(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT c.cabloqueado || (ue.uecontestado=0)" +
					" FROM Canal c JOIN UltimoEnvio ue ON c.codUltimoEnvio = ue.ueNumUltimoEnvio" +
					" WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal);

			if(resultSet.next())
				return (resultSet.getInt(1) == 1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}

	
	/**
	 * Devuelve True si hay mensajes sin responder en el banco indicado.
	 * Recorre ULTIMOENVIO con el banco indicado comprobando si está contestado.
	 * @param id_banco El banco en el que buscar.
	 * @return True si hay algun mensaje sin responder y False en caso contrario.
	 */
	public boolean hayMensajesSinResponder(String id_banco){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT COUNT(ueNumUltimoEnvio) FROM UltimoEnvio " +
					"WHERE codBanco = " + id_banco_bd + " AND uecontestado = 0");

			if(resultSet.next())
				return (resultSet.getInt(1) != 0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}

	
	/**
	 * Devuelve los ultimos mensajes enviados hacia el banco introducido
	 * por parámetro, por todos los canales.
	 * En caso de que no haya mensaje devuelve una lista vacia.
	 * @param id_banco El banco en el que buscar.
	 * @return Un ArrayList con todos los ultimos mensajes de cada canal.
	 */
	public ArrayList<Mensaje> recupera_ultimos_mensajes(String id_banco){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		ArrayList<Mensaje> res = new ArrayList<Mensaje>();
		try {
			resultSet = this.statement.executeQuery("SELECT uestringMensaje FROM UltimoEnvio " +
					"WHERE codBanco = " + id_banco_bd);
			
			while(resultSet.next()){
				Mensaje m = Mensaje.parse(resultSet.getString(1));
				res.add(m);
			}
			
			return res;
		} catch (SQLException | MensajeNoValidoException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	
	/**
	 * Settea a True la variable bloqueado de CANAL para el banco y canal correspondiente. 
	 * @param id_banco El banco en el que se encuentra.
	 * @param canal El canal concreto a bloquear.
	 */
	public void bloquearCanal(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		try {
			this.statement.executeUpdate("UPDATE Canal SET cabloqueado = 1 " +
					"WHERE codBanco = " + id_banco_bd + " AND codCanal = " + canal);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Comprueba si el tipo de mensaje pasado por parámetro se corresponde con la respuesta adecuada a la 
	 * solicitud que hay en el banco y canal indicados por parámetros.
	 * @param tipo_contestacion El tipo de la respuesta que hemos recibido.
	 * @param id_banco El banco en el que se encuentra el canal con la solicitud a comprobar.
	 * @param canal El canal en el que se encuentra la solicitud a comprobar.
	 * @return True si la contestacion se corresponde con la pregunta y False en caso contrario.
	 */
	public boolean esCorrectaContestacion(CodigosMensajes tipo_contestacion, String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		String m = null;
		boolean contestado = true;
		Mensaje message = null;
		ResultSet resultSet;
		try {
			//Consultamos el si ha sido contestado y el mensaje
			resultSet = this.statement.executeQuery("SELECT uecontestado,uestringMensaje FROM Canal c JOIN UltimoEnvio u " +
					"ON c.codUltimoEnvio=u.codigoue WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = "+ canal);
			
			if(resultSet.next()){

				//Obtenemos si ha sido contestado y el mensaje anterior
				contestado = (resultSet.getInt(1)==1);
				m = resultSet.getString(2);
				
				//Parseamos el mensaje
				try {
					message = Mensaje.parse(m);
				} catch (MensajeNoValidoException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				
				//Si ya ha sido contestada, no es correcto
				if(contestado)
					return false;
				
				//Comprobamos si es correcta
				return message.esContestacionCorrecta(tipo_contestacion);
			}
			else
				throw new ConsorcioBDException("No existe el canal para el banco indicados o el canal no ha realizado ningun envio anteriormente.");
			
		} catch (SQLException | ConsorcioBDException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	
	/**
	 * Desbloquea todos los canales del banco
	 * @param id_banco El banco para el cual desbloquear todos los canales.
	 */
	public void desbloquearCanales(String id_banco){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		try {
			this.statement.executeUpdate("UPDATE Canal SET cabloqueado = 0 " +
					"WHERE codBanco = " + id_banco_bd);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Método que comprueba si el ultimo mensaje del canal ha sido respondido.
	 * @param id_banco El banco en el que comprobar.
	 * @param canal EL canal concreto del banco en el que se encuentra el envio.
	 * @return El valor booleando del atributo "contestado" del último envio del canal.
	 */
	public boolean isContestado(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		try {
			resultSet = this.statement.executeQuery("SELECT uecontestado " +
					"FROM Canal c JOIN UltimoEnvio ue ON c.codUltimoEnvio = ue.ueNumUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd +" AND c.codCanal = " + canal);

			if(resultSet.next())
				return (resultSet.getInt(1) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return true;
	}
	
	
	/**
	 * Método que añade un CANAL a la BD.
	 * @param id_banco El banco en el que añadir.
	 * @param canal El numero de canal.
	 */
	private void anhadir_canal(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		try {
			this.statement.executeUpdate("INSERT INTO Canal(codBanco,codCanal) VALUES (" + id_banco_bd + "," + canal + ")");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Metodo que elimina un CANAL de la BD.
	 * @param id_banco El id del banco en el que se encuentra.
	 * @param canal El numero de canal a borrar.
	 */
	private void eliminar_canal(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		try {
			this.statement.executeUpdate("DELETE FROM Canal WHERE codBanco = " + id_banco_bd + " AND codCanal = " + canal);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Obtiene todos los canales de un banco concreto y los elimina.
	 * @param id_banco El banco a eliminar sus canales.
	 */
	private void borrar_canales(String id_banco){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ArrayList<Integer> canales = new ArrayList<Integer>();
		ResultSet resultSet = null;
		try {
			//Realiza la consulta
			resultSet = this.statement.executeQuery("SELECT codCanal " +
					"FROM Canal " +
					"WHERE codBanco = " + id_banco_bd);

			//Obtiene todos los canales a borrar
			while(resultSet.next()){
				canales.add(resultSet.getInt(1));
			}
			
			//Borra todos los canales
			Iterator<Integer> it = canales.iterator();
			while(it.hasNext()){
				eliminar_canal(id_banco,it.next());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	
	/*---------------------------------------------------
	 --------------------- ULTIMOENVIO -----------------------
	 ----------------------------------------------------*/
	
	/**
	 * Cambia el ultimo envio del canal indicado, por el pasado por parametro.
	 * Si el canal esta ocupado no se inserta como ultimo envio.
	 * @param message El mensaje a añadir.
	 * @param ip La ip de la que proviene el envio (Cajero)
	 * @param port El puerto del que proviene el envio (Cajero)
	 * @param canal El canal correspondiente.
	 */
	public void anhadir_ultimo_envio(Mensaje message,String codCajero,String ip_cajero, int puerto_cajero,int canal){

		String id_banco = message.getDestino();
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		int codigo_ultimo_envio = 0;
		
		//if canal==0->mensaje de control sin canal
		//Comprueba si el canal esta ocupado o el mensaje del canal no ha sido respondido
		if (this.isCanal_ocupado(id_banco, canal)){
			System.out.println("El canal se encuentra ocupado, no se ha realizado la inserción.");
			return;
		}
		
		//Selecciona el codigo del ultimo envio para el banco y canal indicados
		try {
			resultSet = this.statement.executeQuery("SELECT codUltimoEnvio" +
					" FROM Canal" +
					" WHERE codCanal=" + canal + " AND codBanco=" + id_banco_bd + " AND codUltimoEnvio IS NOT NULL");
			
			if(resultSet.next()){
				codigo_ultimo_envio = resultSet.getInt(1);
				
				//Borra el envio para el banco y canal indicado
				this.eliminar_ultimo_envio(codigo_ultimo_envio);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Inserta el nuevo envio para el banco y canal indicado
		int nuevo_ultimo_envio = this.insertar_ultimo_envio(message,codCajero, ip_cajero, puerto_cajero);
		
		//Setea el codigo del nuevo envio en la tabla de Canales para el canal concreto
		this.settearCodigoUltimoEnvioEnCanal(id_banco_bd, canal, nuevo_ultimo_envio);
		
	}
	
	
	private void settearCodigoUltimoEnvioEnCanal(int id_banco_bd, int canal, int cod_ultimo_envio){
		try {
			this.statement.executeUpdate("UPDATE Canal SET codUltimoEnvio="+cod_ultimo_envio+
					" WHERE codBanco="+id_banco_bd+" AND codCanal="+canal);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Devuelve el ULTIMOENVIO de un canal.
	 * ERROR: Devuelve NULL en caso de que no haya ningun ultimo envio para el banco/canal indicados
	 * @param id_banco El banco que identifica al envio.
	 * @param canal EL canal que indentifica al envio.
	 * @return El mensaje correspondiente al ULTIMOENVIO realizado por el canal indicado.
	 */
	private Mensaje obtener_ultimo_envio(String id_banco, int canal){
	
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		String str_mensaje;
		ResultSet resultSet;
		Mensaje res = null;
		try {
			//Obtiene el mensaje del Ultimo envio
			resultSet = this.statement.executeQuery("SELECT ue.uestringMensaje " +
					"FROM UltimoEnvio ue JOIN Canal c ON ue.ueNumUltimoEnvio = c.codUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal);
			
			//Si hay mensaje se parsea y se asigna al resultado
			if(resultSet.next()){
				str_mensaje = resultSet.getString(1);
				res = Mensaje.parse(str_mensaje);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (MensajeNoValidoException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}

	
	/**
	 * Método privado que inserta el ultimo envio en la tabla
	 * @param mensaje El mensaje a insertar
	 * @param codCajero El cajero del que proviene
	 * @param ip_cajero La ip del cajero del que proviene
	 * @param puerto_cajero El puerto del cajero del que proviene
	 * @return El codigo de ultimo envio
	 */
	private int insertar_ultimo_envio(Mensaje mensaje,String codCajero,String ip_cajero, int puerto_cajero){
		
		String tarjeta = null;
		int cuenta = 0;
		int num_mensaje = 0;
		String id_banco = mensaje.getDestino();
		
		//Comprueba que los datos son correctos
		if(mensaje.es_datos()){
			try{
				if((tarjeta = ((MensajeDatos)mensaje).getNum_tarjeta()) == null)
					throw new CodigoNoValidoException();
				if((cuenta = ((MensajeDatos)mensaje).getNum_cuenta()) == -1)
					throw new CodigoNoValidoException();
				num_mensaje = ((MensajeDatos)mensaje).getNmsg();
			}catch (CodigoNoValidoException a){
					a.printStackTrace();
					System.exit(-1);
			}
		}
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("ULTIMO ENVIO:"+num_mensaje+"-CAJERO:"+codCajero+"-PUERTO:"+puerto_cajero+"-IP:"+ip_cajero+"-BANCO:"
		+id_banco_bd+"-TARJETA:"+tarjeta+"-CUENTA:"+cuenta);
		
		//Ejecuta la insercion en la BD
		try {
			this.statement.executeUpdate("INSERT INTO UltimoEnvio(ueNumUltimoEnvio,uecodCajero,uepuerto,ueip," +
					"codBanco,codTarjeta,codCuenta,uestringMensaje)" +
					" VALUES (" + num_mensaje + "," + ((codCajero==null)?"NULL":"'"+codCajero+"'") + "," + puerto_cajero + ",'" + ip_cajero + 
					"'," + id_banco_bd + "," + ((tarjeta==null)?"NULL":"'"+tarjeta+"'") + "," + cuenta + ",'" + mensaje.toString() +"')");

		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int cod_ultimo_envio = 0;
		
		ResultSet resultSet;
		try{
			resultSet = this.statement.executeQuery("SELECT MAX(codigoue) FROM UltimoEnvio");
			
			if(resultSet.next())
				cod_ultimo_envio = resultSet.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}		
		return cod_ultimo_envio;
	}
	
	
	/**
	 * Método que elimina el ultimo envio con el codigo pasado por parámetro.
	 * Si no hay ninguno con el codigo indicado no se hace nada.
	 * @param codigo_ultimo_envio EL codigo de ultimo envio a eliminar
	 */
	private void eliminar_ultimo_envio(int codigo_ultimo_envio){
		try {
			this.statement.executeUpdate("DELETE FROM UltimoEnvio WHERE ueNumUltimoEnvio=" + codigo_ultimo_envio);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	
	//---GETTERS ULTIMO ENVIO------
	
	
	/**
	 * Getter de la IP del ULTIMOENVIO que indica en donde se encuentra el cajero a contestar.
	 * @param id_banco El banco correspondiente.
	 * @param num_canal El canal del que obtener.
	 * @return La ip correspondiente.
	 */
	public InetAddress getIpEnvio(String id_banco, int canal){
	
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		String temp = "";
		try {
			resultSet = this.statement.executeQuery("SELECT ue.ueip FROM UltimoEnvio ue JOIN Canal c " +
					"ON ue.ueNumUltimoEnvio = c.codUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal );
			
			if(resultSet.next())
				temp = resultSet.getString(1);
				
			return InetAddress.getByName(temp);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (UnknownHostException e) {
			return null;
		}
		return null;
	}
		
		
	/**
	 * Getter del puerto del ULTIMOENVIO que indica en donde se encuentra el cajero a contestar.
	 * ERROR: Devuelve 0 cuando no hay puerto o ha habido un error
	 * @param id_banco El banco correspondiente.
	 * @param num_canal El canal del que obtener.
	 * @return La puerto correspondiente.
	 */
	public int getPortEnvio(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		int res = 0;
		try {
			resultSet = this.statement.executeQuery("SELECT ue.uepuerto FROM UltimoEnvio ue JOIN Canal c " +
					"ON ue.ueNumUltimoEnvio = c.codUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal);
			
			if(resultSet.next())
				res = resultSet.getInt(1);
				
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}
	
	
	/**
	 * Getter del id del Cajero del ULTIMOENVIO.
	 * ERROR: Devuelve NULL cuando no existe cajero.
	 * @param id_banco El banco en el que obtener el id.
	 * @param num_canal El canal en el que se encuentra el Envio con la id del cajero que lo realizó.
	 * @return Un string que identifica al cajero que realizó el envio.
	 */
	public String getIdCajero(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		int res = 0;
		try {
			resultSet = this.statement.executeQuery("SELECT ue.uecodCajero FROM UltimoEnvio ue JOIN Canal c " +
					"ON ue.ueNumUltimoEnvio = c.codUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal);
			
			if(resultSet.next())
				res = resultSet.getInt(1);
				
			return Integer.toString(res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	
	//---SETTERS ULTIMO ENVIO ----
	
	/**
	 * Método que pone el atributo "contestado" del ULTIMOENVIO a True.
	 * En caso de que no haya ultimo movimiento para el canal indicado, no hace nada.
	 * @param id_banco El banco que identifica al envio.
	 * @param num_canal El canal que identifica al envio.
	 */
	public void setEnvioContestado(String id_banco, int canal){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet;
		try {
			int codigo=0;
			//Obtiene el codigo que identifica al ultimo envio para el banco/canal indicados
			resultSet = this.statement.executeQuery("SELECT ue.codigoue " +
					"FROM UltimoEnvio ue JOIN Canal c ON ue.codigoue = c.codUltimoEnvio " +
					"WHERE c.codBanco = " + id_banco_bd + " AND c.codCanal = " + canal);
		
			//Si existe ultimo envio se actualiza a contestado = TRUE
			if(resultSet.next()){
				codigo = resultSet.getInt(1);
				this.statement.executeUpdate("UPDATE UltimoEnvio SET uecontestado = 1 WHERE codigoue=" + codigo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	
	/*---------------------------------------------------
	 --------------------- MENSAJE ---------------------
	 ----------------------------------------------------*/
	
	
	/**
	 * Método que devuelve un ArrayList con los MENSAJE que se han realizado offline para el banco
	 * indicado por parámetro.
	 * También pone el flag offline a 1 una vez acaba de todos los mensajes del banco 
	 * ya que ahora ya han sido enviados en online.
	 * @param id_banco El banco del que obtener los mensajes offline.
	 * @return Un arraylist con los Mensajes que han sido offline.
	 */
	public ArrayList<Mensaje> getMensajesOffline(String id_banco){
		
		//Obtiene el id real que identifica al banco en la BD.
		int id_banco_bd = 0;
		try{
			id_banco_bd = this.getIdBancoBD(id_banco);
		}catch(ConsorcioBDException c){
			c.printStackTrace();
			System.exit(-1);
		}
		
		ResultSet resultSet = null;
		ArrayList<Mensaje> res = new ArrayList<Mensaje>();
		try {
			//Obtenemos todos los mensajes OFFLINE
			resultSet = this.statement.executeQuery("SELECT mestringMensaje" +
					" FROM Mensaje " +
					" WHERE codBanco = " + id_banco_bd + " AND meonline=0 AND codTDestino=1");
			
			//Parseamos los mensajes
			while(resultSet.next()){
				Mensaje m = Mensaje.parse(resultSet.getString(1));
				res.add(m);
			}
			
			//Ponemos OFFLINE a false para todos los mensajes del id_banco
			this.statement.executeUpdate("UPDATE Mensaje SET meonline =1 WHERE codBanco = " + id_banco_bd);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (MensajeNoValidoException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}
	
	
	/**
	 * Método que obtiene todos los mensajes hacia/desde el CAJERO.
	 * Devuelve un arraylist con los Strings a mostrar en la interfaz grafica.
	 * @return Un arraylist de strings.
	 */
	public ArrayList<String> getMensajesCajeroToString(){
		
		ResultSet resultSet = null;
		ArrayList<String> res = new ArrayList<String>();
		try {
			resultSet = this.statement.executeQuery("SELECT ta.todnombre as ORIGEN,tb.todnombre as DESTINO,m.mestringMensaje" +
					" FROM Mensaje m JOIN TipoOrigDest ta ON ta.codTOrigDest = m.codTOrigen" +
					" JOIN TipoOrigDest tb ON tb.codTOrigDest = m.codTDestino" +
					" WHERE ta.todnombre='Cajero' || tb.todnombre='Cajero' ORDER BY codMensaje");

			while(resultSet.next()){
				
				//str1/str2 = nombre del tipo de origen (CAJERO,BANCO o CONSORCIO)
				String str1 = resultSet.getString(1);
				String str2 = resultSet.getString(2);
				Mensaje m = null;
				
				//Parseamos el mensaje
				try {
					m = Mensaje.parse(resultSet.getString(3));
				} catch (MensajeNoValidoException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				
				//Creamos el string a imprimir
				String elemento = str1+"("+m.getOrigen()+")"+ "->" + str2+"("+m.getDestino()+")" + ": " + m.getTipoMensaje();
				res.add(elemento);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}
	
	
	/**
	 * Método que obtiene todos los mensajes hacia/desde el BANCO.
	 * Devuelve un arraylist con los Strings a mostrar en la interfaz grafica.
	 * @return Un arraylist de strings.
	 */
	public ArrayList<String> getMensajesBancoToString(){
		
		ResultSet resultSet = null;
		ArrayList<String> res = new ArrayList<String>();
		try {
			resultSet = this.statement.executeQuery("SELECT ta.todnombre as ORIGEN,tb.todnombre as DESTINO,m.mestringMensaje" +
					" FROM Mensaje m JOIN TipoOrigDest ta ON ta.codTOrigDest = m.codTOrigen" +
					" JOIN TipoOrigDest tb ON tb.codTOrigDest = m.codTDestino" +
					" WHERE ta.todnombre='Banco' || tb.todnombre='Banco' ORDER BY codMensaje");

			while(resultSet.next()){
				
				//str1/str2 = nombre del tipo de origen (CAJERO,BANCO o CONSORCIO)
				String str1 = resultSet.getString(1);
				String str2 = resultSet.getString(2);
				Mensaje m = null;
				
				//Parseamos el mensaje
				try {
					m = Mensaje.parse(resultSet.getString(3));
				} catch (MensajeNoValidoException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				
				//Creamos el string a imprimir
				String elemento = str1+"("+m.getOrigen()+")"+ "->" + str2+"("+m.getDestino()+")" + ": " + m.getTipoMensaje();
				res.add(elemento);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}
	

	/**
	 * Añade una linea en la tabla MENSAJE
	 * @param es_envio Valor booleano inidica si es un envio o una recepcion
	 */
	public void almacenar_mensaje(Mensaje message,TipoOrigDest torigen,String origen,TipoOrigDest tdestino,String destino){

		int num_mensaje = -1;
		boolean online = false;
		String id_banco = null;
		int id_banco_bd = -1;
	
		//Añade el id_banco, origen, destino o se obtiene de la tarjeta
		if(torigen.equals(TipoOrigDest.BANCO))
			id_banco = origen;
		if(tdestino.equals(TipoOrigDest.BANCO))
			id_banco = destino;
		if((id_banco==null) && (message.es_solicitudDatos())){
			id_banco = ((MensajeDatos) message).getIdBancoFromTarjeta();
		}
		
		//Si el mensaje es de datos obtenemos el numero de mensaje y si es offline
		if(message.es_datos()){
			num_mensaje = ((MensajeDatos) message).getNmsg();
			online = ((MensajeDatos) message).getCodonline();
		}

		//Si hay banco, obtiene el identificar del banco en la BD
		if(id_banco!=null){
			try {
				id_banco_bd = this.getIdBancoBD(id_banco);
			} catch (ConsorcioBDException e1) {
				System.out.println("No existe el banco en la BD.");
			}
		}
		
		try {
			String q = "INSERT INTO Mensaje(codBanco,meNumMensaje,meonline, codTOrigen,meorigen, codTDestino, medestino,mestringMensaje) " +
					"VALUES ("+ ((id_banco_bd==-1)?"NULL":id_banco_bd) + ","+ ((num_mensaje==-1)?"NULL":num_mensaje) + "," + (online) + "," + torigen.getNum() +
					",'" + origen + "'," + tdestino.getNum() + ",'" + destino + "','" + message.toString() +"')";
			
			this.statement.executeUpdate(q);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	
	/*---------------------------------------------------
	 --------- GETTERS PARA INTERFAZ GRAFICA ------------
	 ----------------------------------------------------*/
	
	public ArrayList<ArrayList<String>> getBancos(){
		
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT b.codigo,b.codBanco,e.ebnombre,b.bapuerto,b.baip,b.bamaxCanales,b.balastChannelUsed" +
					" FROM Banco b JOIN EstadoBanco e ON b.codEBanco=e.codEBanco ORDER BY b.codigo");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();
				int cod = resultSet.getInt(1);
				String nombre = resultSet.getString(2);
				String estado = resultSet.getString(3);
				int puerto = resultSet.getInt(4);
				String ip = resultSet.getString(5);
				int canales = resultSet.getInt(6);
				int ultimo_canal = resultSet.getInt(7);
				
				linea.add(String.valueOf(cod));
				linea.add(nombre);
				linea.add((estado==null)?"NULL":estado);
				linea.add((puerto==0)?"NULL":String.valueOf(puerto));
				linea.add((ip==null)?"NULL":ip);
				linea.add(String.valueOf(canales));
				linea.add(String.valueOf(ultimo_canal));
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
	public ArrayList<ArrayList<String>> getCanales(){
		
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT codBanco,codCanal,cabloqueado,codUltimoEnvio,canext_numMensaje" +
					" FROM Canal ORDER BY codBanco");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();
				boolean a = false;
				int cod = resultSet.getInt(1);
				int canal = resultSet.getInt(2);
				int bloqueado = resultSet.getInt(3);
				a = resultSet.getString(4)==null;
				int ultimo_envio = resultSet.getInt(4);
				int siguiente_mensaje = resultSet.getInt(5);
				
				linea.add(String.valueOf(cod));
				linea.add(String.valueOf(canal));
				linea.add((bloqueado==0)?"NO":"SI");
				linea.add((a)?"NULL":String.valueOf(ultimo_envio));//NULL?
				linea.add(String.valueOf(siguiente_mensaje));
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}

	public ArrayList<ArrayList<String>> getTarjetas(){

		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT codTarjeta,tagastoOffline" +
					" FROM Tarjeta ORDER BY codTarjeta");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();

				String codTarjeta = resultSet.getString(1);
				int tagastoOffline = resultSet.getInt(2);
				
				linea.add(codTarjeta);
				linea.add(String.valueOf(tagastoOffline));
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
	
	public ArrayList<ArrayList<String>> getCuentas(){
		
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT codTarjeta,codCuenta,cusaldo" +
					" FROM Cuenta ORDER BY codTarjeta");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();

				String codTarjeta = resultSet.getString(1);
				int codCuenta = resultSet.getInt(2);
				int cusaldo = resultSet.getInt(3);
				
				linea.add(codTarjeta);
				linea.add(String.valueOf(codCuenta));
				linea.add(String.valueOf(cusaldo));
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
	
	public ArrayList<ArrayList<String>> getUltimosEnvios(){
		
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT codigoue,ueNumUltimoEnvio,uecontestado,uecodCajero,uepuerto,ueip,codBanco,codTarjeta,codCuenta,uestringMensaje" +
					" FROM UltimoEnvio ORDER BY codigoue");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();
				boolean a = false;
				
				int codigoue = resultSet.getInt(1);
				int ueNumUltimoEnvio = resultSet.getInt(2);
				int uecontestado = resultSet.getInt(3);
				String uecodCajero = resultSet.getString(4);
				int uepuerto = resultSet.getInt(5);
				String ueip = resultSet.getString(6);
				int codBanco = resultSet.getInt(7);
				String codTarjeta = resultSet.getString(8);
				a = resultSet.getString(9)==null;
				int codCuenta = resultSet.getInt(9);
				String uestringMensaje = resultSet.getString(10);

				linea.add(String.valueOf(codigoue));
				linea.add(String.valueOf(ueNumUltimoEnvio));
				linea.add((uecontestado==0)?"NO":"SI");
				linea.add((uecodCajero==null)?"NULL":uecodCajero);
				linea.add((uepuerto==0)?"NULL":String.valueOf(uepuerto));
				linea.add((ueip==null)?"NULL":ueip);
				linea.add(String.valueOf(codBanco));
				linea.add((codTarjeta==null)?"NULL":codTarjeta);
				linea.add((a)?"NULL":String.valueOf(codCuenta));//NULL?
				linea.add(uestringMensaje);
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
	public ArrayList<ArrayList<String>> getMovimientos(){
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT m.codMovimiento,m.codTarjeta,m.codCuentaOrig,m.codCuentaDest,t.tmnombre,m.mofecha,m.moimporte,m.moonline,m.codBanco" +
					" FROM Movimiento m JOIN TipoMovimiento t ON m.codTMovimiento=t.codTMovimiento ORDER BY m.codMovimiento");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();
				boolean a = false; 

				int codMovimiento = resultSet.getInt(1);
				String codTarjeta = resultSet.getString(2);
				a = resultSet.getString(3)==null;
				int codCuentaOrig = resultSet.getInt(3);
				int codCuentaDest = resultSet.getInt(4);
				String codTMovimiento = resultSet.getString(5);
				Date mofecha = new java.util.Date(resultSet.getDate(6).getTime());
				int moimporte = resultSet.getInt(7);
				int moonline = resultSet.getInt(8);
				int codBanco = resultSet.getInt(9);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				linea.add(String.valueOf(codMovimiento));
				linea.add(codTarjeta);
				linea.add((a)?"NULL":String.valueOf(codCuentaOrig));//NULL?
				linea.add(String.valueOf(codCuentaDest));
				linea.add((codTMovimiento==null)?"NULL":codTMovimiento);
				linea.add((mofecha==null)?"NULL":sdf.format(mofecha));
				linea.add(String.valueOf(moimporte));
				linea.add((moonline==0)?"OFFLINE":"ONLINE");
				linea.add(String.valueOf(codBanco));
				elementos.add(linea);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
	public ArrayList<ArrayList<String>> getMensajes(){
		
		ResultSet resultSet;
		ArrayList<ArrayList<String>> elementos = new ArrayList<ArrayList<String>>();
		try {
			resultSet = this.statement.executeQuery("SELECT codMensaje,meNumMensaje,codTOrigen,meorigen,codTDestino,medestino,codBanco,meonline,mestringMensaje " +
					"FROM Mensaje ORDER BY codMensaje");

			while(resultSet.next()){
				ArrayList<String> linea = new ArrayList<String>();
				boolean a = false;
					
				int codMensaje = resultSet.getInt(1);
				a = resultSet.getString(2)==null;
				int meNumMensaje = resultSet.getInt(2);
				int codTOrigen = resultSet.getInt(3);
				String tipo_orig = (codTOrigen==1)?"Banco":(codTOrigen==2)?"Consorcio":(codTOrigen==3)?"Cajero":"NULL";
				
				String meorigen = resultSet.getString(4);
				int codTDestino = resultSet.getInt(5);
				String tipo_dest = (codTDestino==1)?"Banco":(codTDestino==2)?"Consorcio":(codTDestino==3)?"Cajero":"NULL";
				
				String medestino = resultSet.getString(6);
				int codBanco = resultSet.getInt(7);
				int meonline = resultSet.getInt(8);
				String mestringMensaje = resultSet.getString(9);
				
				linea.add(String.valueOf(codMensaje));
				linea.add((a)?"NULL":String.valueOf(meNumMensaje));//NULL?
				linea.add(tipo_orig);
				linea.add(meorigen);
				linea.add(tipo_dest);
				linea.add(medestino);
				linea.add((codBanco==0)?"NULL":String.valueOf(codBanco));
				linea.add((meonline==0)?"OFFLINE":"ONLINE");
				linea.add(mestringMensaje);
				elementos.add(linea);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return elementos;
	}
	
}

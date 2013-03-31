/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banco.iu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import banco.Banco;
import banco.Conta;
import banco.Movemento;
import banco.Tarxeta;

/**
 *
 * @author ch01
 */
public class VentanaBanco extends javax.swing.JFrame implements NovaContaListener, NovaTarxetaListener, NovaContaAsociadaListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5641750155014378657L;
	private javax.swing.JButton botonabrirsesion;
    private javax.swing.JButton botondetenertrafico;
    private javax.swing.JButton botoneliminarconta;
    private javax.swing.JButton botoneliminartarxeta;
    private javax.swing.JButton botonengadirconta;
    private javax.swing.JButton botonengadirtarxeta;
    private javax.swing.JButton botonengadircontaasociada;
    private javax.swing.JButton botoneliminarcontaasociada;
    private javax.swing.JToggleButton botonforzarrecuperacion;
    private javax.swing.JPanel contasTab;
    private javax.swing.JButton botonestablecervalorespordefecto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> listacontasasociadas;
    private javax.swing.JList<String> listatarxetas;
    private javax.swing.JPanel monYcontrolTab;
    private javax.swing.JTable tablacanles;
    private javax.swing.JTable tablacontas;
    private javax.swing.JPanel tarxTab;
    private javax.swing.JTextArea textlog;
    private javax.swing.JTable movementos;
    private Banco banco;
	private boolean sesionabierta = false;
	private boolean traficoactivo;
	
    /**
     * Creates new form VentanaBanco
     */
    public VentanaBanco(Banco b) {
    	this.banco = b;
        initComponents();
        this.setTitle("Ventana Banco \"" + b.getName() + "\"" + " - ACS 2012/2013");
        
        this.actualizarContas();
        this.actualizarTarxetas();
    }

	private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        contasTab = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablacontas = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        movementos = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        botonengadirconta = new javax.swing.JButton();
        botoneliminarconta = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        botonestablecervalorespordefecto = new javax.swing.JButton();
        monYcontrolTab = new javax.swing.JPanel();
        botonforzarrecuperacion = new javax.swing.JToggleButton();
        botonabrirsesion = new javax.swing.JButton();
        botondetenertrafico = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablacanles = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane4 = new javax.swing.JScrollPane();
        textlog = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        tarxTab = new javax.swing.JPanel();
        botonengadirtarxeta = new javax.swing.JButton();
        botoneliminartarxeta = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        listatarxetas = new javax.swing.JList<String>();
        jScrollPane6 = new javax.swing.JScrollPane();
        listacontasasociadas = new javax.swing.JList<String>();
        jSeparator3 = new javax.swing.JSeparator();
        botonengadircontaasociada = new JButton();
        botoneliminarcontaasociada = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setName(""); // NOI18N

        // TAB CONTAS
                
        jScrollPane1.setViewportView(tablacontas);

        tablacontas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	ListSelectionModel rowSM = tablacontas.getSelectionModel();
    	rowSM.addListSelectionListener(new ListSelectionListener() {
    		public void valueChanged(ListSelectionEvent e) {
    			if (e.getValueIsAdjusting()) return;
    			actualizarMovementos();
    		}
    	});
    	
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jScrollPane2.setViewportView(movementos);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Movementos");

        botonengadirconta.setText("Engadir Conta");
        botonengadirconta.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				engadirConta();
			}
        	
        });

        botoneliminarconta.setText("Eliminar Conta");
        botoneliminarconta.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				eliminarConta();
			}
        	
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Contas");

        botonestablecervalorespordefecto.setText("Establecer Valores por Defecto");
        botonestablecervalorespordefecto.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				establecerValoresDefecto();
			}
        	
        });

        javax.swing.GroupLayout contasTabLayout = new javax.swing.GroupLayout(contasTab);
        contasTab.setLayout(contasTabLayout);
        contasTabLayout.setHorizontalGroup(
            contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contasTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(botonestablecervalorespordefecto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(contasTabLayout.createSequentialGroup()
                            .addComponent(botonengadirconta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(botoneliminarconta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        contasTabLayout.setVerticalGroup(
            contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contasTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonestablecervalorespordefecto)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(contasTabLayout.createSequentialGroup()
                        .addGroup(contasTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonengadirconta)
                            .addComponent(botoneliminarconta))
                        .addGap(7, 7, 7)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Contas", contasTab);

        //TAB TARXETAS
        
        botonengadircontaasociada.setText("Engadir Conta Asociada");
        botonengadircontaasociada.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				engadircontaasociada();
			}
        	
        });
        
        botoneliminarcontaasociada.setText("Eliminar Conta Asociada");
        botoneliminarcontaasociada.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				eliminarcontaasociada();
			}
        	
        });

        
        botonengadirtarxeta.setText("Engadir Tarxeta");
        botonengadirtarxeta.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				engadirTarxeta();
			}
        	
        });

        botoneliminartarxeta.setText("Eliminar Tarxeta");
        botoneliminartarxeta.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				eliminarTarxeta();
			}
        	
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tarxetas");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Contas Asociadas");

        
        listatarxetas.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				VentanaBanco.this.actualizarContasAsociadas();
			}
        	
        });
        
        jScrollPane5.setViewportView(listatarxetas);

        jScrollPane6.setViewportView(listacontasasociadas);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout tarxTabLayout = new javax.swing.GroupLayout(tarxTab);
        tarxTab.setLayout(tarxTabLayout);
        tarxTabLayout.setHorizontalGroup(
            tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarxTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonengadirtarxeta, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(botoneliminartarxeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(botonengadircontaasociada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botoneliminarcontaasociada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                .addContainerGap())
        );
        tarxTabLayout.setVerticalGroup(
            tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarxTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tarxTabLayout.createSequentialGroup()
                        .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonengadirtarxeta)
                            .addComponent(botonengadircontaasociada))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botoneliminartarxeta)
                            .addComponent(botoneliminarcontaasociada))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tarxTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tarxTabLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                            .addGroup(tarxTabLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5))))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tarxetas", tarxTab);
        
        //TAB MONITORIZACION e CONTROL
        
        botonforzarrecuperacion.setText("Forzar Modo Recuperacion");
        botonforzarrecuperacion.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				forzarRecuperacion();
			}
        	
        });

        botonabrirsesion.setText("Abrir Sesion");
        botonabrirsesion.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(VentanaBanco.this.sesionabierta){
					cerrarSesion();
				}else{
					abrirSesion();
				}
				
			}
        	
        });

        botondetenertrafico.setEnabled(false);
        botondetenertrafico.setText("Detener Trafico");
        botondetenertrafico.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(VentanaBanco.this.traficoactivo){
					detenerTrafico();
				}else{
					reanudarTrafico();
				}
			}
        	
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Canles");

        
        jScrollPane3.setViewportView(tablacanles);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        textlog.setColumns(20);
        textlog.setRows(5);
        jScrollPane4.setViewportView(textlog);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Log");

        javax.swing.GroupLayout monYcontrolTabLayout = new javax.swing.GroupLayout(monYcontrolTab);
        monYcontrolTab.setLayout(monYcontrolTabLayout);
        monYcontrolTabLayout.setHorizontalGroup(
            monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monYcontrolTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(botonforzarrecuperacion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(monYcontrolTabLayout.createSequentialGroup()
                        .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(botonabrirsesion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botondetenertrafico, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        monYcontrolTabLayout.setVerticalGroup(
            monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monYcontrolTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(monYcontrolTabLayout.createSequentialGroup()
                        .addComponent(botonforzarrecuperacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(monYcontrolTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonabrirsesion)
                            .addComponent(botondetenertrafico))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, monYcontrolTabLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Monitorizacion e Control", monYcontrolTab);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }

	protected void establecerValoresDefecto() {
		new DialogoSiNon("Estas seguro de que queres establecer os valores por defecto?",new Runnable(){

					@Override
					public void run() {
						banco.establecerValoresPorDefecto();
						VentanaBanco.this.actualizarContas();
						VentanaBanco.this.actualizarTarxetas();
						VentanaBanco.this.actualizarContasAsociadas();
						VentanaBanco.this.actualizarMovementos();
					}
			
		}).setVisible(true);
	}

	protected void eliminarcontaasociada() {
		int selectedindex = this.listatarxetas.getSelectedIndex();
		if(selectedindex == -1) return;
		final int cdgtarxeta = new Integer( (String) this.listatarxetas.getModel().getElementAt(selectedindex));
		
		selectedindex = this.listacontasasociadas.getSelectedIndex();
		String aux = (String) this.listacontasasociadas.getModel().getElementAt(selectedindex);
		final int cdgconta = new Integer(aux.substring(0,aux.indexOf('(')));
		
		new DialogoSiNon("Estas seguro de que queres desasociar a tarxeta " +
				cdgtarxeta + "e a conta " + cdgconta + "?",new Runnable(){

					@Override
					public void run() {
						banco.eliminarContaAsociada(cdgtarxeta, cdgconta);
						VentanaBanco.this.actualizarContasAsociadas();
					}
			
		}).setVisible(true);
	}
	
	protected void engadircontaasociada() {
		int selectedindex = this.listatarxetas.getSelectedIndex();
		if(selectedindex == -1) return;
		final int cdgtarxeta = new Integer( (String) this.listatarxetas.getModel().getElementAt(selectedindex));
		
		ArrayList<Conta> arraycontas = banco.getContas();
		
		Integer[] contas = new Integer[arraycontas.size()];
		int i = 0;
		for(Conta c : arraycontas){
			contas[i++] = c.getNumero();
		}
		
		new DialogoNovaContaAsociada(this, cdgtarxeta, contas).setVisible(true);
		
		
	}

	protected void reanudarTrafico() {
		// TODO Auto-generated method stub
		this.botondetenertrafico.setText("Detener Tráfico");
		this.traficoactivo = true;
	}

	protected void detenerTrafico() {
		// TODO Auto-generated method stub
		this.botondetenertrafico.setText("Reanudar Tráfico");
		this.traficoactivo = false;
	}

	protected void forzarRecuperacion() {
		// TODO Auto-generated method stub
		
	}

	protected void abrirSesion() {
		// TODO Auto-generated method stub
		this.botondetenertrafico.setEnabled(true);
		this.botonabrirsesion.setText("Cerrar Sesión");
		this.sesionabierta = true;
	}
	
	protected void cerrarSesion() {
		this.reanudarTrafico();
		new DialogoSiNon("Esta seguro de que desexa cerrar a sesión?", new Runnable(){

			@Override
			public void run() {
				banco.cerrarSesion();
			}
			
		}).setVisible(true);
		this.botondetenertrafico.setEnabled(false);
		this.botonabrirsesion.setText("Abrir Sesión");
		this.sesionabierta = false;
	}

	protected void eliminarTarxeta() {
		int selrow = this.listatarxetas.getSelectedIndex();
		if (selrow == -1) return;
		final int cdgtarxeta = new Integer((String) listatarxetas.getModel().getElementAt(selrow));
		
		new DialogoSiNon("Esta seguro de que queres eliminar a tarxeta número " + cdgtarxeta + "?",new Runnable(){

			@Override
			public void run() {
				banco.eliminarTarxeta(cdgtarxeta);
				VentanaBanco.this.actualizarTarxetas();
				VentanaBanco.this.actualizarContasAsociadas();
			}
			
		}).setVisible(true);
	}

	protected void engadirTarxeta() {
		new DialogoNovaTarxeta(this).setVisible(true);
	}

	protected void eliminarConta() {
		
		int selrow = this.tablacontas.getSelectedRow();
		if (selrow == -1) return;
		final int cdgConta = (Integer) tablacontas.getModel().getValueAt(selrow, 0);
		
		new DialogoSiNon("Esta seguro de que queres eliminar a conta número " + cdgConta + "?",new Runnable(){

					@Override
					public void run() {
						banco.eliminarConta(cdgConta);
						VentanaBanco.this.actualizarContas();
						VentanaBanco.this.actualizarContasAsociadas();
					}
			
		}).setVisible(true);
	}

	protected void engadirConta() {
		new DialogoNovaConta(this).setVisible(true);
	}

	private void setMovementos(Object[][] data){
		movementos.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String [] {"#Movemento", "Tipo","Importe","Data"}
        ));
    }
    
    private void engadirLinhaLog(String s){
    	this.textlog.append(s);
    }
    
    private void baleirarLog(){
    	this.textlog.setText("");
    }
    
    private void setTablaContas(Object[][] data){
    	tablacontas.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String [] { "Numero Conta", "Saldo" }
            ));
    }
    
    private void setTablaCanles(Object[][] data){
    	tablacanles.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String [] {
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
            ));
    }
    
    private void setListaTarxetas(final String[] list){
    	listatarxetas.setModel(new javax.swing.AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;	
			public int getSize() { return list.length; }
            public String getElementAt(int i) { return list[i]; }
        });
    }
    
    private void setListaContasAsociadas(final String[] list){
    	listacontasasociadas.setModel(new javax.swing.AbstractListModel<String>() {
			private static final long serialVersionUID = -5475210877168039186L;
			public int getSize() { return list.length; }
            public String getElementAt(int i) { return list[i]; }
        });
    }
    
    protected Object[][] formatTaboaMovementos(ArrayList<Movemento> movementos) {
    	int i=0;
 		Object[][] res = new Object[movementos.size()][2];
 		for(Movemento m : movementos){
 			res[i][0] = new Integer(m.codigo);
 			res[i][1] = m.tipo;
 			res[i][2] = new Float(m.importe);
 			res[i][3] = m.data;
 			i++;
 		}
 		return res;
	}
    
    private String[] formatListContas(ArrayList<Conta> contas) {
    	int i=0;
     	String[] res = new String[contas.size()];
     	for(Conta c : contas){
     		res[i++] = new Integer(c.getNumero()).toString() + "(" + c.getSaldo() + ")";
     	}
     	return res;
    }

 	private Object[][] formatTaboaContas(ArrayList<Conta> contas) {
 		int i=0;
 		Object[][] res = new Object[contas.size()][2];
 		for(Conta c : contas){
 			res[i][0] = new Integer(c.getNumero());
 			res[i][1] = new Float(c.getSaldo());
 			i++;
 		}
 		return res;
 	}
 	
 	private String[] formatListTarxetas(ArrayList<Tarxeta> tarxetas) {
 		int i=0;
 		String[] res = new String[tarxetas.size()];
 		for(Tarxeta t : tarxetas){
 			res[i++] = new Integer(t.getCodigo()).toString();
 		}
 		return res;
	}
 	
 	protected void actualizarContasAsociadas(){
 		int selrow = listatarxetas.getSelectedIndex();
 		ArrayList<Conta> res;
 		
 		if(selrow != -1){
 			String ntarxeta = (String) listatarxetas.getModel().getElementAt(selrow);
 			res = banco.getContasAsociadas(new Integer(ntarxeta));
 		}else{
 			res = new ArrayList<Conta>();
 		}
		setListaContasAsociadas(formatListContas(res));
	}
	
	protected void actualizarContas(){
		setTablaContas(this.formatTaboaContas(banco.getContas()));
	}
	
	protected void actualizarMovementos(){
		int selrow = tablacontas.getSelectedRow();
		ArrayList<Movemento> res;
		if(selrow != -1){
			Integer nconta = (Integer) tablacontas.getModel().getValueAt(tablacontas.getSelectedRow(), 0);
			res = banco.getMovementosConta(nconta);
		}else{
			res = new ArrayList<Movemento>();
		}
		setMovementos(formatTaboaMovementos(res));
	}
	
	protected void actualizarTarxetas(){
        setListaTarxetas(this.formatListTarxetas(banco.getTarxetas()));
	}

	@Override
	public void engadirConta(int num, float saldo) {
		banco.engadirConta(new Conta(num,saldo));
		this.actualizarContas();
	}
	
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaBanco.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaBanco.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaBanco.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaBanco.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaBanco(new Banco("fjslkdj","jdbc:mysql://localhost/acs?user=acsuser&password=password")).setVisible(true);
            }
        });
    }

	@Override
	public void engadirTarxeta(int cdgtarxeta) {
		banco.engadirTarxeta(cdgtarxeta);
		this.actualizarTarxetas();
	}

	@Override
	public void engadirContaAsociada(int cdgtarxeta, int cdgconta) {
		banco.engadirContaAsociada(cdgtarxeta, cdgconta);
		this.actualizarContasAsociadas();
	}

}

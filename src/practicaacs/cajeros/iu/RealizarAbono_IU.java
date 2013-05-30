package practicaacs.cajeros.iu;


import javax.swing.JFrame;

import practicaacs.cajeros.Cajero;
import practicaacs.cajeros.Envio;
import practicaacs.fap.CodigosMensajes;
import practicaacs.fap.Mensaje;
import practicaacs.fap.MensajeDatos;
import practicaacs.fap.RespAbono;

public class RealizarAbono_IU extends ConsultaAbstracta {

    JFrame parent;
    Envio envio;
    Cajero cajero;
    
    /**
     * Creates new form RealizarAbono
     */
    public RealizarAbono_IU(JFrame padre,Cajero caj,Envio env) {
        this.parent = padre;
        this.envio = env;
        this.cajero = caj;
        initComponents();
        inicializa_visibilidades();
        this.setLocationRelativeTo(null);
    }

    private void inicializa_visibilidades(){
        this.EsperandoRespuestaLabel.setVisible(false);
        this.SaldoLabel.setVisible(false);
        this.SaldoText.setVisible(false);
        this.ErrorLabel.setVisible(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ImporteText = new javax.swing.JTextField();
        AceptarButton = new javax.swing.JButton();
        EsperandoRespuestaLabel = new javax.swing.JLabel();
        SaldoText = new javax.swing.JTextField();
        SaldoLabel = new javax.swing.JLabel();
        FinalizarButton = new javax.swing.JButton();
        ErrorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Abono");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ABONO");

        jLabel2.setText("Introduzca el importe a abonar");

        AceptarButton.setText("Aceptar");
        AceptarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceptarButtonActionPerformed(evt);
            }
        });

        EsperandoRespuestaLabel.setText("Esperando respuesta...");

        SaldoText.setEditable(false);

        SaldoLabel.setText("Saldo actual:");

        FinalizarButton.setText("Finalizar");
        FinalizarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FinalizarButtonActionPerformed(evt);
            }
        });

        ErrorLabel.setText("La cantidad introducida no es correcta...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SaldoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SaldoText)
                    .addComponent(ImporteText, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EsperandoRespuestaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AceptarButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 129, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FinalizarButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ErrorLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ImporteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ErrorLabel)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(EsperandoRespuestaLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AceptarButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(SaldoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SaldoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FinalizarButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método que se ejecuta al pulsar el boton ACEPTAR
     * @param evt El evento
     */
    private void AceptarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AceptarButtonActionPerformed
        try{
	    	inicializa_visibilidades();
	        String importe = this.ImporteText.getText();
	        
	        //Comprobamos si no se ha introducido el importe
	        if(importe.equals("")){
	        	throw new NumberFormatException();
	        }
	        int importe_abono = Integer.parseInt(importe);
	        this.EsperandoRespuestaLabel.setVisible(true);
	        
	        //Añadimos los componentes del envio
	        this.envio.setTipoMensaje(CodigosMensajes.SOLABONO);
	        this.envio.setImporte(importe_abono);
	        envia_consulta(this.envio);
	        
        }catch(NumberFormatException nfe){
            this.ErrorLabel.setVisible(true);
            return;
        }
    }//GEN-LAST:event_AceptarButtonActionPerformed

    private void FinalizarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FinalizarButtonActionPerformed
        if(this.parent != null)
            this.parent.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_FinalizarButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RealizarAbono_IU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RealizarAbono_IU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RealizarAbono_IU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RealizarAbono_IU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RealizarAbono_IU(null,null,null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AceptarButton;
    private javax.swing.JLabel ErrorLabel;
    private javax.swing.JLabel EsperandoRespuestaLabel;
    private javax.swing.JButton FinalizarButton;
    private javax.swing.JTextField ImporteText;
    private javax.swing.JLabel SaldoLabel;
    private javax.swing.JTextField SaldoText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
    
    
	@Override
    public void envia_consulta(Envio env){
    	env.setTipoMensaje(CodigosMensajes.SOLMOVIMIENTOS);
    	Mensaje envio = this.cajero.crear_mensaje(env);
    	this.cajero.enviar_mensaje(envio,this);
    }
    
	@Override
    public void actualizarIU(MensajeDatos respuesta){
    	this.ImporteText.setText(String.valueOf(((RespAbono)respuesta).getSaldo()));
        this.EsperandoRespuestaLabel.setVisible(false);
        this.ErrorLabel.setVisible(false);
        this.SaldoLabel.setVisible(true);
        this.SaldoText.setVisible(true);
    }

}

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
package practicaacs.banco.iu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import practicaacs.banco.Banco;
/**
 *
 * @author ch01
 */
public class DialogoNovaConta extends javax.swing.JDialog {


	private static final long serialVersionUID = 7952674157045399233L;
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
	
    /**
     * Creates new form DialogoNovaConta
     */
    public DialogoNovaConta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public DialogoNovaConta(final Banco a){
    	this(new javax.swing.JFrame(), true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DialogoNovaConta.this.setVisible(false);
            }
        });

        this.jButton2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					int num_conta = new Integer(DialogoNovaConta.this.jTextField1.getText());
					float saldo = new Float(DialogoNovaConta.this.jTextField2.getText());
					a.engadirConta(num_conta, saldo);
					DialogoNovaConta.this.setVisible(false);
				}catch( NumberFormatException es){
					new DialogoError("Introduza un numeros de conta e saldo válidos.").setVisible(true);
					DialogoNovaConta.this.jTextField1.setText("");
					DialogoNovaConta.this.jTextField2.setText("");
				}
					
				
			}
        });
        
        this.jButton1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				DialogoNovaConta.this.setVisible(false);			
			}
        	
        });
    }

    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Cancelar");

        jButton2.setText("Aceptar");

        jLabel1.setText("Numero de Conta");

        jLabel2.setText("Saldo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                            .addComponent(jTextField1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 224, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

}
